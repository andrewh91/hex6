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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 26/09/2018.
 */

public class HexTall extends Actor {
    int selectedSector;
    int approxSector;
    int blue = 0,red =0,redSymbol=0;

    BitmapFont font = new BitmapFont();
    String text = new String();
    String text1 = new String();
    String text2 = new String();
    String text3 = new String();

    String symbol0 = new String();
    String symbol1 = new String();
    String symbol2 = new String();
    String symbol3 = new String();
    String symbol4 = new String();
    String symbol5 = new String();

    String indexNo = new String();
    SpriteBatch spriteBatch = new SpriteBatch();

    private ShapeRenderer renderer = new ShapeRenderer();//it’s probably better to pass this in rather than make a new shapeRenderer for each hex?

    public final float edgeSize;
    public final float altitudeSize;
    public float posX, posY;
    public boolean visible,highlight,select = false;
    public int selectedSymbol=-1;
    public boolean highlightSymbol = false;
    public int touchX = 0, touchY = 0, touchRadius = 10;
    ArrayList <Integer> removeSymbol= new ArrayList<Integer>(6);

    public HexTall(final float edgeSize, final float centreX, final float centreY, final int index, final GameStage gs, final Database db) {
        indexNo = "" + index;
        text3 = db.getHex(index)[0]+" "+db.getHex(index)[1]+" "+db.getHex(index)[2]+" "+db.getHex(index)[3]+" "+db.getHex(index)[4]+" "+db.getHex(index)[5]+" ";
        symbol0 = ""+db.getHex(index)[0];
        symbol1 = ""+db.getHex(index)[1];
        symbol2 = ""+db.getHex(index)[2];
        symbol3 = ""+db.getHex(index)[3];
        symbol4 = ""+db.getHex(index)[4];
        symbol5 = ""+db.getHex(index)[5];
        this.edgeSize = edgeSize;
        altitudeSize = edgeSize * 0.866025403784439f;
        this.posX = centreX;
        this.posY = centreY;
        visible = true;
font.getData().setScale(3f);
        for(int i =0;i<6;i++) {
            removeSymbol.add(1);

        }


        setBounds(centreX - altitudeSize, centreY - edgeSize, altitudeSize* 2, edgeSize* 2);//posX gives the centre so need  to offset that

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //x=Gdx.input.getX();
                //y=Gdx.input.getY();
                //worldCoordinates = orthographicCamera.unproject(new Vector3(x,y,0));
                //x=worldCoordinates.x;
                //y=worldCoordinates.y;
if(visible) {
    touchLogic(event, x, y);
    gs.setSelectedTall(index, selectedSector);
}
                //approx sector will indicate where the potential overlaps, need to check if the is a hex there then select the hex

            }
        });

    }
    void touchLogic(InputEvent event, float x, float y)
    {

        touchX =(int)x;
        touchY=(int)y;
        if(x >0&&x<altitudeSize*2&&y >0&&y<edgeSize*2)
        {
            text1 = " in square ";
            approxSector = getApproxSector(y, edgeSize, x, altitudeSize);
            if (pointInCircle(altitudeSize, edgeSize, altitudeSize, x, y)) {
                text2 = " in circle ";
                selectedSector = approxSector;
            } else {
                text2 = " not in circle ";
                if (approxSector == 2 || approxSector == 5) {
                    selectedSector = approxSector;
                    text2 = " in hex ";
                } else {//the origin for the touch is the bottom left of the bounding box
                    if (approxSector == 4) {//bottom left
                        if (y > -x / altitudeSize / 2*edgeSize + edgeSize /2) {
                            selectedSector = approxSector;
                            text1 = " corner case ";
                            text2 = " in hex ";
                        } else {//if the touch is outside the hex it must be inside the adjacent hex, plus 10 to the sector to indicate this
                            selectedSector = 14;
                            text2 = " not in hex ";
                        }
                    } else if (approxSector == 0) {//top left
                        if (y < x / altitudeSize / 2*edgeSize + edgeSize *3/2) {
                            selectedSector = approxSector;
                            text1 = " corner case ";
                            text2 = " in hex ";

                        } else {
                            selectedSector = 10;
                            text2 = " not in hex ";

                        }
                    } else if (approxSector == 1) {//top right
                        if (y < -x / altitudeSize / 2*edgeSize + edgeSize * 5/2) {//top right
                            selectedSector = approxSector;
                            text1 = " corner case ";
                            text2 = " in hex ";

                        } else {
                            selectedSector = 11;
                            text2 = " not in hex ";
                        }
                    } else if (approxSector == 3) {//bottom right
                        if (y > x / altitudeSize / 2*edgeSize - edgeSize /2) {
                            selectedSector = approxSector;
                            text1 = " corner case ";
                            text2 = " in hex ";

                        } else {
                            selectedSector = 13;
                            text2 = " not in hex ";
                        }
                    }
                }
            }
        } else
        {
            text1 = " not in square";
        }

        text =" approxsector:"+approxSector +" sector:"+selectedSector +" - x:"+(int)x +" - y:"+(int)y;
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
        text3 = "angle"+Math.round(angle/Math.PI*180)+" x "+x+ " y "+y;
//this will give the angle in radians, the angle is taken between the line from the centre of the hex to the touch point and the line from centre of the hex to the right (i think. This would mean touching to the right of the centre would be angle 0, above the centre would be angle 90 etc except it’s given in radians and rather that give 270 it will give -90

//because angles such as 270 are expressed as -90 if we do 180 - angle we will get all positive angles, (consequence of this is that the angles are now flipped 180 degrees so 0 would be to the left of centre, 90 would be below centre), translated into radians that’s pi-angle and we’ll get values from 0 to about <6.28 aka (2*PI),. because these are the tall hexes we should add PI/6 to the angle to rotate the angle a 12th of a circle clockwise  if we divide it by PI/3 aka (60 degrees) and rounddown we will get an integer from 0 to 5. We can return this simple integer to refer to which sector the angle is in, sector 0 will range from the 10 o’clock position to 121 o’clock, 1 will be from 12 to 2 o’clock, 2 will be 2 to 4 o’clock etc

//
        approxSector = (int)(Math.floor(((Math.PI-angle-Math.PI/6))/(Math.PI/3)));
        return approxSector;
    }

    public void highlight(int value)
    {
        if(visible) {
            red = value;

            highlight = true;
        }
    }
    public void unhighlight(int value)
    {
        red = value;
        highlight=false;
    }
    //method to highlight which symbol is selected
    public void highlightSymbol(int symbol)
    {
        redSymbol = 200;
        highlightSymbol=true;
        selectedSymbol=symbol;
    }
    public void unhighlightSymbol()
    {
        redSymbol = 0;
        highlightSymbol=false;
        selectedSymbol=-1;
    }
    public void select(int value)
    {
        blue = value;
        select=true;
    }
    public void unselect(int value)
    {

        blue=value;
        select=false;
    }

    public void draw(ShapeRenderer sr) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);

            sr.setColor(1*red,100,40*blue,1);
            drawTallHex(sr,posX,posY,edgeSize);
            drawTallSymbol(sr,posX,posY,edgeSize);
            sr.circle(posX,posY,altitudeSize);
            sr.setColor(Color.RED);
            sr.circle(touchX+posX-altitudeSize,touchY+posY-edgeSize,touchRadius);
            sr.circle(touchX+posX-altitudeSize,touchY+posY-edgeSize,2);
//sr.rect(posX-altitudeSize,posY-edgeSize,altitudeSize*2,edgeSize*2);
       //     sr.line(posX,posY-edgeSize,posX+altitudeSize,posY-edgeSize+altitudeSize/altitudeSize/2*edgeSize);

            //renderer.rect(posX-edgeSize,posY-altitudeSize,edgeSize*2,altitudeSize*2);
            //sr.end();

        }
    }

    public void drawSprites(SpriteBatch sb) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);


            //font.draw(sb, "a "+indexNo+" x "+(int)posX +" y "+ (int)posY+ " edge "+(int)edgeSize+text+text1+text2, posX, posY);
            //font.draw(sb,  text3, posX, posY+20);
            drawSymbols(sb,posX,posY,edgeSize*.7f,altitudeSize*.7f);




        }
    }

    public void hide()
    {
        visible=false;
    }
    void drawTallHex(ShapeRenderer sr, float originX, float originY, float edgeSize)
    {
        // draws a ‘tall’ hex hex with flat sides. originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size of the edges, altitudeSize is the height (or altitude) of an equilateral triangle with edge size defined by edgeSize, so the width we be defined as 2 *altitudeSize


        //draw 6 lines starting at the bottom right and going around anti clockwise

        sr.line(originX + altitudeSize, originY - (int)(edgeSize/2), originX , originY- edgeSize);
        sr.line(originX , originY- edgeSize , originX - altitudeSize , originY  - (int)(edgeSize/2));
        sr.line(originX - altitudeSize , originY  - (int)(edgeSize/2), originX - altitudeSize , originY  + (int)(edgeSize/2));
        sr.line(originX - altitudeSize , originY  + (int)(edgeSize/2), originX , originY+edgeSize);
        sr.line( originX , originY+edgeSize, originX +altitudeSize , originY +(int)(edgeSize/2));
        sr.line(originX +altitudeSize , originY +(int)(edgeSize/2),originX +altitudeSize , originY -(int)(edgeSize/2));

    }

    public void drawSymbols (SpriteBatch sb, float originX, float originY , float edgeSize, float altitudeSize)
    {
        if(removeSymbol.get(0)!=-1)
        {
            font.draw(sb, ""+symbol0,(float)(originX - 0.5*edgeSize), (float)(originY+altitudeSize));
        }
        if(removeSymbol.get(1)!=-1)
        {
            font.draw(sb, ""+symbol1, (float)(originX + 0.5*edgeSize), originY+altitudeSize);
        }
        if(removeSymbol.get(2)!=-1)
        {
            font.draw(sb, ""+symbol2, originX + edgeSize, originY);
        }
        if(removeSymbol.get(3)!=-1)
        {
            font.draw(sb, ""+symbol3,(float)(originX + 0.5*edgeSize), originY-altitudeSize);
        }
        if(removeSymbol.get(4)!=-1)
        {
            font.draw(sb, ""+symbol4, (float)(originX - 0.5*edgeSize), originY-altitudeSize);
        }
        if(removeSymbol.get(5)!=-1)
        {
            font.draw(sb, ""+symbol5, originX-edgeSize, originY);}
    }


    void drawTallSymbol(ShapeRenderer sr, float originX, float originY, float edgeSize)
    {
        //draws the highlighted symbol
        if(highlightSymbol)
        {
            sr.setColor(redSymbol,50,50,1);
            if(selectedSymbol==0)
            {
                sr.line(originX , originY , originX - altitudeSize, originY+edgeSize*0.5f);
                sr.line(originX  , originY , originX , originY+edgeSize);
            }
            else if(selectedSymbol==1)
            {
                sr.line(originX  , originY , originX , originY+edgeSize);
                sr.line(originX , originY , originX + altitudeSize, originY+edgeSize*0.5f);
            }
            else if(selectedSymbol==2)
            {
                sr.line(originX , originY , originX + altitudeSize, originY+edgeSize*0.5f);
                sr.line(originX , originY , originX + altitudeSize, originY-edgeSize*0.5f);
            }
            else if(selectedSymbol==3)
            {
                sr.line(originX , originY , originX + altitudeSize, originY-edgeSize*0.5f);
                sr.line(originX  , originY , originX , originY-edgeSize);
            }
            else if(selectedSymbol==4)
            {
                sr.line(originX  , originY , originX , originY-edgeSize);
                sr.line(originX  , originY , originX - altitudeSize, originY-edgeSize*0.5f);
            }
            else if(selectedSymbol==5)
            {
                sr.line(originX  , originY , originX - altitudeSize, originY-edgeSize*0.5f);
                sr.line(originX  , originY , originX - altitudeSize, originY+edgeSize*0.5f);
            }

        }//end if highlightSymbol

    }
    void highlightNonMatchingSymbols(ArrayList<Integer> removeSymbol)
    {
        // we pass in alist of symbols that dont match,
        for(int i =0; i< removeSymbol.size();i++)
        {
            this.removeSymbol.set(removeSymbol.get(i),-1);// for each symbol that doesnt match , set the corresponding index of our array to -1
        }
    }//end of method

    void unHighlightNonMatchingSymbols()
    {
        for(int i =0;i<removeSymbol.size();i++) {// this can have more than 6, so remove everything then add 6 back in
            removeSymbol.clear();

        }
        for(int i =0;i<6;i++) {
            removeSymbol.add(0,1);

        }
    }//end of method


}
