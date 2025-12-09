package com.ipca.socialstore.data.resultwrappers

import com.ipca.socialstore.data.exceptions.AppError

sealed class ResultWrapper<T>() {
    class Success<T>(val data: T) : ResultWrapper<T>()
    class Error<T>(val error: AppError) : ResultWrapper<T>()
}