package com.example.ecotracker.domain.managers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionManager {

    fun checkAndRequestNotificationsPermission(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    200
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }
}