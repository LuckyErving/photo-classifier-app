# 项目结构说明

## 完整目录结构

```
shoujixiangpianfenlei/
├── .github/
│   └── workflows/
│       └── android-ci.yml          # GitHub Actions CI/CD配置
│
├── app/
│   ├── build.gradle                # 应用级Gradle配置
│   ├── proguard-rules.pro          # ProGuard混淆规则
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml # 应用清单文件
│           ├── java/com/example/photoclassifier/
│           │   ├── MainActivity.kt       # 主Activity
│           │   ├── CameraActivity.kt     # 相机Activity
│           │   ├── GalleryActivity.kt    # 相册Activity
│           │   ├── PhotoAdapter.kt       # 照片列表适配器
│           │   ├── Photo.kt              # 照片数据模型
│           │   └── FileManager.kt        # 文件管理工具
│           └── res/
│               ├── drawable/             # 图片资源
│               ├── layout/
│               │   ├── activity_main.xml      # 主界面布局
│               │   ├── activity_camera.xml    # 相机界面布局
│               │   ├── activity_gallery.xml   # 相册界面布局
│               │   └── item_photo.xml         # 照片列表项布局
│               ├── mipmap-*/             # 应用图标（需要添加）
│               ├── values/
│               │   ├── colors.xml        # 颜色定义
│               │   ├── strings.xml       # 字符串资源
│               │   └── themes.xml        # 主题样式
│               └── xml/
│                   └── file_paths.xml    # FileProvider配置
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties    # Gradle Wrapper配置
│
├── .gitignore                  # Git忽略文件
├── build.gradle                # 项目级Gradle配置
├── gradle.properties           # Gradle属性配置
├── gradlew                     # Gradle Wrapper脚本（Unix）
├── gradlew.bat                 # Gradle Wrapper脚本（Windows）
├── settings.gradle             # Gradle设置
├── README.md                   # 项目说明文档
├── CI_CD_GUIDE.md             # CI/CD使用指南
└── QUICK_START.md             # 快速开始指南
```

## 核心文件说明

### Gradle配置文件

#### `build.gradle`（项目级）
- 定义项目级别的Gradle插件版本
- 配置所有子项目共享的仓库

#### `app/build.gradle`（应用级）
- 配置应用ID、版本号
- 设置最低和目标SDK版本
- 声明依赖项（CameraX, Glide, RecyclerView等）
- 配置构建类型和签名

#### `settings.gradle`
- 定义项目名称
- 包含应用模块

#### `gradle.properties`
- Gradle全局配置
- JVM参数设置
- AndroidX启用

### 源代码文件

#### `MainActivity.kt`
- 应用入口
- 权限管理
- 导航到拍照和相册页面

#### `CameraActivity.kt`
- CameraX相机实现
- 拍照功能
- 文件夹选择
- 前后摄像头切换

#### `GalleryActivity.kt`
- 照片网格显示
- 文件夹筛选
- 创建新文件夹
- 照片删除

#### `PhotoAdapter.kt`
- RecyclerView适配器
- 照片列表绑定
- Glide图片加载

#### `FileManager.kt`
- 文件系统操作
- 文件夹管理
- 照片CRUD操作
- Scoped Storage适配

#### `Photo.kt`
- 照片数据类
- 包含文件信息、路径、所属文件夹等

### 布局文件

#### `activity_main.xml`
- 三个按钮：拍照、查看相册、管理文件夹
- 简洁的主界面设计

#### `activity_camera.xml`
- CameraX PreviewView
- 文件夹选择Spinner
- 拍照和切换相机按钮

#### `activity_gallery.xml`
- RecyclerView照片网格
- 文件夹筛选Spinner
- 创建文件夹按钮
- 空状态提示

#### `item_photo.xml`
- CardView卡片样式
- 照片缩略图
- 文件名显示
- 删除按钮

### 资源文件

#### `strings.xml`
- 所有UI文本的中文字符串
- 支持未来国际化

#### `colors.xml`
- Material Design颜色定义
- 主题颜色配置

#### `themes.xml`
- Material Design主题
- 深色模式支持

### Android配置

#### `AndroidManifest.xml`
- 应用元数据配置
- 权限声明（相机、存储）
- Activity声明
- FileProvider配置
- Android 10+ Scoped Storage配置

#### `file_paths.xml`
- FileProvider路径配置
- 共享文件路径定义

### CI/CD配置

#### `.github/workflows/android-ci.yml`
- 自动构建工作流
- 自动测试
- Release自动发布
- APK上传到GitHub Release

### 文档文件

#### `README.md`
- 项目概述
- 功能说明
- 技术栈
- 构建指南

#### `CI_CD_GUIDE.md`
- GitHub Actions详细说明
- 发布流程
- 签名配置
- 故障排查

#### `QUICK_START.md`
- 快速上手指南
- 开发流程
- 常用命令
- 问题解决

## 依赖项说明

### 核心依赖
- `androidx.core:core-ktx` - Kotlin扩展
- `androidx.appcompat:appcompat` - 兼容性支持
- `com.google.android.material:material` - Material Design组件

### CameraX
- `androidx.camera:camera-core` - 核心功能
- `androidx.camera:camera-camera2` - Camera2实现
- `androidx.camera:camera-lifecycle` - 生命周期管理
- `androidx.camera:camera-view` - PreviewView

### 图片加载
- `com.github.bumptech.glide:glide` - 高效图片加载库

### UI组件
- `androidx.recyclerview:recyclerview` - 列表展示
- `androidx.constraintlayout:constraintlayout` - 灵活布局

### 其他
- `androidx.lifecycle:lifecycle-*` - 生命周期组件
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - 协程支持

## 权限说明

### 运行时权限
- `CAMERA` - 拍照必需
- `READ_MEDIA_IMAGES` - Android 13+读取图片
- `READ_EXTERNAL_STORAGE` - Android 10-12读取存储

### 使用Scoped Storage
- Android 10+使用应用专属目录
- 无需 WRITE_EXTERNAL_STORAGE 权限
- 数据隔离，更安全

## 构建配置

### SDK版本
- `minSdk: 29` (Android 10)
- `targetSdk: 34` (Android 14)
- `compileSdk: 34`

### 构建类型
- **Debug**: 开发调试版本，使用debug签名
- **Release**: 发布版本，需配置签名

### ViewBinding
- 启用ViewBinding简化View访问
- 类型安全，避免findViewById

## 下一步开发建议

### 必须完成
- [ ] 添加应用图标
- [ ] 配置Release签名

### 功能增强
- [ ] 图片全屏查看
- [ ] 批量操作
- [ ] 图片编辑
- [ ] 搜索功能
- [ ] 数据库支持

### 优化
- [ ] 启用ProGuard
- [ ] 图片缓存优化
- [ ] 性能监控
- [ ] 崩溃报告

## 注意事项

1. **图标**: mipmap文件夹需要添加实际的图标文件
2. **签名**: 生产环境需要配置正式签名文件
3. **测试**: 建议在真实设备上测试相机功能
4. **权限**: 首次运行需要用户授予权限
5. **存储**: 照片保存在应用专属目录，卸载应用会删除照片

## 技术亮点

✅ **完全兼容Android 10+** - 使用Scoped Storage
✅ **现代化架构** - CameraX, ViewBinding, Kotlin
✅ **Material Design** - 现代UI设计
✅ **CI/CD自动化** - GitHub Actions自动构建发布
✅ **权限最佳实践** - Activity Result API
✅ **代码质量** - Kotlin规范，清晰的代码结构
