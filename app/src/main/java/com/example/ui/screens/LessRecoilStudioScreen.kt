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
import com.example.data.db.entities.RecoilConfigEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.RecoilCurveGraph
import com.example.ui.components.RecoilTargetSimulator
import com.example.ui.theme.*

@Composable
fun LessRecoilStudioScreen(
  recoilConfigs: List<RecoilConfigEntity>,
  isGlobalRecoilActive: Boolean,
  onToggleGlobalRecoil: (Boolean) -> Unit,
  onUpdateRecoilConfig: (RecoilConfigEntity) -> Unit,
  onAddRecoilConfig: (String, Float, Float, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedWeaponIndex by remember { mutableIntStateOf(0) }
  var showAddWeaponDialog by remember { mutableStateOf(false) }

  val activeConfig = recoilConfigs.getOrNull(selectedWeaponIndex) ?: recoilConfigs.firstOrNull() ?: RecoilConfigEntity(
    profileId = 1L,
    weaponName = "Default Assault Rifle",
    verticalPullStrength = 50f,
    horizontalDriftCompensation = -5f
  )

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
              text = "Less-Recoil Studio",
              color = TextPrimary,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            CyberBadge(text = "ANTI-RECOIL V3", color = RecoilRed)
          }
          Text(
            text = "Smart hardware pull-down & spread compensation",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        Switch(
          checked = isGlobalRecoilActive,
          onCheckedChange = onToggleGlobalRecoil,
          colors = SwitchDefaults.colors(
            checkedThumbColor = CyberBackground,
            checkedTrackColor = RecoilRed,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurface
          )
        )
      }
    }

    // Weapon Profile Switcher Tabs
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Weapon Recoil Profiles:",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
        TextButton(
          onClick = { showAddWeaponDialog = true },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Weapon", tint = NeonCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("NEW WEAPON", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(recoilConfigs.indices.toList()) { index ->
          val config = recoilConfigs[index]
          val isSelected = index == selectedWeaponIndex

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) RecoilRed else CyberSurfaceVariant)
              .border(1.dp, if (isSelected) RecoilRed else CyberSurfaceBorder, RoundedCornerShape(12.dp))
              .clickable { selectedWeaponIndex = index }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Column {
              Text(
                text = config.weaponName,
                color = if (isSelected) CyberBackground else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Pull: ${config.verticalPullStrength.toInt()}% • ${config.curveType.take(6)}",
                color = if (isSelected) CyberBackground.copy(alpha = 0.8f) else TextSecondary,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // Live Target Range Simulator (Shows side-by-side recoil spread difference)
    item {
      RecoilTargetSimulator(
        config = activeConfig,
        isRecoilEnabled = isGlobalRecoilActive && activeConfig.isRecoilActive,
        onRecoilToggle = { enabled ->
          onUpdateRecoilConfig(activeConfig.copy(isRecoilActive = enabled))
        }
      )
    }

    // Mathematical Recoil Pull Curve Graph
    item {
      Column {
        Text(
          text = "Active Dynamic Pull-Down Curve:",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(bottom = 6.dp)
        )
        RecoilCurveGraph(
          curveType = activeConfig.curveType,
          pullStrength = activeConfig.verticalPullStrength,
          delayMs = activeConfig.initialDelayMs
        )
      }
    }

    // Scope Multiplier Bar
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Text(
          text = "Optic / Scope Magnification Multiplier:",
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val scopes = listOf("1X", "2X", "3X", "4X", "6X", "8X")
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          scopes.forEach { scope ->
            val isSel = activeConfig.selectedScope == scope
            val mult = when (scope) {
              "2X" -> activeConfig.scope2xMultiplier
              "3X" -> activeConfig.scope3xMultiplier
              "4X" -> activeConfig.scope4xMultiplier
              "6X" -> activeConfig.scope6xMultiplier
              "8X" -> activeConfig.scope8xMultiplier
              else -> activeConfig.scope1xMultiplier
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) NeonCyan else CyberSurface)
                .border(1.dp, if (isSel) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                .clickable {
                  onUpdateRecoilConfig(activeConfig.copy(selectedScope = scope))
                }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = scope,
                  color = if (isSel) CyberBackground else TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${mult}x",
                  color = if (isSel) CyberBackground else TextMuted,
                  fontSize = 9.sp
                )
              }
            }
          }
        }
      }
    }

    // Recoil Precision Tuning Sliders
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Fine-Tuning Parameters",
          color = TextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )

        // Vertical Pull Slider
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Vertical Pull-Down Force:", color = TextSecondary, fontSize = 12.sp)
            Text("${activeConfig.verticalPullStrength.toInt()}%", color = RecoilRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeConfig.verticalPullStrength,
            onValueChange = { onUpdateRecoilConfig(activeConfig.copy(verticalPullStrength = it)) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = RecoilRed, activeTrackColor = RecoilRed)
          )
        }

        // Horizontal Drift Slider
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Horizontal Drift Stabilizer:", color = TextSecondary, fontSize = 12.sp)
            val drift = activeConfig.horizontalDriftCompensation.toInt()
            val driftLabel = if (drift < 0) "Pull Left ($drift%)" else if (drift > 0) "Pull Right (+$drift%)" else "Centered (0%)"
            Text(driftLabel, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeConfig.horizontalDriftCompensation,
            onValueChange = { onUpdateRecoilConfig(activeConfig.copy(horizontalDriftCompensation = it)) },
            valueRange = -50f..50f,
            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
          )
        }

        // Initial Delay (ms)
        Column {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Initial Delay Before Pull:", color = TextSecondary, fontSize = 12.sp)
            Text("${activeConfig.initialDelayMs} ms", color = HazardOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = activeConfig.initialDelayMs.toFloat(),
            onValueChange = { onUpdateRecoilConfig(activeConfig.copy(initialDelayMs = it.toInt())) },
            valueRange = 0f..150f,
            colors = SliderDefaults.colors(thumbColor = HazardOrange, activeTrackColor = HazardOrange)
          )
        }

        // Curve Algorithm Selector
        Column {
          Text("Recoil Curve Algorithm:", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
          val curves = listOf("EXPONENTIAL_DECAY", "SMOOTH_S_CURVE", "LINEAR_CONSTANT", "DYNAMIC_BURST")
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            curves.forEach { c ->
              val isSel = activeConfig.curveType == c
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSel) NeonPurple else CyberSurface)
                .border(1.dp, if (isSel) NeonPurple else CyberSurfaceBorder, RoundedCornerShape(8.dp))
                .clickable { onUpdateRecoilConfig(activeConfig.copy(curveType = c)) }
                .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (c) {
                    "EXPONENTIAL_DECAY" -> "Expon"
                    "SMOOTH_S_CURVE" -> "S-Curve"
                    "LINEAR_CONSTANT" -> "Linear"
                    else -> "Burst"
                  },
                  color = if (isSel) CyberBackground else TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }

  // Dialog for adding custom weapon profile
  if (showAddWeaponDialog) {
    NewWeaponDialog(
      onDismiss = { showAddWeaponDialog = false },
      onAdd = { name, pull, drift, curve ->
        onAddRecoilConfig(name, pull, drift, curve)
        showAddWeaponDialog = false
      }
    )
  }
}

@Composable
private fun NewWeaponDialog(
  onDismiss: () -> Unit,
  onAdd: (String, Float, Float, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var pull by remember { mutableFloatStateOf(55f) }
  var drift by remember { mutableFloatStateOf(-6f) }
  var curve by remember { mutableStateOf("EXPONENTIAL_DECAY") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CyberSurfaceVariant,
    titleContentColor = TextPrimary,
    title = { Text("Add Custom Weapon Recoil Profile", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Weapon Name") },
          placeholder = { Text("e.g. Beryl M762, MP5, Groza, Vector") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RecoilRed,
            unfocusedBorderColor = CyberSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Vertical Pull: ${pull.toInt()}%", fontSize = 12.sp, color = RecoilRed)
        Slider(
          value = pull,
          onValueChange = { pull = it },
          valueRange = 10f..100f,
          colors = SliderDefaults.colors(thumbColor = RecoilRed, activeTrackColor = RecoilRed)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onAdd(name.ifBlank { "Custom Weapon" }, pull, drift, curve)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RecoilRed, contentColor = Color.White)
      ) {
        Text("SAVE PROFILE", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = TextSecondary)
      }
    }
  )
}
