# 快速开始指南

## 1. 项目初始化

### 克隆项目后的第一步

```bash
cd /Users/ervin/Studio/zPlayground/part-time/shoujixiangpianfenlei

# 确保gradlew有执行权限
chmod +x gradlew

# 初始化git（如果还没有）
git init
git add .
git commit -m "Initial commit: Photo Classifier App"
```

## 2. 在Android Studio中打开项目

1. 打开Android Studio
2. 选择 "Open an Existing Project"
3. 选择项目目录
4. 等待Gradle同步完成

## 3. 添加应用图标（重要！）

项目目前缺少应用图标，你需要：

### 方法1：使用Android Studio生成
1. 右键点击 `res` 目录
2. 选择 `New` > `Image Asset`
3. 选择图标类型（Launcher Icons）
4. 上传你的图标图片或使用Clip Art
5. 点击 `Next` 和 `Finish`

### 方法2：手动添加
将以下尺寸的图标文件放入对应目录：
- `mipmap-mdpi/ic_launcher.png` (48x48)
- `mipmap-hdpi/ic_launcher.png` (72x72)
- `mipmap-xhdpi/ic_launcher.png` (96x96)
- `mipmap-xxhdpi/ic_launcher.png` (144x144)
- `mipmap-xxxhdpi/ic_launcher.png` (192x192)

## 4. 本地运行和测试

### 连接设备或启动模拟器

**使用真实设备：**
1. 在手机上启用开发者选项和USB调试
2. 用USB连接手机
3. 在Android Studio中选择你的设备

**使用模拟器：**
1. 在Android Studio中打开 AVD Manager
2. 创建一个Android 10+的虚拟设备
3. 启动模拟器

### 运行应用

```bash
# 方法1：使用Android Studio
# 点击工具栏的 Run 按钮（绿色三角形）

# 方法2：使用命令行
./gradlew installDebug
```

## 5. 设置GitHub仓库

### 创建GitHub仓库

1. 访问 https://github.com/new
2. 创建新仓库（例如：photo-classifier-app）
3. 不要初始化README、.gitignore或license

### 推送代码到GitHub

```bash
# 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/photo-classifier-app.git

# 推送代码
git branch -M main
git push -u origin main
```

## 6. 测试GitHub Actions

推送代码后，GitHub Actions会自动开始构建：

1. 访问你的GitHub仓库
2. 点击 "Actions" 标签
3. 查看构建进度

第一次构建可能需要5-10分钟。

## 7. 发布第一个版本

### 准备发布

1. 确保应用正常运行
2. 更新版本信息（如果需要）

### 创建Release

```bash
# 创建版本标签
git tag v1.0.0

# 推送标签
git push origin v1.0.0
```

### 查看Release

1. 等待GitHub Actions完成（3-5分钟）
2. 访问仓库的 "Releases" 页面
3. 下载APK文件
4. 安装到Android设备测试

## 8. 测试应用功能

### 首次运行
1. 授予相机和存储权限
2. 测试拍照功能
3. 测试创建文件夹
4. 测试照片浏览
5. 测试删除照片

### 常见问题

**权限被拒绝**
- 在手机设置中手动授予权限

**相机无法启动**
- 确保使用真实设备或配置了相机的模拟器

**照片不显示**
- 检查文件管理器中是否有照片
- 路径：`/Android/data/com.example.photoclassifier/files/Pictures/PhotoClassifier/`

## 9. 开发建议

### 使用分支进行开发

```bash
# 创建功能分支
git checkout -b feature/new-feature

# 开发完成后
git add .
git commit -m "Add new feature"
git push origin feature/new-feature

# 在GitHub上创建Pull Request
```

### 代码规范

- 使用有意义的变量名和函数名
- 添加注释说明复杂逻辑
- 遵循Kotlin编码规范

### 测试

```bash
# 运行单元测试
./gradlew test

# 运行所有检查
./gradlew check
```

## 10. 下一步

### 可以改进的功能

- [ ] 添加图片全屏查看
- [ ] 添加图片编辑功能
- [ ] 支持批量操作
- [ ] 添加搜索功能
- [ ] 支持云同步
- [ ] 添加更多主题
- [ ] 国际化支持

### 优化建议

- [ ] 启用ProGuard代码混淆
- [ ] 优化图片加载性能
- [ ] 添加数据库支持
- [ ] 实现图片缓存机制

## 有用的命令

```bash
# 清理构建
./gradlew clean

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease

# 运行测试
./gradlew test

# 安装到设备
./gradlew installDebug

# 卸载
./gradlew uninstallDebug

# 查看所有任务
./gradlew tasks
```

## 获取帮助

- 查看完整文档：README.md
- CI/CD指南：CI_CD_GUIDE.md
- Android开发文档：https://developer.android.com/
- GitHub Actions文档：https://docs.github.com/actions

## 故障排查

### Gradle同步失败
```bash
# 清理Gradle缓存
rm -rf ~/.gradle/caches/
./gradlew clean
```

### 构建失败
1. 检查JDK版本（需要JDK 17）
2. 更新Android SDK
3. 清理并重新构建

### 签名问题
- Debug版本使用debug签名（自动）
- Release版本需要配置签名（见CI_CD_GUIDE.md）

祝你开发顺利！🎉
