package com.sylphem.leaguesfromsportdb.data.repository

import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam
import com.sylphem.leaguesfromsportdb.data.remote.SportsApiService
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository

class SportsRepositoryImpl constructor(

    private val sportsApiService: SportsApiService

) : SportsRepository {

    override suspend fun getLeagues(): Result<List<RemoteLeague>> {
        return try {
            Result.success(sportsApiService.getLeagues().leagues)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getTeams(leagueName: String): Result<List<RemoteTeam>> {
        return try {
            Result.success(sportsApiService.getTeams(leagueName).teams)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}