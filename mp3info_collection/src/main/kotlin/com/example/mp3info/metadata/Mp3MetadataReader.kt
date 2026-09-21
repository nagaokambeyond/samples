package com.example.mp3info.metadata

import com.example.mp3info.persistence.Track
import java.nio.file.Path

fun interface Mp3MetadataReader {
    fun read(path: Path): Track
}
