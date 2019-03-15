package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GameOverStage extends Stage {

    private boolean visible = false;
    public StageInterface stageInterface;
    SpriteBatch sb = new SpriteBatch();
    BitmapFont font = new BitmapFont();
    GlyphLayout glyphLayout = new GlyphLayout();
    CentredText[] centredTextArray = new CentredText[12];
    int screenWidth, screenHeight;
    Actor continueButton = new Actor();
    int continueButtonTimer;

    int scoreValue, difficultyValue,noOfHexesValue, gameModeValue;

    public GameOverStage(Viewport viewport, Texture texture, final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;
        screenWidth=stageInterface.getScreenWidth();
        screenHeight=stageInterface.getScreenHeight();
continueButtonTimer=100;
         continueButton = new Actor();
        continueButton.setBounds(0,0,screenWidth,screenHeight);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                {
                    if(continueButtonTimer<=0)
                    {
                    setVisible(false);
                    //if practise use false bool as argument, if not use true
                    GameOverStage.this.stageInterface.goToGamePauseStage(!stageInterface.getPractise());
                    continueButtonTimer=100;
                }
                }
            }
        });
        continueButton.setVisible(false);
this.addActor(continueButton);

    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());
        if (visible) {
            if(continueButtonTimer>0)
            {
                continueButtonTimer -= Gdx.graphics.getDeltaTime();
            }
            sb.setProjectionMatrix(getViewport().getCamera().combined);
sb.begin();
            Gdx.gl.glClearColor(0.86f, 0.65f, 0.22f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            for(int i = 0 ; i< centredTextArray.length;i++)
            {
                centredTextArray[i].draw(sb);
            }
            sb.end();
            super.draw();
        }
    }

    public void setVisible(boolean visible) {
        continueButton.setBounds(0,0,stageInterface.getScreenWidth(),stageInterface.getScreenHeight());

        this.visible = visible;
        continueButton.setVisible(visible);

    }


    public void setScore(int noOfHexes,int score,int difficulty, int gameMode)
    {

        screenWidth=stageInterface.getScreenWidth();
        screenHeight=stageInterface.getScreenHeight();
        centredTextArray[0]= new CentredText("Complete!");
        centredTextArray[1]= new CentredText("");
        centredTextArray[2]= new CentredText("Game Mode");
        if(gameMode==1)
        {
            centredTextArray[3]= new CentredText("Singles");
        }
        else
        {
            centredTextArray[3]= new CentredText("Field");
        }

        centredTextArray[4]= new CentredText("Number of Hexes");
        centredTextArray[5]= new CentredText(""+noOfHexes);
        centredTextArray[6]= new CentredText("Difficulty");
        centredTextArray[7]= new CentredText(""+difficulty);
        centredTextArray[8]= new CentredText("Score");
        centredTextArray[9]= new CentredText(""+score);
        centredTextArray[10]= new CentredText("");
        centredTextArray[11]= new CentredText("Tap to Continue");

        for(int i = 0 ; i < centredTextArray.length;i++)
        {
            centredTextArray[i].centreText(screenWidth,
                    screenHeight-screenHeight/(centredTextArray.length+1)*(i+1));
        }
    }


    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

            setVisible(false);
            GameOverStage.this.stageInterface.goToGamePauseStage(true);

        }
        return false;
    }

}

