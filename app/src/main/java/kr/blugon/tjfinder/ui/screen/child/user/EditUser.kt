package kr.blugon.tjfinder.ui.screen.child.user

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kr.blugon.tjfinder.MainActivity
import kr.blugon.tjfinder.module.*
import kr.blugon.tjfinder.module.BlugonTJApi.editDescription
import kr.blugon.tjfinder.module.BlugonTJApi.editIsPrivate
import kr.blugon.tjfinder.module.BlugonTJApi.editName
import kr.blugon.tjfinder.module.BlugonTJApi.editProfileImage
import kr.blugon.tjfinder.module.BlugonTJApi.playlists
import kr.blugon.tjfinder.module.State
import kr.blugon.tjfinder.module.api.FinderApi
import kr.blugon.tjfinder.module.api.FinderResponse
import kr.blugon.tjfinder.ui.layout.PretendardText
import kr.blugon.tjfinder.ui.layout.state.Loading
import kr.blugon.tjfinder.ui.layout.state.NotConnectedNetwork
import kr.blugon.tjfinder.ui.screen.child.EditTextField
import kr.blugon.tjfinder.ui.screen.child.playlist.FileUtil
import kr.blugon.tjfinder.ui.theme.ThemeColor
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
fun EditUserScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var state by remember { mutableStateOf(State.DEFAULT) }

    var defaultName by remember { mutableStateOf("") }
    val name = remember { mutableStateOf(defaultName) }

    var defaultDescription by remember { mutableStateOf("") }
    val description = remember { mutableStateOf(defaultDescription) }

    var defaultProfileImage by remember { mutableStateOf("") }
    val profileImage = remember { mutableStateOf(defaultProfileImage) }

    var defaultIsPrivate by remember { mutableStateOf(false) }
    val isPrivate = remember { mutableStateOf(defaultIsPrivate) }

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)) {
            state = State.NOT_INTERNET_AVAILABLE
            return@LaunchedEffect
        }
        user = User.login(context)?: return@LaunchedEffect
        defaultName = user!!.name
        name.value = defaultName

        defaultDescription = user!!.description
        description.value = defaultDescription

        defaultProfileImage = user!!.photoUrl
        profileImage.value = defaultProfileImage

        defaultIsPrivate = user!!.isPrivate
        isPrivate.value = defaultIsPrivate
        state = State.SUCCESS
    }

    if(state == State.NOT_INTERNET_AVAILABLE) return NotConnectedNetwork()
    if(state == State.DEFAULT) return Loading("유저 설정 불러오는중")
    if(state == State.LOADING) return Loading("유저 설정 저장중")


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

        retrofit.sendImage(body).enqueue(object: Callback<FinderResponse> {
            override fun onResponse(call: Call<FinderResponse>, response: Response<FinderResponse>) {
                val body = response.body()
                if(response.isSuccessful && body?.code == 200) {
                    profileImage.value = body.imagePath!!
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
                title = { PretendardText(text = "프로필 편집", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
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
                Box { //프로필 박스
                    AsyncImage( //프로필사진
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(255, 255, 255, 64), CircleShape),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(user!!.photoUrl))
                            .build(),
                        contentDescription = "profile",
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
                label = "이름",
                value = name,
                maxLength = 10,
                keyboardManager = keyboardManager,
            )
            EditTextField(
                label = "소개",
                value = description,
                maxLength = 100,
                maxLines = 10,
                isEssential = false,
                keyboardManager = keyboardManager,
            )

            Row(
                modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PretendardText(text = "비공개") //isPrivate
                Switch(
                    modifier = Modifier.padding(start = 5.dp),
                    checked = isPrivate.value,
                    onCheckedChange = { isPrivate.value = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ThemeColor.Main,
                        uncheckedTrackColor = ThemeColor.MainGray,
                        uncheckedBorderColor = ThemeColor.MainGray,
                        uncheckedThumbColor = ThemeColor.Main,
                        checkedThumbColor = Color.White
                    )
                )
            }

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
                            if (user!!.playlists(context) == null) return@launch
                            state = State.LOADING
                            val successCheck = listOf( //서버 업로드
                                defaultProfileImage == profileImage.value ||
                                user!!.editProfileImage(name.value.ifBlank { defaultProfileImage }),

                                defaultName.trim() == name.value.trim() ||
                                user!!.editName(name.value.ifBlank {
                                    state = State.SUCCESS
                                    return@launch Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                                }.trim()),

                                defaultDescription.trim().trim('\n') ==
                                    description.value.trim().trim('\n') ||
                                user!!.editDescription(description.value.trim().trim('\n')),

                                defaultIsPrivate == isPrivate.value ||
                                user!!.editIsPrivate(isPrivate.value),
                            )
                            Toast.makeText(context,
                                if(!successCheck.contains(false)) "변경사항을 저장했습니다"
                                else "설정 저장에 실패했습니다"
                            ,Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                ) { PretendardText(text = "저장", fontWeight = FontWeight.Bold) }
            }
        }
    }
}