#ImitateBilibili
仿制的BiliBili客户端,采用Material Design设计.  
实现了主界面的数据展示,运行时动态换肤以及视频的播放与控制,三个模块.  
使用RxJava执行异步操作,Volley网络请求,Picasso加载图片,以及EventBus作为事件总线.	

##Apk download
  [Click Raw button](https://github.com/ExcaliburZ/ImitateBilibili/blob/master/apk/zilizili-debug.apk)  
  [Download by Vps](http://182.254.156.215:8080/zilizili-debug.apk)
##Introduce
   - 主界面的数据展示:解决了多个ViewGroup之间的滑动冲突问题  
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/main.png" width="340">
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/video_detail.gif" width="340">
 
   - 运行时动态换肤:通过自定义常用控件,实现不重启Activity换肤  
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/change_theme.gif" width="340">

  
   - 视频的播放与控制:自定义播放时控制面板,实现了如屏幕锁,手势控制音量与亮度,手势调节进度条等功能.  
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/play_controller.png" width="500">
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/screen_lock.png" width="500">
   <img src="https://github.com/ExcaliburZ/ImitateBilibili/blob/master/screenshot/video_play.gif" width="500">
  


