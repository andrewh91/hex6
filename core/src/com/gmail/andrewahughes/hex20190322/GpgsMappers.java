package com.gmail.andrewahughes.hex20190322;

/**
 * Created by Benjamin Schulte on 03.07.2017.
 */

public class GpgsMappers {
    public static String mapToGpgsLeaderboard(String leaderboardId) {
        String gpgsId = null;
//copied from gdx game
        /*
        if (leaderboardId != null) {
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+0))
                gpgsId="CgkIsqq14N8XEAIQAQ";
            //gpgsId = "CgkIhbawkZcMEAIQAQ";//original
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
        */
//debug score board, only one, we should be unable to write to it, but should be able to call it in debug mode
        /*
        if (leaderboardId != null)
        {
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1 + 0))
                gpgsId = "CgkI5p3QhdEeEAIQAQ";
        }
        */

//release mode
        if (leaderboardId != null) {
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+0))
                gpgsId="CgkIsqq14N8XEAIQAQ";
            //gpgsId = "CgkIhbawkZcMEAIQAQ";//original
            if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+1))//10
                gpgsId = "CgkIsqq14N8XEAIQAQ";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+2))//21
                gpgsId = "CgkIsqq14N8XEAIQAg";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+3))//24
                gpgsId = "CgkIsqq14N8XEAIQBg";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+4))//36
                gpgsId = "CgkIsqq14N8XEAIQAw";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+5))//40
                gpgsId = "CgkIsqq14N8XEAIQBA";
            else  if (leaderboardId.equals(GdxGameSvcsApp.LEADERBOARD1+6))//50
                gpgsId = "CgkIsqq14N8XEAIQBQ";
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
