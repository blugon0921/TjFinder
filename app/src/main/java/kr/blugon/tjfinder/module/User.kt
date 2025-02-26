package kr.blugon.tjfinder.module

import android.content.Context
import kr.blugon.tjfinder.utils.api.TjFinderApi

open class NoTagUser(
    open val uid: String,
    open val email: String,
    open val name: String,
    open val photoUrl: String,
)


data class OtherUser(
    val name: String,
    val tag: String,
    val photoUrl: String,
    val description: String,
)

data class User(
    override val uid: String,
    override val email: String,
    override val name: String,
    val tag: String,
    override val photoUrl: String,
    val description: String,
    val isPrivate: Boolean,
): NoTagUser(uid, email, name, photoUrl) {
    companion object {
        suspend fun login(context: Context): User? {
            return if(LoginManager.getSavedUid(context) != null) TjFinderApi.login(LoginManager.getSavedUid(context)!!)
            else null
        }
    }

    fun toOtherUser(): OtherUser {
        return OtherUser(
            this.name,
            this.tag,
            this.photoUrl,
            this.description
        )
    }
}