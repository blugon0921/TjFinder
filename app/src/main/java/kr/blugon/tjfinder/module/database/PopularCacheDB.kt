package kr.blugon.tjfinder.module.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kr.blugon.tjfinder.module.SongType
import kr.blugon.tjfinder.module.Top100Song

private val DatabaseNames = mapOf(
    SongType.K_POP to "kpop",
    SongType.POP to "pop",
    SongType.J_POP to "jpop"
)
class PopularCacheDB(
    val context: Context?
): SQLiteOpenHelper(context, "cache", null, 1) {

    override fun onCreate(db: SQLiteDatabase) = createTable(db)
    private fun createTable(db: SQLiteDatabase) {
        DatabaseNames.forEach { (_, v) ->
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $v (
                    top INTEGER PRIMARY KEY,
                    id INTEGER,
                    title TEXT,
                    singer TEXT,
                    isMR INTEGER
                );
            """.trimIndent())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        DatabaseNames.forEach { (_, v) ->
            db.execSQL("DROP TABLE IF EXISTS $v")
        }
        onCreate(db)
    }

    operator fun get(type: SongType): List<Top100Song> {
        val songs = ArrayList<Top100Song>()
        val db = this.readableDatabase

        val cursor = try {
            db.rawQuery("SELECT * FROM ${DatabaseNames[type]}", null)
        } catch (_: SQLiteException) {
            createTable(db)
            return listOf()
        }

        if(cursor.moveToFirst()) {
            do {
                songs.add(Top100Song(
                    type = type,
                    cursor.getInt(cursor.getColumnIndex("top")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("singer")),
                    null,
                    null,
                    cursor.getInt(cursor.getColumnIndex("isMR")) != 0,
                ))
            } while (cursor.moveToNext())
        }
        db.close()
        return songs.sortedBy { it.top }
    }

    operator fun set(type: SongType, songs: List<Top100Song>) {
        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM ${DatabaseNames[type]}")
        } catch (_: SQLiteException) { createTable(db) }
        songs.forEach {
            val values = ContentValues()
            values.put("top", it.top)
            values.put("id", it.id)
            values.put("title", it.title)
            values.put("singer", it.singer)
            values.put("isMR", if(it.isMR) 1 else 0)
            db.insert(DatabaseNames[type], null, values)
        }
        db.close()
    }
}