package kr.blugon.tjfinder.utils.api

import kr.blugon.tjfinder.module.Song
import kr.blugon.tjfinder.module.SongType
import kr.blugon.tjfinder.module.Top100Song
import org.jsoup.Jsoup
import java.net.URLEncoder

object TJApi {
    private const val URL = "https://www.tjmedia.com/tjsong"

    private const val SEARCH_URL = "$URL/song_search_list.asp"
    private const val POPULAR_URL = "$URL/song_monthPopular.asp"
    private const val MONTH_NEW_URL = "$URL/song_monthNew.asp"

    operator fun get(id: Int): Song? = search("$id", StringType.Id, true).firstOrNull()
    fun search(keyword: String, type: StringType, match: Boolean = false): List<Song> {
        val jsoup = Jsoup.connect("$SEARCH_URL?strType=${type.code}&strText=${
            URLEncoder.encode(keyword, "utf-8")
        }&strSize0${type.sizeCode}=100&strCond=${if(match) 1 else 0}").timeout(10000)
        val document = jsoup.get()
        val songs = ArrayList<Song>()
        repeat((document.select("#BoardType1 > table > tbody").first()?.childrenSize() ?: return listOf()) -1) {
            val song = document.select("#BoardType1 > table > tbody > tr:nth-child(${it + 2})").first()?: return@repeat
            val elements = song.allElements.first()?: return@repeat
            songs.add(
                Song(
                elements.child(0).text().toIntOrNull()?: return@repeat, //id
                elements.child(1).text(), //title
                elements.child(2).text(), //singer
                elements.child(3).text(), //lyricist
                elements.child(4).text(), //composter
                (if( //isMR
                    elements.child(1).childrenSize() == 1 ||
                    elements.child(1).childrenSize() == 3
                ) elements.child(1).children().getOrNull(1)?.classNames()?.contains("mr_icon") //isMR
                else elements.child(1).children().getOrNull(2)?.classNames()?.contains("mr_icon"))
                ?: false,
            )
            )
        }
        return songs
    }

    fun searchWithId(id: Int, match: Boolean = false): List<Song> = search("$id", StringType.Id, match)
    fun searchWithTitle(title: String, match: Boolean = false): List<Song> = search(title, StringType.Title, match)
    fun searchWithSinger(singer: String, match: Boolean = false): List<Song> = search(singer, StringType.Singer, match)

    fun monthPopular(type: SongType = SongType.K_POP): List<Top100Song> {
        val jsoup = Jsoup.connect("$POPULAR_URL?strType=${type.code}")
        val document = jsoup.get()
        val songs = ArrayList<Top100Song>()
        repeat((document.select("#BoardType1 > table > tbody").first()?.childrenSize() ?: return listOf()) -1) {
            val song = document.select("#BoardType1 > table > tbody > tr:nth-child(${it + 2})").first()?: return@repeat
            val elements = song.allElements.first()?: return@repeat
            songs.add(
                Top100Song(
                type,
                elements.child(0).text().toIntOrNull()?: return@repeat,
                elements.child(1).text().toInt(),
                elements.child(2).text(),
                elements.child(3).text()
            )
            )
        }
        songs.sortBy { it.top }
        return songs
    }


    fun monthNew(): List<Song> {
        val document = Jsoup.connect(MONTH_NEW_URL).get()
        val songs = ArrayList<Song>()
        repeat((document.select("#BoardType1 > table > tbody").first()?.childrenSize() ?: return listOf()) -1) {
            val song = document.select("#BoardType1 > table > tbody > tr:nth-child(${it + 2})").first()?: return@repeat
            val elements = song.allElements.first()?: return@repeat
            songs.add(
                Song(
                elements.child(0).text().toIntOrNull()?: return@repeat,
                elements.child(1).text(),
                elements.child(2).text(),
                elements.child(3).text(),
                elements.child(4).text(),
                elements.children().getOrNull(1)?.children()?.getOrNull(1)?.classNames()?.contains("mr_icon")?: false,
            )
            )
        }
        return songs
    }
}

enum class StringType(val code: Int, val sizeCode: Int = code) {
    Id(16, 5),
    Title(1),
    Singer(2)
}