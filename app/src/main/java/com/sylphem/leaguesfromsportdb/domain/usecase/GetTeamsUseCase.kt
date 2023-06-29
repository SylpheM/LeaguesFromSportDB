package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository

class GetTeamsUseCase constructor(
    private val sportsRepository: SportsRepository
) {

    suspend operator fun invoke(leagueName: String): Result<List<Team>> {
        val result = sportsRepository.getTeams(leagueName)
        val resultValue = result.getOrNull()
        return if (result.isSuccess && resultValue != null) {
            Result.success(resultValue.map {
                Team(
                    id = it.idTeam ?: -1,
                    name = it.strTeam ?: "",
                    logo = it.strTeamLogo
                )
            })
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Exception is null"))
        }
    }
}