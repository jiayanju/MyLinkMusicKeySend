# MyLinkMusicKeySend
用于监听方向盘的多媒体按键并控制第三方音乐播放器的上一首，下一首。适用于雪佛兰迈锐宝2017，2018车机。

软件需要打包为系统应用。
1. 生成签名应用app-release.apk.
2. 使用通用签名来重新给apk文件签名。
准备好platform.pk8、platform.x509.pem和签名工具signapk.jar，还有自己的apk，放在同一个文件夹下
3. 执行“java -jar signapk.jar platform.x509.pem platform.pk8 app-release.apk keyplay_V2.apk”做平台签名得到keyplay_V2.apk。 
