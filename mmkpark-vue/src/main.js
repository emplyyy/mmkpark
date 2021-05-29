import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/index'
import 'element-ui/lib/theme-chalk/index.css' //导入element样式
import ElementUI from 'element-ui'
import './assets/css/base.css'//导入自定义基础样式
import {MessageBox} from "element-ui";//导入MessageBox
import {request1} from "./network/request"
Vue.use(ElementUI)
Vue.config.productionTip = false
Vue.prototype.$http=request1
Vue.prototype.$message=ElementUI.Message
Vue.prototype.$confirm=MessageBox.confirm//挂载MessageBox

import VueVideoPlayer from 'vue-video-player'
import 'video.js/dist/video-js.css'
require('video.js/dist/video-js.css')
require('vue-video-player/src/custom-theme.css')
Vue.use(VueVideoPlayer)


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

