package kr.blugon.tjfinder.module.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.SongType
import kr.blugon.tjfinder.module.Top100Song

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
                isMR INTEGER
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_NAME")
        onCreate(db)
    }

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
                songs.add(
                    Song(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("singer")),
                        null,
                        null,
                        cursor.getInt(cursor.getColumnIndex("isMR")) != 0,
                    )
                )
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
            val values = ContentValues()
            values.put("id", it.id)
            values.put("title", it.title)
            values.put("singer", it.singer)
            values.put("isMR", if(it.isMR) 1 else 0)
            db.insert(DATABASE_NAME, null, values)
        }
        db.close()
    }
}