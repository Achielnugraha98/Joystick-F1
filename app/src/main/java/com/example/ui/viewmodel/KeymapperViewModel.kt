package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.entities.*
import com.example.data.models.DefaultDataPresets
import com.example.data.models.MacroStep
import com.example.ui.components.serializeMacroSteps
import com.example.data.repository.KeymapperRepository
import com.example.gamepad.GamepadInputManager
import com.example.gamepad.GamepadInputState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiNotification(
  val id: Long = System.currentTimeMillis(),
  val message: String,
  val isSuccess: Boolean = true
)

class KeymapperViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: KeymapperRepository
  
  val inputState: StateFlow<GamepadInputState> = GamepadInputManager.state

  private val _selectedTab = MutableStateFlow(0) // 0: Home/Profiles, 1: Keymapper, 2: Less Recoil, 3: Macros, 4: Tester, 5: Floating HUD
  val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

  private val _isServiceActive = MutableStateFlow(true)
  val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

  private val _isRecoilGlobalActive = MutableStateFlow(true)
  val isRecoilGlobalActive: StateFlow<Boolean> = _isRecoilGlobalActive.asStateFlow()

  private val _isMacroGlobalActive = MutableStateFlow(true)
  val isMacroGlobalActive: StateFlow<Boolean> = _isMacroGlobalActive.asStateFlow()

  private val _notification = MutableStateFlow<UiNotification?>(null)
  val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

  // Selected Profile ID for editing
  private val _activeProfileId = MutableStateFlow(1L)
  val activeProfileId: StateFlow<Long> = _activeProfileId.asStateFlow()

  // Selected mapping for detailed inspector
  private val _selectedMappingId = MutableStateFlow<Long?>(null)
  val selectedMappingId: StateFlow<Long?> = _selectedMappingId.asStateFlow()

  // All Profiles
  val allProfiles: StateFlow<List<GameProfileEntity>>
  
  // Current Profile data flows
  val currentProfile: StateFlow<GameProfileEntity?>
  val currentKeyMappings: StateFlow<List<KeyMappingEntity>>
  val currentRecoilConfigs: StateFlow<List<RecoilConfigEntity>>
  val currentMacros: StateFlow<List<MacroComboEntity>>
  val currentGamepadSettings: StateFlow<GamepadSettingsEntity?>

  init {
    val db = AppDatabase.getDatabase(application, viewModelScope)
    repository = KeymapperRepository(db.gameProfileDao())

    allProfiles = repository.allProfiles
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    currentProfile = _activeProfileId
      .flatMapLatest { id -> repository.getProfileById(id) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    currentKeyMappings = _activeProfileId
      .flatMapLatest { id -> repository.getKeyMappings(id) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    currentRecoilConfigs = _activeProfileId
      .flatMapLatest { id -> repository.getRecoilConfigs(id) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    currentMacros = _activeProfileId
      .flatMapLatest { id -> repository.getMacros(id) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    currentGamepadSettings = _activeProfileId
      .flatMapLatest { id -> repository.getSettings(id) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Check connected gamepad hardware
    GamepadInputManager.refreshConnectedDevices(application)

    // Auto set active profile ID when loaded
    viewModelScope.launch {
      repository.activeProfile.collect { active ->
        if (active != null) {
          _activeProfileId.value = active.id
        }
      }
    }
  }

  fun setTab(index: Int) {
    _selectedTab.value = index
  }

  fun selectProfile(profileId: Long) {
    _activeProfileId.value = profileId
    viewModelScope.launch {
      repository.setActiveProfile(profileId)
      showNotification("Activated profile: #${profileId}")
    }
  }

  fun toggleService(active: Boolean) {
    _isServiceActive.value = active
    showNotification(if (active) "Keymapper Floating Service Started" else "Keymapper Service Paused")
  }

  fun toggleRecoilGlobal(active: Boolean) {
    _isRecoilGlobalActive.value = active
    showNotification(if (active) "Less-Recoil Engine Enabled" else "Less-Recoil Engine Disabled")
  }

  fun toggleMacroGlobal(active: Boolean) {
    _isMacroGlobalActive.value = active
    showNotification(if (active) "Advanced Macro Engine Enabled" else "Macro Engine Disabled")
  }

  fun createNewProfile(name: String, category: String, iconType: String, description: String) {
    viewModelScope.launch {
      val newId = repository.createProfile(name, category, iconType, description)
      _activeProfileId.value = newId
      showNotification("Created profile: $name")
    }
  }

  fun deleteProfile(profileId: Long) {
    viewModelScope.launch {
      repository.deleteProfile(profileId)
      // Revert to profile 1
      _activeProfileId.value = 1L
      showNotification("Profile deleted")
    }
  }

  // --- Key Mapping Operations ---
  fun selectKeyMapping(mapping: KeyMappingEntity?) {
    _selectedMappingId.value = mapping?.id
  }

  fun addKeyMapping(button: String, action: String, normX: Float, normY: Float, mode: String = "SINGLE_TAP") {
    viewModelScope.launch {
      val newMapping = KeyMappingEntity(
        profileId = _activeProfileId.value,
        gamepadButton = button,
        targetAction = action,
        screenNormalizedX = normX,
        screenNormalizedY = normY,
        mappingMode = mode,
        touchRadiusDp = 28f
      )
      val id = repository.saveKeyMapping(newMapping)
      _selectedMappingId.value = id
      showNotification("Mapped $button -> $action")
    }
  }

  fun updateKeyMappingPosition(mapping: KeyMappingEntity, newX: Float, newY: Float) {
    viewModelScope.launch {
      val updated = mapping.copy(screenNormalizedX = newX, screenNormalizedY = newY)
      repository.updateKeyMapping(updated)
    }
  }

  fun updateKeyMappingDetails(mapping: KeyMappingEntity) {
    viewModelScope.launch {
      repository.updateKeyMapping(mapping)
      showNotification("Updated button mapping: ${mapping.gamepadButton}")
    }
  }

  fun deleteKeyMapping(id: Long) {
    viewModelScope.launch {
      repository.deleteKeyMapping(id)
      if (_selectedMappingId.value == id) {
        _selectedMappingId.value = null
      }
      showNotification("Mapping removed")
    }
  }

  // --- Recoil Config Operations ---
  fun updateRecoilConfig(config: RecoilConfigEntity) {
    viewModelScope.launch {
      repository.updateRecoilConfig(config)
      showNotification("Recoil Curve Saved: ${config.weaponName}")
    }
  }

  fun addRecoilConfig(weaponName: String, verticalStrength: Float, horizontalDrift: Float, curveType: String) {
    viewModelScope.launch {
      val newConfig = RecoilConfigEntity(
        profileId = _activeProfileId.value,
        weaponName = weaponName,
        isRecoilActive = true,
        verticalPullStrength = verticalStrength,
        horizontalDriftCompensation = horizontalDrift,
        curveType = curveType
      )
      repository.saveRecoilConfig(newConfig)
      showNotification("Added recoil profile: $weaponName")
    }
  }

  // --- Macro Operations ---
  fun addMacro(name: String, triggerButton: String, loopMode: String, steps: List<MacroStep>) {
    viewModelScope.launch {
      val json = serializeMacroSteps(steps)
      val totalDuration = steps.sumOf { (it.durationMs + it.delayAfterMs).toLong() }
      val newMacro = MacroComboEntity(
        profileId = _activeProfileId.value,
        name = name,
        triggerButton = triggerButton,
        loopMode = loopMode,
        stepsJson = json,
        totalDurationMs = totalDuration,
        isEnabled = true
      )
      repository.saveMacro(newMacro)
      showNotification("Saved macro combo: $name")
    }
  }

  fun updateMacro(macro: MacroComboEntity) {
    viewModelScope.launch {
      repository.updateMacro(macro)
      showNotification("Updated macro: ${macro.name}")
    }
  }

  fun deleteMacro(macroId: Long) {
    viewModelScope.launch {
      repository.deleteMacro(macroId)
      showNotification("Macro deleted")
    }
  }

  // --- Gamepad Calibration & Settings ---
  fun updateSettings(settings: GamepadSettingsEntity) {
    viewModelScope.launch {
      repository.saveSettings(settings)
      showNotification("Controller calibration saved")
    }
  }

  fun refreshGamepadHardware() {
    GamepadInputManager.refreshConnectedDevices(getApplication())
    showNotification("Controller list refreshed")
  }

  fun simulateGamepadButton(button: String, isPressed: Boolean) {
    GamepadInputManager.simulateButtonPress(button, isPressed)
  }

  fun simulateStick(lx: Float, ly: Float, rx: Float, ry: Float) {
    GamepadInputManager.simulateStickMovement(lx, ly, rx, ry)
  }

  fun simulateTriggers(lt: Float, rt: Float) {
    GamepadInputManager.simulateTriggers(lt, rt)
  }

  fun testHapticVibration() {
    GamepadInputManager.triggerHapticPulse()
    showNotification("Haptic vibration pulse dispatched!")
  }

  fun dismissNotification() {
    _notification.value = null
  }

  private fun showNotification(msg: String, isSuccess: Boolean = true) {
    _notification.value = UiNotification(message = msg, isSuccess = isSuccess)
  }
}
