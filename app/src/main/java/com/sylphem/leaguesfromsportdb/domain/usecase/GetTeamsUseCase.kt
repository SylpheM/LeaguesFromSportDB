package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import javax.inject.Inject

class GetTeamsUseCase @Inject constructor(
    private val sportsRepository: SportsRepository
) {

    suspend operator fun invoke(leagueName: String): Result<List<Team>> =
        when (val result: Result<List<RemoteTeam>> = sportsRepository.getTeams(leagueName)) {
            is Result.Success -> {
                Result.Success(result.value.map {
                    Team(
                        id = it.idTeam ?: -1,
                        name = it.strTeam ?: "",
                        logo = it.strTeamLogo
                    )
                }
                    .filterIndexed { index, _ -> index % 2 == 0 }
                    .sortedByDescending { it.name })
            }
            is Result.Failure -> Result.Failure(result.error)
        }
}