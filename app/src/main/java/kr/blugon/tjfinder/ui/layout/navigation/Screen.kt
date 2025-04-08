package kr.blugon.tjfinder.ui.layout.navigation

import androidx.annotation.StringRes
import kr.blugon.tjfinder.R


interface Screen {
    val isFullScreen: Boolean
//    val number: Int

    val name: String

    companion object {
        fun valueOf(name: String): Screen? {
//        fun valueOf(name: String): Screen {
            return entries.firstOrNull { it.name == name }
//            return BottomScreen.Home
        }

        val entries: List<Screen>
            get() = ArrayList<Screen>().apply {
                addAll(DefaultScreen.entries)
                addAll(BottomScreen.entries)
                addAll(ChildScreen.entries)
            }
    }
}

enum class DefaultScreen(
    override val isFullScreen: Boolean,
): Screen {
    Main(false),
    Login(true);
}

enum class BottomScreen(
    @StringRes val title: Int,
    val icon: Int,
//    override val number: Int,
    val number: Int,
    override val isFullScreen: Boolean = false
): Screen {
    NewSongs(R.string.text_newsongs, R.drawable.starlight, 0),
    Search(R.string.text_search, R.drawable.search, 1),
    Home(R.string.text_home, R.drawable.home, 2),
    Playlist(R.string.text_playlist, R.drawable.playlist, 3),
    User(R.string.text_user, R.drawable.user, 4)
}


enum class ChildScreen(
    val parent: BottomScreen,
    override val isFullScreen: Boolean = false
): Screen {
    PlaylistItem(BottomScreen.Playlist),
    CreatePlaylist(BottomScreen.Playlist, true),
    EditPlaylist(BottomScreen.Playlist, true),

    SearchPlaylist(BottomScreen.Search),
    SearchOtherUser(BottomScreen.Search),

    OtherUserItem(BottomScreen.User),

    EditUser(BottomScreen.User, true),
    Setting(BottomScreen.User, true);

//    override val number: Int get() = this.parent.number
}