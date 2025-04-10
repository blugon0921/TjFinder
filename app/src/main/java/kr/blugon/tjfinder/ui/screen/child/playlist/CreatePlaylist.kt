package kr.blugon.tjfinder.ui.screen.child.playlist

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.module.Playlist
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.User
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.screen.child.EditTextField
import kr.blugon.tjfinder.ui.theme.ThemeColor
import kr.blugon.tjfinder.utils.api.FinderApi
import kr.blugon.tjfinder.utils.api.FinderResponse
import kr.blugon.tjfinder.utils.api.TjFinderApi
import kr.blugon.tjfinder.utils.api.finder.createPlaylist
import kr.blugon.tjfinder.utils.api.finder.editThumbnailOfPlaylist
import kr.blugon.tjfinder.utils.api.finder.playlists
import kr.blugon.tjfinder.utils.isInternetAvailable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylist(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    val playlistCollection = remember { mutableStateListOf<Playlist>() }
    var state by remember { mutableStateOf(State.SUCCESS) }

    var defaultTitle by remember { mutableStateOf("플레이리스트#") }
    val title = remember { mutableStateOf(defaultTitle) }

    val defaultThumbnail = "https://file.blugon.kr/image/tjfinder/defaultthumbnails/${Math.round(Math.random()*4)+1}.png"
    var thumbnail by remember { mutableStateOf(defaultThumbnail) }

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
        val playlists = user!!.playlists(context) ?: return@LaunchedEffect
        playlistCollection.addAll(playlists)
        defaultTitle = "플레이리스트#${playlistCollection.size+1}"
        title.value = defaultTitle
    }


    if(state == State.DEFAULT || state == State.NOT_INTERNET_AVAILABLE) return NotConnectedNetwork()
    if(state == State.LOADING) return Loading("플레이리스트 생성중")

    val retrofit = Retrofit
        .Builder()
        .baseUrl("${TjFinderApi.RequestURL}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FinderApi::class.java)

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val fileName = "${uri.toString().split("/").last()}.${context.contentResolver.getType(uri)!!.split("/").last()}"
        val file = FileUtil.createTempFile(context, fileName)
        FileUtil.copyToFile(context, uri, file)

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        retrofit.sendImage(body).enqueue(object: Callback<FinderResponse> {
            override fun onResponse(call: Call<FinderResponse>, response: Response<FinderResponse>) {
                val body = response.body()
                if(response.isSuccessful && body?.code == 200) {
                    thumbnail = body.imagePath!!
                    Toast.makeText(context, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                } else {
                    if(response.message() == "Request Entity Too Large") {
                        return Toast.makeText(context, "이미지가 너무 큽니다. 1MB까지 업로드 할 수 있습니다", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<FinderResponse>, t: Throwable) = Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
        })
    }

    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
            },
        topBar = { //Topbar
            TopAppBar(
                title = { PretendardText(text = "새 플레이리스트 만들기", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable { launcher.launch("image/*") },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box { //썸네일 박스
                    AsyncImage( //썸네일
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, Color(255, 255, 255, 64), RoundedCornerShape(0.dp)),
                        model = ImageRequest.Builder(context)
                            .data(thumbnail)
                            .build(),
                        contentDescription = "thumbnail",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
                PretendardText( //사진 업로드
                    modifier = Modifier.padding(top = 5.dp),
                    text = "사진 업로드",
                    color = ThemeColor.HyperLink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15f,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            EditTextField(
                label = "제목",
                value = title,
                maxLength = 30,
                keyboardManager = keyboardManager,
            )


            Row(
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalArrangement = Arrangement.End,
            ) {
                Button( //저장
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColor.Main
                    ),
                    contentPadding = PaddingValues(20.dp, 10.dp),
                    shape = RoundedCornerShape(100.dp),
                    onClick = {
                        coroutineScope.launch {
                            if(title.value.isBlank()) {
                                return@launch Toast.makeText(context, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                            }
                            state = State.LOADING
                            val createdPlaylist = user!!.createPlaylist(title.value.trim().trim('\n'))
                            if(createdPlaylist == null) {
                                Toast.makeText(context, "플레이리스트 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                            } else {
                                if(defaultThumbnail != thumbnail) {
                                    val newThumbnail = user!!.editThumbnailOfPlaylist(createdPlaylist, thumbnail) //서버 업로드
                                    if (!newThumbnail) Toast.makeText(context, "플레이리스트 이미지 설정에 실패했습니다", Toast.LENGTH_SHORT).show()
                                    else {
                                        Toast.makeText(context, "새 플레이리스트를 생성했습니다", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "새 플레이리스트를 생성했습니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                            navController.popBackStack()
                        }
                    },
                ) { PretendardText(text = "확인", fontWeight = FontWeight.Bold) }
            }
        }
    }
}