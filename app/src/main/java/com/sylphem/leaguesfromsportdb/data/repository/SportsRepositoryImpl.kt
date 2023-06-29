package com.sylphem.leaguesfromsportdb.data.repository

import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam
import com.sylphem.leaguesfromsportdb.data.remote.SportsApiService
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import javax.inject.Inject

class SportsRepositoryImpl @Inject constructor(

    private val sportsApiService: SportsApiService

) : SportsRepository {

    override suspend fun getLeagues(): Result<List<RemoteLeague>> {
        return try {
            Result.Success(sportsApiService.getLeagues().leagues)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }
    }

    override suspend fun getTeams(leagueName: String): Result<List<RemoteTeam>> {
        return try {
            Result.Success(sportsApiService.getTeams(leagueName).teams)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }
    }

}