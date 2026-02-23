package com.example.promomap.repo

import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.model.User
import com.example.promomap.model.Promo
import kotlinx.coroutines.flow.Flow

class UserRepository(private val fbDatabase: FBDatabase) {

    // Retorna o fluxo de dados do usuário logado (Nome, CPF, Email) [cite: 574, 576]
    fun getLoggedUser(): Flow<User?> = fbDatabase.getLoggedUser()

    // Atualiza apenas o nome no Firestore [cite: 385]
    suspend fun updateUserName(newName: String) {
        fbDatabase.updateUserName(newName)
    }

    // Retorna o fluxo das últimas 20 promoções visitadas [cite: 382]
    fun getVisitHistory(): Flow<List<Promo>> = fbDatabase.getVisitHistory()

    // Adiciona uma promoção ao histórico de visitas do usuário
    suspend fun addToHistory(promo: Promo) {
        fbDatabase.addToHistory(promo)
    }
}