package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.MacroStep
import com.example.ui.components.parseMacroSteps
import com.example.ui.components.serializeMacroSteps
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Gamepad Keymapper Pro", appName)
  }

  @Test
  fun `macro serialization and deserialization works correctly`() {
    val steps = listOf(
      MacroStep(stepType = "TAP", targetKey = "BUTTON_Y", durationMs = 40, delayAfterMs = 30),
      MacroStep(stepType = "PRESS", targetKey = "BUTTON_L2", durationMs = 180, delayAfterMs = 20),
      MacroStep(stepType = "RELEASE", targetKey = "BUTTON_L2", durationMs = 20, delayAfterMs = 0)
    )

    val json = serializeMacroSteps(steps)
    assertNotNull(json)
    assertTrue(json.contains("BUTTON_Y"))
    assertTrue(json.contains("BUTTON_L2"))

    val parsed = parseMacroSteps(json)
    assertEquals(3, parsed.size)
    assertEquals("TAP", parsed[0].stepType)
    assertEquals("BUTTON_Y", parsed[0].targetKey)
    assertEquals(40, parsed[0].durationMs)
    assertEquals(30, parsed[0].delayAfterMs)
    assertEquals("PRESS", parsed[1].stepType)
    assertEquals("RELEASE", parsed[2].stepType)
  }
}
