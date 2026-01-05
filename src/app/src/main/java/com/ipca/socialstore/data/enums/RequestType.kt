package com.ipca.socialstore.data.enums

enum class RequestType(val code: String, val label: String) {
    FOOD("food","Alimentar"),
    HYGIENE("hygiene","Produtos de Higiene"),
    CLEANING("clean","Limpeza");

    companion object {
        // Função auxiliar para encontrar o Enum pelo código
        fun getByCode(code: String): RequestType? = entries.find { it.code == code }
    }
}