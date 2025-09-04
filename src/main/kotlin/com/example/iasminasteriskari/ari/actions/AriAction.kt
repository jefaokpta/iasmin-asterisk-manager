package com.example.iasminasteriskari.ari.actions

data class AriAction(
    val type: ActionEnum,
    val actionId: String? = null,
    val args: List<String> = emptyList()
) {
}
