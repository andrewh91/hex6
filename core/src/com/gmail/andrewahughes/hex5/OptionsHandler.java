package com.gmail.andrewahughes.hex5;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class OptionsHandler {
    public StageInterface stageInterface;

    public OptionsHandler(StageInterface stageInterface)
    {
this.stageInterface=stageInterface;
    }
    void click(int hexIndex, int fieldIndex)
    {
        switch (fieldIndex)
        {
            case 0:
                switch (hexIndex)
                {
                    case 0:
                    OptionsHandler.this.stageInterface.goToMainStage();

                }
        }

    }
}
