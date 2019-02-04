package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOptionField
{
    public int noOfColumns,noOfRows ,noOfHexes,posX,  posY,
            edgeSize,width, height,noOfOptions;
    HexOption hexOptionArray[];
    OptionsHandler optionsHandler;
    int fieldIndex;
    boolean portrait;
    public HexOptionField(int posX, int posY, int width, int height,int noOfOptions, int noOfRows,
                          int noOfColumns, int index, OptionsHandler optionsHandler, boolean portrait) {
        this.noOfColumns = noOfColumns;
        this.noOfRows = noOfRows;
        this.posX = posX;
        this.posY = posY;
        this.width=width;
        this.noOfOptions = noOfOptions;
        this.height=height;
        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
        fieldIndex=index;
        this.optionsHandler=optionsHandler;
        this.portrait=portrait;

        /*
        for(int i=0;i<noOfColumns;i++)
        {
            for(int j=0;j<noOfRows;j++)
            {
                hexOptionArray[noOfHexes] = new HexOption(edgeSize,
                        (float)(posX+(0.5*edgeSize+((j+1)*1.5*edgeSize))-edgeSize),
                        (float)(posY-(edgeSize*0.866025403784439)+(edgeSize*0.866025403784439*2)*(i+1)+(edgeSize*0.866025403784439)*(j%2)),
                        noOfHexes,fieldIndex,optionsHandler);
                noOfHexes++;
            }
        }
*/

        setupOptions(noOfOptions);
    }//end constructor

    public int deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns) {//there are two methods of deriving the edgeSize based on the aove arguments, we need whichever method gives the smallest edgeSzie as this will guarentee the hexes will be contained in the given space.
        //float averageHexWidth=width/noOfColumns;//work out the *average* width of each hex given the size of the area they occupy horizontally and the number of them there are- bear in mind they will overlap

if(portrait) {
    int tempwidth = width, tempnoOfRows = noOfRows;
    width = height;
    height = tempwidth;
    noOfRows = noOfColumns;
    noOfColumns = tempnoOfRows;
}
        float edgeSize = (float) (1 / (0.5 / height + (noOfRows * 1.5) / height));//first method to work out the hex edge size
        float hexWidth;
        if (noOfRows > 1) {
            hexWidth = (float) (width / (noOfColumns + 0.5));//work out the height of each hex given the size of the area they occupy vertically and how many there are
        } else {
            hexWidth = width / noOfColumns;//work out the height of each hex given the size of the area they occupy vertically and how many there are

        }

        if (edgeSize > hexWidth / 0.866025403784439 / 2)//if the other method produces a smaller edgeSize then use that value
        {//
            edgeSize = (float) (hexWidth / 0.866025403784439 / 2);
        }
        return (int) edgeSize;
    }
public void setupOptions(int noOfOptions )
{
    if(portrait)
    {
        switch (noOfOptions) {
            case 1:
                noOfRows=1;
                noOfColumns=1;
                break;
            case 2:
                noOfRows=2;
                noOfColumns=1;
                break;
            case 3:
                noOfRows=2;
                noOfColumns=2;
                break;
            case 4:
                noOfRows=2;
                noOfColumns=2;
                break;
            case 5:
                noOfRows=3;
                noOfColumns=2;
                break;
            case 6:
                noOfRows=3;
                noOfColumns=2;
                break;
            case 7:
                noOfRows=3;
                noOfColumns=3;
                break;
            case 8:
                noOfRows=3;
                noOfColumns=3;
                break;
            case 9:
                noOfRows=3;
                noOfColumns=3;
                break;
            case 10:
                noOfRows=4;
                noOfColumns=3;
                break;
            case 11:
                noOfRows=4;
                noOfColumns=3;
                break;
            case 12:
                noOfRows=4;
                noOfColumns=3;
                break;
            case 13:
                noOfRows=5;
                noOfColumns=3;
                break;
            case 14:
                noOfRows=5;
                noOfColumns=3;
                break;
            case 15:
                noOfRows=5;
                noOfColumns=3;
                break;
        }

//for hexwide hexes, adjacent hexes will be 0.75 of a hex width to the right 
        float hexSpacingX = 1.5f*edgeSize;
//for hexwide hexes, adjacent hexes will be a hex height above  
        float hexSpacingY = 2*edgeSize*0.866025403784439f;
        //depending on the column, the hex might be offset in the y axis by an altitude size
        float hexOffsetY = edgeSize*0.866025403784439f;

//the width of the field will be 2*edgeSize if there is only one column
//the height of the field will also change if there is only one column
        float fieldWidth,fieldHeight;

        if(noOfColumns==1)
        {
             fieldWidth = edgeSize*2;
             fieldHeight = edgeSize*0.866025403784439f*2;
        }
        else
        {
             fieldWidth = edgeSize*2+noOfColumns * hexSpacingX;
             fieldHeight = edgeSize*0.866025403784439f*2*noOfRows+hexOffsetY ;
        }

        float fieldOffsetX = (posX+width)/2-fieldWidth/2-edgeSize;
        float fieldOffsetY = (posY+height)/2-fieldHeight/2-edgeSize*0.866025403784439f;
        hexOptionArray = new HexOption[noOfColumns*noOfRows];
//can use this to draw them normally need to figure out a way to selectively draw patterns
        noOfHexes=0;
        for(int i=0;i<noOfColumns;i++)
        {
            for(int j=0;j<noOfRows;j++)
            {
                hexOptionArray[noOfHexes] = new HexOption(edgeSize,fieldOffsetX+hexSpacingX*j,
                        fieldOffsetY+hexSpacingY*i+hexOffsetY*(j%2),noOfHexes,fieldIndex
                        ,optionsHandler);
                noOfHexes++;
            }
        }
    }


    else //if not portrait
    {
        switch (noOfOptions) {
            case 1:
                noOfRows=1;
                noOfColumns=1;
                break;
            case 2:
                noOfRows=1;
                noOfColumns=2;
                break;
            case 3:
                noOfRows=1;
                noOfColumns=3;
                break;
            case 4:
                noOfRows=2;
                noOfColumns=3;
                break;
            case 5:
                noOfRows=2;
                noOfColumns=3;
                break;
            case 6:
                noOfRows=2;
                noOfColumns=3;
                break;
            case 7:
                noOfRows=2;
                noOfColumns=4;
                break;
            case 8:
                noOfRows=2;
                noOfColumns=4;
                break;
            case 9:
                noOfRows=2;
                noOfColumns=5;
                break;
            case 10:
                noOfRows=2;
                noOfColumns=5;
                break;
            case 11:
                noOfRows=3;
                noOfColumns=4;
                break;
            case 12:
                noOfRows=3;
                noOfColumns=4;
                break;
            case 13:
                noOfRows=3;
                noOfColumns=5;
                break;
            case 14:
                noOfRows=3;
                noOfColumns=5;
                break;
            case 15:
                noOfRows=3;
                noOfColumns=5;
                break;
            case 16:
                noOfRows=3;
                noOfColumns=6;
                break;
            case 17:
                noOfRows=3;
                noOfColumns=6;
                break;
            case 18:
                noOfRows=3;
                noOfColumns=6;
                break;
            case 19:
                noOfRows=3;
                noOfColumns=7;
                break;
            case 20:
                noOfRows=3;
                noOfColumns=7;
                break;
            case 21:
                noOfRows=3;
                noOfColumns=7;
                break;
        }
    }

}
    public void draw(ShapeRenderer sr)
    {
        for (int i = 0;i<noOfOptions;i++)
        {
            hexOptionArray[i].draw(sr);
        }
    }
}//end class