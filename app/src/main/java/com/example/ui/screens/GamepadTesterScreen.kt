package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.GamepadSettingsEntity
import com.example.gamepad.GamepadInputState
import com.example.ui.components.CyberBadge
import com.example.ui.components.GamepadVisualizer
import com.example.ui.theme.*

@Composable
fun GamepadTesterScreen(
  inputState: GamepadInputState,
  settings: GamepadSettingsEntity?,
  onUpdateSettings: (GamepadSettingsEntity) -> Unit,
  onSimulateButton: (String, Boolean) -> Unit,
  onSimulateStick: (Float, Float, Float, Float) -> Unit,
  onSimulateTriggers: (Float, Float) -> Unit,
  onTestHaptic: () -> Unit,
  onRefreshHardware: () -> Unit,
  modifier: Modifier = Modifier
) {
  val activeSettings = settings ?: GamepadSettingsEntity(profileId = 1L)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Gamepad Live Tester",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            CyberBadge(
              text = if (inputState.isAnyConnected) "HARDWARE ONLINE" else "VIRTUAL BRIDGE",
              color = if (inputState.isAnyConnected) NitroGreen else HazardOrange
            )
          }
          Text(
            text = "Real-time key event capture & stick deadzone calibration",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        IconButton(
          onClick = onRefreshHardware,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(CyberSurface)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Scan", tint = NeonCyan, modifier = Modifier.size(18.dp))
        }
      }
    }

    // Live Gamepad Diagram
    item {
      GamepadVisualizer(
        inputState = inputState,
        onButtonClick = { btn ->
          val isAlready = inputState.pressedButtons.contains(btn)
          onSimulateButton(btn, !isAlready)
        }
      )
    }

    // Virtual Input Pad Simulator Bar (for easy testing)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(12.dp)
      ) {
        Text(
          text = "Virtual Tap Simulator (Tap to toggle button state):",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val testButtons = listOf("A", "B", "X", "Y", "LB / L1", "RB / R1", "LT / L2", "RT / R2", "L3 / LS", "R3 / RS", "M1", "M2")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(testButtons) { btn ->
            val isPressed = inputState.pressedButtons.contains(btn)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPressed) NeonCyan else CyberSurface)
                .border(1.dp, if (isPressed) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                .clickable {
                  onSimulateButton(btn, !isPressed)
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = btn,
                color = if (isPressed) CyberBackground else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Real-time Event Monitor Console
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CyberBackground)
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp))
          .padding(12.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "LIVE INPUT STREAM:", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
              text = if (inputState.lastEventTimeMs > 0) "Polled at ${inputState.lastEventTimeMs}ms" else "Idle",
              color = TextMuted,
              fontSize = 9.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = inputState.lastEventDescription,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "LS: (${"%.2f".format(inputState.leftStickX)}, ${"%.2f".format(inputState.leftStickY)})",
              color = TextSecondary,
              fontSize = 10.sp
            )
            Text(
              text = "RS: (${"%.2f".format(inputState.rightStickX)}, ${"%.2f".format(inputState.rightStickY)})",
              color = TextSecondary,
              fontSize = 10.sp
            )
            Text(
              text = "LT: ${"%.2f".format(inputState.leftTrigger)} | RT: ${"%.2f".format(inputState.rightTrigger)}",
              color = TextSecondary,
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Analog Stick Deadzone & Sensitivity Calibration
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Analog Stick Calibration & Deadzones",
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )

        // Left Stick Deadzone
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Left Stick Movement Deadzone:", color = TextSecondary, fontSize = 12.sp)
            Text("${(activeSettings.leftStickDeadzone * 100).toInt()}%", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeSettings.leftStickDeadzone,
            onValueChange = { onUpdateSettings(activeSettings.copy(leftStickDeadzone = it)) },
            valueRange = 0.01f..0.30f,
            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
          )
        }

        // Right Stick Deadzone
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Right Stick Aim / Look Deadzone:", color = TextSecondary, fontSize = 12.sp)
            Text("${(activeSettings.rightStickDeadzone * 100).toInt()}%", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeSettings.rightStickDeadzone,
            onValueChange = { onUpdateSettings(activeSettings.copy(rightStickDeadzone = it)) },
            valueRange = 0.01f..0.30f,
            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
          )
        }

        // Aim Sensitivity
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Aim Sensitivity Multiplier:", color = TextSecondary, fontSize = 12.sp)
            Text("${"%.1f".format(activeSettings.aimSensitivityX)}x", color = NitroGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeSettings.aimSensitivityX,
            onValueChange = { onUpdateSettings(activeSettings.copy(aimSensitivityX = it, aimSensitivityY = it)) },
            valueRange = 0.5f..3.0f,
            colors = SliderDefaults.colors(thumbColor = NitroGreen, activeTrackColor = NitroGreen)
          )
        }

        // Vibration Feedback Test
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Haptic Feedback Motors", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Vibrate controller on recoil / macro execution", color = TextSecondary, fontSize = 10.sp)
          }

          Button(
            onClick = onTestHaptic,
            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface, contentColor = NeonCyan),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Vibration, contentDescription = "Test Vibration", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("TEST MOTOR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
