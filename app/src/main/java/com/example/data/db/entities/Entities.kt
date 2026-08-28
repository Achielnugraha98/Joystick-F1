package com.example.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "game_profiles")
data class GameProfileEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val packageName: String,
  val gameCategory: String, // BATTLE_ROYALE, FPS_SHOOTER, etc.
  val iconType: String, // PUBG, CODM, FREEFIRE, GENSHIN, APEX, GENERIC
  val isDefault: Boolean = false,
  val isActive: Boolean = false,
  val description: String = "",
  val lastModified: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "key_mappings",
  foreignKeys = [
    ForeignKey(
      entity = GameProfileEntity::class,
      parentColumns = ["id"],
      childColumns = ["profileId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["profileId"])]
)
data class KeyMappingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val profileId: Long,
  val gamepadButton: String, // e.g. "RT / R2", "A", "DPAD_UP"
  val targetAction: String, // "SHOOT / FIRE", "AIM / ADS", "JUMP"
  val screenNormalizedX: Float, // 0.0f - 1.0f
  val screenNormalizedY: Float, // 0.0f - 1.0f
  val touchRadiusDp: Float = 28f,
  val mappingMode: String = "SINGLE_TAP", // SINGLE_TAP, TURBO_RAPID, HOLD_DOWN, ANALOG_JOYSTICK, CAMERA_AIM, MACRO_TRIGGER, RECOIL_SYNC
  val turboTapsPerSecond: Int = 15,
  val macroIdRef: Long? = null,
  val isEnabled: Boolean = true
)

@Entity(
  tableName = "recoil_configs",
  foreignKeys = [
    ForeignKey(
      entity = GameProfileEntity::class,
      parentColumns = ["id"],
      childColumns = ["profileId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["profileId"])]
)
data class RecoilConfigEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val profileId: Long,
  val weaponName: String, // e.g. "M416 (Laser Curve)"
  val isRecoilActive: Boolean = true,
  val verticalPullStrength: Float = 48f, // 0f..100f
  val horizontalDriftCompensation: Float = -6f, // -50f..+50f (Negative: pull left, Positive: pull right)
  val initialDelayMs: Int = 60,
  val stepIntervalMs: Int = 16,
  val burstStabilizerEnabled: Boolean = true,
  val scope1xMultiplier: Float = 1.0f,
  val scope2xMultiplier: Float = 1.25f,
  val scope3xMultiplier: Float = 1.6f,
  val scope4xMultiplier: Float = 2.1f,
  val scope6xMultiplier: Float = 2.8f,
  val scope8xMultiplier: Float = 3.6f,
  val selectedScope: String = "1X", // "1X", "2X", "3X", "4X", "6X", "8X"
  val curveType: String = "EXPONENTIAL_DECAY", // EXPONENTIAL_DECAY, SMOOTH_S_CURVE, LINEAR_CONSTANT, DYNAMIC_BURST
  val triggerButton: String = "RT / R2 (SHOOT)"
)

@Entity(
  tableName = "macro_combos",
  foreignKeys = [
    ForeignKey(
      entity = GameProfileEntity::class,
      parentColumns = ["id"],
      childColumns = ["profileId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["profileId"])]
)
data class MacroComboEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val profileId: Long,
  val name: String,
  val description: String = "",
  val triggerButton: String, // "Y", "LB / L1", "M1"
  val loopMode: String = "ONCE", // ONCE, REPEAT_WHILE_HELD, TOGGLE_LOOP
  val stepsJson: String, // JSON representation of List<MacroStep>
  val totalDurationMs: Long = 350,
  val isEnabled: Boolean = true
)

@Entity(
  tableName = "gamepad_settings",
  foreignKeys = [
    ForeignKey(
      entity = GameProfileEntity::class,
      parentColumns = ["id"],
      childColumns = ["profileId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["profileId"])]
)
data class GamepadSettingsEntity(
  @PrimaryKey val profileId: Long,
  val leftStickDeadzone: Float = 0.08f,
  val rightStickDeadzone: Float = 0.06f,
  val aimSensitivityX: Float = 1.2f,
  val aimSensitivityY: Float = 1.0f,
  val hapticVibration: Boolean = true,
  val turboGlobalRate: Int = 18
)
