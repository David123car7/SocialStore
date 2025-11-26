package com.ipca.socialstore.data.resultwrappers

sealed class ResultFlowWrapper<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ResultFlowWrapper<T>(data)
    class Error<T>(message: String, data: T? = null) : ResultFlowWrapper<T>(data , message)
    class Loading<T>(data: T? = null) : ResultFlowWrapper<T>(data)
}