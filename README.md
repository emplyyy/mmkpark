# mmkpark
## 基本介绍
* 基于树莓派4B的嵌入式Web管理系统
    * [Ubuntu Server 20.04.2 LTS](https://ubuntu.com/download/raspberry-pi)
    * [jdk-8u291-linux-aarch64](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
    * Python 3.8.5
    * opencv 4.2.0
    * nginx/1.19.8
    * mosquitto 1.6.9
    * YOLO v3

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Shiro、Mybatis-Plus。
* 数据库采用Sqlite3、Redis 5.0.7。

## 系统演示
通过Web系统录入车库信息，其中包括了监控视频地址（RTSP/RTMP/Http-flv/Hls），后台定时轮询数据库中的车库信息，把截取到的实时画面传入OpenCv中的DNN模块，并将识别结果返回给数据库，用矢量叉积法判断识别到的目标点集是否在用户所框定的车位内部，从而提升识别准确率。
![IMG](https://github.com/emplyyy/mmkpark/tree/main/mmkpark-vue/src/assets/img/home.png)
![IMG](https://github.com/emplyyy/mmkpark/tree/main/mmkpark-vue/src/assets/img/login.png)
![IMG](https://github.com/emplyyy/mmkpark/tree/main/mmkpark-vue/src/assets/img/9.png)
![IMG](https://github.com/emplyyy/mmkpark/tree/main/mmkpark-vue/src/assets/img/9-1.jpg)
![IMG](https://github.com/emplyyy/mmkpark/tree/main/mmkpark-vue/src/assets/img/9-2.jpg)
