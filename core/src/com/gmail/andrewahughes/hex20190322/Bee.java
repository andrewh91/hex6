package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 23/04/2019.
 */

public class Bee
{
BitmapFont font = new BitmapFont();
    float posx =0, posy=0;
    int radius =5;
    float speed =50;
    double angle=Math.PI;
    double angleDiffConstant =0.5f;
    double angleDiff=angleDiffConstant;
    double targetAngle=angle;
    double angleDirection;
    //increase rate of turn if you get this close to the target, to prevent endless looping
    int angleHelperRadius=100;
    int targetRadius = 10;
    ArrayList<Vector2> target = new ArrayList<Vector2>();
    boolean visible = true;



    public Bee(float x, float y)
    {
posx =x;
posy=y;
addTarget(0,0);
addTarget(800,100);
        addTarget(400,1000);
setTargetAngle();
    }
    public  void addTarget(float x, float y)
    {
        target.add(new Vector2(x,y));
    }
    public void setTargetAngle()
    {
        if(target.size()>0)
        {
            targetAngle=Math.atan2(-target.get(target.size()-1).x+posx,-target.get(target.size()-1).y+posy)+Math.PI;
        }
    }
    public void updateAngle(float dx)
    {
        setTargetAngle();
        if(target.size()>0)
        {
            if (dist(posx, target.get(target.size()-1).x, posy, target.get(target.size()-1).y) < angleHelperRadius*angleHelperRadius)
            {
angleDiff=angleDiff*dx+0.1f;
            }
            else
            {
                angleDiff=angleDiffConstant*dx;
            }
            double diff = angle -targetAngle;
            angleDirection= angleDiff;
            if(Math.abs(diff)>Math.PI)
            {
                angleDirection=-angleDiff;
            }
            if(diff+0.1<0.2)
        {
            angle=targetAngle;
        }
            else if(diff<0)
            {

                    angle = angle + angleDirection;
                    if (angle > Math.PI * 2)
                    {
                        angle = 0;
                    }
                    if (angle < 0)
                    {
                        angle = Math.PI * 2;
                    }

            }
            else
            {
                angle=angle-angleDirection;
                if(angle<0)
                {
                    angle=Math.PI*2;
                }
                if(angle>Math.PI*2)
                {
                    angle=0;
                }
            }
        }
    }
    public void updatePos(float dx)
    {
        if(target.size()>0)
        {
            if (dist(posx, target.get(target.size()-1).x, posy, target.get(target.size()-1).y) < targetRadius*targetRadius)
            {
posx=target.get(target.size()-1).x;
posy=target.get(target.size()-1).y;
target.remove(target.size()-1);
            }
            else
            {
                updateAngle(dx);
                posx += speed * dx * Math.cos(angle);
                posy -= speed * dx * Math.sin(angle);
            }
        }
    }
private void drawBee(ShapeRenderer sr)
{
    double dist = radius /3.5f;
    sr.setColor(1.00f,1.00f,1.00f, 1);
    sr.circle((float)(posx+dist*Math.cos(angle-3*Math.PI/4)+dist*Math.sin(angle-3*Math.PI/4) ),(float)(posy+dist*Math.cos(angle-3*Math.PI/4)-dist*Math.sin(angle-3*Math.PI/4)),radius);
    sr.setColor(0.00f,0.00f,0.00f, 1);
    sr.circle(posx,posy,radius);
    sr.setColor(1.00f,0.75f,0.06f, 1);
    sr.circle((float)(posx-dist*Math.cos(angle-3*Math.PI/4)-dist*Math.sin(angle-3*Math.PI/4) ),(float)(posy-dist*Math.cos(angle-3*Math.PI/4)+dist*Math.sin(angle-3*Math.PI/4)),radius);
}
    public void drawFilled(ShapeRenderer sr,float dx)
    {
        if (visible) {
drawBee(sr);
updatePos(dx);
        }
    }

    public void drawSprites(SpriteBatch sb)
    {
        font.draw(sb,"x "+posx+" y "+posy+" angle " + angle,posx+12,posy);
    }
    private int dist(float x1, float x2, float y1, float y2)
    {
        return (int)((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
    }
}
