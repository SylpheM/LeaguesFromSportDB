package com.sylphem.leaguesfromsportdb.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.usecase.GetLeaguesUseCase
import com.sylphem.leaguesfromsportdb.domain.usecase.GetTeamsUseCase
import com.sylphem.leaguesfromsportdb.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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

    val screenStateFlow: StateFlow<ScreenState> =
        combine(
            _searchValueFlow,
            _suggestionsListFlow,
            _teamsListFlow,
            _loadingFlow
        ) { searchValue, suggestionsList, teamsList, loading ->
            if (loading) {
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
            when (result) {
                is Result.Success -> {
                    _loadingFlow.value = false
                    _leaguesListFlow.value = result.value
                }
                is Result.Failure -> {
                    Log.d("LeaguesViewModel", "Failed to get leagues")
                }
            }
        }
    }

    fun onSearch(searchValue: String) {
        _searchValueFlow.value = searchValue
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
        viewModelScope.launch {
            _loadingFlow.value = true
            val result = withContext(Dispatchers.IO) {
                getTeamsUseCase(league.name)
            }
            when (result) {
                is Result.Success -> {
                    _loadingFlow.value = false
                    _teamsListFlow.value = result.value
                }
                is Result.Failure -> {
                    Log.d("LeaguesViewModel", "Failed to get teams")
                }
            }
        }
    }

    fun onClearSearch() {
        _searchValueFlow.value = ""
        _teamsListFlow.value = emptyList()
        _suggestionsListFlow.value = emptyList()
    }
}