// 预览上传图片
document.getElementById('imageInput').addEventListener('change', function (e) {
  let file = e.target.files[0];
  if (file) {
    let reader = new FileReader();
    reader.onload = function (e) {
      let image = new Image();
      image.src = e.target.result;
      image.onload = function () {
        document.getElementById('uploadedImage').src = image.src;
        document.getElementById('uploadedImage').style.display = 'block';
      };
    };
    reader.readAsDataURL(file);
  }
});


// 当点击识别按钮
document.getElementById('sumbutt').addEventListener('click', function () {
  let fileInput = document.getElementById('imageInput');
  if (fileInput.files.length > 0) {
    let formData = new FormData();
    formData.append('file', fileInput.files[0]);

    // 获取用户选择的OCR模型
    let modelSelect = document.getElementById('modelSelect');
    let selectedModel = modelSelect.value;

    showLoadingAnimation();
    // 调用函数发送文件到服务器进行OCR识别
    sendFileToServer(formData, selectedModel);
  } else {
    alert('请选择一个图片文件！');
  }
});


// 发送文件到服务器进行OCR识别
function sendFileToServer(formData, model) {
  let url;
  if (model === 'baidu') {
    url = 'http://localhost:8080/baiduOcr';
  } else if (model === 'myModel') {
    url = 'http://localhost:8080/myModel';
  }

  fetch(url, {
    method: 'POST',
    body: formData
  })
      .then(function (response) {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.text();
      })
      .then(function (text) {
        hideLoadingAnimation();
        console.log('OCR Result:', text);
        displayOcrResult(splitAndProcessText(text));
      })
      .catch(function (error) {
        hideLoadingAnimation();
        alert('识别失败，请稍后重试');
        console.error('There has been a problem with your fetch operation:', error);
      });
}


// 将OCR结果显示在页面上
function displayOcrResult(ocrData) {
  let resultMsgElement = document.getElementById('resultMsg');
  resultMsgElement.value = ocrData.join('\n');
}


// 将从后端收到的字符串分割和处理成数组
function splitAndProcessText(text) {
  return text.split(/\r?\n/);
}


// 显示加载动画
function showLoadingAnimation() {
  let loading = document.getElementById('loading');
  loading.style.display = 'block';
}


// 隐藏加载动画
function hideLoadingAnimation() {
  let loading = document.getElementById('loading');
  loading.style.display = 'none';
}


// 点击打开历史记录页面
let circleButton = document.querySelector('.circle-button');
circleButton.addEventListener('click', function() {
  window.open('re.html', '_blank');
});



