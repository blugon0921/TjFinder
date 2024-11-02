package kr.blugon.tjfinder.module


enum class SortType(val visibleName: String) {
    ID("번호"),
    TITLE("제목"),
    SINGER("가수");
}

enum class PlaylistSortType(val visibleName: String) {
    SONG_COUNT("곡 개수"),
    TITLE("제목"),
    ID("ID"),
    CREATOR("만든이");
}