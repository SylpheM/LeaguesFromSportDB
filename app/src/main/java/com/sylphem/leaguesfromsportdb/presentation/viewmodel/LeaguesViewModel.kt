package com.sylphem.leaguesfromsportdb.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylphem.leaguesfromsportdb.R
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.usecase.GetLeaguesUseCase
import com.sylphem.leaguesfromsportdb.domain.usecase.GetTeamsUseCase
import com.sylphem.leaguesfromsportdb.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor(

    private val getLeaguesUseCase: GetLeaguesUseCase,
    private val getTeamsUseCase: GetTeamsUseCase

) : ViewModel() {

    private val _searchValueFlow = MutableStateFlow("")
    private val _leaguesListFlow = MutableStateFlow<List<League>>(emptyList())
    private val _suggestionsListFlow = MutableStateFlow<List<League>>(emptyList())
    private val _teamsListFlow = MutableStateFlow<List<Team>>(emptyList())
    private val _loadingFlow = MutableStateFlow(true)
    private val _errorFlow = MutableStateFlow<Int?>(null)

    val screenStateFlow: StateFlow<ScreenState> =
        combine(
            _searchValueFlow,
            _suggestionsListFlow,
            _teamsListFlow,
            _loadingFlow,
            _errorFlow
        ) { searchValue, suggestionsList, teamsList, loading, error ->
            if (error != null) {
                ScreenState.Error(searchValue, error)
            } else if (loading) {
                ScreenState.Loading(searchValue)
            } else if (teamsList.isNotEmpty()) {
                ScreenState.Teams(searchValue, teamsList)
            } else {
                ScreenState.Suggestions(searchValue, suggestionsList)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ScreenState.Loading("")
        )

    fun loadData() {
        viewModelScope.launch {
            _loadingFlow.value = true
            val result = withContext(Dispatchers.IO) {
                getLeaguesUseCase()
            }
            _loadingFlow.value = false
            when (result) {
                is Result.Success -> {
                    _leaguesListFlow.value = result.value
                }
                is Result.Failure -> {
                    _errorFlow.value = R.string.get_leagues_fail
                    Log.w(
                        "LeaguesViewModel",
                        "Failed to get leagues " + result.error.localizedMessage
                    )
                }
            }
        }
    }

    fun onSearch(searchValue: String) {
        _searchValueFlow.value = searchValue
        _errorFlow.value = null
        _teamsListFlow.value = emptyList()
        viewModelScope.launch {
            _suggestionsListFlow.value = if (searchValue.isBlank()) {
                emptyList()
            } else {
                _leaguesListFlow.value.filter { it.name.contains(searchValue, ignoreCase = true) }
            }
        }
    }

    fun onLeagueSelected(league: League) {
        _searchValueFlow.value = league.name
        _errorFlow.value = null
        viewModelScope.launch {
            _loadingFlow.value = true
            val result = withContext(Dispatchers.IO) {
                getTeamsUseCase(league.name)
            }
            _loadingFlow.value = false
            when (result) {
                is Result.Success -> {
                    _teamsListFlow.value = result.value
                }
                is Result.Failure -> {
                    _errorFlow.value = R.string.get_teams_fail
                    Log.w(
                        "LeaguesViewModel",
                        "Failed to get teams " + result.error.localizedMessage
                    )
                }
            }
        }
    }

    fun onClearSearch() {
        _searchValueFlow.value = ""
        _teamsListFlow.value = emptyList()
        _suggestionsListFlow.value = emptyList()
        _errorFlow.value = null
    }
}