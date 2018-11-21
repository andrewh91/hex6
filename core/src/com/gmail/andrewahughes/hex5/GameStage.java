package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    public float black=1.0f;
    public Image pauseImg1;
    HexWide hexWide ;
    HexWideField hexWideField ;
    HexTall hexTall;
    HexTallField hexTallField ;

    Database database;
    RectTest rectTest;
    Viewport viewport;
    Vector2 v1,v2,v3,v4 ;

    BitmapFont font = new BitmapFont();
    String text = new String();
    String state = new String();
    int selectedSector=-1, selectedHex=-1;
    int selectedSector2=-1, selectedHex2=-1;


    //recommended number of rows for portrait mode using hexwide in screens with 16:9 aspect ratio
    //can be found using noOfRows =roundup(noOfColumns*2-noOfColumns/2)
    public int noOfRows=3,noOfColumns=4;
    int noOfSelected=0;

    ArrayList<Integer> adjacentArray=new ArrayList<Integer>();
    ArrayList<Integer> nonMatchingSymbols=new ArrayList<Integer>();
    ArrayList<Integer> nonMatchingSymbolsHex1=new ArrayList<Integer>();
    ArrayList<Integer> nonMatchingSymbolsHex2=new ArrayList<Integer>();

    int proposedSelectedHex = -1;
    int proposedSelectedSector = -1;

    boolean zoomSelectionMode =true;

    int portrait1Landscape2=1, fieldPosX=50, fieldPosY=50, fieldWidth=620, fieldHeight=1180;
int difficulty=0;


    public GameStage(Viewport viewport, Texture texture,final StageInterface stageInterface, int portrait) {
        super( viewport );
        this.viewport=viewport;
        this.portrait1Landscape2 =portrait;
        v1=new Vector2(60,60);
        v2=new Vector2(110,110);
        v3=new Vector2(50,50);
        v4=new Vector2(100,100);

        updateField(noOfRows,noOfColumns,portrait,fieldPosX,fieldPosY,fieldWidth,fieldHeight);

        //viewport.getCamera().translate(viewport.getScreenWidth()/2,viewport.getScreenHeight()/2,0);
        //viewport.update(viewport.getScreenWidth(),viewport.getScreenHeight(),true);
        /*database = new Database(31,noOfColumns,noOfRows);
        hexWide= new HexWide(1,0,0,0, this,database);
        this.addActor(hexWide);
        hexWideField= new HexWideField(fieldPosX,fieldPosY,fieldWidth,fieldHeight,noOfRows,noOfColumns,this, database);
        addHexesToStage(hexWideField);*/
//hexTall = new HexTall(150,400,400,0,this,database);
//this.addActor(hexTall);
        rectTest = new RectTest(0,0,0,0);
        this.addActor(rectTest);

        this.stageInterface =stageInterface;
        //this.addActor(hexWide);
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        final Image pauseImg = new Image(texture);
        this.pauseImg1 =pauseImg;
        this.pauseImg1.setVisible(false);//start off invisible

        this.pauseImg1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(pauseImg1.isVisible())
                {//if you click the badlogic image, go back to main menu

                    pauseImg1.setVisible(false);
                    setPause(false);
                    black=1.0f;
                    setVisible(false);
                    GameStage.this.stageInterface.goToMainStage();
                }
            }
        });

        table.add(pauseImg);

        addActor(table);
    }


    public void setSelected(int index, int sector) {
        int totalHexes = noOfColumns * noOfRows;

//overlap
        if (sector > 9)//in the case of an overlap in the touch logic the sector would have 10 added to it's value to indicate that it is the hex's neighbour that is selected.
        {//one of 4 adjacent hexes could be selected, work out which one it is with the selected sector
//10 means the hex above and to the left of the given hex is selected, 
//12 is above and to the right
//13 below right 
//15 below left

//above left would be hex index -1
//above right would be hex index +1
//below right would be hex index - noOfColumns +1
//below left would be hex index - noOfColumns -1
//if hex is in an even column (the left most column is 0 which is even) 
            if ((index % noOfColumns) % 2 == 0) {
                if (sector == 10) {
//if not on left edge - doesnt matter if its on the top
                    if (index % noOfColumns > 0 ) {
                        proposedSelectedHex = index - 1;
                    }
                }// not needed, touch handled by more recent adjacent hex
                /*if (sector == 12) {
//if not on the right edge
                    if (index % noOfColumns < noOfColumns - 1 ) {
                        proposedSelectedHex = index + 1;
                    }
                }*/
                else if (sector == 13) {
//if not on the right edge AND not on the bottom
                    if (index % noOfColumns < noOfColumns - 1 && index - noOfColumns >= 0) {
                        proposedSelectedHex = index - noOfColumns + 1;
                    }
                }
                else if (sector == 15) {
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
                //only need one of these for odd columns most of the surrounding oveelapping hexes are created more recently so they will handle touch
                /*if (sector == 10) {
//if not on left edge AND not on top edge
                    if (index % noOfColumns > 0 && index + noOfColumns < totalHexes) {
                        proposedSelectedHex = index + noOfColumns - 1;
                    }
                }
                if (sector == 12) {
//if not on the right edge AND not on the top
                    if (index % noOfColumns < noOfColumns - 1 && index + noOfColumns < totalHexes) {
                        proposedSelectedHex = index + noOfColumns + 1;
                    }
                }
                if (sector == 13) {
//if not on the right edge
                    if (index % noOfColumns < noOfColumns - 1 ) {
                        proposedSelectedHex = index + 1;
                    }
                }*/
                if (sector == 15) {
//if not on left edge - on wide field odd column adjacents can go below left snd below right
                    if (index % noOfColumns > 0 ) {
                        proposedSelectedHex = index - 1;// bottom left
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
            selectFirstHex();
        }//end if(noOfSelected==0)

        else if (noOfSelected == 1) {
            if (hexWideField.hexWideArray[proposedSelectedHex].highlight) {
                selectSecondHex();
                //if not zoomSelectionMode compare sector with all sectors on first hex
                if (!zoomSelectionMode) {
                   if( compareAll(selectedHex2,selectedHex,proposedSelectedSector))
                   {
                       resetSelection();
                   }
                }

            }//end if highlighted
            else if (hexWideField.hexWideArray[proposedSelectedHex].select) {
                    resetSelection();

            }//end if selected
            else //not highlighted and not selected
            {
                resetSelection();
            }
        }//end else if(noOfSelected==1)
        else if (noOfSelected == 2) {
            if (hexWideField.hexWideArray[proposedSelectedHex].select) {
                if (selectedHex == proposedSelectedHex) {
                    if(compareAll(selectedHex,selectedHex2,proposedSelectedSector)){
                        resetSelection();
                    }
                    else
                    {
                        swapSector1(selectedHex,selectedSector,proposedSelectedSector);
                    }
                } else if (selectedHex2 == proposedSelectedHex) {
                    if(compareAll(selectedHex2,selectedHex,proposedSelectedSector)){
                        resetSelection();
                    }
                    else
                    {
                        swapSector2(selectedHex2,selectedSector2,proposedSelectedSector);

                    }
                }
            }//end if selected
            else {
                resetSelection();
            }
        }//end else if(noOfSelected==2)
    }
    public void resetSelection()
    {
        if(selectedHex!=-1)
        {
            hexWideField.hexWideArray[selectedHex].unHighlightNonMatchingSymbols();

            hexWideField.hexWideArray[selectedHex].unselect(0);
            hexWideField.hexWideArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex,noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
        if(selectedHex2!=-1)
        {
            hexWideField.hexWideArray[selectedHex2].unHighlightNonMatchingSymbols();
            hexWideField.hexWideArray[selectedHex2].unselect(0);
            hexWideField.hexWideArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexWideField.getAdjacent(selectedHex2,noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);
        }
        selectedHex=-1;
        selectedHex2=-1;
        if(zoomSelectionMode)
        {
            //zoom out
        }
        noOfSelected=0;
    }

    public void selectFirstHex()
    {
        if(proposedSelectedHex!=-1)
        {
        selectedHex = proposedSelectedHex;
        hexWideField.hexWideArray[selectedHex].select(200);
        adjacentArray = hexWideField.getAdjacent(selectedHex,noOfColumns, noOfRows);
        hexWideField.highlightAdjacent(adjacentArray);
        if(zoomSelectionMode)
        {
            // zoom to fit adjacents
        }
        noOfSelected=1;
        }
    }

    public void selectSecondHex()
    {
        if(proposedSelectedHex!=-1) {
            adjacentArray = hexWideField.getAdjacent(selectedHex, noOfColumns, noOfRows);
            hexWideField.unhighlightAdjacent(adjacentArray);

            selectedHex2 = proposedSelectedHex;
            hexWideField.hexWideArray[selectedHex2].select(200);
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
            hexWideField.hexWideArray[selectedHex].highlightNonMatchingSymbols(nonMatchingSymbolsHex1);
            hexWideField.hexWideArray[selectedHex2].highlightNonMatchingSymbols(nonMatchingSymbolsHex2);
            if (zoomSelectionMode) {
                // zoom to fit pair of selected hexes
            } else {
                selectedSector2 = proposedSelectedSector;
                hexWideField.hexWideArray[selectedHex2].highlightSymbol(selectedSector2);
            }
            noOfSelected = 2;
        }
    }

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
                        resetSelectionTall();
                    }
                    else
                    {
                        swapSector1Tall(selectedHex,selectedSector,proposedSelectedSector);
                    }
                } else if (selectedHex2 == proposedSelectedHex) {
                    if(compareAllTall(selectedHex2,selectedHex,proposedSelectedSector)){
                        resetSelectionTall();
                    }
                    else
                    {
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
        if(selectedHex!=-1)
        {
            hexTallField.hexTallArray[selectedHex].unselect(0);
            hexTallField.hexTallArray[selectedHex].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex,noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        if(selectedHex2!=-1)
        {
            hexTallField.hexTallArray[selectedHex2].unselect(0);
            hexTallField.hexTallArray[selectedHex2].unhighlightSymbol();
            adjacentArray = hexTallField.getAdjacent(selectedHex2,noOfColumns, noOfRows);
            hexTallField.unhighlightAdjacent(adjacentArray);
        }
        selectedHex=-1;
        selectedHex2=-1;
        proposedSelectedHex=-1;
        if(zoomSelectionMode)
        {
            //zoom out
        }
        noOfSelected=0;
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
            if (zoomSelectionMode) {
                // zoom to fit pair of selected hexes
            } else {
                selectedSector2 = proposedSelectedSector;
                hexTallField.hexTallArray[selectedHex2].highlightSymbol(selectedSector2);
            }
            noOfSelected = 2;
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

        if (visible) {

            Gdx.gl.glClearColor(0.9f*black, 0.9f*black, 0.7f*black, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            viewport.getCamera().update();
            renderer.setProjectionMatrix(viewport.getCamera().combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Color.BLUE);
            //renderer.line(viewport.unproject(v1),viewport.unproject(v2));
            //renderer.setColor(Color.RED);
            //renderer.line(v3,v4);
            //renderer.rect(50,50,700,350);
            //drawWideHex(renderer,150,100,90);
            //drawTallHex(renderer,70,200,70);
            this.act();
            //hexWide.draw(renderer);
            if(portrait1Landscape2==1){
                    hexWideField.draw(renderer);
        }
        else if(portrait1Landscape2==2) {
            hexTallField.draw(renderer);
        }

            //rectTest.draw(renderer);

            renderer.end();
            spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
            spriteBatch.begin();
            //hexWide.drawSprites(spriteBatch);
            //hexTall.drawSprites(spriteBatch);
            if(portrait1Landscape2==1) {
                hexWideField.drawSprites(spriteBatch);
            }
            else if(portrait1Landscape2==2)
            {
                hexTallField.drawSprites(spriteBatch);

            }


            font.draw(spriteBatch,text+ " sh "+selectedHex+" sh2 "+selectedHex2+" s1 "+selectedSector+" s2 "+selectedSector2+" ph "+proposedSelectedHex+" ps "+proposedSelectedSector+" state "+state,30,690);
            spriteBatch.end();



            super.draw();
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

    public void addHexesToStage( HexWideField hwf)
    {
        for(int i=0;i<hwf.getNoOfHexes();i++)
        {
            addActor(hwf.hexWideArray[i]);
        }
    }

    public void addHexesToStage( HexTallField hwf)
    {
        for(int i=0;i<hwf.getNoOfHexes();i++)
        {
            addActor(hwf.hexTallArray[i]);
        }
    }
    public void removeAllActors()
    {
        for(Actor actor : getActors()) {
            //actor.remove();
            actor.addAction(Actions.removeActor());
        }
    }
    public void updateField(int newNoOfRows, int newNoOfColumns, int newPortrait1Landscape2, int newFieldPosX, int newFieldPosY, int newFieldWidth, int newFieldHeight)
    {
        if(newNoOfColumns!=0){noOfColumns=newNoOfColumns;}
        if(newNoOfRows!=0){noOfRows=newNoOfRows;}
        if(newPortrait1Landscape2!=0){ portrait1Landscape2 =newPortrait1Landscape2;}
        if(newFieldPosX!=0){ fieldPosX=newFieldPosX;}
        if(newFieldPosY!=0){ fieldPosY=newFieldPosY;}
        if(newFieldWidth!=0){ fieldWidth=newFieldWidth;}
        if(newFieldHeight!=0){ fieldHeight=newFieldHeight;}

//reset / reload everything 
        removeAllActors();
        database = new Database(31,noOfColumns,noOfRows,portrait1Landscape2);
        if(portrait1Landscape2==1) {
            resetSelection();
            hexWideField = new HexWideField(fieldPosX, fieldPosY, fieldWidth, fieldHeight, noOfRows, noOfColumns, this, database);
            addHexesToStage(hexWideField);
        }
        else if(portrait1Landscape2==2)
        {
            resetSelectionTall();
            hexTallField = new HexTallField(fieldPosX,fieldPosY,fieldWidth,fieldHeight,noOfRows,noOfColumns,this,database);
            addHexesToStage(hexTallField);
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


    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            //if(!pause) {
                // Do your optional back button handling (show pause menu?)
                //setPause(true);
                //pauseImg1.setVisible(true);
                //black = 0.3f;
                GameStage.this.stageInterface.goToGamePauseStage();

            //}
            /*else {
                setPause(false);
                //pauseImg1.setVisible(false);
                //black = 1.f;
            }*/


        }
        return false;
    }



}
