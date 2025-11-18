package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User

interface SearchRepository {
    suspend fun getNewShowsAndRisingKeywords(): Result<NewShowsAndRisingKeywords>
    suspend fun getRecommendKeyword(searchKeyword: String): Result<List<String>>
    suspend fun searchShows(keyword: String, page: Int): Result<PagingData<Show>>
    suspend fun searchProfiles(keyword: String, page: Int): Result<PagingData<User.Others>>
}
