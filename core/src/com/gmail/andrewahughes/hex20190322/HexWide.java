package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 26/09/2018.
 */

public class HexWide extends Actor {
    int selectedSector;
    int approxSector;

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

   public int scorePenaltyMultiplier =0;

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
if(true) {//this used to be if visiblr, but i still want to process the touch if its invisible, cos the camer ashould zoom out if touching an invisible hex
    touchLogic(event, x, y);
    gs.setSelected(index, selectedSector,visible);
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
        highlight=true;
    }
    }

    public void unhighlight(int value)
    {
        highlight=false;
    }
    //method to highlight which symbol is selected
    public void highlightSymbol(int symbol)
    {
        highlightSymbol=true;
        selectedSymbol=symbol;
    }
    public void unhighlightSymbol()
    {
        highlightSymbol=false;
        selectedSymbol=-1;
    }
    public void select(int value)
    {
        select=true;
    }
    public void unselect(int value)
    {

        select=false;
    }

    public void draw(ShapeRenderer sr) {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {
            //sr.begin(ShapeRenderer.ShapeType.Line);

            //drawWideSymbol(sr,posX,posY,edgeSize);
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
            sr.setColor(1.00f,0.75f,0.06f, 1);

            if(select)
            {
                sr.setColor(0.98f,0.84f,0.42f, 1);

            }
            if(highlight)
            {
                sr.setColor(0.94f,0.67f,0.09f, 1);

            }


            drawWideHex(sr,posX,posY,edgeSize*0.95f);
            drawWideSymbol(sr,posX,posY,edgeSize);

if(symbolType==2) {
    drawSymbolsShapes(sr, posX, posY, edgeSize, altitudeSize);

}
else if (symbolType==3)
{
    drawSymbolsFLowers(sr,posX,posY,edgeSize,altitudeSize);
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

float altitudeSize = edgeSize*0.866025403784439f;
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
        //draw the number symbols
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
//draw the primitive symbol shapes
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
    public void drawSymbolsFLowers (ShapeRenderer shapeRenderer, float originX, float originY ,
                                   float edgeSize, float altitudeSize)
    {
//draw the primitive symbol shapes
        float r=altitudeSize/3;
        if(removeSymbol.get(0)!=-1)
        {
            drawFlowers(shapeRenderer,originX - edgeSize/2, (float)(originY+r),edgeSize,altitudeSize,
                    symbol0);
        }
        if(removeSymbol.get(1)!=-1)
        {
            drawFlowers(shapeRenderer,originX,  (float)(originY+r*2),edgeSize,
                    altitudeSize,symbol1);
        }
        if(removeSymbol.get(2)!=-1)
        {
            drawFlowers(shapeRenderer,originX + edgeSize/2, (float)(originY+r),edgeSize,
                    altitudeSize,symbol2);
        }
        if(removeSymbol.get(3)!=-1)
        {
            drawFlowers(shapeRenderer,originX + edgeSize/2, (float)(originY-r),edgeSize,
                    altitudeSize,symbol3);
        }
        if(removeSymbol.get(4)!=-1)
        {
            drawFlowers(shapeRenderer,originX, (float)(originY-r*2),edgeSize,altitudeSize,
                    symbol4);
        }
        if(removeSymbol.get(5)!=-1)
        {

            drawFlowers(shapeRenderer,originX-+ edgeSize/2, (float)(originY-r),edgeSize,
                    altitudeSize ,symbol5);
        }
    }

    public void drawFlowers(ShapeRenderer shapeRenderer, float originX, float originY,
                          float edgeSize, float altitudeSize, String symbol)
    {
        float r=altitudeSize/3;
        int sym = Integer.parseInt(symbol);
        int sym1 = (int)(sym/9);

        int sym2 = sym%7;
        Color primaryColour = Color.RED;
        Color secondaryColour = Color.RED;
        Color tertiaryColour = Color.RED;


        if(sym2==0)
        {
            primaryColour = Color.RED;
            secondaryColour = Color.YELLOW;
            tertiaryColour = Color.ORANGE;
        }
        else if(sym2==1)
        {
            primaryColour = Color.BLUE;
            secondaryColour = Color.YELLOW;
            tertiaryColour = Color.PURPLE;
        }
        else if(sym2==2)
        {
            primaryColour = Color.PURPLE;
            secondaryColour = Color.ORANGE;
            tertiaryColour = Color.CORAL;
        }
        else if(sym2==3)
        {
            primaryColour = Color.ORANGE;
            secondaryColour = Color.YELLOW;
            tertiaryColour = Color.RED;
        }
        else if(sym2==4)
        {
            primaryColour = Color.GREEN;
            secondaryColour = Color.RED;
            tertiaryColour = Color.TEAL;
        }
        else if(sym2==5)
        {
            primaryColour = Color.SALMON;
            secondaryColour = Color.CHARTREUSE;
            tertiaryColour = Color.VIOLET;
        }
        else if(sym2==6)
        {
            primaryColour = Color.WHITE;
            secondaryColour = Color.CORAL;
            tertiaryColour = Color.YELLOW;
        }

            drawFlower(shapeRenderer,sym1,originX,originY,0.15f,primaryColour,secondaryColour,tertiaryColour);
    }
    void drawFlower(ShapeRenderer renderer,int index, float  ox, float oy, float scale, Color primaryColour, Color secondaryColour, Color tertiaryColour)
    {

ox=ox-edgeSize/4;
oy=oy-altitudeSize/4;
scale = edgeSize /1000;
        renderer.setColor(secondaryColour); renderer.triangle(ox,oy,ox+scale*320,oy+scale*180,ox+scale*362,oy+scale*198);
        if(index==0)
        {
// dogwood 4
            renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*180,ox+scale*362,oy+scale*198); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*362,oy+scale*198,ox+scale*380,oy+scale*240); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*260,oy+scale*240,ox+scale*278,oy+scale*198); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*278,oy+scale*198,ox+scale*320,oy+scale*180); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*300,ox+scale*278,oy+scale*282); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*278,oy+scale*282,ox+scale*260,oy+scale*240); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*380,oy+scale*240,ox+scale*362,oy+scale*282); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*362,oy+scale*282,ox+scale*320,oy+scale*300); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*339,oy+scale*173,ox+scale*344,oy+scale*146); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*344,oy+scale*146,ox+scale*360,oy+scale*125); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*360,oy+scale*125,ox+scale*383,oy+scale*112); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*383,oy+scale*112,ox+scale*409,oy+scale*107); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*409,oy+scale*107,ox+scale*438,oy+scale*109); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*438,oy+scale*109,ox+scale*453,oy+scale*107); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*453,oy+scale*107,ox+scale*452,oy+scale*122); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*452,oy+scale*122,ox+scale*453,oy+scale*151); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*453,oy+scale*151,ox+scale*448,oy+scale*177); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*448,oy+scale*177,ox+scale*434,oy+scale*200); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*434,oy+scale*200,ox+scale*415,oy+scale*217); renderer.triangle(ox+scale*340,oy+scale*221,ox+scale*415,oy+scale*217,ox+scale*387,oy+scale*221); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*253,oy+scale*221,ox+scale*226,oy+scale*216); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*226,oy+scale*216,ox+scale*205,oy+scale*200); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*205,oy+scale*200,ox+scale*192,oy+scale*177); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*192,oy+scale*177,ox+scale*187,oy+scale*151); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*187,oy+scale*151,ox+scale*189,oy+scale*122); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*189,oy+scale*122,ox+scale*187,oy+scale*107); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*187,oy+scale*107,ox+scale*202,oy+scale*108); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*202,oy+scale*108,ox+scale*231,oy+scale*107); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*231,oy+scale*107,ox+scale*257,oy+scale*112); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*257,oy+scale*112,ox+scale*280,oy+scale*126); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*280,oy+scale*126,ox+scale*297,oy+scale*145); renderer.triangle(ox+scale*301,oy+scale*220,ox+scale*297,oy+scale*145,ox+scale*301,oy+scale*173); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*301,oy+scale*307,ox+scale*296,oy+scale*334); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*296,oy+scale*334,ox+scale*280,oy+scale*355); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*280,oy+scale*355,ox+scale*257,oy+scale*368); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*257,oy+scale*368,ox+scale*231,oy+scale*373); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*231,oy+scale*373,ox+scale*202,oy+scale*371); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*202,oy+scale*371,ox+scale*187,oy+scale*373); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*187,oy+scale*373,ox+scale*188,oy+scale*358); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*188,oy+scale*358,ox+scale*187,oy+scale*329); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*187,oy+scale*329,ox+scale*192,oy+scale*303); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*192,oy+scale*303,ox+scale*206,oy+scale*280); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*206,oy+scale*280,ox+scale*225,oy+scale*263); renderer.triangle(ox+scale*300,oy+scale*259,ox+scale*225,oy+scale*263,ox+scale*253,oy+scale*259); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*387,oy+scale*259,ox+scale*414,oy+scale*264); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*414,oy+scale*264,ox+scale*435,oy+scale*280); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*435,oy+scale*280,ox+scale*448,oy+scale*303); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*448,oy+scale*303,ox+scale*453,oy+scale*329); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*453,oy+scale*329,ox+scale*451,oy+scale*358); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*451,oy+scale*358,ox+scale*453,oy+scale*373); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*453,oy+scale*373,ox+scale*438,oy+scale*372); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*438,oy+scale*372,ox+scale*409,oy+scale*373); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*409,oy+scale*373,ox+scale*383,oy+scale*368); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*383,oy+scale*368,ox+scale*360,oy+scale*354); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*360,oy+scale*354,ox+scale*343,oy+scale*335); renderer.triangle(ox+scale*339,oy+scale*260,ox+scale*343,oy+scale*335,ox+scale*339,oy+scale*307); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*366,oy+scale*194,ox+scale*371,oy+scale*171,ox+scale*421,oy+scale*139); renderer.triangle(ox+scale*366,oy+scale*194,ox+scale*421,oy+scale*139,ox+scale*390,oy+scale*189); renderer.triangle(ox+scale*274,oy+scale*194,ox+scale*251,oy+scale*189,ox+scale*219,oy+scale*139); renderer.triangle(ox+scale*274,oy+scale*194,ox+scale*219,oy+scale*139,ox+scale*269,oy+scale*170); renderer.triangle(ox+scale*274,oy+scale*286,ox+scale*269,oy+scale*309,ox+scale*219,oy+scale*341); renderer.triangle(ox+scale*274,oy+scale*286,ox+scale*219,oy+scale*341,ox+scale*250,oy+scale*291); renderer.triangle(ox+scale*366,oy+scale*286,ox+scale*389,oy+scale*291,ox+scale*421,oy+scale*341); renderer.triangle(ox+scale*366,oy+scale*286,ox+scale*421,oy+scale*341,ox+scale*371,oy+scale*310);
        }
        else if (index==1)
        {
            //vinca 5
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*323,oy+scale*186,ox+scale*326,oy+scale*62,ox+scale*415,oy+scale*110); renderer.triangle(ox+scale*323,oy+scale*186,ox+scale*415,oy+scale*110,ox+scale*486,oy+scale*179); renderer.triangle(ox+scale*323,oy+scale*186,ox+scale*486,oy+scale*179,ox+scale*371,oy+scale*220); renderer.triangle(ox+scale*270,oy+scale*220,ox+scale*153,oy+scale*179,ox+scale*225,oy+scale*110); renderer.triangle(ox+scale*270,oy+scale*220,ox+scale*225,oy+scale*110,ox+scale*314,oy+scale*63); renderer.triangle(ox+scale*270,oy+scale*220,ox+scale*314,oy+scale*63,ox+scale*317,oy+scale*185); renderer.triangle(ox+scale*286,oy+scale*282,ox+scale*210,oy+scale*380,ox+scale*167,oy+scale*290); renderer.triangle(ox+scale*286,oy+scale*282,ox+scale*167,oy+scale*290,ox+scale*150,oy+scale*191); renderer.triangle(ox+scale*286,oy+scale*282,ox+scale*150,oy+scale*191,ox+scale*267,oy+scale*226); renderer.triangle(ox+scale*349,oy+scale*286,ox+scale*419,oy+scale*388,ox+scale*320,oy+scale*401); renderer.triangle(ox+scale*349,oy+scale*286,ox+scale*320,oy+scale*401,ox+scale*221,oy+scale*387); renderer.triangle(ox+scale*349,oy+scale*286,ox+scale*221,oy+scale*387,ox+scale*290,oy+scale*286); renderer.triangle(ox+scale*372,oy+scale*226,ox+scale*491,oy+scale*191,ox+scale*473,oy+scale*290); renderer.triangle(ox+scale*372,oy+scale*226,ox+scale*473,oy+scale*290,ox+scale*429,oy+scale*379); renderer.triangle(ox+scale*372,oy+scale*226,ox+scale*429,oy+scale*379,ox+scale*355,oy+scale*283); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*180,ox+scale*339,oy+scale*185); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*339,oy+scale*185,ox+scale*356,oy+scale*192); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*356,oy+scale*192,ox+scale*366,oy+scale*205); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*366,oy+scale*205,ox+scale*377,oy+scale*221); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*263,oy+scale*221,ox+scale*274,oy+scale*205); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*274,oy+scale*205,ox+scale*285,oy+scale*191); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*285,oy+scale*191,ox+scale*301,oy+scale*185); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*301,oy+scale*185,ox+scale*320,oy+scale*180); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*285,oy+scale*289,ox+scale*273,oy+scale*273); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*273,oy+scale*273,ox+scale*263,oy+scale*258); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*263,oy+scale*258,ox+scale*262,oy+scale*242); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*262,oy+scale*242,ox+scale*263,oy+scale*221); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*355,oy+scale*289,ox+scale*337,oy+scale*295); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*337,oy+scale*295,ox+scale*319,oy+scale*300); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*319,oy+scale*300,ox+scale*303,oy+scale*296); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*303,oy+scale*296,ox+scale*285,oy+scale*289); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*221,ox+scale*378,oy+scale*241); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*378,oy+scale*241,ox+scale*377,oy+scale*259); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*259,ox+scale*368,oy+scale*273); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*368,oy+scale*273,ox+scale*355,oy+scale*289); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*339,oy+scale*185,ox+scale*381,oy+scale*156,ox+scale*366,oy+scale*206); renderer.triangle(ox+scale*274,oy+scale*205,ox+scale*259,oy+scale*156,ox+scale*302,oy+scale*185); renderer.triangle(ox+scale*272,oy+scale*273,ox+scale*222,oy+scale*272,ox+scale*263,oy+scale*240); renderer.triangle(ox+scale*337,oy+scale*296,ox+scale*320,oy+scale*343,ox+scale*302,oy+scale*295); renderer.triangle(ox+scale*378,oy+scale*242,ox+scale*418,oy+scale*272,ox+scale*366,oy+scale*274);
        }

        else if (index==2)
        {
            //sunflower 10
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*120,ox+scale*324,oy+scale*85); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*324,oy+scale*85,ox+scale*333,oy+scale*70); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*333,oy+scale*70,ox+scale*345,oy+scale*65); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*345,oy+scale*65,ox+scale*363,oy+scale*65); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*363,oy+scale*65,ox+scale*376,oy+scale*67); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*376,oy+scale*67,ox+scale*388,oy+scale*72); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*388,oy+scale*72,ox+scale*402,oy+scale*83); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*402,oy+scale*83,ox+scale*409,oy+scale*95); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*409,oy+scale*95,ox+scale*407,oy+scale*113); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*407,oy+scale*113,ox+scale*390,oy+scale*144); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*249,oy+scale*143,ox+scale*232,oy+scale*112); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*232,oy+scale*112,ox+scale*230,oy+scale*95); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*230,oy+scale*95,ox+scale*237,oy+scale*84); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*237,oy+scale*84,ox+scale*252,oy+scale*73); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*252,oy+scale*73,ox+scale*263,oy+scale*68); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*263,oy+scale*68,ox+scale*276,oy+scale*65); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*276,oy+scale*65,ox+scale*294,oy+scale*64); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*294,oy+scale*64,ox+scale*307,oy+scale*71); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*307,oy+scale*71,ox+scale*316,oy+scale*86); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*316,oy+scale*86,ox+scale*321,oy+scale*121); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*206,oy+scale*203,ox+scale*174,oy+scale*188); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*174,oy+scale*188,ox+scale*162,oy+scale*176); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*162,oy+scale*176,ox+scale*161,oy+scale*162); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*161,oy+scale*162,ox+scale*166,oy+scale*145); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*166,oy+scale*145,ox+scale*173,oy+scale*134); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*173,oy+scale*134,ox+scale*182,oy+scale*124); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*182,oy+scale*124,ox+scale*196,oy+scale*113); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*196,oy+scale*113,ox+scale*210,oy+scale*111); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*210,oy+scale*111,ox+scale*226,oy+scale*118); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*226,oy+scale*118,ox+scale*251,oy+scale*143); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*206,oy+scale*277,ox+scale*171,oy+scale*284); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*171,oy+scale*284,ox+scale*155,oy+scale*280); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*155,oy+scale*280,ox+scale*146,oy+scale*270); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*146,oy+scale*270,ox+scale*140,oy+scale*253); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*140,oy+scale*253,ox+scale*139,oy+scale*241); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*139,oy+scale*241,ox+scale*140,oy+scale*227); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*140,oy+scale*227,ox+scale*145,oy+scale*210); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*145,oy+scale*210,ox+scale*155,oy+scale*200); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*155,oy+scale*200,ox+scale*172,oy+scale*197); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*172,oy+scale*197,ox+scale*207,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*249,oy+scale*337,ox+scale*226,oy+scale*363); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*226,oy+scale*363,ox+scale*210,oy+scale*370); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*210,oy+scale*370,ox+scale*197,oy+scale*367); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*197,oy+scale*367,ox+scale*182,oy+scale*357); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*182,oy+scale*357,ox+scale*174,oy+scale*347); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*174,oy+scale*347,ox+scale*167,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*167,oy+scale*336,ox+scale*161,oy+scale*319); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*161,oy+scale*319,ox+scale*163,oy+scale*305); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*163,oy+scale*305,ox+scale*175,oy+scale*292); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*175,oy+scale*292,ox+scale*207,oy+scale*276); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*360,ox+scale*316,oy+scale*395); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*316,oy+scale*395,ox+scale*307,oy+scale*410); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*307,oy+scale*410,ox+scale*295,oy+scale*415); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*295,oy+scale*415,ox+scale*277,oy+scale*415); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*277,oy+scale*415,ox+scale*264,oy+scale*413); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*264,oy+scale*413,ox+scale*252,oy+scale*408); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*252,oy+scale*408,ox+scale*238,oy+scale*397); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*238,oy+scale*397,ox+scale*231,oy+scale*385); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*231,oy+scale*385,ox+scale*233,oy+scale*367); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*233,oy+scale*367,ox+scale*250,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*391,oy+scale*337,ox+scale*408,oy+scale*368); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*408,oy+scale*368,ox+scale*410,oy+scale*385); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*410,oy+scale*385,ox+scale*403,oy+scale*396); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*403,oy+scale*396,ox+scale*388,oy+scale*407); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*388,oy+scale*407,ox+scale*377,oy+scale*412); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*412,ox+scale*364,oy+scale*415); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*364,oy+scale*415,ox+scale*346,oy+scale*416); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*346,oy+scale*416,ox+scale*333,oy+scale*409); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*333,oy+scale*409,ox+scale*324,oy+scale*394); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*324,oy+scale*394,ox+scale*319,oy+scale*359); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*434,oy+scale*277,ox+scale*466,oy+scale*292); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*466,oy+scale*292,ox+scale*478,oy+scale*304); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*478,oy+scale*304,ox+scale*479,oy+scale*318); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*479,oy+scale*318,ox+scale*474,oy+scale*335); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*474,oy+scale*335,ox+scale*467,oy+scale*346); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*467,oy+scale*346,ox+scale*458,oy+scale*356); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*458,oy+scale*356,ox+scale*444,oy+scale*367); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*444,oy+scale*367,ox+scale*430,oy+scale*369); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*430,oy+scale*369,ox+scale*414,oy+scale*362); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*414,oy+scale*362,ox+scale*389,oy+scale*337); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*434,oy+scale*203,ox+scale*469,oy+scale*196); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*469,oy+scale*196,ox+scale*485,oy+scale*200); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*485,oy+scale*200,ox+scale*494,oy+scale*210); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*494,oy+scale*210,ox+scale*500,oy+scale*227); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*500,oy+scale*227,ox+scale*501,oy+scale*239); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*501,oy+scale*239,ox+scale*500,oy+scale*253); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*500,oy+scale*253,ox+scale*495,oy+scale*270); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*495,oy+scale*270,ox+scale*485,oy+scale*280); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*485,oy+scale*280,ox+scale*468,oy+scale*283); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*468,oy+scale*283,ox+scale*433,oy+scale*277); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*391,oy+scale*143,ox+scale*414,oy+scale*117); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*414,oy+scale*117,ox+scale*430,oy+scale*110); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*430,oy+scale*110,ox+scale*443,oy+scale*113); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*443,oy+scale*113,ox+scale*458,oy+scale*123); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*458,oy+scale*123,ox+scale*466,oy+scale*133); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*466,oy+scale*133,ox+scale*473,oy+scale*144); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*473,oy+scale*144,ox+scale*479,oy+scale*161); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*479,oy+scale*161,ox+scale*477,oy+scale*175); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*477,oy+scale*175,ox+scale*465,oy+scale*188); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*465,oy+scale*188,ox+scale*433,oy+scale*204); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*180,ox+scale*355,oy+scale*191); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*285,oy+scale*191,ox+scale*320,oy+scale*180); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*263,oy+scale*221,ox+scale*285,oy+scale*191); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*263,oy+scale*259,ox+scale*263,oy+scale*221); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*285,oy+scale*289,ox+scale*263,oy+scale*259); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*300,ox+scale*285,oy+scale*289); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*355,oy+scale*289,ox+scale*320,oy+scale*300); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*259,ox+scale*355,oy+scale*289); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*221,ox+scale*377,oy+scale*259); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*355,oy+scale*191,ox+scale*377,oy+scale*221); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*120,ox+scale*337,oy+scale*122); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*337,oy+scale*122,ox+scale*348,oy+scale*125); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*348,oy+scale*125,ox+scale*357,oy+scale*126); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*357,oy+scale*126,ox+scale*365,oy+scale*129); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*365,oy+scale*129,ox+scale*376,oy+scale*134); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*376,oy+scale*134,ox+scale*390,oy+scale*144); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*249,oy+scale*143,ox+scale*264,oy+scale*135); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*264,oy+scale*135,ox+scale*275,oy+scale*130); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*275,oy+scale*130,ox+scale*283,oy+scale*126); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*283,oy+scale*126,ox+scale*291,oy+scale*123); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*291,oy+scale*123,ox+scale*303,oy+scale*122); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*303,oy+scale*122,ox+scale*320,oy+scale*121); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*206,oy+scale*203,ox+scale*213,oy+scale*188); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*213,oy+scale*188,ox+scale*219,oy+scale*178); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*219,oy+scale*178,ox+scale*223,oy+scale*169); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*223,oy+scale*169,ox+scale*228,oy+scale*162); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*228,oy+scale*162,ox+scale*237,oy+scale*154); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*237,oy+scale*154,ox+scale*250,oy+scale*144); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*206,oy+scale*277,ox+scale*203,oy+scale*261); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*203,oy+scale*261,ox+scale*202,oy+scale*249); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*202,oy+scale*249,ox+scale*200,oy+scale*240); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*200,oy+scale*240,ox+scale*200,oy+scale*231); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*200,oy+scale*231,ox+scale*202,oy+scale*220); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*202,oy+scale*220,ox+scale*207,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*249,oy+scale*337,ox+scale*237,oy+scale*326); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*237,oy+scale*326,ox+scale*229,oy+scale*317); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*229,oy+scale*317,ox+scale*223,oy+scale*311); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*223,oy+scale*311,ox+scale*218,oy+scale*303); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*218,oy+scale*303,ox+scale*213,oy+scale*293); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*213,oy+scale*293,ox+scale*207,oy+scale*277); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*360,ox+scale*303,oy+scale*358); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*303,oy+scale*358,ox+scale*292,oy+scale*355); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*292,oy+scale*355,ox+scale*283,oy+scale*354); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*283,oy+scale*354,ox+scale*275,oy+scale*351); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*275,oy+scale*351,ox+scale*264,oy+scale*346); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*264,oy+scale*346,ox+scale*250,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*391,oy+scale*337,ox+scale*376,oy+scale*345); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*376,oy+scale*345,ox+scale*365,oy+scale*350); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*365,oy+scale*350,ox+scale*357,oy+scale*354); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*357,oy+scale*354,ox+scale*349,oy+scale*357); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*349,oy+scale*357,ox+scale*337,oy+scale*358); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*337,oy+scale*358,ox+scale*320,oy+scale*359); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*434,oy+scale*277,ox+scale*427,oy+scale*292); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*427,oy+scale*292,ox+scale*421,oy+scale*302); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*421,oy+scale*302,ox+scale*417,oy+scale*311); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*417,oy+scale*311,ox+scale*412,oy+scale*318); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*412,oy+scale*318,ox+scale*403,oy+scale*326); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*403,oy+scale*326,ox+scale*390,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*434,oy+scale*203,ox+scale*437,oy+scale*219); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*437,oy+scale*219,ox+scale*438,oy+scale*231); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*438,oy+scale*231,ox+scale*440,oy+scale*240); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*440,oy+scale*240,ox+scale*440,oy+scale*249); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*440,oy+scale*249,ox+scale*438,oy+scale*260); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*438,oy+scale*260,ox+scale*433,oy+scale*277); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*391,oy+scale*143,ox+scale*403,oy+scale*154); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*403,oy+scale*154,ox+scale*411,oy+scale*163); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*411,oy+scale*163,ox+scale*417,oy+scale*169); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*417,oy+scale*169,ox+scale*422,oy+scale*177); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*422,oy+scale*177,ox+scale*427,oy+scale*187); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*427,oy+scale*187,ox+scale*433,oy+scale*203);
        }

        else if (index==3)
        {
            //hibiscus 5
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*301,oy+scale*183,ox+scale*321,oy+scale*122); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*122,ox+scale*371,oy+scale*87); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*371,oy+scale*87,ox+scale*426,oy+scale*96); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*426,oy+scale*96,ox+scale*471,oy+scale*142); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*471,oy+scale*142,ox+scale*488,oy+scale*248); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*482,oy+scale*196,ox+scale*466,oy+scale*231); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*260,oy+scale*240,ox+scale*208,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*208,oy+scale*203,ox+scale*191,oy+scale*144); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*191,oy+scale*144,ox+scale*215,oy+scale*95); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*215,oy+scale*95,ox+scale*273,oy+scale*66); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*273,oy+scale*66,ox+scale*380,oy+scale*83); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*328,oy+scale*72,ox+scale*357,oy+scale*98); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*302,oy+scale*297,ox+scale*250,oy+scale*335); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*250,oy+scale*335,ox+scale*189,oy+scale*334); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*189,oy+scale*334,ox+scale*150,oy+scale*295); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*150,oy+scale*295,ox+scale*140,oy+scale*231); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*140,oy+scale*231,ox+scale*189,oy+scale*135); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*163,oy+scale*180,ox+scale*197,oy+scale*161); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*368,oy+scale*275,ox+scale*389,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*389,oy+scale*336,ox+scale*369,oy+scale*393); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*369,oy+scale*393,ox+scale*319,oy+scale*419); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*319,oy+scale*419,ox+scale*255,oy+scale*409); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*255,oy+scale*409,ox+scale*179,oy+scale*332); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*215,oy+scale*371,ox+scale*207,oy+scale*333); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*368,oy+scale*205,ox+scale*432,oy+scale*204); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*432,oy+scale*204,ox+scale*481,oy+scale*241); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*481,oy+scale*241,ox+scale*490,oy+scale*296); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*490,oy+scale*296,ox+scale*460,oy+scale*354); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*460,oy+scale*354,ox+scale*364,oy+scale*402); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*412,oy+scale*381,ox+scale*373,oy+scale*376);renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*383,oy+scale*153,ox+scale*422,oy+scale*205,ox+scale*390,oy+scale*205); renderer.triangle(ox+scale*383,oy+scale*153,ox+scale*390,oy+scale*205,ox+scale*378,oy+scale*161); renderer.triangle(ox+scale*383,oy+scale*153,ox+scale*378,oy+scale*161,ox+scale*330,oy+scale*191); renderer.triangle(ox+scale*383,oy+scale*153,ox+scale*330,oy+scale*191,ox+scale*330,oy+scale*187); renderer.triangle(ox+scale*257,oy+scale*153,ox+scale*318,oy+scale*132,ox+scale*308,oy+scale*162); renderer.triangle(ox+scale*257,oy+scale*153,ox+scale*308,oy+scale*162,ox+scale*263,oy+scale*161); renderer.triangle(ox+scale*257,oy+scale*153,ox+scale*263,oy+scale*161,ox+scale*277,oy+scale*216); renderer.triangle(ox+scale*257,oy+scale*153,ox+scale*277,oy+scale*216,ox+scale*273,oy+scale*215); renderer.triangle(ox+scale*218,oy+scale*273,ox+scale*217,oy+scale*209,ox+scale*242,oy+scale*227); renderer.triangle(ox+scale*218,oy+scale*273,ox+scale*242,oy+scale*227,ox+scale*227,oy+scale*270); renderer.triangle(ox+scale*218,oy+scale*273,ox+scale*227,oy+scale*270,ox+scale*284,oy+scale*274); renderer.triangle(ox+scale*218,oy+scale*273,ox+scale*284,oy+scale*274,ox+scale*281,oy+scale*277); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*258,oy+scale*328,ox+scale*284,oy+scale*310); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*284,oy+scale*310,ox+scale*319,oy+scale*338); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*319,oy+scale*338,ox+scale*341,oy+scale*285); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*341,oy+scale*285,ox+scale*343,oy+scale*288); renderer.triangle(ox+scale*422,oy+scale*273,ox+scale*385,oy+scale*326,ox+scale*375,oy+scale*296); renderer.triangle(ox+scale*422,oy+scale*273,ox+scale*375,oy+scale*296,ox+scale*413,oy+scale*271); renderer.triangle(ox+scale*422,oy+scale*273,ox+scale*413,oy+scale*271,ox+scale*369,oy+scale*234); renderer.triangle(ox+scale*422,oy+scale*273,ox+scale*369,oy+scale*234,ox+scale*373,oy+scale*233); renderer.triangle(ox+scale*330,oy+scale*187,ox+scale*318,oy+scale*208,ox+scale*320,oy+scale*209); renderer.triangle(ox+scale*330,oy+scale*187,ox+scale*320,oy+scale*209,ox+scale*330,oy+scale*190); renderer.triangle(ox+scale*273,oy+scale*215,ox+scale*289,oy+scale*232,ox+scale*291,oy+scale*230); renderer.triangle(ox+scale*273,oy+scale*215,ox+scale*291,oy+scale*230,ox+scale*276,oy+scale*215); renderer.triangle(ox+scale*281,oy+scale*277,ox+scale*303,oy+scale*267,ox+scale*302,oy+scale*265); renderer.triangle(ox+scale*281,oy+scale*277,ox+scale*302,oy+scale*265,ox+scale*282,oy+scale*274); renderer.triangle(ox+scale*343,oy+scale*288,ox+scale*340,oy+scale*265,ox+scale*338,oy+scale*265); renderer.triangle(ox+scale*343,oy+scale*288,ox+scale*338,oy+scale*265,ox+scale*341,oy+scale*286); renderer.triangle(ox+scale*373,oy+scale*233,ox+scale*350,oy+scale*228,ox+scale*349,oy+scale*230); renderer.triangle(ox+scale*373,oy+scale*233,ox+scale*349,oy+scale*230,ox+scale*371,oy+scale*234);
        }

        else if (index==4)
        {
            //hibiscus 3
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*290,oy+scale*189,ox+scale*321,oy+scale*122); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*122,ox+scale*402,oy+scale*102); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*402,oy+scale*102,ox+scale*475,oy+scale*151); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*475,oy+scale*151,ox+scale*500,oy+scale*256); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*500,oy+scale*256,ox+scale*488,oy+scale*253); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*459,oy+scale*335,ox+scale*405,oy+scale*359); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*290,oy+scale*292,ox+scale*217,oy+scale*298); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*217,oy+scale*298,ox+scale*159,oy+scale*238); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*159,oy+scale*238,ox+scale*166,oy+scale*150); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*166,oy+scale*150,ox+scale*244,oy+scale*76); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*244,oy+scale*76,ox+scale*248,oy+scale*88); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*333,oy+scale*72,ox+scale*381,oy+scale*107); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*380,oy+scale*240,ox+scale*422,oy+scale*300); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*422,oy+scale*300,ox+scale*399,oy+scale*380); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*399,oy+scale*380,ox+scale*319,oy+scale*419); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*319,oy+scale*419,ox+scale*216,oy+scale*388); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*216,oy+scale*388,ox+scale*225,oy+scale*379); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*168,oy+scale*312,ox+scale*174,oy+scale*254); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*413,oy+scale*186,ox+scale*415,oy+scale*291,ox+scale*396,oy+scale*261); renderer.triangle(ox+scale*413,oy+scale*186,ox+scale*396,oy+scale*261,ox+scale*405,oy+scale*192); renderer.triangle(ox+scale*413,oy+scale*186,ox+scale*405,oy+scale*192,ox+scale*336,oy+scale*193); renderer.triangle(ox+scale*413,oy+scale*186,ox+scale*336,oy+scale*193,ox+scale*336,oy+scale*189); renderer.triangle(ox+scale*227,oy+scale*186,ox+scale*317,oy+scale*132,ox+scale*300,oy+scale*164); renderer.triangle(ox+scale*227,oy+scale*186,ox+scale*300,oy+scale*164,ox+scale*236,oy+scale*190); renderer.triangle(ox+scale*227,oy+scale*186,ox+scale*236,oy+scale*190,ox+scale*271,oy+scale*250); renderer.triangle(ox+scale*227,oy+scale*186,ox+scale*271,oy+scale*250,ox+scale*268,oy+scale*252); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*228,oy+scale*297,ox+scale*264,oy+scale*295); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*264,oy+scale*295,ox+scale*319,oy+scale*338); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*319,oy+scale*338,ox+scale*353,oy+scale*277); renderer.triangle(ox+scale*320,oy+scale*348,ox+scale*353,oy+scale*277,ox+scale*356,oy+scale*279); renderer.triangle(ox+scale*336,oy+scale*189,ox+scale*317,oy+scale*208,ox+scale*320,oy+scale*209); renderer.triangle(ox+scale*336,oy+scale*189,ox+scale*320,oy+scale*209,ox+scale*337,oy+scale*192); renderer.triangle(ox+scale*268,oy+scale*252,ox+scale*294,oy+scale*259,ox+scale*293,oy+scale*256); renderer.triangle(ox+scale*268,oy+scale*252,ox+scale*293,oy+scale*256,ox+scale*270,oy+scale*249); renderer.triangle(ox+scale*356,oy+scale*279,ox+scale*349,oy+scale*253,ox+scale*347,oy+scale*256); renderer.triangle(ox+scale*356,oy+scale*279,ox+scale*347,oy+scale*256,ox+scale*353,oy+scale*279);    }

        else if (index==5)
        {
//dogwood 4 petal simple
            renderer.setColor(secondaryColour);

            renderer.triangle(ox+scale*320	,oy+scale*240	,ox+scale*380	,oy+scale*240	,ox+scale*320	,oy+scale*180); renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*364	,oy+scale*227	,ox+scale*391	,oy+scale*232); renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*391	,oy+scale*232	,ox+scale*416	,oy+scale*227); renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*416	,oy+scale*227	,ox+scale*448	,oy+scale*114);
            renderer.setColor(primaryColour);

            renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*448	,oy+scale*114	,ox+scale*334	,oy+scale*148); renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*334	,oy+scale*148	,ox+scale*329	,oy+scale*170); renderer.triangle(ox+scale*325	,oy+scale*236	,ox+scale*329	,oy+scale*170	,ox+scale*336	,oy+scale*213); renderer.triangle(ox+scale*320	,oy+scale*240	,ox+scale*320	,oy+scale*180	,ox+scale*260	,oy+scale*240); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*307	,oy+scale*196	,ox+scale*312	,oy+scale*169); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*312	,oy+scale*169	,ox+scale*307	,oy+scale*144); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*307	,oy+scale*144	,ox+scale*194	,oy+scale*112); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*194	,oy+scale*112	,ox+scale*228	,oy+scale*226); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*228	,oy+scale*226	,ox+scale*250	,oy+scale*231); renderer.triangle(ox+scale*316	,oy+scale*235	,ox+scale*250	,oy+scale*231	,ox+scale*293	,oy+scale*224); renderer.triangle(ox+scale*320	,oy+scale*240	,ox+scale*260	,oy+scale*240	,ox+scale*320	,oy+scale*300); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*276	,oy+scale*253	,ox+scale*249	,oy+scale*248); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*249	,oy+scale*248	,ox+scale*224	,oy+scale*253); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*224	,oy+scale*253	,ox+scale*192	,oy+scale*366); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*192	,oy+scale*366	,ox+scale*306	,oy+scale*332); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*306	,oy+scale*332	,ox+scale*311	,oy+scale*310); renderer.triangle(ox+scale*315	,oy+scale*244	,ox+scale*311	,oy+scale*310	,ox+scale*304	,oy+scale*267); renderer.triangle(ox+scale*320	,oy+scale*240	,ox+scale*320	,oy+scale*300	,ox+scale*380	,oy+scale*240); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*333	,oy+scale*284	,ox+scale*328	,oy+scale*311); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*328	,oy+scale*311	,ox+scale*333	,oy+scale*336); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*333	,oy+scale*336	,ox+scale*446	,oy+scale*368); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*446	,oy+scale*368	,ox+scale*412	,oy+scale*254); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*412	,oy+scale*254	,ox+scale*390	,oy+scale*249); renderer.triangle(ox+scale*324	,oy+scale*245	,ox+scale*390	,oy+scale*249	,ox+scale*347	,oy+scale*256);

        }
        else if (index ==6)
        {
            //moonflower 5
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*120,ox+scale*374,oy+scale*98); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*374,oy+scale*98,ox+scale*425,oy+scale*94); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*425,oy+scale*94,ox+scale*439,oy+scale*146); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*439,oy+scale*146,ox+scale*434,oy+scale*204); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*206,oy+scale*203,ox+scale*201,oy+scale*145); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*201,oy+scale*145,ox+scale*214,oy+scale*95); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*214,oy+scale*95,ox+scale*267,oy+scale*97); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*267,oy+scale*97,ox+scale*321,oy+scale*120); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*249,oy+scale*337,ox+scale*193,oy+scale*323); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*193,oy+scale*323,ox+scale*150,oy+scale*297); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*150,oy+scale*297,ox+scale*168,oy+scale*246); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*168,oy+scale*246,ox+scale*207,oy+scale*202); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*391,oy+scale*337,ox+scale*360,oy+scale*386); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*360,oy+scale*386,ox+scale*321,oy+scale*420); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*420,ox+scale*279,oy+scale*386); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*279,oy+scale*386,ox+scale*249,oy+scale*336); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*434,oy+scale*203,ox+scale*472,oy+scale*247); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*472,oy+scale*247,ox+scale*491,oy+scale*294); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*491,oy+scale*294,ox+scale*446,oy+scale*325); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*446,oy+scale*325,ox+scale*389,oy+scale*337); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*123,ox+scale*321,oy+scale*123); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*123,ox+scale*321,oy+scale*240); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*209,oy+scale*204,ox+scale*209,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*209,oy+scale*203,ox+scale*320,oy+scale*239); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*251,oy+scale*335,ox+scale*250,oy+scale*334); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*250,oy+scale*334,ox+scale*319,oy+scale*239); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*389,oy+scale*335,ox+scale*388,oy+scale*335); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*388,oy+scale*335,ox+scale*319,oy+scale*241); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*431,oy+scale*204,ox+scale*432,oy+scale*205); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*432,oy+scale*205,ox+scale*320,oy+scale*241); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*342,oy+scale*133,ox+scale*386,oy+scale*121); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*386,oy+scale*121,ox+scale*400,oy+scale*128); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*400,oy+scale*128,ox+scale*412,oy+scale*141); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*412,oy+scale*141,ox+scale*415,oy+scale*186); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*225,oy+scale*186,ox+scale*227,oy+scale*140); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*227,oy+scale*140,ox+scale*238,oy+scale*129); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*238,oy+scale*129,ox+scale*254,oy+scale*122); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*254,oy+scale*122,ox+scale*298,oy+scale*133); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*239,oy+scale*314,ox+scale*197,oy+scale*297); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*197,oy+scale*297,ox+scale*189,oy+scale*284); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*189,oy+scale*284,ox+scale*187,oy+scale*266); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*187,oy+scale*266,ox+scale*211,oy+scale*228); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*365,oy+scale*339,ox+scale*337,oy+scale*375); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*337,oy+scale*375,ox+scale*321,oy+scale*378); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*378,ox+scale*304,oy+scale*374); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*304,oy+scale*374,ox+scale*275,oy+scale*340); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*429,oy+scale*228,ox+scale*454,oy+scale*266); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*454,oy+scale*266,ox+scale*451,oy+scale*281); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*451,oy+scale*281,ox+scale*443,oy+scale*297); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*443,oy+scale*297,ox+scale*401,oy+scale*314);
        }
        else if (index ==7)
        {
            //daisy 7
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*120,ox+scale*326,oy+scale*85); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*326,oy+scale*85,ox+scale*338,oy+scale*71); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*338,oy+scale*71,ox+scale*356,oy+scale*67); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*356,oy+scale*67,ox+scale*381,oy+scale*70); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*381,oy+scale*70,ox+scale*398,oy+scale*76); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*398,oy+scale*76,ox+scale*414,oy+scale*86); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*414,oy+scale*86,ox+scale*433,oy+scale*103); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*433,oy+scale*103,ox+scale*440,oy+scale*120); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*440,oy+scale*120,ox+scale*436,oy+scale*139); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*436,oy+scale*139,ox+scale*414,oy+scale*166); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*226,oy+scale*165,ox+scale*203,oy+scale*139); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*203,oy+scale*139,ox+scale*199,oy+scale*120); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*199,oy+scale*120,ox+scale*207,oy+scale*104); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*207,oy+scale*104,ox+scale*225,oy+scale*86); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*225,oy+scale*86,ox+scale*241,oy+scale*77); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*241,oy+scale*77,ox+scale*258,oy+scale*70); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*258,oy+scale*70,ox+scale*283,oy+scale*66); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*283,oy+scale*66,ox+scale*301,oy+scale*71); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*301,oy+scale*71,ox+scale*314,oy+scale*86); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*314,oy+scale*86,ox+scale*321,oy+scale*121); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*203,oy+scale*267,ox+scale*168,oy+scale*269); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*168,oy+scale*269,ox+scale*151,oy+scale*260); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*151,oy+scale*260,ox+scale*143,oy+scale*244); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*143,oy+scale*244,ox+scale*141,oy+scale*218); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*141,oy+scale*218,ox+scale*143,oy+scale*200); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*143,oy+scale*200,ox+scale*149,oy+scale*182); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*149,oy+scale*182,ox+scale*161,oy+scale*160); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*161,oy+scale*160,ox+scale*176,oy+scale*150); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*176,oy+scale*150,ox+scale*196,oy+scale*149); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*196,oy+scale*149,ox+scale*228,oy+scale*165); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*268,oy+scale*348,ox+scale*247,oy+scale*377); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*247,oy+scale*377,ox+scale*230,oy+scale*385); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*230,oy+scale*385,ox+scale*213,oy+scale*381); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*213,oy+scale*381,ox+scale*191,oy+scale*367); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*191,oy+scale*367,ox+scale*179,oy+scale*354); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*179,oy+scale*354,ox+scale*168,oy+scale*338); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*168,oy+scale*338,ox+scale*159,oy+scale*314); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*159,oy+scale*314,ox+scale*160,oy+scale*296); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*160,oy+scale*296,ox+scale*171,oy+scale*280); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*171,oy+scale*280,ox+scale*204,oy+scale*266); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*372,oy+scale*348,ox+scale*382,oy+scale*382); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*382,oy+scale*382,ox+scale*377,oy+scale*400); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*377,oy+scale*400,ox+scale*363,oy+scale*412); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*363,oy+scale*412,ox+scale*339,oy+scale*420); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*339,oy+scale*420,ox+scale*321,oy+scale*421); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*321,oy+scale*421,ox+scale*302,oy+scale*420); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*302,oy+scale*420,ox+scale*278,oy+scale*412); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*278,oy+scale*412,ox+scale*264,oy+scale*400); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*264,oy+scale*400,ox+scale*259,oy+scale*381); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*259,oy+scale*381,ox+scale*268,oy+scale*347); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*437,oy+scale*267,ox+scale*470,oy+scale*280); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*470,oy+scale*280,ox+scale*481,oy+scale*295); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*481,oy+scale*295,ox+scale*481,oy+scale*313); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*481,oy+scale*313,ox+scale*472,oy+scale*337); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*472,oy+scale*337,ox+scale*462,oy+scale*353); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*462,oy+scale*353,ox+scale*449,oy+scale*366); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*449,oy+scale*366,ox+scale*428,oy+scale*381); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*428,oy+scale*381,ox+scale*410,oy+scale*384); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*410,oy+scale*384,ox+scale*392,oy+scale*376); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*392,oy+scale*376,ox+scale*371,oy+scale*348); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*414,oy+scale*165,ox+scale*445,oy+scale*148); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*445,oy+scale*148,ox+scale*464,oy+scale*149); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*464,oy+scale*149,ox+scale*478,oy+scale*160); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*478,oy+scale*160,ox+scale*491,oy+scale*182); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*491,oy+scale*182,ox+scale*497,oy+scale*199); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*497,oy+scale*199,ox+scale*499,oy+scale*218); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*499,oy+scale*218,ox+scale*497,oy+scale*243); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*497,oy+scale*243,ox+scale*489,oy+scale*259); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*489,oy+scale*259,ox+scale*471,oy+scale*268); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*471,oy+scale*268,ox+scale*436,oy+scale*267); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*180,ox+scale*367,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*273,oy+scale*203,ox+scale*320,oy+scale*180); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*262,oy+scale*253,ox+scale*273,oy+scale*203); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*294,oy+scale*294,ox+scale*262,oy+scale*253); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*346,oy+scale*294,ox+scale*294,oy+scale*294); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*378,oy+scale*253,ox+scale*346,oy+scale*294); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*367,oy+scale*203,ox+scale*378,oy+scale*253);
        }

        else if (index ==8)
        {
            //poppy 2
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*343,oy+scale*189,ox+scale*344,oy+scale*151); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*344,oy+scale*151,ox+scale*352,oy+scale*132); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*352,oy+scale*132,ox+scale*384,oy+scale*115); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*384,oy+scale*115,ox+scale*428,oy+scale*121); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*428,oy+scale*121,ox+scale*465,oy+scale*148); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*465,oy+scale*148,ox+scale*488,oy+scale*180); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*488,oy+scale*180,ox+scale*498,oy+scale*216); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*498,oy+scale*216,ox+scale*500,oy+scale*240); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*500,oy+scale*240,ox+scale*499,oy+scale*263); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*499,oy+scale*263,ox+scale*489,oy+scale*299); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*489,oy+scale*299,ox+scale*464,oy+scale*334); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*464,oy+scale*334,ox+scale*428,oy+scale*359); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*428,oy+scale*359,ox+scale*382,oy+scale*366); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*382,oy+scale*366,ox+scale*352,oy+scale*348); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*352,oy+scale*348,ox+scale*342,oy+scale*329); renderer.triangle(ox+scale*348,oy+scale*240,ox+scale*342,oy+scale*329,ox+scale*342,oy+scale*292); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*297,oy+scale*291,ox+scale*296,oy+scale*329); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*296,oy+scale*329,ox+scale*288,oy+scale*348); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*288,oy+scale*348,ox+scale*256,oy+scale*365); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*256,oy+scale*365,ox+scale*212,oy+scale*359); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*212,oy+scale*359,ox+scale*175,oy+scale*332); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*175,oy+scale*332,ox+scale*152,oy+scale*300); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*152,oy+scale*300,ox+scale*142,oy+scale*264); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*142,oy+scale*264,ox+scale*140,oy+scale*240); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*140,oy+scale*240,ox+scale*141,oy+scale*217); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*141,oy+scale*217,ox+scale*151,oy+scale*181); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*151,oy+scale*181,ox+scale*176,oy+scale*146); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*176,oy+scale*146,ox+scale*212,oy+scale*121); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*212,oy+scale*121,ox+scale*258,oy+scale*114); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*258,oy+scale*114,ox+scale*288,oy+scale*132); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*288,oy+scale*132,ox+scale*298,oy+scale*151); renderer.triangle(ox+scale*292,oy+scale*240,ox+scale*298,oy+scale*151,ox+scale*298,oy+scale*188); renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*245,oy+scale*110,ox+scale*270,oy+scale*101); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*270,oy+scale*101,ox+scale*306,oy+scale*96); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*306,oy+scale*96,ox+scale*345,oy+scale*96); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*345,oy+scale*96,ox+scale*367,oy+scale*99); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*367,oy+scale*99,ox+scale*395,oy+scale*107); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*395,oy+scale*107,ox+scale*404,oy+scale*120); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*404,oy+scale*120,ox+scale*336,oy+scale*216); renderer.triangle(ox+scale*239,oy+scale*122,ox+scale*336,oy+scale*216,ox+scale*289,oy+scale*214); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*395,oy+scale*370,ox+scale*370,oy+scale*379); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*370,oy+scale*379,ox+scale*334,oy+scale*384); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*334,oy+scale*384,ox+scale*295,oy+scale*384); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*295,oy+scale*384,ox+scale*273,oy+scale*381); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*273,oy+scale*381,ox+scale*245,oy+scale*373); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*245,oy+scale*373,ox+scale*236,oy+scale*360); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*236,oy+scale*360,ox+scale*304,oy+scale*264); renderer.triangle(ox+scale*401,oy+scale*358,ox+scale*304,oy+scale*264,ox+scale*351,oy+scale*266); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*406,oy+scale*157,ox+scale*423,oy+scale*179,ox+scale*422,oy+scale*161); renderer.triangle(ox+scale*234,oy+scale*323,ox+scale*217,oy+scale*301,ox+scale*218,oy+scale*319); renderer.triangle(ox+scale*405,oy+scale*197,ox+scale*411,oy+scale*209,ox+scale*414,oy+scale*200); renderer.triangle(ox+scale*235,oy+scale*283,ox+scale*229,oy+scale*271,ox+scale*226,oy+scale*280); renderer.triangle(ox+scale*458,oy+scale*194,ox+scale*465,oy+scale*220,ox+scale*472,oy+scale*205); renderer.triangle(ox+scale*182,oy+scale*286,ox+scale*175,oy+scale*260,ox+scale*168,oy+scale*275); renderer.triangle(ox+scale*369,oy+scale*182,ox+scale*380,oy+scale*192,ox+scale*378,oy+scale*180); renderer.triangle(ox+scale*271,oy+scale*298,ox+scale*260,oy+scale*288,ox+scale*262,oy+scale*300); renderer.triangle(ox+scale*414,oy+scale*251,ox+scale*412,oy+scale*264,ox+scale*420,oy+scale*258); renderer.triangle(ox+scale*226,oy+scale*229,ox+scale*228,oy+scale*216,ox+scale*220,oy+scale*222); renderer.triangle(ox+scale*425,oy+scale*298,ox+scale*435,oy+scale*278,ox+scale*438,oy+scale*297); renderer.triangle(ox+scale*215,oy+scale*182,ox+scale*205,oy+scale*202,ox+scale*202,oy+scale*183); renderer.triangle(ox+scale*384,oy+scale*285,ox+scale*390,oy+scale*277,ox+scale*390,oy+scale*286); renderer.triangle(ox+scale*256,oy+scale*195,ox+scale*250,oy+scale*203,ox+scale*250,oy+scale*194); renderer.triangle(ox+scale*406,oy+scale*339,ox+scale*387,oy+scale*349,ox+scale*398,oy+scale*356); renderer.triangle(ox+scale*234,oy+scale*141,ox+scale*253,oy+scale*131,ox+scale*242,oy+scale*124); renderer.setColor(secondaryColour); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*180,ox+scale*349,oy+scale*189); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*349,oy+scale*189,ox+scale*370,oy+scale*210); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*370,oy+scale*210,ox+scale*380,oy+scale*239); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*380,oy+scale*239,ox+scale*370,oy+scale*270); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*370,oy+scale*270,ox+scale*347,oy+scale*292); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*347,oy+scale*292,ox+scale*320,oy+scale*300); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*320,oy+scale*300,ox+scale*291,oy+scale*291); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*291,oy+scale*291,ox+scale*270,oy+scale*270); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*270,oy+scale*270,ox+scale*260,oy+scale*241); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*260,oy+scale*241,ox+scale*270,oy+scale*210); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*270,oy+scale*210,ox+scale*293,oy+scale*188); renderer.triangle(ox+scale*320,oy+scale*240,ox+scale*293,oy+scale*188,ox+scale*320,oy+scale*180);

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
            sr.setColor(0.86f,0.65f,0.22f, 1);
            if(selectedSymbol==0)
            {
                sr.triangle(originX , originY ,
                        originX - edgeSize, originY,
                        originX - edgeSize*0.5f, originY+altitudeSize);
            }
            else if(selectedSymbol==1)
            {
                sr.triangle(originX  , originY ,
                        originX - edgeSize*0.5f, originY+altitudeSize,
                        originX + edgeSize*0.5f, originY+altitudeSize);
            }
            else if(selectedSymbol==2)
            {
                sr.triangle(originX  , originY ,
                        originX + edgeSize*0.5f, originY+altitudeSize,
                        originX +edgeSize, originY);
            }
            else if(selectedSymbol==3)
            {
                sr.triangle(originX , originY ,
                        originX +edgeSize, originY,
                        originX + edgeSize*0.5f, originY-altitudeSize);
            }
            else if(selectedSymbol==4)
            {
                sr.triangle(originX  , originY ,
                        originX + edgeSize*0.5f, originY-altitudeSize,
                        originX - edgeSize*0.5f, originY-altitudeSize);
            }
            else if(selectedSymbol==5)
            {
                sr.triangle(originX  , originY ,
                        originX - edgeSize*0.5f, originY-altitudeSize,
                        originX - edgeSize, originY);
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

    void increaseScorePenaltyMultiplier()
    {
        if(scorePenaltyMultiplier<5)
        {
            scorePenaltyMultiplier++;
        }
    }
}