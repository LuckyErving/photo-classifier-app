# 照片分类App - 文件夹功能优化总结

## 🎯 优化目标
在第一次拍照前可以选择照片存放的文件夹，并且可以随时切换文件夹。照片默认保存到选择的文件夹，这个选择会被记住，通过USB数据线可以方便地将整个文件夹复制到电脑。

## ✨ 新增功能

### 1. 文件夹选择器
- **位置**：拍照界面顶部
- **功能**：下拉选择现有文件夹
- **特性**：
  - 自动加载所有已创建的文件夹
  - 显示文件夹名称列表
  - 切换时显示Toast提示

### 2. 快速创建文件夹
- **按钮**："+" 按钮（位于文件夹选择器右侧）
- **功能**：弹出对话框创建新文件夹
- **验证**：
  - 检查文件夹名称是否为空
  - 检查是否包含非法字符（`/` 和 `\`）
  - 检查是否已存在同名文件夹

### 3. 文件夹记忆功能
- **技术**：使用 SharedPreferences 持久化存储
- **存储内容**：最后选择的文件夹完整路径
- **触发时机**：每次切换文件夹时自动保存
- **恢复时机**：打开拍照界面时自动加载

### 4. USB可访问的存储位置
- **存储路径**：`/storage/emulated/0/DCIM/PhotoClassifier/`
- **优势**：
  - 标准DCIM目录，USB可直接访问
  - 卸载应用后照片保留
  - 文件管理器可见
  - 电脑可直接读取

## 📝 代码改动

### 修改的文件

#### 1. `FileManager.kt`
```kotlin
// 新增常量
private const val PREFS_NAME = "PhotoClassifierPrefs"
private const val KEY_LAST_FOLDER = "last_selected_folder"

// 新增SharedPreferences实例
private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

// 修改存储位置为公共DCIM目录
fun getAppRootDirectory(): File {
    val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val appDir = File(dcimDir, APP_FOLDER)
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    return appDir
}

// 新增保存文件夹方法
fun saveLastSelectedFolder(folder: File) {
    prefs.edit().putString(KEY_LAST_FOLDER, folder.absolutePath).apply()
}

// 新增获取文件夹方法
fun getLastSelectedFolder(): File? {
    val path = prefs.getString(KEY_LAST_FOLDER, null)
    return path?.let { 
        val folder = File(it)
        if (folder.exists() && folder.isDirectory) folder else null
    }
}
```

#### 2. `CameraActivity.kt`
```kotlin
// 新增导入
import android.app.AlertDialog
import android.widget.EditText

// 优化文件夹选择器初始化
private fun setupFolderSpinner() {
    // ...existing code...
    
    // 优先加载上次选择的文件夹
    val lastFolder = fileManager.getLastSelectedFolder()
    val folderIndex = if (lastFolder != null) {
        folders.indexOfFirst { it.absolutePath == lastFolder.absolutePath }
    } else {
        -1
    }
    
    if (folderIndex >= 0) {
        binding.spinnerFolder.setSelection(folderIndex)
        selectedFolder = folders[folderIndex]
    }
    
    // 切换时保存选择
    override fun onItemSelected(...) {
        selectedFolder = folders[position]
        selectedFolder?.let { fileManager.saveLastSelectedFolder(it) }
        // 显示提示
    }
}

// 新增创建文件夹按钮监听
binding.btnNewFolder.setOnClickListener {
    showCreateFolderDialog()
}

// 新增创建文件夹对话框
private fun showCreateFolderDialog() {
    val editText = EditText(this)
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.create_folder))
        .setView(editText)
        .setPositiveButton(getString(R.string.ok)) { _, _ ->
            // 创建文件夹并刷新列表
        }
        .show()
}
```

#### 3. `activity_camera.xml`
```xml
<!-- 文件夹选择区域 -->
<LinearLayout
    android:orientation="horizontal">
    
    <Spinner
        android:id="@+id/spinnerFolder"
        android:layout_weight="1" />
    
    <Button
        android:id="@+id/btnNewFolder"
        android:text="+"
        android:textSize="20sp" />
</LinearLayout>
```

#### 4. `strings.xml`
```xml
<string name="current_folder">当前文件夹</string>
<string name="storage_location">存储位置：DCIM/PhotoClassifier</string>
```

#### 5. `MainActivity.kt`
```kotlin
// 优化权限请求，支持不同Android版本
private fun checkPermissionsAndOpenCamera() {
    val requiredPermissions = mutableListOf(Manifest.permission.CAMERA)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    // ...
}
```

#### 6. `AndroidManifest.xml`
```xml
<!-- 支持写入公共存储 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<application
    android:requestLegacyExternalStorage="true">
```

#### 7. `file_paths.xml`
```xml
<!-- 添加DCIM目录配置 -->
<external-path name="dcim" path="DCIM/" />
```

## 🔧 技术细节

### SharedPreferences存储方案
**为什么选择SharedPreferences？**
- ✅ 轻量级，适合存储简单的键值对
- ✅ Android原生支持，无需额外依赖
- ✅ 自动持久化，应用关闭后数据保留
- ✅ 访问速度快
- ✅ 应用私有，安全可靠

**存储位置：**
```
/data/data/com.example.photoclassifier/shared_prefs/PhotoClassifierPrefs.xml
```

**数据格式：**
```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="last_selected_folder">/storage/emulated/0/DCIM/PhotoClassifier/工作照片</string>
</map>
```

### 照片存储方案
**为什么选择DCIM目录？**
- ✅ **USB可访问**：连接电脑后可直接访问
- ✅ **标准位置**：符合Android相机照片存储规范
- ✅ **用户友好**：用户熟悉这个位置
- ✅ **持久化**：卸载应用后照片保留
- ✅ **兼容性好**：所有文件管理器都能访问

**目录结构：**
```
DCIM/
└── PhotoClassifier/
    ├── Default/
    │   ├── IMG_20250108_143022.jpg
    │   └── IMG_20250108_143045.jpg
    ├── 工作照片/
    │   ├── IMG_20250108_150000.jpg
    │   └── IMG_20250108_151230.jpg
    └── 家庭照片/
        ├── IMG_20250108_180000.jpg
        └── IMG_20250108_181500.jpg
```

## 🎨 用户体验改进

### 界面优化
- 文件夹选择器占据大部分宽度，便于查看文件夹名称
- "+"按钮设计简洁，直观易懂
- Toast提示当前文件夹，给用户明确反馈

### 交互流程
1. **首次使用**
   - 自动创建Default文件夹
   - 默认选择Default文件夹
   
2. **创建新文件夹**
   - 点击"+"按钮 → 输入名称 → 自动切换到新文件夹

3. **切换文件夹**
   - 点击下拉列表 → 选择文件夹 → 显示Toast提示

4. **记忆功能**
   - 自动记住最后选择
   - 下次打开自动恢复

## 📱 使用场景

### 场景1：工作照片整理
```
上班路上拍的照片 → 选择"通勤"文件夹
会议白板拍照 → 选择"会议记录"文件夹
工作文档拍照 → 选择"文档"文件夹
```

### 场景2：家庭生活记录
```
宝宝成长照片 → 选择"宝宝成长"文件夹
家庭聚会 → 选择"家庭"文件夹
旅行照片 → 选择"旅行-2025"文件夹
```

### 场景3：USB导出
```
1. 连接USB数据线
2. 选择"文件传输"
3. 打开"内部存储/DCIM/PhotoClassifier/"
4. 复制需要的文件夹到电脑
```

## ✅ 测试验证

详细测试步骤请参考 [TESTING_GUIDE.md](./TESTING_GUIDE.md)

关键测试点：
- ✅ 首次启动默认文件夹
- ✅ 创建新文件夹
- ✅ 切换文件夹
- ✅ 记忆功能
- ✅ USB导出
- ✅ 不同Android版本兼容性

## 📚 相关文档

- [FOLDER_STORAGE_GUIDE.md](./FOLDER_STORAGE_GUIDE.md) - 功能详细说明
- [TESTING_GUIDE.md](./TESTING_GUIDE.md) - 测试指南

## 🚀 下一步建议

1. **批量移动功能**：在相册中支持将照片批量移动到其他文件夹
2. **文件夹重命名**：支持重命名已创建的文件夹
3. **文件夹删除**：删除空文件夹或移动照片后删除
4. **云端同步**：考虑添加云端备份功能
5. **统计信息**：显示每个文件夹的照片数量
