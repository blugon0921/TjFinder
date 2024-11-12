package kr.blugon.tjfinder.module

import kr.blugon.tjfinder.module.BlugonTJApi.getMemo
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
                "title": "$title",
                "singer": "$singer",
                "lyricist": "$lyricist",
                "composer": "$composer",
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