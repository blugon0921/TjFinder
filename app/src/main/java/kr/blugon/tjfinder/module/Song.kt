package kr.blugon.tjfinder.module

import android.database.Cursor
import kr.blugon.tjfinder.module.database.getBoolean
import kr.blugon.tjfinder.module.database.getInt
import kr.blugon.tjfinder.module.database.getString
import kr.blugon.tjfinder.module.database.getStringOrNull
import kr.blugon.tjfinder.ui.layout.lyricsList
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.Serializable


val memoList = HashMap<Int, String>()
var isLoadedMemoList = false
open class Song(
    open val id: Int,
    open val title: String,
    open val singer: String,
    open val lyricist: String,
    open val composer: String,
    open val isMR: Boolean = false,
    open val isMV: Boolean = false,
    open val albumArtUrl: String? = null,
): Serializable {
    constructor(json: JSONObject): this(
        id = json.getInt("pro"),
        title = json.getString("indexTitle"),
        singer = json.getString("indexSong"),
        lyricist = json.getString("word"),
        composer = json.getString("com"),
        isMR = json.getString("icongubun") == "MR",
        isMV = json.getString("mv_yn") == "Y",
        albumArtUrl = if(json.isNull("imgthumb_path")) null
        else json.getString("imgthumb_path")
    )
    constructor(cursor: Cursor): this(
        id = cursor.getInt("id"),
        title = cursor.getString("title"),
        singer = cursor.getString("singer"),
        lyricist = cursor.getString("lyricist"),
        composer = cursor.getString("composer"),
        isMR = cursor.getBoolean("isMR"),
        isMV = cursor.getBoolean("isMV"),
        albumArtUrl = cursor.getStringOrNull("albumArtUrl")
    )

    val memo: String? get() = memoList[id]
//    val lyrics: Lyrics? get() {
//        if(lyricsList[id] != null) return lyricsList[id]!!
//        val jsoup = Jsoup.connect("https://www.tjmedia.co.kr/2006_renew/ZillerGasaService/gasa_view2.asp?pro=${this.id}")
//        val document = try { jsoup.get() } catch (e: Exception) { return null }
//        val lyricsDocument = document.select("#spanscroll > table > tbody > tr > td > pre:nth-child(4)").firstOrNull()?: return null
//        val lyrics = Lyrics(lyricsDocument.html()
//            .replace("\t", "")
//            .replace("<br>", "\n")
//            .replace("\n\n", "\n")
//            .replace("&amp;", "&")
//            .replace("&#035;", "#")
//            .trimEnd('\n')
//        )
//        lyricsList[id] = lyrics
//        return lyrics
//    }
    override fun toString(): String {
        return """
            {
                "id": $id,
                "title": "${title.replace("\\\"", "\\\\\"")}",
                "singer": "${singer.replace("\\\"", "\\\\\"")}",
                "lyricist": "${lyricist.replace("\\\"", "\\\\\"")}",
                "composer": "${composer.replace("\\\"", "\\\\\"")}",
                "isMR": $isMR,
                "isMV": $isMV,
                "albumArtUrl": "${albumArtUrl?.replace("\\\"", "\\\\\"")}"
            }
        """.trimIndent()
    }
}

open class Top100Song(
    open val type: Top100Type,
    open val rank: Int,
    override val id: Int,
    override val title: String,
    override val singer: String,
    override val lyricist: String,
    override val composer: String,
    override val isMR: Boolean = false,
    override val isMV: Boolean = false,
    override val albumArtUrl: String? = null,
): Song(id, title, singer, lyricist, composer, isMR, isMV, albumArtUrl) {
    constructor(type: Top100Type, rank: Int, song: Song) : this(
        type,
        rank,
        song.id,
        song.title,
        song.singer,
        song.lyricist,
        song.composer,
        song.isMR,
        song.isMV,
        song.albumArtUrl
    )
}

class PlaylistSong(
    override val id: Int,
    override val title: String,
    override val singer: String,
    override val lyricist: String,
    override val composer: String,
    override val isMR: Boolean = false,
    override val isMV: Boolean = false,
    override val albumArtUrl: String? = null,
    val playlist: Playlist
): Song(id, title, singer, lyricist, composer, isMR, isMV, albumArtUrl) {
    constructor(song: Song, playlist: Playlist): this(
        song.id,
        song.title,
        song.singer,
        song.lyricist,
        song.composer,
        song.isMR,
        song.isMV,
        song.albumArtUrl,
        playlist
    )

    override fun toString(): String {
        return """
            {
                "id": $id,
                "title": "${title.replace("\\\"", "\\\\\"")}",
                "singer": "${singer.replace("\\\"", "\\\\\"")}",
                "lyricist": "${lyricist.replace("\\\"", "\\\\\"")}",
                "composer": "${composer.replace("\\\"", "\\\\\"")}",
                "isMR": $isMR,
                "isMV": $isMV,
                "albumArtUrl": "${albumArtUrl?.replace("\\\"", "\\\\\"")}",
                "playlistId": "${playlist.id}"
            }
        """.trimIndent()
    }
}

enum class Top100Type(val code: Int, val displayName: String) {
//    SYNTHESIS(0, "종합"),
    K_POP(1, "가요"),
    POP(2, "POP"),
    J_POP(3, "J-POP"),
//    BALLADE(4, "발라드"),
//    DANCE(5, "댄스"),
//    TROT(6, "트로트"),
//    FOLK(7, "포크"),
//    OST(8, "OST"),
//    ROCK_METAL(9, "락/메탈"),
//    RAP_HIPHOP(10, "랩/힙합"),
//    RA_URBAN(11, "R&A/어반");
}

data class Memo(val songId: Int, val memo: String)