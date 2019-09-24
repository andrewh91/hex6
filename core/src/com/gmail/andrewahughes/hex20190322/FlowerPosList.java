package com.gmail.andrewahughes.hex20190322;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrew Hughes on 01/07/2019.
 */

final class FlowerPosList
{
    static ArrayList<Vector2> list = new ArrayList<Vector2>();
    static ArrayList<Integer> index = new ArrayList<Integer>();
    static Random rand = new Random();
    private FlowerPosList()
    {
    }
    public static void add(float x, float y,int i)
    {
        list.add(new Vector2(x,y));
        index.add(i);
    }
    public static Vector2 get(int index)
    {
        if(index>=list.size())
        {
            index = index % list.size();
        }
        return list.get(index);
    }
    public static Vector2 getRandExcludingIndex(int index)
    {

            int a = rand.nextInt(list.size() );
            if (index == a)
            {
                a = (a + 1) % list.size();
            }

            return list.get(a);

    }
    public static Vector2 getRand()
    {
        return list.get(rand.nextInt(list.size()));
    }
    public static Vector2 getLast()
    {
        return list.get(list.size()-1);
    }
    public static Vector2 getSecondLast()
    {
        return list.get(list.size()-2);
    }
    public static int getLastIndex()
    {
        return list.size()-1;
    }
}
