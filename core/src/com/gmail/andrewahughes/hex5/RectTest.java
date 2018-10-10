package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.awt.Rectangle;

/**
 * Created by Andrew Hughes on 26/09/2018.
 */

public class RectTest extends Actor{
    int selectedSector;
    int approxSector;
    int blue=0;

    BitmapFont font = new BitmapFont();
    String text = new String();
    String text1 = new String();
    String text2 = new String();
    SpriteBatch spriteBatch = new SpriteBatch();

    private ShapeRenderer renderer = new ShapeRenderer();//it’s probably better to pass this in rather than make a new shapeRenderer for each hex?

    public boolean visible ;
    public float px,py,width,height;


    public RectTest(final float px, final float py, final float width , final float height)
    {
        visible=true;
        setBounds(px,py,width,height);//posX gives the centre so need  to offset that
        this.px=px;
        this.py=py;
        this.width=width;
        this.height=height;

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //x=Gdx.input.getX();
                //y=Gdx.input.getY();
                text = "  x "  + x+" - y " + y ;

                //worldCoordinates = orthographicCamera.unproject(new Vector3(x,y,0));
                //x=worldCoordinates.x;
                //y=worldCoordinates.y;
                blue++;
                if(x>0&&x<0+width&&y>0&&y<0+height) {
                    text2 = " in square ";
                }
                else{
                        text2=" not in square";
                        blue=0;
                    }
                    text1 = " wx "  + x+" - wy " + y ;
                }
            });

        }


    public void draw(ShapeRenderer sr) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);



            sr.setColor(0,100,40*blue,1);
            sr.rect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
            //renderer.rect(posX-edgeSize,posY-altitudeSize,edgeSize*2,altitudeSize*2);
            //sr.end();

            spriteBatch.begin();
            font.draw(spriteBatch, "Hello World! "+" x "+px +" y "+ py+text+text1+text2, px, py);

            spriteBatch.end();


        }
    }





}

