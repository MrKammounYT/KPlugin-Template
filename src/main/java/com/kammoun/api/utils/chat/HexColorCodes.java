package com.kammoun.api.utils.chat;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColorCodes {
    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public String FormatHex(String msg){
        return colorHex(msg);
    }
    public String uncodeHex(String msg){
        return ChatColor.stripColor(msg);
    }
    private static String colorHex(String s) {
        s = ChatColor.translateAlternateColorCodes(ChatColor.COLOR_CHAR, s);
        s = findAndReplaceRegex("!#[0-9,a-f,A-F]{6}", s);
        s = findAndReplaceRegex("&#[0-9,a-f,A-F]{6}", s);
        s = ChatColor.translateAlternateColorCodes('&', s);
        //System.out.println("String is: " + s);
        return s;
    }

    private static String findAndReplaceRegex(String regex, String input) {
        int i = 0;
        ArrayList<String> matches = new ArrayList<>();
        ArrayList<ChatColor> colorSet = new ArrayList<>();
        Matcher patternMatcher = Pattern.compile(regex).matcher(input);
        while(patternMatcher.find()) {
            matches.add(patternMatcher.group());
        }
        for(String match : matches) {
            colorSet.add(ChatColor.of(match.substring(1)));
        }
        Iterator<String> matchIterator = matches.iterator();
        Iterator<ChatColor> colorIterator = colorSet.iterator();
        while(matchIterator.hasNext() && colorIterator.hasNext()) {
            input = input.replaceFirst(matchIterator.next(), colorIterator.next().toString());
        }
        return input;
    }

    /***
     * Rainbowify's messages
     * @param message The message to be rainbowified
     * @return rainbowified message
     */
    public static String rainbowText(String message) {
        StringBuilder finalizedMessage = new StringBuilder();
        int hue = 0;
        for(int messageChar = 0; messageChar < message.toCharArray().length; messageChar++) {
            Color color = Color.getHSBColor(((float)hue/360), 1f, 1f);
            String red = Integer.toHexString(color.getRed());
            String green = Integer.toHexString(color.getGreen());
            String blue = Integer.toHexString(color.getBlue());

            String hexColor = "!#" + (red.length() <= 2 ? StringUtils.repeat( "0",2-red.length()) + red : red) +
                    (green.length() <= 2 ? StringUtils.repeat( "0",2-green.length()) + green : green) +
                    (blue.length() <= 2 ? StringUtils.repeat( "0",2-blue.length()) + blue : blue);
            finalizedMessage.append(hexColor).append(message.toCharArray()[messageChar]);
            hue += (360/message.toCharArray().length);
        }
        return colorHex(finalizedMessage.toString());
    }

    /***
     * Makes the message a gradient from the startColor to endColor
     * @param message message to add gradient to
     * @param startColor color that text should begin with
     * @param endColor color that text should end with
     * @return gradient formatted message
     */
    public static String gradientText(String message, Color startColor, Color endColor) {
        StringBuilder finalizedMessage = new StringBuilder();
        for(int messageChar = 0; messageChar < message.toCharArray().length; messageChar++) {
            float ratio = (float) messageChar / (float) message.toCharArray().length;
            int red = (int) (endColor.getRed() * ratio + startColor.getRed() * (1 - ratio));
            int green = (int) (endColor.getGreen() * ratio + startColor.getGreen() * (1 - ratio));
            int blue = (int) (endColor.getBlue() * ratio + startColor.getBlue() * (1 - ratio));
            Color stepColor = new Color(red, green, blue);
            String redHex = Integer.toHexString(stepColor.getRed());
            String greenHex = Integer.toHexString(stepColor.getGreen());
            String blueHex = Integer.toHexString(stepColor.getBlue());

            String hexColor = "!#" + (redHex.length() <= 2 ? StringUtils.repeat( "0",2-redHex.length()) + redHex : redHex) +
                    (greenHex.length() <= 2 ? StringUtils.repeat( "0",2-greenHex.length()) + greenHex : greenHex) +
                    (blueHex.length() <= 2 ? StringUtils.repeat( "0",2-blueHex.length()) + blueHex : blueHex);
            finalizedMessage.append(hexColor).append(message.toCharArray()[messageChar]);
        }
        return colorHex(finalizedMessage.toString());
    }

    /***
     * Parses a hex code into a {@link java.awt.Color}
     * @param hexColor hex color string
     * @return color
     */
    public static Color parseHexColor(String hexColor) {
        hexColor = hexColor.replaceAll("&", "");
        if(!hexColor.startsWith("#")) {
            hexColor = "#" + hexColor;
        }
        return ChatColor.of(hexColor).getColor();
    }
}
