package com.scoutcipher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.scoutcipher.ui.screens.*
import com.scoutcipher.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoutCipherTheme(darkTheme = true) {
                val systemUiController = rememberSystemUiController()
                val bgColor = MaterialTheme.colorScheme.background
                SideEffect {
                    systemUiController.setSystemBarsColor(color = bgColor)
                }
                ScoutCipherApp(viewModel)
            }
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val NAV_ITEMS = listOf(
    NavItem("تشفير",      Icons.Default.Lock,        "encode"),
    NavItem("فك التشفير", Icons.Default.LockOpen,    "decode"),
    NavItem("مرجع",       Icons.Default.MenuBook,    "reference"),
    NavItem("تحدي",       Icons.Default.EmojiEvents, "challenge"),
    NavItem("السجل",      Icons.Default.History,     "history"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoutCipherApp(viewModel: MainViewModel) {
    var currentRoute by remember { mutableStateOf("encode") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "⚜️ شفرات الكشاف",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            "Scout Cipher Kit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NAV_ITEMS.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = { currentRoute = item.route },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(if (currentRoute == item.route) 24.dp else 22.dp)
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor    = MaterialTheme.colorScheme.primary,
                            selectedTextColor    = MaterialTheme.colorScheme.primary,
                            unselectedIconColor  = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor  = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor       = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Crossfade(targetState = currentRoute, label = "nav") { route ->
                when (route) {
                    "encode"    -> EncodeDecodeScreen(viewModel, isEncode = true)
                    "decode"    -> EncodeDecodeScreen(viewModel, isEncode = false)
                    "reference" -> ReferenceScreen()
                    "challenge" -> ChallengeScreen(viewModel)
                    "history"   -> HistoryScreen(viewModel)
                }
            }
        }
    }
}
