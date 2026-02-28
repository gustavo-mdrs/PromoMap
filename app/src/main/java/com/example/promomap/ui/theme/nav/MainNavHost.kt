package com.example.promomap.ui.theme.nav

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.promomap.LoginActivity
import com.example.promomap.MainViewModel
import com.example.promomap.model.Promo
import com.example.promomap.ui.theme.CadPromoPage
import com.example.promomap.ui.theme.ConfigPage
import com.example.promomap.ui.theme.HomePage
import com.example.promomap.ui.theme.MapPage
import com.example.promomap.ui.theme.PerfilPage
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    // Pegamos o contexto para poder iniciar a Activity de Login ao sair
    val context = LocalContext.current

    val user by viewModel.user.collectAsState()

    // Função centralizada para fazer o logout e limpar a pilha de navegação
    val fazerLogout = {
        Firebase.auth.signOut()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {

        // --- TELA 1: HOME ---
        composable<Route.Home> {
            HomePage(
                userName = user?.name ?: "Carregando...",
                onNavigateToMap = { viewModel.page = Route.Map },
                onNavigateToCadPromo = { viewModel.page = Route.CadPromo },
                onNavigateToConfig = { viewModel.page = Route.Config },
                onNavigateToProfile = { viewModel.page = Route.Perfil },
                onLogout = { fazerLogout() }
            )
        }

        // --- TELA 2: MAPA ---
        composable<Route.Map> {
            MapPage(viewModel = viewModel)
        }

        // --- TELA 3: CONFIGURAÇÕES ---
        composable<Route.Config> {
            ConfigPage(
                viewModel = viewModel,
                onBackClick = { viewModel.page = Route.Home }
            )
        }

        // --- TELA 4: PERFIL ---
        composable<Route.Perfil> {
            PerfilPage(
                userName = user?.name ?: "Carregando...",
                userEmail = user?.email ?: "",
                userCpf = user?.cpf ?: "",
                onBackClick = { viewModel.page = Route.Home },
                onEditNameClick = {
                    newName = user?.name ?: ""
                    showEditDialog = true
                }
            )

            // Lógica do Diálogo de Edição
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Editar Nome") },
                    text = {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Nome Completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    // Dentro do composable<Route.Perfil>
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (newName.isNotBlank()) {
                                    // 1. DISPARA A LÓGICA
                                    viewModel.updateUserName(newName)
                                    // 2. FECHA O DIÁLOGO
                                    showEditDialog = false
                                }
                            }
                        ) { Text("Salvar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }

        // --- TELA 5: CADASTRO DE PROMOÇÃO ---
        composable<Route.CadPromo> {
            CadPromoPage(
                onBackClick = { viewModel.page = Route.Home },
                onImageClick = {
                    // Futuro: Abrir galeria
                },
                onSaveClick = { local, produto, marca, preco ->
                    val novaPromo = Promo(
                        item = "$produto - $local",
                        marca = marca,
                        preco = preco,
                        localizacao = LatLng(-8.05, -34.90),
                        status = "Ativa"
                    )

                    viewModel.addPromo(novaPromo)
                    viewModel.page = Route.Map
                }
            )
        }
    }
}