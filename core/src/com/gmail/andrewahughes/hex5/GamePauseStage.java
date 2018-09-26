package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public GamePauseStage(Viewport viewport, Texture texture,final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        final Image pauseImg = new Image(texture);
        this.pauseImg1 =pauseImg;
        this.pauseImg1.setVisible(true);//start off invisible

        this.pauseImg1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(pauseImg1.isVisible())
                {//if you click the badlogic image, go back to main menu
                    //pauseImg1.setVisible(false);
                    setPause(false);
                    black=1.0f;
                    setVisible(false);
                    GamePauseStage.this.stageInterface.goToMainStage();
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
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

                setPause(false);
                //pauseImg1.setVisible(false);
                setVisible(false);
                //black = 1.f;
                GamePauseStage.this.stageInterface.goToGameStage();




        }
        return false;
    }
}
