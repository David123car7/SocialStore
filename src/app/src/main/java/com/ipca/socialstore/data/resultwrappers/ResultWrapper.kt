package com.ipca.socialstore.data.resultwrappers

import com.ipca.socialstore.data.exceptions.AppError

sealed class ResultWrapper<T>(val data: T? = null) {
    class Success<T>(data: T) : ResultWrapper<T>(data)
    class Error<T>(val error: AppError, data: T? = null) : ResultWrapper<T>(data)
}