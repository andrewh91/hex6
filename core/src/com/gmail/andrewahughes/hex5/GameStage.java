package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by Andrew Hughes on 13/09/2018.
 */

public class GameStage extends Stage {
    private boolean visible = false;
    private boolean pause = false;
    private ShapeRenderer renderer = new ShapeRenderer();
    public StageInterface stageInterface;
    public float black=1.0f;
    public Image pauseImg1;
    HexWide hexWide = new HexWide(50,400,200);
    public GameStage(Viewport viewport, Texture texture,final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;

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

    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {

            Gdx.gl.glClearColor(0.9f*black, 0.9f*black, 0.7f*black, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Color.BLUE);;

            drawWideHex(renderer,(int)(150),100,90);
            drawTallHex(renderer,70,200,70);
            renderer.end();

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
