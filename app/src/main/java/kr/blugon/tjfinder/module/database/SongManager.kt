@file:SuppressLint("Range")
package kr.blugon.tjfinder.module.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import androidx.core.database.getStringOrNull
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.utils.api.StringType
import kr.blugon.tjfinder.utils.api.TJApi
import java.time.LocalDateTime

fun Cursor.getInt(columnName: String) = getInt(getColumnIndex(columnName))
fun Cursor.getString(columnName: String): String = getString(getColumnIndex(columnName))
fun Cursor.getStringOrNull(columnName: String) = getStringOrNull(getColumnIndex(columnName))
fun Cursor.getBoolean(columnName: String) = getInt(getColumnIndex(columnName)) == 1

object SongManager {
    private const val PREF_NAME = "updateAt"

    operator fun get(id: Int, db: SongCacheDB): Song? {
        val fromDB = db[id]
        return if (fromDB == null) {
            TJApi[id]?.also { db.add(it) }
        } else null
    }
    fun search(keyword: String, type: StringType, db: SongCacheDB, match: Boolean = false): List<Song> = TJApi.search(keyword, type, match).also { db.addAll(it) }

    fun searchWithId(id: Int, db: SongCacheDB, match: Boolean = false): List<Song> = TJApi.searchWithId(id, match).also { db.addAll(it) }
    fun searchWithTitle(title: String, db: SongCacheDB, match: Boolean = false): List<Song> = TJApi.searchWithTitle(title, match).also { db.addAll(it) }
    fun searchWithSinger(singer: String, db: SongCacheDB, match: Boolean = false): List<Song> = TJApi.searchWithSinger(singer, match).also { db.addAll(it) }

    suspend fun monthPopular(type: Top100Type = Top100Type.K_POP, context: Context): List<Top100Song> {
        val lastUpdate = getUpdateAt(context, type.name)
        val popularCacheDB = Top100CacheDB(context)

        if(lastUpdate != null && LocalDateTime.now().dayOfYear == lastUpdate) {
            val cache = popularCacheDB[type]
            if(cache.isNotEmpty()) return cache
        }

        try {
            val cacheDB = SongCacheDB(context)
            TJApi.monthPopular(type).also {
                cacheDB.addAll(it)
                popularCacheDB[type] = it
                saveUpdateAt(context, type.name)
                return it
            }
        } catch (_: NullPointerException) {
            SongCacheDB(context).clear()
            Top100CacheDB(context).clear()
            throw NullPointerException()
        }
    }

    suspend fun monthNew(context: Context): List<Song> {
        val lastUpdate = getUpdateAt(context, "new_month")
        val newCacheDB = NewCacheDB(context)

        if(lastUpdate != null && LocalDateTime.now().dayOfYear == lastUpdate) {
            val cache = newCacheDB.get()
            if(cache.isNotEmpty()) return cache
        }

        try {
            val cacheDB = SongCacheDB(context)
            TJApi.monthNew().also {
                cacheDB.addAll(it)
                newCacheDB.set(it)
                saveUpdateAt(context, "new_month")
                return it
            }
        } catch (_: NullPointerException) {
            SongCacheDB(context).clear()
            NewCacheDB(context).clear()
            throw NullPointerException()
        }
    }


    private fun saveUpdateAt(context: Context, key: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, LocalDateTime.now().dayOfYear)
        editor.apply()
    }

    private fun getUpdateAt(context: Context, key: String): Int? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val l = sharedPreferences.getInt(key, -1)
        return if(l == -1) null
        else l
    }
}