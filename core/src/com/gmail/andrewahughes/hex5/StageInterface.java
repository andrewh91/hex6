package com.gmail.andrewahughes.hex5;

/**
 * Created by Andrew Hughes on 14/09/2018.
 */

public interface StageInterface {
    void goToGameStage();
    void goToMainStage();
    void goToGamePauseStage();
    void updateOptionsGoToGameStage(int newNoOfRows, int newNoOfColumns, int newPortrait1Landscape2,
                                    int newFieldPosX, int newFieldPosY, int newFieldWidth,
                                    int newFieldHeight,int zoom,int difficulty);
}