package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by Andrew Hughes on 02/10/2018.
 */

public class HexWideField
{
public float edgeSize;
public int noOfColumns,noOfRows ,noOfHexes;

HexWide hexWideArray[];

        public HexWideField(int posX, int posY, int width, int height, int noOfRows, int noOfColumns,final GameStage gs, final Database db) {
                this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
                this.noOfColumns = noOfColumns;
                this.noOfRows = noOfRows;
                hexWideArray = new HexWide[noOfColumns * noOfRows];
//unless the specified argument result in the ideal ratio of number of hexes across and down then the hex feild will be offset in either the x or y axis, record that to counteract it
                int marginX = (int) ((width - (0.5 * edgeSize + (noOfColumns * 1.5 * edgeSize))) / 2);
                int marginY;
                if (noOfColumns > 1) {
                        marginY = (int) ((height - (edgeSize * (noOfRows+0.5) * 0.866025403784439 * 2)) / 2);

                } else
                {
                         marginY = (int)((height - (edgeSize*noOfRows*0.866025403784439*2))/2);

                }


         noOfHexes=0;
//create a field of hexes starting from the bottom left, moving right across the columns the back to the far left and up one then start again
        for(int i=0;i<noOfRows;i++)
        {
        for(int j=0;j<noOfColumns;j++)
        {
        hexWideArray[noOfHexes] = new HexWide(edgeSize,(float)(posX+marginX+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize),
                (float)(posY+marginY-(edgeSize*0.866025403784439)+(edgeSize*0.866025403784439*2)*(i+1)+(edgeSize*0.866025403784439)*(j%2)),noOfHexes,gs,db);
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
public int getNoOfHexes()
{
        return noOfHexes;
}


        public void drawSprites(SpriteBatch sb)
        {
                for (int i = 0;i<noOfColumns*noOfRows;i++)
                {
                        hexWideArray[i].drawSprites(sb);
                }
        }
public float deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns) {//there are two methods of deriving the edgeSize based on the aove arguments, we need whichever method gives the smallest edgeSzie as this will guarentee the hexes will be contained in the given space.
        //float averageHexWidth=width/noOfColumns;//work out the *average* width of each hex given the size of the area they occupy horizontally and the number of them there are- bear in mind they will overlap
        float edgeSize = (float) (1 / (0.5 / width + (noOfColumns * 1.5) / width));//first method to work out the hex edge size
        float hexHeight;
        if (noOfColumns > 1) {
                hexHeight = (float) (height / (noOfRows + 0.5));//work out the height of each hex given the size of the area they occupy vertically and how many there are
        } else {
                hexHeight = height / noOfRows;//work out the height of each hex given the size of the area they occupy vertically and how many there are

        }

        if (edgeSize > hexHeight / 0.866025403784439 / 2)//if the other method produces a smaller edgeSize then use that value
        {//
                edgeSize = (float) (hexHeight / 0.866025403784439 / 2);
        }
        return edgeSize;
}

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
                        hexWideArray[array.get(i)].highlight(80);
                }
        }

        public void unhighlightAdjacent(ArrayList<Integer> array)
        {
                for(int i =0; i<array.size();i++)
                {
                        hexWideArray[array.get(i)].unhighlight(0);
                }
        }







}



