package com.ipca.socialstore.data.resultwrappers

import com.ipca.socialstore.data.exceptions.AppError

sealed class ResultFlowWrapper<T>(val data: T? = null) {
    class Success<T>(data: T) : ResultFlowWrapper<T>(data)
    class Error<T>(val error: AppError, data: T? = null) : ResultFlowWrapper<T>(data)
    class Loading<T>(data: T? = null) : ResultFlowWrapper<T>(data)
}