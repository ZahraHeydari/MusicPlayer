package com.android.player.helper

import android.content.Context
import android.content.pm.PackageManager

object ResourceHelper {
    /**
     * Get a color value from a theme attribute.
     * @param context used for getting the color.
     * @param attribute theme attribute.
     * @param defaultColor default to use.
     * @return color value
     */
    fun getThemeColor(context: Context, attribute: Int, defaultColor: Int): Int {
        var themeColor = 0
        val packageName = context.packageName
        try {
            val packageContext = context.createPackageContext(packageName, 0)
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            packageContext.setTheme(applicationInfo.theme)
            val theme = packageContext.theme
            val ta = theme.obtainStyledAttributes(intArrayOf(attribute))
            themeColor = ta.getColor(0, defaultColor)
            ta.recycle()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return themeColor
    }
}
