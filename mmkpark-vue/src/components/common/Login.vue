<template>
  <div class="login-container">

    <div class="header" id="demo">
      <div class="login-box">
        <!--头像区域-->
        <div class="avatar-box">
          <img src="../../assets/img/7.png">
        </div>
        <!--登录表单区域-->
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules" label-width="0px" class="login-form">
          <!--用户名-->
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" prefix-icon="el-icon-s-custom" id="input1"></el-input>
          </el-form-item>
          <!--密码-->
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" id="input2"></el-input>
          </el-form-item>
          <!--按钮-->
          <el-form-item class="btns">
            <el-button type="primary" @click="login" class="btn1">登录</el-button>
            <el-button type="info" @click="resetLoginForm" class="btn1">重置</el-button>
            <el-button type="success" @click="enrollDialogVisible=true" class="btn1">注册</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="div-canvas"> </div>
      <canvas id="canvas"></canvas>

    </div>

    <audio controls autoplay="autoplay" class="audio" loop="loop">
      <source src="../../assets/music/music1.mp3" type="audio/mp3">
    </audio>


    <!--注册对话框-->
    <el-dialog title="注册" :visible.sync="enrollDialogVisible" @close="enrollDialogClosed">
      <!--内容主题区域-->
      <el-form :model="enrollForm" :rules="enrollFormRules" ref="enrollFormRef" label-width="70px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="enrollForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="enrollForm.password" type="password" show-password auto-complete="new-password"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="enrollForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="enrollForm.mobile"></el-input>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <span slot="footer">
          <el-button @click="enrollDialogVisible=false,$message.info('已取消注册')">取消</el-button>
          <el-button type="primary" @click="enroll">确定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "Login",
    created() {

    },
    mounted() {
      this.test();
    },
    data() {

      //验证邮箱的规则
      var checkEmail=(rule,value,callback)=>{
        //验证邮箱的正则表达式：通过regEmail.test(value),如果返回true说明校验通过
        const regEmail=/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/
        if(regEmail.test(value)){
          //邮箱校验通过，是合法邮箱
          callback()
        }
        else {
          //邮箱校验失败，邮箱不合法
          return callback(new Error('请输入合法的邮箱'))
        }
      }

      //验证手机号的规则
      var checkMobile=(rule,value,callback)=>{
        //验证邮箱的正则表达式:通过regEmail.test(value),如果返回true说明校验通过
        const regMobile=/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/
        if(regMobile.test(value)){
          //手机号校验通过，是合法手机号
          return callback();
        }
        else {
          //手机号校验失败，手机号不合法
          callback(new Error('请输入正确的手机号'));
        }
      }

      return {
        loginForm: {
          username: 'admin',
          password:'123456'
      },
        enrollForm:{
          username: '',
          password:'',
          email: '',
          mobile: '',
          charge:''
        },
      //表单对象的验证规则
      loginFormRules:{
        //验证用户名是否合法
        username:[
          {required:true,message:"请输入登录用户名",trigger:"blur"},
          {min:2,max:7,message:"长度在2到7个字符",trigger:"blur"}
        ],
        //验证密码是否合法
        password:[
          {required:true,message:"请输入登录密码",trigger:"blur"},
          {min:3,max:10,message:"长度在3到10个字符",trigger:"blur"}
        ]
        },
        //添加表单的验证规则对象
        enrollFormRules:{
          username:[
            {required:true,message:"请输入用户名",trigger:"blur"},
            {min:2,max:6,message:"长度在2到6个字符",trigger:"blur"}
          ],
          password:[
            {required:true,message:"请输入密码",trigger:"blur"},
            {min:6,max:15,message:"长度在6到15个字符",trigger:"blur"}
          ],
          email:[
            {required:true,message:"请输入邮箱",trigger:"blur"},
            {validator:checkEmail,trigger: 'blur'}
          ],
          mobile:[
            {required:true,message:"请输入手机号",trigger:"blur"},
            {validator:checkMobile,trigger: 'blur'}
          ]
        },
        enrollDialogVisible:false
      }
    },
    methods:{
      //获取车库列表
      getGarageList(){
        this.$http({
          url:'garage/list',
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
           let garageList=res.data.data
            window.sessionStorage.setItem('currentGarage',garageList[0].garageName)
          }
          else {
            this.$message.error("请求车库列表数据失败")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库列表数据失败，请检查网络")
        })
      },
      //点击重置按钮，重置登录表单
      resetLoginForm() {
        this.$refs.loginFormRef.resetFields();
      },
      //表单提交的预验证，符合规则validate函数传入valid=true，反之传入valid=false
      login(){
        this.$refs.loginFormRef.validate((valid)=>{
          if(!valid){return this.$message.error("请按要求输入");}
          //表单预验证通过，提交登录请求
          else{
            this.$http({
              url:'login',
              method:'post',
              data:this.loginForm
            }).then(res=>{
              if(res.data.code==200){
                //将登录成功的token保存到客户端的sessionStorage中
                window.sessionStorage.setItem("token",res.headers.authorization)
                //登录成功跳转到首页
                this.$router.push('/home')
                window.sessionStorage.setItem("loginUser",this.loginForm.username)
                window.sessionStorage.setItem("userType",res.data.data.userType)
                window.sessionStorage.setItem("isRecord",'false')
                window.sessionStorage.setItem("charge",res.data.data.charge)
                this.getGarageList()
                return this.$message.success("登录成功")
              }
              else {
                return this.$message.error(res.data.msg)
              }
            },err=>{
              this.$message.error('登录失败，请检查网络')
              return console.log(err)
            })
          }
        });
      },
      test(){
        let canvas = document.getElementById('canvas'),
          ctx = canvas.getContext('2d'),
          w = canvas.width = window.innerWidth,
          h = canvas.height = window.innerHeight,

          hue = 217,
          stars = [],
          count = 0,
          maxStars = 1300;//星星数量

        let canvas2 = document.createElement('canvas'),
          ctx2 = canvas2.getContext('2d');
        canvas2.width = 100;
        canvas2.height = 100;
        let half = canvas2.width / 2,
          gradient2 = ctx2.createRadialGradient(half, half, 0, half, half, half);
        gradient2.addColorStop(0.025, '#CCC');
        gradient2.addColorStop(0.1, 'hsl(' + hue + ', 61%, 33%)');
        gradient2.addColorStop(0.25, 'hsl(' + hue + ', 64%, 6%)');
        gradient2.addColorStop(1, 'transparent');

        ctx2.fillStyle = gradient2;
        ctx2.beginPath();
        ctx2.arc(half, half, half, 0, Math.PI * 2);
        ctx2.fill();


        function random(min, max) {
          if (arguments.length < 2) {
            max = min;
            min = 0;
          }

          if (min > max) {
            let hold = max;
            max = min;
            min = hold;
          }

          return Math.floor(Math.random() * (max - min + 1)) + min;
        }

        function maxOrbit(x, y) {
          let max = Math.max(x, y),
            diameter = Math.round(Math.sqrt(max * max + max * max));
          return diameter / 2;
          //星星移动范围，值越大范围越小，
        }

        let Star = function() {

          this.orbitRadius = random(maxOrbit(w, h));
          this.radius = random(60, this.orbitRadius) / 8;
          //星星大小
          this.orbitX = w / 2;
          this.orbitY = h / 2;
          this.timePassed = random(0, maxStars);
          this.speed = random(this.orbitRadius) / 50000;
          //星星移动速度
          this.alpha = random(2, 10) / 10;

          count++;
          stars[count] = this;
        }

        Star.prototype.draw = function() {
          let x = Math.sin(this.timePassed) * this.orbitRadius + this.orbitX,
            y = Math.cos(this.timePassed) * this.orbitRadius + this.orbitY,
            twinkle = random(10);

          if (twinkle === 1 && this.alpha > 0) {
            this.alpha -= 0.05;
          } else if (twinkle === 2 && this.alpha < 1) {
            this.alpha += 0.05;
          }

          ctx.globalAlpha = this.alpha;
          ctx.drawImage(canvas2, x - this.radius / 2, y - this.radius / 2, this.radius, this.radius);
          this.timePassed += this.speed;
        }

        for (let i = 0; i < maxStars; i++) {
          new Star();
        }

        function animation() {
          ctx.globalCompositeOperation = 'source-over';
          ctx.globalAlpha = 0.5;
          ctx.fillStyle = 'hsla(' + hue + ', 64%, 6%, 2)';
          ctx.fillRect(0, 0, w, h)

          ctx.globalCompositeOperation = 'lighter';
          for (let i = 1, l = stars.length; i < l; i++) {
            stars[i].draw();
          };

          window.requestAnimationFrame(animation);
        }
        animation();
      },
      //监听添加用户对话框的关闭事件
      enrollDialogClosed(){
        //重置表单内容
        this.$refs.enrollFormRef.resetFields()
      },
      enroll(){
        //对表单进行预校验，校验成功validate函数传入参数valid=true
        this.$refs.enrollFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'user/register',
              method:'post',
              data:this.enrollForm
            }).then(res=>{
              if(res.data.code==200){
                this.$message.success("注册成功")
              }
              else {
                this.$message.error("注册失败")
              }
            },err=>{
              this.$message.error("注册失败,用户名已存在")
              console.log(err)
            })
            this.enrollDialogVisible=false
          }
        })

      }
    }
  }
</script>

<style scoped>
  @import "../../assets/css/index_style.css";

    .login-container{
        background-color: cadetblue;
        height: 100%;
    }
    .login-box{
        width: 450px;
        height: 300px;
        background: rgba(0,0,0,.5);
        border-radius: 5px;
        box-shadow: 0 0 10px cornflowerblue;
        position: absolute;/*设置绝对布局*/
        left: 50%;
        top:50%;
        transform: translate(-50%,-50%);/*横轴移动-50%，纵轴移动-50%*/
        z-index: 5;
        /*最终效果位于正中心*/
    }
    .avatar-box{
        height: 130px;
        width: 130px;
        border-radius: 50%;
        position: absolute;
        left: 50%;
        padding: 10px;
        border: 1px solid white;
        box-shadow: 0 0 10px cornflowerblue;
        transform: translate(-50%,-50%);
    }
    .avatar-box img{
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
    }
    .btns{
        /*将按钮区域居右放置*/
        display: flex;
        justify-content: flex-end;
    }
    .login-form{
        position: absolute;
        bottom: 0;
        width: 100%;
        padding: 0 20px;
        box-sizing: border-box;
    }
  .btn1{
    background: rgba(0,0,0,.5);
  }
  .el-input.el-input--prefix{
    width: 300px!important;
    margin-left: 58px;
    background: rgba(0,0,0,.5);
  }

</style>
<style>
  #input1,#input2{
    background: transparent;
    color: #f7f7f7;
  }
</style>
