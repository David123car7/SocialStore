package com.ipca.socialstore.data.email

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.EmailModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import io.github.jan.supabase.functions.functions
import io.ktor.client.request.setBody


class ResendEmailService @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun sendEmail(to: String, subject: String, messageBody: String): ResultWrapper<Unit> {
        return try {
            val requestPayload = EmailModel(
                to = to,
                subject = subject,
                html = messageBody
            )

            supabase.functions.invoke("send-email") {
                setBody(requestPayload)
            }

            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            ResultWrapper.Error(AppError.UnknownError("Erro ao enviar email: ${e.message}"))
        }
    }
}