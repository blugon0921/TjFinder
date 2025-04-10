package kr.blugon.tjfinder.module

import android.content.Context
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object LoginManager {

    private const val PREF_NAME = "loginData"
    private const val KEY_UID = "uid"

    fun saveLoginInfo(context: Context, uid: String?) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_UID, uid)
        editor.apply()
    }

    fun getSavedUid(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_UID, null)
    }

    fun login(activityResult: ActivityResult, onSuccess: (AuthResult, UnregisteredUser) -> Unit, onException: (Task<AuthResult>?, Exception?) -> Unit = { task, exception -> }) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data).getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    onSuccess(result, UnregisteredUser(
                        result.user!!.uid,
                        result.user!!.email!!,
                        result.user!!.displayName!!,
                        result.user!!.photoUrl!!.toString()
                    ))
                } else {
                    onException(task, null)
                }
            }
        } catch (e: Exception) {
            onException(null, e)
        }
    }

    fun logout(context: Context) {
        saveLoginInfo(context, null)
    }
}