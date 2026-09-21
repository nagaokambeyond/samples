package com.example.mp3info.persistence

import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.jdbc.entity.NamingType

@Entity(immutable = true, naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "tracks")
data class Track(
    @Id
    @Column(name = "path")
    val path: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumArtist: String?,
    val year: String?,
    val genre: String?,
    val trackNumber: String?,
    val discNumber: String?,
    val comment: String?,
    val durationSeconds: Int?,
    val bitRateKbps: Int?,
    val sampleRateHz: Int?,
    val channels: String?,
    val fileSizeBytes: Long,
    val lastModifiedEpochMillis: Long,
)
