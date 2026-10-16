package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ZaraAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isServiceActive = true
        Log.d("ZaraAccessibility", "Zara Accessibility Service Connected")

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        try {
            val root = rootInActiveWindow ?: return
            val extracted = StringBuilder()
            var clickableCount = 0

            fun traverse(node: AccessibilityNodeInfo, depth: Int) {
                if (depth > 12) return

                val text = node.text?.toString()?.trim()
                val desc = node.contentDescription?.toString()?.trim()
                val isClickable = node.isClickable
                val className = node.className?.toString()?.substringAfterLast(".")

                if (!text.isNullOrEmpty() || !desc.isNullOrEmpty()) {
                    val label = when {
                        !text.isNullOrEmpty() -> text
                        else -> desc
                    }
                    val type = if (isClickable) "[Button: $label]" else label
                    extracted.append(type).append(" | ")
                }

                if (isClickable) clickableCount++

                for (i in 0 until node.childCount) {
                    node.getChild(i)?.let { child ->
                        traverse(child, depth + 1)
                    }
                }
            }

            traverse(root, 0)

            val textResult = extracted.toString().trimEnd(' ', '|')
            if (textResult.isNotBlank()) {
                currentScreenSummary = textResult.take(1500)
                currentInteractiveCount = clickableCount
            }
        } catch (e: Exception) {
            Log.e("ZaraAccessibility", "Error processing event", e)
        }
    }

    override fun onInterrupt() {
        Log.d("ZaraAccessibility", "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
            isServiceActive = false
        }
    }

    companion object {
        var instance: ZaraAccessibilityService? = null
            private set
        var isServiceActive: Boolean = false
            private set
        var currentScreenSummary: String = "No screen data captured yet. Enable Accessibility in Android Settings to activate."
        var currentInteractiveCount: Int = 0

        fun getScreenContext(): String {
            return if (isServiceActive) {
                "Active Screen Elements ($currentInteractiveCount interactive controls):\n$currentScreenSummary"
            } else {
                "Accessibility Service is currently OFF. Prompt user to enable Zara AI in Accessibility Settings."
            }
        }
    }
}
