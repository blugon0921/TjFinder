package kr.blugon.tjfinder.utils.api

import kr.blugon.tjfinder.utils.api.finder.PlaylistApi
import kr.blugon.tjfinder.utils.api.finder.UserApi

object TjFinderApi {
    const val RequestURL = "https://tjfinderapi.blugon.kr"

    val User = UserApi()
    val Playlist = PlaylistApi()
}