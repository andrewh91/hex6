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
public int noOfColumns,noOfRows ,noOfHexes,posX,  posY, marginX,  marginY, edgeSizeInt,width, height;

HexWide hexWideArray[];

        public HexWideField(int posX, int posY, int width, int height, int noOfRows, int noOfColumns, int gameMode,final GameStage gs, final Database db) {
               // if singles game mode
               if(gameMode==1)
               {
                       this.edgeSize = deriveEdgeSize(width, height, 2, 1);
               }
               else
               {
                       this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);


               }
                this.noOfColumns = noOfColumns;
                this.noOfRows = noOfRows;
                this.posX = posX;
                this.posY = posY;
                this.width=width;
                this.height=height;
                edgeSizeInt=(int)edgeSize;
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
this.marginX=marginX;
                this.marginY=marginY;

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

        public ArrayList<Integer> getIsolated(int hex, int hex2,int noOfColumns, int noOfRows)
        {

                ArrayList<Integer> adjacentArray = new ArrayList<Integer>();
                ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();

                ArrayList<Integer> adjacentArray2 = new ArrayList<Integer>();
                ArrayList<Integer> isolatedArray = new ArrayList<Integer>();

//add all the indexes of the hexes that are adjacent to both the selected hexes to a list
                adjacentArray= getAdjacent(hex, noOfColumns, noOfRows);
                adjacentArray.addAll(getAdjacent(hex2, noOfColumns, noOfRows));
//set each hex in the array as being adjacent to the match. remove any that are selected or not visible 
                for(int i =0;i<adjacentArray.size();i++)
                {


                        if(hexWideArray[adjacentArray.get(i)].select ||
                                !hexWideArray[adjacentArray.get(i)].visible )
                        {
                                toBeRemoved.add(adjacentArray.get(i));
                        }

                }// end i for loop 

                adjacentArray.removeAll(toBeRemoved);
//adjacentArray contains all adjacents of the 2 selected hexes excluding selected hexes and hexes that are not visible 

                toBeRemoved.clear();

//so we have a list of hexes that were adjacent to either of the two selected hexes and that are not selected themselves and are visible, for each of these count how many hexes are adjacent and are not selected or not visible  
                for(int j = 0; j<adjacentArray.size();j++)
                {
                        adjacentArray2= getAdjacent(adjacentArray.get(j), noOfColumns, noOfRows);
//remove any that are selected or not visible or are adjacent to the selected hexes
                        for(int i =0;i<adjacentArray2.size();i++)
                        {
                                if(hexWideArray[adjacentArray2.get(i)].select ||
                                        !hexWideArray[adjacentArray2.get(i)].visible)
                                {
                                        toBeRemoved.add(adjacentArray2.get(i));

                                }
                        }// end i for loop 
                        adjacentArray2.removeAll(toBeRemoved);
                        toBeRemoved.clear();
//if the hex that was adjacent to the selected hex has no adjacents that are visible and not selected and not adjacent to the selected then add it to the list
                        if(adjacentArray2.size()==0)
                        {
                                isolatedArray.add(adjacentArray.get(j));
                        }
                }//end j for loop

                return isolatedArray;
        }//end method



        /**
         * For use in Singles Game Mode when a match is found, we want the camera to go to
         * the next pair of hexes. This will calculate the x and y position of  the next 2 hexes,
         * considering if we are in portrait or landscape, the size of the hexes etc
         * This method might also be useful for snapping to and zooming in on pairs that the
         * user selects in game modes other than singles.
         */
        int[] getNextHexPairCoords( int firstHex,
                                   int secondHex)
        {
                int[] pos1;
                int[] pos2;
                int[] pos3;
//get the x and y pos of the first hex
                pos1 = getHexCoords( edgeSizeInt, firstHex, noOfRows, noOfColumns,
                        posX, posY, marginX,marginY);
//get the x and y pos of the second hex
                pos2 = getHexCoords( edgeSizeInt, secondHex, noOfRows, noOfColumns,
                        posX, posY, marginX,marginY);

//now find the point inbetween the 2 positions 
                int x= (pos1[0] + pos2[0])/2;
                int y= (pos1[1] + pos2[1])/2;

                pos3 =new int[] {x,y};
                return pos3;

//need to start the zoom logic

        }//end of method 

        float getNextHexZoom(int firstHex, int secondHex)
        {
                int[] pos1;
                int[] pos2;
               pos1 = getHexCoords( edgeSizeInt, secondHex, noOfRows, noOfColumns,
                       posX, posY, marginX,marginY);

                pos2 = getHexCoords( edgeSizeInt, firstHex, noOfRows, noOfColumns,
                        posX, posY, marginX,marginY);

                //considering we use wide hexes, get the width and the height of the 2 hexes together
                int pairHeight = Math.abs(pos1[1]-pos2[1])+(int)(edgeSize*0.866025403784439*2);
                int pairWidth = Math.abs(pos1[0]-pos2[0])+(int)(edgeSize*2);
                //see which one is greater in percent of available space and return it

                float h =(float)(pairHeight)/(float)(height);
                float w = (float)(pairWidth)/(float)(width);
                if(h<w)
                {
                        return w;
                }
                else
                {
                        return h;
                }
        }
        float getAdjacentZoom(int firstHex)
{
        ArrayList<Integer> adjacentArray = new ArrayList<Integer>();
//2d array to hold the x and y coords of each hex
        int[][] posArray= {
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0}
        };
//get all the hexes adjacent to the given hex
        adjacentArray =getAdjacent(firstHex,noOfColumns, noOfRows);
        adjacentArray.add(firstHex);

        int lowX=0;
        int lowY=0;
        int highX=0;
        int highY=0;

//store the coords of each adjacent hex in the posArray, there might not be 6 adjacents, but adjacentArray.size gives how many adjacents there are
        for(int i = 0; i<adjacentArray.size();i++)
        {
                if(hexWideArray[adjacentArray.get(i)].visible) {

                        posArray[i] = getHexCoords(edgeSizeInt, adjacentArray.get(i), noOfRows, noOfColumns,
                                posX, posY, marginX, marginY);
//if current x is lowest record it in lowX variable ; if x is lower or equal to the first x or if x is lower than the currently recorded x set the lowX to x. lowX starts off as 0, so the x will never be lower, so we just have a condition to set lowX if the x is the first x
                        if (lowX==0 || posArray[i][0] <= lowX) {
                                lowX = posArray[i][0];
                        }
//if the x is highest on record, record it
                        if (posArray[i][0] >= highX) {
                                highX = posArray[i][0];
                        }

//if the y is lowest on record, record it
                        if (lowY==0 || posArray[i][1] <= lowY) {
                                lowY = posArray[i][1];
                        }
//if the y is highest on record, record it
                        if (posArray[i][1] >= highY) {
                                highY = posArray[i][1];
                        }
                }
        }
//work out the width of all the hexes together, the lowX and highX show the coords of the centre of the leftmost and rightmost hex, so to get total width we need to factor in how wide each hex is
        float adjacentsWidth= highX-lowX+edgeSize*2;
        float adjacentsHeight=(float)( highY-lowY+edgeSize*2*0.866025403784439);

        //see which one is greater in percent of available space and return it

        float h =(float)(adjacentsHeight )/(float)(height);
        float w = (float)(adjacentsWidth )/(float)(width);
        if(h<w)
        {
                return w;
        }
        else
        {
                return h;
        }
}

        int[] getAdjacentCoords(int firstHex)
        {
                ArrayList<Integer> adjacentArray = new ArrayList<Integer>();
//2d array to hold the x and y coords of each hex 

                int[][] posArray= {
                        {0, 0},
                        {0, 0},
                        {0, 0},
                        {0, 0},
                        {0, 0},
                        {0, 0},
                        {0, 0}
                };
//get all the hexes adjacent to the given hex
                adjacentArray =getAdjacent(firstHex,noOfColumns, noOfRows);
                adjacentArray.add(firstHex);

                int meanX=0;
                int meanY=0;
                int noOfVisible=0;
                int[] pos;
//store the coords of each adjacent hex in the posArray, there might not be 6 adjacents, but adjacentArray.size gives how many adjacents there are
                for(int i = 0; i<adjacentArray.size();i++)
                {
                        if(hexWideArray[adjacentArray.get(i)].visible) {

                        posArray[i] = getHexCoords(edgeSizeInt, adjacentArray.get(i), noOfRows, noOfColumns,
                                posX, posY, marginX, marginY);
                        meanX += posArray[i][0];
                        meanY += posArray[i][1];
                        noOfVisible++;
                }
                }
                if(noOfVisible==0){noOfVisible=1;}
                meanX = (int)(meanX/noOfVisible);
                meanY = (int)(meanY/noOfVisible);

                pos= new int[] {meanX,meanY};
                return pos;
        }
        int[] getHexCoords( int edgeSize, int index, int noOfRows,
                           int noOfColumns, int posX, int posY, int marginX, int marginY)
        {
                int[] pos = {0,0};
/* first of all turn the index number of the hex back into i and j coords which indicate
* it's row and column in the field respectively
* portrait mode, hexes start bottom left then move right, go back to start and up one
* then right again etc
* the hexes will have been given indexes from 0 to noOfRows*noOfColumns-1, the
* following turns that index number back into the number in the row and column
*/

                        int i=(int)(index/noOfColumns);
                        int j=index%noOfColumns;

//now calculate the position of the given hex
                        int x = (int)(posX+marginX+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize);
                        int y = (int)(posY+marginY-(edgeSize*0.866025403784439)+(edgeSize*0.866025403784439*2)*(i+1)
                                +(edgeSize*0.866025403784439)*(j%2));
                        pos = new int[]{x,y};



                return pos;
        }




}



