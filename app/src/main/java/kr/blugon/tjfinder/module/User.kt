package kr.blugon.tjfinder.module

import android.content.Context
import android.net.Uri

open class NoTagUser(
    open val uid: String,
    open val email: String,
    open val name: String,
    open val photoUrl: Uri
)

data class User(
    override val uid: String,
    override val email: String,
    override val name: String,
    val tag: String,
    override val photoUrl: Uri
): NoTagUser(uid, email, name, photoUrl) {
    companion object {
        suspend fun login(context: Context): User? {
            return if(LoginManager.getSavedUid(context) != null) BlugonTJApi.login(LoginManager.getSavedUid(context)!!)
            else null
        }
    }
}


data class OtherUser(
    val name: String,
    val tag: String,
    val photoUrl: Uri
)