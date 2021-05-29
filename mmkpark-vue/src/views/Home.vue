<template>
  <el-container class="home-container">
    <!--头部区域-->
    <el-header>
      <div>
        <img src="../assets/img/8.png">
        <span>停车管理系统</span>
      </div>
      <div :style="'position: absolute;right: 380px;height:40px;width:'+myLength+'px'" class="tool">
        <el-select v-model="currentGarage" filterable :style="'width:'+myLength+'px'" placeholder="请输入选择车库"
                   id="myLength" @change="getLength($event)">
          <el-option
            v-for="item in garageList"
            :key="item.garageId"
            :label="item.garageName"
            :value="item.garageName">
          </el-option>
        </el-select>
      </div>
       <span class="span-user"><i class="el-icon-user-solid"></i> {{$store.state.loginUser}} </span>
      <el-button type="primary" plain @click="reload" icon="el-icon-refresh"
                 style="padding:0;height: 39px;width: 70px;position: absolute;top: 10px;right: 100px">刷新</el-button>
      <el-button type="info" @click="logout">退出</el-button>
    </el-header>
    <!--页面主体区域-->
    <el-container class="main-container">
      <!--侧边栏-->
      <el-aside :width="isCollapse ? '68px' : '235px'">
        <div class="toggle-button" @click="toggleCollapse">|||</div>
        <el-menu background-color="rgb(50,65,87)" active-text-color="rgb(32,160,255)"
                 :collapse="isCollapse" :collapse-transition="false" router
                 :default-active="this.$route.path" text-color="white">
          <el-submenu :index="item.id+''" v-for="item in menuList" :key="item.id">
            <template slot="title">
              <i :class="icon[item.id]" :style="colorControl(item.children)"></i>
              <span :style="colorControl(item.children)">{{item.name}}</span>
            </template>
            <el-menu-item :index="subItem.path" v-for="subItem in item.children">
              <i :class="subItem.id"></i>
              <span>{{subItem.name}}</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <!--右侧主题-->
      <el-main>
        <router-view :sug="currentGarages" @item-click="update"></router-view>
      </el-main>
    </el-container>

  </el-container>
</template>

<script>
  import Record from '../components/content/Record'
  import Inquire from "../components/content/Inquire"
  import Monitor from "../components/content/Monitor";
  import Welcome from "../components/content/Welcome";
  export default {
    created() {
      this.$store.commit('updateLoginUser',window.sessionStorage.getItem('loginUser'))
      this.getGarageList()
    },
    name: "Home",
    components:{
      Record,
      Inquire,
      Monitor,
      Welcome
    },
    data(){
      return{
        menuList:[
          {id:1, name:'首页', children:[{path:'/welcome',name: '首页',id:'el-icon-s-home'}]},
          {id:2, name:'用户管理', children:[{path:'/user',name: '用户列表',id:'el-icon-menu'}]},
          {id:3, name:'车库管理', children:[{path:'/add',name: '车库添加',id:'el-icon-circle-plus'},{path:'/list',name: '车库列表',id:'el-icon-s-data'}]},
          {id:4, name:'车位管理', children:[{path:'/record',name: '车位删录',id:'el-icon-s-operation'},{ path:'/inquire',name:'车位查询',id:'el-icon-s-grid'}]},
          {id:5, name:'监控管理', children:[{path:'/monitor',name: '实时监控',id:'el-icon-camera-solid'},]}
        ],
        icon:{
          '1':'el-icon-house',
          '2':'el-icon-user',
          '3':'el-icon-place',
          '4':'el-icon-map-location',
          '5':'el-icon-video-camera',
        },
        isCollapse: true,
        garageList:[],
        currentGarage:'',
        currentGarages:'',
        myLength:0,
      }
    },
    methods:{
      getGarageList(){
        this.$http({
          url:'garage/list',
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.garageList=res.data.data
            this.myLength=window.sessionStorage.getItem("currentGarage").length*14+50
            this.currentGarage=window.sessionStorage.getItem("currentGarage")
            this.currentGarages=window.sessionStorage.getItem("currentGarage")
          }
          else {
            this.$message.error("请求车库列表数据失败，车库列表为空，请先添加车库")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库列表数据失败，请检查网络")
        })
      },
      //退出：清除token并跳转至登录界面
      logout(){
        if(window.sessionStorage.getItem("isRecord")=='true'){
          this.$confirm('修改的车位信息未提交，是否提交', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(()=>{
            this.$http({
              url:'parkcoor/emp/'+this.currentGarage,
              method:'get',
            }).then(res=>{
              if(res.data.code==200){
                this.$http({
                  url:'parkcoor/save',
                  method:'post',
                  data:JSON.parse(window.sessionStorage.getItem("updateParkingList")),
                }).then(res=>{
                  if(res.data.code==200){
                    this.$message.success('提交修改成功!')
                    window.sessionStorage.clear()
                    this.$router.push('/login')
                  }
                  else{
                    this.$message.error('提交修改失败!')
                  }
                },err=>{
                  this.$message.error("提交修改失败,请检查网络")
                })
              }
            },err=>{
              this.$message.error("清空车位数据失败,请检查网络")
            })
          }).catch(()=>{
            window.sessionStorage.clear()
            this.$router.push('/login')
            this.$message.info('已取消提交修改')
          })
        }
        window.sessionStorage.clear()
        this.$router.push('/login')
      },
      //点击按钮切换菜单的折叠与展开
      toggleCollapse(){
        this.isCollapse=!this.isCollapse
      },

      colorControl(a){
        for(let i=0;i<a.length;i++){
          if(this.$route.path==a[i].path){
            return 'color:rgb(32,160,255)'
          }
        }
        return 'color:#ffffff'
      },
      getLength(event){
        if(window.sessionStorage.getItem("isRecord")=='true'){
          this.currentGarage=window.sessionStorage.getItem("currentGarage")
          this.$confirm('修改的车位信息未提交，是否提交', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(()=>{
            this.$http({
              url:'parkcoor/emp/'+this.currentGarage,
              method:'get',
            }).then(res=>{
              if(res.data.code==200){
                this.$http({
                  url:'parkcoor/save',
                  method:'post',
                  data:JSON.parse(window.sessionStorage.getItem("updateParkingList")),
                }).then(res1=>{
                  if(res1.data.code==200){
                    this.$message.success('提交成功!')
                  }
                  else{
                    this.$message.error('提交失败!')
                  }
                },err1=>{
                  console.log(err1)
                  this.$message.error('提交失败，请检查网络')
                })
              }
              else {
                this.$message.error("清空车位列表数据失败!")
              }
            },err=>{
              console.log(err)
              this.$message.error("清空车位列表数据失败，请检查网络")
            })
            window.sessionStorage.setItem("isRecord",'false')
          }).catch(()=>{
            window.sessionStorage.setItem("isRecord",'false')
            this.$message({
              type: 'info',
              message: '已取消提交修改'
            });
          })
        }
        else {
          this.myLength=event.length*14+50
          //this.currentGarage=event
          this.currentGarages=event
          window.sessionStorage.setItem("currentGarage",this.currentGarage)
        }
      },
      update(){
        this.$http({
          url:'garage/list',
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.garageList=res.data.data
            this.currentGarage=this.garageList[0].garageName
            this.currentGarages=this.garageList[0].garageName
            window.sessionStorage.setItem("currentGarage",this.currentGarage)
          }

        },err=>{
          this.$message.error('更新车库数据失败，请检查网络')
        })
      },
      reload(){
        window.location.reload()
      },
    }
  }
</script>

<style scoped>
  .home-container{
    height: 100%;
  }
  .el-header{
    background-color: #3f5478;
    display: flex;
    justify-content: space-between;
    padding-left: 0;
    align-items: center;
    color: white;
    font-size: 20px;
  }
  .el-aside{
    background-color: rgb(50,65,87);
  }
  .el-menu{
    border-right: none;
  }
  .el-main{
    background-color: #e7e7e7;
    padding: 0px;
    position: relative;
  }
  .el-header div{
    width: 270px;
    height: 60px;
    display: flex;
    justify-content: space-between;
  }
  .el-header div img{
    width: 60px;
    height: 60px;
    margin-left: 5px;
    border-radius: 50%;
    object-fit: cover;
  }
  .el-header div span{
    display: flex;
    align-items: center;
    font-family: "Bradley Hand ITC";
    font-size: 30px;
    text-shadow: -2px 3px 2px #00d8ff;
  }
  .toggle-button{
    background-color: #3c3c66;
    font-size: 10px;
    line-height: 24px;
    color: #ffffff;
    text-align: center;
    letter-spacing: 0.2em;
    cursor: pointer;
  }
  .span-user{
    color: #edf1ff;
    position: absolute;
    right: 200px;
    font-family: "Bradley Hand ITC";
    font-weight: bold;
    font-size: x-large;
    top: 17px;
    text-shadow: 2px 2px 1px #5300ff;
  }
  .el-icon-user-solid{
    font-size: 27px;
    color: #edf1ff;
  }
</style>
<style>
  .tool .el-select__caret.el-input__icon.el-icon-arrow-up{
    height: 40px;
  }
 .tool .el-select .el-input.el-input--suffix .el-input__inner{
    background-color: transparent;
    border: 0;
    color: white;
    text-align: right;
  }
</style>
