 <template>
    <div>
      <!--面包屑导航区域-->
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>车库管理</el-breadcrumb-item>
        <el-breadcrumb-item>车库列表</el-breadcrumb-item>
      </el-breadcrumb>
      <!--卡片区域-->
      <el-card>
        <span class="span1">车库查询表</span>
        <div>
          <el-select v-model="query" placeholder="请输入车库名进行搜索" filterable clearable style="width: 400px;margin-top: 10px">
            <el-option
              v-for="item in garageList"
              :label="item.garageName"
              :value="item.garageName">
            </el-option>
          </el-select>
          <el-button icon="el-icon-search" @click="search"></el-button>
        </div>
        <!--列表区域-->
        <!--自动遍历-->
        <el-table :data="interimGarageList[pagenum-1]" border stripe>
          <el-table-column type="index" :index="myIndex"></el-table-column>
          <el-table-column label="车库ID" prop="garageId"></el-table-column>
          <el-table-column label="车库名" prop="garageName" width="250px"></el-table-column>
          <el-table-column label="车库图片" prop="garagePic" width="90px">
            <template slot-scope="scope">
              <img :src="'http://127.0.0.1:8088/pic/'+scope.row.garagePic" class="img1">
            </template>
          </el-table-column>
          <el-table-column label="车库种类" prop="type" width="80px"></el-table-column>
          <el-table-column label="车位总数" prop="parkTotal" width="80px"></el-table-column>
          <el-table-column label="空车位数" prop="parkRemain" width="80px"></el-table-column>
          <el-table-column label="车库电话" prop="garageInfo" width="115px"></el-table-column>
          <el-table-column label="车库地址" prop="address" width="270px"></el-table-column>
          <el-table-column label="管理员" prop="keeper"></el-table-column>
          <el-table-column label="操作" width="118px">
            <template slot-scope="scope">
              <el-tooltip  effect="light" content="修改车库" placement="top">
                <el-button type="primary" icon="el-icon-edit" size="mini" @click="modifyGarage(scope.$index+(pagenum-1)*pagesize)"></el-button>
              </el-tooltip>
              <el-tooltip  effect="dark" content="删除车库" placement="top">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteGarage(scope.$index+(pagenum-1)*pagesize)"></el-button>
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
      <!--修改对话框-->
      <el-dialog title="修改车库信息" :visible.sync="modifyDialogVisible" @close="modifyDialogClosed" top="20px">
        <!--内容主题区域-->
        <el-form :model="modifyForm" ref="modifyFormRef" label-width="90px" :rules="modifyFormRules">
          <el-form-item label="车库名" prop="garageName">
            <el-input v-model="modifyForm.garageName" disabled></el-input>
          </el-form-item>
          <el-form-item label="车库图片" prop="garagePic">
            <el-upload
              class="avatar-uploader"
              action="http://127.0.0.1:8088/upload"
              :show-file-list="true"
              :on-success="handleAvatarSuccess">
              <img v-if="modifyForm.garagePic" :src="'http://127.0.0.1:8088/pic/'+modifyForm.garagePic" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item label="车库种类" prop="type">
            <el-select v-model="modifyForm.type" placeholder="请选择车库类型" filterable>
              <el-option
                v-for="item in garageType"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="车库地址" prop="address">
            <el-input v-model="modifyForm.address"></el-input>
          </el-form-item>
          <el-form-item label="车库电话" prop="mobile">
            <el-input v-model="modifyForm.garageInfo"></el-input>
          </el-form-item>
          <el-form-item label="车库管理员" prop="keeper">
            <el-select v-model="modifyForm.keeper" placeholder="请选择车库管理员" filterable clearable>
              <el-option
                v-for="(item,index) in userList"
                :key="item.id"
                :label="item.username"
                :value="item.username"
                :disabled="chargeState[index]">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <!--底部区域-->
        <span slot="footer">
          <el-button @click="modifyDialogVisible=false,$message.info('已取消修改车库信息')">取消</el-button>
          <el-button type="primary" @click="modifyConfirm">确定</el-button>
        </span>
      </el-dialog>

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
    name: "List",
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

      return{
        //当前页数
        pagenum:1,
        //当前每页显示多少条数据
        pagesize:5,
        //存储车位总数
        total:0,
        garageList:[],
        interimGarageList:[],
        userList:[],
        query:'',
        modifyDialogVisible:false,
        modifyForm:{
          garageId:'',
          garageName:'',
          garagePic:'',
          type:'',
          garageInfo:'',
          address:'',
          keeper:'',
        },
        Index:'',
        pageshow:true,
        garageType:['地下','地上'],
        //添加表单的验证规则对象
        modifyFormRules:{
          garageInfo:[
            {required:true,message:"请输入手机号",trigger:"blur"},
            {validator:checkMobile,trigger: 'blur'}
          ]
        },
        keepers:'',
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
          return this.$message.error("请求用户列表数据失败，请刷新重新请求")
        })
      },
      getGarageList(){
        this.$http({
          url:'garage/list',
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            this.garageList=res.data.data
            for (let i=0;i<this.garageList.length;i++){
              this.total=this.total+1
            }
            //初始化分页
            this.handleSizeChange(this.pagesize);
          }
          else{
            this.$message.error("请求车库列表数据失败，请刷新重新请求")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库列表数据失败，请刷新重新请求")
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
            this.interimGarageList[j]=new Array()//这句话必须有，否则报错
            for (n;n<(this.total-j*newSize);n++){
              this.interimGarageList[j].push(this.garageList[j*newSize+n])
            }
          }
          else{
            this.interimGarageList[j]=new Array()//这句话必须有，否则报错
            for (n;n<newSize;n++){
              this.interimGarageList[j].push(this.garageList[j*newSize+n])
            }
          }
          n=0
        }
      },
      //监听页面值改变的事件,实现页面切换
      handleCurrentChange(newCurrent){
        this.pagenum=newCurrent
      },
      //修改车库
      modifyGarage(index){
        this.Index=index
        this.keepers=this.garageList[index].keeper
        this.modifyDialogVisible=true
        this.modifyForm=JSON.parse(JSON.stringify(this.garageList[index]))
      },
      //删除车库
      deleteGarage(index){
        this.$confirm('此操作将删除该车库，是否继续', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          this.$http({
            url:'garage/delgar/'+this.garageList[index].garageId,
            method:'get',
          }).then(res=>{
            if (res.data.code==200){
              this.interimGarageList.splice(0,Math.ceil(this.total/this.pagesize))
              this.total=0
              this.getGarageList()
              this.pagenum=1
              this.pageshow = false;//让分页隐藏
              this.$nextTick(() => {//重新渲染分页
                this.pageshow = true;
              })
              if(this.garageList[index].keeper!=''&&this.garageList[index].keeper!=null){
                this.$http({
                  url:'user/save',
                  method:'post',
                  data:{username:this.garageList[index].keeper,charge:''}
                }).then(res=>{
                  this.getUserList()
                  if(this.sessionStorage.getItem('loginUser')==this.garageList[index].keeper){
                    this.sessionStorage.setItem('charge','')
                  }
                },err=>{

                })
              }
              this.$message.success('删除车库成功!')
              this.$emit('item-click');
            }
            else{

            }
          },err=>{
            console.log(err)
            return this.$message.error('删除车库失败!')
          })
        }).catch(()=>{
          this.$message.info('已取消删除车库')
        })
      },
      //搜索车库
      search(){
        let state=1
        for(let i=0;i<this.total;i++){
          if(this.query===this.garageList[i].garageName){
            this.handleSizeChange(1)
            this.pagenum=i+1
            this.pageshow = false;//让分页隐藏
            this.$nextTick(() => {//重新渲染分页
              this.pageshow = true;
            });
            this.$message.success('成功找到车库'+this.query+'!')
            state=0
          }
        }
        if (state){
          this.$message.info('未找到车库'+this.query+'!')
        }
      },
      modifyDialogClosed(){
        //重置表单内容
        this.$refs.modifyFormRef.resetFields()
      },
      modifyConfirm(){
        this.$refs.modifyFormRef.validate(valid=>{
          if(!valid){return}
          else{
            this.$http({
              url:'garage/save',
              method:'post',
              data:this.modifyForm
            }).then(res=>{
              if (res.data.code==200){
                this.interimGarageList.splice(0,Math.ceil(this.total/this.pagesize))
                this.total=0
                this.getGarageList()
                this.pagenum=1
                this.pageshow = false;//让分页隐藏
                this.$nextTick(() => {//重新渲染分页
                  this.pageshow = true;
                })
                if(this.keepers!=''&&this.keepers!=null){
                  this.$http({
                    url:'user/save',
                    method:'post',
                    data:{username:this.keepers,charge:''}
                  }).then(res=>{
                    this.getUserList()
                  },err=>{

                  })
                }
                if(this.modifyForm.keeper!=''&&this.modifyForm.keeper!=null){
                  this.$http({
                    url:'user/save',
                    method:'post',
                    data:{username:this.modifyForm.keeper,charge:this.modifyForm.garageName}
                  }).then(res=>{
                    this.getUserList()
                    if(this.sessionStorage.getItem('loginUser')==this.modifyForm.keeper){
                      this.sessionStorage.setItem('charge',this.modifyForm.garageName)
                    }
                  },err=>{

                  })
                }
                this.$message.success('修改车库信息成功!')
                this.$emit('item-click');
                this.modifyDialogVisible=false
              }
              else{
                this.$message.error('修改车库信息失败!')
              }
            },err=>{
              console.log(err)
              this.$message.error('修改车库信息失败!')
            })
          }
        })
      },
      handleAvatarSuccess(res, file) {
        this.modifyForm.garagePic = res.data;
        console.log(res,file)
      },
      reload(){

      },
    },

  }
</script>

<style scoped>
  .span1{
    text-align: center;
    font-size: 20px;
    color: darkslateblue;
    text-shadow: 2px 2px 2px cadetblue;
  }
  .el-card{
    padding: 10px;
    margin-top:30px;
    margin-left: 15px;
    margin-right: 15px;
  }
  .el-table{
    margin-top: 15px;
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
  .img1{
    height: 40px;
    width: 68px;
  }

</style>
