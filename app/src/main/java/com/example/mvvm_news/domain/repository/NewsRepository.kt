package com.example.mvvm_news.domain.repository

import com.example.mvvm_news.data.model.ApiResponse

interface NewsRepository {

    suspend fun getNewsHeadlines(): ApiResponse
}