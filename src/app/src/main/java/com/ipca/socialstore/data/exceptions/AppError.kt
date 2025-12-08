package com.ipca.socialstore.data.exceptions

sealed class AppError {
    // 1. Business Errors (user fault)
    object InvalidPassword : AppError()
    object UserNotFound : AppError()

    object InvalidDate: AppError()
    object UserAlreadyExists : AppError()
    object InvalidEmail : AppError()
    object InvalidEmailDomain : AppError()
    object UserNotLoggedIn : AppError()
    object InvalidResetToken : AppError()
    object InvalidFilesNumber: AppError()

    // Generic Errors
    data class EmptyField(val fieldLabelResId: Int) : AppError()
    data class InvalidField(val fieldLabelResId: Int) : AppError()

    data class ErroCreatingTable(val fieldLabelResId: Int) : AppError()


    // 2. System Errors
    object NetworkError : AppError() // No internet
    object ParseError : AppError()   // Code doesn't match DB
    data class UnknownError(val message: String) : AppError()
}