package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import javax.inject.Inject

class GetLeaguesUseCase @Inject constructor(
    private val sportsRepository: SportsRepository
) {

    suspend operator fun invoke(): Result<List<League>> =
        when (val result: Result<List<RemoteLeague>> = sportsRepository.getLeagues()) {
            is Result.Success ->
                Result.Success(result.value.map {
                    League(
                        id = it.idLeague ?: -1,
                        name = it.strLeague ?: ""
                    )
                })
            is Result.Failure -> Result.Failure(result.error)
        }
}