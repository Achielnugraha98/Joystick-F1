package com.example.data.models

import com.example.data.db.entities.*

object DefaultDataPresets {

  fun getInitialProfiles(): List<GameProfileEntity> {
    return listOf(
      GameProfileEntity(
        id = 1L,
        name = "PUBG Mobile - Pro Esports Layout",
        packageName = "com.tencent.ig",
        gameCategory = GameCategory.BATTLE_ROYALE.name,
        iconType = "PUBG",
        isDefault = true,
        isActive = true,
        description = "Optimized 6-finger gamepad layout with M416 Laser Less-Recoil & Drop-Shot Macro.",
        lastModified = System.currentTimeMillis()
      ),
      GameProfileEntity(
        id = 2L,
        name = "Call of Duty: Mobile - Claw & Sniper",
        packageName = "com.activision.callofduty.shooter",
        gameCategory = GameCategory.FPS_SHOOTER.name,
        iconType = "CODM",
        isDefault = false,
        isActive = false,
        description = "Precision Quick-Scope 1-Tap macro with slide-cancel and smooth stick deadzones.",
        lastModified = System.currentTimeMillis() - 3600000
      ),
      GameProfileEntity(
        id = 3L,
        name = "Free Fire MAX - Less Recoil & Headshot",
        packageName = "com.dts.freefiremax",
        gameCategory = GameCategory.BATTLE_ROYALE.name,
        iconType = "FREEFIRE",
        isDefault = false,
        isActive = false,
        description = "Auto drag-shot recoil compensation curve and 1-tap fast gloo wall macro.",
        lastModified = System.currentTimeMillis() - 7200000
      ),
      GameProfileEntity(
        id = 4L,
        name = "Apex Legends - Tactical Movement",
        packageName = "com.ea.gp.apexlegendsmobilefps",
        gameCategory = GameCategory.FPS_SHOOTER.name,
        iconType = "APEX",
        isDefault = false,
        isActive = false,
        description = "Super glide bunny hop macro with R-301 / Flatline horizontal stabilizer.",
        lastModified = System.currentTimeMillis() - 10800000
      ),
      GameProfileEntity(
        id = 5L,
        name = "Genshin Impact - Combo & Turbo",
        packageName = "com.miHoYo.GenshinImpact",
        gameCategory = GameCategory.MOBA_RPG.name,
        iconType = "GENSHIN",
        isDefault = false,
        isActive = false,
        description = "Fast Dash Attack Cancel combo, continuous turbo attack, quick elemental swap.",
        lastModified = System.currentTimeMillis() - 14400000
      )
    )
  }

  fun getInitialKeyMappings(profileId: Long): List<KeyMappingEntity> {
    return when (profileId) {
      1L -> listOf(
        KeyMappingEntity(profileId = 1L, gamepadButton = "RT / R2", targetAction = "SHOOT / FIRE", screenNormalizedX = 0.88f, screenNormalizedY = 0.68f, mappingMode = "RECOIL_SYNC", touchRadiusDp = 30f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "LT / L2", targetAction = "AIM / ADS", screenNormalizedX = 0.85f, screenNormalizedY = 0.42f, mappingMode = "HOLD_DOWN", touchRadiusDp = 28f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "A", targetAction = "JUMP", screenNormalizedX = 0.94f, screenNormalizedY = 0.72f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "B", targetAction = "CROUCH / SLIDE", screenNormalizedX = 0.90f, screenNormalizedY = 0.84f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "X", targetAction = "RELOAD", screenNormalizedX = 0.78f, screenNormalizedY = 0.86f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "Y", targetAction = "PRONE", screenNormalizedX = 0.95f, screenNormalizedY = 0.88f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "LB / L1", targetAction = "DROP-SHOT MACRO", screenNormalizedX = 0.88f, screenNormalizedY = 0.68f, mappingMode = "MACRO_TRIGGER", macroIdRef = 1L, touchRadiusDp = 28f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "RB / R1", targetAction = "GRENADE / THROW", screenNormalizedX = 0.74f, screenNormalizedY = 0.70f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "LEFT_STICK", targetAction = "VIRTUAL WASD (MOVE)", screenNormalizedX = 0.18f, screenNormalizedY = 0.72f, mappingMode = "ANALOG_JOYSTICK", touchRadiusDp = 54f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "RIGHT_STICK", targetAction = "CAMERA AIM (LOOK)", screenNormalizedX = 0.72f, screenNormalizedY = 0.50f, mappingMode = "CAMERA_AIM", touchRadiusDp = 70f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "DPAD_UP", targetAction = "MEDKIT / HEAL", screenNormalizedX = 0.28f, screenNormalizedY = 0.88f, mappingMode = "SINGLE_TAP", touchRadiusDp = 24f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "DPAD_DOWN", targetAction = "WEAPON 1", screenNormalizedX = 0.56f, screenNormalizedY = 0.88f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "DPAD_RIGHT", targetAction = "WEAPON 2", screenNormalizedX = 0.68f, screenNormalizedY = 0.88f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "DPAD_LEFT", targetAction = "PISTOL / MELEE", screenNormalizedX = 0.62f, screenNormalizedY = 0.76f, mappingMode = "SINGLE_TAP", touchRadiusDp = 24f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "L3 / LS", targetAction = "AUTO SPRINT", screenNormalizedX = 0.18f, screenNormalizedY = 0.48f, mappingMode = "SINGLE_TAP", touchRadiusDp = 24f),
        KeyMappingEntity(profileId = 1L, gamepadButton = "R3 / RS", targetAction = "PEEK / LEAN", screenNormalizedX = 0.76f, screenNormalizedY = 0.38f, mappingMode = "SINGLE_TAP", touchRadiusDp = 24f)
      )
      2L -> listOf(
        KeyMappingEntity(profileId = 2L, gamepadButton = "RT / R2", targetAction = "SHOOT / FIRE", screenNormalizedX = 0.86f, screenNormalizedY = 0.66f, mappingMode = "RECOIL_SYNC", touchRadiusDp = 30f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "LT / L2", targetAction = "ADS AIM", screenNormalizedX = 0.82f, screenNormalizedY = 0.46f, mappingMode = "HOLD_DOWN", touchRadiusDp = 28f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "LB / L1", targetAction = "QUICK SCOPE MACRO", screenNormalizedX = 0.86f, screenNormalizedY = 0.66f, mappingMode = "MACRO_TRIGGER", macroIdRef = 2L, touchRadiusDp = 28f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "A", targetAction = "JUMP", screenNormalizedX = 0.93f, screenNormalizedY = 0.68f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "B", targetAction = "SLIDE / CROUCH", screenNormalizedX = 0.90f, screenNormalizedY = 0.82f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "X", targetAction = "RELOAD", screenNormalizedX = 0.76f, screenNormalizedY = 0.82f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "LEFT_STICK", targetAction = "MOVE WASD", screenNormalizedX = 0.18f, screenNormalizedY = 0.70f, mappingMode = "ANALOG_JOYSTICK", touchRadiusDp = 50f),
        KeyMappingEntity(profileId = 2L, gamepadButton = "RIGHT_STICK", targetAction = "CAMERA AIM", screenNormalizedX = 0.70f, screenNormalizedY = 0.50f, mappingMode = "CAMERA_AIM", touchRadiusDp = 68f)
      )
      else -> listOf(
        KeyMappingEntity(profileId = profileId, gamepadButton = "RT / R2", targetAction = "PRIMARY ATTACK / FIRE", screenNormalizedX = 0.86f, screenNormalizedY = 0.70f, mappingMode = "RECOIL_SYNC", touchRadiusDp = 30f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "LT / L2", targetAction = "SECONDARY / AIM", screenNormalizedX = 0.82f, screenNormalizedY = 0.44f, mappingMode = "HOLD_DOWN", touchRadiusDp = 28f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "A", targetAction = "DODGE / JUMP", screenNormalizedX = 0.92f, screenNormalizedY = 0.76f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "B", targetAction = "SPECIAL SKILL", screenNormalizedX = 0.80f, screenNormalizedY = 0.86f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "X", targetAction = "INTERACT / RELOAD", screenNormalizedX = 0.74f, screenNormalizedY = 0.72f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "Y", targetAction = "ULTIMATE BURST", screenNormalizedX = 0.88f, screenNormalizedY = 0.32f, mappingMode = "SINGLE_TAP", touchRadiusDp = 26f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "LEFT_STICK", targetAction = "ANALOG MOVE", screenNormalizedX = 0.18f, screenNormalizedY = 0.72f, mappingMode = "ANALOG_JOYSTICK", touchRadiusDp = 54f),
        KeyMappingEntity(profileId = profileId, gamepadButton = "RIGHT_STICK", targetAction = "CAMERA VIEW", screenNormalizedX = 0.72f, screenNormalizedY = 0.50f, mappingMode = "CAMERA_AIM", touchRadiusDp = 70f)
      )
    }
  }

  fun getInitialRecoilConfigs(profileId: Long): List<RecoilConfigEntity> {
    return when (profileId) {
      1L -> listOf(
        RecoilConfigEntity(
          profileId = 1L,
          weaponName = "M416 (Pro Laser Curve)",
          isRecoilActive = true,
          verticalPullStrength = 52f,
          horizontalDriftCompensation = -8f,
          initialDelayMs = 50,
          stepIntervalMs = 16,
          burstStabilizerEnabled = true,
          scope1xMultiplier = 1.0f,
          scope2xMultiplier = 1.25f,
          scope3xMultiplier = 1.65f,
          scope4xMultiplier = 2.15f,
          scope6xMultiplier = 2.90f,
          scope8xMultiplier = 3.80f,
          selectedScope = "1X",
          curveType = "EXPONENTIAL_DECAY",
          triggerButton = "RT / R2 (SHOOT)"
        ),
        RecoilConfigEntity(
          profileId = 1L,
          weaponName = "AKM / Beryl M762 (Heavy Caliber)",
          isRecoilActive = true,
          verticalPullStrength = 78f,
          horizontalDriftCompensation = 14f,
          initialDelayMs = 40,
          stepIntervalMs = 14,
          burstStabilizerEnabled = true,
          scope1xMultiplier = 1.0f,
          scope2xMultiplier = 1.30f,
          scope3xMultiplier = 1.80f,
          scope4xMultiplier = 2.40f,
          scope6xMultiplier = 3.20f,
          scope8xMultiplier = 4.10f,
          selectedScope = "1X",
          curveType = "SMOOTH_S_CURVE",
          triggerButton = "RT / R2 (SHOOT)"
        ),
        RecoilConfigEntity(
          profileId = 1L,
          weaponName = "UMP45 / SMG Fast Stabilizer",
          isRecoilActive = true,
          verticalPullStrength = 36f,
          horizontalDriftCompensation = 0f,
          initialDelayMs = 65,
          stepIntervalMs = 18,
          burstStabilizerEnabled = false,
          scope1xMultiplier = 1.0f,
          scope2xMultiplier = 1.15f,
          scope3xMultiplier = 1.45f,
          scope4xMultiplier = 1.80f,
          scope6xMultiplier = 2.20f,
          scope8xMultiplier = 2.80f,
          selectedScope = "1X",
          curveType = "LINEAR_CONSTANT",
          triggerButton = "RT / R2 (SHOOT)"
        )
      )
      3L -> listOf(
        RecoilConfigEntity(
          profileId = 3L,
          weaponName = "Free Fire Auto Drag-Shot",
          isRecoilActive = true,
          verticalPullStrength = 62f,
          horizontalDriftCompensation = 0f,
          initialDelayMs = 30,
          stepIntervalMs = 12,
          burstStabilizerEnabled = true,
          scope1xMultiplier = 1.0f,
          scope2xMultiplier = 1.20f,
          scope3xMultiplier = 1.50f,
          scope4xMultiplier = 2.00f,
          selectedScope = "1X",
          curveType = "DYNAMIC_BURST",
          triggerButton = "RT / R2 (SHOOT)"
        )
      )
      else -> listOf(
        RecoilConfigEntity(
          profileId = profileId,
          weaponName = "Standard Tactical Less-Recoil",
          isRecoilActive = true,
          verticalPullStrength = 45f,
          horizontalDriftCompensation = -4f,
          initialDelayMs = 50,
          stepIntervalMs = 16,
          burstStabilizerEnabled = true,
          scope1xMultiplier = 1.0f,
          scope2xMultiplier = 1.25f,
          scope3xMultiplier = 1.6f,
          scope4xMultiplier = 2.1f,
          selectedScope = "1X",
          curveType = "EXPONENTIAL_DECAY",
          triggerButton = "RT / R2 (SHOOT)"
        )
      )
    }
  }

  fun getInitialMacros(profileId: Long): List<MacroComboEntity> {
    return when (profileId) {
      1L -> listOf(
        MacroComboEntity(
          id = 1L,
          profileId = 1L,
          name = "Drop-Shot Fast Fire (Prone + ADS + Shoot)",
          description = "Instantly drops prone while opening ADS scope and unleashing full-auto spray.",
          triggerButton = "LB / L1",
          loopMode = "ONCE",
          stepsJson = """[{"id":"s1","stepType":"TAP","label":"Prone Action","targetKey":"BUTTON_Y","screenX":0.95,"screenY":0.88,"durationMs":40,"delayAfterMs":30,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"s2","stepType":"PRESS","label":"Hold ADS Scope","targetKey":"BUTTON_L2","screenX":0.85,"screenY":0.42,"durationMs":200,"delayAfterMs":40,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"s3","stepType":"PRESS","label":"Fire Burst Spray","targetKey":"BUTTON_R2","screenX":0.88,"screenY":0.68,"durationMs":250,"delayAfterMs":0,"swipeDeltaX":0.0,"swipeDeltaY":0.0}]""",
          totalDurationMs = 360,
          isEnabled = true
        ),
        MacroComboEntity(
          id = 2L,
          profileId = 1L,
          name = "Turbo Rapid Fire (30 Taps/Sec)",
          description = "Converts single-tap DMR/Pistols into high velocity full auto fire.",
          triggerButton = "M1",
          loopMode = "REPEAT_WHILE_HELD",
          stepsJson = """[{"id":"t1","stepType":"TAP","label":"Rapid Tap 1","targetKey":"BUTTON_R2","screenX":0.88,"screenY":0.68,"durationMs":16,"delayAfterMs":17,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"t2","stepType":"TAP","label":"Rapid Tap 2","targetKey":"BUTTON_R2","screenX":0.88,"screenY":0.68,"durationMs":16,"delayAfterMs":17,"swipeDeltaX":0.0,"swipeDeltaY":0.0}]""",
          totalDurationMs = 66,
          isEnabled = true
        )
      )
      2L -> listOf(
        MacroComboEntity(
          id = 3L,
          profileId = 2L,
          name = "Quick-Scope Sniper 1-Tap",
          description = "Fast ADS scope in, precision hold breath, instant trigger shot, and weapon cycle.",
          triggerButton = "LB / L1",
          loopMode = "ONCE",
          stepsJson = """[{"id":"qs1","stepType":"PRESS","label":"ADS Scope In","targetKey":"BUTTON_L2","screenX":0.82,"screenY":0.46,"durationMs":120,"delayAfterMs":40,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"qs2","stepType":"TAP","label":"Instant Fire Shot","targetKey":"BUTTON_R2","screenX":0.86,"screenY":0.66,"durationMs":30,"delayAfterMs":20,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"qs3","stepType":"RELEASE","label":"Release Scope","targetKey":"BUTTON_L2","screenX":0.82,"screenY":0.46,"durationMs":20,"delayAfterMs":10,"swipeDeltaX":0.0,"swipeDeltaY":0.0}]""",
          totalDurationMs = 240,
          isEnabled = true
        ),
        MacroComboEntity(
          id = 4L,
          profileId = 2L,
          name = "Slide-Cancel Bunny Hop",
          description = "High-tier movement combo: Crouch Slide into instantaneous Jump cancel.",
          triggerButton = "M2",
          loopMode = "ONCE",
          stepsJson = """[{"id":"sc1","stepType":"TAP","label":"Crouch Slide","targetKey":"BUTTON_B","screenX":0.90,"screenY":0.82,"durationMs":50,"delayAfterMs":70,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"sc2","stepType":"TAP","label":"Jump Cancel","targetKey":"BUTTON_A","screenX":0.93,"screenY":0.68,"durationMs":50,"delayAfterMs":0,"swipeDeltaX":0.0,"swipeDeltaY":0.0}]""",
          totalDurationMs = 170,
          isEnabled = true
        )
      )
      else -> listOf(
        MacroComboEntity(
          id = 5L,
          profileId = profileId,
          name = "Pro Tactical Combo",
          description = "Automated tactical ability combo with instant follow-up strike.",
          triggerButton = "LB / L1",
          loopMode = "ONCE",
          stepsJson = """[{"id":"p1","stepType":"TAP","label":"Ability Trigger","targetKey":"BUTTON_B","screenX":0.80,"screenY":0.86,"durationMs":50,"delayAfterMs":60,"swipeDeltaX":0.0,"swipeDeltaY":0.0},{"id":"p2","stepType":"TAP","label":"Follow-up Attack","targetKey":"BUTTON_R2","screenX":0.86,"screenY":0.70,"durationMs":50,"delayAfterMs":0,"swipeDeltaX":0.0,"swipeDeltaY":0.0}]""",
          totalDurationMs = 160,
          isEnabled = true
        )
      )
    }
  }

  fun getInitialGamepadSettings(profileId: Long): GamepadSettingsEntity {
    return GamepadSettingsEntity(
      profileId = profileId,
      leftStickDeadzone = 0.08f,
      rightStickDeadzone = 0.06f,
      aimSensitivityX = 1.2f,
      aimSensitivityY = 1.0f,
      hapticVibration = true,
      turboGlobalRate = 18
    )
  }
}
