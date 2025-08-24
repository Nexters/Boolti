package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.ShowDataSource
import com.nexters.boolti.domain.model.CastTeams
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.repository.ShowRepository
import javax.inject.Inject

internal class ShowRepositoryImpl @Inject constructor(
    private val showDateSource: ShowDataSource,
) : ShowRepository {
    override suspend fun search(keyword: String): Result<List<Show>> {
        return showDateSource.search(keyword = keyword).map {
            it.map { show -> show.toDomain() }
        }
    }

    override suspend fun searchById(id: String): Result<ShowDetail> {
        return showDateSource.findShowById(id = id).map {
            it.toDomain()
        }
    }

    override suspend fun requestCastTeams(id: String): Result<List<CastTeams>> {
        /*return Result.success(
            listOf(
                CastTeams(
                    teamName = "Salty&Sweet",
                    members = listOf(
                        Cast(nickname = "김불티", roleName = "보컬"),
                        Cast(nickname = "박불티", roleName = "기타"),
                        Cast(nickname = "이불티", roleName = "기타"),
                        Cast(nickname = "송불티", roleName = "키보드"),
                        Cast(nickname = "장불티", roleName = "베이스"),
                        Cast(nickname = "최불티", roleName = "드럼"),
                    ),
                ),
                CastTeams(
                    teamName = "0v0",
                    members = listOf(
                        Cast(nickname = "이불티", roleName = "보컬 & 키보드"),
                        Cast(nickname = "송불티", roleName = "보컬 & 키보드 & 드럼 & 베이스"),
                        Cast(nickname = "장불티", roleName = "베이스"),
                        Cast(nickname = "최불티", roleName = "드럼"),
                    ),
                ),
                CastTeams(
                    teamName = "125",
                )
            )
        )*/
        return showDateSource.requestCastTeams(id).map {
            it.map { it.toDomain() }
        }
    }
}
