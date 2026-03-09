package com.newsscraper.app.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Network service for News API
 */
object NewsApiService {
    
    private const val TAG = "NewsApiService"
    
    // Server URL - change this to your deployed server URL
    // Local testing: "http://10.56.111.12:16209"
    // Deployed on Render: "https://your-app-name.onrender.com"
    var serverUrl: String = "http://10.56.111.12:16209"
    
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
    init {
        Log.i(TAG, "API Service initialized with server: $serverUrl")
    }
    
    private fun log(msg: String) {
        Log.d(TAG, "${dateFormat.format(Date())}: $msg")
    }
    
    /**
     * Make HTTP request
     */
    private fun makeRequest(endpoint: String, method: String = "GET", body: String? = null): String? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        
        try {
            val fullUrl = "$serverUrl$endpoint"
            log("Connecting to: $fullUrl")
            
            val url = URL(fullUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.connectTimeout = 60000  // 60 seconds
            connection.readTimeout = 60000
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            
            if (body != null && method == "POST") {
                connection.doOutput = true
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(body)
                    writer.flush()
                }
            }
            
            val responseCode = connection.responseCode
            log("Response code: $responseCode")
            
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log("Error response: $responseCode")
                return null
            }
            
            reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            
            val responseText = response.toString()
            log("Response length: ${responseText.length}")
            
            return responseText
        } catch (e: Exception) {
            log("Error: ${e.message}")
            return null
        } finally {
            try { reader?.close() } catch (e: Exception) {}
            try { connection?.disconnect() } catch (e: Exception) {}
        }
    }
    
    /**
     * Refresh news - get all items from server
     */
    suspend fun refreshNews(force: Boolean = false): Result<NewsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                log("Refreshing news from: $serverUrl...")
                
                val endpoint = if (force) "/api/refresh?force=true" else "/api/refresh"
                val responseText = makeRequest(endpoint)
                
                if (responseText.isNullOrEmpty()) {
                    log("Empty response from server!")
                    return@withContext Result.failure(Exception("Empty response from server. Please check if server is running."))
                }
                
                try {
                    log("Parsing JSON...")
                    val newsResponse = gson.fromJson(responseText, NewsResponse::class.java)
                    log("Parsed ${newsResponse.items.size} items")
                    
                    if (newsResponse.items.isEmpty()) {
                        log("No items in response!")
                        return@withContext Result.failure(Exception("No news available"))
                    }
                    
                    Result.success(newsResponse)
                } catch (e: JsonSyntaxException) {
                    log("Parse error: ${e.message}")
                    Result.failure(Exception("Parse error: ${e.message}"))
                }
            } catch (e: Exception) {
                log("Exception: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get news items
     */
    suspend fun getNews(): Result<NewsResponse> {
        return refreshNews()
    }
    
    /**
     * Mark items as seen
     */
    suspend fun markItemsSeen(itemIds: List<String>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = gson.toJson(mapOf("item_ids" to itemIds))
                val response = makeRequest("/api/mark-seen", "POST", body)
                
                if (response != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
