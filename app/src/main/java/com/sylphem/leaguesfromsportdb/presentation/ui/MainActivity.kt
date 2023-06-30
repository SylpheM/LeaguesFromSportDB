package com.sylphem.leaguesfromsportdb.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.sylphem.leaguesfromsportdb.presentation.ui.MainScreen
import com.sylphem.leaguesfromsportdb.presentation.ui.theme.LeaguesFromSportDBTheme
import com.sylphem.leaguesfromsportdb.presentation.viewmodel.LeaguesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LeaguesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadData()
        lifecycleScope.launch {
            viewModel.screenStateFlow.collect { screenState ->
                setContent {
                    LeaguesFromSportDBTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            MainScreen(
                                screenState = screenState,
                                onSearchChange = {
                                    viewModel.onSearch(it)
                                },
                                onClearSearch = {
                                    viewModel.onClearSearch()
                                },
                                onLeagueSelected = {
                                    viewModel.onLeagueSelected(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
