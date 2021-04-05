package com.example.fancycars.data.network

open class Result<T> {
    class Success<T>(val result: T) : Result<T>()
    class Failure<T>(val throwable: Throwable = CommonException) : Result<T>()
}

// ERRORS
object NoInternetException: Throwable()
object CommonException: Throwable()