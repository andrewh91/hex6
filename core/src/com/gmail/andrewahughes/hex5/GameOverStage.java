package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;



public class GameOverStage extends Stage {

    private boolean visible = false;
    private boolean pause = false;
    public StageInterface stageInterface;
    public Image pauseImg1;
    public float black=1.0f;
    Skin skin;
    private TextureAtlas atlas;


    private Label gameOver;
    private Label timer;
    private Label difficulty;
    int timeValue, difficultyValue;



    public GameOverStage(Viewport viewport, Texture texture, final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;


        Table table = new Table();
        table.setFillParent(true);
        table.left();

        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));

/*
        final Image pauseImg = new Image(texture);
        this.pauseImg1 =pauseImg;
        this.pauseImg1.setVisible(false);//start off invisible



        table.add(pauseImg);
        table.center();
        table.row().pad(40);
        table.row();
*/

        prepareUI();

        addActor(table);
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {

            Gdx.gl.glClearColor(0.9f*black, 0.0f*black, 0.4f*black, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            super.draw();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setPause(boolean pause) {
        this.pause = pause;
    }


    public void setScore(int timeValueArg, int difficultyValueArg)
    {
        timeValue = timeValueArg;
        difficultyValue= difficultyValueArg;
        updateUI();//prepare ui again to set the score values to the text displayed on acreen
    }
public void updateUI()
{


    timer.setText("Time: "+timeValue);
    difficulty.setText("Difficulty: "+difficultyValue);
}

    public void prepareUI()
    {

        gameOver= new Label("Game Over!", skin);
        timer=new Label("Time: "+timeValue, skin);
        difficulty=new Label("Difficulty: "+difficultyValue, skin);

/*
        zoomModeValue= new TextField("", skin);
        zoomMode= new Label("quick1Zoom2:", skin);
        posXValue= new TextField("", skin);
        posX= new Label("posX:", skin);
        posYValue= new TextField("", skin);
        posY= new Label("posY:", skin);
        widthValue= new TextField("", skin);
        width= new Label("width:", skin);
        heightValue= new TextField("", skin);
        height= new Label("height:", skin);
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
*/

        TextButton toMainStageBtn= new TextButton("Main Menu", skin);
        toMainStageBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                setPause(false);
                black=1.0f;
                setVisible(false);
                GameOverStage.this.stageInterface.goToMainStage();

            }
        });


        TextButton resetBtn= new TextButton("New Game", skin);
        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                GameOverStage.this.stageInterface.updateOptionsGoToGameStage(0,
                        0,0,0,0,
                        0,0,0,0,0);
//before going to the gameover stage from the game stage i should ahve reset the game, so returning to it now should just start the game again

            }
        });


        TextButton submitScoreBtn= new TextButton("Submit Score", skin);
        submitScoreBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //submit score

            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        table.add(gameOver);
        table.row();
        table.row();
        table.add(timer);
        table.add(difficulty);

        table.row();
        table.row();

        table.add(toMainStageBtn);
        table.row();
        table.add(resetBtn);
        table.row();
        table.add(submitScoreBtn);
        addActor(table);


    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

            setPause(false);
            black=1.0f;
            setVisible(false);
            GameOverStage.this.stageInterface.goToMainStage();




        }
        return false;
    }
}

