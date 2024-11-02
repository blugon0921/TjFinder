package kr.blugon.tjfinder.ui.screen.child.playlist

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.BlugonTJApi.editThumbnailOfPlaylist
import kr.blugon.tjfinder.module.BlugonTJApi.editTitleOfPlaylist
import kr.blugon.tjfinder.module.BlugonTJApi.playlists
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.api.FinderApi
import kr.blugon.tjfinder.module.api.FinderResponse
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.screen.child.playlist.EditPlaylist.playlist
import kr.blugon.tjfinder.ui.theme.Pretendard
import kr.blugon.tjfinder.ui.theme.ThemeColor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream


object EditPlaylist {
    lateinit var playlist: MyPlaylist
}
@Composable
fun EditPlaylistScene(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.SUCCESS) }

    val defaultTitle = remember { playlist.title }
    var title by remember { mutableStateOf(defaultTitle) }

    val defaultThumbnail = remember { playlist.thumbnail }
    var thumbnail by remember { mutableStateOf(playlist.thumbnail) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
    }

    if(state == State.DEFAULT || state == State.NOT_INTERNET_AVAILABLE) return NotConnectedNetwork()
    if(state == State.LOADING) return Loading("플레이리스트 편집중")


    val retrofit = Retrofit
        .Builder()
        .baseUrl("${BlugonTJApi.rootTjFinderURL}/")
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

        retrofit.sendImage(body).enqueue(object: Callback<FinderResponse>{
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
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 20.dp),
            text = "플레이리스트 편집",
            fontFamily = Pretendard,
            fontSize = TextUnit(27f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Column(
            modifier = Modifier
                .clickable {
                    launcher.launch("image/*")
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box { //썸네일 박스
                AsyncImage( //썸네일
                    modifier = Modifier
                        .size(128.dp)
                        .border(2.dp, Color(255, 255, 255, 64), RoundedCornerShape(0.dp)),
                    model = ImageRequest.Builder(context)
                        .data(thumbnail)
                        .build(),
                    contentDescription = "thumbnail",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
            Text( //사진 업로드
                modifier = Modifier.padding(top = 5.dp),
                text = "사진 업로드",
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(15f, TextUnitType.Sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextField( //플레이리스트 타이틀 인풋
            modifier = Modifier
                .padding(top = 20.dp)
                .widthIn(170.dp, 350.dp)
                .focusRequester(focusRequester),
            value = title,
            onValueChange = {
                if(100 < it.length) {
                    Toast.makeText(context, "입력 가능한 최대 길이는 100자입니다", Toast.LENGTH_SHORT).show()
                    return@TextField
                }
                title = it
            },
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ThemeColor.ItemBackground,
                unfocusedContainerColor = ThemeColor.ItemBackground,
                cursorColor = ThemeColor.LightGray,
                focusedIndicatorColor = ThemeColor.LightGray,
                unfocusedIndicatorColor = ThemeColor.LightGray
            ),
            singleLine = true,
            placeholder = { //Place Holder
                Text(
                    modifier = Modifier.width(150.dp),
                    text = defaultTitle,
                    color = ThemeColor.LightGray,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardManager?.hide() })
        )
        Row(
            modifier = Modifier
                .padding(0.dp, 20.dp)
                .fillMaxWidth(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) { //버튼
            Button( //취소
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(20.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = { navController.popBackStack() },
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    modifier = Modifier,
                    text = "취소",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(21f, TextUnitType.Sp),
                )
            }
            Button(
                //확인
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Main
                ),
                contentPadding = PaddingValues(20.dp, 10.dp),
                shape = RoundedCornerShape(100.dp),
                onClick = {
                    coroutineScope.launch {
                        if (user!!.playlists(context) == null) return@launch
                        state = State.LOADING
                        var total = 0
                        var fail = 0
                        if(defaultTitle != title) {
                            total++
                            val newTitle = user!!.editTitleOfPlaylist(playlist, title.ifBlank { defaultTitle }) //서버 업로드
                            if (!newTitle) {
                                Toast.makeText(context, "플레이리스트 이름 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                                fail++
                            }
                        }
                        if(defaultThumbnail != thumbnail) {
                            total++
                            val newThumbnail = user!!.editThumbnailOfPlaylist(playlist, thumbnail) //서버 업로드
                            if (!newThumbnail) {
                                Toast.makeText(context, "플레이리스트 이미지 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                                fail++
                            }
                        }
                        if(fail == 0 && total != 0) Toast.makeText(context, "변경사항을 저장했습니다", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
            ) {
                Text(
                    modifier = Modifier,
                    text = "확인",
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(21f, TextUnitType.Sp),
                )
            }
        }
    }
}

object FileUtil {
    // 임시 파일 생성
    fun createTempFile(context: Context, fileName: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, fileName)
    }

    // 파일 내용 스트림 복사
    fun copyToFile(context: Context, uri: Uri, file: File) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(4 * 1024)
        while (true) {
            val byteCount = inputStream!!.read(buffer)
            if (byteCount < 0) break
            outputStream.write(buffer, 0, byteCount)
        }

        outputStream.flush()
    }
}