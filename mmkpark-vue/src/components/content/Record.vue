  <template>
  <div>
    <div class="div-head">
      <!--面包屑导航区域-->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>车位管理</el-breadcrumb-item>
        <el-breadcrumb-item>车位删录</el-breadcrumb-item>
      </el-breadcrumb>
      <div id="show"></div>
    </div>
    <el-container class="record-container">
      <el-card>
        <div id="div1">
          <span class="span1">车位信息表</span>
          <div id="div2"><!--要想这里的switch和table里的switch保持同步必须绑定同一个数据-->
            <el-tooltip  effect="light" content="显示全部车位" placement="top">
              <el-switch v-model="visibleState[0]" @change="totalVisibleStateChange"></el-switch>
            </el-tooltip>
            <el-button type="primary" class="btn1" @click="addParkingList" plain>添加车位</el-button>
            <el-button type="danger" class="btn1" @click="clearParkingList" plain>清空车位</el-button>
            <el-button type="success" plain class="btn1" @click="submit">提交修改</el-button>
          </div>
        </div>
        <el-table :data="interimParkingList[pagenum-1]" border>
          <el-table-column type="index" width="41" :index="myIndex"></el-table-column>
          <el-table-column label="id" width="54" prop="parkName">
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
          <el-table-column label="显示" width="65">
            <!--scope.row中的值是当前遍历到的对象-->
            <template slot-scope="scope">
              <el-switch v-model="visibleState[scope.$index+1+(pagenum-1)*pagesize]"
                         @change="visibleStateChange(scope.row,scope.$index+1+(pagenum-1)*pagesize)">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template slot-scope="scope">
              <el-tooltip  effect="light" content="修改车位" placement="right">
                <el-button type="primary" icon="el-icon-edit" size="mini"
                           @click="modifyParking(scope.$index+(pagenum-1)*pagesize)"></el-button>
              </el-tooltip>
              <el-tooltip  effect="dark" content="删除车位" placement="right">
                <el-button type="danger" icon="el-icon-delete" size="mini"
                           @click="deleteParking(scope.$index+(pagenum-1)*pagesize)">
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <!--分页区域-->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                         :current-page="pagenum" :page-sizes="[1,2,3,4,5,10,15]"
                         :page-size="pagesize" layout="total,sizes,prev,pager,next,jumper"
                         :total="total" :pager-count="5">
        </el-pagination>
      </el-card>

      <div>
        <div class="div-img">
          <img :src="'http://127.0.0.1.101:8088/pic/'+address">
          <canvas id="canvas1" @mousemove="showMouseMove($event)" @click="getCord($event)"></canvas>
        </div>

        <!--录入车位区域-->
        <el-card id="entering">
          <el-table border :data="recordState[0]">
            <el-table-column label="中心点">
              <template slot-scope="scope">
                <div class="div-entering" id="show1"></div>
                <el-tooltip effect="light" content="录入中心点" placement="bottom">
                  <el-switch class="switch-entering" :disabled="scope.row" v-model="recordState[1]"
                             @change="select(scope.column.label)">
                  </el-switch>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="点1">
              <template slot-scope="scope">
                <div class="div-entering" id="show2"></div>
                <el-tooltip effect="light" content="录入点1" placement="bottom">
                  <el-switch class="switch-entering" :disabled="scope.row" v-model="recordState[2]"
                             @change="select(scope.column.label)">
                  </el-switch>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="点2">
              <template slot-scope="scope">
                <div class="div-entering" id="show3"></div>
                <el-tooltip effect="light" content="录入点2" placement="bottom">
                  <el-switch class="switch-entering" :disabled="scope.row" v-model="recordState[3]"
                             @change="select(scope.column.label)">
                  </el-switch>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="点3">
              <template slot-scope="scope">
                <div class="div-entering" id="show4"></div>
                <el-tooltip effect="light" content="录入点3" placement="bottom">
                  <el-switch class="switch-entering" :disabled="scope.row" v-model="recordState[4]"
                             @change="select(scope.column.label)">
                  </el-switch>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="点4">
              <template slot-scope="scope">
                <div class="div-entering" id="show5"></div>
                <el-tooltip effect="light" content="录入点4" placement="bottom">
                  <el-switch class="switch-entering" :disabled="scope.row" v-model="recordState[5]"
                             @change="select(scope.column.label)">
                  </el-switch>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70">
              <template slot-scope="scope">
                <el-tooltip  effect="light" content="确认保存" placement="top">
                  <el-button class="btn2" type="primary" icon="el-icon-check" size="mini" :disabled="scope.row"
                             @click="saveRecord">
                  </el-button>
                </el-tooltip>
                <el-tooltip  effect="dark" content="退出修改" placement="bottom">
                  <el-button class="btn2" type="info" icon="el-icon-close" size="mini" :disabled="scope.row"
                             @click="quitRecord">
                  </el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

    </el-container>

    <!--添加车位对话框-->
    <el-dialog title="确认则提交修改的车位ID，取消则不修改车位ID" :visible.sync="addDialogVisible" @close="addDialogClosed">
      <!--内容主题区域-->
      <el-form :model="addForm" ref="addFormRef" label-width="70px">
        <el-form-item label="ID" prop="parkName">
          <el-input v-model="addForm.parkName" placeholder="请输入车位ID"></el-input>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <span slot="footer">
          <el-button @click="addDialogVisible=false">取消</el-button>
          <el-button type="primary" @click="addID">确定</el-button>
        </span>
    </el-dialog>

  </div>
</template>

<script>
  export default {
    created() {
      this.getParkingList()
      this.getGarage()
    },
    mounted() {
      //画布定义
      this.canvas=document.getElementById('canvas1')
      this.canvas.width=this.canvas.offsetWidth;//让线变好看
      this.canvas.height=this.canvas.offsetHeight;
      this.ctx=this.canvas.getContext('2d');
    },
    name: "Record",
    props:{
      sug:'',
    },
    data() {
      return {
        //当前页数
        pagenum:1,
        //当前每页显示多少条数据
        pagesize:5,
        //存储车位总数
        total:0,
        //存储车位信息
        parkingList:[],
        //注意js中二维数组声明只能在一维数组的某一个值再赋值一个一维数组
        // 首先声明成[[]]形式，然后在使用时要再一次声明interimParkingList[j]=new Array(),其中j为需要再创建数组的数组内值的下标
        interimParkingList:[[]],//将车位信息按分页情况存储，实现动态显示每页的车位的数量
        visibleState:[],
        canvas:{},
        ctx:{},
        recordState:[[true],false,false,false,false,false],
        newCord:{center:{x:'',y:''},border1:{x:'',y:''},border2:{x:'',y:''},border3:{x:'',y:''},border4:{x:'',y:''},
          parkName:'',garName:'',id:'',status:''},
        recordIndex:'',
        isRecord:true,//记录是否修改
        //控制添加用户的对话框的显示与隐藏
        addDialogVisible:false,
        changeDialogVisible:false,
        addForm:{parkName:''},
        address:''
      }
    },
    watch:{
      sug(newValue){
        this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height)
        this.address=''
        for (let i=1;i<6;i++){//关闭全部选择录入点的开关
          this.recordState[i]=false
        }
        this.recordState[0][0]=true//关闭修改车位开关
        this.$set(this.recordState[0],0,this.recordState[0][0]);
        let show1=document.getElementById("show1")
        let show2=document.getElementById("show2")
        let show3=document.getElementById("show3")
        let show4=document.getElementById("show4")
        let show5=document.getElementById("show5")
        show1.innerHTML=''
        show2.innerHTML=''
        show3.innerHTML=''
        show4.innerHTML=''
        show5.innerHTML=''
        this.getGarage()
        this.$http({
          url:'parkcoor/list/'+newValue,
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.parkingList=res.data.data
            this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize))
            this.visibleState.splice(0,this.total+1)
            this.visibleState.push(false)//首个值作为总开关的状态
            this.total=0
            for (let i=0;i<this.parkingList.length;i++){
              this.total=this.total+1
              this.visibleState.push(false);
            }
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
              this.visibleState[0]=false
              this.total=0
            }
            this.$message.error("该车库车位为空，请开始添加车位")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车位列表数据失败，请检查网络")
        })
      }
    },
    methods: {
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
        if(a.center.x!=0||a.center.y!=0){
          if(size==2||size==1){
            this.ctx.font="bold 20px Arial"
            this.ctx.fillStyle='red'
            this.ctx.textAlign = 'center'
            this.ctx.fillText(a.parkName, a.center.x, a.center.y)
          }
          else{
            this.ctx.font="bold 20px Arial"
            this.ctx.textAlign = 'center'
            this.ctx.strokeText(a.parkName, a.center.x, a.center.y)
          }
        }
      },
      setSession(key,value){
        window.sessionStorage.setItem(key,JSON.stringify(value));
      },
      getGarage(){
        this.$http({
          url:'garage/'+window.sessionStorage.getItem('currentGarage'),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
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
            //初始化分页
            this.handleSizeChange(this.pagesize);
          }
          //请求失败说明该车库车位一个也没有
          else{
            if(this.total){
              this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));
              this.parkingList.splice(0,this.total)
              this.visibleState.splice(1,this.total)
              this.total=0
            }
            this.$message.error("该车库车位为空，请开始添加车位")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车位列表数据失败，请检查网络")
        })
      },
      //自定义索引
      myIndex(index){
        return index+(this.pagenum-1)*this.pagesize+1
      },
      //总开关的显示状态，实现一键显示全部车位
      totalVisibleStateChange(){
        for (let i=1;i<this.visibleState.length;i++){
          //由于用js改变switch开关状态无法触发change事件所以需要段代码主动实现
          if(!this.visibleState[0]&&this.visibleState[i]){
            this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height)
            // this.draw(this.parkingList[i-1],'red','destination-out',3)
          }
          else if(this.visibleState[0]&&!this.visibleState[i]){
            this.draw(this.parkingList[i-1],'green','source-over',2)
          }
          this.visibleState[i]=this.visibleState[0]
        }
      },
      //单个车位的显示状态，实现单个车位的显示
      visibleStateChange(data,index){
        if(this.visibleState[index]){
          this.draw(data,'green','source-over',2)
        }
        else{
          this.draw(data,'red','destination-out',5)
          for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
            if(this.visibleState[i]){
              this.draw(this.parkingList[i-1],'green','source-over',1)
            }
          }
        }
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
      //添加车位
      addParkingList(){
        this.parkingList.push(
          {center:{x:0,y:0},border1:{x:0,y:0},border2:{x:0,y:0},border3:{x:0,y:0},border4:{x:0,y:0},
            parkName:'',garName:this.sug,id:'',status:null}
        )
        this.setSession("updateParkingList",JSON.parse(JSON.stringify(this.parkingList)))

        this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));//必须在总车位增加前先清空
        this.total=this.total+1;//车位总数加1
        this.visibleState.push(false);//车位状态数加1
        this.pagenum=Math.ceil(this.total/this.pagesize);//自动跳转到最后一页
        this.handleSizeChange(this.pagesize); //再重新存储
        if(window.sessionStorage.getItem("isRecord")=='false'){
          window.sessionStorage.setItem("isRecord",'true')
        }

        this.$message.success("添加车位成功！");
      },
      //清空车位
      clearParkingList(){
        //弹框提示是否确认清空
        this.$confirm('此操作将清空全部车位，是否继续', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          this.visibleState[0]=false//清空总状态开关置0
          this.totalVisibleStateChange()//所有显示的车位全部清空
          this.parkingList.splice(0,this.total);//确认则清空parkingList全部车位

          this.setSession("updateParkingList",JSON.parse(JSON.stringify(this.parkingList)))

          this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));//确认则清空interimParkingList全部车位
          this.total=0;
          if(window.sessionStorage.getItem("isRecord")=='false'){
            window.sessionStorage.setItem("isRecord",'true')
          }
          this.$message({
            type: 'success',
            message: '清空车位成功!'
          });}).catch(()=>{
          this.$message({
            type: 'info',
            message: '已取消清空'
          });
        })
      },
      //删除车位
      deleteParking(index){
        //弹框提示是否确认删除
        this.$confirm('此操作将删除该车位，是否继续', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          if(this.visibleState[index+1]){//如果删除的车位显示着，则删除不显示
            this.draw(this.parkingList[index],'red','destination-out',3)
          }
          this.parkingList.splice(index,1)

          this.setSession("updateParkingList",JSON.parse(JSON.stringify(this.parkingList)))

          this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));//必须在总数减少前清空
          this.total=this.total-1;//车位总数减1
          this.visibleState.splice(index+1,1);//车位状态数减1
          for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
            if(this.visibleState[i]){
              this.draw(this.parkingList[i-1],'green','source-over',2)
            }
          }
          this.handleSizeChange(this.pagesize);//必须在总数减少后重新存储
          if((index==this.total)&&!(index-this.pagesize*(this.pagenum-1))){
            //如果删除的是最后一页的数据，且最后一页只有 一个数据时，自动跳转到前一页
            this.pagenum=this.pagenum-1
          }
          if(this.total==0){//如果删除完是最后一个车位，总状态开关也应该置0
            this.visibleState[0]=false
          }
          if(window.sessionStorage.getItem("isRecord")=='false'){
            window.sessionStorage.setItem("isRecord",'true')
          }
          this.$message({
            type: 'success',
            message: '删除车位成功!'
          });}).catch(()=>{
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        })
      },
      //修改车位
      modifyParking(index){
      //弹框提示修改车位
        this.$confirm(
          '此操作将重新录入车位,请在图片下方选择要录入的点，然后在图片上选取点，注意除中心点的四个点请选择车位框的四个顶点，并从左上顶点开始以顺时针录入',
          '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          this.newCord=JSON.parse(JSON.stringify(this.parkingList[index]))
          let show1=document.getElementById("show1")
          show1.innerHTML="(X,Y)=("+parseInt(this.newCord.center.x)+","+parseInt(this.newCord.center.y)+")";
          let show2=document.getElementById("show2")
          show2.innerHTML="(X,Y)=("+parseInt(this.newCord.border1.x)+","+parseInt(this.newCord.border1.y)+")";
          let show3=document.getElementById("show3")
          show3.innerHTML="(X,Y)=("+parseInt(this.newCord.border2.x)+","+parseInt(this.newCord.border2.y)+")";
          let show4=document.getElementById("show4")
          show4.innerHTML="(X,Y)=("+parseInt(this.newCord.border3.x)+","+parseInt(this.newCord.border3.y)+")";
          this.addDialogVisible=true;
          let show5=document.getElementById("show5")
          show5.innerHTML="(X,Y)=("+parseInt(this.newCord.border4.x)+","+parseInt(this.newCord.border4.y)+")";
          this.recordState[0][0]=false//打开修改车位开关
          this.$set(this.recordState[0],0,this.recordState[0][0]);
          //recordState[0]是el-table绑定的数据，0是该数据的下标，recordState[0][0]是具体改变的值
          //强制el-table重新遍历绑定的数据，刷新el-table达到及时监听数据改变的效果,switch开关状态改变会自动执行这一动作
          this.recordIndex=index//记录要修改的车位下标
          this.$message({
            type: 'success',
            message: '请开始录入车位'
          });}).catch(()=>{
          this.$message({
            type: 'info',
            message: '已取消修改车位'
          });
        })
      },
      //选择录入的点
      select(label){
        switch (label) {
          case '中心点':
            if (this.recordState[1]){
              this.recordState[2]=false;//同时只能有一个点可以录入
              this.recordState[3]=false;
              this.recordState[4]=false;
              this.recordState[5]=false;
            }
            break;

          case '点1':
            if (this.recordState[2]){
              this.recordState[1]=false;//同时只能有一个点可以录入
              this.recordState[3]=false;
              this.recordState[4]=false;
              this.recordState[5]=false;
            }
            break;

          case '点2':
            if (this.recordState[3]){
              this.recordState[1]=false;//同时只能有一个点可以录入
              this.recordState[2]=false;
              this.recordState[4]=false;
              this.recordState[5]=false;
            }
            break;

          case '点3':
            if (this.recordState[4]){
              this.recordState[2]=false;//同时只能有一个点可以录入
              this.recordState[1]=false;
              this.recordState[3]=false;
              this.recordState[5]=false;
            }
            break;

          case '点4':
            if (this.recordState[5]){
              this.recordState[2]=false;//同时只能有一个点可以录入
              this.recordState[3]=false;
              this.recordState[1]=false;
              this.recordState[4]=false;
            }
            break;
        }
      },
      //获取点坐标
      getCord(e){
          if(this.recordState[1]){
            let show=document.getElementById("show1")
            show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
            this.newCord.center.x=parseInt(e.offsetX)
            this.newCord.center.y=parseInt(e.offsetY)
          }
          else if(this.recordState[2]){
            let show=document.getElementById("show2")
            show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
            this.newCord.border1.x=parseInt(e.offsetX)
            this.newCord.border1.y=parseInt(e.offsetY)
          }
          else if(this.recordState[3]){
            let show=document.getElementById("show3")
            show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
            this.newCord.border2.x=parseInt(e.offsetX)
            this.newCord.border2.y=parseInt(e.offsetY)
          }
          else if(this.recordState[4]){
            let show=document.getElementById("show4")
            show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
            this.newCord.border3.x=parseInt(e.offsetX)
            this.newCord.border3.y=parseInt(e.offsetY)
          }
          else if(this.recordState[5]){
            let show=document.getElementById("show5")
            show.innerHTML="(X,Y)=("+parseInt(e.offsetX)+","+parseInt(e.offsetY)+")";
            this.newCord.border4.x=parseInt(e.offsetX)
            this.newCord.border4.y=parseInt(e.offsetY)
          }
      },
      //保存新修改的车位
      saveRecord(){
        if(this.visibleState[this.recordIndex+1]){//保存前如果修改的车位显示着，则删除显示
          this.draw(this.parkingList[this.recordIndex],'red','destination-out',3)
        }
        let a=this.parkingList[this.recordIndex].parkName
        let newData=JSON.parse(JSON.stringify(this.newCord))//该方法实现传值不传地址
        this.parkingList.splice(this.recordIndex,1,newData)//保存
        this.parkingList[this.recordIndex].parkName=a
        this.parkingList[this.recordIndex].garName=this.sug

        if(window.sessionStorage.getItem("isRecord")=='false'){
          window.sessionStorage.setItem("isRecord",'true')
        }
        this.setSession("updateParkingList",JSON.parse(JSON.stringify(this.parkingList)))


        this.handleSizeChange(this.pagesize);//重新刷新数据
        for (let i=1;i<this.visibleState.length;i++){//有显示状态的全部重新画，避免误删
          if(this.visibleState[i]){
            this.draw(this.parkingList[i-1],'green','source-over',2)
          }
        }
        for (let i=1;i<6;i++){//关闭全部选择录入点的开关
          this.recordState[i]=false
        }
        this.recordState[0][0]=true//关闭修改车位开关
        this.$set(this.recordState[0],0,this.recordState[0][0]);
        let show1=document.getElementById("show1")
        let show2=document.getElementById("show2")
        let show3=document.getElementById("show3")
        let show4=document.getElementById("show4")
        let show5=document.getElementById("show5")
        show1.innerHTML=''
        show2.innerHTML=''
        show3.innerHTML=''
        show4.innerHTML=''
        show5.innerHTML=''

        this.$message.success("保存新修改的车位成功！")
      },
      //取消新车位修改
      quitRecord(){
        for (let i=1;i<6;i++){//关闭全部选择录入点的开关
          this.recordState[i]=false
        }
        this.recordState[0][0]=true//关闭修改车位开关
        this.$set(this.recordState[0],0,this.recordState[0][0]);
        let show1=document.getElementById("show1")
        let show2=document.getElementById("show2")
        let show3=document.getElementById("show3")
        let show4=document.getElementById("show4")
        let show5=document.getElementById("show5")
        show1.innerHTML=''
        show2.innerHTML=''
        show3.innerHTML=''
        show4.innerHTML=''
        show5.innerHTML=''
        this.$message.info("已取消修改车位")
      },
      //显示鼠标坐标
      showMouseMove(e){
        let show=document.getElementById("show")
          let x=e.offsetX;
          let y=e.offsetY;
          show.innerHTML="当前鼠标在图像上的坐标为:(X,Y)=("+x+","+y+")";
      },
      submit(){
        //提交修改的车位数据
        this.$confirm('新修改的车位信息表将替换原车位信息表，是否继续', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          //模拟提交修改数据
          this.$http({
            url:'parkcoor/emp/'+this.sug,
            method:'get',
          }).then(res=>{
            if(res.data.code==200){
              this.$http({
                url:'parkcoor/save',
                method:'post',
                data:this.parkingList,
              }).then(res1=>{
                if(res1.data.code==200){
                  this.$message.success('提交成功!')
                }
                else{
                  this.$message.error('提交失败')
                }
              },err1=>{
                console.log(err1)
                this.$message.error('提交失败，请检查网络')
              })
            }
            else {
              this.$message.error("清空车位列表数据失败")
            }
          },err=>{
            console.log(err)
            this.$message.error("清空车位列表数据失败,请检查网络")
          })
          window.sessionStorage.setItem("isRecord",'false')
        }).catch(()=>{
          this.$message({
            type: 'info',
            message: '已取消提交修改'
          });
        })
      },
      //监听添加用户对话框的关闭事件
      addDialogClosed(){
        //重置表单内容
        this.$refs.addFormRef.resetFields()
      },
      addID(){
        this.parkingList[this.recordIndex].parkName=this.addForm.parkName
        this.interimParkingList.splice(0,Math.ceil(this.total/this.pagesize));//先清空
        this.handleSizeChange(this.pagesize);//再刷新数据
        if(window.sessionStorage.getItem("isRecord")=='false'){
          window.sessionStorage.setItem("isRecord",'true')
        }
        this.setSession("updateParkingList",JSON.parse(JSON.stringify(this.parkingList)))
        this.addDialogVisible=false
      }

    }
  }
</script>

<style scoped>
  .div-head{
    display: flex;
    justify-content: space-between;
  }
  .record-container{
    height: 100%;
  }
  .div-img{
    width: 768px;
    height: 432px;
    position: relative;
    border-radius: 5px;
    box-shadow: 0 0 10px cornflowerblue;
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
    width: 45%;
  }
  #div1{
    display: flex;
    justify-content: space-between;
  }
  #div2{
    margin-bottom: 5px;
    text-align: right;
  }
  .btn1{
    margin-left: 10px;
    padding: 10px;
  }
  .btn2{
    margin-left: 2px;
  }
  .span1{
    text-align: center;
    font-size: 20px;
    color: darkslateblue;
    text-shadow: 2px 2px 2px cadetblue;
  }
  #show{
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
    margin-left: 0;
    border:2px solid #336699;
    width:118px;
    height: 25px;
    text-align: center;
  }
  .el-switch.switch-entering{
    margin-left: 38px;
    margin-top: 10px;
  }
</style>
