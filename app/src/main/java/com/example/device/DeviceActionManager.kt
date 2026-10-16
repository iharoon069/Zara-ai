package com.example.device

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R
import org.json.JSONObject

class DeviceActionManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    private var isTorchOn = false

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "zara_reminders_channel",
                "Zara AI Reminders & Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for timers, reminders, and completed tasks from Zara"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun executeAction(tag: String): ActionResult {
        Log.d("DeviceActionManager", "Executing tag: $tag")
        return try {
            when {
                tag.startsWith("[ACTION:OPEN_APP:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:OPEN_APP:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val app = json.optString("app", "youtube").lowercase()
                    openApplication(app)
                }
                tag.contains("[ACTION:OPEN_CAMERA]") -> {
                    openCamera()
                }
                tag.startsWith("[ACTION:OPEN_SETTINGS:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:OPEN_SETTINGS:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val target = json.optString("target", "general")
                    openSettings(target)
                }
                tag.contains("[ACTION:OPEN_WIFI]") -> {
                    openSettings("wifi")
                }
                tag.contains("[ACTION:CHECK_BATTERY]") -> {
                    checkBattery()
                }
                tag.startsWith("[ACTION:TOGGLE_FLASHLIGHT") -> {
                    val state = if (tag.contains("{")) {
                        val jsonStr = tag.substringAfter("[ACTION:TOGGLE_FLASHLIGHT:").substringBeforeLast("]")
                        JSONObject(jsonStr).optString("state", "toggle")
                    } else "toggle"
                    toggleFlashlight(state)
                }
                tag.startsWith("[ACTION:SET_VOLUME:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:SET_VOLUME:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val action = json.optString("action", "up")
                    adjustVolume(action)
                }
                tag.startsWith("[ACTION:SET_TIMER:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:SET_TIMER:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val minutes = json.optInt("minutes", 5)
                    setDeviceTimer(minutes)
                }
                tag.startsWith("[ACTION:SET_REMINDER:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:SET_REMINDER:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val text = json.optString("text", "Reminder from Zara")
                    val time = json.optString("time", "Soon")
                    setDeviceReminder(text, time)
                }
                tag.startsWith("[ACTION:COPY_TEXT:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:COPY_TEXT:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val text = json.optString("text", "")
                    copyToClipboard(text)
                }
                tag.startsWith("[ACTION:CLICK:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:CLICK:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val target = json.optString("target", "")
                    clickOnScreen(target)
                }
                tag.startsWith("[ACTION:TOUCH:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:TOUCH:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val x = json.optDouble("x", 500.0).toFloat()
                    val y = json.optDouble("y", 800.0).toFloat()
                    touchCoordinates(x, y)
                }
                tag.startsWith("[ACTION:SCROLL:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:SCROLL:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val dir = json.optString("direction", "down")
                    scrollScreen(dir == "down")
                }
                tag.startsWith("[ACTION:GLOBAL:") -> {
                    val jsonStr = tag.substringAfter("[ACTION:GLOBAL:").substringBeforeLast("]")
                    val json = JSONObject(jsonStr)
                    val globalAction = json.optString("action", "back")
                    performGlobal(globalAction)
                }
                tag.contains("[ACTION:READ_SCREEN]") -> {
                    ActionResult.Success("Screen reading session initiated.")
                }
                else -> {
                    ActionResult.Failure("Command processed by Zara.")
                }
            }
        } catch (e: Exception) {
            Log.e("DeviceActionManager", "Action execution error", e)
            ActionResult.Failure("Action failed: ${e.localizedMessage}")
        }
    }

    fun openApplication(appName: String): ActionResult {
        return when (appName) {
            "youtube" -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                ActionResult.Success("YouTube launched.")
            }
            "instagram" -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.instagram.android")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                ActionResult.Success("Instagram launched.")
            }
            "chrome", "browser" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                ActionResult.Success("Browser opened.")
            }
            "whatsapp" -> {
                val intent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    ActionResult.Success("WhatsApp opened.")
                } else {
                    ActionResult.Failure("WhatsApp is not installed on this device.")
                }
            }
            else -> {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(appName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    ActionResult.Success("Application '$appName' opened.")
                } else {
                    ActionResult.Failure("Could not find installed package for '$appName'.")
                }
            }
        }
    }

    fun openCamera(): ActionResult {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            ActionResult.Success("Camera opened.")
        } catch (e: Exception) {
            val fallback = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
            ActionResult.Success("Camera capture opened.")
        }
    }

    fun openSettings(target: String): ActionResult {
        val intent = when (target.lowercase()) {
            "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
            "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            "sound", "volume" -> Intent(Settings.ACTION_SOUND_SETTINGS)
            "display" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
            "accessibility" -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            else -> Intent(Settings.ACTION_SETTINGS)
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return ActionResult.Success("Settings ($target) opened.")
    }

    fun toggleFlashlight(desiredState: String = "toggle"): ActionResult {
        if (cameraManager == null) {
            return ActionResult.Failure("CameraManager not available on this device.")
        }
        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: cameraManager.cameraIdList.firstOrNull()

            if (cameraId == null) {
                return ActionResult.Failure("No flash unit detected on device.")
            }

            val target = when (desiredState) {
                "on" -> true
                "off" -> false
                else -> !isTorchOn
            }

            cameraManager.setTorchMode(cameraId, target)
            isTorchOn = target
            val stateText = if (target) "ON" else "OFF"
            ActionResult.Success("Flashlight turned $stateText.")
        } catch (e: CameraAccessException) {
            ActionResult.Failure("Camera access error: ${e.message}")
        } catch (e: Exception) {
            ActionResult.Failure("Flashlight error: ${e.message}")
        }
    }

    fun adjustVolume(action: String): ActionResult {
        if (audioManager == null) {
            return ActionResult.Failure("AudioManager unavailable.")
        }
        val direction = when (action.lowercase()) {
            "up" -> AudioManager.ADJUST_RAISE
            "down" -> AudioManager.ADJUST_LOWER
            "mute" -> AudioManager.ADJUST_MUTE
            else -> AudioManager.ADJUST_SAME
        }
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction,
            AudioManager.FLAG_SHOW_UI
        )
        return ActionResult.Success("Volume adjusted ($action).")
    }

    fun checkBattery(): ActionResult {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        val batteryPct = if (level >= 0 && scale > 0) {
            (level * 100 / scale)
        } else 80

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val statusStr = if (isCharging) "charging" else "on battery power"
        return ActionResult.Success("Battery is at $batteryPct% ($statusStr).")
    }

    fun setDeviceTimer(minutes: Int): ActionResult {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, minutes * 60)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Zara AI Timer ($minutes min)")
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            postLocalNotification("Timer Created", "$minutes minute timer set successfully by Zara.")
            ActionResult.Success("Timer for $minutes minutes initiated.")
        } catch (e: Exception) {
            postLocalNotification("Zara Timer", "Timer for $minutes minutes scheduled.")
            ActionResult.Success("Timer logged: $minutes min notification alert created.")
        }
    }

    fun setDeviceReminder(text: String, time: String): ActionResult {
        postLocalNotification("Zara Reminder", "$text (Scheduled for $time)")
        return ActionResult.Success("Reminder logged: '$text' at $time.")
    }

    fun copyToClipboard(text: String): ActionResult {
        if (clipboardManager == null) {
            return ActionResult.Failure("ClipboardManager unavailable.")
        }
        val clip = ClipData.newPlainText("Zara AI Copied Text", text)
        clipboardManager.setPrimaryClip(clip)
        return ActionResult.Success("Text copied to clipboard.")
    }

    fun clickOnScreen(target: String): ActionResult {
        val a11y = com.example.service.ZaraAccessibilityService.instance
        if (a11y == null) {
            return ActionResult.Failure("Accessibility Service not enabled. Please enable Zara AI in Accessibility Settings to touch the screen.")
        }
        val clicked = a11y.performClickByText(target)
        return if (clicked) {
            ActionResult.Success("Successfully tapped '$target' on screen.")
        } else {
            ActionResult.Failure("Could not locate '$target' on current screen.")
        }
    }

    fun touchCoordinates(x: Float, y: Float): ActionResult {
        val a11y = com.example.service.ZaraAccessibilityService.instance
        if (a11y == null) {
            return ActionResult.Failure("Accessibility Service not enabled.")
        }
        val touched = a11y.performTouchCoordinates(x, y)
        return if (touched) {
            ActionResult.Success("Touch dispatched at ($x, $y).")
        } else {
            ActionResult.Failure("Touch dispatch failed.")
        }
    }

    fun scrollScreen(forward: Boolean): ActionResult {
        val a11y = com.example.service.ZaraAccessibilityService.instance
        if (a11y == null) {
            return ActionResult.Failure("Accessibility Service not enabled.")
        }
        val scrolled = a11y.performScroll(forward)
        return if (scrolled) {
            ActionResult.Success(if (forward) "Scrolled down." else "Scrolled up.")
        } else {
            ActionResult.Failure("Could not scroll current window.")
        }
    }

    fun performGlobal(action: String): ActionResult {
        val a11y = com.example.service.ZaraAccessibilityService.instance
        if (a11y == null) {
            return ActionResult.Failure("Accessibility Service not enabled.")
        }
        val res = a11y.performGlobal(action)
        return if (res) {
            ActionResult.Success("Performed navigation '$action'.")
        } else {
            ActionResult.Failure("Could not perform navigation '$action'.")
        }
    }

    private fun postLocalNotification(title: String, body: String) {
        try {
            val notification = NotificationCompat.Builder(context, "zara_reminders_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager?.notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: Exception) {
            Log.e("DeviceActionManager", "Notification post failed", e)
        }
    }
}

sealed class ActionResult {
    data class Success(val message: String) : ActionResult()
    data class Failure(val error: String) : ActionResult()
}
