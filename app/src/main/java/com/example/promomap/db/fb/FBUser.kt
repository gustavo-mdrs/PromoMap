package com.example.promomap.db.fb

import com.example.promomap.model.User

// Classe usada para serializar os dados no Firebase Firestore
class FBUser {
    var name: String? = null
    var email: String? = null
    var cpf: String? = null // Campo CPF adicionado para o PromoMap

    // Converte o objeto do Firebase para o modelo interno User
    fun toUser() = User(name!!, email!!, cpf!!)
}

// Função de extensão para transformar o modelo da UI em objeto para o Firebase
fun User.toFBUser(): FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    fbUser.cpf = this.cpf
    return fbUser
}