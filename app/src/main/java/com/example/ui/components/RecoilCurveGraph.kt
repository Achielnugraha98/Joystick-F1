package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun RecoilCurveGraph(
  curveType: String,
  pullStrength: Float,
  delayMs: Int,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(130.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(CyberBackground)
      .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp))
      .padding(8.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val padL = 20f
      val padB = 20f
      val plotW = w - padL - 10f
      val plotH = h - padB - 10f

      // Draw Grid lines
      val vLines = 5
      for (i in 0..vLines) {
        val x = padL + (plotW * (i.toFloat() / vLines))
        drawLine(
          color = Color(0x1A00E5FF),
          start = Offset(x, 10f),
          end = Offset(x, h - padB),
          strokeWidth = 1f
        )
      }
      val hLines = 4
      for (i in 0..hLines) {
        val y = 10f + (plotH * (i.toFloat() / hLines))
        drawLine(
          color = Color(0x1A00E5FF),
          start = Offset(padL, y),
          end = Offset(w - 10f, y),
          strokeWidth = 1f
        )
      }

      // Calculate curve points
      val curvePath = Path()
      val fillPath = Path()
      val steps = 60
      val strengthNorm = (pullStrength / 100f).coerceIn(0.1f, 1f)

      fillPath.moveTo(padL, h - padB)

      for (i in 0..steps) {
        val t = i.toFloat() / steps // 0.0 to 1.0 (time progression)
        val x = padL + (t * plotW)

        val yNorm = when (curveType) {
          "EXPONENTIAL_DECAY" -> {
            // Strong pull initially, then levels off
            (1.0 - exp(-3.5 * t.toDouble())).toFloat() * strengthNorm
          }
          "SMOOTH_S_CURVE" -> {
            // Smooth sigmoid curve
            val s = 1.0 / (1.0 + exp(-6.0 * (t.toDouble() - 0.4)))
            (s.toFloat() * strengthNorm)
          }
          "DYNAMIC_BURST" -> {
            // Initial sharp peak with steady burst steps
            val peak = (sin(t * Math.PI * 2.5) * 0.15f).toFloat()
            (t * strengthNorm + peak).coerceIn(0f, 1f)
          }
          else -> { // LINEAR_CONSTANT
            t * strengthNorm
          }
        }

        val y = (h - padB) - (yNorm * plotH)

        if (i == 0) {
          curvePath.moveTo(x, y)
          fillPath.lineTo(x, y)
        } else {
          curvePath.lineTo(x, y)
          fillPath.lineTo(x, y)
        }
      }

      fillPath.lineTo(padL + plotW, h - padB)
      fillPath.close()

      // Draw curve area fill gradient
      drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
          colors = listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent),
          startY = 10f,
          endY = h - padB
        )
      )

      // Draw Main Curve Stroke
      drawPath(
        path = curvePath,
        color = NeonCyan,
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
      )
    }

    // Label indicators
    Row(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CyberBadge(text = curveType.replace("_", " "), color = NeonCyan)
    }

    Text(
      text = "Pull Curve vs Time (ms)",
      color = TextMuted,
      fontSize = 9.sp,
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(4.dp)
    )
  }
}
