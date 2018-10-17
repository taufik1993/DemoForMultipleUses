package com.iffcokisan.camerademo.interfaces

class UpdateNotification {
    var onChange : OnNotificationUpdateListener ?= null

    constructor(onNotificationUpdateListener: OnNotificationUpdateListener){
        onChange=onNotificationUpdateListener
    }

    fun updateText(value:Boolean){
        onChange?.onNotificationUpdate(value)
    }
}