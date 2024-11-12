package kr.blugon.tjfinder.module


enum class SongSortType(override val displayName: String) : SortType {
    ID("번호"),
    TITLE("제목"),
    SINGER("가수");
}

enum class PlaylistSortType(override val displayName: String): SortType {
    SONG_COUNT("곡 개수"),
    TITLE("제목"),
    ID("ID"),
    CREATOR("만든이");
}

interface SortType {
    val displayName: String
}