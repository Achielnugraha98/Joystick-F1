package com.example.data.models

enum class GameCategory(val displayName: String) {
  BATTLE_ROYALE("Battle Royale"),
  FPS_SHOOTER("FPS Shooter"),
  MOBA_RPG("MOBA / RPG"),
  RACING("Racing / Arcade"),
  CUSTOM("Custom Sandbox")
}

enum class MappingMode(val displayName: String, val tag: String) {
  SINGLE_TAP("Single Tap", "TAP"),
  TURBO_RAPID("Turbo Rapid Fire", "TURBO"),
  HOLD_DOWN("Continuous Hold", "HOLD"),
  ANALOG_JOYSTICK("Analog Move (WASD)", "JOYSTICK"),
  CAMERA_AIM("Camera / Scope Aim", "AIM"),
  MACRO_TRIGGER("Macro Combo Trigger", "MACRO"),
  RECOIL_SYNC("Recoil Compensator Fire", "RECOIL")
}

enum class GamepadButtonCode(val code: String, val label: String, val category: String) {
  A("BUTTON_A", "Button A / Cross", "ACTION"),
  B("BUTTON_B", "Button B / Circle", "ACTION"),
  X("BUTTON_X", "Button X / Square", "ACTION"),
  Y("BUTTON_Y", "Button Y / Triangle", "ACTION"),
  
  DPAD_UP("DPAD_UP", "D-Pad Up", "DPAD"),
  DPAD_DOWN("DPAD_DOWN", "D-Pad Down", "DPAD"),
  DPAD_LEFT("DPAD_LEFT", "D-Pad Left", "DPAD"),
  DPAD_RIGHT("DPAD_RIGHT", "D-Pad Right", "DPAD"),
  
  L1("BUTTON_L1", "LB / L1 Bumper", "SHOULDER"),
  R1("BUTTON_R1", "RB / R1 Bumper", "SHOULDER"),
  L2("BUTTON_L2", "LT / L2 Trigger", "TRIGGER"),
  R2("BUTTON_R2", "RT / R2 Trigger", "TRIGGER"),
  
  L3("BUTTON_L3", "LS / L3 Thumb Click", "STICK_CLICK"),
  R3("BUTTON_R3", "RS / R3 Thumb Click", "STICK_CLICK"),
  
  LEFT_STICK("LEFT_STICK", "Left Analog Stick (Move)", "ANALOG"),
  RIGHT_STICK("RIGHT_STICK", "Right Analog Stick (Look)", "ANALOG"),
  
  START("BUTTON_START", "Start / Menu", "SYSTEM"),
  SELECT("BUTTON_SELECT", "Select / Back", "SYSTEM"),
  
  M1("BUTTON_M1", "M1 Back Paddle", "PADDLE"),
  M2("BUTTON_M2", "M2 Back Paddle", "PADDLE")
}

data class MacroStep(
  val id: String = java.util.UUID.randomUUID().toString(),
  val stepType: String = "TAP", // TAP, PRESS, RELEASE, DELAY, SWIPE
  val label: String = "Action Step",
  val targetKey: String = "BUTTON_A",
  val screenX: Float = 0.5f,
  val screenY: Float = 0.5f,
  val durationMs: Int = 100,
  val delayAfterMs: Int = 50,
  val swipeDeltaX: Float = 0f,
  val swipeDeltaY: Float = 0f
)

enum class RecoilCurveType(val displayName: String, val description: String) {
  EXPONENTIAL_DECAY("Exponential Decay", "Strong initial pull that gradually stabilizes"),
  SMOOTH_S_CURVE("Smooth S-Curve", "Natural human-like compensation curve"),
  LINEAR_CONSTANT("Linear Steady", "Constant consistent downward pull force"),
  DYNAMIC_BURST("Dynamic Burst", "Precision pulse compensation for burst tap fire")
}
