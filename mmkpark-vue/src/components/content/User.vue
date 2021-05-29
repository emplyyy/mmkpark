<template>
    <div>
      <!--面包屑导航区域-->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        <el-breadcrumb-item>用户列表</el-breadcrumb-item>
      </el-breadcrumb>
      <!--卡片区域-->
      <el-card>
        <!--搜索与添加区域-->
        <span class="span1">用户查询表</span>
        <el-row type="flex" justify="space-between">
          <div>
            <el-select v-model="query" placeholder="请输入用户名进行搜索" filterable clearable style="width: 260px;">
              <el-option
                v-for="item in userList"
                :label="item.username"
                :value="item.username">
              </el-option>
            </el-select>
            <el-button icon="el-icon-search" @click="search"></el-button>
          </div>
          <el-button type="primary" @click="addDialogVisible=true,operate='添加用户'">添加用户</el-button>
        </el-row>
        <!--用户列表区域-->
        <!--自动遍历:data="userList"中的userList-->
        <el-table :data="interimUserList[pagenum-1]" border stripe>
          <el-table-column type="index" :index="myIndex"></el-table-column>
          <el-table-column label="用户名" prop="username" ></el-table-column>
          <el-table-column label="邮箱" prop="email"></el-table-column>
          <el-table-column label="电话" prop="mobile"></el-table-column>
          <el-table-column label="负责车库" prop="charge"></el-table-column>
          <el-table-column label="权限" prop="userType"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-tooltip  effect="light" content="修改用户" placement="left">
                <el-button type="primary" icon="el-icon-edit" size="mini"
                           @click="modifyUser(scope.$index+(pagenum-1)*pagesize,scope.row.username)"></el-button>
              </el-tooltip>
              <el-tooltip  effect="dark" content="删除用户" placement="top">
                <el-button type="danger" icon="el-icon-delete" size="mini"
                           @click="deleteUser(scope.$index+(pagenum-1)*pagesize,scope.row.username)"></el-button>
              </el-tooltip>
              <el-tooltip  effect="light" content="修改密码" placement="right">
                <el-button type="warning" icon="el-icon-key" size="mini"
                           @click="modifyPassword(scope.$index+(pagenum-1)*pagesize)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <!--分页区域-->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                       :current-page="pagenum" :page-sizes="[1,2,3,4,5,10,15]"
                       :page-size="pagesize" layout="total,sizes,prev,pager,next,jumper"
                       :total="total" :pager-count="5" v-if="pageshow">
        </el-pagination>
      </el-card>
      <!--添加用户对话框-->
      <el-dialog title="添加用户" :visible.sync="addDialogVisible" @close="addDialogClosed">
        <!--内容主题区域-->
        <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="70px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="addForm.username"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="addForm.password" type="password" show-password auto-complete="new-password"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="addForm.email"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="addForm.mobile"></el-input>
          </el-form-item>
          <el-form-item label="负责车库" prop="charge">
            <el-select v-model="addForm.charge" placeholder="请选择车库" filterable clearable>
              <el-option
                v-for="(item,index) in garageList"
                :key="item.garageId"
                :label="item.garageName"
                :value="test(item.garageName)"
                :disabled="keeperState[index]">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <!--底部区域-->
        <span slot="footer">
          <el-button @click="addDialogVisible=false,$message.success('已取消添加用户')">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </span>
      </el-dialog>
      <!--修改用户对话框-->
      <el-dialog title="修改用户" :visible.sync="modifyDialogVisible" @close="modifyDialogClosed">
        <!--内容主题区域-->
        <el-form :model="modifyForm" :rules="addFormRules" ref="modifyFormRef" label-width="70px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="modifyForm.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="modifyForm.email"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="modifyForm.mobile"></el-input>
          </el-form-item>
          <el-form-item label="负责车库" prop="charge">
            <el-select v-model="modifyForm.charge" placeholder="请选择车库" filterable clearable>
              <el-option
                v-for="(item,index) in garageList"
                :key="item.garageId"
                :label="item.garageName"
                :value="test(item.garageName)"
                :disabled="keeperState[index]">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <!--底部区域-->
        <span slot="footer">
          <el-button @click="modifyDialogVisible=false,$message.success('已取消修改用户')">取消</el-button>
          <el-button type="primary" @click="confirmModify">确定</el-button>
        </span>
      </el-dialog>
      <!--验证权限对话框-->
      <el-dialog title="输入用户密码验证权限" :visible.sync="confirmDialogVisible" @close="confirmDialogClosed">
        <!--内容主题区域-->
        <el-form :model="confirmForm" :rules="addFormRules" ref="confirmFormRef" label-width="70px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="confirmForm.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="confirmForm.password" type="password"></el-input>
          </el-form-item>
        </el-form>
        <!--底部区域-->
        <span slot="footer">
          <el-button @click="confirmDialogVisible=false,$message.success('已取消修改')">取消</el-button>
          <el-button type="primary" @click="confirmRight">确定</el-button>
        </span>
      </el-dialog>
      <!--修改密码对话框-->
      <el-dialog title="修改密码" :visible.sync="passwordDialogVisible" @close="passwordDialogClosed">
        <!--内容主题区域-->
        <el-form :model="passwordForm" :rules="passwordFormRules" ref="passwordFormRef" label-width="70px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="passwordForm.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="原密码" prop="opwd">
            <el-input v-model="passwordForm.opwd" type="password" show-password auto-complete="new-password"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="cpwd">
            <el-input v-model="passwordForm.cpwd" type="password" show-password auto-complete="new-password"></el-input>
          </el-form-item>
        </el-form>
        <!--底部区域-->
        <span slot="footer">
          <el-button @click="passwordDialogVisible=false,$message.success('已取消修改密码')">取消</el-button>
          <el-button type="primary" @click="confirmPassword">确定</el-button>
        </span>
      </el-dialog>
    </div>
</template>

<script>
  export default {
    name: "User",
    created() {
      this.getUserList()
      this.getGarageList()
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

      return{
        //请求搜索的用户名
        query:'',
        //当前页数
        pagenum:1,
        //当前每页显示多少条数据
        pagesize:5,
        //存储请求的用户数据
        userList:[],
        //用于分页
        interimUserList:[[]],
        //车库列表
        garageList:[],
        //存储请求到的用户数据总数
        total:0,
        //控制添加用户的对话框的显示与隐藏
        addDialogVisible:false,
        modifyDialogVisible:false,
        confirmDialogVisible:false,
        passwordDialogVisible:false,
        Index:'',
        pageshow:true,
        newState:'',
        //添加用户的表单数据
        addForm:{
          username: '',
          email:'',
          mobile:'',
          charge:'',
          password:''
        },
        modifyForm:{
          username:'',
          email:'',
          mobile:'',
          charge:'',
        },
        confirmForm:{
          username:'',
          password:''
        },
        passwordForm:{
          username:'',
          opwd:'',
          cpwd:''
        },
        //添加表单的验证规则对象
        addFormRules:{
          username:[
            {required:true,message:"请输入用户名",trigger:"blur"},
            {min:2,max:7,message:"长度在2到7个字符",trigger:"blur"}
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
        passwordFormRules:{
          cpwd:[
            {required:true,message:"请输入密码",trigger:"blur"},
            {min:6,max:15,message:"长度在6到15个字符",trigger:"blur"}
          ],
        },
        charges:'',
        keeperState:[]

      }
    },
    methods: {
      //获取用户列表数据
       getUserList(){
         this.$http({
           url:'user/list',
           method:'get',
         }).then(res=>{
           if(res.data.code==200){
             this.userList=res.data.data
             for (let i=0;i<this.userList.length;i++){
               this.total=this.total+1
             }
             //初始化分页
             this.handleSizeChange(this.pagesize);
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
            this.keeperState=[]
            for(let i=0;i<this.garageList.length;i++){
              if(this.garageList[i].keeper==''||this.garageList[i].keeper==null){
                this.keeperState.push(false)
              }
              else{
                this.keeperState.push(true)
              }
            }
          }
          else {
            this.$message.error("请求车库列表数据失败，请刷新重新请求")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库列表数据失败，请检查网络")
        })
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
            this.interimUserList[j]=new Array()//这句话必须有，否则报错
            for (n;n<(this.total-j*newSize);n++){
              this.interimUserList[j].push(this.userList[j*newSize+n])
            }
          }
          else{
            this.interimUserList[j]=new Array()//这句话必须有，否则报错
            for (n;n<newSize;n++){
              this.interimUserList[j].push(this.userList[j*newSize+n])
            }
          }
          n=0
        }
      },
      //监听页面值改变的事件,实现页面切换
      handleCurrentChange(newCurrent){
        this.pagenum=newCurrent
      },
      //监听添加用户对话框的关闭事件
      addDialogClosed(){
        //重置表单内容
        this.$refs.addFormRef.resetFields()
      },
      modifyDialogClosed(){
        this.$refs.modifyFormRef.resetFields()
      },
      confirmDialogClosed(){
        this.$refs.confirmFormRef.resetFields()
      },
      passwordDialogClosed(){
        this.$refs.passwordFormRef.resetFields()
      },
      //确认添加用户
      addUser(){
        //对表单进行预校验，校验成功validate函数传入参数valid=true
        this.$refs.addFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'user/register',
              method:'post',
              data:this.addForm
            }).then(res=>{
              if(res.data.code==200){
                this.interimUserList.splice(0,Math.ceil(this.total/this.pagesize))
                this.total=0
                this.getUserList()
                this.pagenum=1
                this.pageshow = false;//让分页隐藏
                this.$nextTick(() => {//重新渲染分页
                  this.pageshow = true;
                })
                if(this.addForm.charge!=''&&this.addForm.charge!=null){
                  this.$http({
                    url:'garage/save',
                    method:'post',
                    data:{garageName:this.addForm.charge,keeper:this.addForm.username}
                  }).then(res=>{
                    this.getGarageList()
                  },err=>{

                  })
                }
                this.$message.success('添加用户成功!')
                this.addDialogVisible=false
              }
              else{
                this.$message.error("添加用户失败")
              }
            },err=>{
              this.$message.error("添加用户失败,用户名已存在");
              console.log(err)
            })
          }
        })
      },
      //搜索用户
      search(){
         let state=1
         for(let i=0;i<this.total;i++){
           if(this.query===this.userList[i].username){
             this.handleSizeChange(1)
             this.pagenum=i+1
             this.pageshow = false;//让分页隐藏
             this.$nextTick(() => {//重新渲染分页
               this.pageshow = true;
             })
             this.$message.success('成功找到用户'+this.query+'!')
             state=0
           }
         }
         if (state){
           this.$message.info('未找到用户'+this.query+'!')
         }
      },
      //修改用户
      modifyUser(index,user){

         this.Index=index
         this.charges=this.userList[index].charge
         if(this.$store.state.loginUser==user||window.sessionStorage.getItem('userType')=='超级管理员'){
           this.modifyDialogVisible=true
           this.modifyForm=JSON.parse(JSON.stringify(this.userList[index]))
         }
         else {
           this.newState='修改用户'
           this.confirmForm.username=this.userList[index].username
           this.confirmDialogVisible=true
         }
      },
      //确认修改用户
      confirmModify(){
        this.$refs.modifyFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'user/save',
              method:'post',
              data:this.modifyForm
            }).then(res=>{
              if(res.data.code==200){
                this.interimUserList.splice(0,Math.ceil(this.total/this.pagesize))
                this.total=0
                this.getUserList()
                this.pagenum=1
                this.pageshow = false;//让分页隐藏
                this.$nextTick(() => {//重新渲染分页
                  this.pageshow = true;
                })
                if(this.charges!=''&&this.charges!=null){
                  this.$http({
                    url:'garage/save',
                    method:'post',
                    data:{garageName:this.charges,keeper:''}
                  }).then(res=>{
                    this.getGarageList()
                  },err=>{

                  })
                }
                if(this.modifyForm.charge!=''&&this.modifyForm.charge!=null){
                  this.$http({
                    url:'garage/save',
                    method:'post',
                    data:{garageName:this.modifyForm.charge,keeper:this.modifyForm.username}
                  }).then(res=>{
                    this.getGarageList()
                  },err=>{

                  })
                }
                if(window.sessionStorage.getItem("loginUser")==this.modifyForm.username){
                  window.sessionStorage.setItem("charge",this.modifyForm.charge)
                }
                this.$message.success('修改用户成功!')
                this.modifyDialogVisible=false
              }
              else {
                this.$message.error("修改用户失败!")
              }
            },err=>{
              return this.$message.error("修改用户失败，请检查网络")
              console.log(err)
            })
          }
        })
      },
      //删除用户
      deleteUser(index,user){
        this.Index=index
        this.$confirm('此操作将删除该用户，是否继续', '提示',{
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          if(this.$store.state.loginUser==user||window.sessionStorage.getItem('userType')=='超级管理员'){
            this.$http({
              url:'user/delusr/'+this.userList[index].id,
              method:'get',
            }).then(res=>{
              if(res.data.code==200){
                this.interimUserList.splice(0,Math.ceil(this.total/this.pagesize))
                this.total=0
                this.getUserList()
                this.pagenum=1
                this.pageshow = false;//让分页隐藏
                this.$nextTick(() => {//重新渲染分页
                  this.pageshow = true;
                })
                if(this.userList[index].charge!=''&&this.userList[index].charge!=null){
                  this.$http({
                    url:'garage/save',
                    method:'post',
                    data:{garageName:this.userList[index].charge,keeper:''}
                  }).then(res=>{
                    this.getGarageList()
                  },err=>{

                  })
                }
                this.$message.success('删除用户成功!')
                if(this.$store.state.loginUser==user){
                  this.$router.push('/login')
                }
              }
              else {
                this.$message.error('删除用户失败!')
              }
            },err=>{
              console.log(err)
              return this.$message.error('删除用户失败，请检查网络')
            })
          }
          else {
            this.newState='删除用户'
            this.confirmForm.username=this.userList[index].username
            this.confirmDialogVisible=true
          }
        }).catch(()=>{
          this.$message.info('已取消删除用户')
        })
      },
      //权限验证
      confirmRight(){
        this.$refs.confirmFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'login',
              method:'post',
              data:this.confirmForm
            }).then(res=>{
              if(res.data.code==200){
                this.confirmDialogVisible=false
                if(this.newState=='修改用户'){
                  this.modifyForm=JSON.parse(JSON.stringify(this.userList[this.Index]))
                  this.$message.success('验证成功,请开始修改')
                  this.modifyDialogVisible=true
                }
                else if(this.newState=='删除用户'){
                  this.$http({
                    url:'user/delusr/'+this.userList[this.Index].id,
                    method:'get',
                  }).then(res=>{
                    if(res.data.code==200){
                      this.interimUserList.splice(0,Math.ceil(this.total/this.pagesize))
                      this.total=0
                      this.getUserList()
                      this.pagenum=1
                      this.pageshow = false;//让分页隐藏
                      this.$nextTick(() => {//重新渲染分页
                        this.pageshow = true;
                      })
                      if(this.userList[this.index].charge!=''&&this.userList[this.index].charge!=null){
                        this.$http({
                          url:'garage/save',
                          method:'post',
                          data:{garageName:this.userList[this.index].charge,keeper:''}
                        }).then(res=>{
                          this.getGarageList()
                        },err=>{

                        })
                      }
                      this.$message.success('验证成功,删除用户成功!')
                    }
                    else {
                      this.$message.error('验证成功,删除用户失败!')
                    }
                  },err=>{
                    console.log(err)
                    return this.$message.error('验证成功,删除用户失败，请检查网络')
                  })
                }
              }
              else {
                this.$message.error(res.data.msg)
              }
            },err=>{
              this.$message.error('验证失败，请检查网络')
              console.log(err)
            })
          }
        })
      },
      //修改密码
      modifyPassword(index){
        this.Index=index
        this.passwordDialogVisible=true
        this.passwordForm.username=this.userList[index].username
      },
      //确认修改密码
      confirmPassword(){
        this.$refs.passwordFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'user/cpwd',
              method:'post',
              data:this.passwordForm
            }).then(res=>{
              if(res.data.code==200){
                this.passwordDialogVisible=false
                this.$message.success('修改密码成功!')
              }
              else{
                this.$message.error('原密码错误!')
              }
            },err=>{
              this.$message.error('修改密码失败!')
              this.passwordDialogVisible=false
              console.log(err)
            })
          }
        })
      },
      test(a){
         return a+" "
      }
    },

  }
</script>

<style scoped>
  .el-table{
    margin-top: 15px;
  }
  .el-row{
    margin-top: 10px;
  }
  .el-card{
    padding: 10px;
    margin-top:30px;
    margin-left: 15px;
    margin-right: 15px;
  }
  .span1{
    text-align: center;
    font-size: 20px;
    color: darkslateblue;
    text-shadow: 2px 2px 2px cadetblue;
  }
</style>

