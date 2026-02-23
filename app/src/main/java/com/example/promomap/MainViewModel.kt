package com.example.promomap

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.promomap.model.Promo
import com.example.promomap.model.User
import com.example.promomap.repo.PromoRepository
import com.example.promomap.ui.theme.nav.Route
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: PromoRepository) : ViewModel() {

    // --- NAVEGAÇÃO ---
    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    // --- DADOS (A Mágica Acontece Aqui) ---
    // Converte o Flow do Repositório em um Estado que a tela pode ler.
    // Assim que o app abrir, ele começa a escutar o Firebase automaticamente.
    val promos: StateFlow<List<Promo>> = repo.promos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val user: StateFlow<User?> = repo.getLoggedUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    // Promoção selecionada para o BottomSheet
    private var _selectedPromo = mutableStateOf<Promo?>(null)
    var selectedPromo: Promo?
        get() = _selectedPromo.value
        set(tmp) { _selectedPromo.value = tmp }

    // --- AÇÕES ---
    fun addPromo(promo: Promo) {
        viewModelScope.launch {
            repo.add(promo)
        }
    }

    fun removePromo(promo: Promo) {
        viewModelScope.launch {
            repo.remove(promo)
        }
    }
}

// Factory para criar o ViewModel com o Repositório injetado
class MainViewModelFactory(private val repo: PromoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}