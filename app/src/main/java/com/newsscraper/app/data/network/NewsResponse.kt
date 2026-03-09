package com.newsscraper.app.data.network

import com.newsscraper.app.data.model.NewsItem

/**
 * Response wrapper for API calls
 * Uses snake_case to match the server's JSON response
 */
data class NewsResponse(
    val success: Boolean,
    val items: List<NewsItem> = emptyList(),
    val new_count: Int = 0,
    val total_count: Int = 0,
    val last_update: String? = null,
    val from_cache: Boolean = false,
    val errors: List<String> = emptyList(),
    val count: Int = 0
)
