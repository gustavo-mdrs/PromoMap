package com.example.promomap.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.promomap.MainViewModel
import com.example.promomap.model.Promo
import com.example.promomap.ui.theme.nav.PromoDetailsSheet
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(
    viewModel: MainViewModel
) {
    // Configurações iniciais do Mapa
    val recife = LatLng(-8.05, -34.90)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recife, 12f)
    }

    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Estado para controlar qual promoção foi clicada
    var selectedPromo by remember { mutableStateOf<Promo?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            // --- 1. O MAPA ---
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false
                ),
                onMapClick = { selectedPromo = null } // Clicar fora fecha o detalhe
            ) {
                // Desenha os pinos baseados na lista do ViewModel
                viewModel.promos.forEach { promo ->
                    if (promo.localizacao != null) {
                        Marker(
                            state = MarkerState(position = promo.localizacao),
                            title = promo.item,
                            snippet = "R$ ${String.format("%.2f", promo.preco)}",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                            onClick = {
                                selectedPromo = promo // Abre o BottomSheet
                                false
                            }
                        )
                    }
                }
            }

            // --- 2. O BOTTOM SHEET (Integração) ---
            if (selectedPromo != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedPromo = null },
                    sheetState = sheetState
                ) {
                    // AQUI CHAMAMOS O ARQUIVO QUE CRIAMOS ACIMA
                    PromoDetailsSheet(
                        itemTitle = selectedPromo!!.item,
                        price = selectedPromo!!.preco,
                        marca = selectedPromo!!.marca,
                        local = "Local da Promoção",
                        imageUrl = null, // Passe selectedPromo!!.imageUrl se tiver
                        onReportClick = {
                            viewModel.removePromo(selectedPromo!!)
                            selectedPromo = null
                        }
                    )
                }
            }
        }
    }
}