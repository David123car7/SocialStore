package com.ipca.socialstore.data.enums

enum class DatabaseTables(val tableName: String) {
    PROFILE("profile"),
    ADDRESS("address"),
    CAMPAIGN("campaign"),
    DONATION("donation"),
    ITEM("item"),
    STOCK("stock");

    override fun toString() = tableName
}