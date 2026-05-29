package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CalculationEntity)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: Int)

    @Query("DELETE FROM calculations WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldCalculations(cutoffTimestamp: Long)

    @Query("DELETE FROM calculations")
    suspend fun deleteAllCalculations()
}
