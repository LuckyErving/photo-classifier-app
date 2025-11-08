# 文件夹存储功能说明

## 功能优化

本次优化实现了以下功能：

### 1. 文件夹选择与切换
- **文件夹下拉列表**：在拍照界面顶部显示文件夹选择器，可以随时切换保存照片的目标文件夹
- **创建新文件夹**：点击"+"按钮可以创建新文件夹，创建后自动切换到新文件夹
- **记忆功能**：使用 SharedPreferences 持久化存储最后选择的文件夹，下次打开应用自动选择

### 2. 存储位置
照片存储在：
```
/storage/emulated/0/DCIM/PhotoClassifier/<文件夹名称>/
```

这个位置的优势：
- ✅ **通过USB可访问**：使用数据线连接电脑后，可以在 `内部存储/DCIM/PhotoClassifier/` 目录找到所有照片
- ✅ **标准位置**：DCIM 是 Android 标准的相机照片存储目录
- ✅ **易于管理**：可以直接复制整个文件夹到电脑
- ✅ **无需root权限**：普通用户权限即可访问

### 3. 持久化存储
使用 `SharedPreferences` 存储用户偏好：
- **存储位置**：应用私有数据目录 
  ```
  /data/data/com.example.photoclassifier/shared_prefs/PhotoClassifierPrefs.xml
  ```
- **存储内容**：最后选择的文件夹路径
- **访问方式**：通过 `FileManager` 类的 `saveLastSelectedFolder()` 和 `getLastSelectedFolder()` 方法

## 使用方法

### 第一次拍照
1. 打开应用，点击"拍照"按钮
2. 授予相机和存储权限
3. 在文件夹下拉列表中选择或创建新文件夹
4. 开始拍照，照片会自动保存到选择的文件夹

### 切换文件夹
1. 在拍照界面，点击文件夹下拉列表
2. 选择其他文件夹，或点击"+"创建新文件夹
3. 切换后的照片会保存到新文件夹

### 通过USB导出照片
1. 使用USB数据线连接手机和电脑
2. 在手机上选择"文件传输"模式
3. 在电脑上打开 `内部存储/DCIM/PhotoClassifier/`
4. 复制需要的文件夹到电脑

## 代码改动说明

### FileManager.kt
- 添加 `SharedPreferences` 支持
- 修改 `getAppRootDirectory()` 使用公共DCIM目录
- 添加 `saveLastSelectedFolder()` 和 `getLastSelectedFolder()` 方法

### CameraActivity.kt
- 添加创建文件夹功能
- 添加文件夹选择记忆功能
- 优化文件夹切换提示

### activity_camera.xml
- 添加"+"按钮用于创建新文件夹
- 优化布局，文件夹选择器和按钮并排显示

### strings.xml
- 添加新的字符串资源

### AndroidManifest.xml
- 调整存储权限配置
- 支持 Android 不同版本的权限要求

### file_paths.xml
- 添加 DCIM 目录的 FileProvider 配置

## 技术细节

### Android 存储权限适配
- **Android 10+**：使用 Scoped Storage，访问公共目录需要特定权限
- **Android 13+**：需要 `READ_MEDIA_IMAGES` 权限
- **Android 9-**：需要 `WRITE_EXTERNAL_STORAGE` 权限

### SharedPreferences 数据结构
```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="last_selected_folder">/storage/emulated/0/DCIM/PhotoClassifier/工作照片</string>
</map>
```

## 注意事项

1. **权限管理**：首次使用需要授予相机和存储权限
2. **Android版本**：在 Android 10 及以上版本，应用可以直接写入 DCIM 目录
3. **卸载应用**：卸载应用后，DCIM 目录下的照片会保留，不会被删除
4. **文件夹命名**：文件夹名称不能包含 `/` 和 `\` 字符
