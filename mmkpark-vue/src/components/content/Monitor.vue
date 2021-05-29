<template>
    <div class="div1">
      <videoPlayer class="vjs-custom-skin videoPlayer" :options="playerOptions"></videoPlayer>
      <el-row :gutter="20">
        <el-col :span="10">
          <el-input placeholder="请输入要添加或修改监控视频地址" v-model="address" clearable style="width: 1025px">
            <el-button slot="append" icon="el-icon-upload2" @click="sendAddress"></el-button>
          </el-input>
        </el-col>
      </el-row>
    </div>
</template>

<script>
  import 'video.js/dist/video-js.css'
  import {videoPlayer} from 'vue-video-player'
  import 'videojs-flash'

  export default {
    created() {
      this.getGarage()
    },
    name: "Monitor",
    props:{
      sug:'',
    },
    components: {
      videoPlayer
    },
    data(){
      return{
        playerOptions: {
          height: '576',
          sources: [{
            type: 'rtmp/mp4',
            src: ''
          }],
          techOrder: ['flash'],
          autoplay: true,
          controls: true,
          poster: '',
        },
        address:''

      }
    },
    watch:{
      sug(newValue){
        this.address=''
        this.getGarage()
      }
    },
    methods:{
      getGarage(){
        this.$http({
          url:'garage/'+window.sessionStorage.getItem('currentGarage'),
          method:'get',
        }).then(res=>{
          if(res.data.code==200){
            if(res.data.data.garageVideo==null){
              this.$message.error("该车库还未录入监控视频地址，请在视频下方录入")
            }
            else {
              this.address=res.data.data.garageVideo
              this.playerOptions.sources[0].src=res.data.data.garageVideo
            }
          }
          else {
            this.$message.error("请求车库监控数据失败，请刷新重新请求")
          }
        },err=>{
          console.log(err)
          return this.$message.error("请求车库监控数据失败，请刷新检查网络")
        })
      },
      sendAddress(){
        this.$http({
          url:'garage/save',
          method:'post',
          data:{garageName:this.sug,garageVideo:this.address}
        }).then(res=>{
          if(res.data.code==200){
            this.$message.success('监控地址录入成功!')
            this.getGarage()
          }
          else{

          }
        },err=>{})
      }
    }

  }
</script>

<style scoped>
  .div1{
    width: 1024px;
    position: absolute;
    left: 50%;
    right: 50%;
    transform: translate(-50%,5%);
  }
</style>
