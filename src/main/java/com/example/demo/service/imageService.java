package com.example.demo.service;

import com.example.demo.dao.imageTable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface imageService {

    List<imageTable> getAllImages();//获取所有图片

    void saveOcrData(String modelName,String imagePath, String ocrResult);//保存图片识别结果

    String getFileContentAsBase64(MultipartFile file, boolean urlEncode) throws IOException;//将文件转换为Base64编码

    List<String> performBaiduOcr(MultipartFile file) throws Exception;//执行百度OCR识别

    List<String> performMyselfOcrModel(MultipartFile file) throws Exception;//执行自定义OCR识别

    String loadAndSaveImage(MultipartFile file) throws IOException;//加载并保存图片

}
