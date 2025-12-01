package com.ipca.socialstore.data.helpers

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper

suspend fun <T> safeFlowCall(mapper: ExceptionMapper, apiCall: suspend () -> T): ResultFlowWrapper<T> {
    return try {
        val result = apiCall()
        ResultFlowWrapper.Success(result)
    } catch (e: Exception) {
        val appError = mapper.map(e)

        // 3. Return safe error to ViewModel
        ResultFlowWrapper.Error(appError)
    }
}

suspend fun <T> safeCall(mapper: ExceptionMapper, apiCall: suspend () -> T): ResultWrapper<T> {
    return try {
        val result = apiCall()
        ResultWrapper.Success(result)
    } catch (e: Exception) {
        val appError = mapper.map(e)

        // 3. Return safe error to ViewModel
        ResultWrapper.Error(appError)
    }
}