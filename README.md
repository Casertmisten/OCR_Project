# PS： 使用前请修改application.properties 里面的百度OCR配置

baidu.ocr.appid="yours"

baidu.ocr.apiKey="yours"

baidu.ocr.secretKey="yours"


# 工作流程： 
前端将图片传给java后端，后端将图片保存到本地，然后把图片的Base64编码传给识别模型（使用百度OCR识别API和用falsk框架搭建的python自建模型），最后将图片的本地保存路径和识别结果保存到数据库，供前端调用展示

**python后端代码：** https://github.com/Casertmisten/flaskOcr/tree/master


**一、前端页面**

（1）识别页面

![image](https://github.com/user-attachments/assets/2f0dda74-e72a-4e1c-b403-f3066fc5f7a6)
（2）历史记录页面

![image](https://github.com/user-attachments/assets/b2da063a-05a5-43f2-8a53-1371bbe842df)

**二、数据库结构**

![image](https://github.com/user-attachments/assets/7ffe2bad-5555-4181-aee6-54e644a52e05)

id：序号、自增主键

modelname：识别模型名称

datetime：识别时间

imagedata：图片的存储地址

text：识别结果的内容

**三、系统结构**

![image](https://github.com/user-attachments/assets/3e20a332-d5cd-4983-ae1f-1c1a9251a7d8)




