package com.gmail.andrewahughes.hex20190322;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;

public class AndroidLauncher extends AndroidApplication {
    private GpgsClient gpgsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useImmersiveMode=true;
        PlatformAndroid platformAndroid = new PlatformAndroid();
        platformAndroid.setActivity(this);
        GdxGameSvcsApp game = new GdxGameSvcsApp((Platform)platformAndroid);
        this.gpgsClient = new GpgsClient() {
            @Override
            public boolean submitEvent(String eventId, int increment) {
                return super.submitEvent(com.gmail.andrewahughes.hex20190322.GpgsMappers.mapToGpgsEvent(eventId), increment);
            }
        }.setGpgsAchievementIdMapper(new IGameServiceIdMapper<String>() {
            @Override
            public String mapToGsId(String independantId) {
                return com.gmail.andrewahughes.hex20190322.GpgsMappers.mapToGpgsAchievement(independantId);
            }
        }).setGpgsLeaderboardIdMapper(new IGameServiceIdMapper<String>() {
            @Override
            public String mapToGsId(String independantId) {
                return com.gmail.andrewahughes.hex20190322.GpgsMappers.mapToGpgsLeaderboard(independantId);
            }
        }).initialize(this, true);
        game.gsClient = this.gpgsClient;
        initialize(game, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        gpgsClient.onGpgsActivityResult(requestCode, resultCode, data);
    }

    /**
     * Created by Andrew Hughes on 08/11/2018.
     */



    /**
     * Created by Andrew Hughes on 08/11/2018.
     */


}
