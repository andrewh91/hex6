package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverStage extends Stage {

    private boolean visible = false;
    public StageInterface stageInterface;
    SpriteBatch sb = new SpriteBatch();
    BitmapFont font = new BitmapFont();
    float fontScale=5f;
    float fontSpeed=1;
    GlyphLayout glyphLayout = new GlyphLayout();
    CentredText[] centredTextArray = new CentredText[12];
    int screenWidth, screenHeight;
    Actor continueButton = new Actor();
    float continueButtonTimer;
int scorePosY=0;
    int scoreValue, difficultyValue,noOfHexesValue, gameModeValue =0;

    public GameOverStage(Viewport viewport, final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;
        screenWidth=stageInterface.getScreenWidth();
        screenHeight=stageInterface.getScreenHeight();
continueButtonTimer=0;
         continueButton = new Actor();
        continueButton.setBounds(0,0,screenWidth,screenHeight);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                {
                    if(continueButtonTimer>3)
                    {
                    setVisible(false);
                    //if practise use false bool as argument, if not use true
                    GameOverStage.this.stageInterface.goToGamePauseStage(!stageInterface.getPractise());
                    stageInterface.prepareNewGame();
                    continueButtonTimer=0;
                    fontScale=5f;
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

                continueButtonTimer += Gdx.graphics.getDeltaTime();


            if(continueButtonTimer>3)
            {
               if(scorePosY >50)
               {
                   scorePosY-=Gdx.graphics.getDeltaTime()*fontSpeed;
               }
               else
               {
                   scorePosY=50;
               }
               if(fontScale>2f)
               {
                   fontScale-=(Gdx.graphics.getDeltaTime()*3);
               }
               else
               {
                   fontScale=2f;
               }
               font.getData().setScale(fontScale);
            }
            sb.setProjectionMatrix(getViewport().getCamera().combined);
sb.begin();
            if(gameModeValue==1) {//if singkes mode draw back buffer so game over is not transparent, if field mode shiw the field underneath game over screen
                Gdx.gl.glClearColor(0.86f, 0.65f, 0.22f, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            }
            /*
            for(int i = 0 ; i< centredTextArray.length;i++)

            {
                centredTextArray[i].draw(sb);
            }
            */


            glyphLayout.setText(font,("Complete! Score:" + (int)(scoreValue)));

            font.draw(sb, "Complete! Score:" + (int)(scoreValue) ,
                    stageInterface.getScreenWidth()/2-glyphLayout.width/2
                    , scorePosY);

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
scoreValue=score;
        gameModeValue=gameMode;//record if its singles modr or not, if so we need a back buffer
scorePosY=stageInterface.getScreenHeight()/2;
font.getData().setScale(fontScale);
fontSpeed = (stageInterface.getScreenHeight()-50)/2;
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

