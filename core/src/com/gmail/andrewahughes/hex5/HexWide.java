package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Andrew Hughes on 26/09/2018.
 */

public class HexWide extends Actor{
    int selectedSector;
    int approxSector;
    int blue=0;
    private ShapeRenderer renderer = new ShapeRenderer();//it’s probably better to pass this in rather than make a new shapeRenderer for each hex?

    public final float edgeSize;
    public final float altitudeSize;
    public float posX, posY;
    public boolean visible ;



    public HexWide(final float edgeSize, final float posX, final float posY)
    {
        this.edgeSize = edgeSize;
        altitudeSize = edgeSize * 0.866025403784439f;
        this.posX = posX;
        this.posY = posY;
        visible=true;

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blue++;
//this has already implicitly tested if the touch is within the bounding rect, assuming the tapSquare is bounding
//test if touch is within smaller than bounding circle
                if(pointInCircle(posX, posY, edgeSize,x,y))
                {
//test angle
//returns sector of the hex touch landed in approx ( could technically be outside the hex but still in a sort of projection of that sector)
//sector 0 will range from the 9 o’clock position to the 11 o’clock position, sector 1 from the 11 o’clock to 1 o’clock position and the sectors continue like that clockwise.
                    approxSector = getApproxSector(y,posY,x,posX);

//if angle does not prove touch is in hex then test the corner case that the angle indicates the touch might be in
//if the touch is in approxSector 1 or 4 we know it is deffo in the hex because these sectors are flush with the bounding box and we already know the touch is in the bounding box. If it’s in some other sector we need more tests
                    if(approxSector==1||approxSector==4)
                    {
                        selectedSector = approxSector;
                    }
                    else //sector is something other than 1 or 4
                    {
                        if(approxSector==5)//if approxSector is the bottom left use this test
                        {//if touch point(relative to bottom left corner of hex) y coord is more than given formula then it is inside the hex
                            if(y-(posY)>((-x-(posX))*altitudeSize/edgeSize) + altitudeSize)
                            {
                                selectedSector = approxSector;
                            }
                        }
                        else if (approxSector==0)
                        { //top left
                            if(y-(posY)<((x-(posX))*altitudeSize/edgeSize) + altitudeSize)
                            {
                                selectedSector = approxSector;
                            }
                        }
                        else if (approxSector==2)
                        { //top right
                            if(y-(posY)<((-x-(posX))*altitudeSize/edgeSize) + altitudeSize*5)
                            {
                                selectedSector = approxSector;

                            }
                        }
                        else if (approxSector==3)
                        { //bottom right
                            if(y-(posY)>((-x-(posX))*altitudeSize/edgeSize) + altitudeSize*3)
                            {
                                selectedSector = approxSector;
                            }
                        }
                        else
                        {//if no corner case is true then the touch is not in the hex, return 6 to indicate this, but do not set selectedSector to 6
                            //return 6;
                            approxSector=6;
                        }

                    }
                }
                //return selectedSector;

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

    public int getApproxSector(float y, float posY, float x, float posX)
    {
        double angle = Math.atan2(y-posY,x-posX);
//this will give the angle in radians, the angle is taken between the line from the centre of the hex to the touch point and the line from centre of the hex to the right (i think. This would mean touching to the right of the centre would be angle 0, above the centre would be angle 90 etc except it’s given in radians and rather that give 270 it will give -90

//because angles such as 270 are expressed as -90 if we do 180 - angle we will get all positive angles, (consequence of this is that the angles are now flipped 180 degrees so 0 would be to the left of centre, 90 would be below centre), translated into radians that’s pi-angle and we’ll get values from 0 to about <6.28 aka (2*PI), if we divide it by PI/3 aka (60 degrees) and rounddown we will get an integer from 0 to 5. We can return this simple integer to refer to which sector the angle is in, sector 0 will range from the 9 o’clock position to 11 o’clock, 1 will be from 11 to 1 o’clock, 2 will be 1 to 3 o’clock etc

//
        approxSector = (int)(Math.floor((Math.PI-angle)/(Math.PI/3)));
        return approxSector;
    }


    public void draw() {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(0,100,40*blue,1);

            drawWideHex(renderer,posX,posY,edgeSize);
            renderer.end();

        }
    }

    void drawWideHex(ShapeRenderer sr, float originX, float originY, float edgeSize)
    {
        // draws a ‘wide’ hex hex with flat top and bottom. originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral triangle with edge size defined by edgeSize


        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line(originX + (edgeSize / 2), originY - altitudeSize, originX + edgeSize, originY);
        sr.line(originX + edgeSize, originY , originX + (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX + (edgeSize / 2) , (int)(originY + altitudeSize), originX - (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY + altitudeSize), originX - edgeSize , originY);
        sr.line(originX - edgeSize , originY , originX - (edgeSize / 2) , (int)(originY - altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY - altitudeSize),originX + (edgeSize / 2) , (int)(originY - altitudeSize));

    }



}

