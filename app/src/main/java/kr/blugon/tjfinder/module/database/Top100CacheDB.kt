package kr.blugon.tjfinder.module.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.Top100Song
import kr.blugon.tjfinder.module.Top100Type

class Top100CacheDB(
    val context: Context?
): SQLiteOpenHelper(context, "cache", null, 1) {

    override fun onCreate(db: SQLiteDatabase) = createTable(db)
    private fun createTable(db: SQLiteDatabase) {
        Top100Type.entries.forEach { t ->
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS ${t.name} (
                    rank INTEGER PRIMARY KEY,
                    id INTEGER,
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Top100Type.entries.forEach { t ->
            db.execSQL("DROP TABLE IF EXISTS ${t.name}")
        }
        onCreate(db)
    }

    fun clear() {
        val db = writableDatabase
        Top100Type.entries.forEach { db.execSQL("DROP TABLE IF EXISTS ${it.name}") }
        onCreate(db)
        db.close()
    }

    @SuppressLint("Range")
    operator fun get(type: Top100Type): List<Top100Song> {
        val songs = ArrayList<Top100Song>()
        val db = this.readableDatabase

        val cursor = try {
            db.rawQuery("SELECT * FROM ${type.name}", null)
        } catch (_: SQLiteException) {
            createTable(db)
            return listOf()
        }

        if(cursor.moveToFirst()) {
            do {
                songs.add(Top100Song(
                    type = type,
                    rank = cursor.getInt("rank"),
                    song = Song(cursor),
                ))
            } while (cursor.moveToNext())
        }
        db.close()
        return songs.sortedBy { it.rank }
    }

    operator fun set(type: Top100Type, songs: List<Top100Song>) {
        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM ${type.name}")
        } catch (_: SQLiteException) { createTable(db) }
        songs.forEach {
            db.insert(type.name, null, ContentValues().apply {
                put("rank", it.rank)
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