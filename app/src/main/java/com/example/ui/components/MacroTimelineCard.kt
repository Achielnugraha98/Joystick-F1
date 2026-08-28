package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.MacroComboEntity
import com.example.data.models.MacroStep
import com.example.ui.theme.*
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MacroTimelineCard(
  macro: MacroComboEntity,
  onEditMacro: (MacroComboEntity) -> Unit,
  onDeleteMacro: (Long) -> Unit,
  onToggleEnabled: (MacroComboEntity, Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var isPlayingSimulator by remember { mutableStateOf(false) }
  var currentStepIndex by remember { mutableIntStateOf(-1) }
  val coroutineScope = rememberCoroutineScope()

  // Parse JSON steps
  val steps = remember(macro.stepsJson) {
    parseMacroSteps(macro.stepsJson)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
      .border(1.dp, if (macro.isEnabled) NeonPurple.copy(alpha = 0.4f) else CyberSurfaceBorder, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    // Top Row: Macro Name, Trigger Badge, Switch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = macro.name,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          CyberBadge(
            text = "TRIGGER: ${macro.triggerButton}",
            color = NeonPurple
          )
        }
        if (macro.description.isNotEmpty()) {
          Text(
            text = macro.description,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }

      Switch(
        checked = macro.isEnabled,
        onCheckedChange = { onToggleEnabled(macro, it) },
        colors = SwitchDefaults.colors(
          checkedThumbColor = CyberBackground,
          checkedTrackColor = NeonPurple,
          uncheckedThumbColor = TextMuted,
          uncheckedTrackColor = CyberSurface
        )
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Macro Meta info row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CyberBadge(text = "MODE: ${macro.loopMode.replace('_', ' ')}", color = HazardOrange)
      CyberBadge(text = "DURATION: ${macro.totalDurationMs}ms", color = NeonCyan)
      CyberBadge(text = "${steps.size} STEPS", color = NitroGreen)
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Step Visual Timeline
    Text(
      text = "Execution Sequence Timeline:",
      color = TextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      itemsIndexed(steps) { index, step ->
        val isCurrentExecuting = index == currentStepIndex
        StepPill(
          step = step,
          stepNumber = index + 1,
          isExecuting = isCurrentExecuting
        )
        if (index < steps.size - 1) {
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Next Step",
            tint = if (isCurrentExecuting) NeonPurple else TextMuted,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Action buttons: Test Playback Simulator, Edit Steps, Delete
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Simulator Play Button
      Button(
        onClick = {
          if (!isPlayingSimulator) {
            isPlayingSimulator = true
            coroutineScope.launch {
              for (i in steps.indices) {
                currentStepIndex = i
                val step = steps[i]
                delay(step.durationMs.toLong().coerceAtLeast(30L))
                delay(step.delayAfterMs.toLong().coerceAtLeast(20L))
              }
              currentStepIndex = -1
              isPlayingSimulator = false
            }
          }
        },
        enabled = !isPlayingSimulator && steps.isNotEmpty(),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isPlayingSimulator) NitroGreen else NeonPurple,
          contentColor = CyberBackground
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = if (isPlayingSimulator) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
          contentDescription = "Simulate Play",
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isPlayingSimulator) "SIMULATING..." else "TEST COMBO",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        IconButton(
          onClick = { onEditMacro(macro) },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(CyberSurface)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Macro",
            tint = NeonCyan,
            modifier = Modifier.size(16.dp)
          )
        }

        IconButton(
          onClick = { onDeleteMacro(macro.id) },
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(CyberSurface)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Macro",
            tint = RecoilRed,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun StepPill(
  step: MacroStep,
  stepNumber: Int,
  isExecuting: Boolean
) {
  val borderColor by animateColorAsState(
    targetValue = if (isExecuting) NeonPurple else CyberSurfaceBorder,
    label = "step_border"
  )
  val bgColor by animateColorAsState(
    targetValue = if (isExecuting) NeonPurpleGlow else CyberSurface,
    label = "step_bg"
  )

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(bgColor)
      .border(1.2.dp, borderColor, RoundedCornerShape(10.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "#$stepNumber",
          color = NeonCyan,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = step.stepType,
          color = when (step.stepType) {
            "PRESS" -> HazardOrange
            "RELEASE" -> RecoilRed
            "DELAY" -> TextMuted
            "SWIPE" -> NitroGreen
            else -> Color.White
          },
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Text(
        text = "${step.targetKey.replace("BUTTON_", "")} (${step.durationMs}ms)",
        color = TextSecondary,
        fontSize = 9.sp
      )
    }
  }
}

fun parseMacroSteps(jsonString: String): List<MacroStep> {
  val result = mutableListOf<MacroStep>()
  try {
    val array = JSONArray(jsonString)
    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      result.add(
        MacroStep(
          id = obj.optString("id", UUID.randomUUID().toString()),
          stepType = obj.optString("stepType", "TAP"),
          label = obj.optString("label", "Step"),
          targetKey = obj.optString("targetKey", "BUTTON_A"),
          screenX = obj.optDouble("screenX", 0.5).toFloat(),
          screenY = obj.optDouble("screenY", 0.5).toFloat(),
          durationMs = obj.optInt("durationMs", 50),
          delayAfterMs = obj.optInt("delayAfterMs", 30),
          swipeDeltaX = obj.optDouble("swipeDeltaX", 0.0).toFloat(),
          swipeDeltaY = obj.optDouble("swipeDeltaY", 0.0).toFloat()
        )
      )
    }
  } catch (e: Exception) {
    // fallback default step
    result.add(MacroStep(stepType = "TAP", targetKey = "BUTTON_A", durationMs = 50, delayAfterMs = 20))
  }
  return result
}

fun serializeMacroSteps(steps: List<MacroStep>): String {
  val array = JSONArray()
  for (step in steps) {
    val obj = JSONObject()
    obj.put("id", step.id)
    obj.put("stepType", step.stepType)
    obj.put("label", step.label)
    obj.put("targetKey", step.targetKey)
    obj.put("screenX", step.screenX.toDouble())
    obj.put("screenY", step.screenY.toDouble())
    obj.put("durationMs", step.durationMs)
    obj.put("delayAfterMs", step.delayAfterMs)
    obj.put("swipeDeltaX", step.swipeDeltaX.toDouble())
    obj.put("swipeDeltaY", step.swipeDeltaY.toDouble())
    array.put(obj)
  }
  return array.toString()
}
