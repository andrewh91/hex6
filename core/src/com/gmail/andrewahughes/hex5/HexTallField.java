package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 02/10/2018.
 */

public class HexTallField
{
    public float edgeSize;
    public int noOfColumns,noOfRows ,noOfHexes;

    HexTall hexTallArray[];

    public HexTallField(int posX, int posY, int width, int height, int noOfRows, int noOfColumns,final GameStage gs, final Database db) {
        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
        this.noOfColumns = noOfColumns;
        this.noOfRows = noOfRows;
        hexTallArray = new HexTall[noOfColumns * noOfRows];
//unless the specified argument result in the ideal ratio of number of hexes across and down then the hex field will be offset in either the x or y axis, record that to counteract it
        int marginY = (int) ((height- (0.5 * edgeSize + (noOfColumns * 1.5 * edgeSize))) / 2);
        int marginX;
        if (noOfRows > 1) {
            marginX = (int) ((width- (edgeSize * (noOfRows+0.5) * 0.866025403784439 * 2)) / 2);

        } else
        {
            marginX = (int)((width- (edgeSize*noOfRows*0.866025403784439*2))/2);

        }


        noOfHexes=0;
//create a field of hexes starting from the bottom left, moving up across the rows then back to the far left and right one then start again
        for(int i=0;i<noOfColumns;i++)
        {
            for(int j=0;j<noOfRows;j++)
            {
                hexTallArray[noOfHexes] = new HexTall(edgeSize,(float)(posX+marginX-(edgeSize*0.866025403784439)+(edgeSize*0.866025403784439*2)*(i+1)+(edgeSize*0.866025403784439)*(j%2)),
                        (float)(posY+marginY+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize),noOfHexes,gs,db);
                noOfHexes++;
            }
        }

    }
    public HexTallField(int posX, int posY, int width, int height, float edgeSize)
    {
        this.edgeSize=edgeSize;
    }
    public void draw(ShapeRenderer sr)
    {
        for (int i = 0;i<noOfColumns*noOfRows;i++)
        {
            hexTallArray[i].draw(sr);
        }
    }
    public int getNoOfHexes()
    {
        return noOfHexes;
    }


    public void drawSprites(SpriteBatch sb)
    {
        for (int i = 0;i<noOfColumns*noOfRows;i++)
        {
            hexTallArray[i].drawSprites(sb);
        }
    }
    public float deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns) {//there are two methods of deriving the edgeSize based on the above arguments, we need whichever method gives the smallest edgeSize as this will guarantee the hexes will be contained in the given space.
        //float averageHexHeight=height/noOfRows;//work out the *average* width of each hex given the size of the area they occupy horizontally and the number of them there are- bear in mind they will overlap
        float edgeSize = (float) (1 / (0.5 / height+ (noOfRows * 1.5) / height));//first method to work out the hex edge size
        float hexWidth;
        if (noOfRows > 1) {
            hexWidth = (float) (width / (noOfColumns + 0.5));//work out the height of each hex given the size of the area they occupy vertically and how many there are
        } else {
            hexWidth = width/ noOfColumns;//work out the height of each hex given the size of the area they occupy vertically and how many there are

        }

        if (edgeSize > hexWidth / 0.866025403784439 / 2)//if the other method produces a smaller edgeSize then use that value
        {//
            edgeSize = (float) (hexWidth / 0.866025403784439 / 2);
        }
        return edgeSize;
    }
    //#resumefrom here
    public ArrayList<Integer> getAdjacent(int hex, int noOfColumns, int noOfRows)
    {
        int totalHexes = noOfColumns*noOfRows;
        ArrayList<Integer> adjacentArray = new ArrayList<Integer>();
        int i=0;

//if hex is in an even column (the left most column is 0 which is even)
        if((hex%noOfColumns)%2==0)
        {
//if not on the top edge
            if(hex+noOfColumns<totalHexes)
            {
//add hex above
                adjacentArray.add(hex+noOfColumns);
                i++;
            }
//if not on left edge
            if(hex%noOfColumns>0)
            {
//add hex above left
                adjacentArray.add(hex-1);
                i++;
            }
//if not on the right edge
            if(hex%noOfColumns<noOfColumns-1)
            {
//add hex above and to the right
                adjacentArray.add(hex+1);
                i++;
            }

//if not on bottom edge
            if(hex-noOfColumns>=0)
            {
//add hex below
                adjacentArray.add(hex-noOfColumns);
                i++;

//if not on left edge
                if(hex%noOfColumns>0)
                {
//add hex below left
                    adjacentArray.add(hex-1-noOfColumns);
                    i++;
                }
//if not on the right edge
                if(hex%noOfColumns<noOfColumns-1)
                {
//add below right hex
                    adjacentArray.add(hex+1-noOfColumns);
                    i++;
                }

            }//end if not on bottom
        }//end even

//if hex is in an odd column
        else
        {
//if not on the top edge
            if(hex+noOfColumns<totalHexes)
            {
//add hex above
                adjacentArray.add(hex+noOfColumns);
                i++;
                //if not on left edge
                if(hex%noOfColumns>0)
                {
//add hex above left
                    adjacentArray.add(hex-1+noOfColumns);
                    i++;
                }
//if not on the right edge
                if(hex%noOfColumns<noOfColumns-1)
                {
//add hex above and to the right
                    adjacentArray.add(hex+1+noOfColumns);
                    i++;
                }
            }//end if not on top

//if not on bottom edge
            if(hex-noOfColumns>=0)
            {
//add hex below
                adjacentArray.add(hex-noOfColumns);
                i++;
            }

//if not on left edge
            if(hex%noOfColumns>0)
            {
//add hex below left

                adjacentArray.add(hex-1);
                i++;
            }
//if not on the right edge
            if(hex%noOfColumns<noOfColumns-1)
            {
//add below right hex
                adjacentArray.add(hex+1);
                i++;
            }
        }//end odd
        return adjacentArray;
    }//end method







    public void highlightAdjacent(ArrayList<Integer> array)
    {
        for(int i =0; i<array.size();i++)
        {
            hexTallArray[array.get(i)].highlight(80);
        }
    }

    public void unhighlightAdjacent(ArrayList<Integer> array)
    {
        for(int i =0; i<array.size();i++)
        {
            hexTallArray[array.get(i)].unhighlight(0);
        }
    }







}
