package com.azuyamat.mhlogs

import org.bukkit.ChatColor

fun String.format(): String? {
    return ChatColor.translateAlternateColorCodes('&', toString())
}