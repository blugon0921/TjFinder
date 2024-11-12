package kr.blugon.tjfinder.module.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull
import kr.blugon.tjfinder.module.Song

private const val DATABASE_NAME = "songs"
class SongCacheDB(
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
                isMR INTEGER
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_NAME")
        onCreate(db)
    }

    val songList: List<Song>
        @SuppressLint("Range", "Recycle")
        get() {
            val songs = ArrayList<Song>()
            val db = this.writableDatabase
            val cursor = db.rawQuery("SELECT * FROM $DATABASE_NAME", null)
            if(cursor.moveToFirst()) {
                do {
                    songs.add(Song(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("singer")),
                        cursor.getStringOrNull(cursor.getColumnIndex("lyricist")),
                        cursor.getStringOrNull(cursor.getColumnIndex("composer")),
                        cursor.getInt(cursor.getColumnIndex("isMR")) != 0,
                    ))
                } while (cursor.moveToNext())
            }
            db.close()
            return songs
        }

    operator fun get(id: Int): Song? {
        val db = this.readableDatabase

        val cursor = try {
            db.rawQuery("SELECT * FROM $DATABASE_NAME WHERE id = $id", null)
        } catch (_: SQLiteException) {
            createTable(db)
            return null
        }
        cursor.moveToFirst()
        if(cursor.count <= 0) return null

        val song = Song(
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getString(cursor.getColumnIndex("title")),
            cursor.getString(cursor.getColumnIndex("singer")),
            cursor.getString(cursor.getColumnIndex("lyricist")),
            cursor.getString(cursor.getColumnIndex("composer")),
            cursor.getInt(cursor.getColumnIndex("isMR")) != 0,
        )
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
            val values = ContentValues()
            values.put("id", it.id)
            values.put("title", it.title)
            values.put("singer", it.singer)
            values.put("lyricist", it.lyricist)
            values.put("composer", it.composer)
            values.put("isMR", if(it.isMR) 1 else 0)
            db.insert(DATABASE_NAME, null, values)
        }
        db.close()
    }
}