package com.sylphem.leaguesfromsportdb.data.remote

import com.sylphem.leaguesfromsportdb.data.model.LeaguesResponse
import com.sylphem.leaguesfromsportdb.data.model.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApiService {

    @GET("/all_leagues.php")
    suspend fun getLeagues(): LeaguesResponse

    @GET("/search_all_teams.php")
    suspend fun getTeams(@Query("l") league: String): TeamsResponse

}