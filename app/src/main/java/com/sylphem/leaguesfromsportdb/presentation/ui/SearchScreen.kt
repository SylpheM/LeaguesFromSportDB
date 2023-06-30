package com.sylphem.leaguesfromsportdb.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sylphem.leaguesfromsportdb.R
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.presentation.ui.theme.LeaguesFromSportDBTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    screenState: ScreenState,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onLeagueSelected: (League) -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchField(
            searchValue = screenState.searchValue,
            onValueChange = onSearchChange,
            onClear = onClearSearch,
            focusRequester = focusRequester
        )

        when (screenState) {
            is ScreenState.Error -> {
                Icon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imageVector = Icons.Default.Warning,
                    contentDescription = "",
                    tint = MaterialTheme.colors.primary
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    text = stringResource(id = screenState.errorMessage)
                )
            }
            is ScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(46.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

            }
            is ScreenState.Suggestions -> {
                LazyColumn {
                    items(screenState.leagues) { league ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    onLeagueSelected(league)
                                    focusManager.clearFocus()
                                },
                            text = league.name
                        )
                    }
                }
            }
            is ScreenState.Teams ->
                LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                    items(screenState.teams) { team ->
                        AsyncImage(
                            modifier = Modifier.padding(horizontal = 32.dp, 16.dp),
                            model = team.logo,
                            contentDescription = team.name
                        )
                    }
                }
        }
    }
}

@Composable
fun SearchField(
    searchValue: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    focusRequester: FocusRequester
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .background(MaterialTheme.colors.background),
        value = searchValue,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(id = R.string.search_by_league))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = MaterialTheme.colors.primary
            )
        },
        trailingIcon = {
            if (searchValue.isNotBlank()) {
                Icon(
                    modifier = Modifier.clickable { onClear() },
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenLoadingPreview() {
    LeaguesFromSportDBTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            MainScreen(
                screenState = ScreenState.Loading(""),
                onSearchChange = { },
                onClearSearch = { },
                onLeagueSelected = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenSuggestionsPreview() {
    LeaguesFromSportDBTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            MainScreen(
                screenState = ScreenState.Suggestions(
                    "Ligue",
                    listOf(
                        League(id = 1, name = "Ligue 1"),
                        League(id = 2, name = "Ligue 2"),
                        League(id = 3, name = "Ligue 3")
                    )
                ),
                onSearchChange = { },
                onClearSearch = { },
                onLeagueSelected = { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenTeamsPreview() {
    LeaguesFromSportDBTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            MainScreen(
                screenState = ScreenState.Teams(
                    "Ligue 1",
                    listOf(
                        Team(id = 1, name = "Team 1", logo = ""),
                        Team(id = 2, name = "Team 2", logo = ""),
                        Team(id = 3, name = "Team 3", logo = "")
                    )
                ),
                onSearchChange = { },
                onClearSearch = { },
                onLeagueSelected = { }
            )
        }
    }
}