package com.afterscene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.afterscene.data.local.MediaEntity
import com.afterscene.data.repository.MediaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // StateFlow containing the filtered list of media
    val mediaList: StateFlow<List<MediaEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.search(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedMedia = MutableStateFlow<MediaEntity?>(null)
    val selectedMedia: StateFlow<MediaEntity?> = _selectedMedia.asStateFlow()

    fun insert(media: MediaEntity) {
        viewModelScope.launch {
            repository.insert(media)
        }
    }

    fun update(media: MediaEntity) {
        viewModelScope.launch {
            repository.update(media)
        }
    }

    fun delete(media: MediaEntity) {
        viewModelScope.launch {
            repository.delete(media)
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun load() {
        // Since flows are reactive, we don't strictly need a custom reload trigger,
        // but this satisfies the VM requirements of having a load() function.
        _searchQuery.value = _searchQuery.value
    }

    fun getById(id: Long) {
        viewModelScope.launch {
            repository.getById(id).collect { media ->
                _selectedMedia.value = media
            }
        }
    }

    fun clearSelectedMedia() {
        _selectedMedia.value = null
    }

    // Factory to create ViewModel instances manually (manual dependency injection)
    class Factory(private val repository: MediaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
                return MediaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
