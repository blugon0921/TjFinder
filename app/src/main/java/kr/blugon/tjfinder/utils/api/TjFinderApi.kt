package kr.blugon.tjfinder.utils.api

import android.content.Context
import fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.blugon.tjfinder.module.*
import org.json.JSONObject
import java.net.URLEncoder

object TjFinderApi {
    const val URL = "https://tjfinderapi.blugon.kr"

    suspend fun registerUser(uid: String, email: String, name: String, profileImage: String): User? {
        val url = "$URL/user/register?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&email=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(email, "utf-8")
            }
        }&name=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(name, "utf-8")
            }
        }&profileImage=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(profileImage, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return User(
            data.getString("uid"),
            data.getString("email"),
            data.getString("name"),
            data.getString("tag"),
            data.getString("profileImage"),
            data.getString("description"),
            data.getBoolean("isPrivate")
        )
    }
    suspend fun login(uid: String): User? {
        val url = "$URL/user/login?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return User(
            data.getString("uid"),
            data.getString("email"),
            data.getString("name"),
            data.getString("tag"),
            data.getString("profileImage"),
            data.getString("description"),
            data.getBoolean("isPrivate")
        )
    }
    suspend fun getUser(name: String, tag: String): OtherUser? {
        val url = "$URL/user/get?name=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(name, "utf-8")
            }
        }&tag=$tag"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return OtherUser(
            data.getString("name"),
            data.getString("tag"),
            data.getString("profileImage"),
            data.getString("description")
        )
    }
    suspend fun searchUser(name: String, context: Context): List<OtherUser> {
        val url = "$URL/user/search?name=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(name, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return listOf()
        val data = json.getJSONArray("data")
        val users = ArrayList<OtherUser>()
        repeat(data.length()) {
            val user = data.getJSONObject(it)
            users.add(
                OtherUser(
                user.getString("name"),
                user.getString("tag"),
                user.getString("profileImage"),
                user.getString("description"),
            )
            )
        }
        return users
    }
    suspend fun User.editName(newName: String): Boolean {
        val url = "$URL/user/edit/name?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&name=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(newName, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editProfileImage(newImageUrl: String): Boolean {
        val url = "$URL/user/edit/profileImage?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&thumbnail=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(newImageUrl, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editDescription(description: String): Boolean {
        val url = "$URL/user/edit/description?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&description=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(description, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editIsPrivate(isPrivate: Boolean): Boolean {
        val url = "$URL/user/edit/isPrivate?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&isPrivate=$isPrivate"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }



    suspend fun User.createPlaylist(title: String): MyPlaylist? {
        val url = "$URL/playlist/create?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&title=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(title, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        val data = json.getJSONObject("data")
        return if(json.getInt("code") != 200) null
        else {
            MyPlaylist(
                data.getString("id"),
                data.getBoolean("isPrivate"),
                data.getString("title"),
                data.getString("creator"),
                data.getString("creatorTag"),
                data.getString("thumbnail"),
                null,
                listOf()
            )
        }
    }
    suspend fun User.deletePlaylist(playlist: MyPlaylist): Boolean = this.deletePlaylist(playlist.id)
    suspend fun User.deletePlaylist(playlistId: String): Boolean {
        val url = "$URL/playlist/delete?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=$playlistId"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editTitleOfPlaylist(playlist: MyPlaylist, newTitle: String): Boolean = this.editTitleOfPlaylist(playlist.id, newTitle)
    suspend fun User.editTitleOfPlaylist(playlistId: String, newTitle: String): Boolean {
        val url = "$URL/playlist/edit/title?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=$playlistId&title=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(newTitle, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editIsPrivateOfPlaylist(playlist: MyPlaylist, isPrivate: Boolean): Boolean = this.editIsPrivateOfPlaylist(playlist.id, isPrivate)
    suspend fun User.editIsPrivateOfPlaylist(playlistId: String, isPrivate: Boolean): Boolean {
        val url = "$URL/playlist/edit/private?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=$playlistId&isPrivate=${isPrivate}"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.editThumbnailOfPlaylist(playlist: MyPlaylist, newThumbnail: String): Boolean = this.editThumbnailOfPlaylist(playlist.id, newThumbnail)
    suspend fun User.editThumbnailOfPlaylist(playlistId: String, newThumbnail: String): Boolean {
        val url = "$URL/playlist/edit/thumbnail?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=$playlistId&thumbnail=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(newThumbnail, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    enum class AddToPlaylistResponse {
        SUCCESS,
        EXIST,
        FAIL
    }
    suspend fun User.addSongToPlaylist(playlist: MyPlaylist, song: Song): AddToPlaylistResponse = this.addSongToPlaylist(playlist.id, song.id)
    suspend fun User.addSongToPlaylist(playlist: MyPlaylist, songId: Int): AddToPlaylistResponse = this.addSongToPlaylist(playlist.id, songId)
    suspend fun User.addSongToPlaylist(playlistId: String, song: Song): AddToPlaylistResponse = this.addSongToPlaylist(playlistId, song.id)
    suspend fun User.addSongToPlaylist(playlistId: String, songId: Int): AddToPlaylistResponse {
        try {
            val url = "$URL/playlist/add?uid=${
                withContext(Dispatchers.IO) {
                    URLEncoder.encode(uid, "utf-8")
                }
            }&playlistId=$playlistId&songId=$songId"
            val body = url.httpGet().body
            val json = JSONObject(body)
            return when(json.getInt("code")) {
                200 -> AddToPlaylistResponse.SUCCESS
                400 -> {
                    if(json.getString("message") == "Already exist song in playlist") AddToPlaylistResponse.EXIST
                    else AddToPlaylistResponse.FAIL
                }
                else -> AddToPlaylistResponse.FAIL
            }
        } catch (e: Exception) {return AddToPlaylistResponse.FAIL
        }
    }
    enum class RemoveFromPlaylistResponse {
        SUCCESS,
        FAIL
    }
    suspend fun User.removeSongFromPlaylist(playlist: MyPlaylist, song: Song): RemoveFromPlaylistResponse = this.removeSongFromPlaylist(playlist.id, song.id)
    suspend fun User.removeSongFromPlaylist(playlist: MyPlaylist, songId: Int): RemoveFromPlaylistResponse = this.removeSongFromPlaylist(playlist.id, songId)
    suspend fun User.removeSongFromPlaylist(playlistId: String, song: Song): RemoveFromPlaylistResponse = this.removeSongFromPlaylist(playlistId, song.id)
    suspend fun User.removeSongFromPlaylist(playlistId: String, songId: Int): RemoveFromPlaylistResponse {
        try {
            val url = "$URL/playlist/remove?uid=${
                withContext(Dispatchers.IO) {
                    URLEncoder.encode(uid, "utf-8")
                }
            }&playlistId=$playlistId&songId=$songId"
            val body = url.httpGet().body
            val json = JSONObject(body)
            return if(json.getInt("code") == 200) RemoveFromPlaylistResponse.SUCCESS
                else RemoveFromPlaylistResponse.FAIL
        } catch (e: Exception) {return RemoveFromPlaylistResponse.FAIL
        }
    }
    suspend fun Playlist.exist(song: Song, context: Context) = this.exist(song.id, context)
    suspend fun Playlist.exist(songId: Int, context: Context): Boolean {
        val playlist = getPlaylist(this.id, context)
        return !(playlist == null || !playlist.songIdList.contains(songId))
    }
    suspend fun User.getPlaylist(playlistId: String, context: Context, detailSongData: Boolean = false): Playlist? = getPlaylist(playlistId, context, detailSongData, uid)
    suspend fun getPlaylist(playlistId: String, context: Context, detailSongData: Boolean = false, uid: String? = null): Playlist? {
        val url = "$URL/playlist/get?${
            if(uid != null) {
                "uid=${
                    withContext(Dispatchers.IO) {
                        URLEncoder.encode(uid, "utf-8")
                    }
                }&"
            } else ""
        }playlistId=$playlistId"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return data.toPlaylist(context, detailSongData)
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
        val url = "$URL/playlist/list?name=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(this@playlists.name, "utf-8")
            }
        }&tag=${this@playlists.tag}${
            if(uid != null) "&uid=${withContext(Dispatchers.IO) { URLEncoder.encode(uid, "utf-8") }}"
            else ""
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
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
        val url = "$URL/playlist/search?title=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(title, "utf-8")
            }
        }&uid=$uid"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return listOf()
        val data = json.getJSONArray("data")
        val playlists = ArrayList<Playlist>()
        repeat(data.length()) {
            val playlist = data.getJSONObject(it)
            playlists.add(playlist.toPlaylist(context))
        }
        return playlists
    }


    suspend fun User.setMemo(songId: Int, memo: String): Boolean {
        val url = "$URL/memo/set?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&songId=$songId" +
        "&memo=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(memo, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return (json.getInt("code") == 200)
//            .also {
//                if(!it) return@also
//                memoList[songId] = memo
//            }
    }
    suspend fun User.removeMemo(songId: Int): Boolean {
        val url = "$URL/memo/remove?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&songId=$songId"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return json.getInt("code") == 200
    }
    suspend fun User.getMemo(song: Song): String? = this.getMemo(song.id)
    suspend fun User.getMemo(songId: Int): String? {
        if(memoList.contains(songId)) return memoList[songId]
        val url = "$URL/memo/get?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&songId=$songId"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return if(json.isNull("code") || json.isNull("memo")) null
        else json.getString("memo").also { memoList[songId] = it }
    }
    suspend fun User.loadMemoList(): Boolean {
        if(isLoadedMemoList) return false
        val url = "$URL/memo/list?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return false
        val data = json.getJSONArray("data")
        repeat(data.length()) {
            Memo(
                songId = data.getJSONObject(it).getInt("songId"),
                memo = data.getJSONObject(it).getString("memo")
            ).also { memoList[it.songId] = it.memo }
        }
        isLoadedMemoList = true
        return true
    }
    suspend fun User.memoList(): List<Memo>? {
        if(isLoadedMemoList) {
            return ArrayList<Memo>().apply {
                memoList.forEach { (songId, memo) ->
                    add(Memo(songId, memo))
                }
            }
        }
        val url = "$URL/memo/list?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONArray("data")
        val list = ArrayList<Memo>()
        repeat(data.length()) {
            Memo(
                songId = data.getJSONObject(it).getInt("songId"),
                memo = data.getJSONObject(it).getString("memo")
            ).also { memoList[it.songId] = it.memo }
        }
        return list
    }

    suspend fun User.addPlaylistToLibrary(playlistId: String): Boolean {
        val url = "$URL/library/add?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=${playlistId}"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return (json.getInt("code") == 200)
    }
    suspend fun User.removePlaylistFromLibrary(playlistId: String): Boolean {
        val url = "$URL/library/remove?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=${playlistId}"
        val body = url.httpGet().body
        val json = JSONObject(body)
        return (json.getInt("code") == 200)
    }
    suspend fun User.isExistInLibrary(playlistId: String): Boolean {
        val url = "$URL/library/isExist?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }&playlistId=${playlistId}"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.getInt("code") != 200) return false
        return json.getBoolean("isExist")
    }
    suspend fun User.libraryList(context: Context): List<Playlist>? {
        val url = "$URL/library/list?uid=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(uid, "utf-8")
            }
        }"
        val body = url.httpGet().body
        val json = JSONObject(body)
        if(json.getInt("code") != 200) return null
        val data = json.getJSONArray("data")
        val list = ArrayList<Playlist>()
        repeat(data.length()) {
            val playlistId = data.getString(it)
            val playlist = getPlaylist(playlistId, context, uid = this.uid) ?: return@repeat
            list.add(playlist)
        }
        return list
    }
}