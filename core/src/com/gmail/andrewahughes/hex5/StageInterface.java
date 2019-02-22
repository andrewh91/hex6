package com.gmail.andrewahughes.hex5;

/**
 * Created by Andrew Hughes on 14/09/2018.
 */

public interface StageInterface {
    void goToGameStage();
    void goToMainStage();
    void goToGamePauseStage();
    void goToGameOverStage();
    void updateFieldSwapOrientation();
    void setLandscape();
    void setPortrait();
    void goToGameStageRefresh();
    void setScore(int timeValueArg, int difficultyValueArg,int gameModeArg);
    void updateOptionsGoToGameStage(int newNoOfRows, int newNoOfColumns, int newPortrait1Landscape2,
                                    int newFieldPosX, int newFieldPosY, int newFieldWidth,
                                    int newFieldHeight,int zoom,int difficulty, int gameMode, int symbolType);
    void goToDifficultyOption();
    void goToSymbolOption();
    void goToMainOption();
    void goToSwapOrientationOption();
    void goToGameModeOption();
    void goToNoOfHexesOption();
    void goToZoomModeOption();

    void setDifficulty(int difficulty);
    void setSymbol(int symbol);


    void setOrientation(int orientation);
    void setGameMode(int gameMode);
    void setNoOfHexes(int noOfHexes);
    void setZoomMode(int zoomMode);

}