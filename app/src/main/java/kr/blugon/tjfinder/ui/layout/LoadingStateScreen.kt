package kr.blugon.tjfinder.ui.layout

import androidx.compose.foundation.lazy.LazyListScope
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.ui.layout.state.LoadFail
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork


fun LazyListScope.LoadingStateScreen(
    state: State,
    fail: () -> Unit = {},
    loadingMessage: String? = null,
    success: () -> Unit = {},
) {
    when(state) {
        State.DEFAULT -> {}
        State.SUCCESS -> {
            success()
            return
        }
        State.LOADING -> items(1) {Loading(loadingMessage)}
        State.FAIL -> items(1) {LoadFail()}
        State.NOT_INTERNET_AVAILABLE -> items(1) {NotConnectedNetwork()}
    }
    fail()
}