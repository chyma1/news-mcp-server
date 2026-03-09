package com.newsscraper.app

import android.app.Application
import android.util.Log

/**
 * Application class for News Scraper
 */
class NewsScraperApp : Application() {
    
    companion object {
        private const val TAG = "NewsScraperApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "NewsScraperApp started")
    }
}
