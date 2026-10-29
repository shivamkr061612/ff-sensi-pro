package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM sensitivity_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<SensitivityProfileEntity>>

    @Query("SELECT * FROM sensitivity_profiles WHERE game = :game ORDER BY createdAt DESC")
    fun getProfilesByGame(game: String): Flow<List<SensitivityProfileEntity>>

    @Query("SELECT * FROM sensitivity_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): SensitivityProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SensitivityProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SensitivityProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: SensitivityProfileEntity)

    @Query("DELETE FROM sensitivity_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)

    @Query("DELETE FROM sensitivity_profiles")
    suspend fun deleteAllProfiles()
}
