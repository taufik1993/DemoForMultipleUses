package com.iffcokisan.camerademo.interfaces

import android.content.Context

interface OnNotificationUpdateListener {
    val context: Context
    fun onNotificationUpdate(update: Boolean)
}