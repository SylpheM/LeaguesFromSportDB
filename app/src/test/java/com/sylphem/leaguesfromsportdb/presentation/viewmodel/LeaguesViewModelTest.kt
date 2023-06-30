package com.sylphem.leaguesfromsportdb.presentation.viewmodel

import com.sylphem.leaguesfromsportdb.CoroutinesTestRule
import com.sylphem.leaguesfromsportdb.R
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.usecase.GetLeaguesUseCase
import com.sylphem.leaguesfromsportdb.domain.usecase.GetTeamsUseCase
import com.sylphem.leaguesfromsportdb.presentation.ui.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.then

@OptIn(ExperimentalCoroutinesApi::class)
internal class LeaguesViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val mockGetLeaguesUseCase: GetLeaguesUseCase = mock()
    private val mockGetTeamsUseCase: GetTeamsUseCase = mock()

    private lateinit var leaguesViewModel: LeaguesViewModel

    @Before
    fun before() {
        leaguesViewModel = LeaguesViewModel(
            mockGetLeaguesUseCase,
            mockGetTeamsUseCase,
            coroutinesTestRule.testCoroutineContextProvider
        )
    }

    @Test
    fun loadDataSuccess() = runTest {
        /* Given */
        val mockList = mock<List<League>>()
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(mockList))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        job.cancel()
    }

    @Test
    fun loadDataFailure() = runTest {
        /* Given */
        val exception = Exception()
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Failure(exception))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        assertEquals(ScreenState.Error("", R.string.get_leagues_fail), results[1])
        job.cancel()
    }

    @Test
    fun searchSuccess() = runTest {
        /* Given */
        val leaguesList = listOf(
            League(id = 1, name = "AAA"),
            League(id = 2, name = "BBA"),
            League(id = 3, name = "CCC")
        )
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(leaguesList))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        leaguesViewModel.onSearch("A")
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        assertEquals(
            ScreenState.Suggestions("A", emptyList()), results[2]
        )
        assertEquals(
            ScreenState.Suggestions(
                "A", listOf(
                    League(id = 1, name = "AAA"),
                    League(id = 2, name = "BBA"),
                )
            ), results[3]
        )
        job.cancel()
    }

    @Test
    fun selectLeagueSuccess() = runTest {
        /* Given */
        val leagueA = League(id = 1, name = "AAA")
        val leaguesList = listOf(
            leagueA,
            League(id = 2, name = "BBA"),
            League(id = 3, name = "CCC")
        )
        val teamsList = listOf(
            Team(id = 3, name = "CCC", logo = "logoCCC"),
            Team(id = 1, name = "AAA", logo = "logoAAA")
        )
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(leaguesList))
        given(mockGetTeamsUseCase.invoke("AAA")).willReturn(Result.Success(teamsList))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        leaguesViewModel.onSearch("A")
        leaguesViewModel.onLeagueSelected(leagueA)
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        then(mockGetTeamsUseCase).should().invoke("AAA")
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        assertEquals(
            ScreenState.Suggestions("A", emptyList()), results[2]
        )
        assertEquals(
            ScreenState.Suggestions(
                "A", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[3]
        )
        assertEquals(
            ScreenState.Suggestions(
                "AAA", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[4]
        )
        assertEquals(
            ScreenState.Teams("AAA", teamsList), results[5]
        )
        job.cancel()
    }

    @Test
    fun selectLeagueFailure() = runTest {
        /* Given */
        val exception = Exception()
        val leagueA = League(id = 1, name = "AAA")
        val leaguesList = listOf(
            leagueA,
            League(id = 2, name = "BBA"),
            League(id = 3, name = "CCC")
        )
        val teamsList = listOf(
            Team(id = 3, name = "CCC", logo = "logoCCC"),
            Team(id = 1, name = "AAA", logo = "logoAAA")
        )
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(leaguesList))
        given(mockGetTeamsUseCase.invoke("AAA")).willReturn(Result.Failure(exception))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        leaguesViewModel.onSearch("A")
        leaguesViewModel.onLeagueSelected(leagueA)
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        then(mockGetTeamsUseCase).should().invoke("AAA")
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        assertEquals(
            ScreenState.Suggestions("A", emptyList()), results[2]
        )
        assertEquals(
            ScreenState.Suggestions(
                "A", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[3]
        )
        assertEquals(
            ScreenState.Suggestions(
                "AAA", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[4]
        )
        assertEquals(
            ScreenState.Error("AAA", R.string.get_teams_fail), results[5]
        )
        job.cancel()
    }

    @Test
    fun onClearSearchSuccess() = runTest {
        /* Given */
        val leaguesList = listOf(
            League(id = 1, name = "AAA"),
            League(id = 2, name = "BBA"),
            League(id = 3, name = "CCC")
        )
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(leaguesList))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        leaguesViewModel.onSearch("A")
        leaguesViewModel.onClearSearch()
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        assertEquals(
            ScreenState.Suggestions("A", emptyList()), results[2]
        )
        assertEquals(
            ScreenState.Suggestions(
                "A", listOf(
                    League(id = 1, name = "AAA"),
                    League(id = 2, name = "BBA"),
                )
            ), results[3]
        )
        assertEquals(
            ScreenState.Suggestions(
                "", listOf(
                    League(id = 1, name = "AAA"),
                    League(id = 2, name = "BBA"),
                )
            ), results[4]
        )
        assertEquals(
            ScreenState.Suggestions("", emptyList()), results[5]
        )
        job.cancel()
    }


    @Test
    fun onClearSearchAfterSelectingLeagueSuccess() = runTest {
        /* Given */
        val leagueA = League(id = 1, name = "AAA")
        val leaguesList = listOf(
            leagueA,
            League(id = 2, name = "BBA"),
            League(id = 3, name = "CCC")
        )
        val teamsList = listOf(
            Team(id = 3, name = "CCC", logo = "logoCCC"),
            Team(id = 1, name = "AAA", logo = "logoAAA")
        )
        given(mockGetLeaguesUseCase.invoke()).willReturn(Result.Success(leaguesList))
        given(mockGetTeamsUseCase.invoke("AAA")).willReturn(Result.Success(teamsList))

        val results = mutableListOf<ScreenState>()
        val job = launch(coroutinesTestRule.testDispatcher) {
            leaguesViewModel.screenStateFlow.toList(results)
        }

        /* Then */
        assertEquals(ScreenState.Loading(""), results[0])

        /* When */
        leaguesViewModel.loadData()
        leaguesViewModel.onSearch("A")
        leaguesViewModel.onLeagueSelected(leagueA)
        leaguesViewModel.onClearSearch()
        runCurrent()

        /* Then */
        then(mockGetLeaguesUseCase).should().invoke()
        then(mockGetTeamsUseCase).should().invoke("AAA")
        assertEquals(ScreenState.Suggestions("", emptyList()), results[1])
        assertEquals(
            ScreenState.Suggestions("A", emptyList()), results[2]
        )
        assertEquals(
            ScreenState.Suggestions(
                "A", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[3]
        )
        assertEquals(
            ScreenState.Suggestions(
                "AAA", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[4]
        )
        assertEquals(
            ScreenState.Teams("AAA", teamsList), results[5]
        )
        assertEquals(
            ScreenState.Teams("", teamsList), results[6]
        )
        assertEquals(
            ScreenState.Suggestions(
                "", listOf(
                    leagueA,
                    League(id = 2, name = "BBA"),
                )
            ), results[7]
        )
        assertEquals(
            ScreenState.Suggestions("", emptyList()), results[8]
        )
        job.cancel()
    }
}
