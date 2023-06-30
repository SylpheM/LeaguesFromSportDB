package com.sylphem.leaguesfromsportdb.domain.usecase

import com.sylphem.leaguesfromsportdb.data.model.RemoteLeague
import com.sylphem.leaguesfromsportdb.domain.model.League
import com.sylphem.leaguesfromsportdb.domain.model.Result
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetLeaguesUseCaseTest {
    @Test
    fun getLeaguesSuccess() = runTest {
        /* Given */
        val mockSportsRepository = mock<SportsRepository> {
            onBlocking { getLeagues() } doReturn Result.Success(
                listOf(
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
            )
        }
        val getLeaguesUseCase = GetLeaguesUseCase(mockSportsRepository)

        /* When */
        val result = getLeaguesUseCase()

        /* Then */
        verify(mockSportsRepository).getLeagues()
        assertTrue(result is Result.Success)
        result as Result.Success
        val leaguesList = result.value
        assertEquals(
            listOf(
                League(id = 1, name = "AAA"),
                League(id = 2, name = "BBB"),
                League(id = 3, name = "CCC")
            ), leaguesList
        )
    }

    @Test
    fun getLeaguesFailure() = runTest {
        /* Given */
        val exception = Exception()
        val mockSportsRepository = mock<SportsRepository> {
            onBlocking { getLeagues() } doReturn Result.Failure(exception)
        }
        val getLeaguesUseCase = GetLeaguesUseCase(mockSportsRepository)

        /* When */
        val result = getLeaguesUseCase()

        /* Then */
        verify(mockSportsRepository).getLeagues()
        assertTrue(result is Result.Failure)
        result as Result.Failure
        assertEquals(exception, result.error)
    }
}