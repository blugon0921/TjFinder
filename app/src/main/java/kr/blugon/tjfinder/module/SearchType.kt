package kr.blugon.tjfinder.module

enum class SearchType {
    ID,
    TITLE,
    SINGER,
    LYRICIST,
    COMPOSER;

    val visibleName: String
        get() {
            return when (this) {
                ID -> "곡 번호"
                TITLE -> "곡 제목"
                SINGER -> "가수"
                LYRICIST -> "작사"
                COMPOSER -> "작곡"
            }
        }

    val apiName: String
        get() {
            return this.name.lowercase()
        }
}

enum class MatchType {
    INCLUDE,
    STARTSWITH,
    MATCH;


    val visibleName: String
        get() {
            return when (this) {
                INCLUDE -> "포함"
                STARTSWITH -> "시작"
                MATCH -> "완벽히 일치"
            }
        }

    val apiName: String
        get() {
            return when (this) {
                STARTSWITH -> "startsWith"
                else -> this.name.lowercase()
            }
        }
}