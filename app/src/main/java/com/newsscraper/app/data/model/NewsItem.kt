package com.newsscraper.app.data.model

/**
 * Data model representing a news article
 * Uses snake_case to match the server's JSON response
 */
data class NewsItem(
    val id: String,
    val headline: String,
    val summary: String,
    val source: String,
    val source_url: String,
    val published_at: String,
    val scraped_at: String,
    val category: String,
    val region: String,
    val image_url: String?,
    val is_breaking: Boolean,
    val is_update: Boolean,
    val read_time_seconds: Int
)
