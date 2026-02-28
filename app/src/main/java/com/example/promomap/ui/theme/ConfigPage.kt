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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promomap.MainViewModel

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
                            IconButton(onClick = { /* viewModel.removeFavorite(fav) */ }) {
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
                var endereco by remember { mutableStateOf("") }
                var raio by remember { mutableStateOf("5") }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Adicione locais como Casa ou Trabalho para monitorar raios de busca.", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = apelido,
                        onValueChange = { apelido = it },
                        label = { Text("Apelido (ex: Casa)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = endereco,
                        onValueChange = { endereco = it },
                        label = { Text("Endereço Completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = raio,
                            onValueChange = { raio = it },
                            label = { Text("Raio (km)") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { /* Ação de salvar local */ },
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text(" Salvar")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Exemplo de item salvo
                    SavedLocationItem("Casa", "Rua Exemplo, 123", "5km")
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
fun SavedLocationItem(label: String, address: String, radius: String) {
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
        IconButton(onClick = { /* Deletar local */ }) {
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