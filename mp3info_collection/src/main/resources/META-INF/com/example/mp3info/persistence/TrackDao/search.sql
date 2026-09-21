select
    tracks.path,
    tracks.title,
    tracks.artist,
    tracks.album,
    tracks.album_artist,
    tracks.year,
    tracks.genre,
    tracks.track_number,
    tracks.disc_number,
    tracks.comment,
    tracks.duration_seconds,
    tracks.bit_rate_kbps,
    tracks.sample_rate_hz,
    tracks.channels,
    tracks.file_size_bytes,
    tracks.last_modified_epoch_millis
from tracks
join tracks_fts on tracks.rowid = tracks_fts.rowid
where tracks_fts match /* query */''
order by bm25(tracks_fts), tracks.artist collate nocase, tracks.album collate nocase,
    tracks.title collate nocase, tracks.path collate nocase
