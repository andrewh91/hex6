package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Andrew Hughes on 01/03/2019.
 */
//copy hex option 
public class HexOptionBKGD {
    float width, height, centreX, centreY,edgeSize,altitudeSize ;
boolean visible = false;

    public HexOptionBKGD(final float edgeSize, final float centreX, final float centreY) {
        this.edgeSize=edgeSize*0.95f;
        this.centreX=centreX;
        this.centreY=centreY;
        altitudeSize = this.edgeSize * 0.866025403784439f;
    }

    public void draw(ShapeRenderer sr) {
        if(visible) {


            sr.setColor(0.77f,0.7f,0.035f, 1);



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


}



















