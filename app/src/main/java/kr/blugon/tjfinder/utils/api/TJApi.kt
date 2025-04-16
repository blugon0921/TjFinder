package kr.blugon.tjfinder.utils.api

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import fuel.Parameters
import fuel.httpGet
import kr.blugon.tjfinder.module.OtherUser
import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.Top100Type
import kr.blugon.tjfinder.module.Top100Song
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat

private fun String.jsoupHttpGet(parameters: Parameters): Connection {
    return Jsoup.connect(
        "$this?${
            parameters.joinToString("&") {
                "${
                    URLEncoder.encode(it.first, Charset.forName("utf-8"))
                }=${
                    URLEncoder.encode(it.second, Charset.forName("utf-8"))
                }"
            }
        }"
    )
}
object TJApi {
    private const val URL = "https://tjmedia.com/legacy/api"

    private const val SEARCH_URL = "https://www.tjmedia.com/song/accompaniment_search"
    private const val TOP_URL = "$URL/topAndHot100"
    private const val MONTH_NEW_URL = "$URL/newSongOfMonth"

    operator fun get(id: Int): Song? = search("$id", StringType.Id, true).firstOrNull()
    fun search(keyword: String, type: StringType, match: Boolean = false): List<Song> {
        val jsoup = SEARCH_URL.jsoupHttpGet(
            parameters = listOf(
                "strType" to type.code.toString(),
                "searchTxt" to keyword,
                "pageRowCnt" to "500",
                "strWord" to if (match) "Y" else "N"
            )
        ).timeout(10000)
        val document = jsoup.get()
        val songs = ArrayList<Song>()
        repeat((document.select("#wrap > div > div > div.music.chart-top.type2 > div > ul").first()?.childrenSize() ?: return listOf()) -1) {
            val song = document.select("#wrap > div > div > div.music.chart-top.type2 > div > ul > li:nth-child(${it + 2})").first()?: return@repeat
            val elements = song.firstElementChild()?: return@repeat
            if(elements.childrenSize() == 0) return@repeat
            songs.add(Song(
                id = elements.child(0).child(0).child(1).text().toIntOrNull()?: return@repeat,
                title = if(elements.child(1).child(0).child(0).hasClass("no-ico"))
                    elements.child(1).child(0).child(1).text()
                else elements.child(1).child(0).child(2).text(),
                singer = elements.child(2).text(),
                lyricist = elements.child(3).text(),
                composer = elements.child(4).text(),
                isMR = elements.child(1).child(0).child(0).hasClass("mr"),
                isMV = elements.child(1).child(0).child(0).hasClass("mv"),
            ))
        }
        return songs
    }

    fun searchWithId(id: Int, match: Boolean = false): List<Song> = search("$id", StringType.Id, match)
    fun searchWithTitle(title: String, match: Boolean = false): List<Song> = search(title, StringType.Title, match)
    fun searchWithSinger(singer: String, match: Boolean = false): List<Song> = search(singer, StringType.Singer, match)

    suspend fun monthPopular(type: Top100Type = Top100Type.K_POP): List<Top100Song> {
        val response = TOP_URL.httpGet(parameters = listOf("strType" to type.code.toString()))
        val songList = ArrayList<Top100Song>()
        if(response.statusCode != 200) return songList
        val json = JSONObject(response.body)
        val data = json.getJSONObject("resultData")
        data.getJSONArray("items").let { items ->
            repeat(items.length()) {
                val item = items.getJSONObject(it)
                songList.add(Top100Song(
                    type = type,
                    rank = item.getString("rank").toIntOrNull()?: -1,
                    song = Song(item)
                ))
            }
        }
        songList.sortBy { it.rank }
        return songList
    }


    @SuppressLint("SimpleDateFormat")
    suspend fun monthNew(): List<Song> {
        val date = SimpleDateFormat("yyyyMM").format(System.currentTimeMillis())
        val response = MONTH_NEW_URL.httpGet(parameters = listOf("searchYm" to date))
        val songList = ArrayList<Song>()
        if(response.statusCode != 200) return songList
        val json = JSONObject(response.body)
        val data = json.getJSONObject("resultData")
        data.getJSONArray("items").let { items ->
            repeat(items.length()) {
                val item = items.getJSONObject(it)
                songList.add(Song(item))
            }
        }
        return songList
    }
}

enum class StringType(val code: Int, val sizeCode: Int = code) {
    Id(16, 5),
    Title(1),
    Singer(2)
}