package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.ShowDataSource
import com.nexters.boolti.data.network.response.toDomains
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.repository.ShowRepository
import javax.inject.Inject

class ShowRepositoryImpl @Inject constructor(
    private val showDateSource: ShowDataSource,
) : ShowRepository {
    override suspend fun search(keyword: String): Result<List<Show>> {
        return showDateSource.search(keyword = keyword).map {
            it.toDomains()
        }
    }
}