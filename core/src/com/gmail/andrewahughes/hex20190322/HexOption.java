package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOption extends Actor {
    float width, height, centreX, centreY,edgeSize,altitudeSize,clickTimer=1f ;
    public int  hexIndex, fieldIndex;
    BitmapFont font = new BitmapFont();
    GlyphLayout glyphLayout = new GlyphLayout();
    String text = new String();
     float edgeSizeStatic;

    public HexOption(final float edgeSize, final float centreX, final float centreY,
                     final int hexIndex, final int fieldIndex ) {
        this.edgeSize=edgeSize;
        edgeSizeStatic=edgeSize;
        this.centreX=centreX;
        this.centreY=centreY;
        altitudeSize = edgeSize * 0.866025403784439f;
        this.hexIndex=hexIndex;
        this.fieldIndex=fieldIndex;
        setBounds(centreX - edgeSize, centreY - altitudeSize, edgeSize * 2,
                altitudeSize * 2);
        disable();
font.getData().setScale(2f);
        /*this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //if(pointInCircle(edgeSize, altitudeSize, altitudeSize, x, y))
                {
                    optionsHandler.click(hexIndex, fieldIndex);
                    clickTimer=0f;
                }
            }
        });
        */

    }
    public void setText(String a)
    {
        text=a;
        glyphLayout.setText(font,text);
    }
    public boolean pointInCircle(float circleOriginX, float circleOriginY,
                                 float radius, float pointX, float pointY)
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
    public void disable()
    {
        this.setTouchable(Touchable.disabled);
        this.setVisible(false);
    }
    public void enable()
    {
        this.setTouchable(Touchable.enabled);
        this.setVisible(true);
    }
    public void draw(ShapeRenderer sr,boolean selected) {
        act(Gdx.graphics.getDeltaTime());
if(this.isVisible()) {
    sr.setColor(1.00f,0.75f,0.06f, 1);
    if(selected)
    {
        sr.setColor(0.94f,0.84f,0.42f, 1);

    }
edgeSize=edgeSizeStatic*0.95f;
    altitudeSize=edgeSize*0.866025403784439f;
drawHex(sr);

}

    }
    public void drawText (SpriteBatch sb) {
        if (this.isVisible()) {
            font.draw(sb, " " + text, centreX-edgeSize*0.95f, centreY +glyphLayout.height / 2,edgeSize*2f, Align.center,true);
           // font.draw(sb, "" + hexIndex + " " + text, centreX + glyphLayout.width / 2, centreY + glyphLayout.height / 2,altitudeSize*2f, Align.center,true);
        }

    }

    void drawHex(ShapeRenderer sr)
    {

        sr.triangle(
                centreX+edgeSize,
                centreY,
                centreX+edgeSize/2,
                centreY+altitudeSize,
                centreX-edgeSize/2,
                centreY+altitudeSize);
        sr.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY+altitudeSize,
                centreX-edgeSize,
                centreY);
        sr.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY-altitudeSize);
        sr.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY-altitudeSize,
                centreX+edgeSize/2,
                centreY-altitudeSize);
    }

}