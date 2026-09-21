package com.example.mp3info.metadata

import com.example.mp3info.persistence.Track
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.nio.file.Files
import java.nio.file.Path

class JaudiotaggerMp3MetadataReader : Mp3MetadataReader {
    override fun read(path: Path): Track {
        val audioFile = AudioFileIO.read(path.toFile())
        val tag = audioFile.tag
        fun tagValue(field: FieldKey): String? =
            tag?.getFirst(field)?.trim()?.takeIf { it.isNotEmpty() }

        val header = audioFile.audioHeader
        return Track(
            path = path.toAbsolutePath().normalize().toString(),
            title = tagValue(FieldKey.TITLE),
            artist = tagValue(FieldKey.ARTIST),
            album = tagValue(FieldKey.ALBUM),
            albumArtist = tagValue(FieldKey.ALBUM_ARTIST),
            year = tagValue(FieldKey.YEAR),
            genre = tagValue(FieldKey.GENRE),
            trackNumber = tagValue(FieldKey.TRACK),
            discNumber = tagValue(FieldKey.DISC_NO),
            comment = tagValue(FieldKey.COMMENT),
            durationSeconds = header.trackLength.takeIf { it >= 0 },
            bitRateKbps = header.bitRate.toPositiveIntOrNull(),
            sampleRateHz = header.sampleRate.toPositiveIntOrNull(),
            channels = header.channels?.trim()?.takeIf { it.isNotEmpty() },
            fileSizeBytes = Files.size(path),
            lastModifiedEpochMillis = Files.getLastModifiedTime(path).toMillis(),
        )
    }

    private fun String?.toPositiveIntOrNull(): Int? =
        this?.filter(Char::isDigit)?.toIntOrNull()?.takeIf { it > 0 }
}
