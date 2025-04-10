package kr.blugon.tjfinder.module

import kr.blugon.tjfinder.ui.layout.lyricsList
import org.jsoup.Jsoup
import java.io.Serializable


val memoList = HashMap<Int, String>()
var isLoadedMemoList = false
open class Song(
    open val id: Int,
    open val title: String,
    open val singer: String,
    open val lyricist: String? = null,
    open val composer: String? = null,
    open val isMR: Boolean = false,
): Serializable {
    val memo: String? get() = memoList[id]
    val lyrics: Lyrics? get() {
        if(lyricsList[id] != null) return lyricsList[id]!!
        val jsoup = Jsoup.connect("https://www.tjmedia.co.kr/2006_renew/ZillerGasaService/gasa_view2.asp?pro=${this.id}")
        val document = try { jsoup.get() } catch (e: Exception) { return null }
        val lyricsDocument = document.select("#spanscroll > table > tbody > tr > td > pre:nth-child(4)").firstOrNull()?: return null
        val lyrics = Lyrics(lyricsDocument.html()
            .replace("\t", "")
            .replace("<br>", "\n")
            .replace("\n\n", "\n")
            .replace("&amp;", "&")
            .replace("&#035;", "#")
            .trimEnd('\n')
        )
        lyricsList[id] = lyrics
        return lyrics
    }

//    private fun String.removeTextBetweenBrackets(): String {
//        // 정규식을 사용하여 「과 」사이에 있는 글자를 찾아 제거합니다.
//        return this.replace(Regex("「.*?」"), "").replace("\t", "")
//    }
}

class Top100Song(
    val type: SongType,
    val top: Int,
    override val id: Int,
    override val title: String,
    override val singer: String,
    override val lyricist: String? = null,
    override val composer: String? = null,
    override val isMR: Boolean = false,
): Song(id, title, singer, lyricist, composer)

class PlaylistSong(
    override val id: Int,
    override val title: String,
    override val singer: String,
    override val lyricist: String?,
    override val composer: String?,
    override val isMR: Boolean = false,
    val playlist: Playlist
): Song(id, title, singer, lyricist, composer) {
    constructor(song: Song, playlist: Playlist): this(song.id, song.title, song.singer, song.lyricist, song.composer, song.isMR, playlist)

    override fun toString(): String {
        return """
            {
                "id": $id,
                "title": "${title.replace("\\\"", "\\\\\"")}",
                "singer": "${singer.replace("\\\"", "\\\\\"")}",
                "lyricist": "${lyricist?.replace("\\\"", "\\\\\"")}",
                "composer": "${composer?.replace("\\\"", "\\\\\"")}",
                "isMR": $isMR,
                "playlistId": "${playlist.id}"
            }
        """.trimIndent()
    }
}

enum class SongType(val code: Int, val displayName: String) {
    K_POP(1, "가요"),
    POP(2, "POP"),
    J_POP(3, "J-POP");

    companion object {
        fun valueOf(type: String): SongType {
            return when(type) {
                "가요" -> K_POP
                "POP" -> POP
                "J-POP" -> J_POP
                else -> K_POP
            }
        }
    }
}

data class Memo(val songId: Int, val memo: String)