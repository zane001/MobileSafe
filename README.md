MobileSafe
==========

手机安全卫士
---------------
### 2014-08-29
1：使用ActivityManager获取正在运行的程序信息（PID,占用内存大小等）。<br/>
2：使用PackageManager获取对应的进程的应用程序信息。<br/>
3：自定义Toast显示的位置、图片。<br/>
4: 遍历集合时不能进行移除操作，重新定义一个List<ProcessInfo> killedProcessInfo来存放被Kill的进程。
5: 使用PackageManager获取具有权限信息的应用PackageManager.GET_PERMISSIONS，访问互联网的权限android.permission.INTERNET。<br/>
6：使用SlidingDrawer实现上拉抽屉效果。
7：使用TrafficStats.getUidRxBytes(uid)，根据uid来获取应用的上传、下载流量。
### 2014-08-27
1：使用PackageManager获取手机中的系统程序和用户程序。<br/>
2：通过StatFs和Formatter来获取手机内存、SDCard的内存。<br/>
3：使用PopupWindow时需要设置大小和背景资源。<br/>
4：通过系统的Intent来实现软件的启动、分享、卸载功能。
### 2014-08-23
1：对数据库的增删改查操作，实现黑名单的添加、修改、删除功能。<br/>
2：借助广播实现对黑名单短信的拦截，TelephonyManager监听电话的状态，复用ConvertView来优化ListView。<br/>
3：调用adapter.notifyDataSetChange()来实现上下文菜单列表中的数据及时更新。<br/>
4：通过AIDL方式获取系统的电话管理服务，来实现黑名单自动挂断电话的功能。<br/>
5：通过注册一个内容观察者getContentResolver().registerContentObserver()，观察一个URI数据的改变，及时更新数据库中的数据。
### 2014-07-29
1：采用Service（watchDog）实时获取当前手机中位于栈顶的Activity所对应的包名。<br/>
2：判断该包名是否需要被锁定，如果是，则弹出密码输入框，用户输入正确的密码，临时停止该应用程序的锁定。<br/>
3：在设置中心添加是否开启程序锁功能，来决定watchDog是否开启服务。<br/>
4：为了保证数据的同步，自定义一个ContentProvider，来实时监听数据的变化。<br/>
5：使用BroadcastReceiver来实现锁屏，清空被停止保护的应用程序，保护所有的应用程序，用代码注册BroadcastReceiver。
### 2014-07-20
1：使用ExpandableListView实现常用号码的查询，复用缓存，尽可能减少对数据库的频繁操作。
### 2014-07-19
1：自定义来电号码归属地的显示位置，处理单击和触摸事件，可以在屏幕的任何位置显示。
### 2014-07-17
1：显示来电的号码归属地；<br/>
2：可以在设置中心选择来电归属地的显示风格。
### 2014-07-09
1：手机号码归属地的查询，导入数据库到手机，使用正则表达式区分手机号和固定电话。
### 2014-07-06
1：获取数据库中的联系人数据；<br/>
2：手机更换SIM发送短信；<br/>
3：取得设备的超级管理权限，发送位置信息，播放音乐，清除数据，远程锁屏。
### 2014-07-01
增加手机防盗设置向导页面，SIM卡绑定，更换SIM卡报警。
### 2014-06-22
1：增加每次打开时自动联网判断是否更新版本；<br/>
2：增加设置中心开启（关闭）自动更新；<br/>
3：增加手机防盗模块设置密码，采用Md5方法加密。