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

    float waggleCounter=0f;
    boolean waggle= false;
    boolean waggleDirection= false;
    boolean circle= false;
    boolean circleDirection =false;
    boolean idle = false;
    Random rand = new Random();
    float idleTimer =0f;
    float waggleTimer =0f;
    float circleTimer=0f;
    float circleAngleCounter=0f;

    public Bee(float x, float y)
    {
        posx = x;
        posy = y;
        addTarget(0, 0);
        addTarget(800, 100);
        addTarget(400, 1000);
        setTargetAngle();
    }

    public void addTarget(float x, float y)
    {
        target.add(new Vector2(x, y));
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
                angleDiff = angleDiff * dx + 0.01f;
            } else
            {
                angleDiff = angleDiffConstant * dx;
            }
            double diff = angle - targetAngle;
            angleDirection = angleDiff;
            if (Math.abs(diff) > Math.PI)
            {
                angleDirection = -angleDiff;
            }

            if (diff + 0.3 < 0.6)//if angle is close to correct, speed up
            {
                speedUp(dx);
            }//end if angle is close to correct, speed up
            else//if angle is not close to correct then slow down
            {

                slowDown(dx);
            }
                if (diff + 0.01 < 0.02)//if angle is very close to correct then set angle to the correct angle
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
                        angle = angle + angleDirection;//turn clockwise


                    } else
                    {
                        angle = angle - angleDirection;
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
        if(!idle)
        {
            if (target.size() > 0)
            {
                timer = timer + 6 * speedMod * dx;
                sway = speedConstant / 2 * (float) Math.sin(timer);
                if (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y) < slowDownRadius * slowDownRadius)
                {
                    slowDown(dx);
                    if (dist(posx, target.get(target.size() - 1).x, posy, target.get(target.size() - 1).y) < targetRadius * targetRadius)
                    {
                        //posx = target.get(target.size() - 1).x;
                        //posy = target.get(target.size() - 1).y;
                        target.remove(target.size() - 1);
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
        idleTimer = (float)10+rand.nextInt(20);
        circleTimer= (float)5+rand.nextInt(2);
    }
    private void idle(float dx)
    {
        if(idle)
        {

            circle(dx);
            waggle(dx);
            if(circleTimer<=0)
            {
                if(circle)
                {
                    circle=false;
                    circleTimer= (float)5+rand.nextInt(2);
                }
                else
                {
                    circleTimer= (float)5+rand.nextInt(2);
                    circle=true;
                    if(rand.nextInt(2)==1)
                    {
                        circleDirection =true;
                    }
                    else
                    {
                        circleDirection =false;
                    }
                }
            }


            if(idleTimer+waggleTimer+circleTimer <=0)
            {
                idle=false;
            }
            else
            {
                if(idleTimer>0)
                {
                    idleTimer = idleTimer - dx;
                }
                if(waggleTimer>0)
                {
                    waggleTimer = waggleTimer -dx;
                }
                else
                {
                    waggleTimer=1*rand.nextFloat();
                            waggle=!waggle;
                }
                if(circleTimer>0)
                {
                    circleTimer = circleTimer - dx;
                }
            }
        }
        else
        {
            idle=false;
        }
    }

    private void circle(float dx)
    {
        if(circle)
        {

            if(circleDirection )
            {
                angle+=Math.PI*dx;
                circleAngleCounter+=Math.PI*dx;
            }
            else
            {
                angle-=Math.PI*dx;
                circleAngleCounter+=Math.PI*dx;
            }
            if (angle < 0)
            {
                angle = Math.PI * 2;
            }
            if (angle > Math.PI * 2)
            {
                angle = 0;
            }

            posx += speedConstant * speedMod * dx * Math.cos(angle) ;
            posy -= speedConstant * speedMod * dx * Math.sin(angle) ;
            if(circleAngleCounter>2*Math.PI)
            {
                circleAngleCounter=0;
                circle=false;
                circleTimer=(float)5+rand.nextInt(2);

            }
        }
    }

    private void waggle(float dx)
    {
        if(waggle)
        {

            if(waggleCounter<1f)
            {
                if(waggleDirection)
                {
                    angle = angle- 0.1f;
                }
                else
                {
                    angle = angle+ 0.1f;
                }

                waggleCounter+=dx;
//toggle waggleDirection
                waggleDirection=!waggleDirection;
            }
            else
            {
                waggleCounter=0f;
                waggle=false;
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
        if(visible)
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
            font.draw(sb, "waggletimer " + waggleTimer, posx + 12, posy + 140);
            font.draw(sb, "circletimer " + circleTimer, posx + 12, posy + 160);
            font.draw(sb, "wagglecounter " + waggleCounter, posx + 12, posy + 180);
            font.draw(sb, "circleanglecounter " + circleAngleCounter, posx + 12, posy + 200);


        }

    }

    private int dist(float x1, float x2, float y1, float y2)
    {
        return (int) ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
