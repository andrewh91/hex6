package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrew Hughes on 23/04/2019.
 */

public class Bee
{
    BitmapFont font = new BitmapFont();
    float posx = 0, posy = 0;
    int radius = 5;
    float speedConstant = 50;
    float speedMod = 0.5f;
    double angle = Math.PI;
    double angleDiffConstant = 0.5f;
    double angleDiff = angleDiffConstant;
    double targetAngle = angle;
    double angleDirection;
    //increase rate of turn if you get this close to the target, to prevent endless looping
    int angleHelperRadius = 300;
    int targetRadius = 2;
    int slowDownRadius = 100;
    ArrayList<Vector2> target = new ArrayList<Vector2>();
    boolean visible = false;
    float sway = 0.0f;
    float timer = 0.0f;

    boolean idle = false;
    boolean pause = false;
    Random rand = new Random();
    float idleTimer =0f;
    float pauseTimer =0f;

    FlowerPosList flowerPosList;
Vector2 lastPos;

    public Bee(float x, float y)
    {
        posx = x;
        posy = y;
        lastPos = new Vector2(x,y);
        //addTarget(400, 1000);
    }

    public void addTarget(float x, float y)
    {
        lastPos=new Vector2(x, y);
        target.add(lastPos);
        setTargetAngle();
    }

    public void addNewTarget()
    {
        lastPos=flowerPosList.getRand();

        target.add(lastPos);
        setTargetAngle();
    }
    public void setTargetAngle()
    {
        if (target.size() > 0)
        {
            targetAngle = ((Math.atan2(-target.get(target.size() - 1).x + posx, -target.get(target.size() - 1).y + posy) + Math.PI / 2)+(Math.PI*2))%(Math.PI*2);
        //need target angle to range from 0 to 2PI, the % is actually the remainder in java, not modulus, so you need to add 2PI to ensure positive numbers
        }
    }

    public void updateAngle(float dx)
    {
        setTargetAngle();
        if (target.size() > 0)
        {
            if (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y) < angleHelperRadius * angleHelperRadius)
            {
                angleDiff = angleDiff  + 0.01f;
            } else
            {
                angleDiff = angleDiffConstant ;
            }
            double diff = angle - targetAngle;
            angleDirection = angleDiff;
            if (Math.abs(diff) > Math.PI)
            {
                angleDirection = -angleDiff;
            }

            if (diff  < 0.3 && diff >0.3)//if angle is close to correct, speed up
            {
                speedUp(dx);
            }//end if angle is close to correct, speed up
            else//if angle is not close to correct then slow down
            {

                slowDown(dx);
            }
                if (diff  < 0.01&& diff > -0.01)//if angle is very close to correct then set angle to the correct angle
                {

                    angle = targetAngle;
                } else

                {
                    if (angle < 0)
                    {
                        angle = Math.PI * 2;
                    }
                    if (angle > Math.PI * 2)
                    {
                        angle = 0;
                    }
                    if (diff < 0)//if target clockwise
                    {
                        angle = angle + angleDirection * dx;//turn clockwise


                    } else
                    {
                        angle = angle - angleDirection * dx;
                    }
                }


        }
    }

    public void speedUp(float dx)
    {
        if (speedMod < 1f)
        {
            speedMod += 0.1* dx;
        } else
        {
            speedMod = 1f;
        }
    }

    public void slowDown(float dx)
    {
        if (speedMod > 0.5f)
        {
            speedMod -= 0.3f * dx;
        } else
        {
            speedMod = 0.5f;
        }
    }

    public void updatePos(float dx)
    {
if(!pause)
{
    if (target.size() > 0)
    {
        timer = timer + 6 * speedMod * dx;
        sway = speedConstant * speedMod * (float) Math.sin(timer);
        if (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y) < slowDownRadius * slowDownRadius)
        {
            slowDown(dx);
            if (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y) < targetRadius * targetRadius)
            {
                //posx = target.get(target.size() - 1).x;
                //posy = target.get(target.size() - 1).y;
                target.remove(target.size() - 1);
                angleDiff = angleDiffConstant;
                beginIdle();
            }
        }


        updateAngle(dx);
        posx += speedConstant * speedMod * dx * Math.cos(angle) + sway * speedMod * dx * Math.cos(angle + Math.PI / 2);
        posy -= speedConstant * speedMod * dx * Math.sin(angle) + sway * speedMod * dx * Math.sin(angle + Math.PI / 2);

    }
}
    }
    private void beginIdle()
    {
        idle=true;
        pause=true;
        if(idleTimer<=0)
        {
            idleTimer = (float) 10 + rand.nextInt(20);

        }
    }
    private void idle(float dx)
    {
        if(idle)
        {

            if(pauseTimer<=0)
            {
pause=!pause;
if(pause)
{
    addTarget((float)(posx-(20*Math.cos(angle))),(float)(posy-(20*Math.sin(angle))));

}
else
{
    addTarget(lastPos.x +rand.nextInt(20)-10,lastPos.y+ rand.nextInt(20)-10);
}
pauseTimer = (float) 4 + rand.nextInt(5);
            }


            if(idleTimer <=0)
            {
                idle=false;
                pause= false;
                addNewTarget();
            }
            else
            {
                if(idleTimer>0)
                {
                    idleTimer = idleTimer - dx;
                    if(pauseTimer>0)
                    {
                        pauseTimer = pauseTimer - dx;
                    }


                }
            }
        }
    }

    private void drawBee(ShapeRenderer sr)
    {
        double dist = radius / 3.5f;
        sr.setColor(1.00f, 1.00f, 1.00f, 1);
        sr.circle((float) (posx + 2*dist * Math.cos(angle - 3 * Math.PI / 4) + 2*dist * Math.sin(angle - 3 * Math.PI / 4)), (float) (posy + 2*dist * Math.cos(angle - 3 * Math.PI / 4) - 2*dist * Math.sin(angle - 3 * Math.PI / 4)), radius);
        sr.setColor(0.00f, 0.00f, 0.00f, 1);
        sr.circle((float) (posx + dist * Math.cos(angle - 3 * Math.PI / 4) + dist * Math.sin(angle - 3 * Math.PI / 4)), (float) (posy + dist * Math.cos(angle - 3 * Math.PI / 4) - dist * Math.sin(angle - 3 * Math.PI / 4)), radius);
        sr.setColor(1.00f, 0.75f, 0.06f, 1);
        sr.circle(posx, posy, radius);

    }

    public void drawFilled(ShapeRenderer sr, float dx)
    {
        if (visible)
        {
            drawBee(sr);
            updatePos(dx);
            idle(dx);
        }
    }

    public void drawSprites(SpriteBatch sb)
    {
        if(false)
        {
            font.draw(sb, "x " + (int) posx, posx + 12, posy);
            font.draw(sb, " y " + (int) posy, posx + 12, posy + 20);
            font.draw(sb, " angle " + (int) (angle * 180 / Math.PI), posx + 12, posy + 40);
            if (target.size() > 1)
            {
                font.draw(sb, "dist " + (int) (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y)), posx + 12, posy + 60);
            }
            font.draw(sb, "target angle " + (int) (targetAngle * 180 / Math.PI), posx, posy + 80);
            font.draw(sb, "speed " + speedMod, posx + 12, posy + 100);
            font.draw(sb, "idletimer " + idleTimer, posx + 12, posy + 120);
            font.draw(sb, "pausetimer " + pauseTimer, posx + 12, posy + 140);


        }

    }

    private int dist(float x1, float x2, float y1, float y2)
    {
        return (int) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
