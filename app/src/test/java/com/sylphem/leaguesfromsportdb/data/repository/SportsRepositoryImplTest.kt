package com.sylphem.leaguesfromsportdb.data.repository

import com.sylphem.leaguesfromsportdb.data.model.LeaguesResponse
import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam
import com.sylphem.leaguesfromsportdb.data.model.TeamsResponse
import com.sylphem.leaguesfromsportdb.data.remote.SportsApiService
import com.sylphem.leaguesfromsportdb.domain.model.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class SportsRepositoryImplTest {

    @Test
    fun getLeaguesSuccess() = runTest {
        /* Given */
        val remoteLeagues = listOf(
            RemoteLeague(
                idLeague = 1,
                strLeague = "AAA"
            ),
            RemoteLeague(
                idLeague = 2,
                strLeague = "BBB"
            ),
            RemoteLeague(
                idLeague = 3,
                strLeague = "CCC"
            )
        )
        val mockApiService = mock<SportsApiService> {
            onBlocking { getLeagues() } doReturn LeaguesResponse(
                leagues = remoteLeagues
            )
        }
        val sportsRepository = SportsRepositoryImpl(mockApiService)

        /* When */
        val result = sportsRepository.getLeagues()

        /* Then */
        verify(mockApiService).getLeagues()
        assertTrue(result is Result.Success)
        result as Result.Success
        val leaguesList = result.value
        assertEquals(remoteLeagues, leaguesList)
    }

    @Test
    fun getLeaguesFailure() = runTest {
        /* Given */
        val exception = Exception()
        val mockApiService = mock<SportsApiService> {
            onBlocking { getLeagues() } doAnswer { throw exception }
        }
        val sportsRepository = SportsRepositoryImpl(mockApiService)

        /* When */
        val result = sportsRepository.getLeagues()

        /* Then */
        verify(mockApiService).getLeagues()
        assertTrue(result is Result.Failure)
        result as Result.Failure
        assertEquals(exception, result.error)
    }

    @Test
    fun getTeamsSuccess() = runTest {
        /* Given */
        val remoteTeams = listOf(
            RemoteTeam(
                idTeam = 1,
                strTeam = "AAA",
                strTeamBadge = "logoAAA"
            ),
            RemoteTeam(
                idTeam = 2,
                strTeam = "BBB",
                strTeamBadge = "logoBBB"
            ),
            RemoteTeam(
                idTeam = 3,
                strTeam = "CCC",
                strTeamBadge = "logoCCC"
            )
        )
        val mockApiService = mock<SportsApiService> {
            onBlocking { getTeams("toto") } doReturn TeamsResponse(
                teams = remoteTeams
            )
        }
        val sportsRepository = SportsRepositoryImpl(mockApiService)

        /* When */
        val result = sportsRepository.getTeams("toto")

        /* Then */
        verify(mockApiService).getTeams("toto")
        assertTrue(result is Result.Success)
        result as Result.Success
        val teamsList = result.value
        assertEquals(remoteTeams, teamsList)
    }

    @Test
    fun getTeamsFailure() = runTest {
        /* Given */
        val exception = Exception()
        val mockApiService = mock<SportsApiService> {
            onBlocking { getTeams("toto") } doAnswer { throw exception }
        }
        val sportsRepository = SportsRepositoryImpl(mockApiService)

        /* When */
        val result = sportsRepository.getTeams("toto")

        /* Then */
        verify(mockApiService).getTeams("toto")
        assertTrue(result is Result.Failure)
        result as Result.Failure
        assertEquals(exception, result.error)
    }

    @Test
    fun getTeamsFailureWhenTeamsIsNull() = runTest {
        /* Given */
        val mockApiService = mock<SportsApiService> {
            onBlocking { getTeams("toto") } doReturn TeamsResponse(null)
        }
        val sportsRepository = SportsRepositoryImpl(mockApiService)

        /* When */
        val result = sportsRepository.getTeams("toto")

        /* Then */
        verify(mockApiService).getTeams("toto")
        assertTrue(result is Result.Failure)
        result as Result.Failure
        assertEquals("No data for this league", result.error.message)
    }
}