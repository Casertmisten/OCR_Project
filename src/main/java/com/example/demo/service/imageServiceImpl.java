package com.example.demo.service;

import com.example.demo.dao.imageTableRepository;
import com.example.demo.dao.imageTable;
import com.example.demo.config.baiduOcrProperties;
import com.example.demo.config.myselfModelProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class imageServiceImpl implements imageService{

    private final baiduOcrProperties baiduOcrProperties;
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    private final imageTableRepository imageTableRepository;
    private final myselfModelProperties myselfModelProperties;

    // 初始化
    @Autowired
    public imageServiceImpl(baiduOcrProperties baiduOcrProperties, imageTableRepository imageTableRepository, com.example.demo.config.myselfModelProperties myselfModelProperties) {
        this.baiduOcrProperties = baiduOcrProperties;
        this.imageTableRepository = imageTableRepository;
        this.myselfModelProperties = myselfModelProperties;
    }

    /**
     * 导入图片、保存图片到本地
     *
     * @param file  用户上传的文件
     * @return 图片保存后的文件名
     * @throws IOException 读取文件时发生错误
     */
    public String loadAndSaveImage(MultipartFile file) throws IOException {
        System.out.println("<-------------->");
        System.out.println("Image upload ok!");
        System.out.println("<-------------->");
        System.out.println(" ");

        String contentType = file.getContentType();
        String[] parts = null;//格式为：image/png
        if (contentType != null) {
            parts = contentType.split("/");
        }else{
            System.out.println("<-------------->");
            System.out.println("Parts is null!");
            System.out.println("<-------------->");
            System.out.println(" ");
        }
        String fileExtension = null;//文件后缀
        if (parts != null) {
            fileExtension = parts[parts.length - 1];
        }else{
            System.out.println("<-------------->");
            System.out.println("Parts is null!");
            System.out.println("<-------------->");
            System.out.println(" ");
        }
        // 保存图片到本地
        Path path = Paths.get("D:/code/java/demo/src/main/resources/static/ocrImg");
        Files.createDirectories(path);
        String fileName = "image-" + System.currentTimeMillis() + '.'+fileExtension;
        String saveImgName="/demo/static/ocrImg/"+fileName;
        Files.copy(file.getInputStream(), path.resolve(fileName));

        Path imagePath = path.resolve(fileName);// 图片的本地路径
        // 检查文件是否存在
        if (Files.exists(imagePath)) {
            // 如果文件大小为0，说明文件可能有问题
            long fileSize = Files.size(imagePath);
            if (fileSize == 0) {
                System.err.println("The saved file is empty.");
            }
        } else {
            System.err.println("The file was not saved.");
        }
        return saveImgName;
    }

    /**
     * 获取所有图片的识别记录
     *
     * @return 所有图片的识别记录
     */
    @Override
    public List<imageTable> getAllImages() {
        return imageTableRepository.findAll();
    }

    /**
     * 将识别记录（路径、识别时间、识别结果）保存到数据库
     *
     * @param imagePath 图片在本地的存储路径
     * @param ocrResult 图片的识别记录
     */
    public void saveOcrData(String modelName,String imagePath, String ocrResult) {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 创建 imagesTable 实例
        imageTable ocrhistory = new imageTable();
        ocrhistory.setModelname(modelName);
        ocrhistory.setDatetime(dateFormat.format(now));
        ocrhistory.setImagedata(imagePath);
        ocrhistory.setText(ocrResult);

        // 保存到数据库
        imageTableRepository.save(ocrhistory);
    }

    /**
     * 获取文件base64编码
     *
     * @param file      文件
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    public String getFileContentAsBase64(MultipartFile file, boolean urlEncode) throws IOException {
        if (file == null){
            return "";
        }else{
            byte[] buf = file.getBytes();

            String base64 = Base64.getEncoder().encodeToString(buf);
            if (urlEncode) {
                base64 = URLEncoder.encode(base64, StandardCharsets.UTF_8);
            }
            return base64;
        }
    }

    /**
     * 连接自建OCR模型识别系统
     *
     * @param  fileBase64 base64编码信息，不带文件头
     * @return 识别结果
     */
    public String MyselfOcrModel(String fileBase64) {
        String re="";
        String modelUrl = myselfModelProperties.getUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        String requestBody = "{\"image\": \"" + fileBase64 + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(modelUrl, entity, String.class);
        if(response.getBody() != null){
            re= response.getBody();
        }

        return re;
    }

    /**
     * 执行自建OCR模型识别
     *
     * @param file 需要识别的图片
     * @return 识别结果
     * @throws Exception 异常
     */
    @Override
    public List<String> performMyselfOcrModel(MultipartFile file) throws Exception {
        List<String> wordsList = new ArrayList<>();
        String fileBase64;
        fileBase64 = getFileContentAsBase64(file, false);
        if (fileBase64.isEmpty()){
            wordsList.add("File is NULL");
            return wordsList;
        }
        String res_json=MyselfOcrModel(fileBase64);
        return JsonToWordsList(wordsList, res_json);
    }




    /**
     * 从百度OCR服务获取Access Token。
     *
     * @return Access Token，用于身份验证。
     * @throws IOException 如果在获取Access Token过程中出现IO错误。
     */
    public String getAccessToken() throws IOException {
        String acc="";
        String apiKey=baiduOcrProperties.getApiKey();
        String secretKey=baiduOcrProperties.getSecretKey();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + apiKey
                + "&client_secret=" + secretKey);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.body() != null) {
            acc=response.body().string();
        }
        return new JSONObject(acc).getString("access_token");
    }



    /**
     * 连接百度OCR识别系统
     *
     * @param fileBase64 base64编码信息，不带文件头
     * @return 百度OCR识别结果
     * @throws IOException IO异常
     */
    public String baiduOcr(String fileBase64) throws IOException{
        String res = "";
        System.out.println(fileBase64);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "image="+fileBase64+"&detect_direction=false&paragraph=false&probability=false");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (response.body() != null) {
            res=response.body().string();
        }
        return res;

    }

    /**
     * 执行百度OCR识别操作。
     *
     * @param file 需要进行OCR识别的文件。
     * @return 识别到的文本列表。
     * @throws Exception 如果识别过程中出现错误，则抛出异常。
     */
    public List<String> performBaiduOcr(MultipartFile file) throws Exception {

        List<String> wordsList = new ArrayList<>();
        String fileBase64;
        fileBase64 = getFileContentAsBase64(file, true);
        if (fileBase64.isEmpty()){
            wordsList.add("File is NULL");
            return wordsList;
        }
        String res_json=baiduOcr(fileBase64);
        return JsonToWordsList(wordsList, res_json);
    }

    /**
     *
     * 将json数据转换为wordsList
     *
     * @param wordsList 存储识别结果的列表
     * @param res_json 识别模型回传的Json数据
     * @return wordsList
     */
    private List<String> JsonToWordsList(List<String> wordsList, String res_json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(res_json);
            JsonNode wordsResultNode = rootNode.get("words_result");

            for (JsonNode wordNode : wordsResultNode) {
                wordsList.add(wordNode.get("words").asText());
            }

            // 输出结果
            System.out.println("------wordsList------");
            for (String word : wordsList) {
                System.out.println(word);
            }
            System.out.println("------wordsList------");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordsList;
    }


}
