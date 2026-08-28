package com.example.gamepad

import android.content.Context
import android.hardware.input.InputManager
import android.os.Build
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConnectedControllerInfo(
  val id: Int,
  val name: String,
  val descriptor: String,
  val isGameController: Boolean,
  val vendorId: Int = 0,
  val productId: Int = 0
)

data class GamepadInputState(
  val connectedDevices: List<ConnectedControllerInfo> = emptyList(),
  val isAnyConnected: Boolean = false,
  val primaryDeviceName: String = "No Gamepad Detected",
  val pressedButtons: Set<String> = emptySet(),
  val leftStickX: Float = 0f,
  val leftStickY: Float = 0f,
  val rightStickX: Float = 0f,
  val rightStickY: Float = 0f,
  val leftTrigger: Float = 0f,
  val rightTrigger: Float = 0f,
  val dpadX: Float = 0f,
  val dpadY: Float = 0f,
  val lastEventTimeMs: Long = 0L,
  val lastEventDescription: String = "Ready for input",
  val simulatedVibrationCount: Int = 0
)

object GamepadInputManager {

  private val _state = MutableStateFlow(GamepadInputState())
  val state: StateFlow<GamepadInputState> = _state.asStateFlow()

  fun refreshConnectedDevices(context: Context) {
    try {
      val deviceIds = InputDevice.getDeviceIds()
      val controllers = mutableListOf<ConnectedControllerInfo>()
      for (deviceId in deviceIds) {
        val dev = InputDevice.getDevice(deviceId) ?: continue
        val sources = dev.sources
        val isGamepad = (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) ||
            (sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK)
        if (isGamepad && !dev.isVirtual) {
          controllers.add(
            ConnectedControllerInfo(
              id = dev.id,
              name = dev.name ?: "Generic Gamepad",
              descriptor = dev.descriptor ?: "",
              isGameController = true,
              vendorId = dev.vendorId,
              productId = dev.productId
            )
          )
        }
      }
      _state.update { current ->
        val isConn = controllers.isNotEmpty()
        current.copy(
          connectedDevices = controllers,
          isAnyConnected = isConn,
          primaryDeviceName = if (isConn) controllers.first().name else "Pro Gamepad Virtual Link"
        )
      }
    } catch (e: Exception) {
      // Fallback
    }
  }

  fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    val buttonName = mapKeyCodeToButtonName(keyCode) ?: return false
    _state.update { current ->
      val updatedButtons = current.pressedButtons + buttonName
      current.copy(
        pressedButtons = updatedButtons,
        lastEventTimeMs = SystemClock.uptimeMillis(),
        lastEventDescription = "Pressed: $buttonName (Keycode $keyCode)"
      )
    }
    return true
  }

  fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    val buttonName = mapKeyCodeToButtonName(keyCode) ?: return false
    _state.update { current ->
      val updatedButtons = current.pressedButtons - buttonName
      current.copy(
        pressedButtons = updatedButtons,
        lastEventTimeMs = SystemClock.uptimeMillis(),
        lastEventDescription = "Released: $buttonName"
      )
    }
    return true
  }

  fun onGenericMotionEvent(event: MotionEvent): Boolean {
    if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK ||
      event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {

      val lx = getCenteredAxis(event, MotionEvent.AXIS_X)
      val ly = getCenteredAxis(event, MotionEvent.AXIS_Y)
      val rx = getCenteredAxis(event, MotionEvent.AXIS_Z).let { if (it == 0f) getCenteredAxis(event, MotionEvent.AXIS_RX) else it }
      val ry = getCenteredAxis(event, MotionEvent.AXIS_RZ).let { if (it == 0f) getCenteredAxis(event, MotionEvent.AXIS_RY) else it }

      val lt = event.getAxisValue(MotionEvent.AXIS_LTRIGGER).let { if (it == 0f) event.getAxisValue(MotionEvent.AXIS_BRAKE) else it }
      val rt = event.getAxisValue(MotionEvent.AXIS_RTRIGGER).let { if (it == 0f) event.getAxisValue(MotionEvent.AXIS_GAS) else it }

      val dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
      val dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

      _state.update { current ->
        current.copy(
          leftStickX = lx,
          leftStickY = ly,
          rightStickX = rx,
          rightStickY = ry,
          leftTrigger = lt,
          rightTrigger = rt,
          dpadX = dpadX,
          dpadY = dpadY,
          lastEventTimeMs = SystemClock.uptimeMillis()
        )
      }
      return true
    }
    return false
  }

  // Virtual test helper so users can test gamepad features directly in UI
  fun simulateButtonPress(buttonName: String, isPressed: Boolean) {
    _state.update { current ->
      val updated = if (isPressed) current.pressedButtons + buttonName else current.pressedButtons - buttonName
      current.copy(
        pressedButtons = updated,
        lastEventTimeMs = SystemClock.uptimeMillis(),
        lastEventDescription = if (isPressed) "Simulated Press: $buttonName" else "Simulated Release: $buttonName"
      )
    }
  }

  fun simulateStickMovement(lx: Float, ly: Float, rx: Float, ry: Float) {
    _state.update { current ->
      current.copy(
        leftStickX = lx,
        leftStickY = ly,
        rightStickX = rx,
        rightStickY = ry,
        lastEventTimeMs = SystemClock.uptimeMillis()
      )
    }
  }

  fun simulateTriggers(lt: Float, rt: Float) {
    _state.update { current ->
      val updatedButtons = current.pressedButtons.toMutableSet()
      if (lt > 0.3f) updatedButtons.add("LT / L2") else updatedButtons.remove("LT / L2")
      if (rt > 0.3f) updatedButtons.add("RT / R2") else updatedButtons.remove("RT / R2")
      current.copy(
        leftTrigger = lt,
        rightTrigger = rt,
        pressedButtons = updatedButtons,
        lastEventTimeMs = SystemClock.uptimeMillis()
      )
    }
  }

  fun triggerHapticPulse() {
    _state.update { it.copy(simulatedVibrationCount = it.simulatedVibrationCount + 1) }
  }

  private fun getCenteredAxis(event: MotionEvent, axis: Int, historyPos: Int = -1): Float {
    val range = event.device?.getMotionRange(axis, event.source) ?: return 0f
    val value = if (historyPos < 0) event.getAxisValue(axis) else event.getHistoricalAxisValue(axis, historyPos)
    val flat = range.flat
    return if (Math.abs(value) > flat) value else 0f
  }

  private fun mapKeyCodeToButtonName(keyCode: Int): String? {
    return when (keyCode) {
      KeyEvent.KEYCODE_BUTTON_A -> "A"
      KeyEvent.KEYCODE_BUTTON_B -> "B"
      KeyEvent.KEYCODE_BUTTON_X -> "X"
      KeyEvent.KEYCODE_BUTTON_Y -> "Y"
      KeyEvent.KEYCODE_BUTTON_L1 -> "LB / L1"
      KeyEvent.KEYCODE_BUTTON_R1 -> "RB / R1"
      KeyEvent.KEYCODE_BUTTON_L2 -> "LT / L2"
      KeyEvent.KEYCODE_BUTTON_R2 -> "RT / R2"
      KeyEvent.KEYCODE_BUTTON_THUMBL -> "L3 / LS"
      KeyEvent.KEYCODE_BUTTON_THUMBR -> "R3 / RS"
      KeyEvent.KEYCODE_BUTTON_START -> "START"
      KeyEvent.KEYCODE_BUTTON_SELECT -> "SELECT"
      KeyEvent.KEYCODE_BUTTON_MODE -> "MODE / HOME"
      KeyEvent.KEYCODE_DPAD_UP -> "DPAD_UP"
      KeyEvent.KEYCODE_DPAD_DOWN -> "DPAD_DOWN"
      KeyEvent.KEYCODE_DPAD_LEFT -> "DPAD_LEFT"
      KeyEvent.KEYCODE_DPAD_RIGHT -> "DPAD_RIGHT"
      KeyEvent.KEYCODE_BUTTON_C -> "M1"
      KeyEvent.KEYCODE_BUTTON_Z -> "M2"
      else -> null
    }
  }
}
