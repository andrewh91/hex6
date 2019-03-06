package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 24/09/2018.
 */

public class GamePauseStage extends Stage {

    private boolean visible = false;
    private boolean pause = false;
    public StageInterface stageInterface;
    public Image pauseImg1;
    public float black=1.0f;
    Skin skin;
    private TextureAtlas atlas;

    int difficulty = 1, newDifficulty = 1;
    int orientation = 1, newOrientation = 1;
    int symbol = 1, newSymbol = 1;
    int gameMode = 2, newGameMode = 2;
    int noOfHexes = 1, newNoOfHexes = 1;
    int noOfRows = 0, newNoOfRows = 0;
    int noOfColumns = 0, newNoOfColumns = 0;
    int zoomMode = 2, newZoomMode = 2;
    int scoreboardMode = 0, newScoreboardMode = 0;
    boolean gameOver =false;
    /*
    private TextField zoomModeValue;
    private Label zoomMode;
    private TextField posXValue;
    private Label posX;
    private TextField posYValue;
    private Label posY;
    private TextField widthValue;
    private Label widthLbl;
    private TextField heightValue;
    private Label heightLbl;
    private TextField rowValue;
    private Label row;
    private TextField columnValue;
    private Label column;
    private TextField portrait1Landscape2Value;
    private Label portrait1Landscape2;
    private TextField difficultyValue;
    private Label difficulty;
    private TextField gameModeValue;
    private Label gameMode;

    private TextField symbolTypeValue;
    private Label symbolType;
    */
    Label[][] ost=new Label[12][6];//2d array to hold offline scoreboard text
    Label scoreboardTypeLbl, dateLbl, timeLbl, noOfHexesLbl, scoreLbl, uploadedLbl;



    HexOptionField hexOptionField;
    HexOptionField difficultyOptionField;
    HexOptionField symbolOptionField;
    HexOptionField swapOrientationOptionField ;
    HexOptionField gameModeOptionField ;
    HexOptionField noOfHexesOptionField;
    HexOptionField zoomModeOptionField ;
    HexOptionField scoreboardOptionField ;
    ArrayList<HexOptionField> hexOptionFieldArray = new ArrayList<HexOptionField>();

    HexOption tempHexOption;

    ShapeRenderer shapeRenderer  = new ShapeRenderer();
SpriteBatch spriteBatch = new SpriteBatch();
    int x=0,y=0,width=720,height=1280,noOfOptions=7;
    boolean portrait =true;
 int fieldIndex=0;
    BitmapFont font = new BitmapFont();
int score;
    int dateTimeWidth=0, noOfHexesWidth=0,ScoreWidth=0;
    int scoreboardHeight=800;
    int screenWidth=800;
    ScoreboardRow[] scoreboardRowArray = new ScoreboardRow[14];
    boolean offlineScoreboardVisible = false;

    Actor continueButton = new Actor();


    public GamePauseStage(Viewport viewport, Texture texture,
                          final StageInterface stageInterface) {
        super( viewport );
        setupFields();

        continueButton = new Actor();
        continueButton.debug();
        continueButton.setBounds(0,0,stageInterface.getScreenWidth(),stageInterface.getScreenHeight());
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                {//any changes madehere should be  reflected in the back button
                    continueButton.setVisible(false);
                    offlineScoreboardVisible=false;
                    GamePauseStage.this.stageInterface.goToScoreboardOption();
                }
            }
        });
        continueButton.setVisible(false);
        this.addActor(continueButton);

        for(int i =0;i<hexOptionFieldArray.size();i++)
{
    addHexesToStage(hexOptionFieldArray.get(i));
}
hexOptionField.enableOptions();

updateUI();


/*
        addHexesToStage(difficultyOptionField);
        addHexesToStage(swapOrientationOptionField );
        addHexesToStage(symbolOptionField);
        addHexesToStage(gameModeOptionField );
        addHexesToStage(noOfHexesOptionField);
        addHexesToStage(zoomModeOptionField );
        addHexesToStage(hexOptionField);

        difficultyOptionField.disableOptions();
        swapOrientationOptionField.disableOptions();
        symbolOptionField.disableOptions();
        gameModeOptionField.disableOptions();
        noOfHexesOptionField.disableOptions();
        zoomModeOptionField.disableOptions();
*/

        this.stageInterface =stageInterface;

        Table table = new Table();
        table.setFillParent(true);
        table.left();

        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));


        final Image pauseImg = new Image(texture);
        this.pauseImg1 =pauseImg;
        this.pauseImg1.setVisible(false);//start off invisible
        prepareUI();



/*
        table.add(pauseImg);
        table.center();
        table.row().pad(40);
        table.row();
        prepareUI();

        addActor(table);
        */
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {

            Gdx.gl.glClearColor(0.93f, 0.84f, 0.08f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(getViewport().getCamera().combined);
            for(int i =0;i<hexOptionFieldArray.size();i++)
            {
                hexOptionFieldArray.get(i).draw(shapeRenderer);
            }

            if(!gameOver&&hexOptionFieldArray.get(7).hexOptionArray[2].isVisible())
            {//if scoreboard option menu visible and not gameover grey out the submit score button in the scoreboard option menu
                shapeRenderer.setColor(0.04f,0.04f,0.04f, 1);
                hexOptionFieldArray.get(7).hexOptionArray[2].drawHex(shapeRenderer);

            }

/*
            hexOptionField.draw(shapeRenderer);
            difficultyOptionField.draw(shapeRenderer);
            symbolOptionField.draw(shapeRenderer);
            swapOrientationOptionField.draw(shapeRenderer);
            gameModeOptionField.draw(shapeRenderer);
            noOfHexesOptionField.draw(shapeRenderer);
            zoomModeOptionField.draw(shapeRenderer);
*/


            shapeRenderer.end();
            spriteBatch.setProjectionMatrix(getViewport().getCamera().combined);

            spriteBatch.begin();
            for(int i =0;i<hexOptionFieldArray.size();i++)
            {
                hexOptionFieldArray.get(i).drawText(spriteBatch);

            }
            if(offlineScoreboardVisible)
            {
                for(int i = 0 ; i< scoreboardRowArray.length;i++)
                {
                    scoreboardRowArray[i].draw(spriteBatch);
                }
            }

            /*
            hexOptionField.drawText(spriteBatch);
            difficultyOptionField.drawText(spriteBatch);
            symbolOptionField.drawText(spriteBatch);
            swapOrientationOptionField.drawText(spriteBatch);
            gameModeOptionField.drawText(spriteBatch);
            noOfHexesOptionField.drawText(spriteBatch);
            zoomModeOptionField.drawText(spriteBatch);
            */
//font.draw(spriteBatch,""+hexOptionField.hexOptionArray[0].centreX+
 //       hexOptionField.hexOptionArray[0].centreY ,50,100);
            spriteBatch.end();

            super.draw();
        }
    }
    public void addHexesToStage( HexOptionField hwf)
    {
        for(int i=0;i<hwf.noOfHexes;i++)
        {
            addActor(hwf.hexOptionArray[i]);
        }
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setPause(boolean pause) {
        this.pause = pause;
    }
    void setGameOver(boolean gameOver)
    {
        this.gameOver=gameOver;
    }
    //gamePauseStage
    public void updateUI()
    {
//main menu
//cancel changes
        hexOptionField.hexOptionArray[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newDifficulty = difficulty;
                newOrientation = orientation;
                newSymbol=symbol;
                newGameMode = gameMode;
                newNoOfHexes = noOfHexes;
                newNoOfRows = noOfRows;
                newNoOfColumns = noOfColumns;
                newZoomMode = zoomMode;
                newScoreboardMode=scoreboardMode;


                stageInterface.setDifficulty(difficulty);
                stageInterface.setOrientation(orientation);
                stageInterface.setSymbol(symbol);
                stageInterface.setGameMode(gameMode);
                stageInterface.setNoOfHexes(noOfHexes);
                stageInterface.setZoomMode(zoomMode);

                stageInterface.updateOptionsGoToGameStage(0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0);
            }});//end cancel changes

//return to main menu
        hexOptionField.hexOptionArray[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToMainStage();

            }});//end return to main menu

//go to difficulty menu
        hexOptionField.hexOptionArray[2].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToDifficultyOption();

            }});//end go to difficulty menu

//go to swap orientation
        hexOptionField.hexOptionArray[3].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToSwapOrientationOption();

            }});//end go to swap orientation

//go to symbol
        hexOptionField.hexOptionArray[4].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToSymbolOption();

            }});//end go to symbol

//save changes
        hexOptionField.hexOptionArray[5].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                difficulty = newDifficulty;
                orientation = newOrientation;
                symbol=newSymbol;
                gameMode = newGameMode;
                noOfHexes = newNoOfHexes;
                noOfRows = newNoOfRows;
                noOfColumns = newNoOfColumns;
                zoomMode = newZoomMode;
                scoreboardMode=newScoreboardMode;


                stageInterface.updateOptionsGoToGameStage(
                        noOfRows, noOfColumns,
                        orientation, 0, 0, 0, 0, zoomMode,
                        difficulty, gameMode, symbol);

            }});//end save changes

//go to game mode
        hexOptionField.hexOptionArray[6].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToGameModeOption();

            }});//end go to game mode

//go to no of hexes
        hexOptionField.hexOptionArray[7].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToNoOfHexesOption();

            }});//end go to no of hexes

//go to zoom mode
        hexOptionField.hexOptionArray[8].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToZoomModeOption();

            }});//end go to zoom mode
        hexOptionField.hexOptionArray[9].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToScoreboardOption();

            }});//end go to scoreboard
//end main menu

//difficulty menu
//go back
        difficultyOptionField.hexOptionArray[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stageInterface.goToMainOption();
            }});//end go back
//select difficulty
        for( int i = 1;i<13;i++)
        {
            difficultyOptionField.hexOptionArray[i].addListener(new ClickListener()
            {

                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    //get the touched hexoption
                    tempHexOption=(HexOption)event.getTarget();
newDifficulty=tempHexOption.hexIndex;
                    stageInterface.setDifficulty(newDifficulty);
                }
            });
        }
    //end select difficulty

//end difficulty menu

//swap orientation
//go back
    swapOrientationOptionField.hexOptionArray[0].addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stageInterface.goToMainOption();

        }});//end go back

//select orientation

for(int i = 1;i<3;i++)
    {
        swapOrientationOptionField.hexOptionArray[i].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newOrientation=tempHexOption.hexIndex;
                stageInterface.setOrientation(newOrientation);
            }
        });
    }//end select orientation

//end swap orientation

// symbols menu
//go back
    symbolOptionField.hexOptionArray[0].addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stageInterface.goToMainOption();

        }});//end go back

//select symbol

for(int i = 1;i<3;i++)
    {
        symbolOptionField.hexOptionArray[i].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newSymbol=tempHexOption.hexIndex;
                stageInterface.setSymbol(newSymbol);
            }
        });
    }//end select symbol

//end symbols menu

// gamemenu
//go back
    gameModeOptionField.hexOptionArray[0].addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stageInterface.goToMainOption();

        }});//end go back

//select game mode

for(int i = 1;i<3;i++)
    {
        gameModeOptionField.hexOptionArray[i].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newGameMode=tempHexOption.hexIndex;
                stageInterface.setGameMode(newGameMode);
            }
        });
    }//end select game mode

//end game modemenu

// noofhexes menu
//go back
    noOfHexesOptionField.hexOptionArray[0].addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            stageInterface.goToMainOption();

        }});//end go back
//select no of hexes

for(int i = 1;i<5;i++)
    {
        noOfHexesOptionField.hexOptionArray[i].addListener(new ClickListener() {
            @Override
        public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newNoOfHexes=tempHexOption.hexIndex;
                stageInterface.setNoOfHexes(newNoOfHexes);
//need to derive noOfRows and columns from noOfHexes
                if(portrait)
                {
                    switch (newNoOfHexes)
                    {


                        case 1:
                            newNoOfRows = 3;
                            newNoOfColumns = 2;
                            break;

                        case 2:
                            newNoOfRows = 5;
                            newNoOfColumns = 3;
                            break;

                        case 3:
                            newNoOfRows = 6;
                            newNoOfColumns = 4;
                            break;

                        case 4:
                            newNoOfRows = 8;
                            newNoOfColumns = 5;
                            break;
                    }
                }
        else
                    {
                        switch (newNoOfHexes)
                        {

                            case 1:
                                newNoOfRows = 1;
                                newNoOfColumns = 3;
                                break;

                            case 2:
                                newNoOfRows = 2;
                                newNoOfColumns = 5;
                                break;

                            case 3:
                                newNoOfRows = 3;
                                newNoOfColumns = 7;
                                break;

                            case 4:
                                newNoOfRows = 4;
                                newNoOfColumns = 9;
                                break;

                        }
                    }
                    stageInterface.setNoOfHexes(newNoOfHexes);
        //end of noofhexes switch statement
    }
    });//end select no of hexes
}//end for

//end noofhexes menu

// zoom menu
//go back
zoomModeOptionField.hexOptionArray[0].addListener(new ClickListener() {
@Override
public void clicked(InputEvent event, float x, float y) {
        stageInterface.goToMainOption();

        }});//end go back
//select zoom

        for(int i = 1;i<3;i++)
        {
        zoomModeOptionField.hexOptionArray[i].addListener(new ClickListener() {
@Override
public void clicked(InputEvent event, float x, float y) {
    tempHexOption=(HexOption)event.getTarget();
    newZoomMode=tempHexOption.hexIndex;
    stageInterface.setZoomMode(newZoomMode);
        }
        });
        }//end select zoom mode
//end zoom menu


// scoreboard
//go back
        scoreboardOptionField.hexOptionArray[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(gameOver)//if game over is true then we navigated here when the game ended, the back button should take us back to the main menu
                {
                    stageInterface.goToMainStage();

                }
                else
                {
                    stageInterface.goToMainOption();
                }
            }});//end go back
//select show scoreboard


        scoreboardOptionField.hexOptionArray[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newScoreboardMode=tempHexOption.hexIndex;
                stageInterface.setScoreboardMode(newScoreboardMode);
                stageInterface.showScoreboard();
            }
        });
        //end show scoreboard mode
//select submit score


        scoreboardOptionField.hexOptionArray[2].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tempHexOption=(HexOption)event.getTarget();
                newScoreboardMode=tempHexOption.hexIndex;
                stageInterface.setScoreboardMode(newScoreboardMode);
                if(gameOver)//if game over is true then we navigated here when the game ended, should be allowed to submit a score 
                {
                    if(stageInterface.submitScore(score,difficulty,newNoOfHexes))
                    {
                        //if upload successful, black out submit button
                        gameOver=false;
                    }
                }
            }
        });
        //end submit score

        //show offline scoreboard
        scoreboardOptionField.hexOptionArray[3].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scoreboardOptionField.disableOptions();
                offlineScoreboardVisible=true;
                continueButton.setBounds(0,0,stageInterface.getScreenWidth(),stageInterface.getScreenHeight());
                continueButton.setVisible(true);
                updateUIText();

            }
        });

        //end  show offline

//end scoreboard menu


    }//end updateUI

public void cancelOptions()
{
    newDifficulty = difficulty;
    newOrientation = orientation;
    newSymbol=symbol;
    newGameMode = gameMode;
    newNoOfHexes = noOfHexes;
    newNoOfRows = noOfRows;
    newNoOfColumns = noOfColumns;
    newZoomMode = zoomMode;

    stageInterface.setDifficulty(difficulty);
    stageInterface.setOrientation(orientation);
    stageInterface.setSymbol(symbol);
    stageInterface.setGameMode(gameMode);
    stageInterface.setNoOfHexes(noOfHexes);
    stageInterface.setZoomMode(zoomMode);
    stageInterface.updateOptionsGoToGameStage(0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0);
}

/*
public void prepareUI()
    {

        zoomModeValue= new TextField("", skin);
        zoomMode= new Label("quick1Zoom2:", skin);
        posXValue= new TextField("", skin);
        posX= new Label("posX:", skin);
        posYValue= new TextField("", skin);
        posY= new Label("posY:", skin);
        widthValue= new TextField("", skin);
        widthLbl = new Label("widthLbl:", skin);
        heightValue= new TextField("", skin);
        heightLbl = new Label("heightLbl:", skin);
        rowValue= new TextField("", skin);
        row= new Label("row:", skin);
        columnValue= new TextField("", skin);
        column= new Label("column:", skin);
        portrait1Landscape2Value= new TextField("", skin);
        portrait1Landscape2= new Label("portrait1Landscape2:", skin);
        difficultyValue= new TextField("", skin);
        difficulty= new Label("difficulty:", skin);
        gameModeValue= new TextField("", skin);
        gameMode= new Label("gameMode:", skin);

        symbolTypeValue= new TextField("", skin);
        symbolType= new Label("symbolType:", skin);

        TextButton button4 = new TextButton("swap orientation", skin);
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                GamePauseStage.this.stageInterface.updateFieldSwapOrientation();

            }
        });
        TextButton button3 = new TextButton("return to main menu", skin);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                setPause(false);
                black=1.0f;
                setVisible(false);
                GamePauseStage.this.stageInterface.goToMainStage();

            }
        });

        TextButton button2 = new TextButton("cancel changes", skin);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                GamePauseStage.this.stageInterface.updateOptionsGoToGameStage(0,
                        0,0,0,0,
                        0,0,0,0,0,0);

            }
        });
        TextButton button1 = new TextButton("Save changes", skin);
        button1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int posX,posY,rows,columns,width,height,portrait1Landscape2,zoom,difficulty, gameMode,symbolType;
                try {
                    zoom= Integer.valueOf(zoomModeValue.getText());
                } catch (NumberFormatException nfe) {
                    zoom= 0;
                }
                try {
                    rows= Integer.valueOf(rowValue.getText());
                } catch (NumberFormatException nfe) {
                    rows= 0;
                }
                try {
                    columns= Integer.valueOf(columnValue.getText());
                } catch (NumberFormatException nfe) {
                    columns= 0;
                }
                try {
                    portrait1Landscape2= Integer.valueOf(portrait1Landscape2Value.getText());
                } catch (NumberFormatException nfe) {
                    portrait1Landscape2= 0;
                }
                try {
                    width= Integer.valueOf(widthValue.getText());
                } catch (NumberFormatException nfe) {
                    width= 0;
                }
                try {
                    height= Integer.valueOf(heightValue.getText());
                } catch (NumberFormatException nfe) {
                    height= 0;
                }
                try {
                    posX= Integer.valueOf(posXValue.getText());
                } catch (NumberFormatException nfe) {
                    posX= 0;
                }
                try {
                    posY= Integer.valueOf(posYValue.getText());
                } catch (NumberFormatException nfe) {
                    posY= 0;
                }
                try {
                    difficulty= Integer.valueOf(difficultyValue.getText());
                } catch (NumberFormatException nfe) {
                    difficulty= 0;
                }
                try {
                    gameMode= Integer.valueOf(gameModeValue.getText());
                } catch (NumberFormatException nfe) {
                    gameMode= 0;
                }

                try {
                    symbolType= Integer.valueOf(symbolTypeValue.getText());
                } catch (NumberFormatException nfe) {
                    symbolType= 0;
                }
                GamePauseStage.this.stageInterface.updateOptionsGoToGameStage(rows,columns,
                        portrait1Landscape2,posX,posY,width,height,zoom,difficulty, gameMode,
                        symbolType);

            }
        });
*/
/*
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        table.add(zoomModeValue);
        table.add(zoomMode);
        table.row();
        table.add(posXValue);
        table.add(posX);
        table.row();
        table.add(posYValue);
        table.add(posY);
        table.row();
        table.add(widthValue);
        table.add(widthLbl);
        table.row();
        table.add(heightValue);
        table.add(heightLbl);

        table.row();
        table.add(rowValue);
        table.add(row);
        table.row();
        table.add(columnValue);
        table.add(column);
        table.row();
        table.add(portrait1Landscape2Value);
        table.add(portrait1Landscape2);
        table.row();
        table.add(difficultyValue);
        table.add(difficulty);
        table.row();
        table.add(gameModeValue);
        table.add(gameMode);

        table.row();
        table.add(symbolTypeValue);
        table.add(symbolType);
        table.row();
        table.add(button4);
        table.add(button1);

        table.add(button2);
        table.add(button3);
        addActor(table);
        */

/*
    }// end prepareui
    */
public void setDifficulty(int difficulty)
{
    difficultyOptionField.setSelectedIndex(difficulty);
}
    public void setSymbol(int symbol)
    {
        symbolOptionField.setSelectedIndex(symbol);
    }
    public void changeOrientation(boolean orient)
    {
        portrait=orient;
int longside,shortside;

if(width<height) {
    longside = height;
    shortside = width;
}
else {
    longside=width;
    shortside=height;
}
if(portrait)
{
    height=longside;
    width=shortside;
        }
        else
        {

        }
        //toggle bool for orientation
        //removeAllActors();
        for(int i = hexOptionFieldArray.size(); i >0;i--)
        {
            removeOptionFieldActors(hexOptionFieldArray.get(0));
            hexOptionFieldArray.remove(0);
        }
        setupFields();

        for(int i = 0; i<hexOptionFieldArray.size(); i++)
        {
            addHexesToStage(hexOptionFieldArray.get(i));
        }

        hexOptionField.enableOptions();



        updateUI();
        //addHexesToStage(hexOptionField);
    }

    public void setupFields()
    {
        hexOptionField = new HexOptionField( width, height,9,0,portrait,
                new String[]{""}
                ,0
        );
        createOptionsMenu();

        difficultyOptionField = new HexOptionField( width, height,13,2,
                portrait,
                new String[]{"Back", "0","1","2","3","4","5","6","7","8","9","10","11"}
                ,1
        );
        symbolOptionField= new HexOptionField(width,height,4,4,
                portrait,new String[]{"Back","Numbers","Shapes","Pictures - not implemented"}
                ,1);
        swapOrientationOptionField = new HexOptionField( width, height,3,3,
                portrait,
                new String[]{"Back", "Portrait","Landscape"}
                ,1
        );

        gameModeOptionField = new HexOptionField( width, height,3,6,
                portrait,
                new String[]{"Back", "Singles","Field"}
                ,2
        );

//this one needs to be updated on orientation change
        //construct this so it can  be added to the array
        noOfHexesOptionField = new HexOptionField( width, height,5,7,
                portrait,
                new String[]{""}
                ,0
        );
        createNoOfHexesOptionsField();

        zoomModeOptionField = new HexOptionField( width, height,3,8,
                portrait,
                new String[]{"Back", "Quick","Zoom"}
                ,2
        );

        scoreboardOptionField = new HexOptionField( width, height,4,9,
                portrait,
                new String[]{"Back", "Show Scoreboard","Submit","Show Offline Scoreboard"}
                ,0
        );


        hexOptionFieldArray.add(hexOptionField);
        hexOptionFieldArray.add(difficultyOptionField);
        hexOptionFieldArray.add(swapOrientationOptionField);
        hexOptionFieldArray.add(symbolOptionField);
        hexOptionFieldArray.add(gameModeOptionField);
        hexOptionFieldArray.add(noOfHexesOptionField);
        hexOptionFieldArray.add(zoomModeOptionField);
        hexOptionFieldArray.add(scoreboardOptionField);
    }
    public void removeAllActors()
    {
        for(Actor actor : getActors()) {
            //actor.remove();
            actor.addAction(Actions.removeActor());
        }
    }
    public void removeOptionFieldActors( HexOptionField hof)
    {
        for(int i=0;i<hof.noOfHexes;i++)
        {
            hof.hexOptionArray[i].addAction(Actions.removeActor());
        }
    }

void createOptionsMenu()
{
    hexOptionField = new HexOptionField(width,height,10,
            0,portrait,
            new String[]{"Cancel Changes","Return to Main Menu","Change Difficulty" ,
                    "Swap Orientation","Change Symbol Type",
                    "Save Changes","Change Game Mode","Change Number of Hexes","Change Zoom Mode","Scoreboard"
            }
            ,0);
}
    void createNoOfHexesOptionsField()
    {
        if(portrait)
        {
            noOfHexesOptionField = new HexOptionField( width, height,5,7,
                    portrait,
                    new String[]{"Back", "6","15","24","40"}
                    ,1
            );
        }
        else
        {
            noOfHexesOptionField = new HexOptionField( width, height,5,7,
                    portrait,
                    new String[]{"Back", "3","10","21","36"}
                    ,1
            );
        }
    }

    public void setOrientation(int o)
    {
        swapOrientationOptionField.setSelectedIndex(o);
    }
    public void setGameMode(int g)
    {
        gameModeOptionField.setSelectedIndex(g);
    }
    public void setNoOfHexes(int n)
    {
        noOfHexesOptionField.setSelectedIndex(n);
    }
    public void setZoomMode(int z)
    {
        zoomModeOptionField.setSelectedIndex(z);
    }
    public void setScoreboardMode(int s)
    {
        scoreboardOptionField.setSelectedIndex(s);
    }
public void setScore(int score)
{
    this.score = score;
}

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }

    void setActiveFieldIndex(int activeFieldIndex)
        {
            fieldIndex=activeFieldIndex;
        }


    public void prepareUI()
    {
         dateTimeWidth=0;
          noOfHexesWidth=0;
          ScoreWidth=0;
         scoreboardHeight=800;
         screenWidth=800;
         for(int i =0;i<scoreboardRowArray.length;i++)
         {
             scoreboardRowArray[i]= new ScoreboardRow(font);
         }
         offlineScoreboardVisible = false;
         continueButton.setVisible(false);



    }//end prepareUI

    public void updateUIText()
    {
//this will read in the values from the saved text file into our table to display it
        if(Gdx.files.local("offlineScoreboard2.txt").exists()) {
            String s = Gdx.files.local("offlineScoreboard2.txt").readString();
            String[] sa = s.split(",");

            for(int i = 0 ; i< scoreboardRowArray.length;i++)
//scoreboardRowArray.length should be 14, including the headings
            {

                scoreboardRowArray[i].setText(sa[i*3],sa[i*3+1],sa[i*3+2]);

                if(dateTimeWidth<scoreboardRowArray[i].getTextWidth(sa[i*3]))
                {
                    dateTimeWidth=scoreboardRowArray[i].getTextWidth(sa[i*3]);
                }
                if(noOfHexesWidth<scoreboardRowArray[i].getTextWidth(sa[i*3+1]))
                {
                    noOfHexesWidth=scoreboardRowArray[i].getTextWidth(sa[i*3+1]);
                }
                if(ScoreWidth<scoreboardRowArray[i].getTextWidth(sa[i*3+2]))
                {
                    ScoreWidth=scoreboardRowArray[i].getTextWidth(sa[i*3+2]);
                }

            }//end for
            for(int i = 0 ; i< scoreboardRowArray.length;i++)
            {//need to start a new for loop, because now we know the text width of each column
                scoreboardRowArray[i].setValues( (screenWidth-(dateTimeWidth+noOfHexesWidth+ScoreWidth))/2,
                        dateTimeWidth, noOfHexesWidth, ScoreWidth, 100+scoreboardHeight/14*i);

            }
        }
        else//if file doesn't exist, create it
        {
            FileHandle file = Gdx.files.local("offlineScoreboard2.txt");
            file.writeString("DateTime,Number of Hexes,Score,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                    +"DateTime,Number of Hexes,Score,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , ,"
                            +" , , "
                    , false);

        }
    }//end updateUIText

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

           // setPause(false);
            //pauseImg1.setVisible(false);
           // setVisible(false);
            //black = 1.f;
            //GamePauseStage.this.stageInterface.goToGameStage();
   //optionsHandler.click(0,fieldIndex);

if(continueButton.isVisible())
{
    continueButton.setVisible(false);
    offlineScoreboardVisible=false;
    GamePauseStage.this.stageInterface.goToScoreboardOption();

}
else if(fieldIndex==0)
{
cancelOptions();}
else if(gameOver)
{
    stageInterface.goToMainStage();

}
else
{
    stageInterface.goToMainOption();
}
        }
        return false;
    }
}