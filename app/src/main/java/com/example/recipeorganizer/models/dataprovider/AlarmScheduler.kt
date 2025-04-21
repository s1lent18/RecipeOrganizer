package com.example.recipeorganizer.models.dataprovider

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}