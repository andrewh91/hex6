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

public class HexWide extends Actor{
    int selectedSector;
    int approxSector;
    int blue=0;

    BitmapFont font = new BitmapFont();
    String text = new String();
    String text1 = new String();
    String text2 = new String();
    SpriteBatch spriteBatch = new SpriteBatch();

    private ShapeRenderer renderer = new ShapeRenderer();//it’s probably better to pass this in rather than make a new shapeRenderer for each hex?

    public final float edgeSize;
    public final float altitudeSize;
    public float posX, posY;
    public boolean visible ;


    public HexWide(final float edgeSize, final float centreX, final float centreY)
    {
        this.edgeSize = edgeSize;
        altitudeSize = edgeSize * 0.866025403784439f;
        this.posX = centreX;
        this.posY = centreY;
        visible=true;
        setBounds(centreX-edgeSize,centreY-altitudeSize,edgeSize*2,altitudeSize*2);//posX gives the centre so need  to offset that

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //x=Gdx.input.getX();
                //y=Gdx.input.getY();
                //worldCoordinates = orthographicCamera.unproject(new Vector3(x,y,0));
                //x=worldCoordinates.x;
                //y=worldCoordinates.y;
                blue++;
                if(x>posX-edgeSize&&x<posX+edgeSize&&y>posY-altitudeSize&&y<posY+altitudeSize){
                    text1=" in square ";
                    if (pointInCircle(posX, posY, altitudeSize,x, y)) {
                        text2=" in circle ";
                        approxSector = getApproxSector(y, posY, x, posX);
                    }
                    else{
                        text2=" not in circle but might be in hex... ";
                        if (approxSector == 1 || approxSector == 4) {
                            selectedSector = approxSector;
                            text2=" in hex ";
                        }
                        else{
                            if (approxSector == 5){
                                if (y - (posY) > ((-x - (posX)) * altitudeSize / edgeSize) + altitudeSize) {
                                    selectedSector = approxSector;
                                }
                            }
                            else if (approxSector == 0) {
                                if (y - (posY) < ((x - (posX)) * altitudeSize / edgeSize) + altitudeSize) {
                                    selectedSector = approxSector;
                                }
                            }
                            else if (approxSector == 2) {
                                if (y - (posY) < ((-x - (posX)) * altitudeSize / edgeSize) + altitudeSize * 5) {
                                    selectedSector = approxSector;
                                }
                            }
                            else if (approxSector == 3) {
                                if (y - (posY) > ((-x - (posX)) * altitudeSize / edgeSize) + altitudeSize * 3) {
                                    selectedSector = approxSector;
                                }
                            }
                            else{
                                approxSector = 6;
                                text2=" not in hex ";
                            }
                        }
                    }
                }
                else{
                    text1=" not in square";
                    blue=0;
                }
                text = "" + selectedSector + " - " + x+ " - " + y;
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


    public void draw(ShapeRenderer sr) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);

            sr.setColor(0,100,40*blue,1);
            drawWideHex(sr,posX,posY,edgeSize);
            //renderer.rect(posX-edgeSize,posY-altitudeSize,edgeSize*2,altitudeSize*2);
            //sr.end();
            spriteBatch.begin();
            font.draw(spriteBatch, "Hello World! "+" x "+posX +" y "+ posY+ " edge "+edgeSize+text+text1+text2, posX, posY);

            spriteBatch.end();


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

