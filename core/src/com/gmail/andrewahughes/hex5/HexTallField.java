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
        int marginY = (int) ((height- (0.5 * edgeSize + (noOfRows * 1.5 * edgeSize))) / 2);
        int marginX;
        if (noOfRows > 1) {
            marginX = (int) ((width- (edgeSize * (noOfColumns+0.5) * 0.866025403784439 * 2)) / 2);

        } else
        {
            marginX = (int)((width- (edgeSize*noOfColumns*0.866025403784439*2))/2);

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
    public ArrayList<Integer> getAdjacent(int index, int noOfColumns, int noOfRows)
    {
        int totalHexes = noOfColumns*noOfRows;
        ArrayList<Integer> adjacentArray = new ArrayList<Integer>();
        int i=0;

//if not top 
        if( index % noOfRows < noOfRows-1)
        {
//if even
            if ((index % noOfRows) % 2 == 0)
            {
//if not left
                if(index >=noOfRows)
                {
//select top left 
                    adjacentArray.add(index -noOfRows+ 1);
                    i++;
//end if not left
                }
//select top right//we can do this even if we are on the right edge
                adjacentArray.add(index+1);
                i++;
//end if even
            }
//else if odd
            else  if ((index % noOfRows) % 2 == 1)
            {
//if not right
                if(index < totalHexes-noOfRows)
                {
//select top right
                    adjacentArray.add(index +noOfRows+1);
                    i++;
//end if not right
                }
//select top left
                adjacentArray.add(index+ 1);
                i++;
//end else if odd
            }
//end if not top
        }
//if not bottom 
        if(index % noOfRows > 0)
        {
//if odd
            if ((index % noOfRows) % 2 == 1)
            {
//if not right 
                if(index < totalHexes-noOfRows )
                {
//select bottom right
                    adjacentArray.add(index +noOfRows- 1);
                    i++;
//end if not right
                }
//select bottom left
                adjacentArray.add(index - 1);
                i++;
//end if odd
            }
//else if even
            else if ((index % noOfRows) % 2 == 0)
            {
//if not left
                if(index >=noOfRows )
                {
//select bottom left
                    adjacentArray.add( index - noOfRows - 1);
                    i++;
//end if not left
                }
//select bottom right
                adjacentArray.add(  index -  1);
                i++;
//end if even
            }
//end if not bottom 
        }

//if not left
        if (index >=noOfRows)
        {
//select left
            adjacentArray.add(  index -  noOfRows);
            i++;
//end if not left
        }
//if not right 
        if(index < totalHexes-noOfRows )
        {
//select right 
            adjacentArray.add(  index +  noOfRows);
            i++;
//end if not right
        }

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
