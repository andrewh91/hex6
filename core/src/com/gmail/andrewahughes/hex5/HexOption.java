package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOption extends Actor {
    float width, height, centreX, centreY,edgeSize,altitudeSize,clickTimer=5 ;
    int  hexIndex, fieldIndex;
    BitmapFont font = new BitmapFont();
    GlyphLayout glyphLayout = new GlyphLayout();
    String text = new String();

    OptionsHandler optionsHandler;
    public HexOption(final float edgeSize, final float centreX, final float centreY,
                     final int hexIndex, final int fieldIndex, final OptionsHandler optionsHandler) {
      this.optionsHandler=optionsHandler;
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
                //if(pointInCircle(edgeSize, altitudeSize, altitudeSize, x, y))
                {
                    optionsHandler.click(hexIndex, fieldIndex);
                    clickTimer=5000;
                }
            }
        });

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
    public void draw(ShapeRenderer sr,boolean background) {
        act(Gdx.graphics.getDeltaTime());
if(this.isVisible()) {
    if (background) {
        sr.setColor(0, 0, 0, 1);
    } else if (clickTimer > 0) {
        sr.setColor(100, 100, 100, 1);
        clickTimer -= Gdx.graphics.getDeltaTime();
    } else {
        sr.setColor(100, 100, 40, 1);
        clickTimer = 500f;
    }
    sr.line(centreX + (edgeSize / 2), centreY - altitudeSize, centreX + edgeSize, centreY);
    sr.line(centreX + edgeSize, centreY, centreX + (edgeSize / 2), (int) (centreY + altitudeSize));
    sr.line(centreX + (edgeSize / 2), (int) (centreY + altitudeSize), centreX - (edgeSize / 2), (int) (centreY + altitudeSize));
    sr.line(centreX - (edgeSize / 2), (int) (centreY + altitudeSize), centreX - edgeSize, centreY);
    sr.line(centreX - edgeSize, centreY, centreX - (edgeSize / 2), (int) (centreY - altitudeSize));
    sr.line(centreX - (edgeSize / 2), (int) (centreY - altitudeSize), centreX + (edgeSize / 2), (int) (centreY - altitudeSize));
}

    }
    public void drawText (SpriteBatch sb) {
        if (this.isVisible()) {
            font.draw(sb, "" + hexIndex + " " + text, centreX + glyphLayout.width / 2, centreY + glyphLayout.height / 2);
        }

    }


}