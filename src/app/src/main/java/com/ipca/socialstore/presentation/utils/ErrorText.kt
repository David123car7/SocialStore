package com.ipca.socialstore.presentation.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.R

sealed class ErrorText {
    data class DynamicString(val value: String) : ErrorText()

    class StringResource(@StringRes val resId: Int, vararg val args: Any) : ErrorText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> {
                val resolvedArgs = args.map { arg ->
                    if (arg is ErrorText) {
                        arg.asString()
                    } else {
                        arg
                    }
                }.toTypedArray()
                stringResource(resId, *resolvedArgs)
            }
        }
    }
}

fun AppError.asUiText(): ErrorText {
    return when (this) {
        is AppError.EmptyField -> ErrorText.StringResource(R.string.error_field_empty, ErrorText.StringResource(this.fieldLabelResId))
        is AppError.InvalidField -> ErrorText.StringResource(R.string.error_field_invalid, ErrorText.StringResource(this.fieldLabelResId))
        is AppError.ErroCreatingTable -> ErrorText.StringResource(R.string.error_creating_table, ErrorText.StringResource(this.fieldLabelResId))

        is AppError.NetworkError -> ErrorText.StringResource(R.string.error_network)
        is AppError.UserNotFound -> ErrorText.StringResource(R.string.error_user_not_found)
        is AppError.InvalidDate -> ErrorText.StringResource(R.string.error_invalid_date)
        is AppError.InvalidPassword -> ErrorText.StringResource(R.string.error_invalid_password, 6)
        is AppError.InvalidEmail -> ErrorText.StringResource(R.string.error_invalid_email)
        is AppError.ParseError -> ErrorText.StringResource(R.string.error_technical)
        is AppError.InvalidEmailDomain -> ErrorText.StringResource(R.string.error_invalid_email_domain)
        is AppError.UserAlreadyExists -> ErrorText.StringResource(R.string.error_user_already_exists)
        is AppError.UserNotLoggedIn -> ErrorText.StringResource(R.string.error_user_not_logged_in)
        is AppError.InvalidResetToken -> ErrorText.StringResource(R.string.error_invalid_reset_token)
        is AppError.UnknownError -> ErrorText.DynamicString(this.message) // Fallback
        is AppError.InvalidFilesNumber -> ErrorText.StringResource(R.string.error_invalid_files_number)
    }
}