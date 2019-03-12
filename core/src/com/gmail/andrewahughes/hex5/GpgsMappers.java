package com.gmail.andrewahughes.hex5;

/**
 * Created by Benjamin Schulte on 03.07.2017.
 */

public class GpgsMappers {
    public static String mapToGpgsLeaderboard(String leaderboardId) {
        String gpgsId = null;

        if (leaderboardId != null) {
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+0))
            gpgsId = "CgkIhbawkZcMEAIQAQ";
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+1))
                gpgsId = "CgkIhbawkZcMEAIQAg";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+2))
                gpgsId = "CgkIhbawkZcMEAIQAw";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+3))
                gpgsId = "CgkIhbawkZcMEAIQBA";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+4))
                gpgsId = "CgkIhbawkZcMEAIQBQ";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+5))
                gpgsId = "CgkIhbawkZcMEAIQBg";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+6))
                gpgsId = "CgkIhbawkZcMEAIQBw";
        }

        return gpgsId;
    }

    public static String mapToGpgsAchievement(String achievementId) {
        String gpgsId = null;

        if (achievementId != null) {
            if (achievementId.equals(GdxGameSvcsApp.ACHIEVEMENT1))
                gpgsId = "CgkIu46Sr-8fEAIQAg";
        }

        return gpgsId;
    }

    public static String mapToGpgsEvent(String eventId) {
        String gpgsId = null;

        if (eventId != null) {
            if (eventId.equals(GdxGameSvcsApp.EVENT1))
                gpgsId = "CgkIu46Sr-8fEAIQAQ";
        }

        return gpgsId;
    }
}
