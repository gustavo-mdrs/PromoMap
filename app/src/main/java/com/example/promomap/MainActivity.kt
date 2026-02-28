package com.example.promomap

import android.Manifest
import android.os.Build
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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.repo.PromoRepository
import com.example.promomap.ui.theme.PromoMapTheme
import com.example.promomap.ui.theme.nav.MainNavHost
import com.example.promomap.repo.UserRepository
import com.example.promomap.util.NotificationHelper
import com.example.promomap.util.PromoWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        NotificationHelper.createNotificationChannel(this)


        val promoWorkRequest = PeriodicWorkRequestBuilder<PromoWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ChecarPromocoesWork",
            ExistingPeriodicWorkPolicy.KEEP, // Mantém se já estiver agendado
            promoWorkRequest
        )

        enableEdgeToEdge()

        setContent {
            val fbDB = remember { FBDatabase() }


            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            // Instanciando ambos os repositórios
            val promoRepo = remember { PromoRepository(fbDB) }
            val userRepo = remember { UserRepository(fbDB) }

            // Passando ambos para a Factory
            val viewModel: MainViewModel by viewModels {
                MainViewModelFactory(promoRepo, userRepo)
            }

            val navController = rememberNavController()

            // 2. Permissão de Localização (Essencial para o MapPage)
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
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