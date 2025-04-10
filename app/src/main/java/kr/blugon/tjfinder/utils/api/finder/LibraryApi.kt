package kr.blugon.tjfinder.utils.api.finder

import android.content.Context
import fuel.httpPost
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.utils.api.TjFinderApi.Playlist
import kr.blugon.tjfinder.utils.api.TjFinderApi.RequestURL
import org.json.JSONObject

suspend fun User.addPlaylistToLibrary(playlistId: String): Boolean {
    val url = "$RequestURL/library/add"
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
    return (json.getInt("code") == 200)
}
suspend fun User.removePlaylistFromLibrary(playlistId: String): Boolean {
    val url = "$RequestURL/library/remove"
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
    return (json.getInt("code") == 200)
}
suspend fun User.isExistInLibrary(playlistId: String): Boolean {
    val url = "$RequestURL/library/isExist"
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
    if(json.getInt("code") != 200) return false
    return json.getBoolean("isExist")
}
suspend fun User.libraries(context: Context): List<Playlist>? {
    val url = "$RequestURL/library/list"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf("uid" to uid)
        ).toString()
    )
    val json = JSONObject(response.body)
    if(json.getInt("code") != 200) return null
    val data = json.getJSONArray("data")
    val list = ArrayList<Playlist>()
    repeat(data.length()) {
        val playlistId = data.getString(it)
        val playlist = Playlist.getPlaylist(playlistId, context, uid = this.uid) ?: return@repeat
        list.add(playlist)
    }
    return list
}