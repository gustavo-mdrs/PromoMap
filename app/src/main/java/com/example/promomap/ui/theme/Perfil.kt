package com.example.promomap.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilPage(
    // Dados recebidos (Vêm do ViewModel)
    userName: String,
    userEmail: String,
    userCpf: String,

    // Ações (Callbacks)
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Meu Perfil", color = Color.White, fontWeight = FontWeight.Bold)
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
                    containerColor = Color(0xFF1B5E20) // Verde PromoMap
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- AVATAR DO USUÁRIO ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)), // Verde bem claro
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName.ifEmpty { "Usuário" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Text(
                text = "Membro do PromoMap",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPOS DE INFORMAÇÃO (Apenas Leitura) ---

            ProfileInfoField(label = "Nome Completo", value = userName)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoField(label = "E-mail", value = userEmail)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoField(label = "CPF", value = userCpf)

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÕES DE AÇÃO ---

            // Botão Editar
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Dados")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Sair (Logout)
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta")
            }
        }
    }
}

// --- Componente Auxiliar (Resolve o erro ProfileInfoField) ---
@Composable
fun ProfileInfoField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {}, // Vazio pois é apenas leitura
        label = { Text(label) },
        readOnly = true, // Impede o teclado de abrir
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1B5E20),
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Preview
@Composable
fun PerfilPagePreview() {
    PerfilPage(
        userName = "Gustavo",
        userEmail = "gustavo@email.com",
        userCpf = "123.456.789-00",
        onBackClick = {},
        onEditClick = {},
        onLogoutClick = {}
    )
}