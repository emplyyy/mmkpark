<template>
  <div>
    <!--面包屑导航区域-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>车库管理</el-breadcrumb-item>
      <el-breadcrumb-item>车库添加</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="addGarage">
      <div style="text-align: center;margin-bottom: 10px">
        <span class="span1">车库添加表单</span>
      </div>
      <el-form :model="addForm" ref="addFormRef" label-width="90px" :rules="addFormRules">
        <el-form-item label="车库名" prop="garageName">
          <el-input v-model="addForm.garageName"></el-input>
        </el-form-item>
        <el-form-item label="车库图片" prop="garagePic">
          <el-upload
            class="avatar-uploader"
            action="http://127.0.0.1:8088/upload"
            :show-file-list="true"
            :on-success="handleAvatarSuccess">
            <img v-if="addForm.garagePic" :src="'http://127.0.0.1:8088/pic/'+addForm.garagePic" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="车库种类" prop="type">
          <el-select v-model="addForm.type" placeholder="请选择车库类型" filterable>
            <el-option
              v-for="item in garageType"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车库地址" prop="address">
          <el-input v-model="addForm.address"></el-input>
        </el-form-item>
        <el-form-item label="车库电话" prop="garageInfo">
          <el-input v-model="addForm.garageInfo"></el-input>
        </el-form-item>
        <el-form-item label="车库管理员" prop="keeper">
          <el-select v-model="addForm.keeper" placeholder="请选择车库管理员" filterable clearable>
            <el-option
              v-for="(item,index) in userList"
              :label="item.username"
              :value="item.username"
              :disabled="chargeState[index]">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <div style="text-align: right;margin-bottom: 10px">
        <el-button type="primary" @click="addConfirm">确定添加</el-button>
        <el-button type="info" @click="resetForm">重置表单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
    export default {
      created() {
        this.getUserList()
        this.getGarageList()
      },
      mounted() {
      },
      name: "Add",
      data(){
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
        var checkGarage=(rule,value,callback)=>{
          let state=true
          for (let i=0;i<this.garageList.length;i++){
            if(this.garageList[i].garageName==value){
              state=false
              break
            }
          }
          if(state){
            return callback()
          }
          else {
            callback(new Error('车库名已存在'));
          }
        }

        return{
          userList:[],
          garageList:[],
          addForm:{
            garageName:'',
            garagePic:'',
            type:'',
            garageInfo:'',
            address:'',
            keeper:'',
          },
          garageType:['露天车库','地下车库'],
          //添加表单的验证规则对象
          addFormRules:{
            garageInfo:[
              {required:true,message:"请输入手机号",trigger:"blur"},
              {validator:checkMobile,trigger: 'blur'}
            ],
            garageName: [
              {required:true,message:'请输入车库名',trigger:'blur'},
              {validator:checkGarage,trigger: 'blur'}
            ]
          },
          chargeState:[]

        }
      },
      methods:{
        getUserList(){
          this.$http({
            url:'user/list',
            method:'get',
          }).then(res=>{
            if(res.data.code==200){
              this.userList=res.data.data
              this.chargeState=[]
              for(let i=0;i<this.userList.length;i++){
                if(this.userList[i].charge==''||this.userList[i].charge==null){
                  this.chargeState.push(false)
                }
                else{
                  this.chargeState.push(true)
                }
              }
            }
            else {
              this.$message.error("请求用户列表数据失败，请刷新重新请求")
            }
          },err=>{
            console.log(err)
            return this.$message.error("请求用户列表数据失败，请检查网络")
          })
        },
        //获取车库列表
        getGarageList(){
          this.$http({
            url:'garage/list',
            method:'get',
          }).then(res=>{
            if(res.data.code==200){
              this.garageList=res.data.data
            }
            else {
              this.$message.error("请求车库列表数据失败，请刷新重新请求")
            }
          },err=>{
            console.log(err)
            return this.$message.error("请求车库列表数据失败，请检查网络")
          })
        },
        resetForm(){
          //重置表单内容
          this.$refs.addFormRef.resetFields()
        },
        addConfirm(){
          this.$refs.addFormRef.validate(valid=>{
            if(!valid){return}
            else{
                this.$http({
                  url:'garage/save/',
                  method:'post',
                  data:this.addForm
                }).then(res=>{
                  if(res.data.code==200){
                    if(this.addForm.keeper!=''&&this.addForm.keeper!=null){
                      this.$http({
                        url:'user/save',
                        method:'post',
                        data:{username:this.addForm.keeper,charge:this.addForm.garageName}
                      }).then(res=>{
                        this.getUserList()
                      },err=>{

                      })
                    }
                    this.$message.success('添加车库成功!')
                    this.$emit('item-click');
                  }
                  else{

                  }
                },err=>{
                  this.$message.error("添加用车库失败，去检查网络");
                  console.log(err)
                })

            }
          })
        },
        handleAvatarSuccess(res, file) {
          this.addForm.garagePic = res.data.data
        },
        reload(){

        },
      }
    }
</script>

<style scoped>
  .addGarage{
    margin-right: 300px;
    margin-left: 300px;
    padding-right: 20px;
  }
  .span1{
    text-align: center;
    font-size: 20px;
    color: darkslateblue;
    text-shadow: 2px 2px 2px cadetblue;
  }

</style>
<style>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
