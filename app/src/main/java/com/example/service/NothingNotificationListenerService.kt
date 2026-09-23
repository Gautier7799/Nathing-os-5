package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NothingNotificationListenerService : NotificationListenerService() {

  override fun onListenerConnected() {
    super.onListenerConnected()
    instance = this
    _isListening.value = true
    updateActiveNotifications()
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    _isListening.value = false
    instance = null
    _activePackages.value = emptySet()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    updateActiveNotifications()
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    super.onNotificationRemoved(sbn)
    updateActiveNotifications()
  }

  private fun updateActiveNotifications() {
    try {
      val notifications = activeNotifications ?: emptyArray()
      val packageMap = mutableMapOf<String, Int>()
      for (sbn in notifications) {
        val pkg = sbn.packageName ?: continue
        if (!sbn.isOngoing) {
          packageMap[pkg] = (packageMap[pkg] ?: 0) + 1
        }
      }
      _packageNotificationCounts.value = packageMap
      _activePackages.value = packageMap.keys
    } catch (_: Exception) {
      // Ignored if permission revoked
    }
  }

  companion object {
    private var instance: NothingNotificationListenerService? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _activePackages = MutableStateFlow<Set<String>>(emptySet())
    val activePackages: StateFlow<Set<String>> = _activePackages.asStateFlow()

    private val _packageNotificationCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val packageNotificationCounts: StateFlow<Map<String, Int>> = _packageNotificationCounts.asStateFlow()

    fun getNotificationCount(packageName: String): Int {
      return _packageNotificationCounts.value[packageName] ?: 0
    }
  }
}
