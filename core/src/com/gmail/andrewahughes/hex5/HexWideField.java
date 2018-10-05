package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Andrew Hughes on 02/10/2018.
 */

public class HexWideField
{
public float edgeSize;
public int noOfColumns,noOfRows ;
HexWide hexWideArray[];

        public HexWideField(int posX, int posY, int width, int height,int noOfRows, int noOfColumns)
        {
        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
        this.noOfColumns=noOfColumns;
        this.noOfRows=noOfRows;
         hexWideArray= new HexWide[noOfColumns*noOfRows];
//unless the specified argument result in the ideal ratio of number of hexes across and down then the hex feild will be offset in either the x or y axis, record that to counteract it
        int marginX = (int)((width - (0.5*edgeSize+(noOfColumns*1.5*edgeSize)))/2);
        int marginY = (int)((height - (edgeSize*noOfRows*0.866025403784439*2))/2);
        int noOfHexes=0;
//create a field of hexes starting from the bottom left, moving right across the columns the back to the far left and up one then start again
        for(int i=0;i<noOfColumns;i++)
        {
        for(int j=0;j<noOfRows;j++)
        {
        hexWideArray[noOfHexes] = new HexWide(edgeSize,(float)(posX+marginX+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize),(float)(posY+marginY+(edgeSize*0.866025403784439*2)*(i+1)+(edgeSize*0.866025403784439)*(j%2)));
        noOfHexes++;
        }
        }

        }
public HexWideField(int posX, int posY, int width, int height, float edgeSize)
{
        this.edgeSize=edgeSize;
}
public void draw(ShapeRenderer sr)
{
        for (int i = 0;i<noOfColumns*noOfRows;i++)
        {
                hexWideArray[i].draw(sr);
        }
}
public float deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns)
{//there are two methods of deriving the edgeSize based on the aove arguments, we need whichever method gives the smallest edgeSzie as this will guarentee the hexes will be contained in the given space.
        float averageHexWidth=width/noOfColumns;//work out the *average* width of each hex given the size of the area they occupy horizontally and the number of them there are- bear in mind they will overlap
        float edgeSize = (float)(1/(0.5/averageHexWidth+(noOfColumns*1.5)/averageHexWidth));//first method to work out the hex edge size
        float hexHeight=height/noOfRows;//work out the height of each hex given the size of the area they occupy vertically and how many there are

        if(edgeSize>hexHeight/0.866025403784439/2)//if the other method produces a smaller edgeSize then use that value
        {//
        edgeSize=(float)(hexHeight/0.866025403784439/2);
        }
        return edgeSize;
        }
}


