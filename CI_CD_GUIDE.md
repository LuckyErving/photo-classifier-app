# GitHub Actions CI/CD 使用指南

## 概述

本项目使用GitHub Actions实现自动化构建、测试和发布流程。

## 工作流配置

工作流文件位于：`.github/workflows/android-ci.yml`

## 触发条件

### 1. 自动构建和测试
当你推送代码到 `main` 或 `master` 分支时：
```bash
git add .
git commit -m "Your commit message"
git push origin main
```

### 2. 自动发布Release版本
当你创建版本标签时：
```bash
# 创建版本标签（格式：v1.0.0）
git tag v1.0.0

# 推送标签
git push origin v1.0.0
```

## 工作流程说明

### Build Job
1. ✅ 检出代码
2. ✅ 设置JDK 17环境
3. ✅ 赋予gradlew执行权限
4. ✅ 构建项目
5. ✅ 运行测试
6. ✅ 构建Release APK
7. ✅ 上传APK作为构建产物

### Release Job（仅在创建标签时触发）
1. ✅ 检出代码
2. ✅ 设置JDK 17环境
3. ✅ 构建Release APK
4. ✅ 创建GitHub Release
5. ✅ 上传APK到Release页面

## 如何发布新版本

### 步骤1：更新版本号

编辑 `app/build.gradle` 文件：
```gradle
defaultConfig {
    versionCode 2        // 增加版本代码
    versionName "1.1.0"  // 更新版本名称
}
```

### 步骤2：提交更改
```bash
git add app/build.gradle
git commit -m "Bump version to 1.1.0"
git push origin main
```

### 步骤3：创建版本标签
```bash
git tag v1.1.0
git push origin v1.1.0
```

### 步骤4：等待GitHub Actions完成

1. 访问你的GitHub仓库
2. 点击 "Actions" 标签
3. 查看工作流运行状态
4. 等待所有步骤完成（约3-5分钟）

### 步骤5：查看Release

1. 点击仓库的 "Releases" 标签
2. 查看新创建的Release
3. 下载APK文件

## APK文件位置

- **在Actions中**：每次构建后，APK作为artifact上传，可在Actions页面下载
- **在Release中**：标签推送后，APK会自动上传到Release页面，命名为 `PhotoClassifier-v1.0.0.apk`

## 自定义Release说明

编辑 `.github/workflows/android-ci.yml` 中的 `body` 部分：
```yaml
body: |
  ## 照片分类管理 ${{ steps.get_version.outputs.VERSION }}
  
  ### 更新内容
  - 新增功能1
  - 修复bug2
  - 优化性能3
```

## 配置签名（可选）

为了发布到Google Play或其他应用商店，你需要配置应用签名：

### 1. 生成签名文件
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### 2. 将签名信息添加到GitHub Secrets

在GitHub仓库设置中添加以下Secrets：
- `KEYSTORE_FILE`：签名文件的base64编码
- `KEYSTORE_PASSWORD`：Keystore密码
- `KEY_ALIAS`：Key别名
- `KEY_PASSWORD`：Key密码

### 3. 更新工作流

在 `.github/workflows/android-ci.yml` 中添加签名步骤：
```yaml
- name: Decode Keystore
  env:
    ENCODED_STRING: ${{ secrets.KEYSTORE_FILE }}
  run: |
    echo $ENCODED_STRING | base64 -d > keystore.jks

- name: Build Release APK with signing
  run: ./gradlew assembleRelease
  env:
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### 4. 更新build.gradle

```gradle
android {
    signingConfigs {
        release {
            storeFile file("../keystore.jks")
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

## 故障排查

### 构建失败

1. 检查Gradle配置是否正确
2. 确保所有依赖项都可以下载
3. 查看Actions日志中的错误信息

### 权限问题

确保gradlew有执行权限：
```bash
chmod +x gradlew
git add gradlew
git commit -m "Add execute permission to gradlew"
git push
```

### Release创建失败

确保：
1. 标签格式正确（以v开头，如v1.0.0）
2. GitHub Token有足够的权限
3. 没有重复的标签

## 查看构建日志

1. 进入GitHub仓库
2. 点击 "Actions" 标签
3. 选择具体的工作流运行
4. 展开各个步骤查看详细日志

## 本地测试

在推送之前，可以在本地测试构建：

```bash
# 构建debug版本
./gradlew assembleDebug

# 构建release版本
./gradlew assembleRelease

# 运行测试
./gradlew test

# 运行所有检查
./gradlew check
```

## 提示

- 每次推送到main分支都会触发构建，建议使用feature分支开发
- Release版本使用的是debug签名，生产环境需要配置正式签名
- 可以在Actions页面手动触发工作流
- 构建产物保留90天，过期后会自动删除

## 额外配置

### 添加构建通知

可以配置Slack、Email等通知方式，在构建完成或失败时接收通知。

### 添加代码质量检查

可以集成SonarQube、Lint等工具进行代码质量检查。

### 添加自动化测试

可以添加UI测试、集成测试等，在构建过程中自动运行。
