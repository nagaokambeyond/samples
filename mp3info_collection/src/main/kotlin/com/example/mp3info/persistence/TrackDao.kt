package com.example.mp3info.persistence

import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Script
import org.seasar.doma.Select
import org.seasar.doma.jdbc.Result

@Dao
interface TrackDao {
    @Script
    fun createSchema()

    @Script
    fun clearTracks()

    @Script
    fun clearSearchIndex()

    @Script
    fun rebuildSearchIndex()

    @Select
    fun findByPath(path: String): Track?

    @Select
    fun search(query: String): List<Track>

    @Insert(sqlFile = true)
    fun upsert(track: Track): Result<Track>
}
