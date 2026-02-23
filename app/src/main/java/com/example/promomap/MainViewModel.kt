package com.example.promomap

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.promomap.model.Promo
import com.example.promomap.model.User
import com.example.promomap.repo.PromoRepository
import com.example.promomap.repo.UserRepository
import com.example.promomap.ui.theme.nav.Route
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val promoRepo: PromoRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    // --- NAVEGAÇÃO ---
    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    // --- DADOS REATIVOS ---
    val promos: StateFlow<List<Promo>> = promoRepo.promos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val user: StateFlow<User?> = userRepo.getLoggedUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val visitHistory: StateFlow<List<Promo>> = userRepo.getVisitHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Promoção selecionada
    private var _selectedPromo = mutableStateOf<Promo?>(null)
    var selectedPromo: Promo?
        get() = _selectedPromo.value
        set(tmp) { _selectedPromo.value = tmp }

    // --- AÇÕES ---
    fun addPromo(promo: Promo) {
        viewModelScope.launch { promoRepo.add(promo) }
    }

    fun removePromo(promo: Promo) {
        viewModelScope.launch { promoRepo.remove(promo) }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch { userRepo.updateUserName(newName) }
    }

    fun markAsVisited(promo: Promo) {
        viewModelScope.launch { userRepo.addToHistory(promo) }
    }
}

// Factory atualizada para injetar os dois repositórios
class MainViewModelFactory(
    private val promoRepo: PromoRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(promoRepo, userRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}