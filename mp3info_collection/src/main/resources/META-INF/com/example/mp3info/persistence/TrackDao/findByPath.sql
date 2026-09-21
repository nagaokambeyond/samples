select
    path,
    title,
    artist,
    album,
    album_artist,
    year,
    genre,
    track_number,
    disc_number,
    comment,
    duration_seconds,
    bit_rate_kbps,
    sample_rate_hz,
    channels,
    file_size_bytes,
    last_modified_epoch_millis
from tracks
where path = /* path */''
