package com.current.hub

import android.Manifest
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import android.telecom.CallAudioState
import android.telephony.SmsManager
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.current.hub.ui.theme.CurrentTheme
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val WEB_CLIENT_ID =
    "340753714127-fm8i8ab477bq0ktb86v35mu7a7tm5muo.apps.googleusercontent.com"
private val AppSurfaceShape = RoundedCornerShape(28.dp)
private val AppCardShape = RoundedCornerShape(24.dp)

enum class ExpressiveButtonSize { XS, S, M, L, XL }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ExpressiveButtonSize = ExpressiveButtonSize.M,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val restingCorner = when (size) {
        ExpressiveButtonSize.XS -> 14.dp
        ExpressiveButtonSize.S -> 16.dp
        ExpressiveButtonSize.M -> 20.dp
        ExpressiveButtonSize.L -> 24.dp
        ExpressiveButtonSize.XL -> 28.dp
    }
    val pressedCorner = when (size) {
        ExpressiveButtonSize.XS -> 10.dp
        ExpressiveButtonSize.S -> 12.dp
        ExpressiveButtonSize.M -> 14.dp
        ExpressiveButtonSize.L -> 16.dp
        ExpressiveButtonSize.XL -> 18.dp
    }
    val animatedCorner = androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) pressedCorner else restingCorner,
        animationSpec = tween(durationMillis = 220, easing = LinearEasing),
        label = "expressive_button_shape_morph"
    ).value
    val minHeight = when (size) {
        ExpressiveButtonSize.XS -> 32.dp
        ExpressiveButtonSize.S -> 40.dp
        ExpressiveButtonSize.M -> 48.dp
        ExpressiveButtonSize.L -> 56.dp
        ExpressiveButtonSize.XL -> 64.dp
    }
    val contentPadding = when (size) {
        ExpressiveButtonSize.XS -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ExpressiveButtonSize.S -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ExpressiveButtonSize.M -> PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ExpressiveButtonSize.L -> PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ExpressiveButtonSize.XL -> PaddingValues(horizontal = 28.dp, vertical = 14.dp)
    }

    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(animatedCorner),
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        interactionSource = interactionSource,
        contentPadding = contentPadding,
        content = content
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setContent {
            val isSetupCompleteInitial = sharedPrefs.getBoolean("setup_complete", false)
            val googleUserNameInitial = sharedPrefs.getString("google_user_name", "") ?: ""
            val googleUserPhotoInitial = sharedPrefs.getString("google_user_photo", "") ?: ""
            val googleUserEmailInitial = sharedPrefs.getString("google_user_email", "") ?: ""
            val googleUserBirthdayInitial =
                sharedPrefs.getString("google_user_birthday", "Not set") ?: "Not set"
            val isGoogleConnectedInitial = sharedPrefs.getBoolean("google_connected", false)
            val seedColorLong = sharedPrefs.getLong("seed_color", -1L)
            val seedColor = if (seedColorLong != -1L) Color(seedColorLong.toULong()) else null

            var screenStack by remember {
                val initialScreen = if (isSetupCompleteInitial) "main_menu" else "welcome"
                mutableStateOf(listOf(initialScreen))
            }

            LaunchedEffect(intent) {
                val navigateToChat = intent?.getStringExtra("navigate_to_chat")
                if (navigateToChat != null) {
                    if (screenStack.last() != "chat/$navigateToChat") {
                        screenStack = listOf("main_menu", "chat/$navigateToChat")
                    }
                }
            }

            val currentScreen = screenStack.last()

            fun navigateTo(screen: String) {
                screenStack = screenStack + screen
            }

            fun navigateBack() {
                if (screenStack.size > 1) {
                    screenStack = screenStack.dropLast(1)
                }
            }

            BackHandler(enabled = screenStack.size > 1) {
                navigateBack()
            }

            var isGoogleConnected by remember { mutableStateOf(isGoogleConnectedInitial) }
            var googleUserName by remember { mutableStateOf(googleUserNameInitial) }
            var googleUserPhoto by remember { mutableStateOf(googleUserPhotoInitial) }
            var googleUserEmail by remember { mutableStateOf(googleUserEmailInitial) }
            var googleUserBirthday by remember { mutableStateOf(googleUserBirthdayInitial) }
            var currentSeedColor by remember { mutableStateOf(seedColor) }

            var mainMenuTab by remember { mutableIntStateOf(0) }

            val context = LocalContext.current
            var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
            val hasContactsPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            LaunchedEffect(hasContactsPermission) {
                if (hasContactsPermission) {
                    contacts = getContacts(context)
                }
            }

            CurrentTheme(seedColor = currentSeedColor) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.animation.Crossfade(
                        targetState = currentScreen,
                        label = "navigation_crossfade"
                    ) { screen ->
                        when {
                            screen == "welcome" -> CurrentWelcomeScreen(onContinue = { navigateTo("rationale") })

                            screen == "rationale" -> RationaleScreen(
                                onPermitClick = { navigateTo("permissions") },
                                onContinueClick = { navigateTo("are_you_sure_permissions") }
                            )

                            screen == "permissions" -> PermissionsScreen(
                                onAllPermissionsGranted = {
                                    navigateTo("default_apps")
                                },
                                onPermissionsDenied = { navigateTo("are_you_sure_permissions") }
                            )

                            screen == "default_apps" -> DefaultAppsScreen(
                                onContinue = { navigateTo("backup") }
                            )

                            screen == "are_you_sure_permissions" -> AreYouSurePermissionsScreen(
                                onSureClick = { navigateTo("backup") },
                                onBackClick = { navigateBack() }
                            )

                            screen == "backup" -> BackupScreen(
                                onImportClick = { navigateTo("google_connect_wizard") },
                                onSkipClick = { navigateTo("are_you_sure_backup") }
                            )

                            screen == "google_connect_wizard" -> GoogleConnectScreen(
                                webClientId = WEB_CLIENT_ID,
                                onConnected = { name, photo, email ->
                                    googleUserName = name
                                    googleUserPhoto = photo ?: ""
                                    googleUserEmail = email ?: ""
                                    isGoogleConnected = true
                                    sharedPrefs.edit().apply {
                                        putBoolean("google_connected", true)
                                        putString("google_user_name", name)
                                        putString("google_user_photo", photo ?: "")
                                        putString("google_user_email", email ?: "")
                                        putBoolean("setup_complete", true)
                                    }.apply()
                                    screenStack = listOf("main_menu")
                                },
                                onBack = { navigateBack() }
                            )

                            screen == "are_you_sure_backup" -> AreYouSureBackupScreen(
                                onSureClick = {
                                    sharedPrefs.edit().putBoolean("setup_complete", true).apply()
                                    screenStack = listOf("main_menu")
                                },
                                onBackClick = { navigateBack() }
                            )

                            screen == "main_menu" -> MainMenuScreen(
                                userPhoto = googleUserPhoto,
                                initialContacts = contacts,
                                initialTab = mainMenuTab,
                                onTabChange = { mainMenuTab = it },
                                onSettingsClick = { navigateTo("settings") },
                                onConversationClick = { address -> navigateTo("chat/$address") },
                                onNavigate = { navigateTo(it) }
                            )

                            screen == "settings" -> SettingsScreen(
                                isGoogleConnected = isGoogleConnected,
                                userName = googleUserName,
                                userPhoto = googleUserPhoto,
                                userEmail = googleUserEmail,
                                currentSeedColor = currentSeedColor,
                                onSeedColorChange = { color ->
                                    currentSeedColor = color
                                    sharedPrefs.edit().apply {
                                        if (color != null) putLong(
                                            "seed_color",
                                            color.value.toLong()
                                        )
                                        else remove("seed_color")
                                    }.apply()
                                },
                                onAccountClick = { navigateTo("account") },
                                onConnectClick = { navigateTo("google_connect_settings") },
                                onDeveloperOptionsClick = { navigateTo("developer_options") },
                                onAboutClick = { navigateTo("about") },
                                onLogoutClick = {
                                    isGoogleConnected = false
                                    googleUserName = ""
                                    googleUserPhoto = ""
                                    googleUserEmail = ""
                                    sharedPrefs.edit().apply {
                                        putBoolean("google_connected", false)
                                        putString("google_user_name", "")
                                        putString("google_user_photo", "")
                                        putString("google_user_email", "")
                                    }.apply()
                                },
                                onBack = { navigateBack() }
                            )

                            screen == "google_connect_settings" -> GoogleConnectScreen(
                                webClientId = WEB_CLIENT_ID,
                                onConnected = { name, photo, email ->
                                    googleUserName = name
                                    googleUserPhoto = photo ?: ""
                                    googleUserEmail = email ?: ""
                                    isGoogleConnected = true
                                    sharedPrefs.edit().apply {
                                        putBoolean("google_connected", true)
                                        putString("google_user_name", name)
                                        putString("google_user_photo", photo ?: "")
                                        putString("google_user_email", email ?: "")
                                    }.apply()
                                    navigateBack()
                                },
                                onBack = { navigateBack() }
                            )

                            screen == "account" -> AccountScreen(
                                userName = googleUserName,
                                userPhoto = googleUserPhoto,
                                userEmail = googleUserEmail,
                                birthday = googleUserBirthday,
                                onBirthdayChange = { newDate ->
                                    googleUserBirthday = newDate
                                    sharedPrefs.edit().putString("google_user_birthday", newDate)
                                        .apply()
                                },
                                onBack = { navigateBack() }
                            )

                            screen == "developer_options" -> DeveloperOptionsScreen(
                                onRunSetupWizard = {
                                    sharedPrefs.edit().putBoolean("setup_complete", false).apply()
                                    screenStack = listOf("welcome")
                                },
                                onBack = { navigateBack() }
                            )

                            screen == "about" -> AboutScreen(onBack = { navigateBack() })

                            screen == "dialer" -> DialerScreen(
                                onBack = { navigateBack() },
                                onPlaceCall = { number ->
                                    val hasDirectCallPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CALL_PHONE
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasDirectCallPermission) {
                                        placePhoneCall(context, number)
                                    } else {
                                        // We reuse the existing startCallFlow logic in MainMenuScreen
                                        // or just use placePhoneCall which handles the dialer fallback.
                                        placePhoneCall(context, number)
                                    }
                                }
                            )

                            screen.startsWith("chat/") -> {
                                val address = screen.removePrefix("chat/")
                                ChatScreen(
                                    address = address,
                                    contacts = contacts,
                                    onBack = { navigateBack() }
                                )
                            }
                        }
                    }

                    val activeTelecomCall = CallManager.currentCall
                    val callState = CallManager.callState
                    if (activeTelecomCall != null && callState != android.telecom.Call.STATE_DISCONNECTED) {
                        val number = CallManager.lastNumber
                        val contact = remember(number, contacts) {
                            if (number.isBlank()) return@remember null
                            val normalized =
                                number.replace(" ", "").replace("-", "").replace("(", "")
                                    .replace(")", "").replace("+", "")
                            contacts.find {
                                val contactNormalized =
                                    it.number.replace(" ", "").replace("-", "").replace("(", "")
                                        .replace(")", "").replace("+", "")
                                contactNormalized == normalized || (contactNormalized.length >= 7 && normalized.endsWith(
                                    contactNormalized
                                )) || (normalized.length >= 7 && contactNormalized.endsWith(
                                    normalized
                                ))
                            }
                        }

                        if (activeTelecomCall != null) {
                            CallManager.lastName = contact?.name ?: number
                            CallManager.lastPhotoUri = contact?.photoUri
                        }

                        val displayCall = ActiveCall(
                            name = CallManager.lastName.ifBlank { contact?.name ?: number },
                            number = number,
                            photoUri = CallManager.lastPhotoUri ?: contact?.photoUri
                        )

                        if (callState == android.telecom.Call.STATE_RINGING) {
                            IncomingCallScreen(
                                call = displayCall,
                                onAccept = {
                                    CallService.acceptCall()
                                },
                                onDecline = { CallService.rejectCall() }
                            )
                        } else {
                            ActiveCallScreen(
                                call = displayCall,
                                onEndCall = { CallService.endCall() },
                                onOpenSystemDialer = {
                                    placePhoneCall(this@MainActivity, number, forceDialer = true)
                                },
                                onDismiss = {
                                    CallManager.clear()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Contact(
    val id: String,
    val name: String,
    val number: String,
    val photoUri: String?
)

data class ActiveCall(
    val name: String,
    val number: String,
    val photoUri: String?
)

data class CallRecord(
    val id: Long,
    val name: String?,
    val number: String,
    val type: Int,
    val date: Long,
    val photoUri: String?
)

data class SmsMessage(
    val id: Long,
    val threadId: Long = 0,
    val address: String,
    val body: String,
    val date: Long,
    val type: Int = Telephony.Sms.MESSAGE_TYPE_INBOX,
    val photoUri: String?
)

@Composable
fun rememberContactLookup(contacts: List<Contact>): (String) -> Contact? {
    return remember(contacts) {
        val map = mutableMapOf<String, Contact>()
        contacts.forEach { contact ->
            val normalized = contact.number.filter { it.isDigit() }.takeLast(10)
            if (normalized.isNotEmpty()) {
                map[normalized] = contact
            }
        }
        val lookup: (String) -> Contact? = { address ->
            val normalized = address.filter { it.isDigit() }.takeLast(10)
            if (normalized.isEmpty()) contacts.find { it.number == address }
            else map[normalized] ?: contacts.find { it.number == address }
        }
        lookup
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainMenuScreen(
    userPhoto: String,
    initialContacts: List<Contact>,
    initialTab: Int = 0,
    onTabChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onConversationClick: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var pendingCall by remember { mutableStateOf<ActiveCall?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    var callRefreshKey by remember { mutableIntStateOf(0) }
    var contactRefreshKey by remember { mutableIntStateOf(0) }
    var smsRefreshKey by remember { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var hasCallPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var contacts by remember { mutableStateOf(initialContacts) }
    LaunchedEffect(contactRefreshKey, hasContactsPermission) {
        if (hasContactsPermission) {
            contacts = getContacts(context)
        }
    }
    LaunchedEffect(initialContacts) {
        contacts = initialContacts
    }

    val contactLookup = rememberContactLookup(contacts)

    val refreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    val callPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val queuedCall = pendingCall
        pendingCall = null
        if (granted && queuedCall != null) {
            placePhoneCall(context, queuedCall.number)
        } else if (!granted) {
            Toast.makeText(context, "Call permission denied.", Toast.LENGTH_SHORT).show()
        }
    }
    val startCallFlow: (ActiveCall) -> Unit = { call ->
        val hasDirectCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasDirectCallPermission) {
            placePhoneCall(context, call.number)
        } else {
            pendingCall = call
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCallPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED
                hasContactsPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
                hasSmsPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Current", style = MaterialTheme.typography.titleLarge) },
                        actions = {
                            IconButton(onClick = onSettingsClick) {
                                if (userPhoto.isNotEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(userPhoto).crossfade(true).build(),
                                        contentDescription = "User Photo",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                        )
                    )

                    if (initialTab == 1 || initialTab == 2) {
                        DockedSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { isSearchActive = false },
                            active = isSearchActive,
                            onActiveChange = { isSearchActive = it },
                            placeholder = { Text(if (initialTab == 1) "Search contacts" else "Search messages") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = null)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = AppCardShape
                        ) {
                        }
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = initialTab,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                transitionSpec = {
                    (slideInVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        initialOffsetY = { it / 8 }
                    ) + fadeIn()).togetherWith(
                        slideOutVertically(
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            targetOffsetY = { -it / 8 }
                        ) + fadeOut()
                    )
                },
                label = "main_content_switcher"
            ) { tab ->
                val canRefresh = when (tab) {
                    0 -> hasCallPermission
                    1 -> hasContactsPermission
                    2 -> hasSmsPermission
                    else -> false
                }
                PullToRefreshBox(
                    isRefreshing = isRefreshing && canRefresh,
                    onRefresh = {
                        if (!canRefresh || isRefreshing) return@PullToRefreshBox
                        scope.launch {
                            isRefreshing = true
                            when (tab) {
                                0 -> callRefreshKey++
                                1 -> contactRefreshKey++
                                2 -> smsRefreshKey++
                            }
                            delay(700)
                            isRefreshing = false
                        }
                    },
                    state = refreshState,
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            isRefreshing = isRefreshing && canRefresh,
                            state = refreshState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(24.dp)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        when (tab) {
                            0 -> if (hasCallPermission) {
                                CallLogList(
                                    refreshKey = callRefreshKey,
                                    contactLookup = contactLookup,
                                    onCallClick = startCallFlow
                                )
                            } else {
                                PermissionRequiredEmptyState(
                                    "Call Log",
                                    Manifest.permission.READ_CALL_LOG
                                )
                            }

                            1 -> if (hasContactsPermission) {
                                ContactList(
                                    contacts = contacts,
                                    searchQuery = searchQuery,
                                    onCallContact = startCallFlow,
                                    onSmsContact = { contact ->
                                        onConversationClick(contact.number)
                                    }
                                )
                            } else {
                                PermissionRequiredEmptyState(
                                    "Contacts",
                                    Manifest.permission.READ_CONTACTS
                                )
                            }

                            2 -> if (hasSmsPermission) {
                                SmsList(
                                    refreshKey = smsRefreshKey,
                                    searchQuery = searchQuery,
                                    contactLookup = contactLookup,
                                    onConversationClick = onConversationClick
                                )
                            } else {
                                PermissionRequiredEmptyState("SMS", Manifest.permission.READ_SMS)
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val items = listOf(
                Icons.AutoMirrored.Filled.List to 0,
                Icons.Filled.Person to 1,
                Icons.AutoMirrored.Filled.Message to 2
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { (icon, tabIndex) ->
                    val isSelected = initialTab == tabIndex
                    val morphScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.25f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "nav_item_scale"
                    )
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "nav_item_tint"
                    )
                    val selectedBubbleWidth by animateDpAsState(
                        targetValue = if (isSelected) 72.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "nav_item_bubble_width"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabChange(tabIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .width(selectedBubbleWidth)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {}

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .graphicsLayer(
                                    scaleX = morphScale, scaleY = morphScale
                                ),
                            tint = iconTint
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = initialTab == 0 || initialTab == 2,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 140.dp, end = 24.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    if (initialTab == 0) onNavigate("dialer")
                    else {
                        onTabChange(1)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    if (initialTab == 0) Icons.Filled.Dialpad else Icons.Filled.Edit,
                    contentDescription = if (initialTab == 0) "Dialer" else "Compose"
                )
            }
        }
    }
}

@Composable
fun PermissionRequiredEmptyState(permissionName: String, permission: String) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(shape = AppCardShape) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "$permissionName permission is required to show this content.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExpressiveButton(
                    onClick = { launcher.launch(permission) },
                    size = ExpressiveButtonSize.M
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CallLogList(
    refreshKey: Int = 0,
    contactLookup: (String) -> Contact? = { null },
    onCallClick: (ActiveCall) -> Unit
) {
    val context = LocalContext.current
    var callLog by remember { mutableStateOf<List<CallRecord>>(emptyList()) }

    LaunchedEffect(refreshKey) {
        callLog = getCallLog(context)
    }

    if (callLog.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No call history found.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(callLog, key = { it.id }) { record ->
                val contact = contactLookup(record.number)
                val displayName = record.name ?: contact?.name ?: record.number
                val photoUri = contact?.photoUri

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    onClick = {
                        onCallClick(ActiveCall(displayName, record.number, photoUri))
                    }
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                displayName,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        supportingContent = {
                            val typeStr = when (record.type) {
                                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                                CallLog.Calls.MISSED_TYPE -> "Missed"
                                else -> "Other"
                            }
                            val sdf = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
                            Text(
                                "$typeStr • ${sdf.format(Date(record.date))}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingContent = {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val initial =
                                            displayName.firstOrNull { it.isLetter() }?.toString()
                                                ?.uppercase()
                                        if (initial != null) {
                                            Text(
                                                initial,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        } else {
                                            Icon(Icons.Filled.Person, contentDescription = null)
                                        }
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            Icon(
                                Icons.Filled.Call,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactList(
    contacts: List<Contact>,
    searchQuery: String = "",
    onCallContact: (ActiveCall) -> Unit,
    onSmsContact: (Contact) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var hiddenContactIds by remember {
        mutableStateOf(
            sharedPrefs.getStringSet("hidden_contacts", emptySet()) ?: emptySet()
        )
    }

    val visibleContacts = contacts.filter {
        it.id !in hiddenContactIds &&
                (searchQuery.isBlank() || it.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) || it.number.contains(searchQuery))
    }

    if (visibleContacts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No contacts found.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(visibleContacts, key = { it.id }) { contact ->
                var isExpanded by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Column {
                        ListItem(
                            headlineContent = {
                                Text(
                                    contact.name,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            supportingContent = { Text(contact.number) },
                            leadingContent = {
                                if (contact.photoUri != null) {
                                    AsyncImage(
                                        model = contact.photoUri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(44.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            val initial = contact.name.firstOrNull { it.isLetter() }
                                                ?.toString()?.uppercase()
                                            if (initial != null) {
                                                Text(
                                                    initial,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Filled.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        val newHidden = hiddenContactIds.toMutableSet()
                                            .apply { add(contact.id) }
                                        hiddenContactIds = newHidden
                                        sharedPrefs.edit()
                                            .putStringSet("hidden_contacts", newHidden).apply()
                                    }) {
                                        Icon(
                                            Icons.Filled.VisibilityOff,
                                            contentDescription = "Hide Contact",
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Icon(
                                        if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        AnimatedVisibility(visible = isExpanded) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ExpressiveButton(
                                    onClick = {
                                        onCallContact(
                                            ActiveCall(
                                                contact.name,
                                                contact.number,
                                                contact.photoUri
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    size = ExpressiveButtonSize.S
                                ) {
                                    Icon(
                                        Icons.Filled.Call,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Call")
                                }
                                ExpressiveButton(
                                    onClick = { onSmsContact(contact) },
                                    modifier = Modifier.weight(1f),
                                    size = ExpressiveButtonSize.S
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Message,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Message")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmsList(
    refreshKey: Int = 0,
    searchQuery: String = "",
    contactLookup: (String) -> Contact? = { null },
    onConversationClick: (String) -> Unit
) {
    val context = LocalContext.current
    var smsList by remember { mutableStateOf<List<SmsMessage>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val contentObserver = remember {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                scope.launch {
                    smsList = getSms(context)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        context.contentResolver.registerContentObserver(
            Telephony.Sms.CONTENT_URI,
            true,
            contentObserver
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    LaunchedEffect(refreshKey) {
        smsList = getSms(context)
    }

    if (smsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No messages found.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )
        }
    } else {
        val conversations = remember(smsList, searchQuery) {
            val results = mutableListOf<SmsMessage>()
            val seenThreads = mutableSetOf<Long>()
            val seenAddresses = mutableSetOf<String>()

            smsList.forEach { sms ->
                val normalized = sms.address.filter { it.isDigit() }.takeLast(10)
                val addressKey = if (normalized.length >= 7) normalized else sms.address

                val hasThread = sms.threadId > 0 && seenThreads.contains(sms.threadId)
                val hasAddress = seenAddresses.contains(addressKey)

                if (!hasThread && !hasAddress) {
                    val contact = contactLookup(sms.address)
                    val displayName = contact?.name ?: sms.address
                    if (searchQuery.isBlank() ||
                        displayName.contains(searchQuery, ignoreCase = true) ||
                        sms.body.contains(searchQuery, ignoreCase = true) ||
                        sms.address.contains(searchQuery)
                    ) {
                        results.add(sms)
                    }
                    if (sms.threadId > 0) seenThreads.add(sms.threadId)
                    seenAddresses.add(addressKey)
                }
            }
            results.sortedByDescending { it.date }
        }

        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(conversations, key = { it.id }) { sms ->
                val contact = contactLookup(sms.address)
                val displayName = contact?.name ?: sms.address
                val photoUri = contact?.photoUri

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    onClick = { onConversationClick(sms.address) }
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        supportingContent = {
                            Text(
                                sms.body,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = {
                            val date = Date(sms.date)
                            val format = if (DateUtils.isToday(sms.date)) "HH:mm" else "MMM d"
                            val sdf = remember(format) { SimpleDateFormat(format, Locale.getDefault()) }
                            Text(
                                sdf.format(date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        },
                        leadingContent = {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val initial =
                                            displayName.firstOrNull { it.isLetter() }?.toString()
                                                ?.uppercase()
                                        if (initial != null) {
                                            Text(
                                                initial,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        } else {
                                            Icon(
                                                Icons.AutoMirrored.Filled.Message,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    address: String,
    contacts: List<Contact>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var messages by remember { mutableStateOf<List<SmsMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val contentObserver = remember {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                scope.launch {
                    messages = getSmsHistory(context, address)
                }
            }
        }
    }

    DisposableEffect(address) {
        context.contentResolver.registerContentObserver(
            Telephony.Sms.CONTENT_URI,
            true,
            contentObserver
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    LaunchedEffect(address) {
        messages = getSmsHistory(context, address)
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(0)
        }
    }

    val contact = remember(address, contacts) {
        val normalized = address.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
            .replace("+", "")
        contacts.find {
            val contactNormalized =
                it.number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
                    .replace("+", "")
            contactNormalized == normalized || (contactNormalized.length >= 7 && normalized.endsWith(
                contactNormalized
            )) || (normalized.length >= 7 && contactNormalized.endsWith(normalized))
        }
    }
    val displayName = contact?.name ?: address

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (contact?.photoUri != null) {
                            AsyncImage(
                                model = contact.photoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(displayName, style = MaterialTheme.typography.titleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { placePhoneCall(context, address) }) {
                        Icon(Icons.Filled.Call, contentDescription = "Call")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .navigationBarsPadding()
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                sendSms(context, address, inputText)
                                inputText = ""
                                scope.launch {
                                    delay(500)
                                    messages = getSmsHistory(context, address)
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            reverseLayout = true,
            state = scrollState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe =
                    msg.type == Telephony.Sms.MESSAGE_TYPE_SENT || msg.type == Telephony.Sms.MESSAGE_TYPE_OUTBOX

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val morphCorner = animateDpAsState(
                    targetValue = if (isPressed) 12.dp else 24.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "bubble_morph"
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        onClick = {},
                        interactionSource = interactionSource,
                        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = if (isMe) 24.dp else 4.dp,
                            topEnd = if (isMe) 4.dp else 24.dp,
                            bottomStart = morphCorner.value,
                            bottomEnd = morphCorner.value
                        ),
                        tonalElevation = if (isMe) 0.dp else 1.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = msg.body,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

private fun sendSms(context: Context, address: String, message: String) {
    try {
        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, address)
            put(Telephony.Sms.BODY, message)
            put(Telephony.Sms.DATE, System.currentTimeMillis())
            put(Telephony.Sms.READ, 1)
            put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT)
        }
        context.contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values)

        val smsManager = context.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(address, null, message, null, null)
        Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun getSmsHistory(context: Context, address: String): List<SmsMessage> {
    val history = mutableListOf<SmsMessage>()
    val threadId = try {
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms.THREAD_ID),
            "${Telephony.Sms.ADDRESS} = ?",
            arrayOf(address),
            "${Telephony.Sms.DATE} DESC LIMIT 1"
        )
        cursor?.use {
            if (it.moveToFirst()) it.getLong(0) else null
        }
    } catch (e: Exception) {
        null
    }

    val selection =
        if (threadId != null) "${Telephony.Sms.THREAD_ID} = ?" else "${Telephony.Sms.ADDRESS} = ?"
    val selectionArgs = if (threadId != null) arrayOf(threadId.toString()) else arrayOf(address)

    val cursor = context.contentResolver.query(
        Telephony.Sms.CONTENT_URI,
        null,
        selection,
        selectionArgs,
        "${Telephony.Sms.DATE} DESC"
    )
    cursor?.use {
        val idIdx = it.getColumnIndex(Telephony.Sms._ID)
        val bodyIdx = it.getColumnIndex(Telephony.Sms.BODY)
        val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)
        val typeIdx = it.getColumnIndex(Telephony.Sms.TYPE)
        val threadIdIdx = it.getColumnIndex(Telephony.Sms.THREAD_ID)
        val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)

        while (it.moveToNext()) {
            history.add(
                SmsMessage(
                    id = it.getLong(idIdx),
                    threadId = if (threadIdIdx != -1) it.getLong(threadIdIdx) else 0L,
                    address = if (addressIdx != -1) (it.getString(addressIdx)
                        ?: address) else address,
                    body = it.getString(bodyIdx) ?: "",
                    date = it.getLong(dateIdx),
                    type = it.getInt(typeIdx),
                    photoUri = null
                )
            )
        }
    }
    return history
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(onBack: () -> Unit, onPlaceCall: (String) -> Unit) {
    var number by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dialer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    number,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
                keys.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            Surface(
                                onClick = { number += key },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(key, fontSize = 28.sp)
                                        if (key == "0") {
                                            Text(
                                                "+",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (number.isNotEmpty()) number = number.dropLast(1) },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Delete",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { onPlaceCall(number) },
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            Icons.Filled.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Spacer to balance the layout since there's no third button
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}

@Composable
fun DialerDialog(onDismiss: () -> Unit, onPlaceCall: (String) -> Unit) {
    var number by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dialer") },
        text = {
            Column {
                Text(
                    number,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                val keys =
                    listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
                Column {
                    keys.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        TextButton(onClick = { number += key }) {
                                            Text(key, fontSize = 24.sp)
                                        }
                                        if (key == "0") {
                                            Text(
                                                "+",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.5f
                                                ),
                                                modifier = Modifier.offset(y = (-12).dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            ExpressiveButton(
                onClick = { onPlaceCall(number) },
                enabled = number.isNotBlank(),
                size = ExpressiveButtonSize.M
            ) {
                Text("Call")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (number.isNotEmpty()) number = number.dropLast(1)
            }) {
                Text("Delete")
            }
        }
    )
}

@Composable
fun IncomingCallScreen(
    call: ActiveCall,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Incoming Call",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center) {
                if (pulseScale > 1.02f) {
                    Surface(
                        modifier = Modifier
                            .size(220.dp * pulseScale)
                            .graphicsLayer { alpha = (1.1f - pulseScale) * 5f },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {}
                }

                if (!call.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = call.photoUri,
                        contentDescription = call.name,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(180.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initial =
                                call.name.firstOrNull { it.isLetter() }?.toString()?.uppercase()
                            if (initial != null) {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            Text(
                text = call.name,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (call.name != call.number) {
                Text(
                    text = call.number,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FloatingActionButton(
                        onClick = onDecline,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            Icons.Filled.CallEnd,
                            contentDescription = "Decline",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Decline", style = MaterialTheme.typography.labelLarge)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FloatingActionButton(
                        onClick = onAccept,
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                    ) {
                        Icon(
                            Icons.Filled.Call,
                            contentDescription = "Accept",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Accept", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun ActiveCallScreen(
    call: ActiveCall,
    onEndCall: () -> Unit,
    onOpenSystemDialer: () -> Unit,
    onDismiss: () -> Unit
) {
    var isOnHold by remember { mutableStateOf(false) }
    var showKeypad by remember { mutableStateOf(false) }
    var showAddCallDialer by remember { mutableStateOf(false) }
    var dtmfDigits by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isConnected = CallManager.callState == android.telecom.Call.STATE_ACTIVE
    val isDisconnected = CallManager.callState == android.telecom.Call.STATE_DISCONNECTED

    val callDurationFormatted = remember(CallManager.durationSeconds) {
        val mins = CallManager.durationSeconds / 60
        val secs = CallManager.durationSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isDisconnected -> "Call Ended"
                        isConnected -> "Ongoing Call"
                        else -> "Calling..."
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDisconnected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }

            if (isConnected) {
                Text(
                    text = callDurationFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(40.dp))

            if (!call.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = call.photoUri,
                    contentDescription = call.name,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    color = if (isDisconnected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initial =
                            call.name.firstOrNull { it.isLetter() }?.toString()?.uppercase()
                        if (initial != null) {
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.displayMedium,
                                color = if (isDisconnected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = if (isDisconnected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = call.name,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (call.name != call.number) {
                Text(
                    text = call.number,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.weight(1f))

            if (!isDisconnected) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallActionButton(
                            icon = if (CallManager.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                            label = if (CallManager.isMuted) "Unmute" else "Mute",
                            active = CallManager.isMuted
                        ) {
                            CallService.setMuted(!CallManager.isMuted)
                        }
                        InCallActionButton(
                            icon = Icons.Filled.Dialpad,
                            label = "Keypad",
                            active = showKeypad
                        ) { showKeypad = !showKeypad }
                        InCallActionButton(
                            icon = if (isOnHold) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            label = if (isOnHold) "Resume" else "Hold",
                            active = isOnHold
                        ) {
                            isOnHold = !isOnHold
                            if (isOnHold) {
                                CallManager.currentCall?.hold()
                            } else {
                                CallManager.currentCall?.unhold()
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallActionButton(
                            icon = Icons.Filled.PersonAdd,
                            label = "Add call",
                            active = false,
                            onClick = { showAddCallDialer = true }
                        )
                        InCallActionButton(
                            icon = Icons.AutoMirrored.Filled.Launch,
                            label = "System",
                            active = false,
                            onClick = onOpenSystemDialer
                        )
                        InCallActionButton(
                            icon = when (CallManager.audioRoute) {
                                CallAudioState.ROUTE_SPEAKER -> Icons.AutoMirrored.Filled.VolumeUp
                                CallAudioState.ROUTE_BLUETOOTH -> Icons.Filled.Bluetooth
                                else -> Icons.Filled.Headset
                            },
                            label = "Audio",
                            active = CallManager.audioRoute == CallAudioState.ROUTE_SPEAKER,
                            onClick = {
                                val nextRoute =
                                    if (CallManager.audioRoute == CallAudioState.ROUTE_SPEAKER) {
                                        CallAudioState.ROUTE_EARPIECE
                                    } else {
                                        CallAudioState.ROUTE_SPEAKER
                                    }
                                CallService.setAudioRoute(nextRoute)
                            }
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalIconButton(
                            onClick = {
                                placePhoneCall(context, call.number)
                                onDismiss()
                            },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                Icons.Filled.Call,
                                contentDescription = "Call Again",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Call Again", style = MaterialTheme.typography.labelLarge)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalIconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Cancel",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Cancel", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            if (showKeypad) {
                AnimatedVisibility(
                    visible = showKeypad,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(20.dp))
                        Text(
                            dtmfDigits.ifBlank { " " },
                            style = MaterialTheme.typography.headlineSmall
                        )
                        val keys =
                            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
                        keys.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                row.forEach { key ->
                                    FilledTonalIconButton(
                                        onClick = {
                                            dtmfDigits += key
                                            CallManager.currentCall?.playDtmfTone(key[0])
                                            CallManager.currentCall?.stopDtmfTone()
                                        },
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Text(key, style = MaterialTheme.typography.headlineSmall)
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            if (!isDisconnected) {
                Spacer(Modifier.weight(0.5f))

                FloatingActionButton(
                    onClick = onEndCall,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    shape = CircleShape,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp)
                        .size(80.dp)
                ) {
                    Icon(
                        Icons.Filled.CallEnd,
                        contentDescription = "End call",
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }

        if (showAddCallDialer) {
            DialerDialog(
                onDismiss = { showAddCallDialer = false },
                onPlaceCall = { number ->
                    placePhoneCall(context, number)
                    showAddCallDialer = false
                }
            )
        }
    }
}

@Composable
fun InCallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedCorner = animateDpAsState(
        targetValue = if (isPressed) 18.dp else 32.dp,
        animationSpec = spring<androidx.compose.ui.unit.Dp>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "corner"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(animatedCorner.value),
            color = if (active) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (active) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface,
            interactionSource = interactionSource,
            modifier = Modifier.size(68.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun placePhoneCall(
    context: Context,
    rawNumber: String,
    forceDialer: Boolean = false
): Boolean {
    val number = rawNumber.trim().replace(" ", "").replace("-", "")
    if (number.isBlank()) return false

    val hasCallPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CALL_PHONE
    ) == PackageManager.PERMISSION_GRANTED

    val intent = if (!forceDialer && hasCallPermission) {
        Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
    } else {
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return runCatching {
        context.startActivity(intent)
        true
    }.getOrElse {
        false
    }
}

fun getCallLog(context: Context): List<CallRecord> {
    val callList = mutableListOf<CallRecord>()
    val cursor: Cursor? = context.contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        null, null, null, CallLog.Calls.DATE + " DESC"
    )
    cursor?.use {
        val numberIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
        val nameIdx = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
        val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
        val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)
        val idIdx = it.getColumnIndex(CallLog.Calls._ID)
        val photoIdx = it.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)

        while (it.moveToNext()) {
            if (numberIdx != -1 && typeIdx != -1 && dateIdx != -1 && idIdx != -1) {
                callList.add(
                    CallRecord(
                        id = it.getLong(idIdx),
                        name = if (nameIdx != -1) it.getString(nameIdx) else null,
                        number = it.getString(numberIdx) ?: "Unknown",
                        type = it.getInt(typeIdx),
                        date = it.getLong(dateIdx),
                        photoUri = if (photoIdx != -1) it.getString(photoIdx) else null
                    )
                )
            }
        }
    }
    return callList
}

fun getContacts(context: Context): List<Contact> {
    val contactsMap = mutableMapOf<String, Contact>()
    val cursor = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )
    cursor?.use {
        val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val nameIdx =
            it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val photoIdx =
            it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
        val fullPhotoIdx =
            it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

        while (it.moveToNext()) {
            if (idIdx != -1 && nameIdx != -1 && numberIdx != -1) {
                val id = it.getString(idIdx)
                val name = it.getString(nameIdx) ?: "Unknown"
                val rawNumber = it.getString(numberIdx) ?: ""
                val normalizedNumber = rawNumber.replace(" ", "").replace("-", "")
                val photoUri = when {
                    fullPhotoIdx != -1 -> it.getString(fullPhotoIdx)
                    photoIdx != -1 -> it.getString(photoIdx)
                    else -> null
                }

                if (normalizedNumber.isNotEmpty() && !contactsMap.containsKey(
                        normalizedNumber
                    )
                ) {
                    contactsMap[normalizedNumber] =
                        Contact(id, name, normalizedNumber, photoUri)
                }
            }
        }
    }
    return contactsMap.values.toList().sortedBy { it.name }
}

fun getSms(context: Context): List<SmsMessage> {
    val smsList = mutableListOf<SmsMessage>()
    val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE,
        Telephony.Sms.TYPE,
        Telephony.Sms.THREAD_ID
    )
    val cursor = context.contentResolver.query(
        Telephony.Sms.CONTENT_URI,
        projection, null, null, Telephony.Sms.DATE + " DESC LIMIT 5000"
    )
    cursor?.use {
        val idIdx = it.getColumnIndex(Telephony.Sms._ID)
        val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)
        val bodyIdx = it.getColumnIndex(Telephony.Sms.BODY)
        val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)
        val typeIdx = it.getColumnIndex(Telephony.Sms.TYPE)
        val threadIdIdx = it.getColumnIndex(Telephony.Sms.THREAD_ID)

        while (it.moveToNext()) {
            if (idIdx != -1 && addressIdx != -1 && bodyIdx != -1 && dateIdx != -1) {
                smsList.add(
                    SmsMessage(
                        id = it.getLong(idIdx),
                        threadId = if (threadIdIdx != -1) it.getLong(threadIdIdx) else 0L,
                        address = it.getString(addressIdx) ?: "Unknown",
                        body = it.getString(bodyIdx) ?: "",
                        date = it.getLong(dateIdx),
                        type = if (typeIdx != -1) it.getInt(typeIdx) else Telephony.Sms.MESSAGE_TYPE_INBOX,
                        photoUri = null
                    )
                )
            }
        }
    }
    return smsList
}

@Composable
fun DefaultAppsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager

    var isDialerHeld by remember { mutableStateOf(roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) }
    var isSmsHeld by remember { mutableStateOf(roleManager.isRoleHeld(RoleManager.ROLE_SMS)) }

    val dialerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            isDialerHeld = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        }

    val smsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            isSmsHeld = roleManager.isRoleHeld(RoleManager.ROLE_SMS)
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.AppShortcut,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Default Apps",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "To handle your calls and messages directly, Current needs to be set as your default Phone and SMS app.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            ExpressiveButton(
                onClick = {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                    dialerLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M,
                enabled = !isDialerHeld
            ) {
                Text(if (isDialerHeld) "Phone App: Set as Default" else "Set as Default Phone App")
                if (isDialerHeld) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Check, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExpressiveButton(
                onClick = {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                    smsLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M,
                enabled = !isSmsHeld
            ) {
                Text(if (isSmsHeld) "SMS App: Set as Default" else "Set as Default SMS App")
                if (isSmsHeld) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Check, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isDialerHeld && isSmsHeld) "Continue" else "Skip for now")
            }
        }
    }
}

@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissions = mutableListOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_SMS,
        Manifest.permission.CALL_PHONE
    ).apply {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            onAllPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Security,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Permissions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Current needs access to your contacts, call logs, messages, and phone calling to function as your central hub.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            ExpressiveButton(
                onClick = { launcher.launch(permissions) },
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M
            ) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
fun RationaleScreen(onPermitClick: () -> Unit, onContinueClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Why Permissions?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Without these permissions, Current can't show your history or help you communicate. You can still use the app, but many features will be empty.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            ExpressiveButton(
                onClick = onPermitClick,
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M
            ) {
                Text("I'll permit them")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onContinueClick) {
                Text("Continue without them")
            }
        }
    }
}

@Composable
fun AreYouSurePermissionsScreen(onSureClick: () -> Unit, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Are you sure?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Are you sure you want to proceed without permissions? Most features won't work.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            ExpressiveButton(
                onClick = onSureClick,
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M
            ) {
                Text("I am sure")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBackClick) {
                Text("Go back")
            }
        }
    }
}

@Composable
fun BackupScreen(onImportClick: () -> Unit, onSkipClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.CloudDownload,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Sync & Backup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Connect your Google account to sync your settings and data across devices.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            ExpressiveButton(
                onClick = onImportClick,
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M
            ) {
                Text("Connect Google Account")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onSkipClick) {
                Text("Skip for now")
            }
        }
    }
}

@Composable
fun AreYouSureBackupScreen(onSureClick: () -> Unit, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Are you sure?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Skipping backup means your data stays only on this device. If you lose it, you lose your data.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            ExpressiveButton(
                onClick = onSureClick,
                modifier = Modifier.fillMaxWidth(),
                size = ExpressiveButtonSize.M
            ) {
                Text("I am sure")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBackClick) {
                Text("Go back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoogleConnectScreen(
    webClientId: String,
    onConnected: (String, String?, String?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)

    LaunchedEffect(Unit) {
        val googleIdTokenRequestOptions = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val getCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdTokenRequestOptions)
            .build()

        try {
            val result = credentialManager.getCredential(context, getCredentialRequest)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                onConnected(
                    googleIdTokenCredential.displayName ?: "User",
                    googleIdTokenCredential.profilePictureUri?.toString(),
                    googleIdTokenCredential.id
                )
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleConnect", "Error getting credential", e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Connecting to Google...")
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onBack) {
                Text("Cancel")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isGoogleConnected: Boolean,
    userName: String,
    userPhoto: String,
    userEmail: String,
    currentSeedColor: Color?,
    onSeedColorChange: (Color?) -> Unit,
    onAccountClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDeveloperOptionsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var showHiddenContactsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Surface(
                shape = AppSurfaceShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 2.dp
            ) {
                Column {
                    Text(
                        "Account",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    if (isGoogleConnected) {
                        ListItem(
                            headlineContent = { Text(userName) },
                            supportingContent = { Text(userEmail) },
                            leadingContent = {
                                AsyncImage(
                                    model = userPhoto,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            },
                            modifier = Modifier.clickable { onAccountClick() }
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Logout",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            modifier = Modifier.clickable { onLogoutClick() }
                        )
                    } else {
                        ListItem(
                            headlineContent = { Text("Connect Google Account") },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.AccountCircle,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.clickable { onConnectClick() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = AppSurfaceShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column {
                    Text(
                        "Appearance",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    ListItem(
                        headlineContent = { Text("Theme Color") },
                        supportingContent = { Text(if (currentSeedColor == null) "System (Material You)" else "Custom Seed Color") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        currentSeedColor ?: MaterialTheme.colorScheme.primary
                                    )
                            )
                        },
                        trailingContent = {
                            if (currentSeedColor != null) {
                                TextButton(onClick = { onSeedColorChange(null) }) {
                                    Text("Reset")
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            val presets = listOf(
                                null,
                                Color(0xFF6750A4),
                                Color(0xFF006A60),
                                Color(0xFF984061)
                            )
                            val nextIndex =
                                (presets.indexOf(currentSeedColor) + 1) % presets.size
                            onSeedColorChange(presets[nextIndex])
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = AppSurfaceShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column {
                    Text(
                        "General",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    ListItem(
                        headlineContent = { Text("Hidden Contacts") },
                        supportingContent = { Text("Manage contacts you've hidden") },
                        leadingContent = {
                            Icon(
                                Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable { showHiddenContactsDialog = true }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = AppSurfaceShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column {
                    Text(
                        "App Settings",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    ListItem(
                        headlineContent = { Text("Developer Options") },
                        leadingContent = { Icon(Icons.Filled.Code, contentDescription = null) },
                        modifier = Modifier.clickable { onDeveloperOptionsClick() }
                    )
                    ListItem(
                        headlineContent = { Text("About") },
                        leadingContent = { Icon(Icons.Filled.Info, contentDescription = null) },
                        modifier = Modifier.clickable { onAboutClick() }
                    )
                }
            }
        }
    }

    if (showHiddenContactsDialog) {
        val hiddenIds =
            sharedPrefs.getStringSet("hidden_contacts", emptySet()) ?: emptySet()
        AlertDialog(
            onDismissRequest = { showHiddenContactsDialog = false },
            title = { Text("Hidden Contacts") },
            text = {
                if (hiddenIds.isEmpty()) {
                    Text("No hidden contacts.")
                } else {
                    Text("Found ${hiddenIds.size} hidden contacts. Reset them below.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    sharedPrefs.edit().putStringSet("hidden_contacts", emptySet())
                        .apply()
                    showHiddenContactsDialog = false
                }) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHiddenContactsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userName: String,
    userPhoto: String,
    userEmail: String,
    birthday: String,
    onBirthdayChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Surface(
                shape = AppCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = userPhoto,
                        contentDescription = null,
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = birthday,
                        onValueChange = onBirthdayChange,
                        label = { Text("Birthday") },
                        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperOptionsScreen(onRunSetupWizard: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Options") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ElevatedCard(shape = AppCardShape) {
                ListItem(
                    headlineContent = { Text("Run Setup Wizard") },
                    supportingContent = { Text("Reset setup flag and go to welcome screen") },
                    leadingContent = { Icon(Icons.Default.RestartAlt, contentDescription = null) },
                    modifier = Modifier.clickable { onRunSetupWizard() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val packageInfo = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
    }
    val versionName = packageInfo?.versionName ?: "0.0.1"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isDark) R.drawable.current_logo_dark else R.drawable.current_logo
                        ),
                        contentDescription = "Current Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Text(
                text = "Current",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "v$versionName Alpha",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Developers",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(160.dp),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Andrei Popescu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Lead Developer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun CurrentWelcomeScreen(onContinue: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.current_logo_dark),
                        contentDescription = "Logo",
                        modifier = Modifier.padding(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Current",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Experience a modern, unified hub for your digital life.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 40.dp)
                )
                ExpressiveButton(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    size = ExpressiveButtonSize.XL
                ) {
                    Text(
                        "Get Started",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}
