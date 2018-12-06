package com.gmail.andrewahughes.hex5;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;


/**
 * Created by Andrew Hughes on 13/09/2018.
 */

public class GameStage extends Stage {
    private boolean visible = false;
    private boolean pause = false;
    private ShapeRenderer renderer = new ShapeRenderer();
    SpriteBatch spriteBatch = new SpriteBatch();
    public StageInterface stageInterface;
    HexWideField hexWideField;
    HexTallField hexTallField;
    Database database;
    Viewport viewport;

    int defaultCamPosX;
    int  defaultCamPosY;

    float defaultCamZoom;

    int newCamPosX;
    int  newCamPosY;

    float  newCamZoom;

    //whether we need to run the camera movement logic
    boolean camSnapOutdated=false;
    //the period of time over which the camera movements will happen
    float camSnapTimePeriod=2f;
    //the current progress through the camera transition
    float camSnapTime=0f;
    //the speed to move the camera
    float  camSnapSpeedX;
    float  camSnapSpeedY;
    float  camSnapSpeedZ;



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
    boolean gameOver = false;
    //timer is incremented when not paused, timerFinal was just a way to stop the timer when we get gameover
    float timer = 0f, timerFinal = 0f, penaltyTime = 2f;
    //green and red are used when drawing the coloured background, they are altered when scoring to change background colour
    float green = 0;
    float red = 0;

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
    public int noOfRows = 3, noOfColumns = 4;
    //prefer not to use Boolean for the orientation, when changing the options in game we can use 0 to show no change, 1 portrait 2 landscape
    //game mode is 0 by default, this is the field mode, many hexes on screen, hexes disappear when matched, game mode 1 would be singles mode, 2 hexes on screen, symbols are replaced when matching
    int portrait1Landscape2 = 1, fieldPosX = 50, fieldPosY = 50, fieldWidth = 620, fieldHeight = 1180, gameMode = 0;


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


    public GameStage(Viewport viewport, Texture texture, final StageInterface stageInterface, int portrait) {
        super(viewport);
        this.viewport = viewport;
        this.portrait1Landscape2 = portrait;
        //the font isn’t really final, the symbols will be replaced with pictures eventually
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        //create the field with the given default values which we set above
        updateField(noOfRows, noOfColumns, portrait, fieldPosX, fieldPosY, fieldWidth, fieldHeight, gameMode);
        //the stage interface is used to go from one stage to another and pass variables over
        this.stageInterface = stageInterface;

    }


    //complicated method which handles the touch events. The hex field is a field of tessellating hexes, but we rely on
    //bounding boxes – rectangles – for collision detection. The bounding boxes overlap each other and in libgdx the most
    //recently created hex will overlap others and will block older hexes from receiving any touch that is in both bounding
    //boxes. It’s therefore important to note that these wide hexes are created starting with the bottom left, working to
    //the right then back to the left and up one row then off to the right again etc. So this method will use logic to
    //confirm which hex was actually touched. We can use maths to figure out the index number of the adjacent hexes if we
    //know the number of rows and columns, and if it’s portrait or landscape
    //in fact this method is for portrait and wide hexes only
    public void setSelected(int index, int sector) {
//index will refer to the hex index in the field, sector will be the proposed sector we will confirm if the proposed sector
//is the correct sector below

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
            if ((index % noOfColumns) % 2 == 0) {
                if (sector == 10) {
                    //if not on left edge - doesnt matter if its on the top because even column
                    if (index % noOfColumns > 0) {
                        proposedSelectedHex = index - 1;
                    }
                }
//we don’t need to do one for sector 12 because if there was a hex above and to the right then that touch would have been
//captured by that hex since it would be more recent
                else if (sector == 13) {
                    //if not on the right edge AND not on the bottom
                    if (index % noOfColumns < noOfColumns - 1 && index - noOfColumns >= 0) {
                        proposedSelectedHex = index - noOfColumns + 1;
                    }
                } else if (sector == 15) {
                    //if not on left edge AND not on bottom edge
                    if (index % noOfColumns > 0 && index - noOfColumns >= 0) {
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
            else {
                //only need one of these for odd columns most of the surrounding overlapping hexes are created more recently so they will handle touch, don’t need logic for sector 10, 12, or 13, just for 15
                if (sector == 15) {
                    //if not on left edge - on wide field odd column adjacents can go below left and below right
                    if (index % noOfColumns > 0) {
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
        else {
//since there is no overlap we know the given sector and hex are correct
            proposedSelectedSector = sector;
            proposedSelectedHex = index;
        }
//now we know for sure which sector and hex we are dealing with
//how we process this touch event will depend on how many hexes are already selected
//if no hexes are selected yet
        if (noOfSelected == 0) {
//select the touched hex
            selectFirstHex();
        }//end if(noOfSelected==0)
        //if we have already selected one hex
        else if (noOfSelected == 1) {
//if the hex we are touching is highlighted then it must be adjacent to the first selected hex so select this one too
            if (hexWideField.hexWideArray[proposedSelectedHex].highlight) {
                selectSecondHex();
//this is where quick mode and zoom mode differ, if we are using quick mode we select the second hex and compare the touched
//symbol at the same time, we will take the touched symbol on the second hex and compare it will all symbols on the first hex
                if (!zoomSelectionMode) {
                    if (compareAll(selectedHex2, selectedHex, proposedSelectedSector)) {
//TODO need to complete this, need to add increase score at the least and test it
                        resetSelection();
                    }
                }

            }//end if highlighted
//if the touched hex is not highlighted, see if it’s selected
            else if (hexWideField.hexWideArray[proposedSelectedHex].select) {
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
        else if (noOfSelected == 2) {
            //if the hex we touched is one of the two that is already selected
            if (hexWideField.hexWideArray[proposedSelectedHex].select) {
//we need to figure out which of the 2 selected hexes was touched so we know which hex’s symbol was touched
                if (selectedHex == proposedSelectedHex) {
//if the touched symbol matches one of the symbols on the other hex then increase score and reset selection and if game mode
//is singles highlightNonMatching so the difficulty level is applied to the next set of hexes
                    if (compareAll(selectedHex, selectedHex2, proposedSelectedSector)) {

                        increaseScore();
                        resetSelection();
                        if (gameMode == 1) {
                            highlightNonMatching();
                        }
                    }
//if the touched symbol does not match any of the symbols on the other hex then decrease score and swap sector which will unhighlight the previous selected sector (if there was one) and select the new one.
                    else {
                        decreaseScore();
                        swapSector1(selectedHex, selectedSector, proposedSelectedSector);
                    }
//this is the same logic but for if we touched the symbol on the other hex
                } else if (selectedHex2 == proposedSelectedHex) {
                    if (compareAll(selectedHex2, selectedHex, proposedSelectedSector)) {
                        increaseScore();
                        resetSelection();
                        highlightNonMatching();
                    } else {
                        decreaseScore();
                        swapSector2(selectedHex2, selectedSector2, proposedSelectedSector);

                    }
                }
            }//end if selected
//if we touched a hex that was not one of the 2 selected hexes then reset the selection
            else {
                resetSelection();
            }
        }//end else if(noOfSelected==2)
    }
    public void snapCameraToHex() {
        int posArray[] ={0,0};
        if(portrait1Landscape2==1)
        {

            posArray=  hexWideField.getNextHexPairCoords( selectedHex, selectedHex2);
            setNewCameraZoomTarget(hexWideField.getNextHexZoom(selectedHex,selectedHex2));

        }
        else if(portrait1Landscape2==2)
        {
            posArray=  hexTallField.getNextHexPairCoords( selectedHex, selectedHex2);
            setNewCameraZoomTarget(hexTallField.getNextHexZoom(selectedHex,selectedHex2));

        }
        hexPairPosX=posArray[0];
        hexPairPosY=posArray[1];
        setNewCameraPosTarget(hexPairPosX,hexPairPosY);
        camSnapOutdated=true;
        setCamSnapSpeed();

    }
    void setCamSnapSpeed()
    {
//work out the distance that needs to be covered to move the camera into the target position, then divide it by the amount of time you want the translation to take, then times by the delta time 
        camSnapSpeedX= (float)(newCamPosX - viewport.getCamera().position.x)/camSnapTimePeriod;
        camSnapSpeedY= (float)(newCamPosY - viewport.getCamera().position.y)/camSnapTimePeriod;
        camSnapSpeedZ= (float)(newCamZoom - ((OrthographicCamera)viewport.getCamera()).zoom)/camSnapTimePeriod;
    }

    /**
     * method to slowly move the camera into the target position
     */
    void updateCamSnap()
    {
        if(camSnapOutdated)
        {
//increment this timer so we know when to stop
            camSnapTime+=Gdx.graphics.getDeltaTime();
//increment the position of the camera by the speed times delta time, so it should reach the target position in the defined period of time
            viewport.getCamera().translate(camSnapSpeedX*Gdx.graphics.getDeltaTime(),camSnapSpeedY*Gdx.graphics.getDeltaTime(),0f);

//do the same for the zoom
            ((OrthographicCamera)viewport.getCamera()).zoom+=camSnapSpeedZ*Gdx.graphics.getDeltaTime();

//if the timer has exceeded the time in which we wanted the transition to take place then finish
            if(camSnapTime>=camSnapTimePeriod)
            {
                setNewCameraPos(newCamPosX,newCamPosY);
                setNewCameraZoom(newCamZoom);
                camSnapOutdated=false;
                camSnapTime=0f;
            }//end if camSnapTime>=camSnapTimePeriod
        }//end if cam snap outdated

    }//end method
    //method for reseting the selection so we can start looking for symbols all over again
//if called after scoring but also if we have touched a hex indicative of wanting to cancel the selection
    public void resetSelection()
    {
//if the selected hex is not -1
        if(selectedHex!=-1)
        {
//get the selected hex and unhighlight the non matching symbols this means show all the symbols that were hidden during the
//difficulty level being applied
//set the hex’s ‘select’ Boolean to false
//unhighlight the symbol that was highlighted when it was touched if applicable
//unhighlight all the adjacent hexes of the selected hex, if applicable
            hexWideField.hexWideArray[selectedHex].unHighlightNonMatchingSymbols();

            hexWideField.hexWideArray[selectedHex].unselect(0);
            hexWideField.hexWideArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex,noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
//do the same for the other hex
        if(selectedHex2!=-1)
        {
            hexWideField.hexWideArray[selectedHex2].unHighlightNonMatchingSymbols();
            hexWideField.hexWideArray[selectedHex2].unselect(0);
            hexWideField.hexWideArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex2,noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
//reset the selected hex
        selectedHex=-1;
        selectedHex2=-1;
//I’ve not implemented this yet but the camera should zoom out to show the full field, or zoom out to the maximum zoom out
//amount if the field is so big zooming all the way out would not be helpful
        if(zoomSelectionMode)
        {
            //TODO zoom out
        }
//reset the number of selected back to 0 so our next touch event is free to select a new first hex
        noOfSelected = 0;
        //if playing the singles game
        if(gameMode==1)
        {
            // dont reset if playing singles
            // re select the first 2 hexes and set the nooselected to 2, this means we won’t have to tap the hexes before
//looking for a symbol that matches, it would be pointless requiring the player to tap the hexes to select them since there
//are only 2 hexes to select
            selectedHex=score;
            selectedHex2=score+targetScore;
            hexWideField.hexWideArray[selectedHex].select(0);
            hexWideField.hexWideArray[selectedHex2].select(0);
            noOfSelected=2;
        }
    }
    //select first hex, called if we have touched a hex when there were not any hexes selected already
    public void selectFirstHex()
    {
//just some error handling in case this is called when the proposed selected hex was set to -1, which shouldn’t happen
        if(proposedSelectedHex!=-1)
        {
//store the touched hex which will come in handy later to confirm which hex has been touched first
//set the select Boolean to true in the hex that we touched
//highlight the hexes that are adjacent to the touched hex
            selectedHex = proposedSelectedHex;
            hexWideField.hexWideArray[selectedHex].select(200);
            adjacentArray = hexWideField.getAdjacent(selectedHex,noOfColumns, noOfRows);
            hexWideField.highlightAdjacent(adjacentArray);
//not done this yet but this should set the zoom level and camera position so that all the adjacents to the selected hex fill
//the screen
            if(zoomSelectionMode)
            {
                //TODO zoom to fit adjacents
            }
//set the number of selected to 1 so the next touch will be handled differently
            noOfSelected=1;
        }
    }
    //select second hex, if one has already been selected and we have touched one of the hexes that were adjacent to that
    public void selectSecondHex()
    {
//error handling, if the touched hex index is not -1,
        if(proposedSelectedHex!=-1) {
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
            if (zoomSelectionMode) {
                // TODO zoom to fit pair of selected hexes
            }
            //if quick mode – aka if not zoom mode
            else {
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
        nonMatchingSymbols=database.findNonMatchingSymbols(selectedHex,selectedHex2,difficulty);
//the array now hold all the symbols from the 2 selected hexes apart from the 2 that do match each other
//we don’t know which symbol belongs to which hex at the moment, use the below to figure it out
        for(int i =0;i<nonMatchingSymbols.size();i++)//this list could have size up to 11, so divide it into 2 below
        {
//check if the value in the array is more than 5, we added 6 to the symbols that belong to the second selected hex
            if(nonMatchingSymbols.get(i)>5)
            {
//add the ones over 5 to the second hex list, obviously subtract 6 so the value is between 0-5
                nonMatchingSymbolsHex2.add(nonMatchingSymbols.get(i)-6);
            }
//if the value is not over 5 then it must be on the first hex so add it to the first list
            else
            {
                nonMatchingSymbolsHex1.add(nonMatchingSymbols.get(i));
            }
        }
//if the selected hexes are not -1 then apply the difficulty level causing the relevant symbols to disappear
//the selected hexes might be because sometimes we have to call this method after resetting the selection
        if(selectedHex!=-1 && selectedHex2!=-1) {
            hexWideField.hexWideArray[selectedHex].highlightNonMatchingSymbols(nonMatchingSymbolsHex1);
            hexWideField.hexWideArray[selectedHex2].highlightNonMatchingSymbols(nonMatchingSymbolsHex2);
        }
    }
    //unused method
    public void removeOneSelected(int givenHex, int givenSector, int remainingHex, int remainingSector)
    {
        hexWideField.hexWideArray[givenHex].unselect(0);
        hexWideField.hexWideArray[givenSector].unhighlightSymbol();
        adjacentArray = hexWideField.getAdjacent(remainingHex,noOfColumns, noOfRows);
        hexWideField.highlightAdjacent(adjacentArray);
        selectedHex = remainingHex;
        selectedSector = remainingSector;
        selectedHex2 = -1;
        selectedSector2 = -1;
        noOfSelected=1;
    }

    //if you select a symbol this highlights it so we can see which one you selected and it will unhighlight other symbols
//TODO add arguments for hex2, and symbol2, unhighlight the symbols for hex 2 also so we only have one symbol highlighted
//TODO can delete swapSecotr2 if I add a return to this so that I can set the value for selectedSector or selectedSector2 when I call it
    public void swapSector1(int hex, int oldSector, int newSector)
    {
        hexWideField.hexWideArray[hex].unhighlightSymbol();
        hexWideField.hexWideArray[hex].highlightSymbol(newSector);
        selectedSector=newSector;
    }
    public void swapSector2(int hex, int oldSector, int newSector)
    {
        hexWideField.hexWideArray[hex].unhighlightSymbol();
        hexWideField.hexWideArray[hex].highlightSymbol(newSector);
        selectedSector2=newSector;
    }
    //compares the symbol you touched with all the symbols from the other selected hex returns true if it matches
    public boolean compareAll(int touchedHex,int otherHex, int touchedSector)
    {
        for (int i = 0; i < 6; i++)
        {
            if(database.compareSymbols(touchedHex, touchedSector, otherHex, i))
            {
                return true;
            }
        }
        return false;
    }
    //this method is largely the same but for the tall hexes to be used in landscape some things are different because the hexes
//are set out so differently
    public void setSelectedTall(int index, int sector) {
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
            if ((index % noOfRows) % 2 == 0) {
                if (sector == 10) {
                    //if not on left edge AND not on top edge
                    if (index >=noOfRows && index % noOfRows < noOfRows-1) {
                        proposedSelectedHex = index+ 1-noOfRows;
                    }
                }// this should be impossible, as an adjacent hex would be placed more recently and that would handle the touch
	                /*if (sector == 11) {
	//if  not on the top - doesnt matter if its on the right or not
	                    if ( index % noOfRows < noOfRows-1) {
	                        proposedSelectedHex = index + 1;
	                    }
	                }*/
                else if (sector == 13) {
                    //if  not on the bottom - doesnt matter if its on tbe right
                    if (index % noOfRows > 0) {
                        proposedSelectedHex = index -  1;
                    }
                }
                else if (sector == 14) {
                    //if not on left edge AND not on bottom edge
                    if (index >=noOfRows &&index % noOfRows > 0) {
                        proposedSelectedHex = index - noOfRows - 1;
                    }
                }
            }
            //else if odd row above left would just be hex index + 1
            //else if odd row above right would be hex index + 1 + noOfRows
            //else if odd row below right would be hex index + noOfRows -1
            //else if odd row below left would be hex index - 1

            //if the given hex is in an even row;
            else {//if in odd row
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
                if (sector == 14) {
                    //if not on bottom edge - doesnt matter if its on the keft or not
                    if (index % noOfRows > 0) {
                        proposedSelectedHex = index - 1;
                    }
                }
            }

            //  the touched sector will actually be the opposing sector to the given hex, so translate that

            proposedSelectedSector = (sector + 3 - 10) % 6;

        }//end overlap logic
        else {
            proposedSelectedSector = sector;
            proposedSelectedHex = index;
        }
        if (noOfSelected == 0) {
            selectFirstHexTall();
        }//end if(noOfSelected==0)

        else if (noOfSelected == 1) {
            if (hexTallField.hexTallArray[proposedSelectedHex].highlight) {
                selectSecondHexTall();
                //if not zoomSelectionMode compare sector with all sectors on first hex
                if (!zoomSelectionMode) {
                    if( compareAllTall(selectedHex2,selectedHex,proposedSelectedSector))
                    {
                        resetSelectionTall();
                    }
                }

            }//end if highlighted
            else if (hexTallField.hexTallArray[proposedSelectedHex].select) {
                resetSelectionTall();

            }//end if selected
            else //not highlighted and not selected
            {
                resetSelectionTall();
            }
        }//end else if(noOfSelected==1)
        else if (noOfSelected == 2) {
            if (hexTallField.hexTallArray[proposedSelectedHex].select) {
                if (selectedHex == proposedSelectedHex) {
                    if(compareAllTall(selectedHex,selectedHex2,proposedSelectedSector)){
                        increaseScore();
                        resetSelectionTall();
                        highlightNonMatchingTall();
                    }
                    else
                    {
                        decreaseScore();
                        swapSector1Tall(selectedHex,selectedSector,proposedSelectedSector);
                    }
                } else if (selectedHex2 == proposedSelectedHex) {
                    if(compareAllTall(selectedHex2,selectedHex,proposedSelectedSector)){
                        increaseScore();
                        resetSelectionTall();
                        highlightNonMatchingTall();
                    }
                    else
                    {
                        decreaseScore();
                        swapSector2Tall(selectedHex2,selectedSector2,proposedSelectedSector);

                    }
                }
            }//end if selected
            else {
                resetSelectionTall();
            }
        }//end else if(noOfSelected==2)
    }

    public void resetSelectionTall()
    {

        if (selectedHex != -1) {

            hexTallField.hexTallArray[selectedHex].unHighlightNonMatchingSymbols();

            hexTallField.hexTallArray[selectedHex].unselect(0);
            hexTallField.hexTallArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        if (selectedHex2 != -1) {
            hexTallField.hexTallArray[selectedHex2].unHighlightNonMatchingSymbols();

            hexTallField.hexTallArray[selectedHex2].unselect(0);
            hexTallField.hexTallArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex2, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        selectedHex = -1;
        selectedHex2 = -1;
        if (zoomSelectionMode) {
            //zoom out
        }
        noOfSelected = 0;

        if(gameMode==1)
        {
            // dont reset if playing singles
            // re select the first 2 hexes and set the nooselected to 2
            selectedHex=score;
            selectedHex2=score+targetScore;
            hexTallField.hexTallArray[selectedHex].select(0);
            hexTallField.hexTallArray[selectedHex2].select(0);

            noOfSelected=2;
        }

    }


    public void selectFirstHexTall()
    {
        if(proposedSelectedHex!=-1) {
            selectedHex = proposedSelectedHex;
            hexTallField.hexTallArray[selectedHex].select(200);
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.highlightAdjacent(adjacentArray);
            if (zoomSelectionMode) {
                // zoom to fit adjacents
            }
            noOfSelected = 1;
        }
    }

    public void selectSecondHexTall()
    {
        if(proposedSelectedHex!=-1) {
            adjacentArray = hexTallField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);

            selectedHex2 = proposedSelectedHex;
            hexTallField.hexTallArray[selectedHex2].select(200);
            highlightNonMatchingTall();
            if (zoomSelectionMode) {
                // zoom to fit pair of selected hexes
            } else {
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
        nonMatchingSymbols=database.findNonMatchingSymbols(selectedHex,selectedHex2,difficulty);
        for(int i =0;i<nonMatchingSymbols.size();i++)//this list could have size up to 11, so divide it into 2 below
        {
            if(nonMatchingSymbols.get(i)>5)//the values in the array should refer to symbols (0-5) but some are +6to indicate its in the second hex
            {
                nonMatchingSymbolsHex2.add(nonMatchingSymbols.get(i)-6);//add the ones over 5 to the second hex list, obviously subtract 6 so the value is between 0-5
            }
            else
            {
                nonMatchingSymbolsHex1.add(nonMatchingSymbols.get(i));
            }
        }
        if(selectedHex!=-1 && selectedHex2!=-1) {
            hexTallField.hexTallArray[selectedHex].highlightNonMatchingSymbols(nonMatchingSymbolsHex1);
            hexTallField.hexTallArray[selectedHex2].highlightNonMatchingSymbols(nonMatchingSymbolsHex2);
        }
    }

    public void removeOneSelectedTall(int givenHex, int givenSector, int remainingHex, int remainingSector)
    {
        hexTallField.hexTallArray[givenHex].unselect(0);
        hexTallField.hexTallArray[givenSector].unhighlightSymbol();
        adjacentArray = hexTallField.getAdjacent(remainingHex,noOfColumns, noOfRows);
        hexTallField.highlightAdjacent(adjacentArray);
        selectedHex = remainingHex;
        selectedSector = remainingSector;
        selectedHex2 = -1;
        selectedSector2 = -1;
        noOfSelected=1;
    }

    public void swapSector1Tall(int hex, int oldSector, int newSector)
    {
        hexTallField.hexTallArray[hex].unhighlightSymbol();
        hexTallField.hexTallArray[hex].highlightSymbol(newSector);
        selectedSector=newSector;
    }
    public void swapSector2Tall(int hex, int oldSector, int newSector)
    {
        hexTallField.hexTallArray[hex].unhighlightSymbol();
        hexTallField.hexTallArray[hex].highlightSymbol(newSector);
        selectedSector2=newSector;
    }
    public boolean compareAllTall(int touchedHex,int otherHex, int touchedSector)
    {
        for (int i = 0; i < 6; i++)
        {
            if(database.compareSymbols(touchedHex, touchedSector, otherHex, i))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
//if we reach gameover for example by reaching target score
        if(gameOver)
        {
            //green backgroudn
            Gdx.gl.glClearColor(0 , 0.8f, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            viewport.getCamera().update();
            spriteBatch.begin();
            //display the results
            font.draw(spriteBatch, "finish!   timer: " + (float)(((int)(timer*100)))/100f  + " score: " + score+" difficulty: " +difficulty, 5, 640);
            spriteBatch.end();
            super.draw();
        }
//if not game over
        else {
//if visible, should be visible so long as we are in the game stage
            if (visible) {
//increase the timer
                timer = timer + Gdx.graphics.getDeltaTime();
                //test camera translate
                /*
                if(viewport.getCamera().position.x>1280) {
                    viewport.getCamera().position.set(0, viewport.getCamera().position.y, viewport.getCamera().position.y);
                }
                viewport.getCamera().translate(5f, 0f, 0f);
                */

                //handle camera transition
                updateCamSnap();

                //when we get an answer right of wrong we set green or red to 1 which in turn is used to colour the screen so the
//background flashes to show you got it right or wrong, the below controls how long the screen stays that colour before going
//back to what it was. Use the delta time for effect consistent throughout all frame rates.
//the colour accepts values from 0-1, we are setting it to 1 then reducing it over time, if you reduce it by a larger number
//it will fade faster, so - (2 * Gdx.graphics.getDeltaTime()); should take half a second
                if (green > 0) {
                    green = green - (2 * Gdx.graphics.getDeltaTime());
                } else {
                    green = 0;
                }
                if (red > 0) {
                    red = red - (2 * Gdx.graphics.getDeltaTime());
                } else {
                    red = 0;
                }

                Gdx.gl.glClearColor(0 + red, 0 + green, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                viewport.getCamera().update();
                renderer.setProjectionMatrix(viewport.getCamera().combined);
                renderer.begin(ShapeRenderer.ShapeType.Line);
                renderer.setColor(Color.BLUE);

                this.act();

                if (portrait1Landscape2 == 1) {
                    hexWideField.draw(renderer);
                } else if (portrait1Landscape2 == 2) {
                    hexTallField.draw(renderer);
                }

                renderer.end();
                spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
                spriteBatch.begin();
                if (portrait1Landscape2 == 1) {
                    hexWideField.drawSprites(spriteBatch);
                } else if (portrait1Landscape2 == 2) {
                    hexTallField.drawSprites(spriteBatch);

                }

                font.draw(spriteBatch, "timer: " + (int)(timer) + " score: " + score, 30, 30);
                spriteBatch.end();


                super.draw();
            }
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void drawWideHex(ShapeRenderer sr, int originX, int originY, int edgeSize)
    {
        // draws a ‘wide’ hex hex with flat top and bottom. originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral triangle with edge size defined by edgeSize
        double altitudeSize = edgeSize * 0.866025403784439;

        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line(originX + (edgeSize / 2) , (int)(originY - altitudeSize), originX + edgeSize, originY);
        sr.line(originX + edgeSize, originY , originX + (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX + (edgeSize / 2) , (int)(originY + altitudeSize), originX - (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY + altitudeSize), originX - edgeSize , originY);
        sr.line(originX - edgeSize , originY , originX - (edgeSize / 2) , (int)(originY - altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY - altitudeSize),originX + (edgeSize / 2) , (int)(originY - altitudeSize));

    }
    //for each hex that was created in the hex field we will add it to the stage so that it can be touched
    public void addHexesToStage( HexWideField hwf)
    {
        for(int i=0;i<hwf.getNoOfHexes();i++)
        {
            addActor(hwf.hexWideArray[i]);
        }
    }
    //same but for tall hexes
    public void addHexesToStage( HexTallField hwf)
    {
        for(int i=0;i<hwf.getNoOfHexes();i++)
        {
            addActor(hwf.hexTallArray[i]);
        }
    }
    //remove all actors, needed when switching game mode, or otherwise getting rid of one field and making a new one
    public void removeAllActors()
    {
        for(Actor actor : getActors()) {
            //actor.remove();
            actor.addAction(Actions.removeActor());
        }
    }




    //create or update the field, when changing options only some of them require the field be updated, all the options that do require a field change will be handled here
    public void updateField(int newNoOfRows, int newNoOfColumns, int newPortrait1Landscape2,
                            int newFieldPosX, int newFieldPosY, int newFieldWidth,
                            int newFieldHeight,int newGameMode)
    {
//if any of the arguments are not set to 0 then update the corresponding value
        if(newNoOfColumns!=0){noOfColumns=newNoOfColumns;}
        if(newNoOfRows!=0){noOfRows=newNoOfRows;}
        if(newPortrait1Landscape2!=0){ portrait1Landscape2 =newPortrait1Landscape2;}
        if(newFieldPosX!=0){ fieldPosX=newFieldPosX;}
        if(newFieldPosY!=0){ fieldPosY=newFieldPosY;}
        if(newFieldWidth!=0){ fieldWidth=newFieldWidth;}
        if(newFieldHeight!=0){ fieldHeight=newFieldHeight;}
//if the game mode is changed we need to reset the game which will reset the timer and score etc
        if(newGameMode!=0)
        {
            gameMode=newGameMode;
            resetGame();// reset the score and timer when starting a new game modes

        }
        //reset / reload everything
        removeAllActors();
        if(gameMode==1)//if singles mode overwrite number of rows and columns,
        {
            if(portrait1Landscape2==1)
            {
                noOfColumns =targetScore;
                noOfRows=2;
            }
            else if(portrait1Landscape2==2)
            {
                noOfRows=targetScore;
                noOfColumns=2;
            }
        }
//create a new database, if singles the number of columns and rows has been set to 1 and 2
        database = new Database(31,noOfColumns,noOfRows,portrait1Landscape2);
//set new wide or tall field depending on if we are in portrait or landscape mode
        if(portrait1Landscape2==1) {
            resetSelection();
            hexWideField = new HexWideField(fieldPosX, fieldPosY, fieldWidth, fieldHeight,
                    noOfRows, noOfColumns, this, database);
            addHexesToStage(hexWideField);
//some specific things if in singles game mode
            if(gameMode==1)
            {

                //we want the 2 hexes to be selected automatically
                hexWideField.hexWideArray[selectedHex].select(0);
                hexWideField.hexWideArray[selectedHex2].select(0);
//apply the difficulty level too
                highlightNonMatching();
                //point the camera at the selected hexes
                snapCameraToHex();

            }
        }
        else if(portrait1Landscape2==2)
        {
            resetSelectionTall();
            hexTallField = new HexTallField(fieldPosX,fieldPosY,fieldWidth,fieldHeight,
                    noOfRows,noOfColumns,this,database);
            addHexesToStage(hexTallField);
            if(gameMode==1)
            {

                hexTallField.hexTallArray[selectedHex].select(0);
                hexTallField.hexTallArray[selectedHex2].select(0);
                highlightNonMatchingTall();
                snapCameraToHex();
            }
        }
    }
    public void updateZoom(int newZoom)
    {
        if(newZoom==1)
        {
            zoomSelectionMode=false;
        }
        else if(newZoom==2)
        {
            zoomSelectionMode=true;
        }
    }
    //called when a match is found
    void increaseScore()
    {
//increment score
        score++;
//display green background which will automatically fade out
        flashGreenBackground();
        isTargetReached(score);
//if in singles game mode we need to get some new symbols etc so we can’t just match the same symbols again
        if(gameMode==1){
            selectedHex =score;
            selectedHex2 =score+targetScore;
            snapCameraToHex();
        }
        else// if not singles mode, make matched hexes disappear
        {
            if(portrait1Landscape2==1)
            {
//hide both hexes
                hexWideField.hexWideArray[selectedHex].hide();
                hexWideField.hexWideArray[selectedHex2].hide();
//create a list for temporary use
                ArrayList<Integer> list = new ArrayList<Integer>();
//use the get isolated method to find out if removing the two selected hexes has resulted in any hexes
//becoming isolated  and therefore have no adjacent of their own
                list=hexWideField.getIsolated(selectedHex,selectedHex2,noOfColumns,noOfRows);
                for(int i =0;i<list.size();i++)
                {
//if any are isolated we should hide them too, consider increasing score?
                    hexWideField.hexWideArray[list.get(i)].hide();

                }

            }
//same but for tall hexes
            else
            {
                hexTallField.hexTallArray[selectedHex].hide();
                hexTallField.hexTallArray[selectedHex2].hide();
                ArrayList<Integer> list = new ArrayList<Integer>();
                list=hexTallField.getIsolated(selectedHex,selectedHex2,noOfColumns,noOfRows);
                for(int i =0;i<list.size();i++)
                {
                    hexTallField.hexTallArray[list.get(i)].hide();

                }
            }
        }
    }

    void decreaseScore()
    {
        //score--;
        timer=timer+penaltyTime;//plus 2 seconds to timer
        flashRedBackground();
    }

    void isTargetReached(int currentScore)
    {
        if(currentScore==targetScore)
        {
            gameOver();
        }
    }
    void gameOver()
    {
        gameOver=true;
        timerFinal=timer;
int timerInt = (int)(timer*1000);
int diffInt = difficulty;
        resetGame();
        GameStage.this.stageInterface.setScore(timerInt, diffInt);

        GameStage.this.stageInterface.goToGameOverStage();
    }
    void resetGame()
    {
        gameOver=false;
        score=0;
        timer=0;
    }

    void flashGreenBackground()
    {
        green=1;
    }

    void flashRedBackground()
    {
        red= 1;
    }

    public void updateDifficulty(int newDifficulty)
    {
        difficulty=newDifficulty;
    }
    public void drawTallHex(ShapeRenderer sr, int originX, int originY, int edgeSize)
    {
        // draws a ‘tall’ hex hex with flat sides . originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral triangle with edge size defined by edgeSize

        double altitudeSize = edgeSize * 0.866025403784439;

        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line((int)(originX + altitudeSize) , originY - (edgeSize / 2), (int)(originX + altitudeSize), originY + (edgeSize / 2));
        sr.line((int)(originX + altitudeSize), originY + (edgeSize / 2), originX, originY + edgeSize);
        sr.line(originX, originY + edgeSize, (int)(originX - altitudeSize) , originY + (edgeSize / 2)
        );
        sr.line((int)(originX - altitudeSize) , originY + (edgeSize / 2), (int)(originX - altitudeSize), originY - (edgeSize / 2));
        sr.line((int)(originX - altitudeSize), originY - (edgeSize / 2) ,originX , originY - edgeSize);
        sr.line( originX , originY - edgeSize, (int)(originX + altitudeSize) , originY - (edgeSize / 2));

    }

    void setNewCameraPos(int x, int y)
    {
        newCamPosX=x;
        newCamPosY=y;
        viewport.getCamera().position.set(newCamPosX,newCamPosY,
                viewport.getCamera().position.z);
    }
    void setNewCameraZoom(float z)
    {
        newCamZoom = z;
        ((OrthographicCamera)viewport.getCamera()).zoom=newCamZoom ;
    }
    void setNewCameraPosTarget(int x, int y)
    {
        newCamPosX=x;
        newCamPosY=y;
    }
    void setNewCameraZoomTarget(float z)
    {
        newCamZoom = z;
    }

    void setDefaultCameraPos(int x, int y)
    {
        defaultCamPosX=x;
        defaultCamPosY=y;

        newCamPosX=x;
        newCamPosY=y;
    }
    void setDefaultCameraZoom(float z)
    {
        newCamZoom =z;
        defaultCamZoom = z;
    }

    void resetCameraPos()
    {
        viewport.getCamera().position.set(defaultCamPosX,defaultCamPosY,
                viewport.getCamera().position.z);
    }
    void resetCameraZoom()
    {
        ((OrthographicCamera)viewport.getCamera()).zoom=defaultCamZoom;
    }

    void returnCameraPos()
    {
        viewport.getCamera().position.set(newCamPosX,newCamPosY,
                viewport.getCamera().position.z);
    }
    void returnCameraZoom()
    {
        ((OrthographicCamera)viewport.getCamera()).zoom=newCamZoom;
    }
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){


            GameStage.this.stageInterface.goToGamePauseStage();





        }
        return false;
    }



}

