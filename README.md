# CubeWare

### 简介

**CubeWare是一款使用[时信魔方](https://www.shixincube.com/home)SDK3.0（以下简称魔方引擎）创建的IM应用，旨在通过其演示如何实现引擎强大的IM能力,您可以在CubeWare中找到引擎接口合适的调用方式及IM的简单实现,您也可以通过CubeWare进行二次扩展来快速构建您的IM应用**

CubeWare展示的功能主要包括：用户登录，即时消息（文字，语音，图片，视频），群组，实时音视频会议（多人/单人），白板演示，共享屏幕等。

### 接入指南

1.请前往[时信魔方后台](https://www.shixincube.com/home)获取AppKey和AppID

2.替换`app\src\main\java\cube\ware\AppConstants.java`中的AppKey和APPID，实现登录

### CubeWare页面介绍


- 最近会话列表：展示最近的聊天会话，点击列表可进入详细聊天界面

- 会议界面：发起视频会议，可指定会议开始时以及时长

- 扩展功能入口：多人语音，多人白板

- 联系人界面：联系人以及群组列表展示

- "我的"：个人资料管理，昵称，头像修改等

![页面](https://i.imgur.com/qhBwZ0S.png)

### jar包的使用及介绍

	魔方引擎核心框架 cube-engine-*.jar 基础框架，集成必备。
	魔方引擎网络框架 cube-genie-*.jar 基础框架，集成必备。
	魔方引擎音视频框架 cube-rtc-*.jar libcube_rtc.so 音视频通信模块，集成（可选）。
	魔方引擎信令框架 cube-sip-*.jar 群组音视频聊天模块，集成（可选）

### 模块简述

CubeWare中将魔方引擎的功能分为如下几大模块：

	 用户服务 UserService：提供用户登录，登出，资料信息更新
	 消息服务 MessageService：发送文字，语音，图片，短视频，文件等消息，并可对消息撤回，删除等 
	 文件服务 FileManagerService ：文件目录管理可以创建、修改、删除、移动、拷贝和查询文件； 文件上传下载 支持断点上传和下载文件。
	 群组服务 GroupService：提供创建，删除，解散，退出群组，管理群成员，群资料等功能
	 音视频服务 CallService：实时语音通信，视频通信，默认采用P2P通信
	 会议服务 ConferenceService：支持多人视频，音频
	 白板服务 WhiteboardService ：实现本地涂鸦，在线共享白板
	 屏幕共享服务 ShareDesktopService：可以实时共享桌面内容给其他用户

### CubeWare结构

![结构](https://i.imgur.com/9i359Qi.png)

CubeWare采用MVP框架，通过阿里巴巴开源路由ARouter实现页面交互，更加轻量，易维护。

app包存放CubeWare逻辑代码以及界面布局，资源文件等

其中App包中的service包用于存放各模块服务相关代码，监听回调，状态管理等

![service](https://i.imgur.com/Kr159Jz.png)

ui包用于存放各个界面相关代码，各功能界面实现及入口搭建

![ui](https://i.imgur.com/6Hrdw3e.png)

CommonMvp存放MVp相关类；

CommonSdk存放ARouter相关类；

CommomUtils存放一系列公共工具类，如日志工具类LogUtil，线程调度工具类ThreadUtil，gilde相关等。

### CubeWare核心功能

### 一、即时消息

从联系人界面中选择联系人或者群组发起会话，可发送文字，语音，图片，视频，文件等消息。

对发送的消息长按可执行撤回，删除操作。

用户重新登录可拉取离线消息，展示最近会话列表。

![最近会话列表](https://i.imgur.com/l7BG4df.png) ![联系人会话](https://i.imgur.com/aKmWV7e.png) ![群会话](https://i.imgur.com/dDNkAVu.png)


### 二、音视频通话
  
可选择联系人发起语音通话，视频通话

视频通话过程中可切换为语音通话

![邀请语音通话](https://i.imgur.com/n3FPdMj.png) ![发起通话](https://i.imgur.com/wjDJUJ3.png) ![通话中](https://i.imgur.com/ocF3RJw.png) ![通话结束](https://i.imgur.com/L5VZ6Qd.png) ![邀请视频通话](https://i.imgur.com/BCrkD93.png)
![视频通话中](https://i.imgur.com/BFNQaoL.png)

### 三、会议

可实现多人音视频会议。

多人音视频会议包括多人语音、多人视频,针对于群组或者多个联系人发起。

长按会议列表中的会议，可销毁此会议

CubeWare中提供了三种发起方式：

- **1.在会议界面发起多人视频会议，可指定会议开始时间，会议时长，选择参会人员等。**

流程简述：发起者选择输入会议名称 ，选择参会人员，选择会议开始时间和会议时长，当到规定开始时间时，会主动邀请参会人员， 发起者自主加入会议

![会议](https://i.imgur.com/gC2FVGY.png) ![创建会议](https://i.imgur.com/LKIiZmi.png) ![创建完成](https://i.imgur.com/dftS97h.png) ![创建者加入会议](https://i.imgur.com/di8Ul79.png) ![视频会议中](https://i.imgur.com/cs2StkJ.png)

- **2.在群组聊天界面发起该群中的多人音频、视频会议。**

![群语音](https://i.imgur.com/z6tLRpU.png) ![选择联系人](https://i.imgur.com/PksYKSU.png) ![通话中](https://i.imgur.com/DXNBs8p.png) ![群通话结束](https://i.imgur.com/OiEHYLa.png)

- **3.在扩展功能界面可选择多个联系人，发起语音通话。**

![扩展](https://i.imgur.com/Q3BtYnq.png) ![选择联系人](https://i.imgur.com/tRbSKch.png) ![通话中](https://i.imgur.com/lATFyw0.png)


### 四、白板演示

形同于生活中的白板演示，可绘制，可擦除，可拖动。

CubeWare中白板演示可针对P2P或多人发起在线白板，发起方式同音视频会议。

![邀请白板](https://i.imgur.com/W1FCaI3.png) ![开始白板](https://i.imgur.com/20XrXC9.png)


### 五、共享屏幕

移动客户端没有发起屏幕分享入口。屏幕分享由PC端发起，移动端接受邀请，获取远端屏幕画面数据在本地View显示。

![共享屏幕邀请](https://i.imgur.com/KtBUdi1.png) ![共享屏幕中](https://i.imgur.com/gGn1f1V.png)

体验此功能前往[此处]()，下载PC端CubeWare。

### 开源协议

CubeWare使用[MIT](https://opensource.org/licenses/MIT)开源协议

### 更多

关于时信魔方SDK3.0更多的详细使用方法可查看[时信魔方后台开发者文档](https://www.shixincube.com/home#download)。

感谢您的使用！





