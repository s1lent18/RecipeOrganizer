package com.example.recipeorganizer.models.dataprovider

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)