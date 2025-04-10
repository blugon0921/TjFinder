package kr.blugon.tjfinder.utils.api.finder

import fuel.httpGet
import fuel.httpPost
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.utils.api.TjFinderApi.RequestURL
import org.json.JSONObject

class UserApi {
    suspend fun register(uid: String, email: String, name: String, avatar: String): User? {
        val url = "$RequestURL/user/register"
        val response = url.httpPost(
            headers = mapOf("Content-Type" to "application/json"),
            body = JSONObject(
                mapOf(
                    "uid" to uid,
                    "email" to email,
                    "name" to name,
                    "avatar" to avatar
                )
            ).toString()
        )
        val json = JSONObject(response.body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return User(uid, data)
    }
    suspend fun login(uid: String): User? {
        val url = "$RequestURL/user/login"
        val response = url.httpPost(
            headers = mapOf("Content-Type" to "application/json"),
            body = JSONObject(
                mapOf("uid" to uid)
            ).toString()
        )
        val json = JSONObject(response.body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return User(
            uid,
            data.getString("email"),
            data.getString("name"),
            data.getString("tag"),
            data.getString("avatar"),
            data.getString("description"),
            data.getBoolean("isPrivate")
        )
    }
    suspend fun get(name: String, tag: String): OtherUser? {
        val url = "$RequestURL/user/get"
        val response = url.httpGet(
            parameters = listOf(
                "name" to name,
                "tag" to tag
            )
        )
        val json = JSONObject(response.body)
        if(json.isNull("code") || json.getInt("code") != 200) return null
        val data = json.getJSONObject("data")
        return OtherUser(
            data.getString("name"),
            data.getString("tag"),
            data.getString("avatar"),
            data.getString("description")
        )
    }
    suspend fun search(name: String): List<OtherUser> {
        val url = "$RequestURL/user/search"
        val response = url.httpGet(parameters = listOf("name" to name))
        val json = JSONObject(response.body)
        if(json.isNull("code") || json.getInt("code") != 200) return listOf()
        val data = json.getJSONArray("data")
        val users = ArrayList<OtherUser>()
        repeat(data.length()) {
            val user = data.getJSONObject(it)
            users.add(
                OtherUser(
                    user.getString("name"),
                    user.getString("tag"),
                    user.getString("avatar"),
                    user.getString("description"),
                )
            )
        }
        return users
    }
}

suspend fun User.editName(newName: String): Boolean {
    val url = "$RequestURL/user/edit/name"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "name" to newName
            )
        ).toString(),
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editAvatar(newImageUrl: String): Boolean {
    val url = "$RequestURL/user/edit/avatar"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "avatar" to newImageUrl
            )
        ).toString(),
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editDescription(description: String): Boolean {
    val url = "$RequestURL/user/edit/description"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "description" to description
            )
        ).toString(),
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.editIsPrivate(isPrivate: Boolean): Boolean {
    val url = "$RequestURL/user/edit/isPrivate"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "isPrivate" to isPrivate
            )
        ).toString(),
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}