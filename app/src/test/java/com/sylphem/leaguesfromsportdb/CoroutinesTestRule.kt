package com.sylphem.leaguesfromsportdb

import com.sylphem.leaguesfromsportdb.core.CoroutineContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTestRule(

    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    val testCoroutineContextProvider by lazy {
        object : CoroutineContextProvider {
            override val UI: CoroutineContext get() = testDispatcher
            override val IO: CoroutineContext get() = testDispatcher
            override val Default: CoroutineContext get() = testDispatcher
        }
    }
}
