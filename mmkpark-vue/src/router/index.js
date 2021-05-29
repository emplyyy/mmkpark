import Vue from 'vue'
import VueRouter from 'vue-router'

import ElementUI, {MessageBox} from "element-ui";//导入MessageBox
import {request1} from "../network/request"
const Login=()=>import('../components/common/Login')
const Home=()=>import('../views/Home')
const Welcome=()=>import('../components/content/Welcome')
const User=()=>import('../components/content/User')
const Record=()=>import('../components/content/Record')
const Monitor=()=>import('../components/content/Monitor')
const Inquire=()=>import('../components/content/Inquire')
const Add=()=>import('../components/content/Add')
const List=()=>import('../components/content/List')
const Error=()=>import('../components/content/Error')
Vue.use(VueRouter)
const routes = [
  {
    path:'',
    redirect:'/login'
  },
  {
    path:'*',
    redirect: '/error'
  },
  {
    path:'/login',
    component:Login,
    meta:{
      title:'登录'
    }
  },
  {
    path:'/home',
    component:Home,
    redirect: '/welcome',
    children:[
      {path:'/welcome',component:Welcome,meta:{
          title:'首页'
        }},
      {path:'/user',component:User,meta:{
          title:'用户列表'
        }},
      {path: '/record', component:Record,meta:{
          title:'车位列表'
        }},
      {path: '/monitor',component:Monitor,meta:{
          title:'实时监控'
        }},
      {path: '/inquire',component:Inquire,meta:{
          title:'车位查询'
        }},
      {path: '/add',component:Add,meta:{
          title:'车库添加'
        }},
      {path: '/list',component:List,meta:{
          title:'车库列表'
        }}
      ]
  },
  {
    path: '/error',
    component:Error,
    meta:{
      title:'error'
    }
  }

]

const router = new VueRouter({
  mode:'history',
  routes
})

//路由导航守卫，确保访问登录后的界面时有服务器授权的token，没有则强制跳转到登录界面
router.beforeEach((to,from,next)=>{
  if(from.path=='/record'){
    if(window.sessionStorage.getItem("isRecord")=='true'){//离开车位删录界面时如果有修改未提交，阻止离开
      next('/record')
      MessageBox.confirm('修改的车位信息未提交，是否提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(()=>{
        request1({
          url:'parkcoor/emp/'+window.sessionStorage.getItem("currentGarage"),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            request1({
              url:'parkcoor/save',
              method:'post',
              data:JSON.parse(window.sessionStorage.getItem("updateParkingList")),
            }).then(res1=>{
              if(res1.data.code==200){
                ElementUI.Message({
                  type: 'success',
                  message: '提交修改成功!'
                });
                window.sessionStorage.setItem("isRecord",'false')
                next(to.path)//允许跳转
              }
              else{
                ElementUI.Message({
                  type: 'error',
                  message: '提交修改失败!'
                });
              }
            },err1=>{
              console.log(err1)
              ElementUI.Message({
                type: 'error',
                message: '提交修改失败!'
              });
            })
          }
          else {
            ElementUI.Message({
              type: 'error',
              message: '清空车位列表数据失败!'
            });
          }
        },err=>{
          console.log(err)
          ElementUI.Message({
            type: 'error',
            message: '清空车位列表失败!'
          });
        })
      }).catch(()=>{
        window.sessionStorage.setItem("isRecord",'false')
        ElementUI.Message({
          type: 'info',
          message: '已取消提交修改'
        });
        next(to.path)//允许跳转
      })
    }
  };
  if(to.path=='/login'){
    return next()
  }
  else{
    const tokenStr = window.sessionStorage.getItem('token')
    if(!tokenStr){return next('/login')}
    else{next()}
  }

})

router.beforeEach((to,from,next)=>{
  document.title=to.meta.title
  next()
})

export default router
