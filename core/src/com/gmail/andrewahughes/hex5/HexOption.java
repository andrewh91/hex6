package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOption extends Actor {
    float width, height, centreX, centreY,edgeSize,altitudeSize,clickTimer=0 ;
    int  hexIndex, fieldIndex;

    public HexOption(final float edgeSize, final float centreX, final float centreY,
                     final int hexIndex, final int fieldIndex, final OptionsHandler optionsHandler) {
        this.edgeSize=edgeSize;
        this.centreX=centreX;
        this.centreY=centreY;
        altitudeSize = edgeSize * 0.866025403784439f;
        this.hexIndex=hexIndex;
        setBounds(centreX - edgeSize, centreY - altitudeSize, edgeSize * 2,
                altitudeSize * 2);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(pointInCircle(edgeSize, altitudeSize, altitudeSize, x, y))
                {
                    optionsHandler.click(hexIndex, fieldIndex);
                    clickTimer=500;
                }
            }
        });

    }
    public boolean pointInCircle(float circleOriginX, float circleOriginY, float radius, float pointX, float pointY)
    {//returns true if given circle contains given point
        if((pointX - circleOriginX)*(pointX - circleOriginX) + (pointY - circleOriginY)*(pointY - circleOriginY)<radius*radius)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void draw(ShapeRenderer sr) {
        act(Gdx.graphics.getDeltaTime());
        if(clickTimer>0)
        {
            sr.setColor(100,100,100,1);
            clickTimer-=Gdx.graphics.getDeltaTime();
        }
        else
        {
            sr.setColor(100,100,40,1);
        }
        sr.line(centreX + (edgeSize / 2), centreY - altitudeSize, centreX + edgeSize, centreY);
        sr.line(centreX + edgeSize, centreY , centreX + (edgeSize / 2) , (int)(centreY + altitudeSize));
        sr.line(centreX + (edgeSize / 2) , (int)(centreY + altitudeSize), centreX - (edgeSize / 2) , (int)(centreY + altitudeSize));
        sr.line(centreX - (edgeSize / 2) , (int)(centreY + altitudeSize), centreX - edgeSize , centreY);
        sr.line(centreX - edgeSize , centreY , centreX - (edgeSize / 2) , (int)(centreY - altitudeSize));
        sr.line(centreX - (edgeSize / 2) , (int)(centreY - altitudeSize),centreX + (edgeSize / 2) , (int)(centreY - altitudeSize));


    }
}