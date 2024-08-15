document.addEventListener('DOMContentLoaded', function() {

    const tableBody = document.getElementById('table-body');
    if (tableBody === null) {
        console.error('The element with id "table-body" is not found.');
    } else {
        fetch('http://localhost:8080/history')
            .then(response => response.json())
            .then(data => {
                //逆序
                data.sort((a,b) =>b.id-a.id);
                data.forEach(item => {
                    // 创建一个新的表格行
                    const row = document.createElement('tr');

                    // 创建序号单元格
                    const idCell = document.createElement('td');
                    idCell.textContent = item.id;
                    row.appendChild(idCell);

                    // 创建图片单元格
                    const imageCell = document.createElement('td');
                    const imgElement = document.createElement('img');

                    // 使用 item.imagedata 作为图片的 src
                    imgElement.src = item.imagedata;
                    imgElement.alt = "Image";

                    // 包裹在 div 中
                    const imageContainer = document.createElement('div');
                    imageContainer.className = 'image-container'; // 添加类名
                    imageContainer.appendChild(imgElement);
                    imageCell.appendChild(imageContainer);
                    row.appendChild(imageCell);

                    // 创建识别结果单元格
                    const textCell = document.createElement('td');
                    textCell.textContent = item.text;
                    row.appendChild(textCell);

                    // 创建识别时间单元格
                    const datetimeCell = document.createElement('td');
                    datetimeCell.textContent = item.datetime;
                    row.appendChild(datetimeCell);

                    // 创建识别模型单元格
                    const modelCell = document.createElement('td');
                    modelCell.textContent = item.modelname;
                    row.appendChild(modelCell);

                    // 将新行添加到表格中
                    tableBody.appendChild(row);
                });
            })
            .catch(error => console.error('Error fetching data:', error));
    }

    console.log(tableBody);
});