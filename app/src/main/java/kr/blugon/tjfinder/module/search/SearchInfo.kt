package kr.blugon.tjfinder.module.search

import kr.blugon.tjfinder.module.PlaylistSortType
import kr.blugon.tjfinder.module.SongSortType


data class SearchInfo(
    val input: String = "",
    val category: SearchCategory = SearchCategory.Song,
    val states: SearchStates = SearchStates(),
    val results: SearchResults = SearchResults(),
    val sort: SearchSort = SearchSort()
)

data class SearchSort(
    var song: SongSortType = SongSortType.SINGER,
    var playlist: PlaylistSortType = PlaylistSortType.TITLE
)