package com.autohub.launcher.data.local

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String? = null
)

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _currentPath = MutableStateFlow(getDefaultPath())
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles: StateFlow<Set<String>> = _selectedFiles.asStateFlow()

    init {
        loadFiles()
    }

    private fun getDefaultPath(): String {
        return context.getExternalFilesDir(null)?.absolutePath ?: "/sdcard"
    }

    fun loadFiles() {
        val path = _currentPath.value
        val directory = File(path)

        if (directory.exists() && directory.isDirectory) {
            val fileItems = directory.listFiles()?.map { file ->
                FileItem(
                    name = file.name,
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    size = if (file.isFile) file.length() else 0L,
                    lastModified = file.lastModified(),
                    mimeType = if (file.isFile) {
                        getMimeType(file.name)
                    } else null
                )
            }?.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.name }) ?: emptyList()

            _files.value = fileItems
        } else {
            _files.value = emptyList()
        }
    }

    fun navigateTo(path: String) {
        _currentPath.value = path
        _selectedFiles.value = emptySet()
        loadFiles()
    }

    fun navigateUp() {
        val currentFile = File(_currentPath.value)
        val parentFile = currentFile.parentFile
        if (parentFile != null) {
            navigateTo(parentFile.absolutePath)
        }
    }

    fun toggleFileSelection(path: String) {
        val currentSelection = _selectedFiles.value.toMutableSet()
        if (currentSelection.contains(path)) {
            currentSelection.remove(path)
        } else {
            currentSelection.add(path)
        }
        _selectedFiles.value = currentSelection
    }

    fun clearSelection() {
        _selectedFiles.value = emptySet()
    }

    fun deleteSelectedFiles(): Boolean {
        var success = true
        _selectedFiles.value.forEach { path ->
            val file = File(path)
            if (!file.deleteRecursively()) {
                success = false
            }
        }

        if (success) {
            clearSelection()
            loadFiles()
        }

        return success
    }

    fun renameFile(oldPath: String, newName: String): Boolean {
        val oldFile = File(oldPath)
        val newFile = File(oldFile.parent, newName)

        return if (oldFile.renameTo(newFile)) {
            loadFiles()
            true
        } else {
            false
        }
    }

    fun createFolder(name: String): Boolean {
        val newFolder = File(_currentPath.value, name)
        return if (newFolder.mkdirs()) {
            loadFiles()
            true
        } else {
            false
        }
    }

    fun getFileSize(size: Long): String {
        if (size < 1024) return "$size B"
        val kb = size / 1024.0
        if (kb < 1024) return String.format("%.2f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.2f MB", mb)
        val gb = mb / 1024.0
        return String.format("%.2f GB", gb)
    }

    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex != -1 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1).lowercase()
        } else {
            ""
        }
    }

    fun isImageFile(fileName: String): Boolean {
        val ext = getFileExtension(fileName)
        return ext in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }

    fun isVideoFile(fileName: String): Boolean {
        val ext = getFileExtension(fileName)
        return ext in listOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm")
    }

    fun isAudioFile(fileName: String): Boolean {
        val ext = getFileExtension(fileName)
        return ext in listOf("mp3", "wav", "ogg", "m4a", "aac", "flac")
    }

    fun isDocumentFile(fileName: String): Boolean {
        val ext = getFileExtension(fileName)
        return ext in listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt")
    }

    fun isArchiveFile(fileName: String): Boolean {
        val ext = getFileExtension(fileName)
        return ext in listOf("zip", "rar", "7z", "tar", "gz")
    }

    private fun getMimeType(fileName: String): String? {
        val ext = getFileExtension(fileName)
        return when (ext) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "pdf" -> "application/pdf"
            "mp3" -> "audio/mpeg"
            "mp4" -> "video/mp4"
            else -> null
        }
    }

    fun getStorageInfo(): StorageInfo {
        val internalStorage = context.filesDir.absoluteFile.parentFile
        val totalSpace = internalStorage.totalSpace
        val freeSpace = internalStorage.freeSpace
        val usedSpace = totalSpace - freeSpace

        return StorageInfo(
            totalSpace = totalSpace,
            usedSpace = usedSpace,
            freeSpace = freeSpace,
            usedPercentage = (usedSpace.toDouble() / totalSpace * 100).toInt()
        )
    }
}

data class StorageInfo(
    val totalSpace: Long,
    val usedSpace: Long,
    val freeSpace: Long,
    val usedPercentage: Int
) {
    fun formatTotalSpace(): String {
        return formatFileSize(totalSpace)
    }

    fun formatUsedSpace(): String {
        return formatFileSize(usedSpace)
    }

    fun formatFreeSpace(): String {
        return formatFileSize(freeSpace)
    }

    private fun formatFileSize(size: Long): String {
        if (size < 1024) return "$size B"
        val kb = size / 1024.0
        if (kb < 1024) return String.format("%.2f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.2f MB", mb)
        val gb = mb / 1024.0
        return String.format("%.2f GB", gb)
    }
}
