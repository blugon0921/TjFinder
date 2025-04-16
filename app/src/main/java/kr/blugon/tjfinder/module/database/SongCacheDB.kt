package kr.blugon.tjfinder.module.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull
import kr.blugon.tjfinder.module.Song

private const val TABLE_NAME = "songs"
class SongCacheDB(
    val context: Context?
): SQLiteOpenHelper(context, "cache", null, 1) {

    override fun onCreate(db: SQLiteDatabase) = createTable(db)
    private fun createTable(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY,
                title TEXT,
                singer TEXT,
                lyricist TEXT,
                composer TEXT,
                isMR INTEGER,
                isMV INTEGER,
                albumArtUrl TEXT
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun clear() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        db.close()
    }

    @SuppressLint("Range")
    operator fun get(id: Int): Song? {
        val db = this.readableDatabase

        val cursor = try {
            db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id = $id", null)
        } catch (_: SQLiteException) {
            createTable(db)
            return null
        }
        cursor.moveToFirst()
        if(cursor.count <= 0) return null

        val song = Song(cursor)
        db.close()

        return song
    }

    fun add(song: Song) {
        if(get(song.id) != null) return
        addAll(listOf(song))
    }

    fun addAll(songs: List<Song>) {
        val db = this.writableDatabase
        songs.forEach {
            if(this[it.id] != null) return
            db.insert(TABLE_NAME, null, ContentValues().apply {
                put("id", it.id)
                put("title", it.title)
                put("singer", it.singer)
                put("lyricist", it.lyricist)
                put("composer", it.composer)
                put("isMR", if(it.isMR) 1 else 0)
                put("isMV", if(it.isMV) 1 else 0)
                put("albumArtUrl", it.albumArtUrl)
            })
        }
        db.close()
    }
}