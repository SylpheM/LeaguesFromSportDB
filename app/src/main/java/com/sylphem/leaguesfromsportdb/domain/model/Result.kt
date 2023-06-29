package com.sylphem.leaguesfromsportdb.domain.model

/**
 * Custom Result because we can't mock kotlin's Result class properly.
 *
 * https://stackoverflow.com/questions/65420765/problems-with-kotlin-resultt-on-unit-tests
 */
sealed class Result<T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val error: Throwable) : Result<T>()
}