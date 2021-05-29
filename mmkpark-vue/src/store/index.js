import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    isRecord:false,
    loginUser:'',//记录当前登录用户
    currentGarage:'',//记录当前选择的车库
    //用来实时监听绑定record组件中parkingList
    newParkingList:[],

  },
  mutations: {
    updateLoginUser(state,newLoginUser){
      state.loginUser=newLoginUser
    },
  },
  actions: {
  },
  modules: {
  }
})
