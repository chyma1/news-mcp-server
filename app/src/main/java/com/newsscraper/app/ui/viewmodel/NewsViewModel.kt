package com.newsscraper.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.newsscraper.app.data.model.NewsItem
import com.newsscraper.app.data.network.NewsApiService
import com.newsscraper.app.data.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for news screen
 */
data class NewsUiState(
    val newsItems: List<NewsItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All",
    val isOffline: Boolean = false
)

/**
 * ViewModel for news screen
 */
class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "NewsViewModel"
    }
    
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    val categories = listOf(
        "All", "World", "Politics", "Technology", "Business",
        "Science", "Health", "Sports", "Entertainment", "Environment", "Culture"
    )
    
    init {
        Log.d(TAG, "ViewModel initialized, loading news...")
        loadNews()
    }
    
    fun loadNews() {
        viewModelScope.launch {
            Log.d(TAG, "loadNews started")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Log the server URL being used
            Log.d(TAG, "Using server: ${NewsApiService.serverUrl}")
            
            delay(500)
            
            // Get ALL items from API
            val result = repository.refreshNews(force = true)
            
            result.onSuccess { items ->
                Log.d(TAG, "Success: ${items.size} items")
                _uiState.value = _uiState.value.copy(
                    newsItems = filterByCategory(items, _uiState.value.selectedCategory),
                    isLoading = false,
                    isOffline = false,
                    error = null
                )
            }.onFailure { error ->
                Log.e(TAG, "Failed: ${error.message}")
                
                val cached = repository.getCachedNews()
                if (cached.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        newsItems = filterByCategory(cached, _uiState.value.selectedCategory),
                        isLoading = false,
                        isOffline = true,
                        error = "Offline: ${error.message}"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOffline = true,
                        error = error.message ?: "Failed to load news"
                    )
                }
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            delay(300)
            
            val result = repository.refreshNews(force = true)
            
            result.onSuccess { items ->
                _uiState.value = _uiState.value.copy(
                    newsItems = filterByCategory(items, _uiState.value.selectedCategory),
                    isRefreshing = false,
                    isOffline = false,
                    error = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = error.message
                )
            }
        }
    }
    
    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        
        val currentItems = _uiState.value.newsItems
        _uiState.value = _uiState.value.copy(
            newsItems = filterByCategory(currentItems, category)
        )
    }
    
    private fun filterByCategory(items: List<NewsItem>, category: String): List<NewsItem> {
        return if (category == "All") {
            items
        } else {
            items.filter { it.category == category }
        }
    }
    
    fun markAsSeen(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.markAsSeen(listOf(itemId))
            } catch (e: Exception) {
                Log.e(TAG, "markAsSeen error: ${e.message}")
            }
        }
    }
    
    class Factory(private val repository: NewsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
                return NewsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
