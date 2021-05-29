<template>
  <div class="div-1">
    <div class="div-2">
      <el-card id="card1">
        <div style="height:90px;background-color:#a6b4f1;padding-top: 30px;
        text-align: center;color: rgb(255 255 255);font-size: 23px;font-weight: bold">
          停车管理系统
        </div>
        <img src="../../assets/img/2.png" style="position: relative;border-radius: 50%;object-fit: cover;
        width: 100px;height: 100px;left: 50%;top: 50%;transform: translate(-50%,-50%);border: 4px solid #ffca80;
        ">
        <div style="height: 70px;position: relative;transform: translate(0,-70%);text-align: center">
          <span style="color: rgb(35 154 255);font-size: 18px;font-weight: bold;
          text-shadow: 1px 1px 2px #ffffff">欢迎登陆</span><br/>
          <span style="font-size:18px">{{$store.state.loginUser}}</span>
        </div>
      </el-card>
      <el-card id="card2">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: #6a9bff">
            <i class="el-icon-service" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 30px;height: 40px;margin-top: 32px;margin-bottom: 3px">
              {{adminTotal}}
            </div>
            <span style="font-size: 13px">管理员总数</span>
          </div>
        </div>
      </el-card>
      <el-card id="card3">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: rgb(104 105 255)">
            <i class="el-icon-star-off" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 30px;height: 40px;margin-top: 32px;margin-bottom: 3px">
              {{garTotal}}
            </div>
            <span style="font-size: 13px">车库总数</span>
          </div>
        </div>
      </el-card>
      <el-card id="card4">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: rgb(238 207 19)">
            <i class="el-icon-crop" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 30px;height: 40px;margin-top: 32px;margin-bottom: 3px">
              {{cgarTotal}}
            </div>
            <span style="font-size: 13px">车位总数</span>
          </div>
        </div>
      </el-card>
      <el-card id="card5">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: rgb(118 211 54)">
            <i class="el-icon-full-screen" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 30px;height: 40px;margin-top: 32px;margin-bottom: 3px">
              {{cgarTotal-cgarRenum}}
            </div>
            <span style="font-size: 13px">空车位数</span>
          </div>
        </div>
      </el-card>
      <el-card id="card6">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: rgb(175 57 230)">
            <i class="el-icon-star-on" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 19px;height: 30px;margin-top: 42px;font-weight: bold;">
              {{sug}}
            </div>
            <span style="font-size: 13px">当前车库</span>
          </div>
        </div>
      </el-card>
      <el-card id="card7">
        <div style="display: flex;justify-content: space-between;">
          <div  style="height: 120px;width: 27%;background-color: rgb(246 72 124)">
            <i class="el-icon-connection" style="font-size: 50px;color: rgb(255 255 255);position: relative;
          left: 50%;top: 50%;transform: translate(-50%,-50%);"></i>
          </div>
          <div style="height: 120px;width: 73%;background-color: #d7f3f6;text-align: center">
            <div style="color: #0875ae;font-size: 19px;height: 30px;margin-top: 42px;font-weight: bold;">
              {{charge}}
            </div>
            <span style="font-size: 13px">负责车库</span>
          </div>
        </div>
      </el-card>
    </div>
    <div class="div-3">
      <el-carousel type="card" height="380px" :autoplay="false">
        <el-carousel-item v-for="item in message" :key="item.id">
          <h3 class="medium" v-if="item.id!=2">{{item.name}}</h3>
          <h3 class="medium1" v-else>
            <div>{{item.name1}}</div>
            <div>{{item.name2}}</div></h3>
          <el-collapse v-if="item.id!=2">
            <div style="height: 300px;overflow: auto">
              <el-collapse-item
                v-for="(subItem,index) in item.content" style="height: auto;"
                :name="item.id*10+index">
                <template slot="title">
                <span style="font-size: 14px;margin-left: 10px;color: rgb(7 140 191)">
                   {{index+1}}.{{subItem.title}}
                </span>
                </template>
                <span style="white-space:pre-wrap;word-break: break-all;margin-left: 5px;" :class="{myClass:checked[index]}">
                {{subItem.text}}
                </span>
                <br v-if="item.id==3"/>
                <el-checkbox v-model="checked[index]" v-if="item.id==3" style="margin-left: 650px">完成任务</el-checkbox>
              </el-collapse-item>
            </div>
          </el-collapse>
          <div v-else style="height: 300px">
            <el-card style="position: relative;width: 350px;height: 160px;background-color: transparent;
            transform: translate(2.7%,0%)">
              {{mserial}}
            </el-card>
            <el-card style="position: relative;width: 350px;height: 160px;transform: translate(107.7%,-100%);
            background-color: transparent">
              {{mqtt}}
            </el-card>
            <el-input style="position:relative;transform:translate(12px,-125px);width:295px;height:40px"
                      placeholder="请输入发送到串口消息" v-model="port1">
            </el-input>
            <el-input style="position:relative;transform:translate(88px,-125px);width:295px;height:40px;"
                      placeholder="请输入发送到消息件消息;" v-model="port2">
            </el-input>
            <el-button icon="el-icon-upload" style="height:40px;width:57px;position:relative;
            padding:0;transform:translate(-283.5px,-123px);font-size: 18px" type="primary" plain>
            </el-button>
            <el-button icon="el-icon-s-promotion" style="height:40px;width:57px;position:relative;
            padding:0;transform:translate(22px,-123px);font-size: 18px" type="primary" plain>
            </el-button>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div id="div-bubbles"></div>
    <canvas id="bubbles"></canvas>
  </div>
</template>


<script>
  export default {
    created() {
      this.getData()
      this.getMSG()
    },
    mounted() {
      this.test()
      for(let i=0;i<this.message[2].content.length;i++){
        this.total=this.total+1;
        this.checked.push(false)
      }
    },
    name: "Welcome",
    props:{
      sug:''
    },
    data(){
      return{
        message:[
          {id:1,name:'今日公告',
            content:[
              {title:'系统简介',text:'面向管理员的停车场管理系统，方便管理员管理停车场资源，实时了解停车场状况。'},
              {title:'系统功能',text:''},
              {title:'使用说明',text:''}
              ]
          },
          {id:2,name1:'串口调试',name2:'消息中间件',
            content:[
              {title:'消息1',text:'暂无'},
              {title:'消息2',text:'暂无'},
              {title:'消息3',text:'暂无'}
              ]
          },
          {id:3,name:'待办事项',
            content:[
              {title:'事项1',text:'暂无'},
              {title:'事项2',text:'暂无'},
              {title:'事项3',text:'暂无'}
              ]
          },
        ],
        checked:[],
        total:0,
        charge:window.sessionStorage.getItem("charge"),
        port1:'',
        port2:'',
        adminTotal:0,
        garTotal:0,
        cgarTotal:0,
        cgarRenum:0,
        mqtt:'',
        mserial:''
      }
    },
    watch:{
      sug(newValue){
        this.$http({
          url:'msg/'+newValue,
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.mqtt=res.data.data.mmq
            this.mserial=res.data.data.mserial
          }
          else {

          }
        },err=>{
          this.$message.error("请求数据失败，请检查网络")
        })
      }
    },
    methods:{
      getData(){
        this.$http({
          url:'total',
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.adminTotal=res.data.data.adminTotal
            this.garTotal=res.data.data.garTotal
            this.cgarTotal=res.data.data.cgarTotal
            this.cgarRenum=res.data.data.cgarRenum
          }
          else {

          }
        },err=>{
          this.$message.error("请求数据失败，请检查网络")
        })
      },
      getMSG(){
        this.$http({
          url:'msg/'+window.sessionStorage.getItem('currentGarage'),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.mqtt=res.data.data.mmq
            this.mserial=res.data.data.mserial
          }
          else {

          }
        },err=>{
          this.$message.error("请求数据失败，请检查网络")
        })
      },
      test(){
        var c = document.getElementById('bubbles'),
          ctx = c.getContext('2d'),
          width = window.innerWidth,
          height = window.innerHeight,
          particles = 60,
          minRadius = 5,
          maxRadius = 15,
          speed = 0.01,
          x = width / particles;

        var Bubbles = [];

        for (var i = 0; i < particles; i++) {
          Bubbles.push({
            x: i * x,
            y: height * Math.random(),
            r: minRadius + Math.random() * (maxRadius - minRadius),
            speed: 10 * Math.random()
          });
        }

        function bubble(myColor) {
          c.width = width;
          c.height = height;
          for (i = 0; i < Bubbles.length; i++) {
            var b = Bubbles[i];
            ctx.beginPath();
            ctx.arc(b.x, b.y, b.r, 0, 2 * Math.PI);

            b.alpha = .5 * (b.y / height);
            b.speed += speed;

            ctx.strokeStyle =myColor;
            ctx.stroke();
            ctx.fillStyle = "hsla(203, 75%, 69%," + b.alpha + ")";
            ctx.fill();
            b.y -= b.speed;
            if (b.y < 0) {
              b.y = height;
              b.speed = Math.random() * 5;
            }
          }
        }

        function draw() {
          let myColor=['rgb(32 80 255)','red','green',
            'rgb(72 204 113)']
          function GetRandomNum(Min,Max){
            var Range = Max - Min;
            var Rand = Math.random();
            return(Min + Math.round(Rand * Range));
          }
          let i=myColor[GetRandomNum(0,3)]
          bubble(i);
          window.requestAnimationFrame(draw);
        }

        function resizeCanvas() {
          width = window.innerWidth,
            height = window.innerHeight;
          c.width = width;
          c.height = height;
          draw();
        }
        resizeCanvas();
        window.addEventListener('resize', resizeCanvas, false);
      }
    }
  }
</script>

<style scoped>
  div{
    width: 100%;
    height: 100%;
  }
  #bubbles{
    width:100%;height:100%/*默认全屏显示 可自己设置高度640px*/;
    display:inline-block;vertical-align:baseline;
    position:absolute;
    z-index:0;
  }
  #div-bubbles{
    width:100%;
    height: 100%;
    background: url(../../assets/img/11.jpg);
    background-size: cover;
    z-index: 1;
    position:absolute;
    filter:alpha(opacity=40);
    -moz-opacity:0.6;
    -khtml-opacity: 0.6;
    opacity: 0.6;
  }

  .medium {
    color: #204abc;
    text-shadow: 2px 2px 3px #ffffff;
    font-size: 20px;
    line-height: 60px;
    text-align: center;
    margin-top: 0;
    margin-bottom: 20px;
  }
  .medium1{
    display: flex;
    justify-content: space-between;
    color: #204abc;
    text-shadow: 2px 2px 3px #ffffff;
    font-size: 20px;
    line-height: 60px;
    text-align: center;
    margin-top: 0;
    margin-bottom: 20px;
  }

  .el-carousel__item {
    background-color: #e6e6f7;
    width: 750px;
    opacity: 0.5;
  }
  .el-carousel__item.is-active.el-carousel__item--card.is-in-stage{
    opacity: 1;
    box-shadow: 0px 1px 8px #220b46;
  }
  #card1{
    width: 25%;
    height: 255px;
    position: absolute;
    left: 1%;
    /*left: 50%;
    right: 50%;
    transform: translate(-50%,0);*/
    top: 10px;
    margin-bottom: 5px;
    margin-top: 5px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    background-color: #fff9ea;
    /*background-color: #a6b4f1;*/
  }
  #card2{
    width: 19%;
    height: 120px;
    position: absolute;
    right: 51%;
    top: 15px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #a6f1bf;*/
  }
  #card3{
    width: 19%;
    height: 120px;
    position: absolute;
    right: 51%;
    top: 150px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #f1cea6;*/
  }
  #card4{
    width: 22%;
    height: 120px;
    position: absolute;
    right: 27.5%;
    top: 15px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #f1cea6;*/
  }
  #card5{
    width: 22%;
    height: 120px;
    position: absolute;
    right: 27.5%;
    top: 150px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #f1cea6;*/
  }
  #card6{
    width: 25%;
    height: 120px;
    position: absolute;
    right: 15px;
    top: 15px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #f1cea6;*/
  }
  #card7{
    width: 25%;
    height: 120px;
    position: absolute;
    right: 15px;
    top: 150px;
    opacity: 0.8;
    z-index: 5;
    border: none;
    /*background-color: #f1cea6;*/
  }
  .div-1{
    position: relative;
    height: 100%;
  }
  .div-2{
    position: absolute;
    width: 100%;
    height: 300px;
  }
  .div-3{
    position: absolute;
    top: 260px;
    width: 100%;
    height: 420px;
  }
  .myClass{
    text-decoration:line-through
  }
</style>
<style>
  .el-carousel.el-carousel--horizontal.el-carousel--card{
    /*height: 410px;*/
    height: 98%;
    width: 100%;
    top: 20px;
    z-index: 10;
  }
  .el-collapse{
    border-color: #a1ccd6;
    height: 320px!important;
  }
  .el-collapse-item__header{
    background-color: transparent;
    border-color: #a1ccd6;
  }
 .el-collapse-item__wrap{
    background-color: transparent;
   border-color:#a1ccd6;
  }
  #card1 .el-card__body{
    padding: 0;

  }
  #card2 .el-card__body,#card3 .el-card__body,#card4 .el-card__body,
  #card5 .el-card__body {
    padding: 0;
    height: 120px;
  }
  #card6 .el-card__body,#card7 .el-card__body{
    padding: 0;
    height: 120px;
  }
</style>
