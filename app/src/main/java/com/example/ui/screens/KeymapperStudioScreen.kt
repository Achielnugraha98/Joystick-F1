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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.GameProfileEntity
import com.example.data.db.entities.KeyMappingEntity
import com.example.data.models.GamepadButtonCode
import com.example.data.models.MappingMode
import com.example.gamepad.GamepadInputState
import com.example.ui.components.CyberBadge
import com.example.ui.components.KeyPinOverlayView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeymapperStudioScreen(
  activeProfile: GameProfileEntity?,
  mappings: List<KeyMappingEntity>,
  selectedMappingId: Long?,
  inputState: GamepadInputState,
  onSelectMapping: (KeyMappingEntity?) -> Unit,
  onUpdatePosition: (KeyMappingEntity, Float, Float) -> Unit,
  onAddMapping: (String, String, Float, Float, String) -> Unit,
  onUpdateMappingDetails: (KeyMappingEntity) -> Unit,
  onDeleteMapping: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var pendingNormX by remember { mutableFloatStateOf(0.5f) }
  var pendingNormY by remember { mutableFloatStateOf(0.5f) }

  var editingMapping by remember { mutableStateOf<KeyMappingEntity?>(null) }

  val selectedMapping = mappings.find { it.id == selectedMappingId }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
  ) {
    // Top Bar: Active Profile & Add Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Overlay Keymapper Studio",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Target Profile: ${activeProfile?.name ?: "Default Setup"}",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = {
            pendingNormX = 0.5f
            pendingNormY = 0.5f
            showAddDialog = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBackground),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_pin_btn")
        ) {
          Icon(Icons.Default.AddLocation, contentDescription = "Add Pin", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("ADD PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Interactive Screen Overlay Canvas (Tap to add, Drag to move)
    item {
      KeyPinOverlayView(
        mappings = mappings,
        selectedMappingId = selectedMappingId,
        onSelectMapping = { mapping ->
          onSelectMapping(mapping)
          editingMapping = mapping
        },
        onUpdatePosition = onUpdatePosition,
        onAddNewAtPosition = { x, y ->
          pendingNormX = x
          pendingNormY = y
          showAddDialog = true
        },
        activePressedButtons = inputState.pressedButtons,
        gameBackgroundPreset = activeProfile?.iconType ?: "PUBG"
      )
    }

    // Selected Pin Inspector / Quick Config Card
    if (selectedMapping != null) {
      item {
        SelectedPinInspector(
          mapping = selectedMapping,
          onEdit = { editingMapping = selectedMapping },
          onDelete = { onDeleteMapping(selectedMapping.id) },
          onToggleRecoil = {
            val newMode = if (selectedMapping.mappingMode == "RECOIL_SYNC") "SINGLE_TAP" else "RECOIL_SYNC"
            onUpdateMappingDetails(selectedMapping.copy(mappingMode = newMode))
          },
          onToggleTurbo = {
            val newMode = if (selectedMapping.mappingMode == "TURBO_RAPID") "SINGLE_TAP" else "TURBO_RAPID"
            onUpdateMappingDetails(selectedMapping.copy(mappingMode = newMode))
          }
        )
      }
    }

    // All Key Mappings List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Mapped Buttons (${mappings.size})",
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Tap item to edit or calibrate",
          color = TextSecondary,
          fontSize = 11.sp
        )
      }
    }

    // Key Mappings Items
    items(mappings, key = { it.id }) { mapping ->
      val isSelected = mapping.id == selectedMappingId
      val isPhysicallyPressed = inputState.pressedButtons.contains(mapping.gamepadButton)

      KeyMappingItemRow(
        mapping = mapping,
        isSelected = isSelected,
        isPressed = isPhysicallyPressed,
        onClick = {
          onSelectMapping(mapping)
          editingMapping = mapping
        },
        onDelete = { onDeleteMapping(mapping.id) }
      )
    }
  }

  // Add / Edit Dialog
  if (showAddDialog) {
    KeyMappingEditDialog(
      initialButton = "A",
      initialAction = "TAP ACTION",
      initialMode = "SINGLE_TAP",
      initialTurboRate = 15,
      normX = pendingNormX,
      normY = pendingNormY,
      onDismiss = { showAddDialog = false },
      onSave = { btn, act, mode, rate, x, y ->
        onAddMapping(btn, act, x, y, mode)
        showAddDialog = false
      }
    )
  }

  if (editingMapping != null) {
    val m = editingMapping!!
    KeyMappingEditDialog(
      initialButton = m.gamepadButton,
      initialAction = m.targetAction,
      initialMode = m.mappingMode,
      initialTurboRate = m.turboTapsPerSecond,
      normX = m.screenNormalizedX,
      normY = m.screenNormalizedY,
      onDismiss = { editingMapping = null },
      onSave = { btn, act, mode, rate, x, y ->
        onUpdateMappingDetails(
          m.copy(
            gamepadButton = btn,
            targetAction = act,
            mappingMode = mode,
            turboTapsPerSecond = rate,
            screenNormalizedX = x,
            screenNormalizedY = y
          )
        )
        editingMapping = null
      }
    )
  }
}

@Composable
private fun SelectedPinInspector(
  mapping: KeyMappingEntity,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onToggleRecoil: () -> Unit,
  onToggleTurbo: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
      .border(1.2.dp, NeonCyan, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(NeonCyanGlow)
              .border(1.dp, NeonCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = mapping.gamepadButton.take(3),
              color = NeonCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black
            )
          }

          Column {
            Text(
              text = "${mapping.gamepadButton} -> ${mapping.targetAction}",
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Position: X ${(mapping.screenNormalizedX * 100).toInt()}% , Y ${(mapping.screenNormalizedY * 100).toInt()}%",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeonCyan, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RecoilRed, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Feature toggles for this specific mapped pin
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val isRecoil = mapping.mappingMode == "RECOIL_SYNC"
        val isTurbo = mapping.mappingMode == "TURBO_RAPID"

        OutlinedButton(
          onClick = onToggleRecoil,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isRecoil) RecoilRedGlow else Color.Transparent
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isRecoil) RecoilRed else CyberSurfaceBorder),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = if (isRecoil) "RECOIL SYNC: ON" else "+ SYNC RECOIL",
            color = if (isRecoil) RecoilRed else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = onToggleTurbo,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isTurbo) HazardOrange.copy(alpha = 0.2f) else Color.Transparent
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (isTurbo) HazardOrange else CyberSurfaceBorder),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = if (isTurbo) "TURBO (${mapping.turboTapsPerSecond}Hz)" else "+ TURBO RAPID",
            color = if (isTurbo) HazardOrange else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun KeyMappingItemRow(
  mapping: KeyMappingEntity,
  isSelected: Boolean,
  isPressed: Boolean,
  onClick: () -> Unit,
  onDelete: () -> Unit
) {
  val accentColor = when (mapping.mappingMode) {
    "RECOIL_SYNC" -> RecoilRed
    "TURBO_RAPID" -> HazardOrange
    "MACRO_TRIGGER" -> NeonCyan
    "ANALOG_JOYSTICK" -> NitroGreen
    else -> NeonCyan
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) NeonCyan else CyberSurfaceBorder,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) NeonCyan else CyberSurfaceBorder),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mapping.gamepadButton.replace("BUTTON_", "").take(3),
            color = if (isPressed) OnPrimary else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = mapping.targetAction,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
            CyberBadge(text = mapping.mappingMode.replace('_', ' '), color = accentColor)
          }
          Text(
            text = "X ${(mapping.screenNormalizedX * 100).toInt()}% • Y ${(mapping.screenNormalizedY * 100).toInt()}%",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
        Icon(
          imageVector = Icons.Default.ChevronRight,
          contentDescription = "Select",
          tint = TextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun KeyMappingEditDialog(
  initialButton: String,
  initialAction: String,
  initialMode: String,
  initialTurboRate: Int,
  normX: Float,
  normY: Float,
  onDismiss: () -> Unit,
  onSave: (String, String, String, Int, Float, Float) -> Unit
) {
  var selectedButton by remember { mutableStateOf(initialButton) }
  var actionText by remember { mutableStateOf(initialAction) }
  var selectedMode by remember { mutableStateOf(initialMode) }
  var turboRate by remember { mutableIntStateOf(initialTurboRate) }
  var posX by remember { mutableFloatStateOf(normX) }
  var posY by remember { mutableFloatStateOf(normY) }

  val commonButtons = listOf(
    "RT / R2", "LT / L2", "RB / R1", "LB / L1",
    "A", "B", "X", "Y",
    "LEFT_STICK", "RIGHT_STICK", "L3 / LS", "R3 / RS",
    "DPAD_UP", "DPAD_DOWN", "DPAD_LEFT", "DPAD_RIGHT",
    "M1", "M2", "START", "SELECT"
  )

  val commonActions = listOf(
    "SHOOT / FIRE", "AIM / ADS", "JUMP", "CROUCH / SLIDE", "PRONE",
    "RELOAD", "AUTO SPRINT", "PEEK / LEAN", "GRENADE", "MEDKIT",
    "CAMERA LOOK", "MOVEMENT WASD", "SKILL 1", "ULTIMATE"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CyberSurfaceVariant,
    titleContentColor = TextPrimary,
    title = {
      Text("Configure Button Mapping", fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Gamepad Button:", fontSize = 12.sp, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(commonButtons) { btn ->
            val isSel = selectedButton == btn
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) NeonCyan else CyberSurface)
                .border(1.dp, if (isSel) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                .clickable { selectedButton = btn }
                .padding(horizontal = 10.dp, vertical = 6.dp)
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

        OutlinedTextField(
          value = actionText,
          onValueChange = { actionText = it },
          label = { Text("Target Action Name") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Quick Action Presets:", fontSize = 11.sp, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(commonActions) { act ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CyberSurface)
                .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { actionText = act }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = act, color = TextSecondary, fontSize = 10.sp)
            }
          }
        }

        Text("Mapping Mode:", fontSize = 12.sp, color = TextSecondary)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ModeSelectChip(label = "Tap", mode = "SINGLE_TAP", current = selectedMode, onSelect = { selectedMode = it })
          ModeSelectChip(label = "Turbo", mode = "TURBO_RAPID", current = selectedMode, onSelect = { selectedMode = it })
          ModeSelectChip(label = "Recoil", mode = "RECOIL_SYNC", current = selectedMode, onSelect = { selectedMode = it })
          ModeSelectChip(label = "Macro", mode = "MACRO_TRIGGER", current = selectedMode, onSelect = { selectedMode = it })
        }

        if (selectedMode == "TURBO_RAPID") {
          Text("Turbo Rate: $turboRate taps/second", fontSize = 11.sp, color = HazardOrange)
          Slider(
            value = turboRate.toFloat(),
            onValueChange = { turboRate = it.toInt() },
            valueRange = 5f..30f,
            colors = SliderDefaults.colors(thumbColor = HazardOrange, activeTrackColor = HazardOrange)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(selectedButton, actionText.ifBlank { "Action" }, selectedMode, turboRate, posX, posY)
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBackground)
      ) {
        Text("SAVE MAPPING", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = TextSecondary)
      }
    }
  )
}

@Composable
private fun ModeSelectChip(
  label: String,
  mode: String,
  current: String,
  onSelect: (String) -> Unit
) {
  val isSel = current == mode
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSel) NeonCyan else CyberSurface)
      .border(1.dp, if (isSel) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
      .clickable { onSelect(mode) }
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      color = if (isSel) CyberBackground else TextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
