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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLeagueSelected(league)
                                focusManager.clearFocus()
                            }) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = league.name
                            )
                        }
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

class SampleScreenStateProvider : PreviewParameterProvider<ScreenState> {
    override val values: Sequence<ScreenState>
        get() = sequenceOf(
            ScreenState.Loading(""),
            ScreenState.Error("", R.string.get_leagues_fail),
            ScreenState.Suggestions(
                "Ligue",
                listOf(
                    League(id = 1, name = "Ligue 1"),
                    League(id = 2, name = "Ligue 2"),
                    League(id = 3, name = "Ligue 3")
                )
            ),
            ScreenState.Teams(
                "Ligue 1",
                listOf(
                    Team(
                        id = 1,
                        name = "Team 1",
                        logo = "https://www.thesportsdb.com/images/media/team/badge/z69be41598797026.png"
                    ),
                    Team(
                        id = 2,
                        name = "Team 2",
                        logo = "https://www.thesportsdb.com/images/media/team/badge/wrytst1426871249.png"
                    ),
                    Team(
                        id = 3,
                        name = "Team 3",
                        logo = "https://www.thesportsdb.com/images/media/team/badge/aikowk1546475003.png"
                    )
                )
            )
        )
}

@Preview(showBackground = true)
@Composable
fun MainScreenLoadingPreview(@PreviewParameter(SampleScreenStateProvider::class) data: ScreenState) {
    LeaguesFromSportDBTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            MainScreen(
                screenState = data,
                onSearchChange = { },
                onClearSearch = { },
                onLeagueSelected = { }
            )
        }
    }
}