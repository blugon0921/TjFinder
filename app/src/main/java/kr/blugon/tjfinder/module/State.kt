package kr.blugon.tjfinder.module

enum class State {
    SUCCESS,
    LOADING,
    DEFAULT,
    FAIL,
    NOT_INTERNET_AVAILABLE
}

enum class SearchState {
    SUCCESS,
    NO_RESULT,
    SEARCHING,
    NOT_INTERNET_AVAILABLE
}