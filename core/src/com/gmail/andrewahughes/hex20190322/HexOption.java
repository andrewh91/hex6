package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

    void drawHex(ShapeRenderer renderer)
    {

        renderer.triangle(
                centreX+edgeSize,
                centreY,
                centreX+edgeSize/2,
                centreY+altitudeSize,
                centreX-edgeSize/2,
                centreY+altitudeSize);
        renderer.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY+altitudeSize,
                centreX-edgeSize,
                centreY);
        renderer.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY-altitudeSize);
        renderer.triangle(
                centreX+edgeSize,
                centreY,
                centreX-edgeSize/2,
                centreY-altitudeSize,
                centreX+edgeSize/2,
                centreY-altitudeSize);

//if the text value has been assigned a value
        if(text!=null)
        {

            int ox = (int)centreX;
            int oy = (int)centreY;
            Color primaryColour;
            Color secondaryColour;
            Color tertiaryColour;
            Color contrastColour;

            primaryColour= new Color(0f,1f,0f,1f);
            secondaryColour=new Color(0f,0f,1f,1f);
            tertiaryColour=new Color(1f,0f,0f,1f);
            contrastColour=new Color(1f,1f,1f,1f);



            float scale=  edgeSize / 320f;


//display the appropriate picture
//pause screen options
//"Cancel Changes","Return to Main Menu","Change Difficulty" ,"Change Symbol Type","Save Changes","Change Game Mode","Change Number of Hexes","Change Zoom Mode","Scoreboard"
//scoreboard screen options
//"Back", "Show Scoreboard", "Submit", "Show Offline Scoreboard"
//start menu options
//"Sign In", "Sign out", "Start Practise Game", "Start Hiscore Game","Exit"
            if(text=="Cancel Changes" )
            {


                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-340*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);
/*play**************************************************************************************/
                renderer.setColor(primaryColour);
                renderer.triangle(ox+scale*440,oy-scale*240,ox+scale*264,oy-scale*344,ox+scale*264,oy-scale*140);





            }
            else if(text=="Return to Main Menu" )
            {


                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-320*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);


/*exit*******************************************************************************/
                renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*382,oy-scale*150,ox+scale*410,oy-scale*178,ox+scale*258,oy-scale*330); renderer.triangle(ox+scale*382,oy-scale*150,ox+scale*258,oy-scale*330,ox+scale*230,oy-scale*302); renderer.triangle(ox+scale*230,oy-scale*178,ox+scale*258,oy-scale*150,ox+scale*410,oy-scale*302); renderer.triangle(ox+scale*230,oy-scale*178,ox+scale*410,oy-scale*302,ox+scale*382,oy-scale*330);


            }
            else if(text=="Change Difficulty" )
            {

            }
            else if(text=="Change Symbol Type" )
            {

            }
            else if(text=="Save Changes" )
            {

            }
            else if(text=="Change Game Mode" )
            {

            }
            else if(text=="Change Number of Hexes" )
            {

            }
            else if(text=="Change Zoom Mode" )
            {

            }
            else if(text=="Scoreboard" )
            {





                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-320*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);
/*scoreboard*******************************************************************************/
                renderer.setColor(secondaryColour);
                renderer.triangle(ox+scale*204,oy-scale*124,ox+scale*276,oy-scale*124,ox+scale*276,oy-scale*196); renderer.triangle

                    (ox+scale*204,oy-scale*124,ox+scale*276,oy-scale*196,ox+scale*204,oy-scale*196); renderer.triangle

                    (ox+scale*436,oy-scale*124,ox+scale*284,oy-scale*124,ox+scale*284,oy-scale*196); renderer.triangle

                    (ox+scale*436,oy-scale*124,ox+scale*284,oy-scale*196,ox+scale*436,oy-scale*196); renderer.triangle

                    (ox+scale*204,oy-scale*204,ox+scale*276,oy-scale*204,ox+scale*276,oy-scale*276); renderer.triangle

                    (ox+scale*204,oy-scale*204,ox+scale*276,oy-scale*276,ox+scale*204,oy-scale*276); renderer.triangle

                    (ox+scale*284,oy-scale*204,ox+scale*436,oy-scale*204,ox+scale*436,oy-scale*276); renderer.triangle

                    (ox+scale*284,oy-scale*204,ox+scale*436,oy-scale*276,ox+scale*284,oy-scale*276); renderer.triangle

                    (ox+scale*204,oy-scale*284,ox+scale*276,oy-scale*284,ox+scale*276,oy-scale*356); renderer.triangle

                    (ox+scale*204,oy-scale*284,ox+scale*276,oy-scale*356,ox+scale*204,oy-scale*356); renderer.triangle

                    (ox+scale*284,oy-scale*284,ox+scale*436,oy-scale*284,ox+scale*436,oy-scale*356); renderer.triangle

                    (ox+scale*284,oy-scale*284,ox+scale*436,oy-scale*356,ox+scale*284,oy-scale*356);
            }

            else if(text=="Back" )
            {
                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-320*scale);
                oy=(int)(oy+480*scale); 
                oy=(int)(oy+0*scale);


/*exit*******************************************************************************/
                renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*382,oy-scale*150,ox+scale*410,oy-scale*178,ox+scale*258,oy-scale*330); renderer.triangle(ox+scale*382,oy-scale*150,ox+scale*258,oy-scale*330,ox+scale*230,oy-scale*302); renderer.triangle(ox+scale*230,oy-scale*178,ox+scale*258,oy-scale*150,ox+scale*410,oy-scale*302); renderer.triangle(ox+scale*230,oy-scale*178,ox+scale*410,oy-scale*302,ox+scale*382,oy-scale*330);


            }
            else if(text=="Show Scoreboard" )
            {


                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-320*scale);
                oy=(int)(oy+480*scale); 
                oy=(int)(oy+0*scale);
/*scoreboard*******************************************************************************/
                renderer.setColor(secondaryColour);
                renderer.triangle(ox+scale*204,oy-scale*124,ox+scale*276,oy-scale*124,ox+scale*276,oy-scale*196); renderer.triangle

                    (ox+scale*204,oy-scale*124,ox+scale*276,oy-scale*196,ox+scale*204,oy-scale*196); renderer.triangle

                    (ox+scale*436,oy-scale*124,ox+scale*284,oy-scale*124,ox+scale*284,oy-scale*196); renderer.triangle

                    (ox+scale*436,oy-scale*124,ox+scale*284,oy-scale*196,ox+scale*436,oy-scale*196); renderer.triangle

                    (ox+scale*204,oy-scale*204,ox+scale*276,oy-scale*204,ox+scale*276,oy-scale*276); renderer.triangle

                    (ox+scale*204,oy-scale*204,ox+scale*276,oy-scale*276,ox+scale*204,oy-scale*276); renderer.triangle

                    (ox+scale*284,oy-scale*204,ox+scale*436,oy-scale*204,ox+scale*436,oy-scale*276); renderer.triangle

                    (ox+scale*284,oy-scale*204,ox+scale*436,oy-scale*276,ox+scale*284,oy-scale*276); renderer.triangle

                    (ox+scale*204,oy-scale*284,ox+scale*276,oy-scale*284,ox+scale*276,oy-scale*356); renderer.triangle

                    (ox+scale*204,oy-scale*284,ox+scale*276,oy-scale*356,ox+scale*204,oy-scale*356); renderer.triangle

                    (ox+scale*284,oy-scale*284,ox+scale*436,oy-scale*284,ox+scale*436,oy-scale*356); renderer.triangle

                    (ox+scale*284,oy-scale*284,ox+scale*436,oy-scale*356,ox+scale*284,oy-scale*356);
            }
            else if(text=="Submit" )
            {



                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-288*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy-100*scale);
/*scoreboard upload group********************************************************************/
                renderer.setColor(secondaryColour);
                renderer.triangle(ox+scale*163.2f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-156.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-156.8f,ox+scale*163.2f,oy+scale*-156.8f); renderer.triangle(ox+scale*348.8f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-156.8f); renderer.triangle(ox+scale*348.8f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-156.8f,ox+scale*348.8f,oy+scale*-156.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-220.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-220.8f,ox+scale*163.2f,oy+scale*-220.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-220.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-220.8f,ox+scale*227.2f,oy+scale*-220.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-284.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-284.8f,ox+scale*163.2f,oy+scale*-284.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-284.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-284.8f,ox+scale*227.2f,oy+scale*-284.8f);



                ox = (int)centreX;
                oy = (int)centreY;

                ox=(int)(ox-280*scale);
                oy=(int)(oy+480*scale); 
                oy=(int)(oy-0*scale);
//wifi
//offset y by +92 to centre this
                renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*292,oy+scale*-196,ox+scale*292,oy+scale*-200); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*292,oy+scale*-200,ox+scale*320,oy+scale*-196); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*320,oy+scale*-196,ox+scale*348,oy+scale*-200); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*348,oy+scale*-200,ox+scale*348,oy+scale*-196); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*264,oy+scale*-148,ox+scale*264,oy+scale*-156); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*264,oy+scale*-156,ox+scale*320,oy+scale*-144); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*320,oy+scale*-144,ox+scale*376,oy+scale*-156); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*376,oy+scale*-156,ox+scale*376,oy+scale*-148); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*236,oy+scale*-104,ox+scale*236,oy+scale*-116); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*236,oy+scale*-116,ox+scale*320,oy+scale*-96); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*320,oy+scale*-96,ox+scale*404,oy+scale*-116); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*404,oy+scale*-116,ox+scale*404,oy+scale*-104);




                ox = (int)centreX;
                oy = (int)centreY;

                ox=(int)(ox-360*scale);
                oy=(int)(oy+480*scale); 
                oy=(int)(oy+360*scale);

//up arrow
//centred
                renderer.setColor(primaryColour); renderer.triangle
                    (ox+scale*480,oy+scale*-540,ox+scale*416,oy+scale*-628,ox+scale*448,oy+scale*-628); renderer.triangle(ox+scale*480,oy+scale*-540,ox+scale*448,oy+scale*-628,ox+scale*448,oy+scale*-660); renderer.triangle(ox+scale*480,oy+scale*-540,ox+scale*448,oy+scale*-660,ox+scale*512,oy+scale*-660); renderer.triangle(ox+scale*480,oy+scale*-540,ox+scale*512,oy+scale*-660,ox+scale*512,oy+scale*-628); renderer.triangle(ox+scale*480,oy+scale*-540,ox+scale*512,oy+scale*-628,ox+scale*544,oy+scale*-628);


/*end scoreboard upload group */
            }
            else if(text=="Show Offline Scoreboard" )
            {

                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-288*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy-100*scale);
/*scoreboard offline group*****************************************************************/
                renderer.setColor(secondaryColour); 
                renderer.triangle(ox+scale*163.2f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-156.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-99.2f,ox+scale*220.8f,oy+scale*-156.8f,ox+scale*163.2f,oy+scale*-156.8f); renderer.triangle(ox+scale*348.8f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-156.8f); renderer.triangle(ox+scale*348.8f,oy+scale*-99.2f,ox+scale*227.2f,oy+scale*-156.8f,ox+scale*348.8f,oy+scale*-156.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-220.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-163.2f,ox+scale*220.8f,oy+scale*-220.8f,ox+scale*163.2f,oy+scale*-220.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-220.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-163.2f,ox+scale*348.8f,oy+scale*-220.8f,ox+scale*227.2f,oy+scale*-220.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-284.8f); renderer.triangle(ox+scale*163.2f,oy+scale*-227.2f,ox+scale*220.8f,oy+scale*-284.8f,ox+scale*163.2f,oy+scale*-284.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-284.8f); renderer.triangle(ox+scale*227.2f,oy+scale*-227.2f,ox+scale*348.8f,oy+scale*-284.8f,ox+scale*227.2f,oy+scale*-284.8f);

                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-280*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);

//wifi
//offset y by +92 to centre this 
                renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*292,oy+scale*-196,ox+scale*292,oy+scale*-200); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*292,oy+scale*-200,ox+scale*320,oy+scale*-196); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*320,oy+scale*-196,ox+scale*348,oy+scale*-200); renderer.triangle(ox+scale*320,oy+scale*-188,ox+scale*348,oy+scale*-200,ox+scale*348,oy+scale*-196); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*264,oy+scale*-148,ox+scale*264,oy+scale*-156); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*264,oy+scale*-156,ox+scale*320,oy+scale*-144); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*320,oy+scale*-144,ox+scale*376,oy+scale*-156); renderer.triangle(ox+scale*320,oy+scale*-132,ox+scale*376,oy+scale*-156,ox+scale*376,oy+scale*-148); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*236,oy+scale*-104,ox+scale*236,oy+scale*-116); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*236,oy+scale*-116,ox+scale*320,oy+scale*-96); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*320,oy+scale*-96,ox+scale*404,oy+scale*-116); renderer.triangle(ox+scale*320,oy+scale*-80,ox+scale*404,oy+scale*-116,ox+scale*404,oy+scale*-104);





                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-150*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+12*scale);
//exit
                renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*229.2f,oy+scale*-90,ox+scale*246,oy+scale*-106.8f,ox+scale*154.8f,oy+scale*-198); renderer.triangle(ox+scale*229.2f,oy+scale*-90,ox+scale*154.8f,oy+scale*-198,ox+scale*138,oy+scale*-181.2f); renderer.triangle(ox+scale*138,oy+scale*-106.8f,ox+scale*154.8f,oy+scale*-90,ox+scale*246,oy+scale*-181.2f); renderer.triangle(ox+scale*138,oy+scale*-106.8f,ox+scale*246,oy+scale*-181.2f,ox+scale*229.2f,oy+scale*-198);



/*end scoreboard offline group */

            }
            else if(text=="Sign in" )
            {

                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-380*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+50*scale);
/*head group ************************************************************************/
//offset y by -120 to centre this 
//head
                renderer.setColor(primaryColour); renderer.triangle(ox+scale*380,oy-scale*380,ox+scale*260,oy-scale*380,ox+scale*320,oy-scale*296); renderer.setColor(secondaryColour);

                renderer.triangle(ox+scale*320,oy-scale*300,ox+scale*292,oy-scale*292,ox+scale*272,oy-scale*272); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*272,oy-scale*272,ox+scale*260,oy-scale*244); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*260,oy-scale*244,ox+scale*264,oy-scale*216); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*264,oy-scale*216,ox+scale*284,oy-scale*192); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*284,oy-scale*192,ox+scale*316,oy-scale*180); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*316,oy-scale*180,ox+scale*324,oy-scale*180); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*324,oy-scale*180,ox+scale*356,oy-scale*192); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*356,oy-scale*192,ox+scale*376,oy-scale*216); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*376,oy-scale*216,ox+scale*380,oy-scale*244); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*380,oy-scale*244,ox+scale*368,oy-scale*272); renderer.triangle

                    (ox+scale*320,oy-scale*300,ox+scale*368,oy-scale*272,ox+scale*348,oy-scale*292);




                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-90*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+120*scale);
                
//left arrow
//offset x by -90 to centre 
                renderer.setColor(primaryColour); renderer.triangle
                    (ox+scale*380,oy-scale*240,ox+scale*404,oy-scale*208,ox+scale*404,oy-scale*224); renderer.triangle

                    (ox+scale*380,oy-scale*240,ox+scale*404,oy-scale*224,ox+scale*440,oy-scale*224); renderer.triangle

                    (ox+scale*380,oy-scale*240,ox+scale*440,oy-scale*224,ox+scale*440,oy-scale*256); renderer.triangle

                    (ox+scale*380,oy-scale*240,ox+scale*440,oy-scale*256,ox+scale*404,oy-scale*256); renderer.triangle

                    (ox+scale*380,oy-scale*240,ox+scale*404,oy-scale*256,ox+scale*404,oy-scale*272);
                

                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-280*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy-50*scale);
//wifi
//offset y by +92 to centre this 
                renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy-scale*188,ox+scale*292,oy-scale*196,ox+scale*292,oy-scale*200); renderer.triangle

                    (ox+scale*320,oy-scale*188,ox+scale*292,oy-scale*200,ox+scale*320,oy-scale*196); renderer.triangle

                    (ox+scale*320,oy-scale*188,ox+scale*320,oy-scale*196,ox+scale*348,oy-scale*200); renderer.triangle

                    (ox+scale*320,oy-scale*188,ox+scale*348,oy-scale*200,ox+scale*348,oy-scale*196); renderer.triangle

                    (ox+scale*320,oy-scale*132,ox+scale*264,oy-scale*148,ox+scale*264,oy-scale*156); renderer.triangle

                    (ox+scale*320,oy-scale*132,ox+scale*264,oy-scale*156,ox+scale*320,oy-scale*144); renderer.triangle

                    (ox+scale*320,oy-scale*132,ox+scale*320,oy-scale*144,ox+scale*376,oy-scale*156); renderer.triangle

                    (ox+scale*320,oy-scale*132,ox+scale*376,oy-scale*156,ox+scale*376,oy-scale*148); renderer.triangle

                    (ox+scale*320,oy-scale*80,ox+scale*236,oy-scale*104,ox+scale*236,oy-scale*116); renderer.triangle(ox+scale*320,oy-scale*80,ox+scale*236,oy-scale*116,ox+scale*320,oy-scale*96);

                renderer.triangle(ox+scale*320,oy-scale*80,ox+scale*320,oy-scale*96,ox+scale*404,oy-scale*116); renderer.triangle

                    (ox+scale*320,oy-scale*80,ox+scale*404,oy-scale*116,ox+scale*404,oy-scale*104);
/*end of head group*/
            }
            else if(text=="Start Practise Game" )
            {
                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-340*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);
/*play**************************************************************************************/
                renderer.setColor(primaryColour);
                renderer.triangle(ox+scale*440,oy-scale*240,ox+scale*264,oy-scale*344,ox+scale*264,oy-scale*140);

            }
            else if(text=="Start Hiscore Game" )
            {

                ox = (int)centreX;
                oy = (int)centreY;
                ox=(int)(ox-340*scale);
                oy=(int)(oy+480*scale);
                oy=(int)(oy+0*scale);
/*play**************************************************************************************/
                renderer.setColor(primaryColour);
                renderer.triangle(ox+scale*440,oy-scale*240,ox+scale*264,oy-scale*344,ox+scale*264,oy-scale*140);

            }
            else if(text=="Exit" )
        {
            ox = (int)centreX;
            oy = (int)centreY;
            ox=(int)(ox-250*scale);
            oy=(int)(oy+480*scale);
            oy=(int)(oy+20*scale);
/*exit*******************************************************************************/
            renderer.setColor(tertiaryColour); renderer.triangle(ox+scale*382,oy-scale*150,ox+scale*410,oy-scale*178,ox+scale*258,oy-scale*330); renderer.triangle

                (ox+scale*382,oy-scale*150,ox+scale*258,oy-scale*330,ox+scale*230,oy-scale*302); renderer.triangle

                (ox+scale*230,oy-scale*178,ox+scale*258,oy-scale*150,ox+scale*410,oy-scale*302); renderer.triangle

                (ox+scale*230,oy-scale*178,ox+scale*410,oy-scale*302,ox+scale*382,oy-scale*330);

        }



            else if(text=="Sign out" )
        {

            ox = (int)centreX;
            oy = (int)centreY;
            ox=(int)(ox-380*scale);
            oy=(int)(oy+480*scale);
            oy=(int)(oy+50*scale);
/*head 2 group *****************************************************************************/
//offset y by -120 to centre this 

            renderer.setColor(primaryColour); renderer.triangle(ox+scale*380,oy-scale*380,ox+scale*260,oy-scale*380,ox+scale*320,oy-scale*296); renderer.setColor(secondaryColour);

            renderer.triangle(ox+scale*320,oy-scale*300,ox+scale*292,oy-scale*292,ox+scale*272,oy-scale*272); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*272,oy-scale*272,ox+scale*260,oy-scale*244); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*260,oy-scale*244,ox+scale*264,oy-scale*216); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*264,oy-scale*216,ox+scale*284,oy-scale*192); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*284,oy-scale*192,ox+scale*316,oy-scale*180); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*316,oy-scale*180,ox+scale*324,oy-scale*180); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*324,oy-scale*180,ox+scale*356,oy-scale*192); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*356,oy-scale*192,ox+scale*376,oy-scale*216); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*376,oy-scale*216,ox+scale*380,oy-scale*244); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*380,oy-scale*244,ox+scale*368,oy-scale*272); renderer.triangle

                (ox+scale*320,oy-scale*300,ox+scale*368,oy-scale*272,ox+scale*348,oy-scale*292);



            ox = (int)centreX;
            oy = (int)centreY;
            ox=(int)(ox-90*scale);
            oy=(int)(oy+480*scale); 
            oy=(int)(oy+120*scale);

            
//right arrow

//offset x by +90 to centre 
            renderer.setColor(tertiaryColour);
            renderer.triangle(ox+scale*260,oy-scale*240,ox+scale*236,oy-scale*272,ox+scale*236,oy-scale*256); renderer.triangle

                (ox+scale*260,oy-scale*240,ox+scale*236,oy-scale*256,ox+scale*200,oy-scale*256); renderer.triangle

                (ox+scale*260,oy-scale*240,ox+scale*200,oy-scale*256,ox+scale*200,oy-scale*224); renderer.triangle

                (ox+scale*260,oy-scale*240,ox+scale*200,oy-scale*224,ox+scale*236,oy-scale*224); renderer.triangle

                (ox+scale*260,oy-scale*240,ox+scale*236,oy-scale*224,ox+scale*236,oy-scale*208);
            


            ox = (int)centreX;
            oy = (int)centreY;
            ox=(int)(ox-280*scale);
            oy=(int)(oy+480*scale); 
            oy=(int)(oy-50*scale);
//wifi
//offset y by +92 to centre this 
            renderer.setColor(primaryColour); renderer.triangle(ox+scale*320,oy-scale*188,ox+scale*292,oy-scale*196,ox+scale*292,oy-scale*200); renderer.triangle

                (ox+scale*320,oy-scale*188,ox+scale*292,oy-scale*200,ox+scale*320,oy-scale*196); renderer.triangle

                (ox+scale*320,oy-scale*188,ox+scale*320,oy-scale*196,ox+scale*348,oy-scale*200); renderer.triangle

                (ox+scale*320,oy-scale*188,ox+scale*348,oy-scale*200,ox+scale*348,oy-scale*196); renderer.triangle

                (ox+scale*320,oy-scale*132,ox+scale*264,oy-scale*148,ox+scale*264,oy-scale*156); renderer.triangle

                (ox+scale*320,oy-scale*132,ox+scale*264,oy-scale*156,ox+scale*320,oy-scale*144); renderer.triangle

                (ox+scale*320,oy-scale*132,ox+scale*320,oy-scale*144,ox+scale*376,oy-scale*156); renderer.triangle

                (ox+scale*320,oy-scale*132,ox+scale*376,oy-scale*156,ox+scale*376,oy-scale*148); renderer.triangle

                (ox+scale*320,oy-scale*80,ox+scale*236,oy-scale*104,ox+scale*236,oy-scale*116); renderer.triangle(ox+scale*320,oy-scale*80,ox+scale*236,oy-scale*116,ox+scale*320,oy-scale*96);

            renderer.triangle(ox+scale*320,oy-scale*80,ox+scale*320,oy-scale*96,ox+scale*404,oy-scale*116); renderer.triangle

                (ox+scale*320,oy-scale*80,ox+scale*404,oy-scale*116,ox+scale*404,oy-scale*104);
/*end of head 2 group*/
        }




        }//end if



    }





            }