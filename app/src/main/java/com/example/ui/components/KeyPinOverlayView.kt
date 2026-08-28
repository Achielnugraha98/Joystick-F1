package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entities.KeyMappingEntity
import com.example.ui.theme.*

@Composable
fun KeyPinOverlayView(
  mappings: List<KeyMappingEntity>,
  selectedMappingId: Long?,
  onSelectMapping: (KeyMappingEntity) -> Unit,
  onUpdatePosition: (KeyMappingEntity, Float, Float) -> Unit,
  onAddNewAtPosition: (Float, Float) -> Unit,
  activePressedButtons: Set<String>,
  gameBackgroundPreset: String = "PUBG",
  modifier: Modifier = Modifier
) {
  var canvasSize by remember { mutableStateOf(IntSize(1, 1)) }
  var testTouchRipple by remember { mutableStateOf<Offset?>(null) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(280.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(CyberBackground)
      .border(1.2.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
      .onSizeChanged { canvasSize = it }
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = { offset ->
            testTouchRipple = offset
            val normX = (offset.x / canvasSize.width.coerceAtLeast(1)).coerceIn(0.05f, 0.95f)
            val normY = (offset.y / canvasSize.height.coerceAtLeast(1)).coerceIn(0.05f, 0.95f)
            // Check if tapped near an existing pin
            val hit = mappings.minByOrNull { m ->
              val px = m.screenNormalizedX * canvasSize.width
              val py = m.screenNormalizedY * canvasSize.height
              val dx = px - offset.x
              val dy = py - offset.y
              dx * dx + dy * dy
            }
            if (hit != null) {
              val px = hit.screenNormalizedX * canvasSize.width
              val py = hit.screenNormalizedY * canvasSize.height
              val dist = kotlin.math.sqrt((px - offset.x) * (px - offset.x) + (py - offset.y) * (py - offset.y))
              if (dist < 90f) {
                onSelectMapping(hit)
                return@detectTapGestures
              }
            }
            // Otherwise propose to add new key at this point
            onAddNewAtPosition(normX, normY)
          }
        )
      }
  ) {
    // Render Virtual Game HUD Backdrop Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Background subtle game UI layout lines
      // Mini-map circle at top right
      drawCircle(
        color = Color(0x1F00E5FF),
        radius = 36.dp.toPx(),
        center = Offset(w - 50.dp.toPx(), 45.dp.toPx())
      )
      drawCircle(
        color = Color(0x4000E5FF),
        radius = 36.dp.toPx(),
        center = Offset(w - 50.dp.toPx(), 45.dp.toPx()),
        style = Stroke(width = 1.5f)
      )

      // Virtual D-pad circle at bottom left
      drawCircle(
        color = Color(0x15FFFFFF),
        radius = 48.dp.toPx(),
        center = Offset(w * 0.18f, h * 0.72f)
      )
      drawCircle(
        color = Color(0x3300E5FF),
        radius = 48.dp.toPx(),
        center = Offset(w * 0.18f, h * 0.72f),
        style = Stroke(width = 1f)
      )

      // Virtual Fire Button at bottom right
      drawCircle(
        color = Color(0x22EF4444),
        radius = 32.dp.toPx(),
        center = Offset(w * 0.88f, h * 0.68f)
      )
      drawCircle(
        color = Color(0x66EF4444),
        radius = 32.dp.toPx(),
        center = Offset(w * 0.88f, h * 0.68f),
        style = Stroke(width = 1.5f)
      )

      // Virtual Scope Button
      drawCircle(
        color = Color(0x22A855F7),
        radius = 26.dp.toPx(),
        center = Offset(w * 0.85f, h * 0.42f)
      )

      // Center crosshair
      val cX = w / 2
      val cY = h / 2
      drawLine(
        color = Color(0x2AFFFFFF),
        start = Offset(cX - 12.dp.toPx(), cY),
        end = Offset(cX + 12.dp.toPx(), cY),
        strokeWidth = 1f
      )
      drawLine(
        color = Color(0x2AFFFFFF),
        start = Offset(cX, cY - 12.dp.toPx()),
        end = Offset(cX, cY + 12.dp.toPx()),
        strokeWidth = 1f
      )
    }

    // Render Mapped Key Pins
    mappings.forEach { mapping ->
      val isSelected = mapping.id == selectedMappingId
      val isPhysicallyPressed = activePressedButtons.contains(mapping.gamepadButton)

      val pinColor = when (mapping.mappingMode) {
        "RECOIL_SYNC" -> RecoilRed
        "TURBO_RAPID" -> HazardOrange
        "MACRO_TRIGGER" -> NeonPurple
        "ANALOG_JOYSTICK" -> NitroGreen
        "CAMERA_AIM" -> Color(0xFF38BDF8)
        else -> NeonCyan
      }

      val posX = mapping.screenNormalizedX * canvasSize.width
      val posY = mapping.screenNormalizedY * canvasSize.height

      KeyPinItem(
        mapping = mapping,
        pinColor = pinColor,
        isSelected = isSelected,
        isPressed = isPhysicallyPressed,
        posX = posX,
        posY = posY,
        onDrag = { dx, dy ->
          val newNormX = ((posX + dx) / canvasSize.width.coerceAtLeast(1)).coerceIn(0.05f, 0.95f)
          val newNormY = ((posY + dy) / canvasSize.height.coerceAtLeast(1)).coerceIn(0.05f, 0.95f)
          onUpdatePosition(mapping, newNormX, newNormY)
        },
        onClick = { onSelectMapping(mapping) }
      )
    }

    // Top instruction watermark
    Row(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .background(CyberSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
          .border(0.8.dp, CyberSurfaceBorder, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = "Tap screen to add / Drag pins to relocate",
          color = TextSecondary,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun KeyPinItem(
  mapping: KeyMappingEntity,
  pinColor: Color,
  isSelected: Boolean,
  isPressed: Boolean,
  posX: Float,
  posY: Float,
  onDrag: (Float, Float) -> Unit,
  onClick: () -> Unit
) {
  val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
    initialValue = 1f,
    targetValue = if (isPressed) 1.25f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pin_pulse"
  )

  Box(
    modifier = Modifier
      .offset(
        x = (posX - 22.dp.value * 2).dp.coerceAtLeast(0.dp),
        y = (posY - 22.dp.value * 2).dp.coerceAtLeast(0.dp)
      )
      .pointerInput(mapping.id) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          onDrag(dragAmount.x, dragAmount.y)
        }
      }
      .clickable(onClick = onClick)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(if (isSelected) 44.dp else 38.dp)
          .clip(CircleShape)
          .background(if (isPressed) pinColor else CyberSurfaceVariant)
          .border(
            width = if (isSelected) 2.dp else 1.2.dp,
            color = if (isSelected) Color.White else pinColor,
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = mapping.gamepadButton.take(4),
            color = if (isPressed) CyberBackground else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
          if (mapping.mappingMode == "TURBO_RAPID") {
            Text(
              text = "${mapping.turboTapsPerSecond}Hz",
              color = if (isPressed) CyberBackground else HazardOrange,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          } else if (mapping.mappingMode == "RECOIL_SYNC") {
            Text(
              text = "RECOIL",
              color = if (isPressed) CyberBackground else RecoilRed,
              fontSize = 7.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Small action label tag
      Box(
        modifier = Modifier
          .padding(top = 2.dp)
          .background(CyberBackground.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
          .border(0.5.dp, pinColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text(
          text = mapping.targetAction.take(10),
          color = pinColor,
          fontSize = 8.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
