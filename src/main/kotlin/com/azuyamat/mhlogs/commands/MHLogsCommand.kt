package com.azuyamat.mhlogs.commands

import com.azuyamat.mhlogs.PluginHolder
import com.azuyamat.mhlogs.format
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MHLogsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("mhlogs", ignoreCase = true)) {
            if (args.isEmpty() || args[0].lowercase() == "help"){
                val linkText = TextComponent("&bhttps://mhlogs.com".format())
                linkText.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://mhlogs.com")
                val message = TextComponent("&7[&bMHLOGS&7] ➤ &7Use &b/mhlogs export &7to export your logs ".format())
                message.addExtra(linkText)
                sender.spigot().sendMessage(message)
                return true
            }
            if (args[0].lowercase() == "export"){
                sender.sendMessage("&7[&bMHLOGS&7] ➤ &7Exporting logs...".format())
                val timestamp = PluginHolder.instance.retrieveAndSendLogsToAPI()
                if (timestamp === null){
                    sender.sendMessage("&cAn error occurred with MHLogs".format())
                    return true
                }
                val linkText = TextComponent("&bhttps://mhlogs.com/log/$timestamp".format())
                linkText.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://mhlogs.com/log/$timestamp")
                val message = TextComponent("&7[&bMHLOGS&7] ➤ &7View your logs ".format())
                message.addExtra(linkText)
                sender.spigot().sendMessage(message)
            }
            return true
        }
        return false
    }
}
