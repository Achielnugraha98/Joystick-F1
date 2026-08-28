package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamepad.GamepadInputState
import com.example.ui.theme.*

@Composable
fun GamepadVisualizer(
  inputState: GamepadInputState,
  onButtonClick: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          colors = listOf(CyberSurfaceVariant, CyberSurface)
        ),
        RoundedCornerShape(20.dp)
      )
      .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
      .padding(16.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      // Top row: Triggers & Bumpers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Trigger & Bumper
        Column(modifier = Modifier.weight(1f)) {
          TriggerBar(
            label = "LT / L2",
            value = inputState.leftTrigger,
            isPressed = inputState.pressedButtons.contains("LT / L2"),
            onClick = { onButtonClick("LT / L2") }
          )
          Spacer(modifier = Modifier.height(6.dp))
          GamepadBumperButton(
            label = "LB / L1",
            isPressed = inputState.pressedButtons.contains("LB / L1"),
            onClick = { onButtonClick("LB / L1") }
          )
        }

        // Controller Brand / Model Logo in Center
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (inputState.isAnyConnected) NeonCyanGlow else CyberSurface)
              .border(1.dp, if (inputState.isAnyConnected) NeonCyan else CyberSurfaceBorder, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (inputState.isAnyConnected) NitroGreen else HazardOrange)
            )
          }
          Text(
            text = if (inputState.isAnyConnected) "SYNCED" else "READY",
            color = if (inputState.isAnyConnected) NitroGreen else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        // Right Trigger & Bumper
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          TriggerBar(
            label = "RT / R2",
            value = inputState.rightTrigger,
            isPressed = inputState.pressedButtons.contains("RT / R2"),
            onClick = { onButtonClick("RT / R2") }
          )
          Spacer(modifier = Modifier.height(6.dp))
          GamepadBumperButton(
            label = "RB / R1",
            isPressed = inputState.pressedButtons.contains("RB / R1"),
            onClick = { onButtonClick("RB / R1") }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main Controller Body: Left Side (Stick + DPAD), Center (Select/Start), Right Side (Face buttons + Stick)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Section: Left Analog Stick & D-Pad
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          // Left Stick
          AnalogStickWidget(
            label = "LS (L3)",
            stickX = inputState.leftStickX,
            stickY = inputState.leftStickY,
            isClicked = inputState.pressedButtons.contains("L3 / LS"),
            onClick = { onButtonClick("L3 / LS") }
          )

          Spacer(modifier = Modifier.height(12.dp))

          // D-Pad Cross
          DPadCrossWidget(
            pressedButtons = inputState.pressedButtons,
            onDirectionClick = onButtonClick
          )
        }

        // Center Section: Select, Home, Start, Back Paddles
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(0.9f)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            SmallGamepadButton(
              label = "SELECT",
              isPressed = inputState.pressedButtons.contains("SELECT"),
              onClick = { onButtonClick("SELECT") }
            )
            SmallGamepadButton(
              label = "START",
              isPressed = inputState.pressedButtons.contains("START"),
              onClick = { onButtonClick("START") }
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Back Paddles (M1 / M2 for Esports Pro Controllers)
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            PaddleButton(
              label = "M1",
              isPressed = inputState.pressedButtons.contains("M1"),
              onClick = { onButtonClick("M1") }
            )
            PaddleButton(
              label = "M2",
              isPressed = inputState.pressedButtons.contains("M2"),
              onClick = { onButtonClick("M2") }
            )
          }
        }

        // Right Section: Action Face Buttons (Y, X, B, A) & Right Stick
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          // Diamond Action Buttons
          FaceButtonsDiamond(
            pressedButtons = inputState.pressedButtons,
            onButtonClick = onButtonClick
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Right Stick
          AnalogStickWidget(
            label = "RS (R3)",
            stickX = inputState.rightStickX,
            stickY = inputState.rightStickY,
            isClicked = inputState.pressedButtons.contains("R3 / RS"),
            onClick = { onButtonClick("R3 / RS") }
          )
        }
      }
    }
  }
}

@Composable
private fun TriggerBar(
  label: String,
  value: Float,
  isPressed: Boolean,
  onClick: () -> Unit
) {
  val activeColor by animateColorAsState(
    targetValue = if (isPressed || value > 0.1f) NeonCyan else TextMuted,
    animationSpec = tween(150),
    label = "trigger_color"
  )

  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = activeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text(
        text = "${(value * 100).toInt()}%",
        color = activeColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { if (isPressed && value == 0f) 1f else value.coerceIn(0f, 1f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = NeonCyan,
      trackColor = CyberSurface
    )
  }
}

@Composable
private fun GamepadBumperButton(
  label: String,
  isPressed: Boolean,
  onClick: () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = if (isPressed) NeonCyan else GamepadButtonDefault,
    label = "bumper_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (isPressed) CyberBackground else TextPrimary,
    label = "bumper_text"
  )

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .border(1.dp, if (isPressed) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text = label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
private fun AnalogStickWidget(
  label: String,
  stickX: Float,
  stickY: Float,
  isClicked: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(62.dp)
        .clip(CircleShape)
        .background(GamepadGrip)
        .border(1.5.dp, if (isClicked) NeonPurple else CyberSurfaceBorder, CircleShape)
        .clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        // Grid crosshair lines
        drawLine(
          color = Color(0x33FFFFFF),
          start = Offset(size.width / 2, 8f),
          end = Offset(size.width / 2, size.height - 8f),
          strokeWidth = 1f
        )
        drawLine(
          color = Color(0x33FFFFFF),
          start = Offset(8f, size.height / 2),
          end = Offset(size.width - 8f, size.height / 2),
          strokeWidth = 1f
        )

        // Deadzone boundary
        drawCircle(
          color = Color(0x1A00E5FF),
          radius = size.width * 0.22f,
          center = center,
          style = Stroke(width = 1.5f)
        )

        // Stick Head with dynamic deflection
        val maxDeflect = size.width * 0.28f
        val stickCenter = Offset(
          x = center.x + (stickX.coerceIn(-1f, 1f) * maxDeflect),
          y = center.y + (stickY.coerceIn(-1f, 1f) * maxDeflect)
        )

        drawCircle(
          brush = Brush.radialGradient(
            colors = if (isClicked) listOf(NeonPurple, GamepadButtonDefault) else listOf(NeonCyan, GamepadButtonDefault),
            center = stickCenter,
            radius = size.width * 0.25f
          ),
          radius = size.width * 0.25f,
          center = stickCenter
        )

        drawCircle(
          color = if (isClicked) NeonPurple else NeonCyan,
          radius = size.width * 0.25f,
          center = stickCenter,
          style = Stroke(width = 2f)
        )
      }
    }
    Text(
      text = label,
      color = if (isClicked) NeonPurple else TextSecondary,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(top = 3.dp)
    )
  }
}

@Composable
private fun DPadCrossWidget(
  pressedButtons: Set<String>,
  onDirectionClick: (String) -> Unit
) {
  Box(
    modifier = Modifier.size(72.dp),
    contentAlignment = Alignment.Center
  ) {
    // Up
    DPadButton(
      icon = Icons.Default.KeyboardArrowUp,
      isPressed = pressedButtons.contains("DPAD_UP"),
      onClick = { onDirectionClick("DPAD_UP") },
      modifier = Modifier.align(Alignment.TopCenter)
    )
    // Down
    DPadButton(
      icon = Icons.Default.KeyboardArrowDown,
      isPressed = pressedButtons.contains("DPAD_DOWN"),
      onClick = { onDirectionClick("DPAD_DOWN") },
      modifier = Modifier.align(Alignment.BottomCenter)
    )
    // Left
    DPadButton(
      icon = Icons.Default.KeyboardArrowLeft,
      isPressed = pressedButtons.contains("DPAD_LEFT"),
      onClick = { onDirectionClick("DPAD_LEFT") },
      modifier = Modifier.align(Alignment.CenterStart)
    )
    // Right
    DPadButton(
      icon = Icons.Default.KeyboardArrowRight,
      isPressed = pressedButtons.contains("DPAD_RIGHT"),
      onClick = { onDirectionClick("DPAD_RIGHT") },
      modifier = Modifier.align(Alignment.CenterEnd)
    )
  }
}

@Composable
private fun DPadButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isPressed: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg by animateColorAsState(
    targetValue = if (isPressed) NeonCyan else GamepadButtonDefault,
    label = "dpad_bg"
  )
  val tint by animateColorAsState(
    targetValue = if (isPressed) CyberBackground else TextPrimary,
    label = "dpad_tint"
  )

  Box(
    modifier = modifier
      .size(24.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .border(0.8.dp, if (isPressed) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(4.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(imageVector = icon, contentDescription = "DPad direction", tint = tint, modifier = Modifier.size(16.dp))
  }
}

@Composable
private fun FaceButtonsDiamond(
  pressedButtons: Set<String>,
  onButtonClick: (String) -> Unit
) {
  Box(
    modifier = Modifier.size(72.dp),
    contentAlignment = Alignment.Center
  ) {
    // Y (Top) - Yellow / Green accent
    FaceRoundButton(
      label = "Y",
      accentColor = HazardOrange,
      isPressed = pressedButtons.contains("Y"),
      onClick = { onButtonClick("Y") },
      modifier = Modifier.align(Alignment.TopCenter)
    )
    // A (Bottom) - Green accent
    FaceRoundButton(
      label = "A",
      accentColor = NitroGreen,
      isPressed = pressedButtons.contains("A"),
      onClick = { onButtonClick("A") },
      modifier = Modifier.align(Alignment.BottomCenter)
    )
    // X (Left) - Cyan / Blue accent
    FaceRoundButton(
      label = "X",
      accentColor = NeonCyan,
      isPressed = pressedButtons.contains("X"),
      onClick = { onButtonClick("X") },
      modifier = Modifier.align(Alignment.CenterStart)
    )
    // B (Right) - Red / Coral accent
    FaceRoundButton(
      label = "B",
      accentColor = RecoilRed,
      isPressed = pressedButtons.contains("B"),
      onClick = { onButtonClick("B") },
      modifier = Modifier.align(Alignment.CenterEnd)
    )
  }
}

@Composable
private fun FaceRoundButton(
  label: String,
  accentColor: Color,
  isPressed: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg by animateColorAsState(
    targetValue = if (isPressed) accentColor else GamepadButtonDefault,
    label = "face_btn_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (isPressed) CyberBackground else accentColor,
    label = "face_btn_text"
  )

  Box(
    modifier = modifier
      .size(24.dp)
      .clip(CircleShape)
      .background(bg)
      .border(1.dp, if (isPressed) accentColor else accentColor.copy(alpha = 0.5f), CircleShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(text = label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
private fun SmallGamepadButton(
  label: String,
  isPressed: Boolean,
  onClick: () -> Unit
) {
  val bg by animateColorAsState(
    targetValue = if (isPressed) NeonCyan else GamepadButtonDefault,
    label = "small_btn_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (isPressed) CyberBackground else TextSecondary,
    label = "small_btn_text"
  )

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bg)
      .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text = label, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
private fun PaddleButton(
  label: String,
  isPressed: Boolean,
  onClick: () -> Unit
) {
  val bg by animateColorAsState(
    targetValue = if (isPressed) NeonPurple else CyberSurface,
    label = "paddle_btn_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (isPressed) CyberBackground else NeonPurple,
    label = "paddle_btn_text"
  )

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bg)
      .border(1.dp, if (isPressed) NeonPurple else NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(text = label, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
  }
}
