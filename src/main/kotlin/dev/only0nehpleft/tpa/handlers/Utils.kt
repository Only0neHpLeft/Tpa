package dev.only0nehpleft.tpa.handlers

import net.md_5.bungee.api.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {
    fun color(str: String): String {
        var str = str
        val match: Matcher = Pattern.compile("\\\\u[a-fA-F0-9]{4}").matcher(str)
        while (match.find()) {
            val code = match.group()
            str = str.replace(code, Character.toString(code.replace("\\u", "").toInt(16).toChar()))
        }

        val hexColorMatch: Matcher = Pattern.compile("(?<!&)#([a-fA-F0-9]{6})").matcher(str)
        val builder = StringBuilder()
        while (hexColorMatch.find()) {
            hexColorMatch.appendReplacement(builder, "&#" + hexColorMatch.group(1))
        }
        hexColorMatch.appendTail(builder)
        str = builder.toString()

        val hexMatch: Matcher = Pattern.compile("&#[a-fA-F0-9]{6}").matcher(str)
        while (hexMatch.find()) {
            val color = hexMatch.group()
            val hexColor = color.replace("&#", "#")
            str = str.replace(color, ChatColor.of(hexColor).toString())
        }

        return ChatColor.translateAlternateColorCodes('&', str)
    }
}