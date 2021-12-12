package com.cinema.adapters.outbound.repositories.dto

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class MovieStorageTest {

    companion object {
        private const val KEY_PREFIX = "mo#"
        private const val SORT_KEY_PREFIX = "mo#"
    }

    @Test
    fun `given a movie storage buildKey returns a copy with new keys`() {
        val movie = MovieStorage(id = UUID.randomUUID().toString())

        val movieWithBuildKey = movie.buildKey()

        assertTrue(movieWithBuildKey.id.contains(KEY_PREFIX))
        assertTrue(movieWithBuildKey.id.contains(movie.id))
        assertTrue(movieWithBuildKey.sortKey.contains(SORT_KEY_PREFIX))
        assertTrue(movieWithBuildKey.sortKey.contains(movie.id))
    }

    @Test
    fun `given a movie storage  unBuildKey returns a copy without new keys`() {
        val movieId = UUID.randomUUID().toString()
        val movie = MovieStorage(id = "$KEY_PREFIX$movieId", sortKey = "$SORT_KEY_PREFIX$movieId")

        val menuItemWithoutBuildKey = movie.unBuildKey()

        assertTrue(menuItemWithoutBuildKey.id.contains(KEY_PREFIX).not())
        assertTrue(movie.id.contains(menuItemWithoutBuildKey.id))
        assertTrue(menuItemWithoutBuildKey.id.contains(SORT_KEY_PREFIX).not())
        assertTrue(movie.sortKey.contains(menuItemWithoutBuildKey.sortKey))
    }
}
