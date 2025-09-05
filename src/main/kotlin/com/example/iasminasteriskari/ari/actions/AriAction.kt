package com.example.iasminasteriskari.ari.actions

data class AriAction(
    val type: ActionEnum,
    val args: List<String> = emptyList(),
    val actionId: String? = null
) {
}
