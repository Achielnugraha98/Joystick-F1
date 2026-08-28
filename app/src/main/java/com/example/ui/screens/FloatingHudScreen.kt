package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.GameProfileEntity
import com.example.ui.components.CyberBadge
import com.example.ui.theme.*

@Composable
fun FloatingHudScreen(
  activeProfile: GameProfileEntity?,
  isServiceActive: Boolean,
  onToggleService: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCrosshairOverlay by remember { mutableStateOf(true) }
  var crosshairColorIndex by remember { mutableIntStateOf(0) }
  var showFpsCounter by remember { mutableStateOf(true) }
  var isLowLatencyMode by remember { mutableStateOf(true) }
  var isGyroscopeAssist by remember { mutableStateOf(false) }

  val crosshairColors = listOf(NeonCyan, NitroGreen, RecoilRed, HazardOrange, Color.White)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
  ) {
    // Top Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Floating HUD & In-Game Assistant",
              color = TextPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            CyberBadge(text = "BOOSTER", color = NitroGreen)
          }
          Text(
            text = "Floating icon overlay, custom crosshair & ultra-low latency",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        Switch(
          checked = isServiceActive,
          onCheckedChange = onToggleService,
          colors = SwitchDefaults.colors(
            checkedThumbColor = CyberBackground,
            checkedTrackColor = NitroGreen,
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurface
          )
        )
      }
    }

    // In-Game Floating Preview
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(CyberSurfaceVariant)
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val cX = size.width / 2
          val cY = size.height / 2

          // Background subtle tactical grid
          drawLine(color = Color(0x15FFFFFF), start = Offset(0f, cY), end = Offset(size.width, cY), strokeWidth = 1f)
          drawLine(color = Color(0x15FFFFFF), start = Offset(cX, 0f), end = Offset(cX, size.height), strokeWidth = 1f)

          if (showCrosshairOverlay) {
            val color = crosshairColors[crosshairColorIndex]
            val gap = 6.dp.toPx()
            val len = 14.dp.toPx()
            // Custom Pro Crosshair Reticle
            drawLine(color = color, start = Offset(cX - gap - len, cY), end = Offset(cX - gap, cY), strokeWidth = 2f)
            drawLine(color = color, start = Offset(cX + gap, cY), end = Offset(cX + gap + len, cY), strokeWidth = 2f)
            drawLine(color = color, start = Offset(cX, cY - gap - len), end = Offset(cX, cY - gap), strokeWidth = 2f)
            drawLine(color = color, start = Offset(cX, cY + gap), end = Offset(cX, cY + gap + len), strokeWidth = 2f)
            drawCircle(color = color, radius = 2f, center = Offset(cX, cY))
          }
        }

        // Floating Ball Widget Simulation in Corner
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(14.dp)
            .size(42.dp)
            .clip(CircleShape)
            .background(NeonCyanGlow)
            .border(1.5.dp, NeonCyan, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.SportsEsports, contentDescription = "Floating Ball", tint = NeonCyan, modifier = Modifier.size(22.dp))
        }

        // In-game FPS / Ping Badge Simulation
        if (showFpsCounter) {
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(12.dp)
              .background(CyberBackground.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
              .border(0.8.dp, NitroGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
              Text("120 FPS", color = NitroGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("•", color = TextMuted, fontSize = 10.sp)
              Text("16 ms", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Text(
          text = "Floating Game Assistant Overlay Simulation",
          color = TextMuted,
          fontSize = 11.sp,
          modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
        )
      }
    }

    // Assistant Customization Options
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text("Tactical Crosshair Customizer", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Show Screen Center Crosshair", color = TextSecondary, fontSize = 12.sp)
          Switch(
            checked = showCrosshairOverlay,
            onCheckedChange = { showCrosshairOverlay = it },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberBackground, checkedTrackColor = NeonCyan)
          )
        }

        if (showCrosshairOverlay) {
          Text("Crosshair Reticle Color:", color = TextSecondary, fontSize = 11.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            crosshairColors.forEachIndexed { index, color ->
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(color)
                  .border(
                    width = if (crosshairColorIndex == index) 2.dp else 0.dp,
                    color = Color.White,
                    shape = CircleShape
                  )
                  .clickable { crosshairColorIndex = index }
              )
            }
          }
        }
      }
    }

    // Performance & Optimization Toggles
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurfaceVariant, RoundedCornerShape(16.dp))
          .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text("Performance & Input Engine", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("FPS & Input Latency HUD", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("Real-time framerate and polling benchmark", color = TextSecondary, fontSize = 10.sp)
          }
          Switch(
            checked = showFpsCounter,
            onCheckedChange = { showFpsCounter = it },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberBackground, checkedTrackColor = NitroGreen)
          )
        }

        Divider(color = CyberSurfaceBorder, thickness = 0.8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Ultra-Low Latency Kernel Bypass", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("Minimizes touch injection lag to under 1ms", color = TextSecondary, fontSize = 10.sp)
          }
          Switch(
            checked = isLowLatencyMode,
            onCheckedChange = { isLowLatencyMode = it },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberBackground, checkedTrackColor = NeonCyan)
          )
        }

        Divider(color = CyberSurfaceBorder, thickness = 0.8.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Gamepad Gyroscope Motion Aiming", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("Translates device tilt / gyro into camera look", color = TextSecondary, fontSize = 10.sp)
          }
          Switch(
            checked = isGyroscopeAssist,
            onCheckedChange = { isGyroscopeAssist = it },
            colors = SwitchDefaults.colors(checkedThumbColor = CyberBackground, checkedTrackColor = NeonPurple)
          )
        }
      }
    }
  }
}
