package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.db.entities.*
import com.example.data.models.DefaultDataPresets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    GameProfileEntity::class,
    KeyMappingEntity::class,
    RecoilConfigEntity::class,
    MacroComboEntity::class,
    GamepadSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun gameProfileDao(): GameProfileDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "gamepad_keymapper_pro.db"
        )
        .addCallback(object : Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
              scope.launch(Dispatchers.IO) {
                populateInitialData(database.gameProfileDao())
              }
            }
          }
        })
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    private suspend fun populateInitialData(dao: GameProfileDao) {
      val profiles = DefaultDataPresets.getInitialProfiles()
      for (profile in profiles) {
        val profileId = dao.insertProfile(profile)
        val mappings = DefaultDataPresets.getInitialKeyMappings(profile.id).map { it.copy(profileId = profileId) }
        dao.insertKeyMappings(mappings)

        val recoils = DefaultDataPresets.getInitialRecoilConfigs(profile.id).map { it.copy(profileId = profileId) }
        dao.insertRecoilConfigs(recoils)

        val macros = DefaultDataPresets.getInitialMacros(profile.id).map { it.copy(profileId = profileId) }
        dao.insertMacros(macros)

        val settings = DefaultDataPresets.getInitialGamepadSettings(profile.id).copy(profileId = profileId)
        dao.insertOrUpdateSettings(settings)
      }
    }
  }
}
