package kr.blugon.tjfinder.utils.api.finder

import fuel.httpPost
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.utils.api.TjFinderApi.RequestURL
import org.json.JSONObject

suspend fun User.setMemo(songId: Int, memo: String): Boolean {
    val url = "$RequestURL/memo/set"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "songId" to songId,
                "memo" to memo
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return (json.getInt("code") == 200)
}
suspend fun User.removeMemo(songId: Int): Boolean {
    val url = "$RequestURL/memo/remove"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "songId" to songId
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return json.getInt("code") == 200
}
suspend fun User.getMemo(song: Song): String? = this.getMemo(song.id)
suspend fun User.getMemo(songId: Int): String? {
    if(memoList.contains(songId)) return memoList[songId]
    val url = "$RequestURL/memo/get"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf(
                "uid" to uid,
                "songId" to songId
            )
        ).toString()
    )
    val json = JSONObject(response.body)
    return if(json.isNull("code") || json.isNull("memo")) null
    else json.getString("memo").also { memoList[songId] = it }
}
suspend fun User.memoList(): List<Memo>? {
    if(isLoadedMemoList) {
        return ArrayList<Memo>().apply {
            memoList.forEach { (songId, memo) ->
                add(Memo(songId, memo))
            }
        }
    }
    val url = "$RequestURL/memo/list"
    val response = url.httpPost(
        headers = mapOf("Content-Type" to "application/json"),
        body = JSONObject(
            mapOf("uid" to uid)
        ).toString()
    )
    val json = JSONObject(response.body)
    if(json.isNull("code") || json.getInt("code") != 200) return null
    val data = json.getJSONArray("data")
    val list = ArrayList<Memo>()
    repeat(data.length()) {
        Memo(
            songId = data.getJSONObject(it).getInt("songId"),
            memo = data.getJSONObject(it).getString("memo")
        ).also { memoList[it.songId] = it.memo }
    }
    if(!isLoadedMemoList) isLoadedMemoList = true
    return list
}