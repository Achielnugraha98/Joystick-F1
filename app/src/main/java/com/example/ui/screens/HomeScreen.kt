package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.entities.GameProfileEntity
import com.example.data.models.GameCategory
import com.example.gamepad.GamepadInputState
import com.example.ui.components.CyberBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  allProfiles: List<GameProfileEntity>,
  activeProfile: GameProfileEntity?,
  inputState: GamepadInputState,
  isServiceActive: Boolean,
  isRecoilActive: Boolean,
  isMacroActive: Boolean,
  onSelectProfile: (Long) -> Unit,
  onCreateProfile: (String, String, String, String) -> Unit,
  onDeleteProfile: (Long) -> Unit,
  onToggleService: (Boolean) -> Unit,
  onToggleRecoil: (Boolean) -> Unit,
  onToggleMacro: (Boolean) -> Unit,
  onNavigateToTab: (Int) -> Unit,
  onRefreshDevices: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showNewProfileDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
  ) {
    // 1. Sophisticated Dark Active Profile Hero Banner
    item {
      ActiveProfileHeroBanner(
        activeProfile = activeProfile,
        onLaunchStudio = { onNavigateToTab(1) }
      )
    }

    // 2. Controller Connection Status Bar
    item {
      GamepadStatusHero(
        inputState = inputState,
        onRefresh = onRefreshDevices,
        onTestClick = { onNavigateToTab(4) } // Gamepad Tester
      )
    }

    // 3. Two-Column Feature Cards (Anti-Recoil & Macro Engine)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Anti-Recoil Card
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(28.dp))
            .clickable { onNavigateToTab(2) }
            .padding(18.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Adjust,
                contentDescription = "Target",
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
              )
              Switch(
                checked = isRecoilActive,
                onCheckedChange = onToggleRecoil,
                colors = SwitchDefaults.colors(
                  checkedThumbColor = OnPrimary,
                  checkedTrackColor = NeonCyan,
                  uncheckedThumbColor = TextMuted,
                  uncheckedTrackColor = CyberSurfaceBorder
                )
              )
            }
            Column {
              Text(
                text = "Anti-Recoil",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Vertical Pull: 85%",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }

        // Macro Engine Card
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(28.dp))
            .clickable { onNavigateToTab(3) }
            .padding(18.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Macro",
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
              )
              CyberBadge(
                text = if (isMacroActive) "ACTIVE" else "OFF",
                color = if (isMacroActive) NeonCyan else TextMuted
              )
            }
            Column {
              Text(
                text = "Macro Engine",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Response: 2ms",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // 4. Master Service & Features Toggle
    item {
      MasterToggleSection(
        isServiceActive = isServiceActive,
        isRecoilActive = isRecoilActive,
        isMacroActive = isMacroActive,
        onToggleService = onToggleService,
        onToggleRecoil = onToggleRecoil,
        onToggleMacro = onToggleMacro
      )
    }

    // 5. Game Profiles Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Game Profiles",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Custom button maps, recoil curves & macros",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        Button(
          onClick = { showNewProfileDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = OnPrimary),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_profile_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Profile", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("NEW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 6. Game Profiles Cards List
    items(allProfiles, key = { it.id }) { profile ->
      val isCurrentActive = profile.id == activeProfile?.id
      GameProfileCard(
        profile = profile,
        isActive = isCurrentActive,
        onSelect = { onSelectProfile(profile.id) },
        onOpenKeymapper = {
          onSelectProfile(profile.id)
          onNavigateToTab(1) // Screen Keymapper
        },
        onOpenRecoil = {
          onSelectProfile(profile.id)
          onNavigateToTab(2) // Less Recoil Studio
        },
        onOpenMacro = {
          onSelectProfile(profile.id)
          onNavigateToTab(3) // Macro Studio
        },
        onDelete = { onDeleteProfile(profile.id) }
      )
    }
  }

  // Dialog for creating a new game profile
  if (showNewProfileDialog) {
    NewProfileDialog(
      onDismiss = { showNewProfileDialog = false },
      onCreate = { name, cat, icon, desc ->
        onCreateProfile(name, cat, icon, desc)
        showNewProfileDialog = false
      }
    )
  }
}

@Composable
private fun ActiveProfileHeroBanner(
  activeProfile: GameProfileEntity?,
  onLaunchStudio: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .background(CyberSurfaceVariant)
      .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(28.dp))
      .padding(20.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(NeonCyan),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SportsEsports,
          contentDescription = "Active Profile",
          tint = OnPrimary,
          modifier = Modifier.size(32.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "ACTIVE PROFILE",
          color = PrimaryContainer,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = activeProfile?.name ?: "Warzone Mobile",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "6 Macros • 12 Custom Keys",
          color = PrimaryContainer.copy(alpha = 0.75f),
          fontSize = 12.sp
        )
      }

      Button(
        onClick = onLaunchStudio,
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonCyan,
          contentColor = OnPrimary
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          text = "LAUNCH",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

@Composable
private fun GamepadStatusHero(
  inputState: GamepadInputState,
  onRefresh: () -> Unit,
  onTestClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        Brush.linearGradient(
          colors = listOf(CyberSurfaceVariant, CyberSurface)
        ),
        RoundedCornerShape(20.dp)
      )
      .border(1.dp, if (inputState.isAnyConnected) NeonCyan.copy(alpha = 0.5f) else CyberSurfaceBorder, RoundedCornerShape(20.dp))
      .padding(16.dp)
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
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (inputState.isAnyConnected) NeonCyanGlow else CyberSurface)
              .border(1.dp, if (inputState.isAnyConnected) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Gamepad,
              contentDescription = "Gamepad Icon",
              tint = if (inputState.isAnyConnected) NeonCyan else TextSecondary,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Text(
              text = if (inputState.isAnyConnected) inputState.primaryDeviceName else "Gamepad Bridge Ready",
              color = TextPrimary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (inputState.isAnyConnected) NitroGreen else HazardOrange)
              )
              Text(
                text = if (inputState.isAnyConnected) "Hardware Controller Connected" else "Virtual Pad & Hardware Ready",
                color = if (inputState.isAnyConnected) NitroGreen else TextSecondary,
                fontSize = 11.sp
              )
            }
          }
        }

        IconButton(
          onClick = onRefresh,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(CyberSurface)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Scan Controllers",
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Controller Benchmark Stats Pill Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatusStatPill(
          label = "Latency",
          value = if (inputState.isAnyConnected) "1.2 ms" else "< 1.0 ms",
          color = NitroGreen,
          modifier = Modifier.weight(1f)
        )
        StatusStatPill(
          label = "Polling",
          value = "1000 Hz",
          color = NeonCyan,
          modifier = Modifier.weight(1f)
        )
        StatusStatPill(
          label = "Inputs",
          value = "${inputState.pressedButtons.size} Active",
          color = if (inputState.pressedButtons.isNotEmpty()) NeonPurple else TextMuted,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Live Button Tester Shortcut Button
      Button(
        onClick = onTestClick,
        colors = ButtonDefaults.buttonColors(containerColor = CyberSurface, contentColor = NeonCyan),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.SportsEsports, contentDescription = "Tester", modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("OPEN GAMEPAD LIVE TESTER & CALIBRATION", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun StatusStatPill(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(CyberSurface)
      .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(10.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Text(text = label, color = TextMuted, fontSize = 9.sp)
      Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun MasterToggleSection(
  isServiceActive: Boolean,
  isRecoilActive: Boolean,
  isMacroActive: Boolean,
  onToggleService: (Boolean) -> Unit,
  onToggleRecoil: (Boolean) -> Unit,
  onToggleMacro: (Boolean) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(CyberSurfaceVariant, RoundedCornerShape(18.dp))
      .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(18.dp))
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    ToggleRow(
      title = "Floating Keymapper Service",
      subtitle = "Screen touch injection overlay & virtual mapping",
      icon = Icons.Default.Layers,
      color = NeonCyan,
      checked = isServiceActive,
      onCheckedChange = onToggleService
    )

    Divider(color = CyberSurfaceBorder, thickness = 0.8.dp)

    ToggleRow(
      title = "Less-Recoil (Anti-Recoil) Engine",
      subtitle = "Smart weapon pull-down & spread stabilizer",
      icon = Icons.Default.Adjust,
      color = RecoilRed,
      checked = isRecoilActive,
      onCheckedChange = onToggleRecoil
    )

    Divider(color = CyberSurfaceBorder, thickness = 0.8.dp)

    ToggleRow(
      title = "Advanced Macro Combo Engine",
      subtitle = "Drop-shot, fast quick-scope, turbo multi-touch",
      icon = Icons.Default.Bolt,
      color = NeonPurple,
      checked = isMacroActive,
      onCheckedChange = onToggleMacro
    )
  }
}

@Composable
private fun ToggleRow(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (checked) color.copy(alpha = 0.15f) else CyberSurface)
          .border(1.dp, if (checked) color else CyberSurfaceBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = if (checked) color else TextMuted, modifier = Modifier.size(18.dp))
      }
      Column {
        Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = CyberBackground,
        checkedTrackColor = color,
        uncheckedThumbColor = TextMuted,
        uncheckedTrackColor = CyberSurface
      )
    )
  }
}

@Composable
private fun QuickFeatureHub(
  onNavigateToTab: (Int) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    FeatureCard(
      title = "Keymapper",
      subtitle = "Layout Editor",
      icon = Icons.Default.Gamepad,
      color = NeonCyan,
      onClick = { onNavigateToTab(1) },
      modifier = Modifier.weight(1f)
    )
    FeatureCard(
      title = "Less Recoil",
      subtitle = "Recoil Studio",
      icon = Icons.Default.TrackChanges,
      color = RecoilRed,
      onClick = { onNavigateToTab(2) },
      modifier = Modifier.weight(1f)
    )
    FeatureCard(
      title = "Macro Studio",
      subtitle = "Combos & Turbo",
      icon = Icons.Default.FlashOn,
      color = NeonPurple,
      onClick = { onNavigateToTab(3) },
      modifier = Modifier.weight(1f)
    )
    FeatureCard(
      title = "Floating HUD",
      subtitle = "Game Booster",
      icon = Icons.Default.SmartDisplay,
      color = NitroGreen,
      onClick = { onNavigateToTab(5) },
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun FeatureCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurfaceVariant)
      .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(10.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
      Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = TextMuted, fontSize = 9.sp)
    }
  }
}

@Composable
private fun GameProfileCard(
  profile: GameProfileEntity,
  isActive: Boolean,
  onSelect: () -> Unit,
  onOpenKeymapper: () -> Unit,
  onOpenRecoil: () -> Unit,
  onOpenMacro: () -> Unit,
  onDelete: () -> Unit
) {
  val borderAccent = if (isActive) NeonCyan else CyberSurfaceBorder

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(if (isActive) CyberSurfaceVariant else CyberSurface)
      .border(if (isActive) 1.5.dp else 1.dp, borderAccent, RoundedCornerShape(18.dp))
      .clickable(onClick = onSelect)
      .padding(16.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isActive) NeonCyanGlow else CyberBackground)
              .border(1.dp, if (isActive) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (profile.gameCategory) {
                "BATTLE_ROYALE" -> Icons.Default.MilitaryTech
                "FPS_SHOOTER" -> Icons.Default.GpsFixed
                "MOBA_RPG" -> Icons.Default.Shield
                else -> Icons.Default.SportsEsports
              },
              contentDescription = "Game Icon",
              tint = if (isActive) NeonCyan else TextSecondary,
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = profile.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              if (isActive) {
                CyberBadge(text = "ACTIVE", color = NitroGreen)
              }
            }
            Text(
              text = profile.description.ifEmpty { profile.gameCategory.replace('_', ' ') },
              color = TextSecondary,
              fontSize = 11.sp,
              maxLines = 1
            )
          }
        }

        if (!profile.isDefault) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(30.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RecoilRed, modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Direct Quick Access Button Bar for this profile
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onOpenKeymapper,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.TouchApp, contentDescription = "Map", tint = NeonCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Keymap", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onOpenRecoil,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, RecoilRed.copy(alpha = 0.5f)),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Adjust, contentDescription = "Recoil", tint = RecoilRed, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Recoil", color = RecoilRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onOpenMacro,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Bolt, contentDescription = "Macro", tint = NeonPurple, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Macro", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun NewProfileDialog(
  onDismiss: () -> Unit,
  onCreate: (String, String, String, String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("BATTLE_ROYALE") }
  var description by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CyberSurfaceVariant,
    titleContentColor = TextPrimary,
    textContentColor = TextSecondary,
    title = {
      Text("Create New Game Profile", fontWeight = FontWeight.Bold)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Game Profile Name") },
          placeholder = { Text("e.g. Free Fire MAX Pro, Warzone Mobile") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Select Game Genre:", fontSize = 12.sp, color = TextSecondary)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          GenreChip(label = "Battle Royale", selected = category == "BATTLE_ROYALE", onClick = { category = "BATTLE_ROYALE" })
          GenreChip(label = "FPS Shooter", selected = category == "FPS_SHOOTER", onClick = { category = "FPS_SHOOTER" })
          GenreChip(label = "RPG/MOBA", selected = category == "MOBA_RPG", onClick = { category = "MOBA_RPG" })
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Layout Notes") },
          placeholder = { Text("e.g. Anti-recoil calibration for AKM and Sniper quick switch") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberSurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val finalName = name.ifBlank { "Custom Game Profile" }
          onCreate(finalName, category, "GENERIC", description)
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberBackground)
      ) {
        Text("CREATE PROFILE", fontWeight = FontWeight.Bold)
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
private fun GenreChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (selected) NeonCyan else CyberSurface)
      .border(1.dp, if (selected) NeonCyan else CyberSurfaceBorder, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      color = if (selected) CyberBackground else TextSecondary,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
