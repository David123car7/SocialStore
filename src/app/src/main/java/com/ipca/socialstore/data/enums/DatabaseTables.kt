package com.ipca.socialstore.data.enums

enum class DatabaseTables(val tableName: String) {
    PROFILE("profile"),
    ADDRESS("address");

    override fun toString() = tableName
}