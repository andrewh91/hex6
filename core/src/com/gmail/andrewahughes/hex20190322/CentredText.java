package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Andrew Hughes on 06/03/2019.
 */
public class CentredText
        {
        String text = new String();
        int x,y;
        BitmapFont font = new BitmapFont();
        GlyphLayout glyphLayout = new GlyphLayout();

public CentredText(String text)
        {
        this.text = text;
        }
        void centreText(int screenWidth, int y)
        {
        glyphLayout.setText(font,text);
        x=(int)(screenWidth/2- glyphLayout.width/2);
        this.y=y;
        }
public void draw(SpriteBatch sb)
        {
        font.draw(sb,text,x,y);
        }
        }