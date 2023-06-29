package com.sylphem.leaguesfromsportdb.domain.repository

import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam

import com.sylphem.leaguesfromsportdb.domain.model.Result

interface SportsRepository {

    suspend fun getLeagues(): Result<List<RemoteLeague>>

    suspend fun getTeams(leagueName: String): Result<List<RemoteTeam>>

}