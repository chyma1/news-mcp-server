package com.newsscraper.app.data.repository

import android.util.Log
import com.newsscraper.app.data.model.NewsItem
import com.newsscraper.app.data.network.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for news data
 */
class NewsRepository {
    
    companion object {
        private const val TAG = "NewsRepository"
    }
    
    // In-memory cache
    private var cachedItems: List<NewsItem> = emptyList()
    
    private fun log(tag: String, message: String) {
        Log.d(TAG, "$tag: $message")
    }
    
    /**
     * Refresh news from network - get all items
     */
    suspend fun refreshNews(force: Boolean = false): Result<List<NewsItem>> {
        return withContext(Dispatchers.IO) {
            try {
                log("refreshNews", "Starting refresh...")
                
                // Request all items
                val result = NewsApiService.refreshNews(force = force)
                
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    if (response != null && response.items.isNotEmpty()) {
                        cachedItems = response.items
                        log("refreshNews", "Loaded ${response.items.size} items successfully")
                        Result.success(response.items)
                    } else {
                        if (cachedItems.isNotEmpty()) {
                            log("refreshNews", "Using cached ${cachedItems.size} items")
                            Result.success(cachedItems)
                        } else {
                            log("refreshNews", "No items available")
                            Result.failure(Exception("No news available"))
                        }
                    }
                } else {
                    val error = result.exceptionOrNull()
                    log("refreshNews", "API failed: ${error?.message}")
                    
                    if (cachedItems.isNotEmpty()) {
                        log("refreshNews", "Using cached items")
                        Result.success(cachedItems)
                    } else {
                        Result.failure(error ?: Exception("Unknown error"))
                    }
                }
            } catch (e: Exception) {
                log("refreshNews", "Exception: ${e.message}")
                
                if (cachedItems.isNotEmpty()) {
                    log("refreshNews", "Using cached items after error")
                    Result.success(cachedItems)
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    /**
     * Get cached news
     */
    fun getCachedNews(): List<NewsItem> {
        log("getCachedNews", "Returning ${cachedItems.size} cached items")
        return cachedItems
    }
    
    /**
     * Mark item as seen
     */
    suspend fun markAsSeen(itemIds: List<String>) {
        withContext(Dispatchers.IO) {
            try {
                log("markAsSeen", "Marking ${itemIds.size} items as seen")
                NewsApiService.markItemsSeen(itemIds)
            } catch (e: Exception) {
                log("markAsSeen", "Error: ${e.message}")
            }
        }
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        cachedItems = emptyList()
        log("clearCache", "Cache cleared")
    }
    
    /**
     * Get news count
     */
    fun getNewsCount(): Int = cachedItems.size
}
