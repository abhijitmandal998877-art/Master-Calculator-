package com.example.data.repository

import com.example.data.database.CalculationDao
import com.example.data.database.CalculationEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val calculationDao: CalculationDao) {
    val allCalculations: Flow<List<CalculationEntity>> = calculationDao.getAllCalculations()

    suspend fun insert(calculation: CalculationEntity) {
        calculationDao.insertCalculation(calculation)
    }

    suspend fun deleteById(id: Int) {
        calculationDao.deleteCalculationById(id)
    }

    suspend fun deleteOld(cutoffTimestamp: Long) {
        calculationDao.deleteOldCalculations(cutoffTimestamp)
    }

    suspend fun clearAll() {
        calculationDao.deleteAllCalculations()
    }
}
