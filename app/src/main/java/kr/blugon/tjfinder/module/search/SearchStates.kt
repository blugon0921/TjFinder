package kr.blugon.tjfinder.module.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kr.blugon.tjfinder.module.SearchState

data class SearchStates(
    var song: SearchState = SearchState.NO_RESULT,
    var playlist: SearchState = SearchState.NO_RESULT,
    var user: SearchState = SearchState.NO_RESULT,
) {
    fun setAll(state: SearchState) {
        song = state
        playlist = state
        user = state
    }

    operator fun get(category: SearchCategory): SearchState {
        return when(category) {
            SearchCategory.Song -> song
            SearchCategory.Playlist -> playlist
            SearchCategory.User -> user
        }
    }
    operator fun set(category: SearchCategory, state: SearchState) {
        when(category) {
            SearchCategory.Song -> song = state
            SearchCategory.Playlist -> playlist = state
            SearchCategory.User -> user = state
        }
    }

    fun toStated(): StatedSearchStates = StatedSearchStates(
        mutableStateOf(this.song),
        mutableStateOf(this.playlist),
        mutableStateOf(this.user),
    )
}

data class StatedSearchStates(
    val song: MutableState<SearchState> = mutableStateOf(SearchState.NO_RESULT),
    val playlist: MutableState<SearchState> = mutableStateOf(SearchState.NO_RESULT),
    val user: MutableState<SearchState> = mutableStateOf(SearchState.NO_RESULT),
) {
    fun setAll(state: SearchState) {
        song.value = state
        playlist.value = state
        user.value = state
    }
    fun setAll(state: SearchStates) {
        song.value = state.song
        playlist.value = state.playlist
        user.value = state.user
    }

    operator fun get(category: SearchCategory): SearchState {
        return when(category) {
            SearchCategory.Song -> song.value
            SearchCategory.Playlist -> playlist.value
            SearchCategory.User -> user.value
        }
    }
    operator fun set(category: SearchCategory, state: SearchState) {
        when(category) {
            SearchCategory.Song -> song.value = state
            SearchCategory.Playlist -> playlist.value = state
            SearchCategory.User -> user.value = state
        }
    }

    fun toDefault(): SearchStates = SearchStates(
        song.value,
        playlist.value,
        user.value
    )
}