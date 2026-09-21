package com.example.mp3info.service

import com.example.mp3info.metadata.Mp3MetadataReader
import com.example.mp3info.persistence.TrackDao
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension

data class ScanSummary(
    val discovered: Int,
    val inserted: Int,
    val updated: Int,
    val failed: Int,
)

class Mp3Scanner(
    private val metadataReader: Mp3MetadataReader,
    private val trackDao: TrackDao,
    private val reportError: (String) -> Unit = { System.err.println(it) },
) {
    fun scan(root: Path): ScanSummary {
        var discovered = 0
        var inserted = 0
        var updated = 0
        var failed = 0

        Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
            override fun visitFile(path: Path, attributes: BasicFileAttributes): FileVisitResult {
                if (!attributes.isRegularFile || !path.extension.equals("mp3", ignoreCase = true)) {
                    return FileVisitResult.CONTINUE
                }

                discovered++
                try {
                    val track = metadataReader.read(path)
                    val existed = trackDao.findByPath(track.path) != null
                    trackDao.upsert(track)
                    if (existed) updated++ else inserted++
                } catch (exception: Exception) {
                    failed++
                    reportError("Failed to read $path: ${exception.message ?: exception.javaClass.simpleName}")
                }
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(path: Path, exception: IOException): FileVisitResult {
                failed++
                reportError("Failed to access $path: ${exception.message ?: exception.javaClass.simpleName}")
                return FileVisitResult.CONTINUE
            }
        })
        trackDao.rebuildSearchIndex()
        return ScanSummary(discovered, inserted, updated, failed)
    }
}
