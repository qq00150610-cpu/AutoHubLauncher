# AutoHub 智驾桌面

一款适配80%以上安卓车机的智能车机桌面系统,以"安全驾驶、高效交互、美观个性化"为核心理念。

## 项目信息

- **项目名称**: AutoHub 智驾桌面
- **版本**: 1.0.0
- **最低SDK**: Android 7.0 (API 24)
- **目标SDK**: Android 14 (API 34)
- **架构**: MVVM + Clean Architecture
- **UI框架**: Jetpack Compose
- **语言**: Kotlin

## 核心功能

### Phase 1: 核心版本 (v1.0)
- ✅ 基础桌面框架
- ✅ 底部导航栏(首页/导航/音乐/视频/设置)
- ✅ 快捷应用区
- ✅ 主题系统(支持Material Design 3)
- ✅ 智能卡片系统(天气卡片)
- ✅ 系统设置快捷入口

### Phase 2: 功能完善 (v1.5)
- ✅ 导航界面和功能(搜索、快捷入口、应用集成)
- ✅ 音乐界面和控制(播放器UI、控制功能)
- ✅ 视频界面(播放器、安全限制、画中画UI)
- ✅ 设置界面(通用设置、车辆设置、高级设置)
- ✅ 悬浮球功能(显示、拖拽、边缘吸附)
- ✅ 语音控制服务(语音识别、命令解析)
- ✅ 后台应用管理
- ✅ 数据持久化(DataStore配置存储)

### Phase 3: 个性化 (v2.0)
- ✅ 数据层架构(设置管理、文件管理、应用管理)
- ✅ 常用Widget组件(天气Widget、速度表Widget、迷你播放器Widget)
- ✅ 方控映射数据模型(按键映射配置文件)
- ✅ 车辆控制适配器接口(比亚迪/吉利/通用适配器)
- ✅ 应用管理界面(应用列表、卸载、信息查看)
- ✅ 文件管理界面(浏览、删除、重命名、存储信息)
- ✅ 工厂模式界面(密码验证、硬件测试、系统操作)
- ✅ 空调控制界面(温度控制、风速控制、出风模式)
- ✅ 方控映射界面(配置方案、按键编辑、测试模式)
- ✅ 用户注册登录系统(手机号登录、微信登录、用户中心)

### Phase 4: 车型深度适配 (v2.5)
- 🚧 本机设置完整适配
- 🚧 工厂模式入口
- 🚧 CAN总线数据读取
- 🚧 更多车型适配
- 🚧 车型适配SDK开放

## 项目结构

```
AutoHubLauncher/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/autohub/launcher/
│   │       │   ├── AutoHubApplication.kt          # Application类
│   │       │   ├── data/                           # 数据层
│   │       │   │   ├── api/                       # API接口
│   │       │   │   │   ├── AuthApi.kt            # 认证API
│   │       │   │   │   ├── NetworkModule.kt      # 网络模块配置
│   │       │   │   │   └── MockAuthApi.kt        # Mock API实现
│   │       │   │   ├── model/                     # 数据模型
│   │       │   │   │   └── User.kt               # 用户模型
│   │       │   │   └── repository/                # 仓库
│   │       │   │       ├── UserRepository.kt      # 用户仓库
│   │       │   │       ├── SettingsRepository.kt   # 设置仓库
│   │       │   │       ├── FileManager.kt         # 文件管理
│   │       │   │       └── AppManager.kt          # 应用管理
│   │       │   ├── di/                            # 依赖注入模块
│   │       │   │   └── AppModule.kt
│   │       │   ├── domain/                        # 领域层
│   │       │   │   ├── model/                     # 数据模型
│   │       │   │   └── usecase/                   # 用例
│   │       │   ├── service/                       # 后台服务
│   │       │   │   ├── FloatingBallService.kt     # 悬浮球服务
│   │       │   │   ├── MusicControlService.kt     # 音乐控制服务
│   │       │   │   ├── NavigationService.kt       # 导航服务
│   │       │   │   ├── CarControlService.kt       # 车辆控制服务
│   │       │   │   ├── VoiceControlService.kt    # 语音控制服务
│   │       │   │   └── WeChatLoginService.kt     # 微信登录服务
│   │       │   ├── receiver/                      # 广播接收器
│   │       │   │   └── BootReceiver.kt
│   │       │   └── ui/                            # UI层
│   │       │       ├── app/                       # 应用入口
│   │       │       │   ├── AppScreen.kt          # 应用主界面
│   │       │       │   └── AppViewModel.kt       # 应用ViewModel
│   │       │       ├── auth/                      # 认证模块
│   │       │       │   ├── LoginScreen.kt        # 登录界面
│   │       │       │   └── LoginViewModel.kt     # 登录ViewModel
│   │       │       ├── main/                      # 主界面
│   │       │       │   ├── MainActivity.kt
│   │       │       │   ├── MainViewModel.kt
│   │       │       │   └── MainScreen.kt
│   │       │       ├── navigation/                # 导航模块
│   │       │       │   ├── NavigationScreen.kt
│   │       │       │   └── NavigationViewModel.kt
│   │       │       ├── music/                     # 音乐模块
│   │       │       │   ├── MusicScreen.kt
│   │       │       │   └── MusicViewModel.kt
│   │       │       ├── video/                     # 视频模块
│   │       │       │   ├── VideoScreen.kt
│   │       │       │   └── VideoViewModel.kt
│   │       │       ├── settings/                  # 设置模块
│   │       │       │   ├── SettingsScreen.kt
│   │       │       │   └── SettingsViewModel.kt
│   │       │       ├── profile/                   # 个人中心
│   │       │       │   ├── ProfileScreen.kt      # 个人中心界面
│   │       │       │   └── ProfileViewModel.kt   # 个人中心ViewModel
│   │       │       ├── apps/                      # 应用管理
│   │       │       │   ├── AppsScreen.kt
│   │       │       │   └── AppsViewModel.kt
│   │       │       ├── files/                     # 文件管理
│   │       │       │   ├── FilesScreen.kt
│   │       │       │   └── FilesViewModel.kt
│   │       │       ├── factory/                   # 工厂模式
│   │       │       │   ├── FactoryModeScreen.kt
│   │       │       │   └── FactoryModeViewModel.kt
│   │       │       ├── aircontrol/                # 空调控制
│   │       │       │   ├── AirControlScreen.kt
│   │       │       │   └── AirControlViewModel.kt
│   │       │       ├── steeringwidget/             # 方控映射
│   │       │       │   ├── SteeringWidgetScreen.kt
│   │       │       │   └── SteeringWidgetViewModel.kt
│   │       │       └── theme/                     # 主题
│   │       └── res/                               # 资源文件
│   ├── build.gradle                               # App模块构建配置
│   └── proguard-rules.pro                         # ProGuard混淆规则
├── build.gradle                                   # 项目级构建配置
├── settings.gradle                                # 项目设置
└── gradle/wrapper/                                # Gradle包装器
```

## 技术栈

### 核心框架
- **Jetpack Compose** - 现代化UI框架
- **Hilt** - 依赖注入框架
- **Room** - 本地数据库
- **DataStore** - 数据存储
- **Coroutines & Flow** - 异步编程
- **Navigation Compose** - 导航组件

### 网络与数据
- **Retrofit2** - 网络请求框架
- **OkHttp** - HTTP客户端
- **Coil** - 图片加载库
- **Gson** - JSON解析
- **微信SDK** - 微信登录

### 架构模式
- **MVVM** - Model-View-ViewModel架构
- **Clean Architecture** - 整洁架构
- **Repository Pattern** - 仓库模式
- **Use Case Pattern** - 用例模式

## 编译和运行

### 前置要求
- Android Studio Arctic Fox (2020.3.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.0

### 编译步骤

1. 克隆项目到本地
2. 用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮或使用快捷键 `Ctrl+R`

### 构建APK

```bash
# Debug版本
./gradlew assembleDebug

# Release版本
./gradlew assembleRelease
```

APK文件位置:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 权限说明

应用需要以下权限:

- `SYSTEM_ALERT_WINDOW` - 悬浮窗权限(必需)
- `ACCESS_FINE_LOCATION` - 定位权限(导航和天气)
- `READ_EXTERNAL_STORAGE` - 存储权限(壁纸和应用数据)
- `BLUETOOTH` - 蓝牙权限(车辆信息)
- `RECORD_AUDIO` - 麦克风权限(语音控制)
- `INTERNET` - 网络权限(在线服务)

## 用户认证系统

### 登录方式
- **手机号登录**: 支持短信验证码登录/注册
- **微信登录**: 支持微信账号快速登录

### 功能特性
- 登录状态自动保存
- Token自动刷新
- 用户信息管理
- 个人中心界面
- VIP会员系统
- 账户安全设置

### 技术实现
- 使用DataStore进行数据持久化
- Retrofit进行网络请求
- 微信SDK集成
- Hilt依赖注入
- MVVM架构

### 使用说明
1. 首次启动应用会自动跳转到登录界面
2. 选择手机号或微信登录
3. 手机号登录:输入手机号 → 获取验证码 → 输入验证码 → 登录
4. 微信登录:点击微信图标 → 授权 → 登录
5. 登录成功后自动跳转到桌面
6. 点击顶部头像进入个人中心
7. 可在个人中心查看和编辑用户信息

### 注意事项
- 当前版本使用Mock API进行演示
- 测试环境验证码固定为: `123456`
- 实际部署时需要替换为真实的API接口
- 需要在微信开放平台申请AppID和AppSecret

## 车型适配

### 已适配车型
- 比亚迪 DiLink系统(秦PLUS、汉EV、宋PLUS等)
- 吉利 GKUI系统(博越、星越L等)
- 长安系统(CS75PLUS、UNI-T等)

### 适配功能
- 空调控制
- 原车设置入口
- CAN总线数据
- 方控映射

## 性能指标

- 启动时间: < 2秒
- 内存占用: < 80MB
- 支持分辨率: 1024x600 至 2000x1200
- 支持方向: 横屏/竖屏自适应

## 功能模块

### 悬浮球功能
- 单击展开快捷菜单
- 长按返回桌面
- 左右滑返回上一页
- 拖拽调整位置
- 边缘自动吸附

### 画中画功能
- 支持拖拽调整位置
- 支持4角吸附
- 支持调整透明度
- 支持大小缩放

### 视频安全限制
- 行驶中自动暂停视频
- 停车自动恢复播放
- 音频继续播放选项
- 强制安全提醒

### 音乐控制
- 多播放器兼容
- 歌词显示
- 迷你播放器
- 沉浸大屏模式

## 贡献指南

欢迎贡献代码! 请遵循以下步骤:

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 联系方式

- 项目主页: https://github.com/yourusername/AutoHubLauncher
- 问题反馈: https://github.com/yourusername/AutoHubLauncher/issues

## 致谢

感谢以下项目的灵感启发:
- 嘟嘟桌面
- 智车桌面
- 布丁桌面
- Android Auto
- Material Design车载设计规范

---

**注意**: 本项目仅供学习和研究使用。请遵守当地法律法规,确保行车安全。
