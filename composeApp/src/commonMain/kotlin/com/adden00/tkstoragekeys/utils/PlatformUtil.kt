package com.adden00.tkstoragekeys.utils

expect fun getPlatform(): Platform

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    WEB
}