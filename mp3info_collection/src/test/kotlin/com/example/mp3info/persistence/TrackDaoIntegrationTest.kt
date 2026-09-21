package com.example.mp3info.persistence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.nio.file.Files

class TrackDaoIntegrationTest {
    @Test
    fun `creates schema and upserts tracks`() {
        val database = Files.createTempFile("mp3info", ".sqlite")
        val config = SqliteConfig(database)
        val dao = TrackDaoImpl(config)
        config.transactionManager.required { dao.createSchema() }

        val first = track(title = "First")
        dao.upsert(first)
        assertEquals("First", dao.findByPath(first.path)?.title)

        dao.upsert(first.copy(title = "Updated"))
        assertEquals("Updated", dao.findByPath(first.path)?.title)
        assertNull(dao.findByPath("/missing.mp3"))

        val second = track(title = "Different").copy(
            path = "/music/another.mp3",
            artist = "Example Artist",
            genre = "Jazz",
        )
        dao.upsert(second)
        dao.rebuildSearchIndex()
        assertEquals(listOf(first.path), dao.search("updated").map { it.path })
        assertEquals(listOf(second.path), dao.search("jazz").map { it.path })

        dao.clearTracks()
        dao.clearSearchIndex()
        assertNull(dao.findByPath(first.path))
        assertEquals(emptyList(), dao.search("updated"))
    }

    private fun track(title: String) = Track(
        path = "/music/example.mp3", title = title, artist = null, album = null, albumArtist = null,
        year = null, genre = null, trackNumber = null, discNumber = null, comment = null,
        durationSeconds = 180, bitRateKbps = 320, sampleRateHz = 44100, channels = "Stereo",
        fileSizeBytes = 100L, lastModifiedEpochMillis = 200L,
    )
}
