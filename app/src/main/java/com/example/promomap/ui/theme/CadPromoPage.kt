package com.example.promomap.ui.theme

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
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadPromoPage(
    // Callbacks: A tela avisa "o usuário clicou aqui", mas não decide o que fazer
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    onSaveClick: (String, String, String, Double, LatLng?) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var promoLocation by remember { mutableStateOf<LatLng?>(null) }
    var localName by remember { mutableStateOf("") }
    var isFetchingLocation by remember { mutableStateOf(false) }
    var produto by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }

    // ScrollState para garantir que a tela role se o teclado cobrir os campos
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Adicionando Promoção",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20) // Verde escuro (Identidade do PromoMap)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
                .verticalScroll(scrollState), // Habilita rolagem vertical
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- INÍCIO DO FORMULÁRIO (Baseado no Slide 8) ---

            // Campo 1: Nome do Local
            OutlinedTextField(
                value = localName,
                onValueChange = { localName = it },
                label = { Text("Nome do Local") },
                placeholder = { Text("Ex: Mix Mateus") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF1B5E20))
                },
                shape = RoundedCornerShape(8.dp)
            )
            TextButton(
                onClick = {
                    scope.launch {
                        isFetchingLocation = true
                        // Chama a nossa função de GPS do LocationUtils
                        val gps = com.example.promomap.util.LocationUtils.obterLocalizacaoAtual(context)
                        if (gps != null) {
                            promoLocation = gps
                            android.widget.Toast.makeText(context, "Localização capturada!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(context, "Ligue o GPS do celular.", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        isFetchingLocation = false
                    }
                },
                modifier = Modifier.align(Alignment.Start) // Alinha à esquerda
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
                    // Pequena lógica de UI apenas para permitir números e ponto/vírgula
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

            // --- ÁREA DE UPLOAD DE FOTO (Placeholder Visual) ---
            Text(
                text = "Foto do Produto",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9)) // Fundo verde bem claro
                    .border(2.dp, Color(0xFF80CBC4), RoundedCornerShape(12.dp)) // Borda tracejada visual
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onImageClick() }
                    ), // Chama o evento, a lógica de abrir galeria fica fora
                contentAlignment = Alignment.Center
            ) {
                // Aqui entraria a lógica de mostrar a imagem selecionada futuramente
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Adicionar Imagem",
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÕES DE AÇÃO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão Voltar
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Voltar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botão Cadastrar
                Button(
                    onClick = {
                        val precoDouble = preco.replace(",", ".").toDoubleOrNull() ?: 0.0

                        // Lançamos uma coroutine porque a busca do endereço pode levar alguns milissegundos
                        scope.launch {
                            // Verifica se o usuário já pegou o GPS. Se não, tenta converter o nome digitado em coordenadas.
                            val finalLocation = promoLocation ?: com.example.promomap.util.LocationUtils.obterCoordenadasPorEndereco(context, localName)

                            // Envia todos os dados, agora incluindo a localização!
                            onSaveClick(localName, produto, marca, precoDouble, finalLocation)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
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
    // Preview para testar o layout no Android Studio sem rodar o app
    CadPromoPage(
        onBackClick = {},
        onImageClick = {},
        onSaveClick = { _, _, _, _, _ -> }
    )
}
