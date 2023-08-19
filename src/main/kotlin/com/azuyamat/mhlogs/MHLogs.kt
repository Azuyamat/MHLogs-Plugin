package com.azuyamat.mhlogs

import com.azuyamat.mhlogs.commands.MHLogsCommand
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.logging.Level

class MHLogs : JavaPlugin() {


    override fun onEnable() {
        PluginHolder.instance = this
        val mhlogsCommandExecutor = MHLogsCommand()
        getCommand("mhlogs")?.executor = mhlogsCommandExecutor
    }

    fun retrieveAndSendLogsToAPI(): String? {
        val server = server
        val logsFolder = File(server.worldContainer, "logs")
        if ((!logsFolder.isDirectory) || (logsFolder.listFiles() == null)){
            server.logger.warning("MHLogs couldn't find a logs folder. The plugin won't work as desired.")
            return null
        }
        for (listFile in logsFolder.listFiles()!!) {
            if (listFile.isFile && listFile.name.endsWith(".log")) {
                server.logger.log(Level.INFO, "Found log file: "+listFile.name)
                val logContent = listFile.readText(StandardCharsets.UTF_8)
                return sendPOST(logContent)
            }
        }
        return null
    }

    private val gson = Gson()

    private fun sendPOST(content : String = ""): String? {
        try{
            val url = URL("https://mhlogs.com/api/logs")

            println("Content size: ${content.length}")
            val requestBody = mapOf(
                "content" to content,
                "username" to "mhlogs",
                "userId" to "mhlogs"
            )
            val requestBodyJson = gson.toJson(requestBody)

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Content-Length", requestBodyJson.length.toString())
                doOutput = true
                val requestBodyBytes = requestBodyJson.toByteArray(StandardCharsets.UTF_8)

                outputStream.use { os ->
                    os.write(requestBodyBytes)
                }
                println("\nSent 'GET' request to URL : $url Response Code : $responseCode")

                var jsonResponse: JsonObject? = null;

                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        println(line)
                        jsonResponse = JsonParser.parseString(line).asJsonObject
                    }
                }
                return jsonResponse?.get("timestamp")?.asString
            }
        } catch (error : IOException){
            println("That's not cool! An error occurred")
        }
        return null
    }
}
