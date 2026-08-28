package com.example.data.repository

import com.example.data.db.GameProfileDao
import com.example.data.db.entities.*
import com.example.data.models.DefaultDataPresets
import kotlinx.coroutines.flow.Flow

class KeymapperRepository(private val dao: GameProfileDao) {

  val allProfiles: Flow<List<GameProfileEntity>> = dao.getAllProfiles()
  val activeProfile: Flow<GameProfileEntity?> = dao.getActiveProfile()

  fun getProfileById(profileId: Long): Flow<GameProfileEntity?> = dao.getProfileById(profileId)

  fun getKeyMappings(profileId: Long): Flow<List<KeyMappingEntity>> = dao.getKeyMappingsForProfile(profileId)

  fun getRecoilConfigs(profileId: Long): Flow<List<RecoilConfigEntity>> = dao.getRecoilConfigsForProfile(profileId)

  fun getActiveRecoilConfig(profileId: Long): Flow<RecoilConfigEntity?> = dao.getActiveRecoilConfig(profileId)

  fun getMacros(profileId: Long): Flow<List<MacroComboEntity>> = dao.getMacrosForProfile(profileId)

  fun getSettings(profileId: Long): Flow<GamepadSettingsEntity?> = dao.getSettingsForProfile(profileId)

  suspend fun setActiveProfile(profileId: Long) = dao.setActiveProfile(profileId)

  suspend fun createProfile(name: String, category: String, iconType: String, description: String): Long {
    val newProfile = GameProfileEntity(
      name = name,
      packageName = "custom.game.${System.currentTimeMillis()}",
      gameCategory = category,
      iconType = iconType,
      isDefault = false,
      isActive = true,
      description = description,
      lastModified = System.currentTimeMillis()
    )
    val profileId = dao.insertProfile(newProfile)
    dao.setActiveProfile(profileId)
    
    // Seed default mappings, recoil and macro for this profile
    val mappings = DefaultDataPresets.getInitialKeyMappings(999L).map { it.copy(profileId = profileId) }
    dao.insertKeyMappings(mappings)

    val recoils = DefaultDataPresets.getInitialRecoilConfigs(999L).map { it.copy(profileId = profileId) }
    dao.insertRecoilConfigs(recoils)

    val macros = DefaultDataPresets.getInitialMacros(999L).map { it.copy(profileId = profileId) }
    dao.insertMacros(macros)

    val settings = DefaultDataPresets.getInitialGamepadSettings(profileId)
    dao.insertOrUpdateSettings(settings)

    return profileId
  }

  suspend fun updateProfile(profile: GameProfileEntity) = dao.updateProfile(profile)

  suspend fun deleteProfile(profileId: Long) = dao.deleteProfileById(profileId)

  suspend fun saveKeyMapping(keyMapping: KeyMappingEntity) = dao.insertKeyMapping(keyMapping)

  suspend fun updateKeyMapping(keyMapping: KeyMappingEntity) = dao.updateKeyMapping(keyMapping)

  suspend fun deleteKeyMapping(id: Long) = dao.deleteKeyMappingById(id)

  suspend fun saveRecoilConfig(recoilConfig: RecoilConfigEntity) = dao.insertRecoilConfig(recoilConfig)

  suspend fun updateRecoilConfig(recoilConfig: RecoilConfigEntity) = dao.updateRecoilConfig(recoilConfig)

  suspend fun saveMacro(macro: MacroComboEntity) = dao.insertMacro(macro)

  suspend fun updateMacro(macro: MacroComboEntity) = dao.updateMacro(macro)

  suspend fun deleteMacro(id: Long) = dao.deleteMacroById(id)

  suspend fun saveSettings(settings: GamepadSettingsEntity) = dao.insertOrUpdateSettings(settings)
}
