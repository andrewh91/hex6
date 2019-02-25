package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    OptionsHandler optionsHandler;
    HexOptionField hexOptionField;
    HexOptionField difficultyOptionField;
    HexOptionField symbolOptionField;
    HexOptionField swapOrientationOptionField ;
    HexOptionField gameModeOptionField ;
    HexOptionField noOfHexesOptionField;
    HexOptionField zoomModeOptionField ;

    ShapeRenderer shapeRenderer  = new ShapeRenderer();
SpriteBatch spriteBatch = new SpriteBatch();
    int x=0,y=0,width=720,height=1280,noOfOptions=7;
    boolean portrait =true;
 int fieldIndex=0;
    BitmapFont font = new BitmapFont();

    public GamePauseStage(Viewport viewport, Texture texture,
                          final StageInterface stageInterface) {
        super( viewport );
         optionsHandler =new OptionsHandler(stageInterface);
        createOptionsMenu();

        difficultyOptionField = new HexOptionField( width, height,13,2,
                optionsHandler,portrait,
                new String[]{"Back", "0","1","2","3","4","5","6","7","8","9","10","11"}
                ,1
        );
        symbolOptionField= new HexOptionField(width,height,4,4,
                optionsHandler,portrait,new String[]{"Back","Numbers","Shapes","Pictures - not implemented"}
                ,1);
        swapOrientationOptionField = new HexOptionField( width, height,3,3,
                optionsHandler,portrait,
                new String[]{"Back", "Portrait","Landscape"}
                ,1
        );

        gameModeOptionField = new HexOptionField( width, height,3,6,
                optionsHandler,portrait,
                new String[]{"Back", "Singles","Field"}
                ,2
        );

//this one needs to be updated on orientation change
        createNoOfHexesOptionsField();

        zoomModeOptionField = new HexOptionField( width, height,3,8,
                optionsHandler,portrait,
                new String[]{"Back", "Quick","Zoom"}
                ,2
        );


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

            hexOptionField.draw(shapeRenderer);
            difficultyOptionField.draw(shapeRenderer);
            symbolOptionField.draw(shapeRenderer);
            swapOrientationOptionField.draw(shapeRenderer);
            gameModeOptionField.draw(shapeRenderer);
            noOfHexesOptionField.draw(shapeRenderer);
            zoomModeOptionField.draw(shapeRenderer);



            shapeRenderer.end();
            spriteBatch.setProjectionMatrix(getViewport().getCamera().combined);

spriteBatch.begin();
            hexOptionField.drawText(spriteBatch);
            difficultyOptionField.drawText(spriteBatch);
            symbolOptionField.drawText(spriteBatch);
            swapOrientationOptionField.drawText(spriteBatch);
            gameModeOptionField.drawText(spriteBatch);
            noOfHexesOptionField.drawText(spriteBatch);
            zoomModeOptionField.drawText(spriteBatch);
font.draw(spriteBatch,fieldIndex+""+hexOptionField.hexOptionArray[0].isTouchable(),50,100);
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


    }
public void setDifficulty(int difficulty)
{
    difficultyOptionField.setSelectedIndex(difficulty);
}
    public void setSymbol(int symbol)
    {
        symbolOptionField.setSelectedIndex(symbol);
    }
    public void changeOrientation(boolean orientation)
    {
        portrait=orientation;
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
        removeOptionFieldActors(hexOptionField );
        createOptionsMenu();
        addHexesToStage(hexOptionField);


        removeOptionFieldActors(noOfHexesOptionField );
        createNoOfHexesOptionsField();
        addHexesToStage(noOfHexesOptionField);
        noOfHexesOptionField.disableOptions();
        //addHexesToStage(hexOptionField);
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
    hexOptionField = new HexOptionField(width,height,9,
            0,optionsHandler,portrait,
            new String[]{"Cancel Changes","Return to Main Menu","Change Difficulty" ,
                    "Swap Orientation","Change Symbol Type",
                    "Save Changes","Change Game Mode","Change Number of Hexes","Change Zoom Mode"
            }
            ,0);
}
    void createNoOfHexesOptionsField()
    {
        if(portrait)
        {
            noOfHexesOptionField = new HexOptionField( width, height,5,7,
                    optionsHandler,portrait,
                    new String[]{"Back", "6","15","24","40"}
                    ,1
            );
        }
        else
        {
            noOfHexesOptionField = new HexOptionField( width, height,5,7,
                    optionsHandler,portrait,
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

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }

    void setActiveFieldIndex(int activeFieldIndex)
        {
            fieldIndex=activeFieldIndex;
        }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

           // setPause(false);
            //pauseImg1.setVisible(false);
           // setVisible(false);
            //black = 1.f;
            //GamePauseStage.this.stageInterface.goToGameStage();
optionsHandler.click(0,fieldIndex);



        }
        return false;
    }
}