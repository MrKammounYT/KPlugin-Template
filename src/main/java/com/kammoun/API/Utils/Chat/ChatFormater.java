package com.kammoun.API.Utils.Chat;

import org.bukkit.ChatColor;

public class ChatFormater {

    private static final HexColorCodes HEX_COLOR_CODES = new HexColorCodes();

    public static HexColorCodes getHexColorCodes() {
        return HEX_COLOR_CODES;
    }

    public static String Color(String toColor){
        if(toColor == null || toColor.isEmpty())return toColor;
        return ChatColor.translateAlternateColorCodes('&', HEX_COLOR_CODES.FormatHex(toColor));
    }

}
