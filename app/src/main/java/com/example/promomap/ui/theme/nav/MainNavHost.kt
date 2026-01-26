package com.example.promomap.ui.theme.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.promomap.MainViewModel
import com.example.promomap.model.Promo
import com.example.promomap.ui.* // Importa todas as suas páginas (Home, Map, Config, etc)
import com.example.promomap.ui.theme.CadPromoPage
import com.example.promomap.ui.theme.ConfigPage
import com.example.promomap.ui.theme.HomePage
import com.example.promomap.ui.theme.MapPage
import com.example.promomap.ui.theme.PerfilPage
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {

        // --- TELA 1: HOME ---
        composable<Route.Home> {
            HomePage(
                userName = viewModel.user?.name ?: "Usuário",
                // Navegação: Apenas mudamos o estado do ViewModel
                onNavigateToMap = { viewModel.page = Route.Map },
                onNavigateToCadPromo = { viewModel.page = Route.CadPromo },
                onNavigateToConfig = { viewModel.page = Route.Config },
                onNavigateToProfile = { viewModel.page = Route.Perfil },
                onLogout = {
                    Firebase.auth.signOut()
                    // A MainActivity deve tratar o fechamento ou o listener do Firebase fará isso
                }
            )
        }

        // --- TELA 2: MAPA ---
        composable<Route.Map> {
            // A MapPage recebe o ViewModel inteiro para ler a lista de promos
            MapPage(viewModel = viewModel)
        }

        // --- TELA 3: CONFIGURAÇÕES ---
        composable<Route.Config> {
            ConfigPage(
                onBackClick = { viewModel.page = Route.Home },
                onDeleteAccountClick = {
                    // Lógica futura de excluir conta
                }
            )
        }

        // --- TELA 4: PERFIL ---
        composable<Route.Perfil> {
            val user = viewModel.user
            PerfilPage(
                userName = user?.name ?: "Carregando...",
                userEmail = user?.email ?: "",
                userCpf = user?.cpf ?: "",
                onBackClick = { viewModel.page = Route.Home },
                onEditClick = { /* Futuro: Ir para edição */ },
                onLogoutClick = { Firebase.auth.signOut() }
            )
        }

        // --- TELA 5: CADASTRO DE PROMOÇÃO ---
        composable<Route.CadPromo> {
            CadPromoPage(
                onBackClick = { viewModel.page = Route.Home },
                onImageClick = {
                    // Futuro: Abrir galeria
                },
                onSaveClick = { local, produto, marca, preco ->
                    // Lógica para salvar a promoção
                    // 1. Criar o objeto Promo
                    val novaPromo = Promo(
                        item = "$produto - $local", // Nomeamos o item com o local para facilitar
                        marca = marca,
                        preco = preco,
                        // ATENÇÃO: Aqui estamos usando uma localização fixa de RECIFE para teste
                        // No passo anterior da UI, removemos a lógica de GPS da View.
                        // Para funcionar real, precisaríamos passar a location aqui.
                        localizacao = LatLng(-8.05, -34.90),
                        status = "Ativa"
                    )

                    // 2. Salvar no Firebase através do ViewModel
                    viewModel.addPromo(novaPromo)

                    // 3. Redirecionar para o Mapa para ver o pino criado
                    viewModel.page = Route.Map
                }
            )
        }
    }
}