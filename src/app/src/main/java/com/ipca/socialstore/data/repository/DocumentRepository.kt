package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DocumentRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){
    
}