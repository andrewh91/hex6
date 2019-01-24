package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOptionField
{
    public int noOfColumns,noOfRows ,noOfHexes,posX,  posY,
            edgeSize,width, height;
    HexOption hexOptionArray[];
    int fieldIndex;

    public HexOptionField(int posX, int posY, int width, int height, int noOfRows,
                          int noOfColumns, int index, OptionsHandler optionsHandler) {
        this.noOfColumns = noOfColumns;
        this.noOfRows = noOfRows;
        this.posX = posX;
        this.posY = posY;
        this.width=width;
        this.height=height;
        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
        fieldIndex=index;
        hexOptionArray = new HexOption[noOfColumns * noOfRows];

        for(int i=0;i<noOfRows;i++)
        {
            for(int j=0;j<noOfColumns;j++)
            {
                hexOptionArray[noOfHexes] = new HexOption(edgeSize,
                        (float)(posX+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize),
                        (float)(posY-(edgeSize*0.866025403784439)+(edgeSize*0.866025403784439*2)*(i+1)+(edgeSize*0.866025403784439)*(j%2)),
                        noOfHexes,fieldIndex,optionsHandler);
                noOfHexes++;
            }
        }
    }//end constructor

    public int deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns) {//there are two methods of deriving the edgeSize based on the aove arguments, we need whichever method gives the smallest edgeSzie as this will guarentee the hexes will be contained in the given space.
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
        return (int) edgeSize;
    }

    public void draw(ShapeRenderer sr)
    {
        for (int i = 0;i<noOfColumns*noOfRows;i++)
        {
            hexOptionArray[i].draw(sr);
        }
    }
}//end class