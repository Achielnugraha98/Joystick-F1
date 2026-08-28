package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CyberBadge(
  text: String,
  color: Color = NeonCyan,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(CyberSurfaceBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
      .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 2.dp)
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 0.5.sp
    )
  }
}

