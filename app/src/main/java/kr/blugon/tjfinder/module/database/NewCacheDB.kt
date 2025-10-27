package kr.blugon.tjfinder.module.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kr.blugon.tjfinder.module.Song

private const val DATABASE_NAME = "monthNew"
class NewCacheDB(
    val context: Context?
): SQLiteOpenHelper(context, "cache", null, 1) {

    override fun onCreate(db: SQLiteDatabase) = createTable(db)
    private fun createTable(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $DATABASE_NAME (
                id INTEGER PRIMARY KEY,
                title TEXT,
                singer TEXT,
                lyricist TEXT,
                composer TEXT,
                isMR INTEGER,
                isMV INTEGER,
                isExclusive INTEGER,
                albumArtUrl TEXT
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_NAME")
        onCreate(db)
    }

    fun clear() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_NAME")
        onCreate(db)
        db.close()
    }

    @SuppressLint("Range")
    fun get(): List<Song> {
        val songs = ArrayList<Song>()
        val db = this.readableDatabase

        val cursor = try {
            db.rawQuery("SELECT * FROM $DATABASE_NAME", null)
        } catch (_: SQLiteException) {
            createTable(db)
            return listOf()
        }

        if(cursor.moveToFirst()) {
            do {
                songs.add(Song(cursor))
            } while (cursor.moveToNext())
        }
        db.close()
        return songs
    }

    fun set(songs: List<Song>) {
        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM $DATABASE_NAME")
        } catch (_: SQLiteException) { createTable(db) }
        songs.forEach {
            db.insert(DATABASE_NAME, null, ContentValues().apply {
                put("id", it.id)
                put("title", it.title)
                put("singer", it.singer)
                put("lyricist", it.lyricist)
                put("composer", it.composer)
                put("isMR", if(it.isMR) 1 else 0)
                put("isMV", if(it.isMV) 1 else 0)
                put("isExclusive", if(it.isExclusive) 1 else 0)
                put("albumArtUrl", it.albumArtUrl)
            })
        }
        db.close()
    }
}