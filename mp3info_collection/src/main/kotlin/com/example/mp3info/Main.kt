package com.example.mp3info

import com.example.mp3info.metadata.JaudiotaggerMp3MetadataReader
import com.example.mp3info.persistence.SqliteConfig
import com.example.mp3info.persistence.TrackDaoImpl
import com.example.mp3info.service.Mp3Scanner
import com.example.mp3info.service.ScanSummary
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when (args.firstOrNull()) {
        "scan" -> scan(args)
        "clear" -> clear(args)
        "search" -> search(args)
        else -> usageAndExit()
    }
}

private fun scan(args: Array<String>) {
    if (args.size != 3) usageAndExit()
    val sourceDirectory = Path.of(args[1])
    if (!Files.isDirectory(sourceDirectory)) {
        System.err.println("Source directory does not exist or is not a directory: $sourceDirectory")
        exitProcess(2)
    }

    val (config, dao) = openDatabase(Path.of(args[2]))
    config.transactionManager.required {
        dao.createSchema()
    }

    var summary: ScanSummary? = null
    config.transactionManager.required {
        summary = Mp3Scanner(JaudiotaggerMp3MetadataReader(), dao).scan(sourceDirectory)
    }
    val result = checkNotNull(summary)
    println("Discovered: ${result.discovered}, inserted: ${result.inserted}, updated: ${result.updated}, failed: ${result.failed}")
}

private fun clear(args: Array<String>) {
    if (args.size != 2) usageAndExit()
    val (config, dao) = openDatabase(Path.of(args[1]))
    config.transactionManager.required {
        dao.createSchema()
        dao.clearTracks()
        dao.clearSearchIndex()
    }
    println("Cleared all stored tracks.")
}

private fun search(args: Array<String>) {
    if (args.size != 3 || args[2].isBlank()) usageAndExit()
    val (config, dao) = openDatabase(Path.of(args[1]))
    config.transactionManager.required {
        dao.createSchema()
    }
    val tracks = dao.search(toFtsQuery(args[2]))
    if (tracks.isEmpty()) {
        println("No matches.")
        return
    }

    println("Path\tTitle\tArtist\tAlbum\tGenre")
    tracks.forEach { track ->
        println(
            listOf(track.path, track.title, track.artist, track.album, track.genre)
                .joinToString("\t") { it?.replace(Regex("[\\t\\r\\n]"), " ") ?: "" },
        )
    }
}

private fun toFtsQuery(query: String): String =
    query.trim()
        .split(Regex("\\s+"))
        .joinToString(" AND ") { term -> "\"${term.replace("\"", "\"\"")}\"" }

private fun openDatabase(path: Path): Pair<SqliteConfig, TrackDaoImpl> {
    val databasePath = path.toAbsolutePath().normalize()
    databasePath.parent?.let(Files::createDirectories)
    val config = SqliteConfig(databasePath)
    return config to TrackDaoImpl(config)
}

private fun usageAndExit(): Nothing {
    System.err.println(
        "Usage:\n" +
            "  mp3info-collection scan <source-directory> <sqlite-db-path>\n" +
            "  mp3info-collection clear <sqlite-db-path>\n" +
            "  mp3info-collection search <sqlite-db-path> <query>",
    )
    exitProcess(2)
}
