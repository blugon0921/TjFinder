package kr.blugon.tjfinder.module.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.User

abstract class SearchResult {
    abstract val song: List<Song>
    abstract val playlist: List<Playlist>
    abstract val user: List<OtherUser>

    fun isEmpty(): Boolean = song.isEmpty() && playlist.isEmpty() && user.isEmpty()
    fun isNotEmpty(): Boolean = !isEmpty()
}

data class SearchResults(
    override val song: List<Song> = listOf(),
    override val playlist: List<Playlist> = listOf(),
    override val user: List<OtherUser> = listOf(),
): SearchResult() {
    fun toMutable(): MutableSearchResults = MutableSearchResults(
        song = mutableStateListOf<Song>().apply { addAll(song) },
        playlist = mutableStateListOf<Playlist>().apply { addAll(playlist) },
        user = mutableStateListOf<OtherUser>().apply { addAll(user) },
    )
}
data class MutableSearchResults(
    override val song: SnapshotStateList<Song> = mutableStateListOf(),
    override val playlist: SnapshotStateList<Playlist> = mutableStateListOf(),
    override val user: SnapshotStateList<OtherUser> = mutableStateListOf(),
): SearchResult() {
    fun toDefault(): SearchResults = SearchResults(
        song = ArrayList<Song>().apply { addAll(song) },
        playlist = ArrayList<Playlist>().apply { addAll(playlist) },
        user = ArrayList<OtherUser>().apply { addAll(user) },
    )
    fun clear() {
        song.clear()
        playlist.clear()
        user.clear()
    }
    fun add(song: Song) = this.song.add(song)
    fun add(playlist: Playlist) = this.playlist.add(playlist)
    fun add(user: OtherUser) = this.user.add(user)
    fun addAll(results: SearchResults) = addAll(results.song, results.playlist, results.user)
    fun addAll(song: List<Song> = listOf(), playlist: List<Playlist> = listOf(), user: List<OtherUser> = listOf()) {
        this.song.addAll(song)
        this.playlist.addAll(playlist)
        this.user.addAll(user)
    }
}