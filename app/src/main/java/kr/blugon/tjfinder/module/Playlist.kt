package kr.blugon.tjfinder.module

import android.content.Context
import kotlinx.coroutines.*
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.database.SongManager
import org.json.JSONObject
import kotlin.concurrent.thread


fun JSONObject.toPlaylist(context: Context, detailSongData: Boolean = false): Playlist {
    val songListArray = this.getJSONArray("songList")
    val songList = when(detailSongData) {
        true -> ArrayList<PlaylistSong>()
        false -> null
    }
    val songIdList = ArrayList<Int>()
    val playlist = Playlist(
        this.getString("id"),
        this.getBoolean("isPrivate"),
        this.getString("title"),
        this.getString("creator"),
        this.getString("creatorTag"),
        this.getString("thumbnail"),
        null,
        listOf()
    )
    repeat(songListArray.length()) {
        if(detailSongData) {
            songList!!.add(PlaylistSong(
                SongManager[songListArray.getInt(it), SongCacheDB(context)]?: return@repeat,
                playlist,
            ))
        }
        songIdList.add(songListArray.getInt(it))
    }
    return playlist.apply {
        this.songList = songList
        this.songIdList = songIdList
    }
}
open class Playlist(
    open val id: String,
    open var isPrivate: Boolean,
    open val title: String,
    open val creator: String,
    open val creatorTag: String,
    open val thumbnail: String,
    open var songList: List<PlaylistSong>?,
    open var songIdList: List<Int>,
    open var isMine: Boolean = false
) {
    @OptIn(DelicateCoroutinesApi::class)
    fun loadSongList(context: Context) {
        thread {
            GlobalScope.launch {
                songList = arrayListOf<PlaylistSong>().apply {
                    val jobs = ArrayList<Deferred<Song?>>()
                    songIdList.forEach {
                        jobs += CoroutineScope(Dispatchers.Default).async {
                            val db = SongCacheDB(context)
                            db[it]?: SongManager[it, db]!!
                        }
                    }
                    jobs.forEach { add(PlaylistSong((it.await()?: return@forEach), this@Playlist)) }
                    this.sortBy { it.singer }
                }
            }
        }
    }

    fun toMine(): MyPlaylist = MyPlaylist(this)
}

class MyPlaylist(
    override val id: String,
    override var isPrivate: Boolean,
    override val title: String,
    override val creator: String,
    override val creatorTag: String,
    override val thumbnail: String,
    override var songList: List<PlaylistSong>?,
    override var songIdList: List<Int>,
): Playlist(id, isPrivate, title, creator, creatorTag, thumbnail, songList, songIdList, true) {
    constructor(playlist: Playlist) : this(playlist.id, playlist.isPrivate, playlist.title, playlist.creator, playlist.creatorTag, playlist.thumbnail, playlist.songList, playlist.songIdList)
}