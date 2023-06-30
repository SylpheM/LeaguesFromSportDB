package com.sylphem.leaguesfromsportdb.core

import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {

    val UI: CoroutineContext

    val IO: CoroutineContext

    val Default: CoroutineContext
}