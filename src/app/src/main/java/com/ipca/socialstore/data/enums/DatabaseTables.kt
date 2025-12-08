package com.ipca.socialstore.data.enums

enum class DatabaseTables(val tableName: String) {
    PROFILE("profile"),
    ADDRESS("address"),
    CAMPAIGN("campaign"),
    DONATION("donation"),
    ITEM("item"),
    STOCK("stock"),
    DONATION_ITEM("donation_Item");

    override fun toString() = tableName
}