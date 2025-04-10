package kr.blugon.tjfinder.module

import android.content.Context
import kr.blugon.tjfinder.utils.api.TjFinderApi
import org.json.JSONObject

class UnregisteredUser(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String,
)


data class OtherUser(
    val name: String,
    val tag: String,
    val avatar: String,
    val description: String,
) {
    constructor(json: JSONObject) : this(
        json.getString("name"),
        json.getString("tag"),
        json.getString("avatar"),
        json.getString("description")
    )
}

data class User(
    val uid: String,
    val email: String,
    val name: String,
    val tag: String,
    val avatar: String,
    val description: String,
    val isPrivate: Boolean,
) {
    constructor(uid: String, json: JSONObject) : this(
        uid,
        json.getString("email"),
        json.getString("name"),
        json.getString("tag"),
        json.getString("avatar"),
        json.getString("description"),
        json.getBoolean("isPrivate")
    )


    companion object {
        suspend fun login(context: Context): User? {
            return if(LoginManager.getSavedUid(context) != null) TjFinderApi.User.login(LoginManager.getSavedUid(context)!!)
            else null
        }
    }

    fun toOtherUser(): OtherUser {
        return OtherUser(
            this.name,
            this.tag,
            this.avatar,
            this.description
        )
    }
}