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
import com.google.android.gms.maps.model.LatLng
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

    val favorites: StateFlow<List<String>> = userRepo.getFavorites()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    fun toggleNotification(key: String, enabled: Boolean) {
        viewModelScope.launch { userRepo.toggleNotification(key, enabled) }
    }

    fun addFavoriteProduct(product: String) {
        viewModelScope.launch { userRepo.addFavorite(product) }
    }

    fun saveNewLocation(name: String, addr: String, rad: String, lat: Double, lng: Double) {
        viewModelScope.launch { userRepo.saveLocation(name, addr, rad, lat, lng) }
    }

    fun getAlerts(promos: List<Promo>, userLocation: LatLng): List<Promo> {
        val favs = favorites.value
        return promos.filter { promo ->
            // O produto está nos favoritos?
            val isFavorite = favs.any { it.equals(promo.item, ignoreCase = true) }

            // A promoção está a menos de 5km? (Cálculo simples de distância)
            val distance = calculateDistance(userLocation, promo.localizacao ?: userLocation)

            isFavorite && distance <= 5.0
        }
    }

    // Cálculo simples de distância em KM
    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude, results
        )
        return results[0] / 1000.0 // converte metros para km
    }

    fun removeFavoriteProduct(product: String) {
        viewModelScope.launch { userRepo.removeFavorite(product) }
    }

    val savedLocations: StateFlow<List<Map<String, String>>> = userRepo.getSavedLocations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun removeSavedLocation(locMap: Map<String, String>) {
        viewModelScope.launch { userRepo.removeLocation(locMap) }
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