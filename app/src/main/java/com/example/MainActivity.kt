package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gamepad.GamepadInputManager
import com.example.ui.components.CyberBadge
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.KeymapperViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: KeymapperViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      GamepadKeymapperProTheme {
        MainAppContent(viewModel = viewModel)
      }
    }
  }

  // Intercept Gamepad Key Events (Buttons, Triggers, Dpad)
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (event != null && GamepadInputManager.onKeyDown(keyCode, event)) {
      return true
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    if (event != null && GamepadInputManager.onKeyUp(keyCode, event)) {
      return true
    }
    return super.onKeyUp(keyCode, event)
  }

  // Intercept Gamepad Motion Events (Analog Joysticks & Analog Triggers)
  override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
    if (event != null && GamepadInputManager.onGenericMotionEvent(event)) {
      return true
    }
    return super.onGenericMotionEvent(event)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: KeymapperViewModel) {
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
  val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
  val currentMappings by viewModel.currentKeyMappings.collectAsStateWithLifecycle()
  val currentRecoilConfigs by viewModel.currentRecoilConfigs.collectAsStateWithLifecycle()
  val currentMacros by viewModel.currentMacros.collectAsStateWithLifecycle()
  val currentGamepadSettings by viewModel.currentGamepadSettings.collectAsStateWithLifecycle()
  val selectedMappingId by viewModel.selectedMappingId.collectAsStateWithLifecycle()

  val inputState by viewModel.inputState.collectAsStateWithLifecycle()
  val isServiceActive by viewModel.isServiceActive.collectAsStateWithLifecycle()
  val isRecoilActive by viewModel.isRecoilGlobalActive.collectAsStateWithLifecycle()
  val isMacroActive by viewModel.isMacroGlobalActive.collectAsStateWithLifecycle()
  val notification by viewModel.notification.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(notification) {
    notification?.let {
      snackbarHostState.showSnackbar(
        message = it.message,
        duration = SnackbarDuration.Short
      )
      viewModel.dismissNotification()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = CyberBackground,
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 80.dp)
      ) { data ->
        Snackbar(
          containerColor = CyberSurfaceVariant,
          contentColor = TextPrimary,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
          Text(text = data.visuals.message, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
      }
    },
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "App Icon",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
              )
            }

            Column {
              Text(
                text = "Mapex Pro",
                color = NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (inputState.isAnyConnected) NitroGreen else HazardOrange)
                )
                Text(
                  text = if (inputState.isAnyConnected) "${inputState.primaryDeviceName} connected" else (currentProfile?.name ?: "No Profile"),
                  color = TextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Light
                )
              }
            }
          }
        },
        actions = {
          // Status indicator pill
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(CyberSurface)
              .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (inputState.isAnyConnected) NitroGreen else HazardOrange)
            )
            Text(
              text = if (inputState.isAnyConnected) "ONLINE" else "STANDBY",
              color = if (inputState.isAnyConnected) NitroGreen else TextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = CyberBackground,
          titleContentColor = TextPrimary
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = CyberNavBar,
        tonalElevation = 0.dp,
        modifier = Modifier
          .border(
            width = 1.dp,
            color = CyberSurfaceBorder,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
          )
          .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
      ) {
        val tabs = listOf(
          Triple("Remap", Icons.Default.GridView, 0),
          Triple("Studio", Icons.Default.TouchApp, 1),
          Triple("Recoil", Icons.Default.Adjust, 2),
          Triple("Macros", Icons.Default.Code, 3),
          Triple("Tester", Icons.Default.SportsEsports, 4)
        )

        tabs.forEach { (label, icon, index) ->
          val isSelected = selectedTab == index

          NavigationBarItem(
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) OnPrimaryContainer else TextSecondary
              )
            },
            label = {
              Text(
                text = label,
                color = if (isSelected) TextPrimary else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
              )
            },
            selected = isSelected,
            onClick = { viewModel.setTab(index) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = PrimaryContainer,
              selectedIconColor = OnPrimaryContainer,
              unselectedIconColor = TextSecondary,
              selectedTextColor = TextPrimary,
              unselectedTextColor = TextMuted
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> HomeScreen(
          allProfiles = allProfiles,
          activeProfile = currentProfile,
          inputState = inputState,
          isServiceActive = isServiceActive,
          isRecoilActive = isRecoilActive,
          isMacroActive = isMacroActive,
          onSelectProfile = { viewModel.selectProfile(it) },
          onCreateProfile = { name, cat, icon, desc -> viewModel.createNewProfile(name, cat, icon, desc) },
          onDeleteProfile = { viewModel.deleteProfile(it) },
          onToggleService = { viewModel.toggleService(it) },
          onToggleRecoil = { viewModel.toggleRecoilGlobal(it) },
          onToggleMacro = { viewModel.toggleMacroGlobal(it) },
          onNavigateToTab = { viewModel.setTab(it) },
          onRefreshDevices = { viewModel.refreshGamepadHardware() }
        )
        1 -> KeymapperStudioScreen(
          activeProfile = currentProfile,
          mappings = currentMappings,
          selectedMappingId = selectedMappingId,
          inputState = inputState,
          onSelectMapping = { viewModel.selectKeyMapping(it) },
          onUpdatePosition = { mapping, x, y -> viewModel.updateKeyMappingPosition(mapping, x, y) },
          onAddMapping = { btn, act, x, y, mode -> viewModel.addKeyMapping(btn, act, x, y, mode) },
          onUpdateMappingDetails = { viewModel.updateKeyMappingDetails(it) },
          onDeleteMapping = { viewModel.deleteKeyMapping(it) }
        )
        2 -> LessRecoilStudioScreen(
          recoilConfigs = currentRecoilConfigs,
          isGlobalRecoilActive = isRecoilActive,
          onToggleGlobalRecoil = { viewModel.toggleRecoilGlobal(it) },
          onUpdateRecoilConfig = { viewModel.updateRecoilConfig(it) },
          onAddRecoilConfig = { name, pull, drift, curve -> viewModel.addRecoilConfig(name, pull, drift, curve) }
        )
        3 -> MacroStudioScreen(
          macros = currentMacros,
          isGlobalMacroActive = isMacroActive,
          onToggleGlobalMacro = { viewModel.toggleMacroGlobal(it) },
          onAddMacro = { name, trig, mode, steps -> viewModel.addMacro(name, trig, mode, steps) },
          onUpdateMacro = { viewModel.updateMacro(it) },
          onDeleteMacro = { viewModel.deleteMacro(it) }
        )
        4 -> GamepadTesterScreen(
          inputState = inputState,
          settings = currentGamepadSettings,
          onUpdateSettings = { viewModel.updateSettings(it) },
          onSimulateButton = { btn, isPressed -> viewModel.simulateGamepadButton(btn, isPressed) },
          onSimulateStick = { lx, ly, rx, ry -> viewModel.simulateStick(lx, ly, rx, ry) },
          onSimulateTriggers = { lt, rt -> viewModel.simulateTriggers(lt, rt) },
          onTestHaptic = { viewModel.testHapticVibration() },
          onRefreshHardware = { viewModel.refreshGamepadHardware() }
        )
        5 -> FloatingHudScreen(
          activeProfile = currentProfile,
          isServiceActive = isServiceActive,
          onToggleService = { viewModel.toggleService(it) }
        )
      }
    }
  }
}
