@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
package com.current.hub

import android.Manifest
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupMenuState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.current.hub.ui.theme.CurrentTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val WEB_CLIENT_ID = "340753714127-fm8i8ab477bq0ktb86v35mu7a7tm5muo.apps.googleusercontent.com"
private val AppCardShape = RoundedCornerShape(28.dp)

enum class ExpressiveButtonSize { XS, S, M, L, XL }

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
    val animatedCorner = animateDpAsState(
        targetValue = if (isPressed) pressedCorner else restingCorner,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
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
            val isGoogleConnectedInitial = sharedPrefs.getBoolean("google_connected", false)
            val seedColorLong = sharedPrefs.getLong("seed_color", -1L)
            val seedColor = if (seedColorLong != -1L) Color(seedColorLong.toULong()) else null
            val currentAppIconIdInitial = sharedPrefs.getString("app_icon_id", "default") ?: "default"

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
            var currentSeedColor by remember { mutableStateOf(seedColor) }
            var currentAppIconId by remember { mutableStateOf(currentAppIconIdInitial) }

            var mainMenuTab by remember { mutableIntStateOf(0) }

            val context = LocalContext.current
            var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
            val hasContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

            LaunchedEffect(hasContactsPermission) {
                if (hasContactsPermission) {
                    contacts = getContacts(context)
                }
            }

            val isRinging = CallManager.callState == android.telecom.Call.STATE_RINGING
            val isActiveCall = CallManager.callState != CallManager.STATE_IDLE && !isRinging

            CurrentTheme(seedColor = currentSeedColor) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.animation.Crossfade(targetState = currentScreen, label = "navigation") { screen ->
                        when {
                            screen == "welcome" -> CurrentWelcomeScreen(onContinue = { navigateTo("rationale") })
                            screen == "rationale" -> RationaleScreen(onPermitClick = { navigateTo("permissions") }, onContinueClick = { navigateTo("are_you_sure_permissions") })
                            screen == "permissions" -> PermissionsScreen(onAllPermissionsGranted = { navigateTo("default_apps") }, onPermissionsDenied = { navigateTo("are_you_sure_permissions") })
                            screen == "default_apps" -> DefaultAppsScreen(onContinue = { navigateTo("backup") })
                            screen == "are_you_sure_permissions" -> AreYouSurePermissionsScreen(onSureClick = { navigateTo("backup") }, onBackClick = { navigateBack() })
                            screen == "backup" -> BackupScreen(onImportClick = { navigateTo("google_connect_wizard") }, onSkipClick = { navigateTo("are_you_sure_backup") })
                            screen == "google_connect_wizard" -> GoogleConnectScreen(onConnected = { name, photo, email ->
                                googleUserName = name; googleUserPhoto = photo ?: ""; googleUserEmail = email ?: ""; isGoogleConnected = true
                                sharedPrefs.edit().putBoolean("google_connected", true).putString("google_user_name", name).putString("google_user_photo", photo ?: "").putString("google_user_email", email ?: "").putBoolean("setup_complete", true).apply()
                                screenStack = listOf("main_menu")
                            }, onBack = { navigateBack() })
                            screen == "are_you_sure_backup" -> AreYouSureBackupScreen(onSureClick = { sharedPrefs.edit().putBoolean("setup_complete", true).apply(); screenStack = listOf("main_menu") }, onBackClick = { navigateBack() })
                            screen == "main_menu" -> MainMenuScreen(isGoogleConnected, googleUserName, googleUserPhoto, contacts, mainMenuTab, { mainMenuTab = it }, { navigateTo("settings") }, { navigateTo("dialer") }, { navigateTo("chat/$it") })
                            screen == "settings" -> SettingsScreen(isGoogleConnected, googleUserName, googleUserPhoto, googleUserEmail, currentSeedColor, currentAppIconId, { color -> currentSeedColor = color; sharedPrefs.edit().putLong("seed_color", color?.value?.toLong() ?: -1L).apply() }, { iconId -> currentAppIconId = iconId; sharedPrefs.edit().putString("app_icon_id", iconId).apply() }, { navigateTo("account") }, { navigateTo("google_connect_settings") }, { navigateTo("developer_options") }, { navigateTo("about") }, { isGoogleConnected = false; sharedPrefs.edit().putBoolean("google_connected", false).apply() }, { navigateBack() })
                            screen == "google_connect_settings" -> GoogleConnectScreen(onConnected = { name, photo, email ->
                                googleUserName = name; googleUserPhoto = photo ?: ""; googleUserEmail = email ?: ""; isGoogleConnected = true
                                sharedPrefs.edit().putBoolean("google_connected", true).putString("google_user_name", name).putString("google_user_photo", photo ?: "").putString("google_user_email", email ?: "").apply()
                                navigateBack()
                            }, onBack = { navigateBack() })
                            screen == "account" -> AccountScreen(googleUserName, googleUserPhoto, googleUserEmail) { navigateBack() }
                            screen == "developer_options" -> DeveloperOptionsScreen { navigateBack() }
                            screen == "about" -> AboutScreen { navigateBack() }
                            screen == "dialer" -> DialerScreen({ navigateBack() }, { placePhoneCall(context, it) })
                            screen.startsWith("chat/") -> ChatScreen(screen.substring(5), contacts) { navigateBack() }
                        }
                    }

                    if (isRinging) {
                        IncomingCallScreen(ActiveCall(CallManager.lastName.ifBlank { CallManager.lastNumber }, CallManager.lastNumber, CallManager.lastPhotoUri), onAccept = { CallService.acceptCall() }, onDecline = { CallService.rejectCall() })
                    } else if (isActiveCall) {
                        ActiveCallScreen(ActiveCall(CallManager.lastName.ifBlank { CallManager.lastNumber }, CallManager.lastNumber, CallManager.lastPhotoUri), onEndCall = { CallService.endCall() }, onOpenSystemDialer = { placePhoneCall(context, CallManager.lastNumber, true) }, onDismiss = { /* Optionally handled by CallManager clear */ })
                    }
                }
            }
        }
    }
}

data class Contact(val id: String, val name: String, val number: String, val photoUri: String?)
data class ActiveCall(val name: String, val number: String, val photoUri: String?)
data class CallRecord(val id: Long, val name: String?, val number: String, val type: Int, val date: Long, val photoUri: String?)
data class SmsMessage(val id: Long, val threadId: Long = 0, val address: String, val body: String, val date: Long, val type: Int = Telephony.Sms.MESSAGE_TYPE_INBOX, val photoUri: String? = null)
data class AppIconOption(val id: String, val name: String, val iconResId: Int, val aliasName: String)

val appIconOptions = listOf(
    AppIconOption("default", "Default", R.drawable.current_logo, "MainActivityDefault"),
    AppIconOption("dark", "Dark", R.drawable.current_logo_dark, "MainActivityDark"),
    AppIconOption("icon1", "Expressive", R.drawable.current_logo_app_icon1, "MainActivityIcon1")
)

@Composable
fun AppIconItem(option: AppIconOption, isSelected: Boolean, onClick: () -> Unit) {
    val cornerRadius by animateDpAsState(targetValue = if (isSelected) 36.dp else 12.dp, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "corner")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp).clickable(onClick = onClick)) {
        Surface(modifier = Modifier.size(72.dp), shape = RoundedCornerShape(cornerRadius), color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Palette, contentDescription = option.name) }
        }
        Text(option.name, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun rememberContactLookup(contacts: List<Contact>): (String) -> Contact? {
    return remember(contacts) {
        val map = contacts.associateBy { contact -> contact.number.filter { it.isDigit() }.takeLast(10) }
        val lookup: (String) -> Contact? = { address ->
            val normalized = address.filter { it.isDigit() }.takeLast(10)
            map[normalized] ?: contacts.find { it.number == address }
        }
        lookup
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(isGoogleConnected: Boolean, userName: String, userPhoto: String, initialContacts: List<Contact>, initialTab: Int, onTabChange: (Int) -> Unit, onSettingsClick: () -> Unit, onDialerClick: () -> Unit, onConversationClick: (String) -> Unit) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val contactLookup = rememberContactLookup(initialContacts)
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Current", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold) },
                    actions = { IconButton(onClick = onSettingsClick) { Icon(Icons.Filled.Settings, contentDescription = "Settings") } },
                    scrollBehavior = scrollBehavior
                )
                if (initialTab > 0) {
                    SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, onSearch = {}, active = false, onActiveChange = {}, placeholder = { Text("Search...") }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {}
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple(Icons.Filled.Home, "Hub", 0),
                    Triple(Icons.AutoMirrored.Filled.List, "Recents", 1),
                    Triple(Icons.Filled.Person, "Contacts", 2),
                    Triple(Icons.AutoMirrored.Filled.Message, "Messages", 3)
                )
                items.forEach { (icon, label, index) ->
                    NavigationBarItem(selected = initialTab == index, onClick = { onTabChange(index) }, icon = { Icon(icon, contentDescription = label) }, label = { Text(label) })
                }
            }
        },
        floatingActionButton = {
            if (initialTab != 0) {
                FloatingActionButton(onClick = { if (initialTab == 1) onDialerClick() else onTabChange(2) }) {
                    Icon(if (initialTab == 1) Icons.Filled.Dialpad else Icons.Filled.Edit, contentDescription = "Action")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (initialTab) {
                0 -> HubDashboard(userName, userPhoto, initialContacts, onConversationClick, onNewCallClick = onDialerClick, onNewMessageClick = { onTabChange(2) })
                1 -> CallLogList(contactLookup = contactLookup) { placePhoneCall(context, it.number) }
                2 -> ContactList(initialContacts, searchQuery, { placePhoneCall(context, it.number) }, { onConversationClick(it.number) })
                3 -> SmsList(searchQuery = searchQuery, contactLookup = contactLookup, onConversationClick = onConversationClick)
            }
        }
    }
}

@Composable
fun HubDashboard(userName: String, userPhoto: String, contacts: List<Contact>, onConversationClick: (String) -> Unit, onNewCallClick: () -> Unit, onNewMessageClick: () -> Unit) {
    val context = LocalContext.current
    val recentCall = remember { getCallLog(context).firstOrNull() }
    val contactLookup = rememberContactLookup(contacts)
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Hello, ${userName.ifBlank { "there" }}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
        val locale = LocalConfiguration.current.locales[0]
        Text(SimpleDateFormat("EEEE, MMMM d", locale).format(Date()), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardBentoCard(Modifier.weight(1f), "New Call", Icons.Filled.Call, MaterialTheme.colorScheme.primaryContainer, onClick = onNewCallClick)
            DashboardBentoCard(Modifier.weight(1f), "New Message", Icons.AutoMirrored.Filled.Message, MaterialTheme.colorScheme.secondaryContainer, onClick = onNewMessageClick)
        }
        
        Spacer(Modifier.height(24.dp))
        
        if (recentCall != null) {
            val contact = contactLookup(recentCall.number)
            ElevatedCard(onClick = { placePhoneCall(context, recentCall.number) }, shape = AppCardShape, modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Last Activity", style = MaterialTheme.typography.labelMedium) },
                    supportingContent = { Text(contact?.name ?: recentCall.number, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                    trailingContent = { Icon(Icons.Filled.RestartAlt, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Text("Frequent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            contacts.take(5).forEach { contact ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onConversationClick(contact.number) }) {
                    Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, contentDescription = null) }
                    }
                    Text(contact.name.split(" ").first(), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun DashboardBentoCard(modifier: Modifier = Modifier, title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = modifier.height(120.dp), shape = AppCardShape, colors = CardDefaults.elevatedCardColors(containerColor = color)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DialerScreen(onBack: () -> Unit, onPlaceCall: (String) -> Unit) {
    var number by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("Dialer") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(number, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 48.dp))
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
            keys.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { key -> DialerKey(key) { number += key } }
                }
            }
            Spacer(Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { if (number.isNotEmpty()) number = number.dropLast(1) }) { Icon(Icons.Filled.Clear, contentDescription = "Delete") }
                FloatingActionButton(onClick = { onPlaceCall(number) }, containerColor = Color.Green) { Icon(Icons.Filled.Call, contentDescription = "Call") }
            }
        }
    }
}

@Composable
fun DialerKey(key: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 16.dp else 40.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "corner"
    )

    Surface(
        modifier = Modifier
            .size(80.dp)
            .padding(6.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(key, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CallLogList(contactLookup: (String) -> Contact?, onCallClick: (ActiveCall) -> Unit) {
    val context = LocalContext.current
    val callLog = remember { getCallLog(context) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(callLog) { record ->
            val contact = contactLookup(record.number)
            val displayName = contact?.name ?: record.name ?: record.number
            val isOutgoing = record.type == CallLog.Calls.OUTGOING_TYPE
            
            ElevatedCard(onClick = { onCallClick(ActiveCall(displayName, record.number, contact?.photoUri)) }, shape = AppCardShape, modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(displayName, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(record.number) },
                    leadingContent = {
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Box(contentAlignment = Alignment.Center) {
                                if (contact?.photoUri != null) {
                                    AsyncImage(model = contact.photoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Filled.Person, contentDescription = null)
                                }
                            }
                        }
                    },
                    trailingContent = {
                        Icon(
                            if (isOutgoing) Icons.Default.NorthEast else Icons.Default.SouthWest,
                            contentDescription = if (isOutgoing) "Outgoing" else "Incoming",
                            tint = if (isOutgoing) MaterialTheme.colorScheme.primary else Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun ContactList(contacts: List<Contact>, searchQuery: String, onCallContact: (ActiveCall) -> Unit, onSmsContact: (Contact) -> Unit) {
    val filtered = contacts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.number.contains(searchQuery) }
    val sharedPrefs = LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(filtered) { contact ->
            var expanded by remember { mutableStateOf(false) }
            var menuExpanded by remember { mutableStateOf(false) }
            var isBlocked by remember { mutableStateOf(sharedPrefs.getBoolean("blocked_${contact.number}", false)) }

            ElevatedCard(onClick = { expanded = !expanded }, shape = AppCardShape, modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text(contact.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(contact.number) },
                        leadingContent = {
                            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (contact.photoUri != null) {
                                        AsyncImage(model = contact.photoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } else {
                                        Icon(Icons.Filled.Person, contentDescription = null)
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            Box {
                                IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu") }
                                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text(if (isBlocked) "Unblock" else "Block") },
                                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null) },
                                        onClick = {
                                            isBlocked = !isBlocked
                                            sharedPrefs.edit().putBoolean("blocked_${contact.number}", isBlocked).apply()
                                            menuExpanded = false
                                        }
                                    )
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    
                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val interactionSource1 = remember { MutableInteractionSource() }
                            val interactionSource2 = remember { MutableInteractionSource() }
                            
                            ButtonGroup(
                                overflowIndicator = { menuState: ButtonGroupMenuState -> ButtonGroupDefaults.OverflowIndicator(menuState = menuState) },
                                modifier = Modifier.fillMaxWidth(),
                                expandedRatio = 1f
                            ) {
                                customItem(
                                    buttonGroupContent = {
                                        ExpressiveButton(
                                            onClick = { onSmsContact(contact) },
                                            modifier = Modifier.animateWidth(interactionSource1, ButtonDefaults.ButtonWithIconContentPadding),
                                            enabled = !isBlocked
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Message", softWrap = false, overflow = TextOverflow.Visible)
                                        }
                                    },
                                    menuContent = { DropdownMenuItem(text = { Text("Message") }, onClick = { onSmsContact(contact) }) }
                                )
                                
                                customItem(
                                    buttonGroupContent = {
                                        ExpressiveButton(
                                            onClick = { onCallContact(ActiveCall(contact.name, contact.number, contact.photoUri)) },
                                            modifier = Modifier.animateWidth(interactionSource2, ButtonDefaults.ButtonWithIconContentPadding),
                                            enabled = !isBlocked
                                        ) {
                                            Icon(Icons.Filled.Call, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Call", softWrap = false, overflow = TextOverflow.Visible)
                                        }
                                    },
                                    menuContent = { DropdownMenuItem(text = { Text("Call") }, onClick = { onCallContact(ActiveCall(contact.name, contact.number, contact.photoUri)) }) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmsList(searchQuery: String, contactLookup: (String) -> Contact?, onConversationClick: (String) -> Unit) {
    val context = LocalContext.current
    val sms = remember { getSms(context) }
    val threads = sms.distinctBy { it.threadId }
    
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(threads) { msg ->
            val contact = contactLookup(msg.address)
            val displayName = contact?.name ?: msg.address
            
            ElevatedCard(onClick = { onConversationClick(msg.address) }, shape = AppCardShape, modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(displayName, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(msg.body, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = {
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                            Box(contentAlignment = Alignment.Center) {
                                if (contact?.photoUri != null) {
                                    AsyncImage(model = contact.photoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null)
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

@Composable
fun ChatScreen(address: String, contacts: List<Contact>, onBack: () -> Unit) {
    val context = LocalContext.current
    var messages by remember { mutableStateOf(getSmsHistory(context, address)) }
    var text by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text(address) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }, bottomBar = {
        Row(modifier = Modifier.padding(16.dp).windowInsetsPadding(WindowInsets.ime)) {
            TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
            IconButton(onClick = { sendSms(context, address, text); text = ""; messages = getSmsHistory(context, address) }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null) }
        }
    }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize(), reverseLayout = true) {
            items(messages) { msg -> Text(msg.body, modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) }
        }
    }
}

@Composable
fun SettingsScreen(isGoogleConnected: Boolean, userName: String, userPhoto: String, userEmail: String, currentSeedColor: Color?, currentAppIcon: String, onSeedColorChange: (Color?) -> Unit, onAppIconChange: (String) -> Unit, onAccountClick: () -> Unit, onConnectClick: () -> Unit, onDeveloperOptionsClick: () -> Unit, onAboutClick: () -> Unit, onLogoutClick: () -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            item { ListItem(headlineContent = { Text(if (isGoogleConnected) userName else "Connect Account") }, modifier = Modifier.clickable(onClick = if (isGoogleConnected) onAccountClick else onConnectClick)) }
            item { Text("Appearance", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.primary) }
            item { Row(modifier = Modifier.horizontalScroll(rememberScrollState())) { appIconOptions.forEach { AppIconItem(it, currentAppIcon == it.id) { onAppIconChange(it.id) } } } }
            item { ListItem(headlineContent = { Text("Developer Options") }, modifier = Modifier.clickable(onClick = onDeveloperOptionsClick)) }
            item { ListItem(headlineContent = { Text("About") }, modifier = Modifier.clickable(onClick = onAboutClick)) }
            if (isGoogleConnected) item { TextButton(onClick = onLogoutClick) { Text("Sign Out", color = Color.Red) } }
        }
    }
}

@Composable
fun AccountScreen(userName: String, userPhoto: String, userEmail: String, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Account") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp)) {
            Text(userName, style = MaterialTheme.typography.displaySmall); Text(userEmail)
        }
    }
}

@Composable
fun DeveloperOptionsScreen(onBack: () -> Unit) { Text("Dev Options", modifier = Modifier.clickable(onClick = onBack)) }

@Composable
fun AboutScreen(onBack: () -> Unit) { Text("About Current Beta 2", modifier = Modifier.clickable(onClick = onBack)) }

@Composable
fun CurrentWelcomeScreen(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Welcome to Current", style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(48.dp))
        ExpressiveButton(onClick = onContinue) { Text("Get Started") }
    }
}

@Composable
fun RationaleScreen(onPermitClick: () -> Unit, onContinueClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text("Permissions required"); ExpressiveButton(onClick = onPermitClick) { Text("Grant") }; TextButton(onClick = onContinueClick) { Text("Later") }
    }
}

@Composable
fun PermissionsScreen(onAllPermissionsGranted: () -> Unit, onPermissionsDenied: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results -> if (results.values.all { it }) onAllPermissionsGranted() else onPermissionsDenied() }
    LaunchedEffect(Unit) { launcher.launch(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS)) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
fun DefaultAppsScreen(onContinue: () -> Unit) { ExpressiveButton(onClick = onContinue) { Text("Set Default Apps") } }
@Composable
fun AreYouSurePermissionsScreen(onSureClick: () -> Unit, onBackClick: () -> Unit) { Text("Sure?", modifier = Modifier.clickable(onClick = onSureClick)) }
@Composable
fun BackupScreen(onImportClick: () -> Unit, onSkipClick: () -> Unit) { ExpressiveButton(onClick = onImportClick) { Text("Connect Google") }; TextButton(onClick = onSkipClick) { Text("Skip") } }
@Composable
fun GoogleConnectScreen(onConnected: (String, String?, String?) -> Unit, onBack: () -> Unit) {
    LaunchedEffect(Unit) { delay(1000); onConnected("Demo User", null, "demo@example.com") }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
}
@Composable
fun AreYouSureBackupScreen(onSureClick: () -> Unit, onBackClick: () -> Unit) { TextButton(onClick = onSureClick) { Text("Finish") } }

@Composable
fun FullDialerDialog(onDismiss: () -> Unit, onPlaceCall: (String) -> Unit) {
    AlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            DialerScreen(onDismiss, onPlaceCall)
        }
    }
}

@Composable
fun IncomingCallScreen(call: ActiveCall, onAccept: () -> Unit, onDecline: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(call.name, style = MaterialTheme.typography.displayMedium); Text(call.number)
            Row { IconButton(onClick = onDecline) { Icon(Icons.Filled.CallEnd, contentDescription = null, tint = Color.Red) }; IconButton(onClick = onAccept) { Icon(Icons.Filled.Call, contentDescription = null, tint = Color.Green) } }
        }
    }
}

@Composable
fun ActiveCallScreen(call: ActiveCall, onEndCall: () -> Unit, onOpenSystemDialer: () -> Unit, onDismiss: () -> Unit) {
    var showAddCallDialer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(call.name, style = MaterialTheme.typography.displayMedium)
            LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth(0.5f))
            Row {
                IconButton(onClick = onEndCall) { Icon(Icons.Filled.CallEnd, contentDescription = null, tint = Color.Red) }
                IconButton(onClick = { showAddCallDialer = true }) { Icon(Icons.Filled.PersonAdd, contentDescription = null) }
            }
        }
        if (showAddCallDialer) FullDialerDialog(onDismiss = { showAddCallDialer = false }, onPlaceCall = { placePhoneCall(context, it); showAddCallDialer = false })
    }
}

@Composable
fun InCallActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) { Icon(icon, contentDescription = label, tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
}

private fun placePhoneCall(context: Context, number: String, forceDialer: Boolean = false): Boolean {
    val intent = Intent(if (forceDialer) Intent.ACTION_DIAL else Intent.ACTION_CALL, Uri.parse("tel:$number")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    return try { context.startActivity(intent); true } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }); false }
}

fun getCallLog(context: Context): List<CallRecord> {
    val list = mutableListOf<CallRecord>()
    context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC")?.use { cursor ->
        val numIdx = cursor.getColumnIndex(CallLog.Calls.NUMBER); val dateIdx = cursor.getColumnIndex(CallLog.Calls.DATE); val typeIdx = cursor.getColumnIndex(CallLog.Calls.TYPE)
        while (cursor.moveToNext()) {
            if (numIdx != -1 && dateIdx != -1 && typeIdx != -1) {
                list.add(CallRecord(0, null, cursor.getString(numIdx) ?: "", cursor.getInt(typeIdx), cursor.getLong(dateIdx), null))
            }
        }
    }
    return list
}

fun getContacts(context: Context): List<Contact> {
    val list = mutableListOf<Contact>()
    context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")?.use { cursor ->
        val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME); val numIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cursor.moveToNext()) {
            if (nameIdx != -1 && numIdx != -1) {
                list.add(Contact("", cursor.getString(nameIdx) ?: "", cursor.getString(numIdx) ?: "", null))
            }
        }
    }
    return list.distinctBy { it.number }
}

fun getSms(context: Context): List<SmsMessage> {
    val list = mutableListOf<SmsMessage>()
    context.contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC")?.use { cursor ->
        val bodyIdx = cursor.getColumnIndex(Telephony.Sms.BODY); val addrIdx = cursor.getColumnIndex(Telephony.Sms.ADDRESS); val tidIdx = cursor.getColumnIndex(Telephony.Sms.THREAD_ID)
        while (cursor.moveToNext()) {
            if (bodyIdx != -1 && addrIdx != -1 && tidIdx != -1) {
                list.add(SmsMessage(0, cursor.getLong(tidIdx), cursor.getString(addrIdx) ?: "", cursor.getString(bodyIdx) ?: "", 0, 0, null))
            }
        }
    }
    return list
}

fun getSmsHistory(context: Context, address: String): List<SmsMessage> {
    val list = mutableListOf<SmsMessage>()
    context.contentResolver.query(Telephony.Sms.CONTENT_URI, null, "address = ?", arrayOf(address), Telephony.Sms.DATE + " DESC")?.use { cursor ->
        val bodyIdx = cursor.getColumnIndex(Telephony.Sms.BODY)
        while (cursor.moveToNext()) {
            if (bodyIdx != -1) {
                list.add(SmsMessage(0, 0, address, cursor.getString(bodyIdx) ?: "", 0, 0, null))
            }
        }
    }
    return list
}

fun sendSms(context: Context, address: String, message: String) {
    try {
        val smsManager = context.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(address, null, message, null, null)
    } catch (e: Exception) {
        Toast.makeText(context, "SMS Failed", Toast.LENGTH_SHORT).show()
    }
}
