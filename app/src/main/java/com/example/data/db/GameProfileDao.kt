package com.example.data.db

import androidx.room.*
import com.example.data.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProfileDao {

  // --- Profiles ---
  @Query("SELECT * FROM game_profiles ORDER BY isActive DESC, lastModified DESC")
  fun getAllProfiles(): Flow<List<GameProfileEntity>>

  @Query("SELECT * FROM game_profiles WHERE id = :profileId LIMIT 1")
  fun getProfileById(profileId: Long): Flow<GameProfileEntity?>

  @Query("SELECT * FROM game_profiles WHERE isActive = 1 LIMIT 1")
  fun getActiveProfile(): Flow<GameProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProfile(profile: GameProfileEntity): Long

  @Update
  suspend fun updateProfile(profile: GameProfileEntity)

  @Query("UPDATE game_profiles SET isActive = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
  suspend fun setActiveProfile(profileId: Long)

  @Delete
  suspend fun deleteProfile(profile: GameProfileEntity)

  @Query("DELETE FROM game_profiles WHERE id = :profileId")
  suspend fun deleteProfileById(profileId: Long)

  // --- Key Mappings ---
  @Query("SELECT * FROM key_mappings WHERE profileId = :profileId ORDER BY id ASC")
  fun getKeyMappingsForProfile(profileId: Long): Flow<List<KeyMappingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKeyMapping(keyMapping: KeyMappingEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKeyMappings(keyMappings: List<KeyMappingEntity>)

  @Update
  suspend fun updateKeyMapping(keyMapping: KeyMappingEntity)

  @Delete
  suspend fun deleteKeyMapping(keyMapping: KeyMappingEntity)

  @Query("DELETE FROM key_mappings WHERE id = :id")
  suspend fun deleteKeyMappingById(id: Long)

  // --- Recoil Configs ---
  @Query("SELECT * FROM recoil_configs WHERE profileId = :profileId ORDER BY id ASC")
  fun getRecoilConfigsForProfile(profileId: Long): Flow<List<RecoilConfigEntity>>

  @Query("SELECT * FROM recoil_configs WHERE profileId = :profileId AND isRecoilActive = 1 LIMIT 1")
  fun getActiveRecoilConfig(profileId: Long): Flow<RecoilConfigEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecoilConfig(recoilConfig: RecoilConfigEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecoilConfigs(recoilConfigs: List<RecoilConfigEntity>)

  @Update
  suspend fun updateRecoilConfig(recoilConfig: RecoilConfigEntity)

  @Delete
  suspend fun deleteRecoilConfig(recoilConfig: RecoilConfigEntity)

  // --- Macro Combos ---
  @Query("SELECT * FROM macro_combos WHERE profileId = :profileId ORDER BY id ASC")
  fun getMacrosForProfile(profileId: Long): Flow<List<MacroComboEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMacro(macro: MacroComboEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMacros(macros: List<MacroComboEntity>)

  @Update
  suspend fun updateMacro(macro: MacroComboEntity)

  @Delete
  suspend fun deleteMacro(macro: MacroComboEntity)

  @Query("DELETE FROM macro_combos WHERE id = :id")
  suspend fun deleteMacroById(id: Long)

  // --- Gamepad Settings ---
  @Query("SELECT * FROM gamepad_settings WHERE profileId = :profileId LIMIT 1")
  fun getSettingsForProfile(profileId: Long): Flow<GamepadSettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSettings(settings: GamepadSettingsEntity)
}
