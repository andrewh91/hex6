package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

public class HexWide extends Actor {
    int selectedSector;
    int approxSector;
    int blue = 0,red =0,redSymbol=0;

    BitmapFont font = new BitmapFont();
    GlyphLayout glyphLayout = new GlyphLayout();

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

    public HexWide(final float edgeSize, final float centreX, final float centreY, final int index, final GameStage gs, final Database db) {
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
font.getData().setScale(2f);
        for(int i =0;i<6;i++) {
            removeSymbol.add(1);

        }
        setBounds(centreX - edgeSize, centreY - altitudeSize, edgeSize * 2, altitudeSize * 2);//posX gives the centre so need  to offset that

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
    gs.setSelected(index, selectedSector);
    //approx sector will indicate where the potential overlaps, need to check if the is a hex there then select the hex
}
            }
        });

    }
 void touchLogic(InputEvent event, float x, float y)
{

    touchX =(int)x;
    touchY=(int)y;
    if(x >0&&x<edgeSize *2&&y >0&&y<altitudeSize *2)
    {
        text1 = " in square ";
        approxSector = getApproxSector(y, altitudeSize, x, edgeSize);
        if (pointInCircle(edgeSize, altitudeSize, altitudeSize, x, y)) {
            text2 = " in circle ";
            selectedSector = approxSector;
        } else {
            text2 = " not in circle ";
            if (approxSector == 1 || approxSector == 4) {
                selectedSector = approxSector;
                text2 = " in hex ";
            } else {//the origin for the touch is the bottom left of the bounding box
                if (approxSector == 5) {
                    if (y > -x * altitudeSize * 2 / edgeSize + altitudeSize) {// translate up one altitude
                        selectedSector = approxSector;
                        text1 = " corner case ";
                        text2 = " in hex ";
                    } else {
                        selectedSector = 15;
                        text2 = " not in hex ";
                    }
                } else if (approxSector == 0) {
                    if (y < x * altitudeSize * 2 / edgeSize + altitudeSize) {
                        selectedSector = approxSector;
                        text1 = " corner case ";
                        text2 = " in hex ";

                    } else {
                        selectedSector = 10;
                        text2 = " not in hex ";

                    }
                } else if (approxSector == 2) {
                    if (y < -x * altitudeSize * 2 / edgeSize + altitudeSize * 5) {// need to translate to the right, but actually ee just translate up by altitude size times 5
                        selectedSector = approxSector;
                        text1 = " corner case ";
                        text2 = " in hex ";

                    } else {
                        selectedSector = 12;
                        text2 = " not in hex ";
                    }
                } else if (approxSector == 3) {
                    if (y > x * altitudeSize * 2 / edgeSize - altitudeSize * 3) {
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
//this will give the angle in radians, the angle is taken between the line from the centre of the hex to the touch point and the line from centre of the hex to the right (i think. This would mean touching to the right of the centre would be angle 0, above the centre would be angle 90 etc except it’s given in radians and rather that give 270 it will give -90

//because angles such as 270 are expressed as -90 if we do 180 - angle we will get all positive angles, (consequence of this is that the angles are now flipped 180 degrees so 0 would be to the left of centre, 90 would be below centre), translated into radians that’s pi-angle and we’ll get values from 0 to about <6.28 aka (2*PI), if we divide it by PI/3 aka (60 degrees) and rounddown we will get an integer from 0 to 5. We can return this simple integer to refer to which sector the angle is in, sector 0 will range from the 9 o’clock position to 11 o’clock, 1 will be from 11 to 1 o’clock, 2 will be 1 to 3 o’clock etc

//
        approxSector = (int)(Math.floor((Math.PI-angle)/(Math.PI/3)));
        return approxSector;
    }

    public void highlight(int value)
    {
        if(visible){
        red = value;
        highlight=true;
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

            drawWideSymbol(sr,posX,posY,edgeSize);
           //circle around hex interior
           // sr.circle(posX,posY,altitudeSize);
            //touch point
            //sr.setColor(Color.RED);
            //sr.circle(touchX+posX-edgeSize,touchY+posY-altitudeSize,touchRadius);




            //renderer.rect(posX-edgeSize,posY-altitudeSize,edgeSize*2,altitudeSize*2);
            //sr.end();

        }
    }

    public void drawFilled(ShapeRenderer sr, int symbolType)
    {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            sr.setColor(0.95f-0.5f*red,0.74f,0.25f-0.5f*blue,1);

            drawWideHex(sr,posX,posY,edgeSize);

if(symbolType==2) {
    drawSymbolsShapes(sr, posX, posY, edgeSize, altitudeSize);

}
        }
    }

    public void drawSprites(SpriteBatch sb,int symbolType) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);


            //font.draw(sb, "a "+indexNo+" x "+(int)posX +" y "+ (int)posY+ " edge "+(int)edgeSize+text+text1+text2, posX, posY);
          //  font.draw(sb,  text3, posX, posY+20);
            if(symbolType==1) {
                drawSymbols(sb, posX, posY, edgeSize * .7f, altitudeSize * .7f);
            }




        }
    }
    public void hide()
    {
        visible=false;
    }

    void drawWideHex(ShapeRenderer sr, float centreX, float centreY, float edgeSize)
    {
        // draws a ‘wide’ hex hex with flat top and bottom. originX and originY are the coordinates passed in which will determine the centre of the hex, edge size will determine the size, altitudeSize is the height (or altitude) of an equilateral triangle with edge size defined by edgeSize


        //draw 6 lines starting at the bottom right and going around anti clockwise
/*
        sr.line(originX + (edgeSize / 2), originY - altitudeSize, originX + edgeSize, originY);
        sr.line(originX + edgeSize, originY , originX + (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX + (edgeSize / 2) , (int)(originY + altitudeSize), originX - (edgeSize / 2) , (int)(originY + altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY + altitudeSize), originX - edgeSize , originY);
        sr.line(originX - edgeSize , originY , originX - (edgeSize / 2) , (int)(originY - altitudeSize));
        sr.line(originX - (edgeSize / 2) , (int)(originY - altitudeSize),originX + (edgeSize / 2) , (int)(originY - altitudeSize));
*/
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
    public void drawSymbols (SpriteBatch sb, float originX, float originY , float edgeSize, float altitudeSize)
    {
        float r=altitudeSize/3;

        if(removeSymbol.get(0)!=-1)
        {
            glyphLayout.setText(font,symbol0);
            font.draw(sb, ""+symbol0, originX - edgeSize/2  -glyphLayout.width/2, (float)(originY+r+glyphLayout.height/2));
        }
        if(removeSymbol.get(1)!=-1)
        {
            glyphLayout.setText(font,symbol1);
            font.draw(sb, ""+symbol1, originX -glyphLayout.width/2,  (float)(originY+r*2+glyphLayout.height/2));
        }
        if(removeSymbol.get(2)!=-1)
        {
            glyphLayout.setText(font,symbol2);
            font.draw(sb, ""+symbol2, originX + edgeSize/2 -glyphLayout.width/2, (float)(originY+r+glyphLayout.height/2));
        }
        if(removeSymbol.get(3)!=-1)
        {
            glyphLayout.setText(font,symbol3);
            font.draw(sb, ""+symbol3, originX + edgeSize/2 -glyphLayout.width/2, (float)(originY-r+glyphLayout.height/2));
        }
        if(removeSymbol.get(4)!=-1)
        {
            glyphLayout.setText(font,symbol4);
            font.draw(sb, ""+symbol4, originX -glyphLayout.width/2, (float)(originY-r*2+glyphLayout.height/2));
        }
        if(removeSymbol.get(5)!=-1)
        {
            glyphLayout.setText(font,symbol5);
            font.draw(sb, ""+symbol5, originX-+ edgeSize/2 -glyphLayout.width/2, (float)(originY-r+glyphLayout.height/2));
        }
    }






    public void drawSymbolsShapes (ShapeRenderer shapeRenderer, float originX, float originY ,
                                   float edgeSize, float altitudeSize)
    {

        float r=altitudeSize/3;
        if(removeSymbol.get(0)!=-1)
        {
            drawShape(shapeRenderer,originX - edgeSize/2, (float)(originY+r),edgeSize,altitudeSize,
                    symbol0);
        }
        if(removeSymbol.get(1)!=-1)
        {
            drawShape(shapeRenderer,originX,  (float)(originY+r*2),edgeSize,
                    altitudeSize,symbol1);
        }
        if(removeSymbol.get(2)!=-1)
        {
            drawShape(shapeRenderer,originX + edgeSize/2, (float)(originY+r),edgeSize,
                    altitudeSize,symbol2);
        }
        if(removeSymbol.get(3)!=-1)
        {
            drawShape(shapeRenderer,originX + edgeSize/2, (float)(originY-r),edgeSize,
                    altitudeSize,symbol3);
        }
        if(removeSymbol.get(4)!=-1)
        {
            drawShape(shapeRenderer,originX, (float)(originY-r*2),edgeSize,altitudeSize,
            symbol4);
        }
        if(removeSymbol.get(5)!=-1)
        {

            drawShape(shapeRenderer,originX-+ edgeSize/2, (float)(originY-r),edgeSize,
                    altitudeSize ,symbol5);
        }
    }
public void drawShape(ShapeRenderer shapeRenderer, float originX, float originY,
                      float edgeSize, float altitudeSize, String symbol)
{
    float r=altitudeSize/3;
    int sym = Integer.parseInt(symbol);
    int sym1 = (int)(sym/7);

    int sym2 = sym%7;


    if(sym2==0)
    {
        shapeRenderer.setColor(Color.RED);
    }
    else if(sym2==1)
    {
        shapeRenderer.setColor(Color.ORANGE);
    }
    else if(sym2==2)
    {
        shapeRenderer.setColor(Color.YELLOW);
    }
    else if(sym2==3)
    {
        shapeRenderer.setColor(Color.GREEN);
    }
    else if(sym2==4)
    {
        shapeRenderer.setColor(Color.BLUE);
    }
    else if(sym2==5)
    {
        shapeRenderer.setColor(Color.VIOLET);
    }
    else if(sym2==6)
    {
        shapeRenderer.setColor(Color.WHITE);
    }

    if(sym1==0)
    {
        drawTriangleSymbol(shapeRenderer,originX,originY,r);
    }
    else if (sym1==1)
    {
        shapeRenderer.circle(originX, originY, r);
    }
    else if (sym1==2)
    {
        drawRectangleSymbol(shapeRenderer,originX,originY,r);
    }
    else if (sym1==3)
    {
        drawStarSymbol(shapeRenderer,originX,originY,r);
    }
    else if (sym1==4)
    {
        drawHexSymbol(shapeRenderer,originX,originY,r);
    }

}

    void drawTriangleSymbol(ShapeRenderer shapeRenderer,float ox,float oy, float r)
    {
        float es= 0.707106781186547f*r;
        float as = 0.866025403784439f*es;
        shapeRenderer.triangle(ox-es, oy-as, ox+es,oy-as,ox,oy+r);
    }


    void drawRectangleSymbol(ShapeRenderer shapeRenderer,float ox,float oy, float r)
    {
        float es = 0.707106781186547f*r;
        shapeRenderer.triangle(ox-es, oy - es, ox-es, oy+ es, ox+es, oy+ es);
        shapeRenderer.triangle(ox-es, oy- es, ox+es, oy- es, ox+es, oy+ es);
    }

    void drawStarSymbol(ShapeRenderer shapeRenderer,float ox,float oy, float r)
    {
 float innerRadius=-0.381966011f;
        float ax=0,	ay=r;
        float  bx=0.951056516f*r,	by=0.309016994f*r;
        float  cx=0.587785252f*r,	cy=-0.809016994f*r;
        float  dx=-0.587785252f*r, dy=-0.809016994f*r;
        float  ex=-0.951056516f*r,	ey=0.309016994f*r;


        shapeRenderer.triangle(ox+ax, oy+ay, ox+dx, oy+dy, ox+ex*innerRadius, oy+ey*innerRadius);
        shapeRenderer.triangle(ox+bx, oy+by, ox+ex, oy+ey, ox+ax*innerRadius, oy+ay*innerRadius);
        shapeRenderer.triangle(ox+ax, oy+ay, ox+cx, oy+cy, ox+bx*innerRadius, oy+by*innerRadius);


    }

    void drawHexSymbol(ShapeRenderer shapeRenderer,float ox,float oy, float r)
    {
        float as = 0.866025403784439f * r;

        shapeRenderer.triangle(ox, oy+r, ox+as, oy+r/2,  ox+as, oy-r/2);
        shapeRenderer.triangle(ox, oy+r, ox+as, oy-r/2, ox,oy-r);
        shapeRenderer.triangle(ox, oy+r, ox,oy-r,ox-as,oy-r/2);
        shapeRenderer.triangle(ox, oy+r, ox-as,oy-r/2, ox-as,oy+r/2);
    }

    void drawWideSymbol(ShapeRenderer sr, float originX, float originY, float edgeSize)
    {
        //draws the highlighted symbol
        if(highlightSymbol)
        {
            sr.setColor(redSymbol,50,50,1);
            if(selectedSymbol==0)
            {
                sr.line(originX , originY , originX - edgeSize, originY);
                sr.line(originX  , originY , originX - edgeSize*0.5f, originY+altitudeSize);
            }
            else if(selectedSymbol==1)
            {
                sr.line(originX  , originY , originX - edgeSize*0.5f, originY+altitudeSize);
                sr.line(originX  , originY , originX + edgeSize*0.5f, originY+altitudeSize);
            }
            else if(selectedSymbol==2)
            {
                sr.line(originX  , originY , originX + edgeSize*0.5f, originY+altitudeSize);
                sr.line(originX , originY , originX +edgeSize, originY);
            }
            else if(selectedSymbol==3)
            {
                sr.line(originX , originY , originX +edgeSize, originY);
                sr.line(originX  , originY , originX + edgeSize*0.5f, originY-altitudeSize);
            }
            else if(selectedSymbol==4)
            {
                sr.line(originX  , originY , originX + edgeSize*0.5f, originY-altitudeSize);
                sr.line(originX  , originY , originX - edgeSize*0.5f, originY-altitudeSize);
            }
            else if(selectedSymbol==5)
            {
                sr.line(originX  , originY , originX - edgeSize*0.5f, originY-altitudeSize);
                sr.line(originX , originY , originX - edgeSize, originY);
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