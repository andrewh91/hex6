package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Andrew Hughes on 13/09/2018.
 */

public class GameStage extends Stage
{
    private boolean visible = false;
    private boolean pause = false;
    private ShapeRenderer renderer = new ShapeRenderer();
    SpriteBatch spriteBatch = new SpriteBatch();
    SpriteBatch hudBatch = new SpriteBatch();
    public StageInterface stageInterface;
    HexWideField hexWideField;
    HexTallField hexTallField;
    Database database;
    Viewport viewport;

    Viewport hudViewport;

    int defaultCamPosX;
    int defaultCamPosY;

    float defaultCamZoom;

    int newCamPosX;
    int newCamPosY;

    float newCamZoom;

    //whether we need to run the camera movement logic
    boolean camSnapOutdated = false;
    //the period of time over which the camera movements will happen
    float camSnapTimePeriod = 0.1f;
    //the current progress through the camera transition
    float camSnapTime = 0f;
    //the speedConstant to move the camera
    float camSnapSpeedX;
    float camSnapSpeedY;
    float camSnapSpeedZ;


    BitmapFont font = new BitmapFont();
    String text = new String();
    String state = new String();
    //ints to store the selected hex’s index position in the field of hexes
    //and the selected sector which will be 0 - 5
    int selectedSector = -1, selectedHex = -1;
    int selectedSector2 = -1, selectedHex2 = -1;
    //proposedSelectedHex and symbol stores the touched hex and symbol in the touch logic which is used to help figure out which hex is actually selected in the case of hexes bounding boxes overlapping
    int proposedSelectedHex = -1;
    int proposedSelectedSector = -1;
    //holds the number of selected hexes, will be 0,1 or 2, what a touch does changes depending on how many hexes are selected
    int noOfSelected = 0;
    //an array list to hold the index values of the hexes adjacent to the given hex
    ArrayList<Integer> adjacentArray = new ArrayList<Integer>();


    //score is incremented when matching when target score is reached we end some game modes
    int score = 0, targetScore = 10;
    int scoreFieldMode = 0, targetScoreFieldMode = 0, displayScore = 0;
    boolean gameOver = false;
    //timer is incremented when not paused, timerFinal was just a way to stop the timer when we get gameover
    float timer = 0f, timerFinal = 0f, penaltyTime = 5f;
    //green and red are used when drawing the coloured background, they are altered when scoring to change background colour
    float red = 0.86f;
    float green = 0.65f;
    float blue = 0.22f;
    float bkgdr = 0.10f, bkgdg = 0.70f, bkgdb = 0.00f;

    ArrayList<Bee> beeArray = new ArrayList<Bee>();
    //recommended number of rows for portrait mode using hexwide in screens with 16:9 aspect ratio
//can be found using noOfRows =roundup(noOfColumns*2-noOfColumns/2)
//recommended number of columns for landscape mode using hexTall in screens with 16:9 aspect ratio
// can be found using noOfColumns = noOfRows * 2 + 1
/*
Handy list in order of total hexes
Col’s  	rows	total	orientation
1	2	2	portrait
3	1	3	landscape
2	3	6	portrait
5	2	10	landscape
3	5	15	portrait
7	3	21	landscape
4	6	24	portrait
9	4	36	landscape
5	8	40	portrait
6	9	54	portrait
11	5	55	landscape
7	11	77	portrait
13	6	78	landscape
8	12	96	portrait
15	7	105	landscape
9	14	126	portrait
*/
    //default values for the field
    public int noOfRows = 6, noOfColumns = 4;
    //prefer not to use Boolean for the orientation, when changing the options in game we can use 0 to show no change, 1 portrait 2 landscape
    //game mode is 0 by default, this is the field mode, many hexes on screen, hexes disappear when matched, game mode 1 would be singles mode, 2 hexes on screen, symbols are replaced when matching
    //field width and height are just defaults, they will be changed later
    int portrait1Landscape2 = 1, fieldPosX = 50, fieldPosY = 50, fieldWidth = 620, fieldHeight = 1180, gameMode = 2, symbolType = 3, noOfSymbols = 31;


    //haven’t properly implemented the zoom mode yet, the alternative is called quickMode – or just zoomSelectionMode false
// – zoomMode basically has an extra touch when trying to match, you select 2 hexes ( that’s 2 touches) then third touch will
//test symbol, quick mode sort of combines the 2nd and 3rd touch, so there is only 2 touches, 1 to select first hex, 2 to select
//second hex as well as symbol and compare symbol so you have to be careful what you’re touching, which would be hard if the
//camera was zoomed out too far which is why zoom mode is more useful in large hexfields
    boolean zoomSelectionMode = true;
    int hexPairPosX, hexPairPosY;
    //difficulty will remove some of the symbols so there are less wrong answers, and less to look at when
    //trying to compare matches, high difficulty value will make it more easy
    int difficulty = 0;

    //arrayList to store which symbols are false matches – we use this to remove symbols to make the difficulty easier
    ArrayList<Integer> nonMatchingSymbols = new ArrayList<Integer>();
    ArrayList<Integer> nonMatchingSymbolsHex1 = new ArrayList<Integer>();
    ArrayList<Integer> nonMatchingSymbolsHex2 = new ArrayList<Integer>();
    boolean ready = false;
    Actor readyButton;

    FlowerPosList flowerPosList;
    int matchedFlowerIndex;


    public GameStage(Viewport viewport, final StageInterface stageInterface, int portrait)
    {
        super(viewport);
        this.viewport = viewport;
        hudViewport = new StretchViewport(stageInterface.getScreenWidth(), stageInterface.getScreenHeight());
        hudViewport.setScreenBounds(0, 0, stageInterface.getScreenWidth(), stageInterface.getScreenHeight());

        this.portrait1Landscape2 = portrait;
        //the font isn’t really final, the symbols will be replaced with pictures eventually
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        //create the field with the given default values which we set above
        fieldWidth = stageInterface.getScreenWidth() - fieldPosX * 2;
        fieldHeight = stageInterface.getScreenHeight() - fieldPosY * 2;
        updateField(noOfRows, noOfColumns, portrait, fieldPosX, fieldPosY, fieldWidth,
                fieldHeight, gameMode, symbolType);
        //the stage interface is used to go from one stage to another and pass variables over
        this.stageInterface = stageInterface;

        createReadyButton();
    }


    //complicated method which handles the touch events. The hex field is a field of tessellating hexes, but we rely on
    //bounding boxes – rectangles – for collision detection. The bounding boxes overlap each other and in libgdx the most
    //recently created hex will overlap others and will block older hexes from receiving any touch that is in both bounding
    //boxes. It’s therefore important to note that these wide hexes are created starting with the bottom left, working to
    //the right then back to the left and up one row then off to the right again etc. So this method will use logic to
    //confirm which hex was actually touched. We can use maths to figure out the index number of the adjacent hexes if we
    //know the number of rows and columns, and if it’s portrait or landscape
    //in fact this method is for portrait and wide hexes only
    public void setSelected(int index, int sector, boolean visible)
    {
//index will refer to the hex index in the field, sector will be the proposed sector we will confirm if the proposed sector
//is the correct sector below

        if (visible == false)//if we touch a hex that is invisible, reset selection
        {
            resetSelection();
        } else
        {
            //we need totalHexes, number of columns and rows to figure out what the adjacent hexes are and if the current hexes
            //is on the extreme edges of the field
            int totalHexes = noOfColumns * noOfRows;

            //overlap logic
            if (sector > 9)
            //in the case of an overlap in the touch logic the sector would have 10 added to it's value to indicate that it is the hex's neighbour that is selected.
            {
                //remember we are in portrait mode with wide hexes
                //one of 4 adjacent hexes could be selected, work out which one it is with the selected sector
                //we also need to see if we are on the extreme edges of the field because then there might not be 4 adjacent hexes
                //but that depends on if we are in an odd or even column because of the way hexes tessellate they are sort of staggered
                //meaning even if the hex is on the top row for example it might still have hexes above and to the left,
                // and above and to the right
                //10 means the hex above and to the left of the given hex is selected,
                //12 is above and to the right
                //13 below right
                //15 below left

                //if hex is in an even column (the left most column is 0 which is even)
                //above left would be hex index -1
                //above right would be hex index +1
                //below right would be hex index - noOfColumns +1
                //below left would be hex index - noOfColumns -1
                if ((index % noOfColumns) % 2 == 0)
                {
                    if (sector == 10)
                    {
                        //if not on left edge - doesnt matter if its on the top because even column
                        if (index % noOfColumns > 0)
                        {
                            proposedSelectedHex = index - 1;
                        }
                    }
//we don’t need to do one for sector 12 because if there was a hex above and to the right then that touch would have been
//captured by that hex since it would be more recent
                    else if (sector == 13)
                    {
                        //if not on the right edge AND not on the bottom
                        if (index % noOfColumns < noOfColumns - 1 && index - noOfColumns >= 0)
                        {
                            proposedSelectedHex = index - noOfColumns + 1;
                        }
                    } else if (sector == 15)
                    {
                        //if not on left edge AND not on bottom edge
                        if (index % noOfColumns > 0 && index - noOfColumns >= 0)
                        {
                            proposedSelectedHex = index - noOfColumns - 1;
                        }
                    }
                }
                //else if the given hex is in an odd column;
                //above left would be hex index + noOfColumns -1
                //above right would be hex index + noOfColumns +1
                //below right would be hex index +1
                //below left would be hex index -1
                //if the given hex is in an even column;
                else
                {
                    //only need one of these for odd columns most of the surrounding overlapping hexes are created more recently so they will handle touch, don’t need logic for sector 10, 12, or 13, just for 15
                    if (sector == 15)
                    {
                        //if not on left edge - on wide field odd column adjacents can go below left and below right
                        if (index % noOfColumns > 0)
                        {
                            proposedSelectedHex = index - 1;// bottom left
                        }
                    }
                }

                //  because the touch point is in the adjacent hex the touched sector will actually be the opposing sector to the given
                //  hex, so if it was 12 if should become 5 so translate that like so
                proposedSelectedSector = (sector + 3 - 10) % 6;

            }//end overlap logic
//the above was just for if the sector was over 9 which would have indicated there was a potential overlap, the below is for
//if there is no overlap
            else
            {
//since there is no overlap we know the given sector and hex are correct
                proposedSelectedSector = sector;
                proposedSelectedHex = index;
            }
//now we know for sure which sector and hex we are dealing with
//how we process this touch event will depend on how many hexes are already selected
//if no hexes are selected yet
            if (noOfSelected == 0)
            {
//select the touched hex
                selectFirstHex();
            }//end if(noOfSelected==0)
            //if we have already selected one hex
            else if (noOfSelected == 1)
            {
//if the hex we are touching is highlighted then it must be adjacent to the first selected hex so select this one too
                if (hexWideField.hexWideArray[proposedSelectedHex].highlight)
                {
                    selectSecondHex();
//this is where quick mode and zoom mode differ, if we are using quick mode we select the second hex and compare the touched
//symbol at the same time, we will take the touched symbol on the second hex and compare it will all symbols on the first hex
                    if (!zoomSelectionMode)
                    {
                        if (compareAll(selectedHex2, selectedHex, proposedSelectedSector))
                        {
//TODO need to complete this, need to add increase score at the least and test it
                            resetSelection();
                        }
                    }

                }//end if highlighted
//if the touched hex is not highlighted, see if it’s selected
                else if (hexWideField.hexWideArray[proposedSelectedHex].select)
                {
//if we touch a hex that is already selected and we only have one selected the we should unselect that hex
                    resetSelection();

                }//end if selected
//if we touch a hex that is not selected and not highlighted then we’ve just touched some random hex, unselect everything
                else //not highlighted and not selected
                {
                    resetSelection();
                }
            }//end else if(noOfSelected==1)
//if we have 2 hexes selected already
            else if (noOfSelected == 2)
            {
                //if the hex we touched is one of the two that is already selected
                if (hexWideField.hexWideArray[proposedSelectedHex].select)
                {
//we need to figure out which of the 2 selected hexes was touched so we know which hex’s symbol was touched
                    if (selectedHex == proposedSelectedHex)
                    {
//if the touched symbol matches one of the symbols on the other hex then increase score and reset selection and if game mode
//is singles highlightNonMatching so the difficulty level is applied to the next set of hexes
                        if (compareAll(selectedHex, selectedHex2, proposedSelectedSector))
                        {
                            matchedFlowerIndex = database.getHex(selectedHex)[proposedSelectedSector];
                            increaseScore();
                            resetSelection();
                            if (gameMode == 1)
                            {
                                highlightNonMatching();
                            }
                        }
//if the touched symbol does not match any of the symbols on the other hex then decrease score and swap sector which will unhighlight the previous selected sector (if there was one) and select the new one.
                        else
                        {
                            decreaseScore();
                            hexWideField.hexWideArray[selectedHex].increaseScorePenaltyMultiplier();

                            swapSector1(selectedHex, selectedSector, proposedSelectedSector);
                        }
//this is the same logic but for if we touched the symbol on the other hex
                    } else if (selectedHex2 == proposedSelectedHex)
                    {
                        if (compareAll(selectedHex2, selectedHex, proposedSelectedSector))
                        {
                            matchedFlowerIndex = database.getHex(selectedHex2)[proposedSelectedSector];
                            increaseScore();
                            resetSelection();
                            highlightNonMatching();
                        } else
                        {
                            decreaseScore();
                            hexWideField.hexWideArray[selectedHex2].increaseScorePenaltyMultiplier();

                            swapSector2(selectedHex2, selectedSector2, proposedSelectedSector);

                        }
                    }
                }//end if selected
//if we touched a hex that was not one of the 2 selected hexes then reset the selection
                else
                {
                    resetSelection();
                }
            }//end else if(noOfSelected==2)
        }//end else if visible

    }

    public void snapCameraToHex()
    {
        int posArray[] = {0, 0};
        if (portrait1Landscape2 == 1)
        {

            posArray = hexWideField.getNextHexPairCoords(selectedHex, selectedHex2);
            setNewCameraZoomTarget(hexWideField.getNextHexZoom(selectedHex, selectedHex2));

        } else if (portrait1Landscape2 == 2)
        {
            posArray = hexTallField.getNextHexPairCoords(selectedHex, selectedHex2);
            setNewCameraZoomTarget(hexTallField.getNextHexZoom(selectedHex, selectedHex2));

        }
        hexPairPosX = posArray[0];
        hexPairPosY = posArray[1];
        setNewCameraPosTarget(hexPairPosX, hexPairPosY);
        camSnapOutdated = true;
        setCamSnapSpeed();
    }

    public void snapCameraToField()
    {

        setNewCameraZoomTarget(defaultCamZoom);

        setNewCameraPosTarget(defaultCamPosX, defaultCamPosY);
        camSnapOutdated = true;
        setCamSnapSpeed();

    }

    public void snapCameraToAdjacents()
    {
        int posArray[] = {0, 0};
        if (portrait1Landscape2 == 1)
        {
//this just gets the selected hex's centre coords
            posArray = hexWideField.getAdjacentCoords(selectedHex);
            setNewCameraZoomTarget(hexWideField.getAdjacentZoom(selectedHex));

        } else if (portrait1Landscape2 == 2)
        {
            posArray = hexTallField.getAdjacentCoords(selectedHex);
            setNewCameraZoomTarget(hexTallField.getAdjacentZoom(selectedHex));

        }
        hexPairPosX = posArray[0];
        hexPairPosY = posArray[1];
        setNewCameraPosTarget(hexPairPosX, hexPairPosY);
        camSnapOutdated = true;
        setCamSnapSpeed();

    }


    void setCamSnapSpeed()
    {

//work out the distance that needs to be covered to move the camera into the target position, then divide it by the amount of time you want the translation to take, then times by the delta time 
        camSnapSpeedX = (float) (newCamPosX - viewport.getCamera().position.x) / camSnapTimePeriod;
        camSnapSpeedY = (float) (newCamPosY - viewport.getCamera().position.y) / camSnapTimePeriod;
        camSnapSpeedZ = (float) (newCamZoom - ((OrthographicCamera) viewport.getCamera()).zoom) / camSnapTimePeriod;
    }

    /**
     * method to slowly move the camera into the target position
     */
    void updateCamSnap()
    {
        if (camSnapOutdated)
        {
//increment this timer so we know when to stop
            camSnapTime += Gdx.graphics.getDeltaTime();
//increment the position of the camera by the speedConstant times delta time, so it should reach the target position in the defined period of time
            viewport.getCamera().translate(camSnapSpeedX * Gdx.graphics.getDeltaTime(), camSnapSpeedY * Gdx.graphics.getDeltaTime(), 0f);

//do the same for the zoom
            ((OrthographicCamera) viewport.getCamera()).zoom += camSnapSpeedZ * Gdx.graphics.getDeltaTime();

//if the timer has exceeded the time in which we wanted the transition to take place then finish
            if (camSnapTime >= camSnapTimePeriod)
            {
                setNewCameraPos(newCamPosX, newCamPosY);
                setNewCameraZoom(newCamZoom);
                camSnapOutdated = false;
                camSnapTime = 0f;
            }//end if camSnapTime>=camSnapTimePeriod
        }//end if cam snap outdated

    }//end method

    //method for reseting the selection so we can start looking for symbols all over again
//if called after scoring but also if we have touched a hex indicative of wanting to cancel the selection
    public void resetSelection()
    {
//if the selected hex is not -1
        if (selectedHex != -1 && selectedHex != hexWideField.hexWideArray.length)
        {
//get the selected hex and unhighlight the non matching symbols this means show all the symbols that were hidden during the
//difficulty level being applied
//set the hex’s ‘select’ Boolean to false
//unhighlight the symbol that was highlighted when it was touched if applicable
//unhighlight all the adjacent hexes of the selected hex, if applicable
            hexWideField.hexWideArray[selectedHex].unHighlightNonMatchingSymbols();

            hexWideField.hexWideArray[selectedHex].unselect(0);
            hexWideField.hexWideArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
//do the same for the other hex
        if (selectedHex2 != -1 && selectedHex2 != hexWideField.hexWideArray.length)
        {
            hexWideField.hexWideArray[selectedHex2].unHighlightNonMatchingSymbols();
            hexWideField.hexWideArray[selectedHex2].unselect(0);
            hexWideField.hexWideArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex2, noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
//the camera should zoom out to show the full field, or zoom out to the maximum zoom out
//amount if the field is so big zooming all the way out would not be helpful
        if (zoomSelectionMode && gameMode != 1)
        {
            snapCameraToField();
        }
//reset the number of selected back to 0 so our next touch event is free to select a new first hex
        noOfSelected = 0;
        //if playing the singles game
        if (gameMode == 1)
        {
            // dont reset if playing singles
            // re select the first 2 hexes and set the nooselected to 2, this means we won’t have to tap the hexes before
//looking for a symbol that matches, it would be pointless requiring the player to tap the hexes to select them since there
//are only 2 hexes to select
            if (score < targetScore)
            {//if we sway reset sele tion twice this could lead to out of bouns, selectedHex2 should no exceed target score*2
                selectedHex = score;
                selectedHex2 = score + targetScore;
            } else//if we have reached the target score set the selected hexes to something other than -1
            {
                selectedHex = 0;
                selectedHex2 = 0;
            }
            // if the field game mode had less hexes than the default target score for the singles game mode
            // then this would do index out of bounds, use this if statement to prevent that,
            // might be neccessary to repeat reset selection method  after creating new database for singles mode

            if (hexWideField != null && selectedHex != -1 &&
                    selectedHex < hexWideField.hexWideArray.length &&
                    selectedHex2 != -1 &&
                    selectedHex2 < hexWideField.hexWideArray.length)

            {

                hexWideField.hexWideArray[selectedHex].select(0);
                hexWideField.hexWideArray[selectedHex2].select(0);
                noOfSelected = 2;
            }


        } else//game mode field
        {

//reset the selected hex
            selectedHex = -1;
            selectedHex2 = -1;
        }
    }

    //select first hex, called if we have touched a hex when there were not any hexes selected already
    public void selectFirstHex()
    {
//just some error handling in case this is called when the proposed selected hex was set to -1, which shouldn’t happen
        if (proposedSelectedHex != -1)
        {
//store the touched hex which will come in handy later to confirm which hex has been touched first
//set the select Boolean to true in the hex that we touched
//highlight the hexes that are adjacent to the touched hex
            selectedHex = proposedSelectedHex;
            hexWideField.hexWideArray[selectedHex].select(200);
            adjacentArray = hexWideField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexWideField.highlightAdjacent(adjacentArray);
//this should set the zoom level and camera position so that all the adjacents to the selected hex fill
//the screen
            if (zoomSelectionMode)
            {
                snapCameraToAdjacents();
            }
//set the number of selected to 1 so the next touch will be handled differently
            noOfSelected = 1;
        }
    }

    //select second hex, if one has already been selected and we have touched one of the hexes that were adjacent to that
    public void selectSecondHex()
    {
//error handling, if the touched hex index is not -1,
        if (proposedSelectedHex != -1)
        {
//unhighlight all the adjacent of the first selected hex
            adjacentArray = hexWideField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
            //record the index of the second selected hex
            selectedHex2 = proposedSelectedHex;
//set the select Boolean to true in the hex that we touched
            hexWideField.hexWideArray[selectedHex2].select(200);
//now that we have 2 hexes selected we can apply the difficulty level, remove the required amount of symbols that are false
//matches
            highlightNonMatching();
//zoom in even further so that just the 2 selected hexes will fill the whole screen
            if (zoomSelectionMode)
            {
                snapCameraToHex();

            }
            //if quick mode – aka if not zoom mode
            else
            {
//record the sector that was touched
                selectedSector2 = proposedSelectedSector;
//highlight the symbol that was touched
                hexWideField.hexWideArray[selectedHex2].highlightSymbol(selectedSector2);
//TODO I should add logic here were we compare the match and increase score etc
            }
//set the number of select to 2 so the next touch will be different
            noOfSelected = 2;
        }
    }

    //this will apply the difficulty level, the difficulty level is set and stored elsewhere
    void highlightNonMatching()
    {
//clear the arrays
        nonMatchingSymbolsHex1.clear();
        nonMatchingSymbolsHex2.clear();
//set the array to all the non matching symbols given by the database method
        nonMatchingSymbols = database.findNonMatchingSymbols(selectedHex, selectedHex2, difficulty);
//the array now hold all the symbols from the 2 selected hexes apart from the 2 that do match each other
//we don’t know which symbol belongs to which hex at the moment, use the below to figure it out
        for (int i = 0; i < nonMatchingSymbols.size(); i++)//this list could have size up to 11, so divide it into 2 below
        {
//check if the value in the array is more than 5, we added 6 to the symbols that belong to the second selected hex
            if (nonMatchingSymbols.get(i) > 5)
            {
//add the ones over 5 to the second hex list, obviously subtract 6 so the value is between 0-5
                nonMatchingSymbolsHex2.add(nonMatchingSymbols.get(i) - 6);
            }
//if the value is not over 5 then it must be on the first hex so add it to the first list
            else
            {
                nonMatchingSymbolsHex1.add(nonMatchingSymbols.get(i));
            }
        }
//if the selected hexes are not -1 then apply the difficulty level causing the relevant symbols to disappear
//the selected hexes might be because sometimes we have to call this method after resetting the selection
        if (selectedHex != -1 && selectedHex2 != -1)
        {
            hexWideField.hexWideArray[selectedHex].highlightNonMatchingSymbols(nonMatchingSymbolsHex1);
            hexWideField.hexWideArray[selectedHex2].highlightNonMatchingSymbols(nonMatchingSymbolsHex2);
        }
    }

    //unused method
    public void removeOneSelected(int givenHex, int givenSector, int remainingHex, int remainingSector)
    {
        hexWideField.hexWideArray[givenHex].unselect(0);
        hexWideField.hexWideArray[givenSector].unhighlightSymbol();
        adjacentArray = hexWideField.getAdjacent(remainingHex, noOfColumns, noOfRows);
        hexWideField.highlightAdjacent(adjacentArray);
        selectedHex = remainingHex;
        selectedSector = remainingSector;
        selectedHex2 = -1;
        selectedSector2 = -1;
        noOfSelected = 1;
    }

    //if you select a symbol this highlights it so we can see which one you selected and it will unhighlight other symbols
//TODO add arguments for hex2, and symbol2, unhighlight the symbols for hex 2 also so we only have one symbol highlighted
//TODO can delete swapSecotr2 if I add a return to this so that I can set the value for selectedSector or selectedSector2 when I call it
    public void swapSector1(int hex, int oldSector, int newSector)
    {
        hexWideField.hexWideArray[hex].unhighlightSymbol();
        hexWideField.hexWideArray[hex].highlightSymbol(newSector);
        selectedSector = newSector;
    }

    public void swapSector2(int hex, int oldSector, int newSector)
    {
        hexWideField.hexWideArray[hex].unhighlightSymbol();
        hexWideField.hexWideArray[hex].highlightSymbol(newSector);
        selectedSector2 = newSector;
    }

    //compares the symbol you touched with all the symbols from the other selected hex returns true if it matches
    public boolean compareAll(int touchedHex, int otherHex, int touchedSector)
    {
        for (int i = 0; i < 6; i++)
        {
            if (database.compareSymbols(touchedHex, touchedSector, otherHex, i))
            {
                return true;
            }
        }
        return false;
    }

    //this method is largely the same but for the tall hexes to be used in landscape some things are different because the hexes
//are set out so differently
    public void setSelectedTall(int index, int sector)
    {
        int totalHexes = noOfColumns * noOfRows;

        //overlap
        if (sector > 9)//in the case of an overlap in the touch logic the sector would have 10 added to it's value to indicate that it is the hex's neighbour that is selected.
        {//one of 4 adjacent hexes could be selected, work out which one it is with the selected sector
            //10 means the hex above and to the left of the given hex is selected,
            //11 is above and to the right
            //13 below right
            //14 below left

            //if hex is in an even row (including 0) above left would be hex index +1 + noOfRows
            //if hex is in an even row above right would be hex index +1
            //if hex is in an even row below right would be hex index -1
            //if hex is in an even row below left would be hex index - noOfRows -1

            //if hex is in an even rows (the bottom most row is 0 which is even)
            if ((index % noOfRows) % 2 == 0)
            {
                if (sector == 10)
                {
                    //if not on left edge AND not on top edge
                    if (index >= noOfRows && index % noOfRows < noOfRows - 1)
                    {
                        proposedSelectedHex = index + 1 - noOfRows;
                    }
                }// this should be impossible, as an adjacent hex would be placed more recently and that would handle the touch
                    /*if (sector == 11) {
	//if  not on the top - doesnt matter if its on the right or not
	                    if ( index % noOfRows < noOfRows-1) {
	                        proposedSelectedHex = index + 1;
	                    }
	                }*/
                else if (sector == 13)
                {
                    //if  not on the bottom - doesnt matter if its on tbe right
                    if (index % noOfRows > 0)
                    {
                        proposedSelectedHex = index - 1;
                    }
                } else if (sector == 14)
                {
                    //if not on left edge AND not on bottom edge
                    if (index >= noOfRows && index % noOfRows > 0)
                    {
                        proposedSelectedHex = index - noOfRows - 1;
                    }
                }
            }
            //else if odd row above left would just be hex index + 1
            //else if odd row above right would be hex index + 1 + noOfRows
            //else if odd row below right would be hex index + noOfRows -1
            //else if odd row below left would be hex index - 1

            //if the given hex is in an even row;
            else
            {//if in odd row
                //only one corner case is not covered by overlapping adjacent hexes that were placed more recwntly
	                /*if (sector == 10) {
	//if not on left edge AND not on top edge
	                    if (index >=noOfRows && index % noOfRows < noOfRows-1) {
	                        proposedSelectedHex = index + 1;
	                    }
	                }
	                if (sector == 11) {
	//if  not on the top - doesnt matter if its o  the left or not
	                    if ( index % noOfRows < noOfRows-1) {
	                        proposedSelectedHex = index + noOfRows + 1;
	                    }
	                }
	                if (sector == 13) {
	//if not on the right edge AND not on the bottom
	                    if (index <= totalHexes-noOfRows && index % noOfRows > 0) {
	                        proposedSelectedHex = index +noOfRows- 1;
	                    }
	                }*/
                if (sector == 14)
                {
                    //if not on bottom edge - doesnt matter if its on the keft or not
                    if (index % noOfRows > 0)
                    {
                        proposedSelectedHex = index - 1;
                    }
                }
            }

            //  the touched sector will actually be the opposing sector to the given hex, so translate that

            proposedSelectedSector = (sector + 3 - 10) % 6;

        }//end overlap logic
        else
        {
            proposedSelectedSector = sector;
            proposedSelectedHex = index;
        }
        if (noOfSelected == 0)
        {
            selectFirstHexTall();
        }//end if(noOfSelected==0)

        else if (noOfSelected == 1)
        {
            if (hexTallField.hexTallArray[proposedSelectedHex].highlight)
            {
                selectSecondHexTall();
                //if not zoomSelectionMode compare sector with all sectors on first hex
                if (!zoomSelectionMode)
                {
                    if (compareAllTall(selectedHex2, selectedHex, proposedSelectedSector))
                    {
                        resetSelectionTall();
                    }
                }

            }//end if highlighted
            else if (hexTallField.hexTallArray[proposedSelectedHex].select)
            {
                resetSelectionTall();

            }//end if selected
            else //not highlighted and not selected
            {
                resetSelectionTall();
            }
        }//end else if(noOfSelected==1)
        else if (noOfSelected == 2)
        {
            if (hexTallField.hexTallArray[proposedSelectedHex].select)
            {
                if (selectedHex == proposedSelectedHex)
                {
                    if (compareAllTall(selectedHex, selectedHex2, proposedSelectedSector))
                    {
                        increaseScore();
                        resetSelectionTall();
                        highlightNonMatchingTall();
                    } else
                    {
                        decreaseScore();
                        swapSector1Tall(selectedHex, selectedSector, proposedSelectedSector);
                    }
                } else if (selectedHex2 == proposedSelectedHex)
                {
                    if (compareAllTall(selectedHex2, selectedHex, proposedSelectedSector))
                    {
                        increaseScore();
                        resetSelectionTall();
                        highlightNonMatchingTall();
                    } else
                    {
                        decreaseScore();
                        swapSector2Tall(selectedHex2, selectedSector2, proposedSelectedSector);

                    }
                }
            }//end if selected
            else
            {
                resetSelectionTall();
            }
        }//end else if(noOfSelected==2)

    }

    public void resetSelectionTall()
    {

        if (selectedHex != -1 && selectedHex != hexTallField.hexTallArray.length)
        {

            hexTallField.hexTallArray[selectedHex].unHighlightNonMatchingSymbols();

            hexTallField.hexTallArray[selectedHex].unselect(0);
            hexTallField.hexTallArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        if (selectedHex2 != -1 && selectedHex2 != hexTallField.hexTallArray.length)
        {
            hexTallField.hexTallArray[selectedHex2].unHighlightNonMatchingSymbols();

            hexTallField.hexTallArray[selectedHex2].unselect(0);
            hexTallField.hexTallArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex2, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        if (zoomSelectionMode && gameMode != 1)
        {
            //zoom out
            snapCameraToField();
        }
        noOfSelected = 0;

        if (gameMode == 1)
        {
            // dont reset if playing singles
            // re select the first 2 hexes and set the nooselected to 2
            if (score < targetScore)
            {
                selectedHex = score;
                selectedHex2 = score + targetScore;
            } else//if we have reached the target score set the selected hexes to something other than -1
            {
                selectedHex = 0;
                selectedHex2 = 0;
            }
            if (hexTallField != null && selectedHex != -1 &&
                    selectedHex < hexTallField.hexTallArray.length &&
                    selectedHex2 != -1 &&
                    selectedHex2 < hexTallField.hexTallArray.length)

            {
                hexTallField.hexTallArray[selectedHex].select(0);
                hexTallField.hexTallArray[selectedHex2].select(0);
                noOfSelected = 2;
            }
        } else
        {

            selectedHex = -1;
            selectedHex2 = -1;
        }

    }


    public void selectFirstHexTall()
    {
        if (proposedSelectedHex != -1)
        {
            selectedHex = proposedSelectedHex;
            hexTallField.hexTallArray[selectedHex].select(200);
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.highlightAdjacent(adjacentArray);
            if (zoomSelectionMode)
            {
                // zoom to fit adjacents
                snapCameraToAdjacents();
            }
            noOfSelected = 1;
        }
    }

    public void selectSecondHexTall()
    {
        if (proposedSelectedHex != -1)
        {
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);

            selectedHex2 = proposedSelectedHex;
            hexTallField.hexTallArray[selectedHex2].select(200);
            highlightNonMatchingTall();
            if (zoomSelectionMode)
            {
                snapCameraToHex();
                // zoom to fit pair of selected hexes
            } else
            {
                selectedSector2 = proposedSelectedSector;
                hexTallField.hexTallArray[selectedHex2].highlightSymbol(selectedSector2);
            }
            noOfSelected = 2;
        }
    }

    void highlightNonMatchingTall()
    {
        nonMatchingSymbolsHex1.clear();
        nonMatchingSymbolsHex2.clear();
        nonMatchingSymbols = database.findNonMatchingSymbols(selectedHex, selectedHex2, difficulty);
        for (int i = 0; i < nonMatchingSymbols.size(); i++)//this list could have size up to 11, so divide it into 2 below
        {
            if (nonMatchingSymbols.get(i) > 5)//the values in the array should refer to symbols (0-5) but some are +6to indicate its in the second hex
            {
                nonMatchingSymbolsHex2.add(nonMatchingSymbols.get(i) - 6);//add the ones over 5 to the second hex list, obviously subtract 6 so the value is between 0-5
            } else
            {
                nonMatchingSymbolsHex1.add(nonMatchingSymbols.get(i));
            }
        }
        if (selectedHex != -1 && selectedHex2 != -1)
        {
            hexTallField.hexTallArray[selectedHex].highlightNonMatchingSymbols(nonMatchingSymbolsHex1);
            hexTallField.hexTallArray[selectedHex2].highlightNonMatchingSymbols(nonMatchingSymbolsHex2);
        }
    }

    public void removeOneSelectedTall(int givenHex, int givenSector, int remainingHex, int remainingSector)
    {
        hexTallField.hexTallArray[givenHex].unselect(0);
        hexTallField.hexTallArray[givenSector].unhighlightSymbol();
        adjacentArray = hexTallField.getAdjacent(remainingHex, noOfColumns, noOfRows);
        hexTallField.highlightAdjacent(adjacentArray);
        selectedHex = remainingHex;
        selectedSector = remainingSector;
        selectedHex2 = -1;
        selectedSector2 = -1;
        noOfSelected = 1;
    }

    public void swapSector1Tall(int hex, int oldSector, int newSector)
    {
        hexTallField.hexTallArray[hex].unhighlightSymbol();
        hexTallField.hexTallArray[hex].highlightSymbol(newSector);
        selectedSector = newSector;
    }

    public void swapSector2Tall(int hex, int oldSector, int newSector)
    {
        hexTallField.hexTallArray[hex].unhighlightSymbol();
        hexTallField.hexTallArray[hex].highlightSymbol(newSector);
        selectedSector2 = newSector;
    }

    public boolean compareAllTall(int touchedHex, int otherHex, int touchedSector)
    {
        for (int i = 0; i < 6; i++)
        {
            if (database.compareSymbols(touchedHex, touchedSector, otherHex, i))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw()
    {
        act(Gdx.graphics.getDeltaTime());
        if (visible)
        {
            if (!ready)
            {
                hudViewport.apply();
                hudBatch.begin();
                this.act();

                Gdx.gl.glClearColor(0.86f, 0.65f, 0.22f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                font.draw(hudBatch, "Tap and hold screen to get ready", 0, stageInterface.getScreenHeight() / 17 * 9, viewport.getCamera().viewportWidth, Align.center, true);
                font.draw(hudBatch, "Release to start", 0, stageInterface.getScreenHeight() / 17 * 8, viewport.getCamera().viewportWidth, Align.center, true);
                hudBatch.end();

                super.draw();
            } else
            {
//if visible, should be visible so long as we are in the game stage

//increase the timer
                timer = timer + Gdx.graphics.getDeltaTime();
                //test camera translate
                /*
                if(viewport.getCamera().position.x>1280) {
                    viewport.getCamera().position.set(0, viewport.getCamera().position.y, viewport.getCamera().position.y);
                }
                viewport.getCamera().translate(5f, 0f, 0f);
                */


                //when we get an answer right of wrong we set green or red to 1 which in turn is used to colour the screen so the
//background flashes to show you got it right or wrong, the below controls how long the screen stays that colour before going
//back to what it was. Use the delta time for effect consistent throughout all frame rates.
//the colour accepts values from 0-1, we are setting it to 1 then reducing it over time, if you reduce it by a larger number
//it will fade faster, so - (2 * Gdx.graphics.getDeltaTime()); should take half a second
                if (green > bkgdg * 1.05)
                {
                    green = green - (2 * Gdx.graphics.getDeltaTime());
                } else if (green < bkgdg * 0.95)
                {
                    green = green + (2 * Gdx.graphics.getDeltaTime());

                } else
                {
                    green = bkgdg;
                }

                if (red > bkgdr * 1.05)
                {
                    red = red - (2 * Gdx.graphics.getDeltaTime());
                } else if (red < bkgdr * 0.95)
                {
                    red = red + (2 * Gdx.graphics.getDeltaTime());

                } else
                {
                    red = bkgdr;
                }

                if (blue > bkgdb * 1.05)
                {
                    blue = blue - (2 * Gdx.graphics.getDeltaTime());
                } else if (blue < bkgdb * 0.95)
                {
                    blue = blue + (2 * Gdx.graphics.getDeltaTime());

                } else
                {
                    blue = bkgdb;
                }
                Gdx.gl.glClearColor(red, green, blue, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                //spritebatch without matrix transformations


                viewport.apply();

                //handle camera transition
                updateCamSnap();

                viewport.getCamera().update();


                renderer.setProjectionMatrix(viewport.getCamera().combined);
                //renderer.begin(ShapeRenderer.ShapeType.Line);
                //renderer.setColor(Color.BLUE);
                renderer.begin(ShapeRenderer.ShapeType.Filled);

                this.act();

                if (portrait1Landscape2 == 1)
                {
                    //hexWideField.draw(renderer);
                    //renderer.end();
                    //renderer.begin(ShapeRenderer.ShapeType.Filled);
                    for (int i = 0; i < flowerPosList.list.size(); i++)
                    {

                        float ox = flowerPosList.list.get(i).x;
                        float oy = flowerPosList.list.get(i).y;
    /*
Color secondaryColour = new Color(Color.YELLOW);
    Color primaryColour = new Color(Color.RED);
    Color tertiaryColour = new Color(Color.BLUE);
    renderer.setColor(primaryColour);
    */
                        FlowerDB.draw(renderer, flowerPosList.index.get(i) % 31, ox, oy, hexWideField.edgeSize / 320, false);
                        //drawFlower(i%9,ox,oy,scale,primaryColour,secondaryColour, tertiaryColour);
                    }
                    for (int i = 0; i < beeArray.size(); i++)
                    {
                        beeArray.get(i).drawFilled(renderer, Gdx.graphics.getDeltaTime());
                    }

                    hexWideField.drawFilled(renderer, symbolType);
                } else if (portrait1Landscape2 == 2)
                {

                    //hexTallField.draw(renderer);
                    //renderer.end();
                    //renderer.begin(ShapeRenderer.ShapeType.Filled);

                    hexTallField.drawFilled(renderer, symbolType);

                }

                renderer.end();

                spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
                spriteBatch.begin();
                if (portrait1Landscape2 == 1)
                {
                    hexWideField.drawSprites(spriteBatch, symbolType);
                    for (int i = 0; i < beeArray.size(); i++)
                    {
                        beeArray.get(i).drawSprites(spriteBatch);
                    }
                } else if (portrait1Landscape2 == 2)
                {
                    hexTallField.drawSprites(spriteBatch, symbolType);

                }
                spriteBatch.end();
                hudViewport.apply();
                hudBatch.begin();

                if (gameMode == 1)
                {
                    font.draw(hudBatch, "timer: " + (int) (timer), stageInterface.getScreenWidth() / 3, 30);

                } else if (gameOver == false)
                {
                    font.draw(hudBatch, "timer: " + (int) (timer), stageInterface.getScreenWidth() / 3, 30);
                    font.draw(hudBatch, "score: " + (int) (displayScore - timer * 1000), stageInterface.getScreenWidth() / 2, 70);

                }


                hudBatch.end();

                super.draw();
            }
        }
    }

    public void setVisible(boolean visible)
    {

        this.visible = visible;
    }

    public void setPause(boolean pause)
    {
        this.pause = pause;
    }

    public void drawWideHex(ShapeRenderer sr, int originX, int originY, int edgeSize)
    {
        // draws a ‘wide’ hex hex with flat top and bottom. originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral triange with edge size defined by edgeSize
        double altitudeSize = edgeSize * 0.866025403784439;

        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line(originX + (edgeSize / 2), (int) (originY - altitudeSize), originX + edgeSize, originY);
        sr.line(originX + edgeSize, originY, originX + (edgeSize / 2), (int) (originY + altitudeSize));
        sr.line(originX + (edgeSize / 2), (int) (originY + altitudeSize), originX - (edgeSize / 2), (int) (originY + altitudeSize));
        sr.line(originX - (edgeSize / 2), (int) (originY + altitudeSize), originX - edgeSize, originY);
        sr.line(originX - edgeSize, originY, originX - (edgeSize / 2), (int) (originY - altitudeSize));
        sr.line(originX - (edgeSize / 2), (int) (originY - altitudeSize), originX + (edgeSize / 2), (int) (originY - altitudeSize));

    }

    //for each hex that was created in the hex field we will add it to the stage so that it can be touched
    public void addHexesToStage(HexWideField hwf)
    {
        for (int i = 0; i < hwf.getNoOfHexes(); i++)
        {
            addActor(hwf.hexWideArray[i]);
        }


        ready = false;
        createReadyButton();
    }

    //same but for tall hexes
    public void addHexesToStage(HexTallField hwf)
    {
        for (int i = 0; i < hwf.getNoOfHexes(); i++)
        {
            addActor(hwf.hexTallArray[i]);
        }

    }

    //remove all actors, needed when switching game mode, or otherwise getting rid of one field and making a new one
    public void removeAllActors()
    {
        for (Actor actor : getActors())
        {
            //actor.remove();
            actor.addAction(Actions.removeActor());
        }

    }

    void updateFieldRefresh()
    {
        resetGame();
        updateField(noOfRows, noOfColumns, portrait1Landscape2, fieldPosX, fieldPosY,
                fieldWidth, fieldHeight, gameMode, symbolType);

    }

    void updateFieldSwapOrientation()
    {

        resetGame();


        if (portrait1Landscape2 == 1)
        {
            resetSelection();
//swap the orientation to the alternative
            portrait1Landscape2 = 2;
            stageInterface.setLandscape();
//this is an attempt to keep the same aspect ratio - 16:9, maximise the space used
            noOfRows = noOfColumns;
            noOfColumns = noOfRows * 2 + 1;

        } else if (portrait1Landscape2 == 2)
        {
            resetSelectionTall();
//swap the orientation to the alternative
            portrait1Landscape2 = 1;
            stageInterface.setPortrait();
//this is an attempt to keep the same aspect ratio - 16:9, maximise the space used
            noOfColumns = noOfRows;
            noOfRows = (int) (noOfColumns * 1.5) + 1;

        }
        //reset / reload everything
        removeAllActors();

//swap orientation , swap all the values, the portrait1Landscape2 is swapped above
        int tempWidth = fieldWidth;
        int tempFieldPosX = fieldPosX;

        fieldWidth = fieldHeight;
        fieldHeight = tempWidth;
        fieldPosY = tempFieldPosX;

        if (gameMode == 1)//if singles mode overwrite number of rows and columns,
        {
            if (portrait1Landscape2 == 1)
            {
                noOfColumns = targetScore;
                noOfRows = 2;
            } else if (portrait1Landscape2 == 2)
            {
                noOfRows = targetScore;
                noOfColumns = 2;
            }
        }
        targetScoreFieldMode = noOfColumns * noOfRows;

//create a new database, if singles the number of columns and rows has been set to 1 and 2
        database = new Database(noOfSymbols, noOfColumns, noOfRows, portrait1Landscape2);
//set new wide or tall field depending on if we are in portrait or landscape mode
        if (portrait1Landscape2 == 1)
        {
            hexWideField = new HexWideField(fieldPosX, fieldPosY, fieldWidth, fieldHeight,
                    noOfRows, noOfColumns, gameMode, this, database);
            addHexesToStage(hexWideField);
//some specific things if in singles game mode
            if (gameMode == 1)
            {

                //we want the 2 hexes to be selected automatically
                hexWideField.hexWideArray[selectedHex].select(0);
                hexWideField.hexWideArray[selectedHex2].select(0);
//apply the difficulty level too
                highlightNonMatching();
                //point the camera at the selected hexes
                camSnapTime = camSnapTimePeriod;
                snapCameraToHex();

            }
        } else if (portrait1Landscape2 == 2)
        {
            hexTallField = new HexTallField(fieldPosX, fieldPosY, fieldWidth, fieldHeight,
                    noOfRows, noOfColumns, gameMode, this, database);
            addHexesToStage(hexTallField);
            if (gameMode == 1)
            {

                hexTallField.hexTallArray[selectedHex].select(0);
                hexTallField.hexTallArray[selectedHex2].select(0);
                highlightNonMatchingTall();
                camSnapTime = camSnapTimePeriod;
                snapCameraToHex();
            }
        }
    }


    //create or update the field, when changing options only some of them require the field be updated, all the options that do require a field change will be handled here
    public void updateField(int newNoOfRows, int newNoOfColumns, int newPortrait1Landscape2,
                            int newFieldPosX, int newFieldPosY, int newFieldWidth,
                            int newFieldHeight, int newGameMode, int newSymbolType)
    {
//if any of the arguments are not set to 0 then update the corresponding value
        /*if(newNoOfColumns!=0){noOfColumns=newNoOfColumns;}
        if(newNoOfRows!=0){noOfRows=newNoOfRows;}
        if(newPortrait1Landscape2!=0){ portrait1Landscape2 =newPortrait1Landscape2;}*/
        if (isNewNoOfColumns(newNoOfColumns))
        {
            noOfColumns = newNoOfColumns;
        }
        if (isNewNoOfRows(newNoOfRows))
        {
            noOfRows = newNoOfRows;
        }
        if (isNewPortrait1Landscape2(newPortrait1Landscape2))
        {
            portrait1Landscape2 = newPortrait1Landscape2;
        }
        if (newFieldPosX != 0)
        {
            fieldPosX = newFieldPosX;
        }
        if (newFieldPosY != 0)
        {
            fieldPosY = newFieldPosY;
        }
        if (newFieldWidth != 0)
        {
            fieldWidth = newFieldWidth;
        }
        if (newFieldHeight != 0)
        {
            fieldHeight = newFieldHeight;
        }
//if the game mode is changed we need to reset the game which will reset the timer and score etc
        if (isNewGameMode(newGameMode))
        {

            gameMode = newGameMode;

        }
        resetGame();// reset the score and timer when starting a new game modes

        if (newSymbolType != 0)
        {
            symbolType = newSymbolType;
            //if symbol is numbers or flowers use 31 symbols, if symbol type is shaoe usr 35
            if (symbolType == 1)
            {
                noOfSymbols = 31;
            }
            if (symbolType == 2)
            {
                noOfSymbols = 35;
            }
            if (symbolType == 3)
            {
                noOfSymbols = 31;
            }
        }
        if (portrait1Landscape2 == 1)
        {
            resetSelection();
        } else if (portrait1Landscape2 == 2)
        {
            resetSelectionTall();
        }
        //reset / reload everything
        removeAllActors();

       /* if(gameMode==1)//if singles mode overwrite number of rows and columns,
        {
            if(portrait1Landscape2==1)
            {
                //noOfColumns =targetScore;
                targetScore=noOfColumns;
                noOfRows=2;
            }
            else if(portrait1Landscape2==2)
            {
                noOfRows=targetScore;
                noOfColumns=2;
            }
        }*/
        targetScoreFieldMode = noOfColumns * noOfRows;
//create a new database, if singles the number of columns and rows has been set to 1 and 2
        database = new Database(noOfSymbols, noOfColumns, noOfRows, portrait1Landscape2);
//set new wide or tall field depending on if we are in portrait or landscape mode
        if (portrait1Landscape2 == 1)
        {
            hexWideField = new HexWideField(fieldPosX, fieldPosY, fieldWidth, fieldHeight,
                    noOfRows, noOfColumns, gameMode, this, database);
            addHexesToStage(hexWideField);
            beeArray.clear();
            for (int i = 0; i < hexWideField.hexWideArray.length; i++)
            {
                beeArray.add(new Bee(hexWideField.hexWideArray[i].posX, hexWideField.hexWideArray[i].posY));
            }
//some specific things if in singles game mode
            if (gameMode == 1)
            {
                targetScore = noOfColumns;
                selectedHex = score;
                selectedHex2 = score + targetScore;
                resetSelection();
                //we want the 2 hexes to be selected automatically
                hexWideField.hexWideArray[selectedHex].select(0);
                hexWideField.hexWideArray[selectedHex2].select(0);

                selectedHex = score;
                selectedHex2 = score + targetScore;
                noOfSelected = 2;
//apply the difficulty level too
                highlightNonMatching();
                //point the camera at the selected hexes
                camSnapTime = camSnapTimePeriod;
                snapCameraToHex();

            }
        } else if (portrait1Landscape2 == 2)
        {
            hexTallField = new HexTallField(fieldPosX, fieldPosY, fieldWidth, fieldHeight,
                    noOfRows, noOfColumns, gameMode, this, database);
            addHexesToStage(hexTallField);
            if (gameMode == 1)
            {

                hexTallField.hexTallArray[selectedHex].select(0);
                hexTallField.hexTallArray[selectedHex2].select(0);

                selectedHex = score;
                selectedHex2 = score + targetScore;
                noOfSelected = 2;

                highlightNonMatchingTall();
                camSnapTime = camSnapTimePeriod;
                snapCameraToHex();
            }
        }

    }

    public void updateZoom(int newZoom)
    {
        if (newZoom == 1)
        {
            zoomSelectionMode = false;
        } else if (newZoom == 2)
        {
            zoomSelectionMode = true;
        }
    }


    public boolean isNewNoOfRows(int v)
    {
        if (v == noOfRows || v == 0)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public boolean isNewNoOfColumns(int v)
    {
        if (v == noOfColumns || v == 0)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public boolean isNewPortrait1Landscape2(int v)
    {
        if (v == portrait1Landscape2)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public boolean isNewGameMode(int v)
    {
        if (v == gameMode)
        {
            return false;
        } else
        {
            return true;
        }
    }


    //called when a match is found
    void increaseScore()
    {
//increment score
        score++;
//display green background which will automatically fade out
        flashGreenBackground();
//if in singles game mode we need to get some new symbols etc so we can’t just match the same symbols again
        if (gameMode == 1)
        {
            selectedHex = score;
            selectedHex2 = score + targetScore;
            snapCameraToHex();
        } else// if not singles mode, make matched hexes disappear
        {
            if (portrait1Landscape2 == 1)
            {
//hide both hexes
                hexWideField.hexWideArray[selectedHex].hide();
                beeArray.get(selectedHex).visible = true;
                hexWideField.hexWideArray[selectedHex2].hide();
                // beeArray.get(selectedHex2).visible=true;

                scoreFieldMode++;
                scoreFieldMode++;
                int scoremultiplier = hexWideField.hexWideArray[selectedHex].scorePenaltyMultiplier + hexWideField.hexWideArray[selectedHex2].scorePenaltyMultiplier;
                if (scoremultiplier > 5)
                {
                    scoremultiplier = 5;
                }
                displayScore = displayScore + 3200 / (int) (Math.pow(2, scoremultiplier));
                displayScore = displayScore + 3200 / (int) (Math.pow(2, scoremultiplier));

                flowerPosList.add(hexWideField.hexWideArray[selectedHex].posX
                        , hexWideField.hexWideArray[selectedHex].posY, matchedFlowerIndex);
                flowerPosList.add(hexWideField.hexWideArray[selectedHex2].posX
                        , hexWideField.hexWideArray[selectedHex2].posY, matchedFlowerIndex);

                beeArray.get(selectedHex).addNewTarget();

//create a list for temporary use
                ArrayList<Integer> list = new ArrayList<Integer>();
//use the get isolated method to find out if removing the two selected hexes has resulted in any hexes
//becoming isolated  and therefore have no adjacent of their own
                list = hexWideField.getIsolated(selectedHex, selectedHex2, noOfColumns, noOfRows);
                for (int i = 0; i < list.size(); i++)
                {
//if any are isolated we should hide them too, consider increasing score?
                    if (hexWideField.hexWideArray[list.get(i)].visible)
                    {
                        hexWideField.hexWideArray[list.get(i)].hide();
                        beeArray.get(list.get(i)).visible = true;

                        scoreFieldMode++;
                        displayScore = displayScore + 3200 / (int) (Math.pow(2, scoremultiplier));

                        flowerPosList.add(hexWideField.hexWideArray[list.get(i)].posX
                                , hexWideField.hexWideArray[list.get(i)].posY, matchedFlowerIndex);
                        beeArray.get(list.get(i)).addNewTarget();

                    }
                }

            }
//same but for tall hexes
            else
            {
                hexTallField.hexTallArray[selectedHex].hide();
                hexTallField.hexTallArray[selectedHex2].hide();
                scoreFieldMode++;
                scoreFieldMode++;
                ArrayList<Integer> list = new ArrayList<Integer>();
                list = hexTallField.getIsolated(selectedHex, selectedHex2, noOfColumns, noOfRows);
                for (int i = 0; i < list.size(); i++)
                {
                    if (hexTallField.hexTallArray[list.get(i)].visible)
                    {
                        hexTallField.hexTallArray[list.get(i)].hide();

                        scoreFieldMode++;
                    }
                }
            }
        }
        isTargetReached(score);

    }

    void decreaseScore()
    {
        //score--;
        timer = timer + penaltyTime;//plus 2 seconds to timer
        flashRedBackground();
    }

    void isTargetReached(int currentScore)
    {
        if (gameMode == 1)
        {
            if (currentScore == targetScore)
            {
                gameOver();
            }
        } else
        {
            if (scoreFieldMode == targetScoreFieldMode)
            {
                gameOver();
            }
        }
    }

    public void prepareNewGame()
    {

        resetGame();
        updateField(noOfRows, noOfColumns, portrait1Landscape2, fieldPosX,
                fieldPosY, fieldWidth, fieldHeight, gameMode, symbolType);
    }

    void gameOver()
    {
        gameOver = true;
        timerFinal = timer;
        int timerInt = (int) (timer * 1000);
        int diffInt = difficulty;
        Date date = new Date();
        Calendar calendarG = new GregorianCalendar();
        calendarG.setTime(date);

//int minutes = calendarG.get(Calendar.MINUTE);
//int hours = calendarG.get(Calendar.HOUR_OF_DAY);

        int year = calendarG.get(Calendar.YEAR);
        int month = calendarG.get(Calendar.MONTH) + 1;
        int day = calendarG.get(Calendar.DAY_OF_MONTH);
        int intDate = year * 10000 + month * 100 + day;
        if (gameMode == 1)
        {
            GameStage.this.stageInterface.setScore(timerInt, noOfRows * noOfColumns, intDate);
            GameStage.this.stageInterface.goToGameOverStage(noOfRows * noOfColumns, timerInt,
                    difficulty, gameMode);
        } else
        {
            int fieldScore = displayScore - timerInt;

            GameStage.this.stageInterface.setScore(fieldScore, noOfRows * noOfColumns, intDate);
            GameStage.this.stageInterface.goToGameOverStage(noOfRows * noOfColumns, fieldScore,
                    difficulty, gameMode);
        }
    }

    void resetGame()
    {

        //this updates the camera return position to the default  position
        gameOver = false;
        score = 0;
        scoreFieldMode = 0;
        timer = 0;
    }

    void flashGreenBackground()
    {
        red = 1.0f;
        green = 0.95f;
        blue = 0.7f;
    }

    void flashRedBackground()
    {
        red = 0.0f;
        green = 0.0f;
        blue = 0.0f;
    }

    public void updateDifficulty(int newDifficulty)
    {
        //if the new difficulty value is 0 then it will assume no change rather than set as 0, so
        // so just minus 1 so that if we set difficulty to 1 it will couny as changing th e difficulty but will set it to 0,
        difficulty = newDifficulty - 1;
    }

    public void updateSymbolType(int newSymbolType)
    {
        symbolType = newSymbolType;
    }

    public void drawTallHex(ShapeRenderer sr, int originX, int originY, int edgeSize)
    {
        // draws a ‘tall’ hex hex with flat sides . originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral tringle with edge size defined by edgeSize

        double altitudeSize = edgeSize * 0.866025403784439;

        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line((int) (originX + altitudeSize), originY - (edgeSize / 2), (int) (originX + altitudeSize), originY + (edgeSize / 2));
        sr.line((int) (originX + altitudeSize), originY + (edgeSize / 2), originX, originY + edgeSize);
        sr.line(originX, originY + edgeSize, (int) (originX - altitudeSize), originY + (edgeSize / 2)
        );
        sr.line((int) (originX - altitudeSize), originY + (edgeSize / 2), (int) (originX - altitudeSize), originY - (edgeSize / 2));
        sr.line((int) (originX - altitudeSize), originY - (edgeSize / 2), originX, originY - edgeSize);
        sr.line(originX, originY - edgeSize, (int) (originX + altitudeSize), originY - (edgeSize / 2));

    }

    void drawFlower(int index, float ox, float oy, float scale, Color primaryColour, Color secondaryColour, Color tertiaryColour)
    {
        if (index == 0)
        {
// dogwood 4
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 362, oy + scale * 198);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 362, oy + scale * 198, ox + scale * 380, oy + scale * 240);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 260, oy + scale * 240, ox + scale * 278, oy + scale * 198);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 278, oy + scale * 198, ox + scale * 320, oy + scale * 180);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 300, ox + scale * 278, oy + scale * 282);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 278, oy + scale * 282, ox + scale * 260, oy + scale * 240);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 380, oy + scale * 240, ox + scale * 362, oy + scale * 282);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 362, oy + scale * 282, ox + scale * 320, oy + scale * 300);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 339, oy + scale * 173, ox + scale * 344, oy + scale * 146);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 344, oy + scale * 146, ox + scale * 360, oy + scale * 125);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 360, oy + scale * 125, ox + scale * 383, oy + scale * 112);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 383, oy + scale * 112, ox + scale * 409, oy + scale * 107);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 409, oy + scale * 107, ox + scale * 438, oy + scale * 109);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 438, oy + scale * 109, ox + scale * 453, oy + scale * 107);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 453, oy + scale * 107, ox + scale * 452, oy + scale * 122);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 452, oy + scale * 122, ox + scale * 453, oy + scale * 151);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 453, oy + scale * 151, ox + scale * 448, oy + scale * 177);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 448, oy + scale * 177, ox + scale * 434, oy + scale * 200);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 434, oy + scale * 200, ox + scale * 415, oy + scale * 217);
            renderer.triangle(ox + scale * 340, oy + scale * 221, ox + scale * 415, oy + scale * 217, ox + scale * 387, oy + scale * 221);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 253, oy + scale * 221, ox + scale * 226, oy + scale * 216);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 226, oy + scale * 216, ox + scale * 205, oy + scale * 200);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 205, oy + scale * 200, ox + scale * 192, oy + scale * 177);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 192, oy + scale * 177, ox + scale * 187, oy + scale * 151);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 187, oy + scale * 151, ox + scale * 189, oy + scale * 122);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 189, oy + scale * 122, ox + scale * 187, oy + scale * 107);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 187, oy + scale * 107, ox + scale * 202, oy + scale * 108);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 202, oy + scale * 108, ox + scale * 231, oy + scale * 107);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 231, oy + scale * 107, ox + scale * 257, oy + scale * 112);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 257, oy + scale * 112, ox + scale * 280, oy + scale * 126);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 280, oy + scale * 126, ox + scale * 297, oy + scale * 145);
            renderer.triangle(ox + scale * 301, oy + scale * 220, ox + scale * 297, oy + scale * 145, ox + scale * 301, oy + scale * 173);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 301, oy + scale * 307, ox + scale * 296, oy + scale * 334);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 296, oy + scale * 334, ox + scale * 280, oy + scale * 355);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 280, oy + scale * 355, ox + scale * 257, oy + scale * 368);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 257, oy + scale * 368, ox + scale * 231, oy + scale * 373);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 231, oy + scale * 373, ox + scale * 202, oy + scale * 371);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 202, oy + scale * 371, ox + scale * 187, oy + scale * 373);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 187, oy + scale * 373, ox + scale * 188, oy + scale * 358);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 188, oy + scale * 358, ox + scale * 187, oy + scale * 329);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 187, oy + scale * 329, ox + scale * 192, oy + scale * 303);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 192, oy + scale * 303, ox + scale * 206, oy + scale * 280);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 206, oy + scale * 280, ox + scale * 225, oy + scale * 263);
            renderer.triangle(ox + scale * 300, oy + scale * 259, ox + scale * 225, oy + scale * 263, ox + scale * 253, oy + scale * 259);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 387, oy + scale * 259, ox + scale * 414, oy + scale * 264);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 414, oy + scale * 264, ox + scale * 435, oy + scale * 280);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 435, oy + scale * 280, ox + scale * 448, oy + scale * 303);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 448, oy + scale * 303, ox + scale * 453, oy + scale * 329);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 453, oy + scale * 329, ox + scale * 451, oy + scale * 358);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 451, oy + scale * 358, ox + scale * 453, oy + scale * 373);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 453, oy + scale * 373, ox + scale * 438, oy + scale * 372);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 438, oy + scale * 372, ox + scale * 409, oy + scale * 373);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 409, oy + scale * 373, ox + scale * 383, oy + scale * 368);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 383, oy + scale * 368, ox + scale * 360, oy + scale * 354);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 360, oy + scale * 354, ox + scale * 343, oy + scale * 335);
            renderer.triangle(ox + scale * 339, oy + scale * 260, ox + scale * 343, oy + scale * 335, ox + scale * 339, oy + scale * 307);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 366, oy + scale * 194, ox + scale * 371, oy + scale * 171, ox + scale * 421, oy + scale * 139);
            renderer.triangle(ox + scale * 366, oy + scale * 194, ox + scale * 421, oy + scale * 139, ox + scale * 390, oy + scale * 189);
            renderer.triangle(ox + scale * 274, oy + scale * 194, ox + scale * 251, oy + scale * 189, ox + scale * 219, oy + scale * 139);
            renderer.triangle(ox + scale * 274, oy + scale * 194, ox + scale * 219, oy + scale * 139, ox + scale * 269, oy + scale * 170);
            renderer.triangle(ox + scale * 274, oy + scale * 286, ox + scale * 269, oy + scale * 309, ox + scale * 219, oy + scale * 341);
            renderer.triangle(ox + scale * 274, oy + scale * 286, ox + scale * 219, oy + scale * 341, ox + scale * 250, oy + scale * 291);
            renderer.triangle(ox + scale * 366, oy + scale * 286, ox + scale * 389, oy + scale * 291, ox + scale * 421, oy + scale * 341);
            renderer.triangle(ox + scale * 366, oy + scale * 286, ox + scale * 421, oy + scale * 341, ox + scale * 371, oy + scale * 310);
        } else if (index == 1)
        {
            //vinca 5
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 323, oy + scale * 186, ox + scale * 326, oy + scale * 62, ox + scale * 415, oy + scale * 110);
            renderer.triangle(ox + scale * 323, oy + scale * 186, ox + scale * 415, oy + scale * 110, ox + scale * 486, oy + scale * 179);
            renderer.triangle(ox + scale * 323, oy + scale * 186, ox + scale * 486, oy + scale * 179, ox + scale * 371, oy + scale * 220);
            renderer.triangle(ox + scale * 270, oy + scale * 220, ox + scale * 153, oy + scale * 179, ox + scale * 225, oy + scale * 110);
            renderer.triangle(ox + scale * 270, oy + scale * 220, ox + scale * 225, oy + scale * 110, ox + scale * 314, oy + scale * 63);
            renderer.triangle(ox + scale * 270, oy + scale * 220, ox + scale * 314, oy + scale * 63, ox + scale * 317, oy + scale * 185);
            renderer.triangle(ox + scale * 286, oy + scale * 282, ox + scale * 210, oy + scale * 380, ox + scale * 167, oy + scale * 290);
            renderer.triangle(ox + scale * 286, oy + scale * 282, ox + scale * 167, oy + scale * 290, ox + scale * 150, oy + scale * 191);
            renderer.triangle(ox + scale * 286, oy + scale * 282, ox + scale * 150, oy + scale * 191, ox + scale * 267, oy + scale * 226);
            renderer.triangle(ox + scale * 349, oy + scale * 286, ox + scale * 419, oy + scale * 388, ox + scale * 320, oy + scale * 401);
            renderer.triangle(ox + scale * 349, oy + scale * 286, ox + scale * 320, oy + scale * 401, ox + scale * 221, oy + scale * 387);
            renderer.triangle(ox + scale * 349, oy + scale * 286, ox + scale * 221, oy + scale * 387, ox + scale * 290, oy + scale * 286);
            renderer.triangle(ox + scale * 372, oy + scale * 226, ox + scale * 491, oy + scale * 191, ox + scale * 473, oy + scale * 290);
            renderer.triangle(ox + scale * 372, oy + scale * 226, ox + scale * 473, oy + scale * 290, ox + scale * 429, oy + scale * 379);
            renderer.triangle(ox + scale * 372, oy + scale * 226, ox + scale * 429, oy + scale * 379, ox + scale * 355, oy + scale * 283);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 339, oy + scale * 185);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 339, oy + scale * 185, ox + scale * 356, oy + scale * 192);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 356, oy + scale * 192, ox + scale * 366, oy + scale * 205);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 366, oy + scale * 205, ox + scale * 377, oy + scale * 221);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 263, oy + scale * 221, ox + scale * 274, oy + scale * 205);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 274, oy + scale * 205, ox + scale * 285, oy + scale * 191);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 285, oy + scale * 191, ox + scale * 301, oy + scale * 185);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 301, oy + scale * 185, ox + scale * 320, oy + scale * 180);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 285, oy + scale * 289, ox + scale * 273, oy + scale * 273);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 273, oy + scale * 273, ox + scale * 263, oy + scale * 258);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 263, oy + scale * 258, ox + scale * 262, oy + scale * 242);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 262, oy + scale * 242, ox + scale * 263, oy + scale * 221);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 355, oy + scale * 289, ox + scale * 337, oy + scale * 295);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 337, oy + scale * 295, ox + scale * 319, oy + scale * 300);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 319, oy + scale * 300, ox + scale * 303, oy + scale * 296);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 303, oy + scale * 296, ox + scale * 285, oy + scale * 289);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 221, ox + scale * 378, oy + scale * 241);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 378, oy + scale * 241, ox + scale * 377, oy + scale * 259);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 259, ox + scale * 368, oy + scale * 273);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 368, oy + scale * 273, ox + scale * 355, oy + scale * 289);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 339, oy + scale * 185, ox + scale * 381, oy + scale * 156, ox + scale * 366, oy + scale * 206);
            renderer.triangle(ox + scale * 274, oy + scale * 205, ox + scale * 259, oy + scale * 156, ox + scale * 302, oy + scale * 185);
            renderer.triangle(ox + scale * 272, oy + scale * 273, ox + scale * 222, oy + scale * 272, ox + scale * 263, oy + scale * 240);
            renderer.triangle(ox + scale * 337, oy + scale * 296, ox + scale * 320, oy + scale * 343, ox + scale * 302, oy + scale * 295);
            renderer.triangle(ox + scale * 378, oy + scale * 242, ox + scale * 418, oy + scale * 272, ox + scale * 366, oy + scale * 274);
        } else if (index == 2)
        {
            //sunflower 10
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 120, ox + scale * 324, oy + scale * 85);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 324, oy + scale * 85, ox + scale * 333, oy + scale * 70);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 333, oy + scale * 70, ox + scale * 345, oy + scale * 65);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 345, oy + scale * 65, ox + scale * 363, oy + scale * 65);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 363, oy + scale * 65, ox + scale * 376, oy + scale * 67);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 376, oy + scale * 67, ox + scale * 388, oy + scale * 72);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 388, oy + scale * 72, ox + scale * 402, oy + scale * 83);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 402, oy + scale * 83, ox + scale * 409, oy + scale * 95);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 409, oy + scale * 95, ox + scale * 407, oy + scale * 113);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 407, oy + scale * 113, ox + scale * 390, oy + scale * 144);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 249, oy + scale * 143, ox + scale * 232, oy + scale * 112);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 232, oy + scale * 112, ox + scale * 230, oy + scale * 95);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 230, oy + scale * 95, ox + scale * 237, oy + scale * 84);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 237, oy + scale * 84, ox + scale * 252, oy + scale * 73);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 252, oy + scale * 73, ox + scale * 263, oy + scale * 68);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 263, oy + scale * 68, ox + scale * 276, oy + scale * 65);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 276, oy + scale * 65, ox + scale * 294, oy + scale * 64);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 294, oy + scale * 64, ox + scale * 307, oy + scale * 71);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 307, oy + scale * 71, ox + scale * 316, oy + scale * 86);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 316, oy + scale * 86, ox + scale * 321, oy + scale * 121);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 206, oy + scale * 203, ox + scale * 174, oy + scale * 188);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 174, oy + scale * 188, ox + scale * 162, oy + scale * 176);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 162, oy + scale * 176, ox + scale * 161, oy + scale * 162);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 161, oy + scale * 162, ox + scale * 166, oy + scale * 145);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 166, oy + scale * 145, ox + scale * 173, oy + scale * 134);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 173, oy + scale * 134, ox + scale * 182, oy + scale * 124);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 182, oy + scale * 124, ox + scale * 196, oy + scale * 113);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 196, oy + scale * 113, ox + scale * 210, oy + scale * 111);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 210, oy + scale * 111, ox + scale * 226, oy + scale * 118);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 226, oy + scale * 118, ox + scale * 251, oy + scale * 143);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 206, oy + scale * 277, ox + scale * 171, oy + scale * 284);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 171, oy + scale * 284, ox + scale * 155, oy + scale * 280);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 155, oy + scale * 280, ox + scale * 146, oy + scale * 270);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 146, oy + scale * 270, ox + scale * 140, oy + scale * 253);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 140, oy + scale * 253, ox + scale * 139, oy + scale * 241);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 139, oy + scale * 241, ox + scale * 140, oy + scale * 227);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 140, oy + scale * 227, ox + scale * 145, oy + scale * 210);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 145, oy + scale * 210, ox + scale * 155, oy + scale * 200);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 155, oy + scale * 200, ox + scale * 172, oy + scale * 197);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 172, oy + scale * 197, ox + scale * 207, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 249, oy + scale * 337, ox + scale * 226, oy + scale * 363);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 226, oy + scale * 363, ox + scale * 210, oy + scale * 370);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 210, oy + scale * 370, ox + scale * 197, oy + scale * 367);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 197, oy + scale * 367, ox + scale * 182, oy + scale * 357);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 182, oy + scale * 357, ox + scale * 174, oy + scale * 347);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 174, oy + scale * 347, ox + scale * 167, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 167, oy + scale * 336, ox + scale * 161, oy + scale * 319);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 161, oy + scale * 319, ox + scale * 163, oy + scale * 305);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 163, oy + scale * 305, ox + scale * 175, oy + scale * 292);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 175, oy + scale * 292, ox + scale * 207, oy + scale * 276);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 360, ox + scale * 316, oy + scale * 395);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 316, oy + scale * 395, ox + scale * 307, oy + scale * 410);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 307, oy + scale * 410, ox + scale * 295, oy + scale * 415);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 295, oy + scale * 415, ox + scale * 277, oy + scale * 415);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 277, oy + scale * 415, ox + scale * 264, oy + scale * 413);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 264, oy + scale * 413, ox + scale * 252, oy + scale * 408);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 252, oy + scale * 408, ox + scale * 238, oy + scale * 397);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 238, oy + scale * 397, ox + scale * 231, oy + scale * 385);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 231, oy + scale * 385, ox + scale * 233, oy + scale * 367);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 233, oy + scale * 367, ox + scale * 250, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 391, oy + scale * 337, ox + scale * 408, oy + scale * 368);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 408, oy + scale * 368, ox + scale * 410, oy + scale * 385);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 410, oy + scale * 385, ox + scale * 403, oy + scale * 396);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 403, oy + scale * 396, ox + scale * 388, oy + scale * 407);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 388, oy + scale * 407, ox + scale * 377, oy + scale * 412);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 412, ox + scale * 364, oy + scale * 415);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 364, oy + scale * 415, ox + scale * 346, oy + scale * 416);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 346, oy + scale * 416, ox + scale * 333, oy + scale * 409);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 333, oy + scale * 409, ox + scale * 324, oy + scale * 394);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 324, oy + scale * 394, ox + scale * 319, oy + scale * 359);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 434, oy + scale * 277, ox + scale * 466, oy + scale * 292);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 466, oy + scale * 292, ox + scale * 478, oy + scale * 304);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 478, oy + scale * 304, ox + scale * 479, oy + scale * 318);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 479, oy + scale * 318, ox + scale * 474, oy + scale * 335);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 474, oy + scale * 335, ox + scale * 467, oy + scale * 346);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 467, oy + scale * 346, ox + scale * 458, oy + scale * 356);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 458, oy + scale * 356, ox + scale * 444, oy + scale * 367);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 444, oy + scale * 367, ox + scale * 430, oy + scale * 369);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 430, oy + scale * 369, ox + scale * 414, oy + scale * 362);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 414, oy + scale * 362, ox + scale * 389, oy + scale * 337);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 434, oy + scale * 203, ox + scale * 469, oy + scale * 196);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 469, oy + scale * 196, ox + scale * 485, oy + scale * 200);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 485, oy + scale * 200, ox + scale * 494, oy + scale * 210);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 494, oy + scale * 210, ox + scale * 500, oy + scale * 227);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 500, oy + scale * 227, ox + scale * 501, oy + scale * 239);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 501, oy + scale * 239, ox + scale * 500, oy + scale * 253);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 500, oy + scale * 253, ox + scale * 495, oy + scale * 270);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 495, oy + scale * 270, ox + scale * 485, oy + scale * 280);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 485, oy + scale * 280, ox + scale * 468, oy + scale * 283);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 468, oy + scale * 283, ox + scale * 433, oy + scale * 277);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 391, oy + scale * 143, ox + scale * 414, oy + scale * 117);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 414, oy + scale * 117, ox + scale * 430, oy + scale * 110);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 430, oy + scale * 110, ox + scale * 443, oy + scale * 113);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 443, oy + scale * 113, ox + scale * 458, oy + scale * 123);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 458, oy + scale * 123, ox + scale * 466, oy + scale * 133);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 466, oy + scale * 133, ox + scale * 473, oy + scale * 144);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 473, oy + scale * 144, ox + scale * 479, oy + scale * 161);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 479, oy + scale * 161, ox + scale * 477, oy + scale * 175);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 477, oy + scale * 175, ox + scale * 465, oy + scale * 188);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 465, oy + scale * 188, ox + scale * 433, oy + scale * 204);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 355, oy + scale * 191);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 285, oy + scale * 191, ox + scale * 320, oy + scale * 180);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 263, oy + scale * 221, ox + scale * 285, oy + scale * 191);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 263, oy + scale * 259, ox + scale * 263, oy + scale * 221);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 285, oy + scale * 289, ox + scale * 263, oy + scale * 259);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 300, ox + scale * 285, oy + scale * 289);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 355, oy + scale * 289, ox + scale * 320, oy + scale * 300);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 259, ox + scale * 355, oy + scale * 289);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 221, ox + scale * 377, oy + scale * 259);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 355, oy + scale * 191, ox + scale * 377, oy + scale * 221);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 120, ox + scale * 337, oy + scale * 122);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 337, oy + scale * 122, ox + scale * 348, oy + scale * 125);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 348, oy + scale * 125, ox + scale * 357, oy + scale * 126);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 357, oy + scale * 126, ox + scale * 365, oy + scale * 129);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 365, oy + scale * 129, ox + scale * 376, oy + scale * 134);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 376, oy + scale * 134, ox + scale * 390, oy + scale * 144);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 249, oy + scale * 143, ox + scale * 264, oy + scale * 135);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 264, oy + scale * 135, ox + scale * 275, oy + scale * 130);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 275, oy + scale * 130, ox + scale * 283, oy + scale * 126);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 283, oy + scale * 126, ox + scale * 291, oy + scale * 123);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 291, oy + scale * 123, ox + scale * 303, oy + scale * 122);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 303, oy + scale * 122, ox + scale * 320, oy + scale * 121);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 206, oy + scale * 203, ox + scale * 213, oy + scale * 188);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 213, oy + scale * 188, ox + scale * 219, oy + scale * 178);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 219, oy + scale * 178, ox + scale * 223, oy + scale * 169);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 223, oy + scale * 169, ox + scale * 228, oy + scale * 162);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 228, oy + scale * 162, ox + scale * 237, oy + scale * 154);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 237, oy + scale * 154, ox + scale * 250, oy + scale * 144);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 206, oy + scale * 277, ox + scale * 203, oy + scale * 261);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 203, oy + scale * 261, ox + scale * 202, oy + scale * 249);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 202, oy + scale * 249, ox + scale * 200, oy + scale * 240);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 200, oy + scale * 240, ox + scale * 200, oy + scale * 231);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 200, oy + scale * 231, ox + scale * 202, oy + scale * 220);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 202, oy + scale * 220, ox + scale * 207, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 249, oy + scale * 337, ox + scale * 237, oy + scale * 326);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 237, oy + scale * 326, ox + scale * 229, oy + scale * 317);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 229, oy + scale * 317, ox + scale * 223, oy + scale * 311);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 223, oy + scale * 311, ox + scale * 218, oy + scale * 303);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 218, oy + scale * 303, ox + scale * 213, oy + scale * 293);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 213, oy + scale * 293, ox + scale * 207, oy + scale * 277);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 360, ox + scale * 303, oy + scale * 358);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 303, oy + scale * 358, ox + scale * 292, oy + scale * 355);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 292, oy + scale * 355, ox + scale * 283, oy + scale * 354);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 283, oy + scale * 354, ox + scale * 275, oy + scale * 351);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 275, oy + scale * 351, ox + scale * 264, oy + scale * 346);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 264, oy + scale * 346, ox + scale * 250, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 391, oy + scale * 337, ox + scale * 376, oy + scale * 345);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 376, oy + scale * 345, ox + scale * 365, oy + scale * 350);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 365, oy + scale * 350, ox + scale * 357, oy + scale * 354);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 357, oy + scale * 354, ox + scale * 349, oy + scale * 357);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 349, oy + scale * 357, ox + scale * 337, oy + scale * 358);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 337, oy + scale * 358, ox + scale * 320, oy + scale * 359);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 434, oy + scale * 277, ox + scale * 427, oy + scale * 292);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 427, oy + scale * 292, ox + scale * 421, oy + scale * 302);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 421, oy + scale * 302, ox + scale * 417, oy + scale * 311);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 417, oy + scale * 311, ox + scale * 412, oy + scale * 318);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 412, oy + scale * 318, ox + scale * 403, oy + scale * 326);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 403, oy + scale * 326, ox + scale * 390, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 434, oy + scale * 203, ox + scale * 437, oy + scale * 219);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 437, oy + scale * 219, ox + scale * 438, oy + scale * 231);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 438, oy + scale * 231, ox + scale * 440, oy + scale * 240);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 440, oy + scale * 240, ox + scale * 440, oy + scale * 249);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 440, oy + scale * 249, ox + scale * 438, oy + scale * 260);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 438, oy + scale * 260, ox + scale * 433, oy + scale * 277);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 391, oy + scale * 143, ox + scale * 403, oy + scale * 154);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 403, oy + scale * 154, ox + scale * 411, oy + scale * 163);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 411, oy + scale * 163, ox + scale * 417, oy + scale * 169);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 417, oy + scale * 169, ox + scale * 422, oy + scale * 177);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 422, oy + scale * 177, ox + scale * 427, oy + scale * 187);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 427, oy + scale * 187, ox + scale * 433, oy + scale * 203);
        } else if (index == 3)
        {
            //hibiscus 5
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 301, oy + scale * 183, ox + scale * 321, oy + scale * 122);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 122, ox + scale * 371, oy + scale * 87);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 371, oy + scale * 87, ox + scale * 426, oy + scale * 96);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 426, oy + scale * 96, ox + scale * 471, oy + scale * 142);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 471, oy + scale * 142, ox + scale * 488, oy + scale * 248);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 482, oy + scale * 196, ox + scale * 466, oy + scale * 231);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 260, oy + scale * 240, ox + scale * 208, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 208, oy + scale * 203, ox + scale * 191, oy + scale * 144);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 191, oy + scale * 144, ox + scale * 215, oy + scale * 95);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 215, oy + scale * 95, ox + scale * 273, oy + scale * 66);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 273, oy + scale * 66, ox + scale * 380, oy + scale * 83);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 328, oy + scale * 72, ox + scale * 357, oy + scale * 98);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 302, oy + scale * 297, ox + scale * 250, oy + scale * 335);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 250, oy + scale * 335, ox + scale * 189, oy + scale * 334);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 189, oy + scale * 334, ox + scale * 150, oy + scale * 295);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 150, oy + scale * 295, ox + scale * 140, oy + scale * 231);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 140, oy + scale * 231, ox + scale * 189, oy + scale * 135);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 163, oy + scale * 180, ox + scale * 197, oy + scale * 161);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 368, oy + scale * 275, ox + scale * 389, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 389, oy + scale * 336, ox + scale * 369, oy + scale * 393);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 369, oy + scale * 393, ox + scale * 319, oy + scale * 419);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 319, oy + scale * 419, ox + scale * 255, oy + scale * 409);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 255, oy + scale * 409, ox + scale * 179, oy + scale * 332);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 215, oy + scale * 371, ox + scale * 207, oy + scale * 333);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 368, oy + scale * 205, ox + scale * 432, oy + scale * 204);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 432, oy + scale * 204, ox + scale * 481, oy + scale * 241);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 481, oy + scale * 241, ox + scale * 490, oy + scale * 296);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 490, oy + scale * 296, ox + scale * 460, oy + scale * 354);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 460, oy + scale * 354, ox + scale * 364, oy + scale * 402);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 412, oy + scale * 381, ox + scale * 373, oy + scale * 376);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 383, oy + scale * 153, ox + scale * 422, oy + scale * 205, ox + scale * 390, oy + scale * 205);
            renderer.triangle(ox + scale * 383, oy + scale * 153, ox + scale * 390, oy + scale * 205, ox + scale * 378, oy + scale * 161);
            renderer.triangle(ox + scale * 383, oy + scale * 153, ox + scale * 378, oy + scale * 161, ox + scale * 330, oy + scale * 191);
            renderer.triangle(ox + scale * 383, oy + scale * 153, ox + scale * 330, oy + scale * 191, ox + scale * 330, oy + scale * 187);
            renderer.triangle(ox + scale * 257, oy + scale * 153, ox + scale * 318, oy + scale * 132, ox + scale * 308, oy + scale * 162);
            renderer.triangle(ox + scale * 257, oy + scale * 153, ox + scale * 308, oy + scale * 162, ox + scale * 263, oy + scale * 161);
            renderer.triangle(ox + scale * 257, oy + scale * 153, ox + scale * 263, oy + scale * 161, ox + scale * 277, oy + scale * 216);
            renderer.triangle(ox + scale * 257, oy + scale * 153, ox + scale * 277, oy + scale * 216, ox + scale * 273, oy + scale * 215);
            renderer.triangle(ox + scale * 218, oy + scale * 273, ox + scale * 217, oy + scale * 209, ox + scale * 242, oy + scale * 227);
            renderer.triangle(ox + scale * 218, oy + scale * 273, ox + scale * 242, oy + scale * 227, ox + scale * 227, oy + scale * 270);
            renderer.triangle(ox + scale * 218, oy + scale * 273, ox + scale * 227, oy + scale * 270, ox + scale * 284, oy + scale * 274);
            renderer.triangle(ox + scale * 218, oy + scale * 273, ox + scale * 284, oy + scale * 274, ox + scale * 281, oy + scale * 277);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 258, oy + scale * 328, ox + scale * 284, oy + scale * 310);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 284, oy + scale * 310, ox + scale * 319, oy + scale * 338);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 319, oy + scale * 338, ox + scale * 341, oy + scale * 285);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 341, oy + scale * 285, ox + scale * 343, oy + scale * 288);
            renderer.triangle(ox + scale * 422, oy + scale * 273, ox + scale * 385, oy + scale * 326, ox + scale * 375, oy + scale * 296);
            renderer.triangle(ox + scale * 422, oy + scale * 273, ox + scale * 375, oy + scale * 296, ox + scale * 413, oy + scale * 271);
            renderer.triangle(ox + scale * 422, oy + scale * 273, ox + scale * 413, oy + scale * 271, ox + scale * 369, oy + scale * 234);
            renderer.triangle(ox + scale * 422, oy + scale * 273, ox + scale * 369, oy + scale * 234, ox + scale * 373, oy + scale * 233);
            renderer.triangle(ox + scale * 330, oy + scale * 187, ox + scale * 318, oy + scale * 208, ox + scale * 320, oy + scale * 209);
            renderer.triangle(ox + scale * 330, oy + scale * 187, ox + scale * 320, oy + scale * 209, ox + scale * 330, oy + scale * 190);
            renderer.triangle(ox + scale * 273, oy + scale * 215, ox + scale * 289, oy + scale * 232, ox + scale * 291, oy + scale * 230);
            renderer.triangle(ox + scale * 273, oy + scale * 215, ox + scale * 291, oy + scale * 230, ox + scale * 276, oy + scale * 215);
            renderer.triangle(ox + scale * 281, oy + scale * 277, ox + scale * 303, oy + scale * 267, ox + scale * 302, oy + scale * 265);
            renderer.triangle(ox + scale * 281, oy + scale * 277, ox + scale * 302, oy + scale * 265, ox + scale * 282, oy + scale * 274);
            renderer.triangle(ox + scale * 343, oy + scale * 288, ox + scale * 340, oy + scale * 265, ox + scale * 338, oy + scale * 265);
            renderer.triangle(ox + scale * 343, oy + scale * 288, ox + scale * 338, oy + scale * 265, ox + scale * 341, oy + scale * 286);
            renderer.triangle(ox + scale * 373, oy + scale * 233, ox + scale * 350, oy + scale * 228, ox + scale * 349, oy + scale * 230);
            renderer.triangle(ox + scale * 373, oy + scale * 233, ox + scale * 349, oy + scale * 230, ox + scale * 371, oy + scale * 234);
        } else if (index == 4)
        {
            //hibiscus 3
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 290, oy + scale * 189, ox + scale * 321, oy + scale * 122);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 122, ox + scale * 402, oy + scale * 102);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 402, oy + scale * 102, ox + scale * 475, oy + scale * 151);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 475, oy + scale * 151, ox + scale * 500, oy + scale * 256);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 500, oy + scale * 256, ox + scale * 488, oy + scale * 253);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 459, oy + scale * 335, ox + scale * 405, oy + scale * 359);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 290, oy + scale * 292, ox + scale * 217, oy + scale * 298);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 217, oy + scale * 298, ox + scale * 159, oy + scale * 238);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 159, oy + scale * 238, ox + scale * 166, oy + scale * 150);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 166, oy + scale * 150, ox + scale * 244, oy + scale * 76);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 244, oy + scale * 76, ox + scale * 248, oy + scale * 88);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 333, oy + scale * 72, ox + scale * 381, oy + scale * 107);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 380, oy + scale * 240, ox + scale * 422, oy + scale * 300);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 422, oy + scale * 300, ox + scale * 399, oy + scale * 380);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 399, oy + scale * 380, ox + scale * 319, oy + scale * 419);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 319, oy + scale * 419, ox + scale * 216, oy + scale * 388);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 216, oy + scale * 388, ox + scale * 225, oy + scale * 379);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 168, oy + scale * 312, ox + scale * 174, oy + scale * 254);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 413, oy + scale * 186, ox + scale * 415, oy + scale * 291, ox + scale * 396, oy + scale * 261);
            renderer.triangle(ox + scale * 413, oy + scale * 186, ox + scale * 396, oy + scale * 261, ox + scale * 405, oy + scale * 192);
            renderer.triangle(ox + scale * 413, oy + scale * 186, ox + scale * 405, oy + scale * 192, ox + scale * 336, oy + scale * 193);
            renderer.triangle(ox + scale * 413, oy + scale * 186, ox + scale * 336, oy + scale * 193, ox + scale * 336, oy + scale * 189);
            renderer.triangle(ox + scale * 227, oy + scale * 186, ox + scale * 317, oy + scale * 132, ox + scale * 300, oy + scale * 164);
            renderer.triangle(ox + scale * 227, oy + scale * 186, ox + scale * 300, oy + scale * 164, ox + scale * 236, oy + scale * 190);
            renderer.triangle(ox + scale * 227, oy + scale * 186, ox + scale * 236, oy + scale * 190, ox + scale * 271, oy + scale * 250);
            renderer.triangle(ox + scale * 227, oy + scale * 186, ox + scale * 271, oy + scale * 250, ox + scale * 268, oy + scale * 252);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 228, oy + scale * 297, ox + scale * 264, oy + scale * 295);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 264, oy + scale * 295, ox + scale * 319, oy + scale * 338);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 319, oy + scale * 338, ox + scale * 353, oy + scale * 277);
            renderer.triangle(ox + scale * 320, oy + scale * 348, ox + scale * 353, oy + scale * 277, ox + scale * 356, oy + scale * 279);
            renderer.triangle(ox + scale * 336, oy + scale * 189, ox + scale * 317, oy + scale * 208, ox + scale * 320, oy + scale * 209);
            renderer.triangle(ox + scale * 336, oy + scale * 189, ox + scale * 320, oy + scale * 209, ox + scale * 337, oy + scale * 192);
            renderer.triangle(ox + scale * 268, oy + scale * 252, ox + scale * 294, oy + scale * 259, ox + scale * 293, oy + scale * 256);
            renderer.triangle(ox + scale * 268, oy + scale * 252, ox + scale * 293, oy + scale * 256, ox + scale * 270, oy + scale * 249);
            renderer.triangle(ox + scale * 356, oy + scale * 279, ox + scale * 349, oy + scale * 253, ox + scale * 347, oy + scale * 256);
            renderer.triangle(ox + scale * 356, oy + scale * 279, ox + scale * 347, oy + scale * 256, ox + scale * 353, oy + scale * 279);
        } else if (index == 5)
        {
//dogwood 4 petal simple
            renderer.setColor(secondaryColour);

            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 380, oy + scale * 240, ox + scale * 320, oy + scale * 180);
            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 364, oy + scale * 227, ox + scale * 391, oy + scale * 232);
            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 391, oy + scale * 232, ox + scale * 416, oy + scale * 227);
            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 416, oy + scale * 227, ox + scale * 448, oy + scale * 114);
            renderer.setColor(primaryColour);

            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 448, oy + scale * 114, ox + scale * 334, oy + scale * 148);
            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 334, oy + scale * 148, ox + scale * 329, oy + scale * 170);
            renderer.triangle(ox + scale * 325, oy + scale * 236, ox + scale * 329, oy + scale * 170, ox + scale * 336, oy + scale * 213);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 260, oy + scale * 240);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 307, oy + scale * 196, ox + scale * 312, oy + scale * 169);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 312, oy + scale * 169, ox + scale * 307, oy + scale * 144);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 307, oy + scale * 144, ox + scale * 194, oy + scale * 112);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 194, oy + scale * 112, ox + scale * 228, oy + scale * 226);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 228, oy + scale * 226, ox + scale * 250, oy + scale * 231);
            renderer.triangle(ox + scale * 316, oy + scale * 235, ox + scale * 250, oy + scale * 231, ox + scale * 293, oy + scale * 224);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 260, oy + scale * 240, ox + scale * 320, oy + scale * 300);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 276, oy + scale * 253, ox + scale * 249, oy + scale * 248);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 249, oy + scale * 248, ox + scale * 224, oy + scale * 253);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 224, oy + scale * 253, ox + scale * 192, oy + scale * 366);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 192, oy + scale * 366, ox + scale * 306, oy + scale * 332);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 306, oy + scale * 332, ox + scale * 311, oy + scale * 310);
            renderer.triangle(ox + scale * 315, oy + scale * 244, ox + scale * 311, oy + scale * 310, ox + scale * 304, oy + scale * 267);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 300, ox + scale * 380, oy + scale * 240);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 333, oy + scale * 284, ox + scale * 328, oy + scale * 311);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 328, oy + scale * 311, ox + scale * 333, oy + scale * 336);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 333, oy + scale * 336, ox + scale * 446, oy + scale * 368);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 446, oy + scale * 368, ox + scale * 412, oy + scale * 254);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 412, oy + scale * 254, ox + scale * 390, oy + scale * 249);
            renderer.triangle(ox + scale * 324, oy + scale * 245, ox + scale * 390, oy + scale * 249, ox + scale * 347, oy + scale * 256);

        } else if (index == 6)
        {
            //moonflower 5
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 120, ox + scale * 374, oy + scale * 98);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 374, oy + scale * 98, ox + scale * 425, oy + scale * 94);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 425, oy + scale * 94, ox + scale * 439, oy + scale * 146);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 439, oy + scale * 146, ox + scale * 434, oy + scale * 204);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 206, oy + scale * 203, ox + scale * 201, oy + scale * 145);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 201, oy + scale * 145, ox + scale * 214, oy + scale * 95);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 214, oy + scale * 95, ox + scale * 267, oy + scale * 97);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 267, oy + scale * 97, ox + scale * 321, oy + scale * 120);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 249, oy + scale * 337, ox + scale * 193, oy + scale * 323);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 193, oy + scale * 323, ox + scale * 150, oy + scale * 297);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 150, oy + scale * 297, ox + scale * 168, oy + scale * 246);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 168, oy + scale * 246, ox + scale * 207, oy + scale * 202);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 391, oy + scale * 337, ox + scale * 360, oy + scale * 386);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 360, oy + scale * 386, ox + scale * 321, oy + scale * 420);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 420, ox + scale * 279, oy + scale * 386);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 279, oy + scale * 386, ox + scale * 249, oy + scale * 336);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 434, oy + scale * 203, ox + scale * 472, oy + scale * 247);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 472, oy + scale * 247, ox + scale * 491, oy + scale * 294);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 491, oy + scale * 294, ox + scale * 446, oy + scale * 325);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 446, oy + scale * 325, ox + scale * 389, oy + scale * 337);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 123, ox + scale * 321, oy + scale * 123);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 123, ox + scale * 321, oy + scale * 240);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 209, oy + scale * 204, ox + scale * 209, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 209, oy + scale * 203, ox + scale * 320, oy + scale * 239);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 251, oy + scale * 335, ox + scale * 250, oy + scale * 334);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 250, oy + scale * 334, ox + scale * 319, oy + scale * 239);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 389, oy + scale * 335, ox + scale * 388, oy + scale * 335);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 388, oy + scale * 335, ox + scale * 319, oy + scale * 241);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 431, oy + scale * 204, ox + scale * 432, oy + scale * 205);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 432, oy + scale * 205, ox + scale * 320, oy + scale * 241);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 342, oy + scale * 133, ox + scale * 386, oy + scale * 121);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 386, oy + scale * 121, ox + scale * 400, oy + scale * 128);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 400, oy + scale * 128, ox + scale * 412, oy + scale * 141);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 412, oy + scale * 141, ox + scale * 415, oy + scale * 186);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 225, oy + scale * 186, ox + scale * 227, oy + scale * 140);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 227, oy + scale * 140, ox + scale * 238, oy + scale * 129);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 238, oy + scale * 129, ox + scale * 254, oy + scale * 122);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 254, oy + scale * 122, ox + scale * 298, oy + scale * 133);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 239, oy + scale * 314, ox + scale * 197, oy + scale * 297);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 197, oy + scale * 297, ox + scale * 189, oy + scale * 284);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 189, oy + scale * 284, ox + scale * 187, oy + scale * 266);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 187, oy + scale * 266, ox + scale * 211, oy + scale * 228);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 365, oy + scale * 339, ox + scale * 337, oy + scale * 375);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 337, oy + scale * 375, ox + scale * 321, oy + scale * 378);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 378, ox + scale * 304, oy + scale * 374);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 304, oy + scale * 374, ox + scale * 275, oy + scale * 340);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 429, oy + scale * 228, ox + scale * 454, oy + scale * 266);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 454, oy + scale * 266, ox + scale * 451, oy + scale * 281);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 451, oy + scale * 281, ox + scale * 443, oy + scale * 297);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 443, oy + scale * 297, ox + scale * 401, oy + scale * 314);
        } else if (index == 7)
        {
            //daisy 7
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 120, ox + scale * 326, oy + scale * 85);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 326, oy + scale * 85, ox + scale * 338, oy + scale * 71);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 338, oy + scale * 71, ox + scale * 356, oy + scale * 67);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 356, oy + scale * 67, ox + scale * 381, oy + scale * 70);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 381, oy + scale * 70, ox + scale * 398, oy + scale * 76);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 398, oy + scale * 76, ox + scale * 414, oy + scale * 86);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 414, oy + scale * 86, ox + scale * 433, oy + scale * 103);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 433, oy + scale * 103, ox + scale * 440, oy + scale * 120);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 440, oy + scale * 120, ox + scale * 436, oy + scale * 139);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 436, oy + scale * 139, ox + scale * 414, oy + scale * 166);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 226, oy + scale * 165, ox + scale * 203, oy + scale * 139);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 203, oy + scale * 139, ox + scale * 199, oy + scale * 120);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 199, oy + scale * 120, ox + scale * 207, oy + scale * 104);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 207, oy + scale * 104, ox + scale * 225, oy + scale * 86);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 225, oy + scale * 86, ox + scale * 241, oy + scale * 77);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 241, oy + scale * 77, ox + scale * 258, oy + scale * 70);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 258, oy + scale * 70, ox + scale * 283, oy + scale * 66);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 283, oy + scale * 66, ox + scale * 301, oy + scale * 71);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 301, oy + scale * 71, ox + scale * 314, oy + scale * 86);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 314, oy + scale * 86, ox + scale * 321, oy + scale * 121);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 203, oy + scale * 267, ox + scale * 168, oy + scale * 269);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 168, oy + scale * 269, ox + scale * 151, oy + scale * 260);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 151, oy + scale * 260, ox + scale * 143, oy + scale * 244);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 143, oy + scale * 244, ox + scale * 141, oy + scale * 218);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 141, oy + scale * 218, ox + scale * 143, oy + scale * 200);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 143, oy + scale * 200, ox + scale * 149, oy + scale * 182);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 149, oy + scale * 182, ox + scale * 161, oy + scale * 160);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 161, oy + scale * 160, ox + scale * 176, oy + scale * 150);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 176, oy + scale * 150, ox + scale * 196, oy + scale * 149);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 196, oy + scale * 149, ox + scale * 228, oy + scale * 165);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 268, oy + scale * 348, ox + scale * 247, oy + scale * 377);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 247, oy + scale * 377, ox + scale * 230, oy + scale * 385);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 230, oy + scale * 385, ox + scale * 213, oy + scale * 381);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 213, oy + scale * 381, ox + scale * 191, oy + scale * 367);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 191, oy + scale * 367, ox + scale * 179, oy + scale * 354);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 179, oy + scale * 354, ox + scale * 168, oy + scale * 338);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 168, oy + scale * 338, ox + scale * 159, oy + scale * 314);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 159, oy + scale * 314, ox + scale * 160, oy + scale * 296);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 160, oy + scale * 296, ox + scale * 171, oy + scale * 280);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 171, oy + scale * 280, ox + scale * 204, oy + scale * 266);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 372, oy + scale * 348, ox + scale * 382, oy + scale * 382);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 382, oy + scale * 382, ox + scale * 377, oy + scale * 400);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 377, oy + scale * 400, ox + scale * 363, oy + scale * 412);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 363, oy + scale * 412, ox + scale * 339, oy + scale * 420);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 339, oy + scale * 420, ox + scale * 321, oy + scale * 421);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 321, oy + scale * 421, ox + scale * 302, oy + scale * 420);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 302, oy + scale * 420, ox + scale * 278, oy + scale * 412);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 278, oy + scale * 412, ox + scale * 264, oy + scale * 400);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 264, oy + scale * 400, ox + scale * 259, oy + scale * 381);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 259, oy + scale * 381, ox + scale * 268, oy + scale * 347);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 437, oy + scale * 267, ox + scale * 470, oy + scale * 280);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 470, oy + scale * 280, ox + scale * 481, oy + scale * 295);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 481, oy + scale * 295, ox + scale * 481, oy + scale * 313);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 481, oy + scale * 313, ox + scale * 472, oy + scale * 337);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 472, oy + scale * 337, ox + scale * 462, oy + scale * 353);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 462, oy + scale * 353, ox + scale * 449, oy + scale * 366);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 449, oy + scale * 366, ox + scale * 428, oy + scale * 381);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 428, oy + scale * 381, ox + scale * 410, oy + scale * 384);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 410, oy + scale * 384, ox + scale * 392, oy + scale * 376);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 392, oy + scale * 376, ox + scale * 371, oy + scale * 348);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 414, oy + scale * 165, ox + scale * 445, oy + scale * 148);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 445, oy + scale * 148, ox + scale * 464, oy + scale * 149);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 464, oy + scale * 149, ox + scale * 478, oy + scale * 160);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 478, oy + scale * 160, ox + scale * 491, oy + scale * 182);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 491, oy + scale * 182, ox + scale * 497, oy + scale * 199);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 497, oy + scale * 199, ox + scale * 499, oy + scale * 218);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 499, oy + scale * 218, ox + scale * 497, oy + scale * 243);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 497, oy + scale * 243, ox + scale * 489, oy + scale * 259);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 489, oy + scale * 259, ox + scale * 471, oy + scale * 268);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 471, oy + scale * 268, ox + scale * 436, oy + scale * 267);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 367, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 273, oy + scale * 203, ox + scale * 320, oy + scale * 180);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 262, oy + scale * 253, ox + scale * 273, oy + scale * 203);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 294, oy + scale * 294, ox + scale * 262, oy + scale * 253);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 346, oy + scale * 294, ox + scale * 294, oy + scale * 294);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 378, oy + scale * 253, ox + scale * 346, oy + scale * 294);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 367, oy + scale * 203, ox + scale * 378, oy + scale * 253);
        } else if (index == 8)
        {
            //poppy 2
            renderer.setColor(primaryColour);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 343, oy + scale * 189, ox + scale * 344, oy + scale * 151);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 344, oy + scale * 151, ox + scale * 352, oy + scale * 132);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 352, oy + scale * 132, ox + scale * 384, oy + scale * 115);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 384, oy + scale * 115, ox + scale * 428, oy + scale * 121);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 428, oy + scale * 121, ox + scale * 465, oy + scale * 148);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 465, oy + scale * 148, ox + scale * 488, oy + scale * 180);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 488, oy + scale * 180, ox + scale * 498, oy + scale * 216);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 498, oy + scale * 216, ox + scale * 500, oy + scale * 240);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 500, oy + scale * 240, ox + scale * 499, oy + scale * 263);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 499, oy + scale * 263, ox + scale * 489, oy + scale * 299);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 489, oy + scale * 299, ox + scale * 464, oy + scale * 334);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 464, oy + scale * 334, ox + scale * 428, oy + scale * 359);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 428, oy + scale * 359, ox + scale * 382, oy + scale * 366);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 382, oy + scale * 366, ox + scale * 352, oy + scale * 348);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 352, oy + scale * 348, ox + scale * 342, oy + scale * 329);
            renderer.triangle(ox + scale * 348, oy + scale * 240, ox + scale * 342, oy + scale * 329, ox + scale * 342, oy + scale * 292);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 297, oy + scale * 291, ox + scale * 296, oy + scale * 329);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 296, oy + scale * 329, ox + scale * 288, oy + scale * 348);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 288, oy + scale * 348, ox + scale * 256, oy + scale * 365);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 256, oy + scale * 365, ox + scale * 212, oy + scale * 359);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 212, oy + scale * 359, ox + scale * 175, oy + scale * 332);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 175, oy + scale * 332, ox + scale * 152, oy + scale * 300);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 152, oy + scale * 300, ox + scale * 142, oy + scale * 264);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 142, oy + scale * 264, ox + scale * 140, oy + scale * 240);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 140, oy + scale * 240, ox + scale * 141, oy + scale * 217);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 141, oy + scale * 217, ox + scale * 151, oy + scale * 181);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 151, oy + scale * 181, ox + scale * 176, oy + scale * 146);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 176, oy + scale * 146, ox + scale * 212, oy + scale * 121);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 212, oy + scale * 121, ox + scale * 258, oy + scale * 114);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 258, oy + scale * 114, ox + scale * 288, oy + scale * 132);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 288, oy + scale * 132, ox + scale * 298, oy + scale * 151);
            renderer.triangle(ox + scale * 292, oy + scale * 240, ox + scale * 298, oy + scale * 151, ox + scale * 298, oy + scale * 188);
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 245, oy + scale * 110, ox + scale * 270, oy + scale * 101);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 270, oy + scale * 101, ox + scale * 306, oy + scale * 96);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 306, oy + scale * 96, ox + scale * 345, oy + scale * 96);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 345, oy + scale * 96, ox + scale * 367, oy + scale * 99);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 367, oy + scale * 99, ox + scale * 395, oy + scale * 107);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 395, oy + scale * 107, ox + scale * 404, oy + scale * 120);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 404, oy + scale * 120, ox + scale * 336, oy + scale * 216);
            renderer.triangle(ox + scale * 239, oy + scale * 122, ox + scale * 336, oy + scale * 216, ox + scale * 289, oy + scale * 214);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 395, oy + scale * 370, ox + scale * 370, oy + scale * 379);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 370, oy + scale * 379, ox + scale * 334, oy + scale * 384);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 334, oy + scale * 384, ox + scale * 295, oy + scale * 384);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 295, oy + scale * 384, ox + scale * 273, oy + scale * 381);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 273, oy + scale * 381, ox + scale * 245, oy + scale * 373);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 245, oy + scale * 373, ox + scale * 236, oy + scale * 360);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 236, oy + scale * 360, ox + scale * 304, oy + scale * 264);
            renderer.triangle(ox + scale * 401, oy + scale * 358, ox + scale * 304, oy + scale * 264, ox + scale * 351, oy + scale * 266);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 406, oy + scale * 157, ox + scale * 423, oy + scale * 179, ox + scale * 422, oy + scale * 161);
            renderer.triangle(ox + scale * 234, oy + scale * 323, ox + scale * 217, oy + scale * 301, ox + scale * 218, oy + scale * 319);
            renderer.triangle(ox + scale * 405, oy + scale * 197, ox + scale * 411, oy + scale * 209, ox + scale * 414, oy + scale * 200);
            renderer.triangle(ox + scale * 235, oy + scale * 283, ox + scale * 229, oy + scale * 271, ox + scale * 226, oy + scale * 280);
            renderer.triangle(ox + scale * 458, oy + scale * 194, ox + scale * 465, oy + scale * 220, ox + scale * 472, oy + scale * 205);
            renderer.triangle(ox + scale * 182, oy + scale * 286, ox + scale * 175, oy + scale * 260, ox + scale * 168, oy + scale * 275);
            renderer.triangle(ox + scale * 369, oy + scale * 182, ox + scale * 380, oy + scale * 192, ox + scale * 378, oy + scale * 180);
            renderer.triangle(ox + scale * 271, oy + scale * 298, ox + scale * 260, oy + scale * 288, ox + scale * 262, oy + scale * 300);
            renderer.triangle(ox + scale * 414, oy + scale * 251, ox + scale * 412, oy + scale * 264, ox + scale * 420, oy + scale * 258);
            renderer.triangle(ox + scale * 226, oy + scale * 229, ox + scale * 228, oy + scale * 216, ox + scale * 220, oy + scale * 222);
            renderer.triangle(ox + scale * 425, oy + scale * 298, ox + scale * 435, oy + scale * 278, ox + scale * 438, oy + scale * 297);
            renderer.triangle(ox + scale * 215, oy + scale * 182, ox + scale * 205, oy + scale * 202, ox + scale * 202, oy + scale * 183);
            renderer.triangle(ox + scale * 384, oy + scale * 285, ox + scale * 390, oy + scale * 277, ox + scale * 390, oy + scale * 286);
            renderer.triangle(ox + scale * 256, oy + scale * 195, ox + scale * 250, oy + scale * 203, ox + scale * 250, oy + scale * 194);
            renderer.triangle(ox + scale * 406, oy + scale * 339, ox + scale * 387, oy + scale * 349, ox + scale * 398, oy + scale * 356);
            renderer.triangle(ox + scale * 234, oy + scale * 141, ox + scale * 253, oy + scale * 131, ox + scale * 242, oy + scale * 124);
            renderer.setColor(secondaryColour);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 180, ox + scale * 349, oy + scale * 189);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 349, oy + scale * 189, ox + scale * 370, oy + scale * 210);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 370, oy + scale * 210, ox + scale * 380, oy + scale * 239);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 380, oy + scale * 239, ox + scale * 370, oy + scale * 270);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 370, oy + scale * 270, ox + scale * 347, oy + scale * 292);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 347, oy + scale * 292, ox + scale * 320, oy + scale * 300);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 320, oy + scale * 300, ox + scale * 291, oy + scale * 291);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 291, oy + scale * 291, ox + scale * 270, oy + scale * 270);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 270, oy + scale * 270, ox + scale * 260, oy + scale * 241);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 260, oy + scale * 241, ox + scale * 270, oy + scale * 210);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 270, oy + scale * 210, ox + scale * 293, oy + scale * 188);
            renderer.triangle(ox + scale * 320, oy + scale * 240, ox + scale * 293, oy + scale * 188, ox + scale * 320, oy + scale * 180);

        }
    }

    void setNewCameraPos(int x, int y)
    {
        newCamPosX = x;
        newCamPosY = y;
        viewport.getCamera().position.set(newCamPosX, newCamPosY,
                viewport.getCamera().position.z);
    }

    void setNewCameraZoom(float z)
    {
        newCamZoom = z;
        ((OrthographicCamera) viewport.getCamera()).zoom = newCamZoom;
    }

    void setNewCameraPosTarget(int x, int y)
    {
        newCamPosX = x;
        newCamPosY = y;
    }

    void setNewCameraZoomTarget(float z)
    {
        newCamZoom = z;
    }

    void setDefaultCameraPos(int x, int y)
    {
        defaultCamPosX = x;
        defaultCamPosY = y;

        newCamPosX = x;
        newCamPosY = y;
    }

    void setDefaultCameraZoom(float z)
    {
        newCamZoom = z;
        defaultCamZoom = z;
    }

    void resetCameraPos()
    {
        viewport.getCamera().position.set(defaultCamPosX, defaultCamPosY,
                viewport.getCamera().position.z);
    }

    void resetCameraZoom()
    {
        ((OrthographicCamera) viewport.getCamera()).zoom = defaultCamZoom;
    }

    void returnCameraPos()
    {
        viewport.getCamera().position.set(newCamPosX, newCamPosY,
                viewport.getCamera().position.z);
    }

    void returnCameraZoom()
    {
        ((OrthographicCamera) viewport.getCamera()).zoom = newCamZoom;
    }

    void createReadyButton()
    {
        readyButton = new Actor();
        readyButton.setBounds(-80000, -80000, +160000, +160000);
        readyButton.debug();
        readyButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                {
                    readyButton.setVisible(false);
                    ready = true;
                }
            }
        });
        addActor(readyButton);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.BACK)
        {


            GameStage.this.stageInterface.goToGamePauseStage(false);


        }
        return false;
    }


}

