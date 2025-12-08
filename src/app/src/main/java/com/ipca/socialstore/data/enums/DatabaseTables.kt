package com.ipca.socialstore.data.enums

enum class DatabaseTables(val tableName: String) {
    USER("user"),
    PROFILE("profile"),
    ADDRESS("address"),
    CAMPAIGN("campaign"),
    DONATION("donation"),
    DONATION_ITEM("donation_Item"),

    ITEM("item"),
    STOCK("stock"),

    APPLICATION("application"),
    APPLICATION_STATE("application_state"),
    ACADEMIC("academic");

    override fun toString() = tableName
}