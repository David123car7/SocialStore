package com.ipca.socialstore.data.exceptions

sealed class AppError {
    // 1. Business Errors (user fault)
    object InvalidPassword : AppError()
    object DifferentPassword: AppError()
    object UserNotFound : AppError()
    object DataNotFound: AppError()
    object DataNotCreated: AppError()
    object DataNotUpdated: AppError()
    object InvalidDate: AppError()
    object UserAlreadyExists : AppError()
    object ApplicationAllreadyExists: AppError()
    object ApplicationDontExists: AppError()
    object InvalidEmail : AppError()
    object InvalidEmailDomain : AppError()
    object UserNotLoggedIn : AppError()
    object InvalidResetToken : AppError()
    object InvalidFilesNumber: AppError()

    // Generic Errors
    data class EmptyField(val fieldLabelResId: Int) : AppError()
    data class InvalidField(val fieldLabelResId: Int) : AppError()
    // 2. System Errors
    object NetworkError : AppError() // No internet
    object ParseError : AppError()   // Code doesn't match DB
    data class UnknownError(val message: String) : AppError()
}