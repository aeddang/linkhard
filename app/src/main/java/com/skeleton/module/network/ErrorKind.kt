package com.skeleton.module.network

enum class ErrorKind {
    NETWORK,
    HTTP,
    UNEXPECTED,
    UNKNOWNHOST
}

enum class ErrorActionType {
    Retry,
    Finish,
    Confirm
}