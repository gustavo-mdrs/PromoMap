package com.example.promomap

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.db.fb.FBPromo
import com.example.promomap.db.fb.FBUser
import com.example.promomap.db.fb.toFBPromo
import com.example.promomap.model.Promo
import com.example.promomap.model.User

// Removido WeatherService pois o foco é Promoção, não Clima
class MainViewModel(private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {

    // Lista de promoções observável pela UI (Mapa e Lista)
    private val _promos = mutableStateMapOf<String, Promo>()
    val promos: List<Promo>
        get() = _promos.values.toList().sortedBy { it.item }

    // Usuário logado (Nome, Email, CPF) - Slide 11
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    // Promoção selecionada no momento (para ver detalhes no Slide 10)
    private var _selectedPromo = mutableStateOf<Promo?>(null)
    var selectedPromo: Promo?
        get() = _selectedPromo.value
        set(tmp) { _selectedPromo.value = tmp }

    init {
        db.setListener(this)
    }

    // Ações de gerenciamento de promoções (Slide 8 e 10)
    fun addPromo(promo: Promo) {
        db.addPromo(promo.toFBPromo())
    }

    fun removePromo(promo: Promo) {
        db.removePromo(promo.toFBPromo())
    }

    // Callbacks do FBDatabase.Listener (Sincronização em tempo real)
    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser() // Agora inclui o CPF conforme seu FBUser
    }

    override fun onUserSignOut() {
        _user.value = null
        _promos.clear()
    }

    override fun onPromoAdded(promo: FBPromo) {
        _promos[promo.id ?: promo.item!!] = promo.toPromo()
    }

    override fun onPromoUpdated(promo: FBPromo) {
        _promos[promo.id ?: promo.item!!] = promo.toPromo()
    }

    override fun onPromoRemoved(promo: FBPromo) {
        _promos.remove(promo.id ?: promo.item!!)
    }
}

class MainViewModelFactory(private val db: FBDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}