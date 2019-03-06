package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Andrew Hughes on 06/03/2019.
 */

public class ScoreboardRow
{
    GlyphLayout glyphLayout = new GlyphLayout();
    String dateTime;
    String noOfHexes;
    String score;
    int width;
    BitmapFont font;
    int x1,x2,x3;
    int margin,  width1,  width2,  width3,  height;

    public ScoreboardRow(BitmapFont font)
    {
        this.font = font;
        x1=0;
        x2=0;
        x3=0;
        margin=0;
    }

    public void setText(String text1, String text2, String text3)
    {
        dateTime=text1;
        noOfHexes=text2;
        score=text3;
    }
    public int getTextWidth(String text)
    {
        glyphLayout.setText(font,text);
        return (int)glyphLayout.width;
    }
    public int getTotalWidth()
    {
        width = getTextWidth(dateTime)+
                getTextWidth(noOfHexes)+
                getTextWidth(score);

        return width;
    }

    public void setValues( int margin, int width1, int width2, int width3, int height)
    {
        this.margin=margin;
        this.width1=width1;
        this.width2=width2;
        this.width3=width3;
        this.height =height;
        x1=margin+(width1/2);
        x2=margin+width1+(width2/2);
        x3=margin+width1+width2+(width3/2);
    }

    public void draw(SpriteBatch sb)
    {
//outside this class we need a method that takes all the widths of all the 
//instances of this class and decides which width is the largest, then we will 
//feed that largest width into the draw method for all instances
        font.draw(sb, ""+dateTime, x1,height);
        font.draw(sb, ""+noOfHexes, x2,height);
        font.draw(sb, ""+score, x3,height);
    }
}

