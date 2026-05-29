package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "WEIGHT_TO_PRICE" or "MONEY_TO_WEIGHT"
    val pricePerKg: Double,
    val inputWeightGrams: Double,
    val outputPrice: Double,
    val inputAmount: Double,
    val outputWeightGrams: Double
)
