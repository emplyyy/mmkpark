# -*- coding:utf-8 -*-
import cv2
import numpy as np

def cvcar(videoUrl, pic):

    weightsPath = 'yolov3.weights'  # 模型权重文件
    configPath = 'yolov3.cfg'  # 模型配置文件
    labelsPath = 'yolov3.txt'  # 模型类别标签文件


    # 初始化一些参数
    LABELS = open(labelsPath).read().strip().split("\n")
    boxes = []
    confidences = []
    classIDs = []

    global str
    # 加载网络配置与训练的权重文件 构建网络
    net = cv2.dnn.readNetFromDarknet(configPath, weightsPath)
    # 读入待检测的图像
    ret, frame = cv2.VideoCapture(videoUrl).read()
    cv2.imwrite(pic, frame)
    image = cv2.imread(pic)
    # 得到图像的高和宽
    (H, W) = image.shape[0: 2]

    # 得到YOLO需要的输出层
    ln = net.getLayerNames()
    out = net.getUnconnectedOutLayers()  # 得到未连接层得序号  [[200] /n [267]  /n [400] ]
    x = []
    for i in out:  # 1=[200]
        x.append(ln[i[0] - 1])  # i[0]-1    取out中的数字  [200][0]=200  ln(199)= 'yolo_82'
    ln = x

    # 从输入图像构造一个blob，然后通过加载的模型，给我们提供边界框和相关概率
    # blobFromImage(image, scalefactor=None, size=None, mean=None, swapRB=None, crop=None, ddepth=None)

    blob = cv2.dnn.blobFromImage(image, 1 / 255.0, (416, 416), swapRB=True, crop=False)
    # 构造了一个blob图像，对原图像进行了图像的归一化，缩放了尺寸 ，对应训练模型

    net.setInput(blob)  # 将blob设为输入？？？ 具体作用还不是很清楚
    layerOutputs = net.forward(ln)  # ln此时为输出层名称  ，向前传播  得到检测结果

    for output in layerOutputs:  # 对三个输出层 循环
        for detection in output:  # 对每个输出层中的每个检测框循环
            scores = detection[5:]  # detection=[x,y,h,w,c,class1,class2] scores取第6位至最后
            classID = np.argmax(scores)  # np.argmax反馈最大值的索引
            confidence = scores[classID]
            if confidence > 0.5:  # 过滤掉那些置信度较小的检测结果
                box = detection[0:4] * np.array([W, H, W, H])
                (centerX, centerY, width, height) = box.astype("int")
                # 边框的左上角
                x = int(centerX - (width / 2))
                y = int(centerY - (height / 2))
                # 更新检测出来的框
                boxes.append([x, y, int(width), int(height)])
                confidences.append(float(confidence))
                classIDs.append(classID)

    idxs = cv2.dnn.NMSBoxes(boxes, confidences, 0.2, 0.3)
    box_seq = idxs.flatten()

    a = []
    if len(idxs) > 0:
        for seq in box_seq:
            (x, y) = (boxes[seq][0], boxes[seq][1])  # 框左上角
            (w, h) = (boxes[seq][2], boxes[seq][3])  # 框宽高
            if classIDs[seq] == 0:  # 根据类别设定框的颜色
                color = [0, 0, 255]
            else:
                color = [0, 255, 0]
            cv2.rectangle(image, (x, y), (x + w, y + h), color, 2)  # 画框
            text = "{}: {:.4f}".format(LABELS[classIDs[seq]], confidences[seq])
            cv2.putText(image, text, (x, y - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.3, color, 1)  # 写字
            a.append(x + w / 2)
            a.append(y + h / 2)

    b = np.array(a)
    b = 3 * b / 5
    # str = '[' + ",".join(str(i) for i in b) + ']'
    # # print('['+",".join(str(i) for i in b)+']')
    # print(str)
    # cv2.namedWindow('Image', cv2.WINDOW_NORMAL)
    # cv2.imshow("Image", image)
    # cv2.waitKey(0)
    return b




