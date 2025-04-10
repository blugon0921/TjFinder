package kr.blugon.tjfinder.utils.api.finder

import android.content.Context
import fuel.httpGet
import fuel.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.api.TjFinderApi.RequestURL
import org.json.JSONObject
import java.net.URLEncoder

class PlaylistApi {
    suspend fun getPlaylist(playlistId: String, context: Context? = null, detailSongData: Boolean = false, uid: String? = null): Playlist? {
        val url = "$RequestURL/playlist/get"
        val response = if(uid == null) {
            url.httpGet(parameters = listOf("playlistId" to playlistId))
        } else {
            url.httpPost(
                headers = mapOf("Content-Type" to "application/json"),
                body = JSONObject(
                    mapOf("uid" to uid)
                ).toString(),
                parameters = listOf("playlistId" to playlistId)
            )
        }
        val json = JSONObject(response.body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return data.toPlaylist(context, detailSongData)
    }

    enum class AddToPlaylistResponse {
        SUCCESS,
        EXIST,
        FAIL
    }
    enum class RemoveFromPlaylistResponse {
        SUCCESS,
        FAIL
    }
}
suspend fun User.getPlaylist(playlistId: String, context: Context? = null, detailSongData: Boolean = false): Playlist?
        = TjFinderApi.Playlist.getPlaylist(playlistId, context, detailSongData, uid)

suspend fun User.createPlaylist(title: String): MyPlaylist? {
    val url = "$RequestURL/playlist/create"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "title" to title
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    val data = json.getJSONObject("data")
    return if(json.getInt("code") != 200) null
    else data.toMyPlaylist()
}
suspend fun User.deletePlaylist(playlist: MyPlaylist): Boolean = this.deletePlaylist(playlist.id)
suspend fun User.deletePlaylist(playlistId: String): Boolean {
    val url = "$RequestURL/playlist/delete"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "playlistId" to playlistId
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editTitleOfPlaylist(playlist: MyPlaylist, newTitle: String): Boolean = this.editTitleOfPlaylist(playlist.id, newTitle)
suspend fun User.editTitleOfPlaylist(playlistId: String, newTitle: String): Boolean {
    val url = "$RequestURL/playlist/edit/title"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "playlistId" to playlistId,
                "title" to newTitle
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editIsPrivateOfPlaylist(playlist: MyPlaylist, isPrivate: Boolean): Boolean = this.editIsPrivateOfPlaylist(playlist.id, isPrivate)
suspend fun User.editIsPrivateOfPlaylist(playlistId: String, isPrivate: Boolean): Boolean {
    val url = "$RequestURL/playlist/edit/private"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "playlistId" to playlistId,
                "isPrivate" to isPrivate
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editThumbnailOfPlaylist(playlist: MyPlaylist, newThumbnail: String): Boolean = this.editThumbnailOfPlaylist(playlist.id, newThumbnail)
suspend fun User.editThumbnailOfPlaylist(playlistId: String, newThumbnail: String): Boolean {
    val url = "$RequestURL/playlist/edit/thumbnail"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "playlistId" to playlistId,
                "thumbnail" to newThumbnail
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}

suspend fun User.addSongToPlaylist(playlist: MyPlaylist, songId: Int): PlaylistApi.AddToPlaylistResponse = this.addSongToPlaylist(playlist.id, songId)
suspend fun User.addSongToPlaylist(playlistId: String, songId: Int): PlaylistApi.AddToPlaylistResponse {
    try {
        val url = "$RequestURL/playlist/add"
        val response = url.httpPost(
            headers = mapOf("Content-Type" to "application/json"),
            body = JSONObject(
                mapOf(
                    "uid" to uid,
                    "playlistId" to playlistId,
                    "songId" to songId
                )
            ).toString()
        )
        val json = JSONObject(response.body)
        return when(json.getInt("code")) {
            200 -> PlaylistApi.AddToPlaylistResponse.SUCCESS
            400 -> {
                if(json.getString("message") == "Already exist song in playlist") PlaylistApi.AddToPlaylistResponse.EXIST
                else PlaylistApi.AddToPlaylistResponse.FAIL
            }
            else -> PlaylistApi.AddToPlaylistResponse.FAIL
        }
    } catch (e: Exception) {return PlaylistApi.AddToPlaylistResponse.FAIL
    }
}
suspend fun User.removeSongFromPlaylist(playlist: MyPlaylist, songId: Int): PlaylistApi.RemoveFromPlaylistResponse = this.removeSongFromPlaylist(playlist.id, songId)
suspend fun User.removeSongFromPlaylist(playlistId: String, songId: Int): PlaylistApi.RemoveFromPlaylistResponse {
    try {
        val url = "$RequestURL/playlist/remove"
        val response = url.httpPost(
            headers = mapOf("Content-Type" to "application/json"),
            body = JSONObject(
                mapOf(
                    "uid" to uid,
                    "playlistId" to playlistId,
                    "songId" to songId
                )
            ).toString()
        )
        val json = JSONObject(response.body)
        return if(json.getInt("code") == 200) PlaylistApi.RemoveFromPlaylistResponse.SUCCESS
        else PlaylistApi.RemoveFromPlaylistResponse.FAIL
    } catch (e: Exception) {return PlaylistApi.RemoveFromPlaylistResponse.FAIL
    }
}
suspend fun Playlist.exist(song: Song, context: Context) = this.exist(song.id, context)
suspend fun Playlist.exist(songId: Int, context: Context): Boolean {
    val playlist = TjFinderApi.Playlist.getPlaylist(this.id, context)
    return !(playlist == null || !playlist.songIdList.contains(songId))
}

suspend fun User.playlists(context: Context, detailSongData: Boolean = false): List<MyPlaylist>? {
    val playlists = this.toOtherUser().playlists(context, detailSongData, this@playlists.uid)?: return null
    return ArrayList<MyPlaylist>().apply {
        playlists.forEach {
            this.add(it.toMine())
        }
    }
}
suspend fun OtherUser.playlists(context: Context, detailSongData: Boolean = false, uid: String? = null): List<Playlist>? {
    val url = "$RequestURL/playlist/list"
    val response = if(uid == null) {
        url.httpGet(
            parameters = listOf(
                "name" to this.name,
                "tag" to this.tag
            )
        )
    } else {
        url.httpPost(
            headers = mapOf("Content-Type" to "application/json"),
            body = JSONObject(
                mapOf("uid" to uid)
            ).toString(),
            parameters = listOf(
                "name" to this.name,
                "tag" to this.tag
            )
        )
    }
    val json = JSONObject(response.body)
    if(json.isNull("code") || json.getInt("code") != 200) return null
    val data = json.getJSONArray("data")
    val playlists = ArrayList<Playlist>()
    repeat(data.length()) {
        val list = data.getJSONObject(it)
        playlists.add(list.toPlaylist(context, detailSongData))
    }
    return playlists
}
suspend fun User.searchPlaylist(title: String, context: Context): List<Playlist> {
    val url = "$RequestURL/playlist/search"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf("uid" to uid)
        ).toString(),
        parameters = listOf("title" to title)
    )
    val json = JSONObject(response.body)
    if(json.isNull("code") || json.getInt("code") != 200) return listOf()
    val data = json.getJSONArray("data")
    val playlists = ArrayList<Playlist>()
    repeat(data.length()) {
        val playlist = data.getJSONObject(it)
        playlists.add(playlist.toPlaylist(context))
    }
    return playlists
}