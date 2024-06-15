package org.d3if3156.skyroom.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3156.skyroom.R
import org.d3if3156.skyroom.model.Sky
import org.d3if3156.skyroom.model.User
import org.d3if3156.skyroom.network.ApiStatus
import org.d3if3156.skyroom.network.SkyApi
import org.d3if3156.skyroom.network.UserDataStore
import org.d3if3156.skyroom.ui.theme.SKYROOMTheme
import org.d3if3156.skyroom.util.SettingsDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val poppinsblack = FontFamily(Font(R.font.poppinsblack))
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))

    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)

    val context = LocalContext.current
    val dataStoreUser = UserDataStore(context)
    val user by dataStoreUser.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showSkyDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showSkyDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = poppinsblack
                        )
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black),
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val options = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = false,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                },
                containerColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_sky),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.desc),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = poppinsregular
                )
            )
            Divider()
            ScreenContent(showList = true, viewModel, user.email, Modifier.padding(padding))

            if (showDialog) {
                ProfilDialog(
                    user = user,
                    onDismissRequest = { showDialog = false },
                    onConfirmation = {
                        CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStoreUser) }
                        showDialog = false
                    },
                    navController = navController
                )
            }
            if (showSkyDialog) {
                SkyDialog(
                    bitmap = bitmap,
                    onDismissRequest = { showSkyDialog = false }
                ) { nama_rasibintang, daerah_langitdifoto ->
                    viewModel.saveData(user.email, nama_rasibintang, daerah_langitdifoto, bitmap!!)
                    showSkyDialog = false
                }
            }
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }
}

    @Composable
fun ScreenContent(showList: Boolean, viewModel: MainViewModel, userId: String, modifier: Modifier) {
    val poppinslight = FontFamily(Font(R.font.poppinslight))

    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }
    if (data.isEmpty()) {
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val image = painterResource(id = R.drawable.emptydata)
            Image(painter = image, contentDescription = null)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = R.string.listkosong),
                fontFamily = poppinslight )
        }
    }
    else {
        if (showList) {

    when (status) {
        ApiStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(data) { sky ->
                    GridItem(sky = sky, onDelete = {
                        viewModel.deleteData(userId, sky.id)
                    })
                }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
        }
    }
}

@Composable
fun GridItem(sky: Sky, onDelete: (String) -> Unit) {
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))

    var showDialog by remember { mutableStateOf(false) }

    HapusDialog(
        openDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = {
            onDelete(sky.id)
            showDialog = false
        }
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(SkyApi.getSkyUrl(sky.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, sky.nama_rasibintang),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = sky.nama_rasibintang,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = poppinsregular
                )
                Text(
                    text = sky.daerah_langitdifoto,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = poppinsregular
                )
            }

                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
@Composable
fun ListItem(
    sky: Sky,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val poppinsregular = FontFamily(Font(R.font.poppinsregular))
    var showDialog by remember { mutableStateOf(false) }

    HapusDialog(
        openDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = {
            onDelete(sky.id)
            showDialog = false
        }
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(SkyApi.getSkyUrl(sky.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, sky.nama_rasibintang),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = sky.nama_rasibintang,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = poppinsregular
                )
                Text(
                    text = sky.daerah_langitdifoto,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = poppinsregular
                )
            }

            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.hapus),
                    tint = Color.White
                )
            }
        }
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User(), isLoggedIn = false)
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)

@Composable
fun ScreenPreview() {
    SKYROOMTheme {
        MainScreen(rememberNavController())
    }
}