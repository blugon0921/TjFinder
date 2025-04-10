package kr.blugon.tjfinder.module

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kr.blugon.tjfinder.module.database.SongCacheDB
import kr.blugon.tjfinder.module.database.SongManager
import org.json.JSONObject
import kotlin.concurrent.thread


suspend fun JSONObject.toPlaylist(context: Context? = null, detailSongData: Boolean = false): Playlist {
    val songListArray = this.getJSONArray("songList")
    val playlist = Playlist(
        this.getString("id"),
        this.getBoolean("isPrivate"),
        this.getString("title"),
        OtherUser(this.getJSONObject("owner")),
        this.getString("thumbnail"),
        null,
        listOf()
    )
    if(songListArray.length() == 0) return playlist //no in songs
    val songIdList = ArrayList<Int>()
    repeat(songListArray.length()) {
        val id = songListArray.getInt(it)
        songIdList.add(id)
    }
    return playlist.apply {
        this.songIdList = songIdList
        if(detailSongData && context != null) this.loadSongList(context)
    }
}
open class Playlist(
    open val id: String,
    open var isPrivate: Boolean,
    open val title: String,
    open val owner: OtherUser,
    open val thumbnail: String,
    open var songList: List<PlaylistSong>?,
    open var songIdList: List<Int>,
    open var isMine: Boolean = false
) {
    suspend fun loadSongList(context: Context) {
        CoroutineScope(IO).launch {
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
        }.join()
    }

    fun toMine(): MyPlaylist = MyPlaylist(this)

    override fun toString(): String {
        return """
            {
                "id": "$id",
                "title": "$title",
                "owner": "$owner",
                "thumbnail": "$thumbnail",
                "songList": $songList,
                "songIdList": $songIdList,
                "isPrivate": $isPrivate,
                "isMine": $isMine
            }
        """.trimIndent()
    }
}

suspend fun JSONObject.toMyPlaylist(context: Context? = null, detailSongData: Boolean = false): MyPlaylist {
    val songListArray = this.getJSONArray("songList")
    val playlist = MyPlaylist(
        this.getString("id"),
        this.getBoolean("isPrivate"),
        this.getString("title"),
        OtherUser(this.getJSONObject("owner")),
        this.getString("thumbnail"),
        null,
        listOf()
    )
    if(songListArray.length() == 0) return playlist //no in songs
    val songIdList = ArrayList<Int>()
    repeat(songListArray.length()) {
        val id = songListArray.getInt(it)
        songIdList.add(id)
    }
    return playlist.apply {
        this.songIdList = songIdList
        if(detailSongData && context != null) this.loadSongList(context)
    }
}
class MyPlaylist(
    override val id: String,
    override var isPrivate: Boolean,
    override val title: String,
    override val owner : OtherUser,
    override val thumbnail: String,
    override var songList: List<PlaylistSong>?,
    override var songIdList: List<Int>,
): Playlist(id, isPrivate, title, owner, thumbnail, songList, songIdList, true) {
    constructor(playlist: Playlist) : this(
        playlist.id,
        playlist.isPrivate,
        playlist.title,
        playlist.owner,
        playlist.thumbnail,
        playlist.songList,
        playlist.songIdList
    )
}