package com.gmail.andrewahughes.hex5;

/**
 * Created by Andrew Hughes on 14/09/2018.
 */

public interface StageInterface {
    void goToGameStage();
    void goToMainStage();
    void goToGamePauseStage(boolean gameOver);
    void goToScoreboardOption();

    void setScoreboardMode(int scoreboardMode);

    void showScoreboard();
    boolean submitScore(int score, int difficulty, int noOfHes);
    void goToGameOverStage(int noOfHexes, int score, int difficulty,int gameMode) ;
    void updateFieldSwapOrientation();
    void setLandscape();
    void setPortrait();
    void goToGameStageRefresh();
    void setScore(int score);
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

    int getScreenHeight();
    int getScreenWidth();

    void setOrientation(int orientation);
    void setGameMode(int gameMode);
    void setNoOfHexes(int noOfHexes);
    void setZoomMode(int zoomMode);

}