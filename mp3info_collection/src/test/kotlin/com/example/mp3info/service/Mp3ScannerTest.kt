package com.example.mp3info.service

import com.example.mp3info.metadata.Mp3MetadataReader
import com.example.mp3info.persistence.SqliteConfig
import com.example.mp3info.persistence.Track
import com.example.mp3info.persistence.TrackDaoImpl
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Files

class Mp3ScannerTest {
    @Test
    fun `scans nested mp3 files and continues after a reader failure`() {
        val root = Files.createTempDirectory("music")
        val good = root.resolve("nested/good.mp3")
        good.parent.createDirectories()
        good.createFile()
        root.resolve("broken.MP3").createFile()
        root.resolve("ignore.txt").createFile()

        val config = SqliteConfig(Files.createTempFile("mp3info", ".sqlite"))
        val dao = TrackDaoImpl(config)
        config.transactionManager.required { dao.createSchema() }
        val reader = Mp3MetadataReader { path ->
            if (path.fileName.toString().startsWith("broken")) error("broken file")
            Track(path.toAbsolutePath().toString(), "Song", null, null, null, null, null, null, null, null, 1, 2, 3, null, 4, 5)
        }

        val errors = mutableListOf<String>()
        val result = Mp3Scanner(reader, dao, errors::add).scan(root)

        assertEquals(2, result.discovered)
        assertEquals(1, result.inserted)
        assertEquals(0, result.updated)
        assertEquals(1, result.failed)
        assertEquals("Song", dao.findByPath(good.toAbsolutePath().toString())?.title)
        assertEquals(listOf(good.toAbsolutePath().toString()), dao.search("song").map { it.path })
        assertEquals(1, errors.size)
    }
}
