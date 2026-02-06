package com.kammoun.api.utils.time;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static String format(long totalSeconds) {
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String formatMillis(long milliseconds) {
        if (milliseconds < 0) {
            milliseconds = 0;
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        long millis = milliseconds % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }

    public static long parse(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return 0L;
        }

        long totalSeconds = 0;
        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(timeString.toLowerCase());

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    totalSeconds += value * 86400L; // 24 * 60 * 60
                    break;
                case "h":
                    totalSeconds += value * 3600L; // 60 * 60
                    break;
                case "m":
                    totalSeconds += value * 60L;
                    break;
                case "s":
                    totalSeconds += value;
                    break;
            }
        }
        return totalSeconds;
    }
}