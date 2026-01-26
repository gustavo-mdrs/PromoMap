package com.example.promomap

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.ui.theme.PromoMapTheme
import com.example.promomap.ui.theme.nav.MainNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 1. Inicialização do Banco e ViewModel
            val fbDB = remember { FBDatabase() }

            // Usando a Factory para injetar o Banco no ViewModel
            val viewModel: MainViewModel by viewModels {
                MainViewModelFactory(fbDB)
            }

            val navController = rememberNavController()

            // 2. Permissão de Localização (Essencial para o MapPage)
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    // Opcional: Lidar com a resposta da permissão
                }
            )

            PromoMapTheme {
                // Removemos a BottomNavBar daqui.
                // A navegação agora depende exclusivamente dos botões dentro das telas (Home, Perfil, etc.)
                Scaffold { innerPadding ->

                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Solicita permissão assim que o app abre
                        LaunchedEffect(Unit) {
                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }

                        // 3. O Host que exibe as telas
                        MainNavHost(navController = navController, viewModel = viewModel)
                    }

                    // 4. Navegação Reativa
                    // O app observa 'viewModel.page'.
                    // Se você clicar no botão "Mapa" na Home, a variável muda e este bloco troca a tela.
                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            // Limpa a pilha ao voltar para a Home para evitar "loops" de voltar
                            navController.graph.startDestinationRoute?.let { startRoute ->
                                popUpTo(startRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }
}