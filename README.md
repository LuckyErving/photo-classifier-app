# 照片分类管理 APP

一个功能完整的Android照片分类管理应用，支持拍照、文件夹分类和照片管理。

## 功能特性

- 📷 **拍照功能**：使用CameraX实现高质量拍照，支持前后摄像头切换
- 📁 **文件夹管理**：创建自定义文件夹，将照片分类保存
- 🖼️ **照片浏览**：网格布局浏览所有照片或指定文件夹的照片
- 🗑️ **照片管理**：支持删除照片功能
- ✅ **Android 10+ 支持**：完全适配Android 10及以上版本的Scoped Storage

## 系统要求

- Android 10 (API 29) 及以上版本
- 相机权限
- 存储权限（根据Android版本自动适配）

## 技术栈

- **语言**：Kotlin
- **架构**：MVVM（简化版）
- **UI**：Material Design
- **相机**：CameraX
- **图片加载**：Glide
- **布局**：ConstraintLayout, RecyclerView
- **权限管理**：Activity Result API

## 项目结构

```
app/src/main/
├── java/com/example/photoclassifier/
│   ├── MainActivity.kt           # 主界面
│   ├── CameraActivity.kt         # 拍照界面
│   ├── GalleryActivity.kt        # 相册界面
│   ├── PhotoAdapter.kt           # 照片列表适配器
│   ├── Photo.kt                  # 照片数据模型
│   └── FileManager.kt            # 文件管理工具类
├── res/
│   ├── layout/                   # 布局文件
│   ├── values/                   # 资源文件（字符串、颜色、主题）
│   └── xml/                      # XML配置文件
└── AndroidManifest.xml           # 应用清单
```

## 构建项目

### 前置条件

- JDK 17 或更高版本
- Android Studio (推荐最新稳定版)
- Android SDK (API 29+)

### 本地构建

1. 克隆项目
```bash
git clone <repository-url>
cd shoujixiangpianfenlei
```

2. 使用Android Studio打开项目

3. 等待Gradle同步完成

4. 运行项目
```bash
./gradlew assembleDebug
```

## CI/CD

本项目使用GitHub Actions实现持续集成和持续部署。

### 工作流程

- **推送到main/master分支**：自动构建和测试
- **创建版本标签（v*）**：自动构建Release APK并发布到GitHub Release

### 创建新版本

```bash
# 创建版本标签
git tag v1.0.0

# 推送标签到远程仓库
git push origin v1.0.0
```

GitHub Actions将自动：
1. 构建项目
2. 运行测试
3. 生成Release APK
4. 创建GitHub Release
5. 上传APK到Release页面

## 使用说明

### 拍照

1. 点击"拍照"按钮
2. 在下拉菜单中选择要保存的文件夹
3. 点击拍照按钮进行拍照
4. 照片自动保存到选定的文件夹

### 查看相册

1. 点击"查看相册"按钮
2. 使用下拉菜单筛选文件夹或查看所有照片
3. 点击删除按钮可删除照片

### 管理文件夹

1. 在相册界面点击"创建文件夹"按钮
2. 输入文件夹名称
3. 新文件夹将出现在文件夹列表中

## 权限说明

应用需要以下权限：

- **CAMERA**：用于拍照
- **READ_MEDIA_IMAGES**（Android 13+）：读取照片
- **READ_EXTERNAL_STORAGE**（Android 10-12）：读取照片

应用使用Scoped Storage，所有照片保存在应用专属目录中，无需额外的存储权限。

## 注意事项

1. **应用图标**：需要手动添加应用图标到各个mipmap文件夹
2. **签名配置**：生产环境需要配置正式的签名文件
3. **ProGuard**：Release版本可以启用代码混淆
4. **测试**：建议添加单元测试和UI测试

## 许可证

本项目仅供学习和参考使用。

## 贡献

欢迎提交Issue和Pull Request！

## 联系方式

如有问题，请创建Issue或联系开发者。
