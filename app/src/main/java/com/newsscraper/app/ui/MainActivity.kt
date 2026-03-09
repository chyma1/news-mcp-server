package com.newsscraper.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.newsscraper.app.data.network.NewsApiService
import com.newsscraper.app.data.repository.NewsRepository
import com.newsscraper.app.ui.screen.NewsScreen
import com.newsscraper.app.ui.viewmodel.NewsViewModel
import com.newsscraper.app.ui.theme.NewsScraperTheme

/**
 * Main Activity for the News Scraper App
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: NewsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize repository (no parameters needed - uses NewsApiService object)
        val repository = NewsRepository()
        
        // Create ViewModel
        val factory = NewsViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
        
        // Set up Compose UI
        setContent {
            NewsScraperTheme {
                val uiState by viewModel.uiState.collectAsState()
                
                NewsScreen(
                    newsItems = uiState.newsItems,
                    isLoading = uiState.isLoading,
                    isRefreshing = uiState.isRefreshing,
                    isOffline = uiState.isOffline,
                    error = uiState.error,
                    selectedCategory = uiState.selectedCategory,
                    categories = viewModel.categories,
                    onRefresh = { viewModel.refresh() },
                    onCategorySelected = { viewModel.selectCategory(it) },
                    onMarkAsSeen = { viewModel.markAsSeen(it) }
                )
            }
        }
    }
}
