package com.example.promomap

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.db.fb.toFBUser
import com.example.promomap.model.User
import com.example.promomap.ui.theme.PromoMapTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PromoMapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    // Estados baseados exatamente no protótipo do Slide 6
    var cpf by rememberSaveable { mutableStateOf("") }
    var nomeCompleto by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConfirm by rememberSaveable { mutableStateOf("") }

    val activity = LocalActivity.current as Activity

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Faça seu cadastro!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo CPF (Slide 6)
        OutlinedTextField(
            value = cpf,
            label = { Text(text = "Digite seu CPF") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { cpf = it }
        )

        Spacer(modifier = Modifier.size(10.dp))

        // Campo Nome Completo (Slide 6)
        OutlinedTextField(
            value = nomeCompleto,
            label = { Text(text = "Insira seu nome completo") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { nomeCompleto = it }
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "Insira seu Email") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Insira sua senha") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = passwordConfirm,
            label = { Text(text = "Confirme sua senha") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { passwordConfirm = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.size(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Integração com FBDatabase da Prática 06 para salvar dados extras (CPF e Nome)
                                FBDatabase().register(User(nomeCompleto, email, cpf).toFBUser())
                                Toast.makeText(activity, "Registro OK!", Toast.LENGTH_LONG).show()
                                activity.finish()
                            } else {
                                Toast.makeText(activity, "Registro FALHOU!", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                // Validação: todos os campos preenchidos e senhas iguais
                enabled = email.isNotEmpty() && nomeCompleto.isNotEmpty() &&
                        cpf.isNotEmpty() && password.isNotEmpty() &&
                        password == passwordConfirm
            ) {
                Text("Registrar")
            }

            Button(
                onClick = {
                    email = ""; password = ""; nomeCompleto = "";
                    passwordConfirm = ""; cpf = ""
                }
            ) {
                Text("Limpar")
            }
        }

        // Link para voltar ao Login (Slide 6)
        TextButton(
            onClick = { activity.finish() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Já possui um cadastro? Clique aqui")
        }
    }
}