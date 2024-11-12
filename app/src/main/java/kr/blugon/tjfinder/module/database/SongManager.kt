package kr.blugon.tjfinder.module.database

import android.content.Context
import kotlinx.coroutines.*
import kr.blugon.tjfinder.module.*
import java.time.LocalDateTime

private val keys = mapOf(
    SongType.K_POP to "kpop",
    SongType.POP to "pop",
    SongType.J_POP to "jpop"
)
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

    fun monthPopular(type: SongType = SongType.K_POP, context: Context): List<Top100Song> {
        val lastUpdate = getUpdateAt(context, keys[type]!!)
        val popularCacheDB = PopularCacheDB(context)

        if(lastUpdate != null && LocalDateTime.now().dayOfYear == lastUpdate) {
            val cache = popularCacheDB[type]
            if(cache.isNotEmpty()) return cache
        }

        val cacheDB = SongCacheDB(context)
        TJApi.monthPopular(type).also {
            cacheDB.addAll(it)
            popularCacheDB[type] = it
            saveUpdateAt(context, keys[type]!!)
            return it
        }
    }

    fun monthNew(context: Context): List<Song> {
        val lastUpdate = getUpdateAt(context, "new_month")
        val newCacheDB = NewCacheDB(context)

        if(lastUpdate != null && LocalDateTime.now().dayOfYear == lastUpdate) {
            val cache = newCacheDB.get()
            if(cache.isNotEmpty()) return cache
        }

        val cacheDB = SongCacheDB(context)
        TJApi.monthNew().also {
            cacheDB.addAll(it)
            newCacheDB.set(it)
            saveUpdateAt(context, "new_month")
            return it
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