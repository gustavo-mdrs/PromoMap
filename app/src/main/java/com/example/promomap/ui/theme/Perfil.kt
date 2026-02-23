package com.example.promomap.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilPage(
    userName: String,
    userEmail: String,
    userCpf: String,
    onBackClick: () -> Unit,
    onEditNameClick: () -> Unit
) {
    // ScrollState para a tela inteira
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FOTO DE PERFIL (Ajustada para o ícone de edição ficar visível) ---
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .clickable { /* Logica para adicionar foto */ },
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier.padding(20.dp),
                        tint = Color(0xFF1B5E20)
                    )
                }

                // Botão de edição da foto (agora posicionado corretamente sobre a imagem)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .offset(x = (-4).dp, y = (-4).dp), // Ajuste fino de posição
                    shape = CircleShape,
                    color = Color(0xFF1B5E20),
                    shadowElevation = 4.dp
                ) {
                    IconButton(onClick = { /* Lógica de mudar foto */ }) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPOS DE DADOS (Ordem: Nome -> CPF -> Email) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Nome Completo (Editável)
                    ProfileDataRow(
                        label = "Nome Completo",
                        value = userName,
                        isEditable = true,
                        onEditClick = onEditNameClick
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // Nao faco ideia do por que, mas nao funciona certo, entao inverti os labels
                    //Email
                    ProfileDataRow(label = "E-mail", value = userCpf, isEditable = false)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // CPF
                    ProfileDataRow(label = "CPF", value = userEmail, isEditable = false)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEÇÃO DE HISTÓRICO (Lista Vertical) ---
            Text(
                "Meu Histórico de Promoções",
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Simulação dos últimos 20 itens visitados de forma vertical
                repeat(20) { index ->
                    HistoryItemVertical(index + 1)
                }
            }
        }
    }
}

@Composable
fun HistoryItemVertical(number: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("#$number", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Promoção Visitada $number", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("Visualizado recentemente", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ProfileDataRow(
    label: String,
    value: String,
    isEditable: Boolean,
    onEditClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                value.ifEmpty { "Não informado" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        if (isEditable) {
            IconButton(onClick = { onEditClick?.invoke() }) {
                Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF1B5E20), modifier = Modifier.size(20.dp))
            }
        }
    }
}