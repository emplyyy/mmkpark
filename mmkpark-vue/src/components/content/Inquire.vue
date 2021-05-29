<template>
    <div>
      <div class="div-head">
        <!--面包屑导航区域-->
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>车位管理</el-breadcrumb-item>
          <el-breadcrumb-item>车位查询</el-breadcrumb-item>
        </el-breadcrumb>
        <div id="show0"></div>
      </div>
      <el-container class="inquire-container">
        <el-card>
          <div id="div1">
            <span class="span1">车位查询表</span>
            <div id="div2">
              <span id="show">
                空车位/总车位 : {{number}}/{{total}}
              </span>
              <el-tooltip  effect="light" content="显示全部车位情况" placement="top">
                <el-switch v-model="visibleState[0]" @change="totalVisibleStateChange"></el-switch>
              </el-tooltip>
            </div>
          </div>
          <el-table :data="interimParkingList[pagenum-1]" border :row-class-name="rowStyle">
            <el-table-column type="index" width="25" :index="myIndex"></el-table-column>
            <el-table-column label="id" width="48">
              <template slot-scope="scope">
                {{scope.row.parkName}}
              </template>
            </el-table-column>
            <el-table-column label="中心点" width="84">
              <template slot-scope="scope">
                ({{scope.row.center.x}},{{scope.row.center.y}})
              </template>
            </el-table-column>
            <el-table-column label="点1" width="84">
              <template slot-scope="scope">
                ({{scope.row.border1.x}},{{scope.row.border1.y}})
              </template>
            </el-table-column>
            <el-table-column label="点2" width="84">
              <template slot-scope="scope">
                ({{scope.row.border2.x}},{{scope.row.border2.y}})
              </template>
            </el-table-column>
            <el-table-column label="点3" width="84">
              <template slot-scope="scope">
                ({{scope.row.border3.x}},{{scope.row.border3.y}})
              </template>
            </el-table-column>
            <el-table-column label="点4" width="84">
              <template slot-scope="scope">
                ({{scope.row.border4.x}},{{scope.row.border4.y}})
              </template>
            </el-table-column>
            <el-table-column label="显示" width="64">
              <!--scope.row中的值是当前遍历到的对象-->
              <template slot-scope="scope">
                <el-switch v-model="visibleState[scope.$index+1+(pagenum-1)*pagesize]"
                           @change="visibleStateChange(scope.row,scope.$index+1+(pagenum-1)*pagesize)">
                </el-switch>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="50">
              <template slot-scope="scope">
                {{showState(scope.$index+(pagenum-1)*pagesize)}}
              </template>
            </el-table-column>
            <el-table-column label="修正" width="64">
              <!--scope.row中的值是当前遍历到的对象-->
              <template slot-scope="scope">
                <el-switch v-model="modifyState[scope.$index+(pagenum-1)*pagesize]"
                           @change="modifyStateChange(scope.row,scope.$index+(pagenum-1)*pagesize)">
                </el-switch>
              </template>
            </el-table-column>
          </el-table>
          <!--分页区域-->
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                         :current-page="pagenum" :page-sizes="[1,2,3,4,5,7,10,15]"
                         :page-size="pagesize" layout="total,sizes,prev,pager,next,jumper"
                         :total="total" :pager-count="5" v-show="pageshow">
          </el-pagination>
        </el-card>
        <div>
          <div class="div-img">
            <img :src="'http://127.0.0.1:8088/pic/'+address">
            <canvas id="canvas1" @mousemove="showMouseMove($event)" @click="getCord($event)"></canvas>
          </div>
          <!--偏移量设置-->
          <el-card id="entering">
            <div style="float: left;padding-top: 5px;margin-right: 50px">偏移量设置:</div>
            <div style="float: left;margin-right: 50px">
              <el-tooltip  effect="light" content="原点设置开关" placement="bottom">
                <el-switch v-model="offsetState[0]" @change="offsetStateChange(0)"
                           style="position: relative;transform: translate(0,3px)"></el-switch>
              </el-tooltip>
              <div class="div-entering" id="show1"></div>
            </div>

            <div style="float: left;margin-right: 10px">
              <el-tooltip  effect="light" content="偏移点设置开关" placement="bottom">
                <el-switch v-model="offsetState[1]" @change="offsetStateChange(1)"
                           style="position: relative;transform: translate(0,3px)"></el-switch>
              </el-tooltip>
              <div class="div-entering" id="show2"></div>
            </div>

            <el-tooltip  effect="light" content="确认保存" placement="bottom">
              <el-button type="primary" icon="el-icon-check" size="mini" style="margin-right: 5px;margin-left: 50px"
                         @click="saveRecord">
              </el-button>
            </el-tooltip>
            <el-tooltip  effect="dark" content="退出设置" placement="bottom">
              <el-button type="info" icon="el-icon-close" size="mini" style="margin-left: 0;"
                         @click="quitRecord">
              </el-button>
            </el-tooltip>
          </el-card>
        </div>
      </el-container>
    </div>
</template>

<script>
  import 'video.js/dist/video-js.css'
  import {videoPlayer} from 'vue-video-player'
  import 'videojs-flash'
  export default {
    created() {
      this.getGarage()
      //请求车位数据
      this.getParkingList()
    },
    mounted() {
      //画布定义
      this.canvas=document.getElementById('canvas1')
      this.canvas.width=this.canvas.offsetWidth;//让线变好看
      this.canvas.height=this.canvas.offsetHeight;
      this.ctx=this.canvas.getContext('2d');

    },
    name: "Inquire",
    props:{
      sug:'',
    },
    components: {
      videoPlayer
    },
    data(){
      return{
        playerOptions: {
          height: '432',
          sources: [{
            type: 'rtmp/mp4',
            src: 'rtmp://192.168.1.111/live/test'
          }],
          techOrder: ['flash'],
          autoplay: false,
          controls: true,
          poster: 'https://surmon-china.github.io/vue-quill-editor/static/images/surmon-9.jpg'
        },
        //当前页数
        pagenum:1,
        //当前每页显示多少条数据
        pagesize:7,
        //存储车位总数
        total:0,
        //存储空车位数量
        number:0,
        parkingList:[],
        interimParkingList:[[]],//将车位信息按分页情况存储，实现动态显示每页的车位的数量
        parkingStateList:[],//存储后端发生的车位识别的结果
        parkingState:[],//存储前端处理后的车位状态结果
        visibleState:[],
        canvas:{},
        ctx:{},
        pageshow:true,
        address:'',
        offsetState:[false,false],
        offsets:[0,0],
        newOffsets:[0,0,0,0],
        modifyState:[]
      }
    },
    watch:{
      sug(newValue){
        this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height)
        this.address=''
        this.getGarage()
        this.$http({
          url:'parkcoor/list/'+newValue,
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.parkingList=res.data.data
            this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize))
            this.visibleState.splice(0,this.total+1)
            this.visibleState.push(false);//首个值作为总开关的状态
            this.parkingState.splice(0,this.total)
            this.total=0
            this.number=0
            for (let i=0;i<this.parkingList.length;i++){
              this.total=this.total+1
              this.visibleState.push(false);
            }
            for (let i=0;i<this.parkingList.length;i++){
              this.parkingState.push(this.parkingList[i].status)
              this.$set(this.parkingState,i,this.parkingState[i])
            }

            this.$http({
              url:'msg/'+newValue,
              method:'get',
            }).then(res1=>{
              if(res1.data.code==200){
                if(res1.data.data.mpycv==null){
                  this.number=this.total
                  this.$message.error("该车库未完成车位识别")
                }
                else {
                  this.parkingStateList=eval(res1.data.data.mpycv)
                  if(this.offsets[0]!=0||this.offsets[1]!=0){//有设置偏移量才执行
                    for(let i=0;i<this.parkingStateList.length/2;i++){
                      this.parkingStateList[2*i]=this.parkingStateList[2*i]-this.offsets[0]
                      this.parkingStateList[2*i+1]=this.parkingStateList[2*i]-this.offsets[1]
                    }
                  }
                  //初始化车位的空否状态
                  this.isPointInRect(this.parkingList,this.parkingStateList)
                  let data=[]
                  for(let i=0;i<this.total;i++){
                    data.push({id:this.parkingList[i].id,status:this.parkingState[i]})
                  }
                  this.$http({
                    url:'parkcoor/save',
                    method:'post',
                    data:data
                  }).then(res=>{

                  },err=>{

                  })
                }

              }
              else {

              }
            },err1=>{
              this.$message.error("请求数据失败，请检查网络")
            })

            //初始化分页
            this.handleSizeChange(this.pagesize);
            this.pagenum=1
            this.pageshow = false;//让分页隐藏
            this.$nextTick(() => {//重新渲染分页
              this.pageshow = true;
            })
          }
          //请求失败说明该车库车位一个也没有
          else{
            if(this.total){
              this.pagenum=1
              this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));
              this.parkingList.splice(0,this.total)
              this.visibleState.splice(1,this.total)
              this.parkingState.splice(0,this.total)
              this.total=0
              this.number=0
            }
            this.$message.error("该车库车位为空，请先添加车位")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车位列表数据失败，请检查网络")
        })
      }
    },
    methods:{
      getParkingList(){
        this.$http({
          url:'parkcoor/list/'+window.sessionStorage.getItem("currentGarage"),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.parkingList=res.data.data
            this.visibleState.push(false);//首个值作为总开关的状态
            for (let i=0;i<this.parkingList.length;i++){
              this.total=this.total+1
              this.visibleState.push(false);
            }
            for (let i=0;i<this.parkingList.length;i++){
              this.parkingState.push(this.parkingList[i].status)
              this.$set(this.parkingState,i,this.parkingState[i])
            }

            this.$http({
              url:'msg/'+window.sessionStorage.getItem('currentGarage'),
              method:'get',
            }).then(res1=>{
              if(res1.data.code==200){
                if(res1.data.data.mpycv==null){
                  this.number=this.total
                  this.$message.error("该车库未完成车位识别")
                }
                else {
                  this.parkingStateList=eval(res1.data.data.mpycv)
                  if(this.offsets[0]!=0||this.offsets[1]!=0){//有设置偏移量才执行
                    for(let i=0;i<this.parkingStateList.length/2;i++){
                      this.parkingStateList[2*i]=this.parkingStateList[2*i]-this.offsets[0]
                      this.parkingStateList[2*i+1]=this.parkingStateList[2*i]-this.offsets[1]
                    }
                  }
                  //初始化车位的空否状态
                  this.isPointInRect(this.parkingList,this.parkingStateList)
                  let data=[]
                  for(let i=0;i<this.total;i++){
                    data.push({id:this.parkingList[i].id,status:this.parkingState[i]})
                  }
                  this.$http({
                    url:'parkcoor/save',
                    method:'post',
                    data:data
                  }).then(res=>{

                  },err=>{

                  })
                }


              }
              else {

              }

            },err1=>{
              this.$message.error("请求数据失败，请检查网络")
            })

            //初始化分页
            this.handleSizeChange(this.pagesize);

          }
          //请求失败说明该车库车位一个也没有
          else{
            if(this.total){
              this.pagenum=1
              this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));
              this.parkingList.splice(0,this.total)
              this.visibleState.splice(1,this.total)
              this.total=0
              this.number=0
            }
            this.$message.error("该车库车位为空，请先添加车位")
          }

        },err=>{
          console.log(err)
          return this.$message.error("请求车位列表数据失败，请检查网络")
        })
      },
      getGarage(){
        this.$http({
          url:'garage/'+window.sessionStorage.getItem('currentGarage'),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.offsets=eval(res.data.data.offset)
            this.$set(this.offsets)
            if(res.data.data.garagePic==null){
              this.$message.error("该车库还未录入车库图片")
            }
            else {
              console.log(res.data.data.garagePic)
              this.address=res.data.data.garagePic
            }
          }
          else {
            this.$message.error("请求车库图片数据失败，请刷新重新请求")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库图片数据失败，请刷新检查网络")
        })
      },
      //canvas画框函数
      draw(a,color,mode,size){
        this.ctx.globalCompositeOperation=mode;
        this.ctx.beginPath();
        for(let i=1;i<5;i++){
          this.ctx.moveTo(a.border1.x,a.border1.y)
          this.ctx.lineTo(a.border2.x,a.border2.y)
          this.ctx.moveTo(a.border2.x,a.border2.y)
          this.ctx.lineTo(a.border3.x,a.border3.y)
          this.ctx.moveTo(a.border3.x,a.border3.y)
          this.ctx.lineTo(a.border4.x,a.border4.y)
          this.ctx.moveTo(a.border4.x,a.border4.y)
          this.ctx.lineTo(a.border1.x,a.border1.y)
        }
        this.ctx.strokeStyle=color;
        this.ctx.lineWidth=size;
        this.ctx.stroke();
        this.ctx.closePath();
      },
      //判断车位状态函数
      isPointInRect(data,point){
        for (let i=0;i<this.total;i++){
          for (let j=0;j<(this.parkingStateList.length/2);j++){
            let a=(data[i].border2.x-data[i].border1.x)*(point[2*j+1]-data[i].border1.y)-(data[i].border2.y-data[i].border1.y)*(point[2*j]-data[i].border1.x)
            let b=(data[i].border3.x-data[i].border2.x)*(point[2*j+1]-data[i].border2.y)-(data[i].border3.y-data[i].border2.y)*(point[2*j]-data[i].border2.x)
            if((a>0&&b>0)||(a<0&&b<0)){
              let c=(data[i].border4.x-data[i].border3.x)*(point[2*j+1]-data[i].border3.y)-(data[i].border4.y-data[i].border3.y)*(point[2*j]-data[i].border3.x)
              if((b>0&&c>0)||(b<0&&c<0)){
                let d=(data[i].border1.x-data[i].border4.x)*(point[2*j+1]-data[i].border4.y)-(data[i].border1.y-data[i].border4.y)*(point[2*j]-data[i].border4.x)
                if((c>0&&d>0)||(c<0&&d<0)){
                  this.parkingState[i]=false
                  this.$set(this.parkingState,i,this.parkingState[i])
                }
              }
            }
          }
        }
        this.number=0
        for (let i=0;i<this.parkingState.length;i++){
          if(this.parkingState[i]!=false&&this.parkingState[i]!="false"){
            this.number=this.number+1
          }
        }
        this.modifyState=[]
        for(let i=0;i<this.parkingState.length;i++){
          if(this.parkingState[i]==false||this.parkingState[i]=="false"){
            this.modifyState.push(true)
          }
          else{
            this.modifyState.push(false)
          }
        }
      },
      //自定义索引
      myIndex(index){
        return index+(this.pagenum-1)*this.pagesize+1
      },
      //监听页面范围改变的事件,实现每页显示指定数量的车位
      handleSizeChange(newSize){
        this.pagesize=newSize
        let i=Math.ceil(this.total/newSize)//向上取整
        let n=0
        for(let j=0;j<i;j++){
          if(j==(i-1)){//最后一页是特殊情况
            this.interimParkingList[j]=new Array()//这句话必须有，否则报错
            for (n;n<(this.total-j*newSize);n++){
              this.interimParkingList[j].push(this.parkingList[j*newSize+n])
            }
          }
          else{
            this.interimParkingList[j]=new Array()//这句话必须有，否则报错
            for (n;n<newSize;n++){
              this.interimParkingList[j].push(this.parkingList[j*newSize+n])
            }
          }
          n=0
        }
      },
      //监听页面值改变的事件,实现页面切换
      handleCurrentChange(newCurrent){
        this.pagenum=newCurrent
      },
      //总开关的显示状态，实现一键显示全部车位
      totalVisibleStateChange(){
        for (let i=1;i<this.visibleState.length;i++){
          //由于用js改变switch开关状态无法触发change事件所以需要段代码主动实现
          if(!this.visibleState[0]&&this.visibleState[i]){
            this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height)
          }
          else if(this.visibleState[0]&&!this.visibleState[i]){
            if(this.parkingState[i-1]==false||this.parkingState[i-1]=="false"){
              let data={center:{x:this.parkingList[i-1].center.x,y:this.parkingList[i-1].center.y},
                border1:{x:this.parkingList[i-1].border1.x+5,y:this.parkingList[i-1].border1.y+5},
                border2:{x:this.parkingList[i-1].border2.x-5,y:this.parkingList[i-1].border2.y+5},
                border3:{x:this.parkingList[i-1].border3.x-5,y:this.parkingList[i-1].border3.y-5},
                border4:{x:this.parkingList[i-1].border4.x+5,y:this.parkingList[i-1].border4.y-5}}
              this.draw(data,'red','source-over',2)
            }
            else {
              this.draw(this.parkingList[i-1],'green','source-over',2)
            }
          }
          this.visibleState[i]=this.visibleState[0]
        }
      },
      //单个车位的显示状态，实现单个车位的显示
      visibleStateChange(data,index){
        if(this.visibleState[index]){
          if(this.parkingState[index-1]==false||this.parkingState[index-1]=="false"){
            let Data={center:{x:data.center.x,y:data.center.y},
              border1:{x:data.border1.x+5,y:data.border1.y+5},
              border2:{x:data.border2.x-5,y:data.border2.y+5},
              border3:{x:data.border3.x-5,y:data.border3.y-5},
              border4:{x:data.border4.x+5,y:data.border4.y-5}}
            this.draw(Data,'red','source-over',2)
          }
          else {
            this.draw(data,'green','source-over',2)
          }
        }
        else{
          if(this.parkingState[index-1]==false||this.parkingState[index-1]=="false"){
            let Data={center:{x:data.center.x,y:data.center.y},
              border1:{x:data.border1.x+5,y:data.border1.y+5},
              border2:{x:data.border2.x-5,y:data.border2.y+5},
              border3:{x:data.border3.x-5,y:data.border3.y-5},
              border4:{x:data.border4.x+5,y:data.border4.y-5}}
            this.draw(Data,'red','destination-out',5)
          }
          else {
            this.draw(data,'red','destination-out',5)
          }

          for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
            if(this.visibleState[i]){
              if(this.parkingState[i-1]==false||this.parkingState[i-1]=="false"){
                let data={center:{x:this.parkingList[i-1].center.x,y:this.parkingList[i-1].center.y},
                  border1:{x:this.parkingList[i-1].border1.x+5,y:this.parkingList[i-1].border1.y+5},
                  border2:{x:this.parkingList[i-1].border2.x-5,y:this.parkingList[i-1].border2.y+5},
                  border3:{x:this.parkingList[i-1].border3.x-5,y:this.parkingList[i-1].border3.y-5},
                  border4:{x:this.parkingList[i-1].border4.x+5,y:this.parkingList[i-1].border4.y-5}}
                this.draw(data,'red','source-over',2)
              }
              else {
                this.draw(this.parkingList[i-1],'green','source-over',2)
              }
            }
          }

        }
      },
      showState(index){
        if(this.parkingState[index]==false||this.parkingState[index]=="false"){
          return '有车'
        }
        else {
          return '无车'
        }
      },
      rowStyle({row,rowIndex}){
        if(this.parkingState[rowIndex+(this.pagenum-1)*this.pagesize]==false||this.parkingState[rowIndex+(this.pagenum-1)*this.pagesize]=="false"){
          return 'redStyle'
        }
        else{
          return 'greenStyle'
        }
      },
      //显示鼠标坐标
      showMouseMove(e){
        let show=document.getElementById("show0")
        let x=e.offsetX;
        let y=e.offsetY;
        show.innerHTML="当前鼠标在图像上的坐标为:(X,Y)=("+x+","+y+")";
      },
      offsetStateChange(index){
        if(index==0){
          if(this.offsetState[0]){
            this.offsetState[1]=false
          }
        }
        else {
          if(this.offsetState[1]){
            this.offsetState[0]=false
          }
        }

      },
      getCord(e){
        if(this.offsetState[0]){
          let show=document.getElementById("show1")
          show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
          this.newOffsets[0]=parseInt(e.offsetX)
          this.newOffsets[1]=parseInt(e.offsetY)
        }
        else if(this.offsetState[1]){
          let show=document.getElementById("show2")
          show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
          this.newOffsets[2]=parseInt(e.offsetX)
          this.newOffsets[3]=parseInt(e.offsetY)
        }
      },
      saveRecord(){
        this.offsets[0]=(this.newOffsets[2]-this.newOffsets[0])
        this.offsets[1]=(this.newOffsets[3]-this.newOffsets[1])
        this.$http({
          url:'garage/save',
          method:'post',
          data:{garageName:window.sessionStorage.getItem('currentGarage'),offset:JSON.stringify(this.offsets)}
        }).then(res=>{
          for (let i=0;i<2;i++){//关闭全部选择录入点的开关
            this.offsetState[i]=false
            this.$set(this.offsetState,i,this.offsetState[i])
          }
          let show1=document.getElementById("show1")
          let show2=document.getElementById("show2")
          show1.innerHTML=''
          show2.innerHTML=''
          this.newOffsets=[0,0,0,0]
          this.$message.success('偏移量设置成功')
          this.getGarage()
          //请求车位数据
          this.getParkingList()
        },err=>{
          this.$message.error('偏移量设置失败，请检查网络')
        })
      },
      quitRecord(){
        for (let i=0;i<2;i++){//关闭全部选择录入点的开关
          this.offsetState[i]=false
          this.$set(this.offsetState,i,this.offsetState[i])
        }
        let show1=document.getElementById("show1")
        let show2=document.getElementById("show2")
        show1.innerHTML=''
        show2.innerHTML=''
        this.newOffsets=[0,0,0,0]
        this.$message.info("已取消偏移量设置")
      },
      modifyStateChange(data,index){
        if(this.modifyState[index]){
          this.parkingState[index]=false
          if(this.visibleState[index+1]){
            this.draw(data,'green','destination-out',5)
            for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
              if(i!=index+1){
                if(this.parkingState[i-1]==false||this.parkingState[i-1]=="false"){
                  let data={center:{x:this.parkingList[i-1].center.x,y:this.parkingList[i-1].center.y},
                    border1:{x:this.parkingList[i-1].border1.x+5,y:this.parkingList[i-1].border1.y+5},
                    border2:{x:this.parkingList[i-1].border2.x-5,y:this.parkingList[i-1].border2.y+5},
                    border3:{x:this.parkingList[i-1].border3.x-5,y:this.parkingList[i-1].border3.y-5},
                    border4:{x:this.parkingList[i-1].border4.x+5,y:this.parkingList[i-1].border4.y-5}}
                  this.draw(data,'red','source-over',2)
                }
                else {
                  this.draw(this.parkingList[i-1],'green','source-over',2)
                }
              }
            }
            let Data={center:{x:data.center.x,y:data.center.y},
              border1:{x:data.border1.x+5,y:data.border1.y+5},
              border2:{x:data.border2.x-5,y:data.border2.y+5},
              border3:{x:data.border3.x-5,y:data.border3.y-5},
              border4:{x:data.border4.x+5,y:data.border4.y-5}}
            this.draw(Data,'red','source-over',2)
          }

        }
        else{
          this.parkingState[index]=null
          if(this.visibleState[index+1]){
            let Data={center:{x:data.center.x,y:data.center.y},
              border1:{x:data.border1.x+5,y:data.border1.y+5},
              border2:{x:data.border2.x-5,y:data.border2.y+5},
              border3:{x:data.border3.x-5,y:data.border3.y-5},
              border4:{x:data.border4.x+5,y:data.border4.y-5}}
            this.draw(Data,'red','destination-out',5)
            for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
              if(i!=index+1){
                if(this.parkingState[i-1]==false||this.parkingState[i-1]=="false"){
                  let data={center:{x:this.parkingList[i-1].center.x,y:this.parkingList[i-1].center.y},
                    border1:{x:this.parkingList[i-1].border1.x+5,y:this.parkingList[i-1].border1.y+5},
                    border2:{x:this.parkingList[i-1].border2.x-5,y:this.parkingList[i-1].border2.y+5},
                    border3:{x:this.parkingList[i-1].border3.x-5,y:this.parkingList[i-1].border3.y-5},
                    border4:{x:this.parkingList[i-1].border4.x+5,y:this.parkingList[i-1].border4.y-5}}
                  this.draw(data,'red','source-over',2)
                }
                else {
                  this.draw(this.parkingList[i-1],'green','source-over',2)
                }
              }
            }
            this.draw(data,'green','source-over',2)
          }
        }
        this.$http({
          url:'parkcoor/save',
          method:'post',
          data:[{id:this.parkingList[index].id,status:this.parkingState[index]}]
        }).then(res=>{
          if(this.parkingState[index]==false){
            this.number=this.number-1
          }
          else {
            this.number=this.number+1
          }
        },err=>{

        })
      }
    }
    }
</script>

<style scoped>
  .div-head{
    display: flex;
    justify-content: space-between;
  }
  .inquire-container{
    height: 100%;
  }
  .span1{
    text-align: center;
    font-size: 20px;
    color: darkslateblue;
    text-shadow: 2px 2px 2px cadetblue;
  }
  .div-img{
    width: 768px;
    height: 432px;
    position: relative;
    border-radius: 5px;
    box-shadow: 0 0 10px cornflowerblue;
    flex-shrink: 0;
    /*position: absolute;
    right:10px;*/
  }
  #canvas1{
    width: 100%;
    height: 100%;
    border-radius: 5px;
    position: absolute;
    right: 0;
    bottom: 0;
    top:0;
    left: 0;
  }
  .div-img img{
    width: 100%;
    height: 100%;
    border-radius: 5px;
    object-fit: fill;
    z-index: 1;
  }
  .el-card{
    width: 47%;
  }
  #div1{
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  #div2{
    margin-top: 10px;
    text-align: right;
  }
  .btn1{
    margin-left: 15px;
    padding: 10px;
  }
  #show{
    padding-left: 10px;
    padding-right: 10px;
    margin-right:100px;
    width:350px;
    height: 25px;
    border:2px solid #336699;
    color: forestgreen;
    text-align: center;
    box-shadow: 0 0 10px cornflowerblue;
  }
  #show0{
    margin-left: 10px;
    margin-right: 25px;
    margin-top: 15px;
    width:350px;
    height: 25px;
    border:2px solid #336699;
    color: crimson;
    text-align: center;
    box-shadow: 0 0 10px cornflowerblue;
  }
  #entering{
    margin-top: 5px;
    margin-left: 0;
    width: 768px;
  }
  .div-entering{
    margin-right: 0;
    margin-left: 10px;
    border:2px solid #336699;
    width:150px;
    height: 25px;
    text-align: center;
    float: right;
  }
</style>
<style>
  .el-table .greenStyle{
    color: forestgreen!important;
  }
  .el-table .redStyle{
    color: red!important;
  }
</style>
