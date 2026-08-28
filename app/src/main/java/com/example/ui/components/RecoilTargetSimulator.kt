package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.RecoilConfigEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class BulletImpact(
  val x: Float, // -1f .. +1f from center
  val y: Float, // -1f .. +1f from center
  val shotNumber: Int,
  val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun RecoilTargetSimulator(
  config: RecoilConfigEntity,
  isRecoilEnabled: Boolean,
  onRecoilToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var isFiring by remember { mutableStateOf(false) }
  val bullets = remember { mutableStateListOf<BulletImpact>() }
  var totalShots by remember { mutableIntStateOf(0) }
  var shotStreak by remember { mutableIntStateOf(0) }

  // Simulation coroutine when firing is active
  LaunchedEffect(isFiring, isRecoilEnabled, config) {
    if (isFiring) {
      var shotIndex = 0
      while (isActive && isFiring && shotIndex < 35) {
        shotIndex++
        totalShots++
        shotStreak++

        // Base raw recoil physics: pulls up significantly with random horizontal drift
        val rawVerticalRecoil = (shotIndex * 0.038f).coerceAtMost(0.85f)
        val rawHorizontalRecoil = sin(shotIndex * 0.7f) * 0.12f + (Random.nextFloat() - 0.5f) * 0.08f

        // Less Recoil Compensation logic:
        val (finalX, finalY) = if (isRecoilEnabled && config.isRecoilActive) {
          // Calculate compensation pull force
          val pullPercent = (config.verticalPullStrength / 100f).coerceIn(0f, 1f)
          val horizComp = (config.horizontalDriftCompensation / 100f)
          
          val scopeFactor = when (config.selectedScope) {
            "2X" -> config.scope2xMultiplier
            "3X" -> config.scope3xMultiplier
            "4X" -> config.scope4xMultiplier
            "6X" -> config.scope6xMultiplier
            "8X" -> config.scope8xMultiplier
            else -> config.scope1xMultiplier
          }

          val compensatedPull = rawVerticalRecoil * (1f - (pullPercent * 0.95f)) * (1f / scopeFactor.coerceAtLeast(1f))
          val compensatedHoriz = (rawHorizontalRecoil - (horizComp * 0.15f)) * 0.35f

          val tightJitterX = (Random.nextFloat() - 0.5f) * 0.035f
          val tightJitterY = (Random.nextFloat() - 0.5f) * 0.035f

          Pair((compensatedHoriz + tightJitterX).coerceIn(-0.95f, 0.95f), (-compensatedPull + tightJitterY).coerceIn(-0.95f, 0.95f))
        } else {
          // Uncontrolled recoil spray
          val naturalJitterX = (Random.nextFloat() - 0.5f) * 0.12f
          val naturalJitterY = (Random.nextFloat() - 0.5f) * 0.08f
          Pair((rawHorizontalRecoil + naturalJitterX).coerceIn(-0.95f, 0.95f), (-rawVerticalRecoil + naturalJitterY).coerceIn(-0.95f, 0.95f))
        }

        bullets.add(BulletImpact(x = finalX, y = finalY, shotNumber = shotIndex))
        delay(config.stepIntervalMs.toLong().coerceIn(10L, 80L))
      }
      if (shotIndex >= 35) {
        isFiring = false
      }
    }
  }

  // Calculate Accuracy Score
  val accuracyScore = remember(bullets.size, isRecoilEnabled) {
    if (bullets.isEmpty()) 98
    else {
      val avgDistance = bullets.map { kotlin.math.sqrt(it.x * it.x + it.y * it.y) }.average()
      val score = (100 - (avgDistance * 100)).toInt().coerceIn(20, 99)
      score
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CyberSurfaceVariant, RoundedCornerShape(20.dp))
      .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
      .padding(16.dp)
  ) {
    // Header with mode toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Recoil Test Range",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          CyberBadge(
            text = if (isRecoilEnabled) "SMART NO-RECOIL ON" else "RAW RECOIL",
            color = if (isRecoilEnabled) NitroGreen else RecoilRed
          )
        }
        Text(
          text = "Weapon: ${config.weaponName} | Scope: ${config.selectedScope}",
          color = TextSecondary,
          fontSize = 12.sp
        )
      }

      // Quick Clear / Reset
      IconButton(
        onClick = {
          isFiring = false
          bullets.clear()
          shotStreak = 0
        },
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(CyberSurface)
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Clear Target",
          tint = TextSecondary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Interactive Target Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(CyberBackground)
        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cX = w / 2
        val cY = h / 2
        val maxRadius = (w.coerceAtMost(h) / 2) * 0.9f

        // Draw Target Rings
        val ringCount = 5
        for (i in 1..ringCount) {
          val r = maxRadius * (i.toFloat() / ringCount)
          val ringColor = when (i) {
            1 -> Color(0x66EF4444) // Bullseye
            2 -> Color(0x33F59E0B)
            3 -> Color(0x2200E5FF)
            else -> Color(0x15FFFFFF)
          }
          drawCircle(
            color = ringColor,
            radius = r,
            center = Offset(cX, cY)
          )
          drawCircle(
            color = Color(0x3300E5FF),
            radius = r,
            center = Offset(cX, cY),
            style = Stroke(width = 1f)
          )
        }

        // Crosshair Reticle Lines
        drawLine(
          color = Color(0x6600E5FF),
          start = Offset(cX - maxRadius, cY),
          end = Offset(cX + maxRadius, cY),
          strokeWidth = 1.2f
        )
        drawLine(
          color = Color(0x6600E5FF),
          start = Offset(cX, cY - maxRadius),
          end = Offset(cX, cY + maxRadius),
          strokeWidth = 1.2f
        )

        // Bullet impact holes
        bullets.forEach { bullet ->
          val hitX = cX + (bullet.x * maxRadius)
          val hitY = cY + (bullet.y * maxRadius)

          // Bullet Outer Glow
          drawCircle(
            color = if (isRecoilEnabled) NeonCyan.copy(alpha = 0.5f) else RecoilRed.copy(alpha = 0.5f),
            radius = 6f,
            center = Offset(hitX, hitY)
          )
          // Bullet Core
          drawCircle(
            color = if (isRecoilEnabled) Color.White else HazardOrange,
            radius = 3.5f,
            center = Offset(hitX, hitY)
          )
        }
      }

      // Realtime Accuracy & Spread Stats Pill
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .background(CyberSurface.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
          .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Column {
          Text(
            text = "Tightness: $accuracyScore%",
            color = if (accuracyScore > 80) NitroGreen else HazardOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Hits: ${bullets.size}/35",
            color = TextSecondary,
            fontSize = 10.sp
          )
        }
      }

      // Fire Control Overlay Button
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(12.dp)
      ) {
        Button(
          onClick = {
            if (isFiring) {
              isFiring = false
            } else {
              bullets.clear()
              isFiring = true
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isFiring) RecoilRed else NeonCyan,
            contentColor = CyberBackground
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = if (isFiring) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = "Trigger Simulation",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isFiring) "STOP SPRAY" else "HOLD TO FIRE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Comparison summary metrics
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      MetricCard(
        title = "Vertical Control",
        value = "${config.verticalPullStrength.toInt()}%",
        subtitle = "Pull-Down Force",
        color = NeonCyan,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Horizontal Drift",
        value = "${config.horizontalDriftCompensation.toInt()}%",
        subtitle = "Drift Stabilizer",
        color = NeonPurple,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Less Recoil",
        value = if (isRecoilEnabled) "ACTIVE" else "OFF",
        subtitle = "Tap to Switch",
        color = if (isRecoilEnabled) NitroGreen else RecoilRed,
        isClickable = true,
        onClick = { onRecoilToggle(!isRecoilEnabled) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier,
  isClickable: Boolean = false,
  onClick: () -> Unit = {}
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurface)
      .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
      .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(10.dp)
  ) {
    Column {
      Text(text = title, color = TextMuted, fontSize = 10.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, color = TextSecondary, fontSize = 9.sp)
    }
  }
}
