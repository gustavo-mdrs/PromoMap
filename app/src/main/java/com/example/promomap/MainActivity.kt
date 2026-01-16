package com.example.promomap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.ui.theme.PromoMapTheme
import com.example.promomap.ui.nav.BottomNavBar
import com.example.promomap.ui.nav.BottomNavItem
import com.example.promomap.ui.nav.MainNavHost
import com.example.promomap.ui.nav.Route
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Inicialização conforme Prática 06 e 09
            val fbDB = remember { FBDatabase() }
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB)
            )

            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()

            // Define se o FAB aparece (Apenas na tela de Lista ou Mapa, conforme sua lógica)
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true

            // Lançador de permissão de localização (Necessário para o Slide 9)
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )

            // Estado para o diálogo de nova promoção (Slide 8)
            var showDialog by remember { mutableStateOf(false) }

            PromoMapTheme {
                // Diálogo adaptado para adicionar promoções
                if (showDialog) {
                    // Aqui você chamará o seu Composable de diálogo para Promoções
                    // Ex: PromoDialog(onDismiss = { showDialog = false }, onConfirm = { ... })
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = viewModel.user?.name ?: "[carregando...]"
                                Text("Bem-vindo/a! $name")
                            },
                            actions = {
                                IconButton(onClick = {
                                    Firebase.auth.signOut()
                                    // O AuthStateListener na classe Application cuidará de voltar ao Login
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,
                        )
                        BottomNavBar(viewModel, items)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar Promoção")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Solicita permissão ao carregar (Prática 04)
                        LaunchedEffect(Unit) {
                            launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }

                        MainNavHost(navController = navController, viewModel = viewModel)
                    }

                    // Sincronização da navegação com o estado do ViewModel (Prática 08)
                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) { saveState = true }
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}