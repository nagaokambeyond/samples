insert into tracks (
    path, title, artist, album, album_artist, year, genre, track_number, disc_number, comment,
    duration_seconds, bit_rate_kbps, sample_rate_hz, channels, file_size_bytes, last_modified_epoch_millis
) values (
    /* track.path */'', /* track.title */'', /* track.artist */'', /* track.album */'',
    /* track.albumArtist */'', /* track.year */'', /* track.genre */'', /* track.trackNumber */'',
    /* track.discNumber */'', /* track.comment */'', /* track.durationSeconds */0,
    /* track.bitRateKbps */0, /* track.sampleRateHz */0, /* track.channels */'',
    /* track.fileSizeBytes */0, /* track.lastModifiedEpochMillis */0
)
on conflict(path) do update set
    title = excluded.title,
    artist = excluded.artist,
    album = excluded.album,
    album_artist = excluded.album_artist,
    year = excluded.year,
    genre = excluded.genre,
    track_number = excluded.track_number,
    disc_number = excluded.disc_number,
    comment = excluded.comment,
    duration_seconds = excluded.duration_seconds,
    bit_rate_kbps = excluded.bit_rate_kbps,
    sample_rate_hz = excluded.sample_rate_hz,
    channels = excluded.channels,
    file_size_bytes = excluded.file_size_bytes,
    last_modified_epoch_millis = excluded.last_modified_epoch_millis
