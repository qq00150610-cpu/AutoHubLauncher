package com.autohub.launcher.ui.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.local.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val fileManager: FileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilesUiState())
    val uiState: StateFlow<FilesUiState> = _uiState.asStateFlow()

    init {
        loadFiles()
        loadStorageInfo()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            fileManager.currentPath.collect { path ->
                _uiState.value = _uiState.value.copy(
                    currentPath = path
                )
            }
        }

        viewModelScope.launch {
            fileManager.files.collect { files ->
                _uiState.value = _uiState.value.copy(
                    files = files
                )
            }
        }
    }

    private fun loadStorageInfo() {
        val storageInfo = fileManager.getStorageInfo()
        _uiState.value = _uiState.value.copy(
            storageInfo = storageInfo
        )
    }

    fun onFileClicked(file: com.autohub.launcher.data.local.FileItem) {
        if (file.isDirectory) {
            fileManager.navigateTo(file.path)
        } else {
            // Open file based on type
            openFile(file)
        }
    }

    fun onNavigationUp() {
        fileManager.navigateUp()
    }

    fun onHomeNavigation() {
        fileManager.navigateTo(fileManager.getDefaultPath())
    }

    fun onFileSelectionToggled(path: String) {
        fileManager.toggleFileSelection(path)
    }

    fun clearSelection() {
        fileManager.clearSelection()
    }

    fun onCreateFolder(folderName: String) {
        val success = fileManager.createFolder(folderName)
        if (success) {
            // Folder created successfully
        }
    }

    fun onDeleteSelected() {
        val success = fileManager.deleteSelectedFiles()
        if (success) {
            // Files deleted successfully
        }
    }

    fun onRenameFile(oldPath: String, newName: String) {
        val success = fileManager.renameFile(oldPath, newName)
        if (success) {
            // File renamed successfully
        }
    }

    fun onFileShare(path: String) {
        // Implement file sharing
    }

    fun onFileCopy(path: String) {
        // Implement file copy
    }

    fun onFileMove(path: String) {
        // Implement file move
    }

    private fun openFile(file: com.autohub.launcher.data.local.FileItem) {
        when {
            fileManager.isImageFile(file.name) -> {
                // Open image viewer
            }
            fileManager.isVideoFile(file.name) -> {
                // Open video player
            }
            fileManager.isAudioFile(file.name) -> {
                // Open audio player
            }
            fileManager.isDocumentFile(file.name) -> {
                // Open document viewer
            }
            fileManager.isArchiveFile(file.name) -> {
                // Extract archive
            }
            else -> {
                // Unknown file type
            }
        }
    }
}

data class FilesUiState(
    val currentPath: String = "",
    val files: List<com.autohub.launcher.data.local.FileItem> = emptyList(),
    val selectedFiles: Set<String> = emptySet(),
    val storageInfo: com.autohub.launcher.data.local.StorageInfo? = null,
    val isSelectionMode: Boolean = false,
    val showCreateFolderDialog: Boolean = false,
    val showRenameDialog: Boolean = false,
    val fileToRename: String? = null
)
