package com.ipca.socialstore.data.helpers

import com.ipca.socialstore.data.enums.DatabaseTables
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder

fun SupabaseClient.from(table: DatabaseTables): PostgrestQueryBuilder {
    return this.from(table.tableName)
}