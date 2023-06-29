package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import javax.inject.Inject

class GetLeaguesUseCase @Inject constructor(
    private val sportsRepository: SportsRepository
) {

    suspend operator fun invoke(): Result<List<League>> {
        val result = sportsRepository.getLeagues()
        val resultValue = result.getOrNull()
        return if (result.isSuccess && resultValue != null) {
            Result.success(resultValue.map {
                League(
                    id = it.idLeague ?: -1,
                    name = it.strLeague ?: ""
                )
            })
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Exception is null"))
        }
    }
}