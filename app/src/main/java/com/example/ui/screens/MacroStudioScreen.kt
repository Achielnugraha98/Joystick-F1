package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.MacroComboEntity
import com.example.data.models.MacroStep
import com.example.ui.components.CyberBadge
import com.example.ui.components.MacroTimelineCard
import com.example.ui.components.parseMacroSteps
import com.example.ui.components.serializeMacroSteps
import com.example.ui.theme.*

@Composable
fun MacroStudioScreen(
  macros: List<MacroComboEntity>,
  isGlobalMacroActive: Boolean,
  onToggleGlobalMacro: (Boolean) -> Unit,
  onAddMacro: (String, String, String, List<MacroStep>) -> Unit,
  onUpdateMacro: (MacroComboEntity) -> Unit,
  onDeleteMacro: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showNewMacroDialog by remember { mutableStateOf(false) }
  var editingMacro by remember { mutableStateOf<MacroComboEntity?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
  ) {
    // Header & Global Toggle
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Advanced Macro Studio",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            CyberBadge(text = "COMBO ENGINE", color = NeonPurple)
          }
          Text(
            text = "Multi-button sequence automation & turbo timing",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        Switch(
          checked = isGlobalMacroActive,
          onCheckedChange = onToggleGlobalMacro,
          colors = SwitchDefaults.colors(
            checkedThumbColor = CyberBackground,
            checkedTrackColor = NeonPurple,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurface
          )
        )
      }
    }

    // Quick Pro Esports Combo Templates
    item {
      Text(
        text = "Esports Pro Combo Templates:",
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(4.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
          PresetComboChip(
            title = "Drop-Shot Fast Fire",
            subtitle = "Prone + ADS + Shoot",
            onClick = {
              val steps = listOf(
                MacroStep(stepType = "TAP", targetKey = "BUTTON_Y", durationMs = 40, delayAfterMs = 30),
                MacroStep(stepType = "PRESS", targetKey = "BUTTON_L2", durationMs = 180, delayAfterMs = 20),
                MacroStep(stepType = "PRESS", targetKey = "BUTTON_R2", durationMs = 240, delayAfterMs = 0)
              )
              onAddMacro("Drop-Shot Fast Fire", "LB / L1", "ONCE", steps)
            }
          )
        }
        item {
          PresetComboChip(
            title = "Quick-Scope 1-Tap",
            subtitle = "ADS + Shot + Cycle",
            onClick = {
              val steps = listOf(
                MacroStep(stepType = "PRESS", targetKey = "BUTTON_L2", durationMs = 120, delayAfterMs = 30),
                MacroStep(stepType = "TAP", targetKey = "BUTTON_R2", durationMs = 30, delayAfterMs = 20),
                MacroStep(stepType = "RELEASE", targetKey = "BUTTON_L2", durationMs = 20, delayAfterMs = 10)
              )
              onAddMacro("Quick-Scope 1-Tap", "M1", "ONCE", steps)
            }
          )
        }
        item {
          PresetComboChip(
            title = "Turbo Auto Click 30Hz",
            subtitle = "30 Taps/Sec Pistol",
            onClick = {
              val steps = listOf(
                MacroStep(stepType = "TAP", targetKey = "BUTTON_R2", durationMs = 16, delayAfterMs = 17),
                MacroStep(stepType = "TAP", targetKey = "BUTTON_R2", durationMs = 16, delayAfterMs = 17)
              )
              onAddMacro("Turbo Auto Click 30Hz", "M2", "REPEAT_WHILE_HELD", steps)
            }
          )
        }
      }
    }

    // Macro Cards Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Active Macros (${macros.size})",
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )

        Button(
          onClick = { showNewMacroDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = CyberBackground),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("create_macro_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Create Macro", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("CREATE MACRO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Macro Timeline Cards
    items(macros, key = { it.id }) { macro ->
      MacroTimelineCard(
        macro = macro,
        onEditMacro = { editingMacro = it },
        onDeleteMacro = { onDeleteMacro(it) },
        onToggleEnabled = { m, enabled ->
          onUpdateMacro(m.copy(isEnabled = enabled))
        }
      )
    }
  }

  // Dialog for creating/editing macro
  if (showNewMacroDialog) {
    MacroSequenceBuilderDialog(
      initialName = "",
      initialTrigger = "LB / L1",
      initialLoopMode = "ONCE",
      initialSteps = listOf(
        MacroStep(stepType = "TAP", label = "Step 1", targetKey = "BUTTON_A", durationMs = 50, delayAfterMs = 40),
        MacroStep(stepType = "TAP", label = "Step 2", targetKey = "BUTTON_R2", durationMs = 100, delayAfterMs = 0)
      ),
      onDismiss = { showNewMacroDialog = false },
      onSave = { name, trig, mode, steps ->
        onAddMacro(name, trig, mode, steps)
        showNewMacroDialog = false
      }
    )
  }

  if (editingMacro != null) {
    val m = editingMacro!!
    MacroSequenceBuilderDialog(
      initialName = m.name,
      initialTrigger = m.triggerButton,
      initialLoopMode = m.loopMode,
      initialSteps = parseMacroSteps(m.stepsJson),
      onDismiss = { editingMacro = null },
      onSave = { name, trig, mode, steps ->
        val json = serializeMacroSteps(steps)
        val total = steps.sumOf { (it.durationMs + it.delayAfterMs).toLong() }
        onUpdateMacro(m.copy(name = name, triggerButton = trig, loopMode = mode, stepsJson = json, totalDurationMs = total))
        editingMacro = null
      }
    )
  }
}

@Composable
private fun PresetComboChip(
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurfaceVariant)
      .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Column {
      Text(text = title, color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
    }
  }
}

@Composable
private fun MacroSequenceBuilderDialog(
  initialName: String,
  initialTrigger: String,
  initialLoopMode: String,
  initialSteps: List<MacroStep>,
  onDismiss: () -> Unit,
  onSave: (String, String, String, List<MacroStep>) -> Unit
) {
  var name by remember { mutableStateOf(initialName) }
  var triggerButton by remember { mutableStateOf(initialTrigger) }
  var loopMode by remember { mutableStateOf(initialLoopMode) }
  val steps = remember { mutableStateListOf<MacroStep>().apply { addAll(initialSteps) } }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CyberSurfaceVariant,
    titleContentColor = TextPrimary,
    title = {
      Text("Macro Sequence Builder", fontWeight = FontWeight.Bold)
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Macro Combo Name") },
            placeholder = { Text("e.g. Drop Shot Spray, Fast Reload Cancel") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NeonPurple,
              unfocusedBorderColor = CyberSurfaceBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }

        item {
          Text("Trigger Gamepad Button:", fontSize = 12.sp, color = TextSecondary)
          val buttons = listOf("LB / L1", "RB / R1", "Y", "B", "M1", "M2", "L3 / LS", "R3 / RS")
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(buttons) { btn ->
              val isSel = triggerButton == btn
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) NeonPurple else CyberSurface)
                  .border(1.dp, if (isSel) NeonPurple else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                  .clickable { triggerButton = btn }
                  .padding(horizontal = 8.dp, vertical = 5.dp)
              ) {
                Text(
                  text = btn,
                  color = if (isSel) CyberBackground else TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        item {
          Text("Loop Execution Mode:", fontSize = 12.sp, color = TextSecondary)
          val modes = listOf("ONCE", "REPEAT_WHILE_HELD", "TOGGLE_LOOP")
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            modes.forEach { m ->
              val isSel = loopMode == m
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) HazardOrange else CyberSurface)
                  .border(1.dp, if (isSel) HazardOrange else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                  .clickable { loopMode = m }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (m) {
                    "ONCE" -> "1-Shot"
                    "REPEAT_WHILE_HELD" -> "While Held"
                    else -> "Toggle"
                  },
                  color = if (isSel) CyberBackground else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Step Actions List:", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
            TextButton(
              onClick = {
                steps.add(MacroStep(stepType = "TAP", targetKey = "BUTTON_A", durationMs = 50, delayAfterMs = 30))
              },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Icon(Icons.Default.AddCircle, contentDescription = "Add Step", tint = NeonPurple, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("ADD STEP", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        itemsIndexed(steps) { idx, step ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(CyberSurface)
              .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(10.dp))
              .padding(8.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("#${idx + 1} ${step.stepType} - ${step.targetKey}", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                if (steps.size > 1) {
                  IconButton(onClick = { steps.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = RecoilRed, modifier = Modifier.size(14.dp))
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Duration ms
                Column(modifier = Modifier.weight(1f)) {
                  Text("Duration: ${step.durationMs}ms", color = TextSecondary, fontSize = 9.sp)
                  Slider(
                    value = step.durationMs.toFloat(),
                    onValueChange = { steps[idx] = step.copy(durationMs = it.toInt()) },
                    valueRange = 10f..500f,
                    colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                  )
                }
                // Delay ms
                Column(modifier = Modifier.weight(1f)) {
                  Text("Delay After: ${step.delayAfterMs}ms", color = TextSecondary, fontSize = 9.sp)
                  Slider(
                    value = step.delayAfterMs.toFloat(),
                    onValueChange = { steps[idx] = step.copy(delayAfterMs = it.toInt()) },
                    valueRange = 0f..400f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(name.ifBlank { "Custom Combo" }, triggerButton, loopMode, steps.toList())
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = CyberBackground)
      ) {
        Text("SAVE MACRO", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = TextSecondary)
      }
    }
  )
}
