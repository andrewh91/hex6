package com.gmail.andrewahughes.hex5;

import javax.crypto.spec.OAEPParameterSpec;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class OptionsHandler
{
    public StageInterface stageInterface;
     int difficulty = 1, newDifficulty = 1;
    int orientation = 1, newOrientation = 1;
    int symbol = 1, newSymbol = 1;
    int gameMode = 2, newGameMode = 2;
    int noOfHexes = 1, newNoOfHexes = 1;
    int noOfRows = 0, newNoOfRows = 0;
    int noOfColumns = 0, newNoOfColumns = 0;
    int zoomMode = 2, newZoomMode = 2;

    public OptionsHandler(StageInterface stageInterface)
    {
        this.stageInterface = stageInterface;
    }

    void click(int hexIndex, int fieldIndex)
    {
        switch (fieldIndex)
        {

            case 0: //first option screen
                switch (hexIndex)
                {
                    case 0:// cancel changes

//reset the default selected options for next time we see this menu 

                        newDifficulty = difficulty;
                        newOrientation = orientation;
                        newSymbol=symbol;
                        newGameMode = gameMode;
                        newNoOfHexes = noOfHexes;
                        newNoOfRows = noOfRows;
                        newNoOfColumns = noOfColumns;
                        newZoomMode = zoomMode;

                        OptionsHandler.this.stageInterface.setDifficulty(difficulty);
                        OptionsHandler.this.stageInterface.setOrientation(orientation);
                        OptionsHandler.this.stageInterface.setSymbol(symbol);
                        OptionsHandler.this.stageInterface.setGameMode(gameMode);
                        OptionsHandler.this.stageInterface.setNoOfHexes(noOfHexes);
                        OptionsHandler.this.stageInterface.setZoomMode(zoomMode);
                        OptionsHandler.this.stageInterface.updateOptionsGoToGameStage(0, 0,
                                0, 0, 0, 0, 0, 0,
                                0, 0, 0);
                        break;
                    case 1://return to main menu
                        OptionsHandler.this.stageInterface.goToMainStage();
                        break;
                    case 2://go to difficulty menu
                        OptionsHandler.this.stageInterface.goToDifficultyOption();
                        break;
                    case 3://go to swap orientation
                        OptionsHandler.this.stageInterface.goToSwapOrientationOption();
                        break;
                    case 4://go to symbol mode menu
                        OptionsHandler.this.stageInterface.goToSymbolOption();
                        break;
                    case 5://save changes
                        difficulty = newDifficulty;
                        orientation = newOrientation;
                        symbol=newSymbol;
                        gameMode = newGameMode;
                        noOfHexes = newNoOfHexes;
                        noOfRows = newNoOfRows;
                        noOfColumns = newNoOfColumns;
                        zoomMode = newZoomMode;


                        OptionsHandler.this.stageInterface.updateOptionsGoToGameStage(
                                noOfRows, noOfColumns,
                                orientation, 0, 0, 0, 0, zoomMode,
                                difficulty, gameMode, symbol);
                        break;
                    case 6:
                        OptionsHandler.this.stageInterface.goToGameModeOption();
                        break;
                    case 7:
                        OptionsHandler.this.stageInterface.goToNoOfHexesOption();
                        break;
                    case 8:
                        OptionsHandler.this.stageInterface.goToZoomModeOption();
                        break;


                }//end hex index 0 switch
                break;


            case 2://difficulty menu
                switch (hexIndex)
                {

                    case 0://go back to first menu
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    //select difficulty
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        newDifficulty = hexIndex;
                        OptionsHandler.this.stageInterface.setDifficulty(newDifficulty);
                        break;
                }//end hex index 2 switch

            case 3://swap orientation
            {
                switch (hexIndex)
                {

                    case 0://go back to first menu
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    case 1:
                    case 2:
                        newOrientation = hexIndex;
                        OptionsHandler.this.stageInterface.setOrientation(newOrientation);
                        break;
                }
            }
            break;
            case 4://symbols menu
                switch (hexIndex)
                {
                    case 0:
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    case 1:
                    case 2:
                        newSymbol = hexIndex;
                        OptionsHandler.this.stageInterface.setSymbol(newSymbol);
                        break;

                }
                break;


            case 6://gameMode
            {
                switch (hexIndex)
                {

                    case 0://go back to first menu
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    case 1:
                    case 2:
                        newGameMode = hexIndex;
                        OptionsHandler.this.stageInterface.setGameMode(newGameMode);
                        break;
                }
            }
            break;
            case 7://noOfHexesOptionField
            {
                switch (hexIndex)
                {

                    case 0://go back to first menu
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        newNoOfHexes = hexIndex;
                        OptionsHandler.this.stageInterface.setNoOfHexes(newNoOfHexes);
//need to derive noOfRows and columns from noOfHexes 
                        switch (newNoOfHexes)
                        {

                            case 3:
                                newNoOfRows = 1;
                                newNoOfColumns = 3;
                                break;
                            case 6:
                                newNoOfRows = 3;
                                newNoOfColumns = 2;
                                break;
                            case 10:
                                newNoOfRows = 2;
                                newNoOfColumns = 5;
                                break;
                            case 15:
                                newNoOfRows = 5;
                                newNoOfColumns = 3;
                                break;
                            case 21:
                                newNoOfRows = 3;
                                newNoOfColumns = 7;
                                break;
                            case 24:
                                newNoOfRows = 6;
                                newNoOfColumns = 4;
                                break;
                            case 36:
                                newNoOfRows = 4;
                                newNoOfColumns = 9;
                                break;
                            case 40:
                                newNoOfRows = 8;
                                newNoOfColumns = 5;
                                break;

                        }//end of noofhexes switch statement

                        break;
                }
            }
            break;

            case 8://zoomMode
            {
                switch (hexIndex)
                {

                    case 0://go back to first menu
                        OptionsHandler.this.stageInterface.goToMainOption();
                        break;
                    case 1:
                    case 2:
                        newZoomMode = hexIndex;
                        OptionsHandler.this.stageInterface.setZoomMode(newZoomMode);
                        break;
                }
            }
            break;


        }//end field index switch


    }
}
