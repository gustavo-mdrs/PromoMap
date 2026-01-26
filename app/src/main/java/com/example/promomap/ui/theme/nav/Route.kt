package com.example.promomap.ui.theme.nav

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data object Map : Route
    @Serializable data object Config : Route
    @Serializable data object Perfil : Route
    @Serializable data object CadPromo : Route
}