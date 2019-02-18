package com.gmail.andrewahughes.hex5;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class OptionsHandler {
    public StageInterface stageInterface;
public int difficulty=1, newDifficulty=0;
    public OptionsHandler(StageInterface stageInterface)
    {
this.stageInterface=stageInterface;
    }
    void click(int hexIndex, int fieldIndex)
    {
        switch (fieldIndex)
        {

            case 0: //first option screen
                switch (hexIndex)
                {
                    case 0:// cancel changes
                    OptionsHandler.this.stageInterface.setDifficulty(difficulty);

                    OptionsHandler.this.stageInterface.updateOptionsGoToGameStage(0,0,
                            0,0,0,0,0,0,
                            0,0,0);
                    break;
                    case 1://return to main menu
                        OptionsHandler.this.stageInterface.goToMainStage();
                        break;
                    case 2://go to difficulty menu
                        OptionsHandler.this.stageInterface.goToDifficultyOption();
                        break;

                    case 5://save changes
difficulty=newDifficulty;
                        OptionsHandler.this.stageInterface.updateOptionsGoToGameStage(0,0,
                                0,0,0,0,0,0,
                                difficulty,0,0);
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
                    case 1:case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9:case 10:case 11:case 12:
newDifficulty=hexIndex;
OptionsHandler.this.stageInterface.setDifficulty(newDifficulty);
                    break;
                }//end hex index 2 switch
        }//end field index switch











    }
}
