package com.sylphem.leaguesfromsportdb.presentation

import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Team

sealed class ScreenState(val searchValue: String) {

    data class Loading(val searchVal: String) : ScreenState(searchVal)

    data class Suggestions(val searchVal: String, val leagues: List<League>) :
        ScreenState(searchVal)

    data class Teams(val searchVal: String, val teams: List<Team>) : ScreenState(searchVal)
}