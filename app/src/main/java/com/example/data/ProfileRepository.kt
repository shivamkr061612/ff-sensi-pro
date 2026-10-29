package com.example.data

import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: ProfileDao) {

    fun getAllProfiles(): Flow<List<SensitivityProfileEntity>> = dao.getAllProfiles()

    fun getProfilesByGame(game: String): Flow<List<SensitivityProfileEntity>> = dao.getProfilesByGame(game)

    suspend fun getProfileById(id: Long): SensitivityProfileEntity? = dao.getProfileById(id)

    suspend fun saveProfile(profile: SensitivityProfileEntity): Long = dao.insertProfile(profile)

    suspend fun updateProfile(profile: SensitivityProfileEntity) = dao.updateProfile(profile)

    suspend fun deleteProfile(profile: SensitivityProfileEntity) = dao.deleteProfile(profile)

    suspend fun deleteProfileById(id: Long) = dao.deleteProfileById(id)

    suspend fun clearAll() = dao.deleteAllProfiles()
}
