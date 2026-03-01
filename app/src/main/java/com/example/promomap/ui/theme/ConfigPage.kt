package com.example.promomap.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promomap.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigPage(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val themeColor = Color(0xFF1B5E20)

    var expandedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- SEÇÃO NOTIFICAÇÕES ---
            ExpandableConfigCard(
                title = "Notificações",
                icon = Icons.Default.Notifications,
                isExpanded = expandedSection == "notif",
                onExpandClick = { expandedSection = if (expandedSection == "notif") null else "notif" }
            ) {
                ConfigSwitchItem(
                    title = "Promoções próximas",
                    subtitle = "Notificar ofertas próximas a locais de interesse",
                    isChecked = true,
                    onToggle = { viewModel.toggleNotification("promo_proxima", it) }
                )
                ConfigSwitchItem(
                    title = "Produtos de interesse",
                    subtitle = "Alertar sobre baixas de preço em favoritos",
                    isChecked = true,
                    onToggle = { viewModel.toggleNotification("produto_fav", it) }
                )
                ConfigSwitchItem(
                    title = "Alta densidade",
                    subtitle = "Notificar locais com muitos anúncios",
                    isChecked = true,
                    onToggle = { viewModel.toggleNotification("alta_densidade", it) }
                )
                ConfigSwitchItem(
                    title = "Finalizadas",
                    subtitle = "Alertar promoções encerradas perto de locais de interesse",
                    isChecked = true,
                    onToggle = { viewModel.toggleNotification("finalizada", it) }
                )
            }

            // --- SEÇÃO FAVORITOS ---
            ExpandableConfigCard(
                title = "Favoritos",
                icon = Icons.Default.Favorite,
                isExpanded = expandedSection == "fav",
                onExpandClick = { expandedSection = if (expandedSection == "fav") null else "fav" }
            ) {
                var newFav by remember { mutableStateOf("") }
                val favs by viewModel.favorites.collectAsState() // Lê do banco

                Column {
                    favs.forEach { fav ->
                        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• $fav")
                            IconButton(onClick = { viewModel.removeFavoriteProduct(fav) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Gray)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newFav,
                        onValueChange = { newFav = it },
                        label = { Text("Novo Produto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = {
                        viewModel.addFavoriteProduct(newFav)
                        newFav = ""
                    }) {
                        Text("Adicionar")
                    }
                }
            }

            // --- SEÇÃO LOCAIS ---
            ExpandableConfigCard(
                title = "Meus Locais",
                icon = Icons.Default.LocationOn,
                isExpanded = expandedSection == "locais",
                onExpandClick = { expandedSection = if (expandedSection == "locais") null else "locais" }
            ) {
                var apelido by remember { mutableStateOf("") }
                var cep by remember { mutableStateOf("") }
                var endereco by remember { mutableStateOf("") }
                var raio by remember { mutableStateOf("5") }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                val locaisSalvos by viewModel.savedLocations.collectAsState()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = apelido,
                        onValueChange = { apelido = it },
                        label = { Text("Apelido (ex: Casa)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // LINHA COM CEP E BOTÃO DE BUSCAR
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = cep,
                            onValueChange = { cep = it },
                            label = { Text("CEP (Apenas números)") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (cep.length == 8) {
                                scope.launch {
                                    try {
                                        val resp = com.example.promomap.util.ViaCepClient.api.buscarCep(cep)
                                        endereco = "${resp.logradouro}, ${resp.bairro}, ${resp.localidade} - ${resp.uf}"
                                    } catch (e: Exception) { /* Erro ao buscar CEP */ }
                                }
                            }
                        }) { Text("Buscar CEP") }
                    }

                    OutlinedTextField(
                        value = endereco,
                        onValueChange = { endereco = it },
                        label = { Text("Endereço Completo (Adicione o número)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = raio,
                            onValueChange = { raio = it },
                            label = { Text("Raio (km)") },
                            modifier = Modifier.width(100.dp)
                        )

                        // BOTÃO SALVAR (AGORA COM COORDENADAS)
                        Button(
                            onClick = {
                                if (apelido.isNotBlank() && endereco.isNotBlank()) {
                                    scope.launch {
                                        // Converte o endereço digitado em Coordenadas!
                                        val coords = com.example.promomap.util.LocationUtils.obterCoordenadasPorEndereco(context, endereco)
                                        if (coords != null) {
                                            viewModel.saveNewLocation(apelido, endereco, raio, coords.latitude, coords.longitude)
                                            apelido = ""; cep = ""; endereco = ""; raio = "5"
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Text(" Salvar")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Exibe a lista real do Firebase
                    locaisSalvos.forEach { loc ->
                        SavedLocationItem(
                            label = loc["name"] ?: "",
                            address = loc["address"] ?: "",
                            radius = loc["radius"] ?: "5",
                            onDeleteClick = { viewModel.removeSavedLocation(loc) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ExpandableConfigCard(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF1B5E20))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun SavedLocationItem(label: String, address: String, radius: String, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(address, fontSize = 12.sp, color = Color.Gray)
            Text("Raio: $radius", fontSize = 11.sp, color = Color(0xFF1B5E20))
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Gray)
        }
    }
}

@Composable
fun ConfigSwitchItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle(it) } // Dispara a função do ViewModel
        )
    }
}