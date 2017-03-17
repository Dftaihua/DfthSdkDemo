# DfthSdkDemo: 东方泰华SDK集成使用工程

这个工程是用来集成东方泰华设备的操作，数据的存储和后续的服务。

这个工程包含一个库文件和示例代码

# lib
app/libs/dfth_sdk.aar

#示例使用
在DfthSdkApplication中添加自己的APPID，APPSecret.(申请方法详见<a href='http://open.dfthlong.com/openplat'></a>)

#lib集成
1.将app/libs/dfth_sdk.aar拷贝到工程libs/dfth_sdk.aar中，并且在AndroidManifest.xml添加权限
```xml
    <!-- 蓝牙权限 -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <!--往sdcard中写入数据的权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <!--在sdcard中创建/删除文件的权限 -->
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
    <!--网络权限-->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```
2.将根目录下build.gradle添加
```groovy
allprojects {
    repositories {
    jcenter()
    flatDir{
    dirs 'libs'
    }
  }
}
```
3.在app/build.gradle添加依赖
```groovy
    compile(name:'dfth_sdk', ext:'aar')
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    compile 'io.reactivex.rxjava2:rxjava:2.0.1'
    compile 'com.squareup.retrofit2:retrofit:2.1.0'
    compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    compile 'com.squareup.retrofit2:converter-scalars:2.1.0'
    compile 'com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0'
    compile 'com.squareup.okhttp3:logging-interceptor:3.1.2'
    compile 'com.squareup.okhttp3:okhttp:3.1.2'
    compile 'org.apache.ant:ant:1.9.7'
    compile 'com.lovedise:permissiongen:0.0.6'
```
4.在Application中调用
```java
    DfthSDKConfig config = DfthSDKConfig.getConfig(getApplicationContext(),Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyBluetooth", "MyBluetooth", LogLevel.FULL, Logger.ERROR, "MyBluetooth", 1,"http://apitest.open.dfthlong.com/");
    config.setClientId("4e8b63624a4f490c8aa671b5c82f95f3");
    config.setClientSecret("b20f7cc80c6641e0b0e854c001c8f890");
    DfthSDKManager.initWithConfig(config);
```
5.在使用SDK前调用
```java
    DfthSDKManager.getManager().onInit(this);
    DfthSDKManager.getManager().oauth(new DfthSdkCallBack() {
        @Override
        public void onInitResponse(boolean success, String accessToken) {
        Log.e("dfth_sdk", "oauth->" + success);
          //验证成功后，可以连接设备和查询数据
          //可以创建用户
        }
    });
```
6.设备管理获取
```java
   DfthDeviceFactory factory =  DfthSDKManager.getManager().getDeviceFactory();
```
7.网络接口
```java
   DfthService service = DfthSDKManager.getManager().getDfthService();
```
7.数据查询
```java
   DfthLocalDatabase database = DfthSDKManager.getManager().getDatabase();
```

#联系我们

    公司网址:<a href='http://www.dftaihua.com'></a>
    公司邮箱:dfth@dftaihua.com
    公司电话:010-67857716


