package com.example.demo.controller;

import com.example.demo.dao.imageTable;
import com.example.demo.service.imageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ocrController {

    private final imageService imageService;

    @Autowired
    public ocrController(imageService imageService) {
        this.imageService = imageService;
    }


    /**
     * 获取所有OCR识别记录。
     *
     * @return 所有OCR识别记录。
     */
    @GetMapping("/history")
    public List<imageTable> getAllImages() {
        return imageService.getAllImages();
    }


    /**
     * 使用自建OCR模型识别图片
     *
     * @param file 用户上传的文件
     * @return OCR识别结果。
     * @throws IOException 读取文件时发生错误
     */
    @PostMapping("/myModel")
    public ResponseEntity<String> myModel(MultipartFile file) throws IOException {
        String saveImgName= imageService.loadAndSaveImage(file);
        try {
            List<String> ocrResult = imageService.performMyselfOcrModel(file);//执行自建OCR识别
            // 将 List<String> 转换为一个单一的字符串
            StringBuilder resultString = new StringBuilder();
            for (String line : ocrResult) {
                resultString.append(line).append("\n");
            }
            String modelName="OcrNetV1";
            imageService.saveOcrData(modelName,saveImgName, resultString.toString());
            return ResponseEntity.ok(resultString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Doing ocr is error：" + e.getMessage());// 返回一个带有错误信息的失败响应
        }
    }


    /**
     * 使用百度ocr处理图片文字识别请求。
     *
     * @param file  用户上传的文件
     * @return 识别结果。
     * @throws IOException 如果读取文件时发生错误。
     */
    @PostMapping(value = "/baiduOcr")
    public ResponseEntity<String> ocr(MultipartFile file) throws IOException {
        String saveImgName=imageService.loadAndSaveImage(file);

        try {
            List<String> ocrResult = imageService.performBaiduOcr(file);//执行百度OCR识别
            // 将 List<String> 转换为一个单一的字符串
            StringBuilder resultString = new StringBuilder();
            for (String line : ocrResult) {
                resultString.append(line).append("\n");
            }
            System.out.println(saveImgName);
            String modelName="BaiDuOcr";
            imageService.saveOcrData(modelName,saveImgName, resultString.toString());

            return ResponseEntity.ok(resultString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Doing ocr is error：" + e.getMessage());// 返回一个带有错误信息的失败响应
        }

    }
}
