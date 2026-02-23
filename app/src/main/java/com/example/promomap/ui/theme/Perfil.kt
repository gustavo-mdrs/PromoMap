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
    onEditNameClick: () -> Unit // Callback para editar apenas o nome
) {
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
            // --- FOTO DE PERFIL ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        onClick = { /* Logica para adicionar foto */ }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF1B5E20)
                )
                // Ícone pequeno indicando que pode trocar a foto
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = Color(0xFF1B5E20)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        Modifier.padding(4.dp).size(16.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPOS DE DADOS ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Nome Completo - ÚNICO EDITÁVEL
                    ProfileDataRow(
                        label = "Nome Completo",
                        value = userName,
                        isEditable = true,
                        onEditClick = onEditNameClick
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // CPF - APENAS LEITURA
                    ProfileDataRow(label = "CPF", value = userCpf, isEditable = false)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // E-MAIL - APENAS LEITURA
                    ProfileDataRow(label = "E-mail", value = userEmail, isEditable = false)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEÇÃO DE HISTÓRICO ---
            Text(
                "Meu Histórico de Promoções",
                modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Card(
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma promoção cadastrada ainda.", color = Color.Gray, fontSize = 14.sp)
                }
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