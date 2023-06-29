package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.data.model.RemoteTeam
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.model.Team
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GetTeamsUseCaseTest {

    @Test
    fun getTeamsSuccess() = runBlocking {
        /* Given */
        val mockSportsRepository = mock<SportsRepository> {
            onBlocking { getTeams("toto") } doReturn Result.Success(
                listOf(
                    RemoteTeam(
                        idTeam = 1,
                        strTeam = "AAA",
                        strTeamLogo = "logoAAA"
                    ),
                    RemoteTeam(
                        idTeam = 2,
                        strTeam = "BBB",
                        strTeamLogo = "logoBBB"
                    ),
                    RemoteTeam(
                        idTeam = 3,
                        strTeam = "CCC",
                        strTeamLogo = "logoCCC"
                    )
                )
            )
        }
        val getTeamsUseCase = GetTeamsUseCase(mockSportsRepository)

        /* When */
        val result = getTeamsUseCase("toto")

        /* Then */
        verify(mockSportsRepository).getTeams("toto")
        assertTrue(result is Result.Success)
        result as Result.Success
        val teamsList = result.value
        assertEquals(
            teamsList,
            listOf(
                Team(id = 3, name = "CCC", logo = "logoCCC"),
                Team(id = 1, name = "AAA", logo = "logoAAA")
            )
        )
    }

    @Test
    fun getTeamsFailure() = runBlocking {
        /* Given */
        val exception = Exception()
        val mockSportsRepository = mock<SportsRepository> {
            onBlocking { getTeams("toto") } doReturn Result.Failure(exception)
        }
        val getTeamsUseCase = GetTeamsUseCase(mockSportsRepository)

        /* When */
        val result = getTeamsUseCase("toto")

        /* Then */
        verify(mockSportsRepository).getTeams("toto")
        assertTrue(result is Result.Failure)
        result as Result.Failure
        assertEquals(result.error, exception)
    }
}