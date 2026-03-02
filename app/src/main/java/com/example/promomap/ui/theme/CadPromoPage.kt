package com.example.promomap.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadPromoPage(
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onSaveClick: (String, String, String, Double, LatLng) -> Unit // <- Agora exige o LatLng
) {
    var localName by remember { mutableStateOf("") }
    var produto by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var promoLocation by remember { mutableStateOf<LatLng?>(null) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    // Ferramenta nativa do Android para buscar o GPS
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permissão de GPS caso o usuário não tenha dado ainda
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permissão de localização necessária!", Toast.LENGTH_SHORT).show()
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionando Promoção", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo 1: Nome do Local
            OutlinedTextField(
                value = localName,
                onValueChange = { localName = it },
                label = { Text("Endereço Completo ou Nome do Local") },
                placeholder = { Text("Ex: Rua das Flores, 123 - Recife") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF1B5E20)) },
                shape = RoundedCornerShape(8.dp)
            )

            // Botão de Capturar GPS Atual
            TextButton(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        scope.launch {
                            isFetchingLocation = true
                            try {
                                val location = fusedLocationClient.lastLocation.await()
                                if (location != null) {
                                    promoLocation = LatLng(location.latitude, location.longitude)
                                    Toast.makeText(context, "GPS capturado com sucesso!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Erro ao obter GPS. Certifique-se de que o GPS está ligado.", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao buscar satélite.", Toast.LENGTH_SHORT).show()
                            } finally {
                                isFetchingLocation = false
                            }
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.Place, contentDescription = "Pegar GPS", tint = Color(0xFF1B5E20))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFetchingLocation) "Buscando satélite..."
                    else if (promoLocation != null) "GPS Capturado com sucesso! ✓"
                    else "Usar minha localização atual (GPS)",
                    color = if (promoLocation != null) Color(0xFF1B5E20) else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 2: Produto
            OutlinedTextField(
                value = produto,
                onValueChange = { produto = it },
                label = { Text("Nome do Produto") },
                placeholder = { Text("Ex: Papel Higiênico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 3: Marca
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                placeholder = { Text("Ex: Neve") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 4: Preço
            OutlinedTextField(
                value = preco,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' || char == ',' }) {
                        preco = it
                    }
                },
                label = { Text("Preço (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Adicionar Imagem (Placeholder)
            Text(text = "Foto do Produto", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9))
                    .border(2.dp, Color(0xFF80CBC4), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onImageClick() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color(0xFF1B5E20), modifier = Modifier.size(48.dp))
                    Text("Adicionar Imagem", color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÕES DE AÇÃO ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Voltar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // BOTÃO CADASTRAR COM A LÓGICA DE VALIDAÇÃO
                Button(
                    onClick = {
                        val precoDouble = preco.replace(",", ".").toDoubleOrNull() ?: 0.0

                        scope.launch {
                            var finalLocation = promoLocation

                            // Se o usuário não clicou no GPS, tenta converter o endereço digitado
                            if (finalLocation == null && localName.isNotBlank()) {
                                finalLocation = withContext(Dispatchers.IO) {
                                    try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val addresses = geocoder.getFromLocationName(localName, 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            LatLng(addresses[0].latitude, addresses[0].longitude)
                                        } else null
                                    } catch (e: Exception) {
                                        null // Se falhar a conversão (ex: digitou "AsdAsd"), retorna nulo
                                    }
                                }
                            }

                            // Verifica se conseguiu a coordenada (seja por GPS ou Geocoder)
                            if (finalLocation != null) {
                                // Deu certo! Manda salvar (o MainNavHost que mudará a tela para o Mapa)
                                onSaveClick(localName, produto, marca, precoDouble, finalLocation)
                            } else {
                                // DEU ERRO! Avisa o usuário na tela atual e NÃO SAI DA TELA.
                                Toast.makeText(
                                    context,
                                    "Erro de localização! Aperte o botão do GPS ou digite um endereço real/completo.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Cadastrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun CadPromoPagePreview() {
    CadPromoPage(onBackClick = {}, onImageClick = {}, onSaveClick = { _, _, _, _, _ -> })
}