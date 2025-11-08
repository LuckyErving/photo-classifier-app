# GitHub Actions CI/CD 修复说明

## 🔧 修复的问题

### 1. Resource not accessible by integration 错误
**原因**：
- `actions/create-release@v1` 和 `actions/upload-release-asset@v1` 已被废弃
- 新版 GitHub Actions 需要显式声明权限
- 旧的 API 不再被支持

**解决方案**：
- ✅ 使用 `softprops/action-gh-release@v1` 替代旧的 action
- ✅ 添加 `permissions: contents: write` 权限声明
- ✅ 简化 release 创建流程，一步完成上传

### 2. APK 文件命名
**修改前**：`PhotoClassifier-v1.0.0.apk`
**修改后**：`photo-classifier-app-v1.0.0.apk`

## 📋 主要改动

### 1. 添加权限声明
```yaml
release:
  needs: build
  runs-on: ubuntu-latest
  if: startsWith(github.ref, 'refs/tags/v')
  permissions:
    contents: write  # ← 新增：允许创建 release
```

### 2. 更新 Release Action
**旧版**：
```yaml
- name: Create Release
  uses: actions/create-release@v1  # ❌ 已废弃
  
- name: Upload Release APK
  uses: actions/upload-release-asset@v1  # ❌ 已废弃
```

**新版**：
```yaml
- name: Create Release and Upload APK
  uses: softprops/action-gh-release@v1  # ✅ 推荐使用
```

### 3. 重命名 APK 文件
```yaml
- name: Rename APK
  run: |
    mv app/build/outputs/apk/release/app-release-unsigned.apk \
       app/build/outputs/apk/release/photo-classifier-app-${{ steps.get_version.outputs.VERSION }}.apk || \
    mv app/build/outputs/apk/release/app-release.apk \
       app/build/outputs/apk/release/photo-classifier-app-${{ steps.get_version.outputs.VERSION }}.apk
```

**文件名格式**：
```
photo-classifier-app-v1.0.0.apk
photo-classifier-app-v1.1.0.apk
photo-classifier-app-v2.0.0.apk
```

### 4. 优化 Release 说明
增加了更详细的发布说明：
- ✨ 新增功能
- 🎯 核心特性  
- 📍 存储位置
- 📥 安装说明
- 📱 系统要求

## 🚀 使用方式

### 创建新版本发布

1. **提交代码**
```bash
git add .
git commit -m "feat: 添加文件夹管理功能"
git push origin main
```

2. **创建并推送标签**
```bash
# 创建标签
git tag -a v1.1.0 -m "Release v1.1.0: 文件夹管理增强"

# 推送标签到远程
git push origin v1.1.0
```

3. **自动构建和发布**
GitHub Actions 会自动：
- ✅ 构建 APK
- ✅ 运行测试
- ✅ 创建 Release
- ✅ 上传 APK（文件名：`photo-classifier-app-v1.1.0.apk`）

### 查看构建状态

访问仓库的 Actions 页面：
```
https://github.com/LuckyErving/photo-classifier-app/actions
```

### 下载发布的 APK

访问 Releases 页面：
```
https://github.com/LuckyErving/photo-classifier-app/releases
```

## 📝 版本号规范

遵循语义化版本规范 (Semantic Versioning)：

### 版本格式
```
v<major>.<minor>.<patch>

例如：
v1.0.0  # 初始版本
v1.1.0  # 新增功能
v1.1.1  # Bug 修复
v2.0.0  # 重大更新
```

### 版本递增规则

| 类型 | 说明 | 示例 |
|------|------|------|
| **Major** | 重大更新，可能不兼容 | v1.0.0 → v2.0.0 |
| **Minor** | 新增功能，向下兼容 | v1.0.0 → v1.1.0 |
| **Patch** | Bug 修复，向下兼容 | v1.0.0 → v1.0.1 |

### 版本示例

```bash
# 初始发布
git tag -a v1.0.0 -m "Release v1.0.0: 初始版本"

# 新增文件夹功能
git tag -a v1.1.0 -m "Release v1.1.0: 文件夹管理增强"

# 修复权限问题
git tag -a v1.1.1 -m "Release v1.1.1: 修复存储权限问题"

# 重大架构升级
git tag -a v2.0.0 -m "Release v2.0.0: 迁移到 Jetpack Compose"
```

## 🔍 测试修复

### 本地测试（不创建 Release）

提交到分支并创建 Pull Request：
```bash
git checkout -b test-ci
git add .
git commit -m "test: CI 配置测试"
git push origin test-ci
```

在 GitHub 上创建 PR，会触发 build job（不会创建 release）

### 测试 Release 流程

创建测试标签：
```bash
# 创建测试标签
git tag -a v0.0.1-test -m "Test release"
git push origin v0.0.1-test

# 查看构建结果
# 如果成功，删除测试标签和 release
git tag -d v0.0.1-test
git push origin :refs/tags/v0.0.1-test
```

## ⚠️ 注意事项

### 1. 标签命名
- ✅ 必须以 `v` 开头：`v1.0.0`
- ❌ 不要使用：`1.0.0`, `ver1.0.0`, `version-1.0.0`

### 2. 标签唯一性
- 每个标签只能创建一次
- 不能重复推送相同的标签
- 如需修改，必须先删除旧标签

### 3. 权限要求
- 仓库需要启用 Actions 权限
- Workflow 需要 `contents: write` 权限
- `GITHUB_TOKEN` 会自动提供

### 4. APK 签名
当前构建的是未签名的 APK，如需发布到生产环境：
1. 生成签名密钥
2. 配置签名信息到 GitHub Secrets
3. 修改 Gradle 配置使用签名

## 🐛 常见问题

### Q1: 推送标签后没有触发 Actions
**检查**：
- 标签是否以 `v` 开头
- Actions 是否启用
- Workflow 文件是否在 main 分支

### Q2: Release 创建失败
**检查**：
- `permissions: contents: write` 是否配置
- 仓库 Settings → Actions → General → Workflow permissions 是否设置为 "Read and write permissions"

### Q3: APK 文件找不到
**检查**：
- Gradle 构建是否成功
- APK 输出路径是否正确
- 文件重命名步骤是否执行成功

### Q4: 如何删除错误的 Release
```bash
# 1. 在 GitHub 网页上删除 Release
# 2. 删除本地标签
git tag -d v1.0.0

# 3. 删除远程标签
git push origin :refs/tags/v1.0.0
```

## 📚 相关资源

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [softprops/action-gh-release](https://github.com/softprops/action-gh-release)
- [语义化版本规范](https://semver.org/lang/zh-CN/)
- [Android 签名配置](https://developer.android.com/studio/publish/app-signing)

---

**更新时间**: 2025-11-08
**状态**: ✅ 已修复
