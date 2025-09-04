package com.example.iasminasteriskari.ari.actions

data class AriAction(
    val action: ActionEnum,
    val actionId: String? = null,
    val args: List<String> = emptyList()
) {
}
