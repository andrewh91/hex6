package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Andrew Hughes on 24/01/2019.
 */

public class HexOptionField
{
    public int noOfColumns,noOfRows ,noOfHexes,
            edgeSize,width, height,noOfOptions;
    HexOption hexOptionArray[];
    HexOption hexBackgroundArray[];
    String[] optionTextArray;
    int[] ignore=new int[]{};
    OptionsHandler optionsHandler;
    int fieldIndex;
    boolean portrait;
    float fieldWidth,fieldHeight,fieldOffsetX,fieldOffsetY;
    BitmapFont font = new BitmapFont();
int selectedIndex;


    public HexOptionField(int width, int height,int noOfOptions,int index, OptionsHandler optionsHandler,
                          boolean portrait,String[] optionsText,int defaultOption) {
selectedIndex=defaultOption;
        this.width=width;
        this.noOfOptions = optionsText.length;
        this.height=height;
        //this must be called after we derive the noOfRows and Columns
        //this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);
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

        optionTextArray=setupOptionsText(optionsText);
        setupOptions(optionTextArray.length);
        for(int i =0;i<optionTextArray.length;i++)
        {
        asignOptionText(i,optionTextArray[i]);
        }
    }//end constructor

    public int deriveEdgeSize(int width, int height,int noOfRows, int noOfColumns) {//there are two methods of deriving the edgeSize based on the aove arguments, we need whichever method gives the smallest edgeSzie as this will guarentee the hexes will be contained in the given space.
        //float averageHexWidth=width/noOfColumns;//work out the *average* width of each hex given the size of the area they occupy horizontally and the number of them there are- bear in mind they will overlap


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
                ignore=new int[]{0,2};
                break;
            case 8:
                noOfRows=3;
                noOfColumns=3;
                ignore=new int[]{7};
                break;
            case 9:
                noOfRows=3;
                noOfColumns=3;
                break;
            case 10:
                noOfRows=4;
                noOfColumns=3;
                ignore=new int[]{0,2};
                break;
            case 11:
                noOfRows=4;
                noOfColumns=3;
                ignore=new int[]{10};
                break;
            case 12:
                noOfRows=4;
                noOfColumns=3;
                break;
            case 13:
                noOfRows=5;
                noOfColumns=3;
                ignore=new int[]{0,2};
                break;
            case 14:
                noOfRows=5;
                noOfColumns=3;
                ignore=new int[]{13};
                break;
            case 15:
                noOfRows=5;
                noOfColumns=3;
                break;
        }
        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);

//for hexwide hexes, adjacent hexes will be 0.75 of a hex width to the right 
        float hexSpacingX = 1.5f*edgeSize;
//for hexwide hexes, adjacent hexes will be a hex height above  
        float hexSpacingY = 2*edgeSize*0.866025403784439f;
        //depending on the column, the hex might be offset in the y axis by an altitude size
        float hexOffsetY = edgeSize*0.866025403784439f;

//the width of the field will be 2*edgeSize if there is only one column
//the height of the field will also change if there is only one column


        if(noOfColumns==1)
        {
             fieldWidth = edgeSize*2;
             fieldHeight = edgeSize*0.866025403784439f*2;
        }
        else
        {
             fieldWidth = edgeSize*2+(noOfColumns-1) * hexSpacingX;
             fieldHeight = edgeSize*0.866025403784439f*2*noOfRows+hexOffsetY ;
        }

//try this, i want it to produce an offset that will still overlap the background hexes, replace the current field offset values in hex option field class
//given the edge size that we already worked out, how many hexes do we need to more than fill the background?
        int noOfBackgroundColumns=(int)(width / (edgeSize*2));
        int noOfBackgroundRows=(int)(height / (edgeSize*0.866025403784439f*2));

        fieldOffsetX  = (int)((noOfBackgroundColumns-noOfColumns)/2+1)*(hexSpacingX) ;
        float backgroundOffsetY = ((int)((noOfBackgroundColumns-noOfColumns)/2+1)%2)*hexOffsetY;
        fieldOffsetY  = ((noOfBackgroundRows-noOfRows)/2+1)*(hexSpacingY);
        hexOptionArray = new HexOption[noOfColumns*noOfRows];
//can use this to draw them normally need to figure out a way to selectively draw patterns
        noOfHexes=0;
        int ignoreIndex=0,hexIndex=0;

        for(int i=0;i<noOfRows;i++)
        {
            for(int j=0;j<noOfColumns;j++)
            {
                if(ignoreIndex<ignore.length&&ignore[ignoreIndex]==hexIndex) {
                    ignoreIndex++;
                }
                else{
                    hexOptionArray[noOfHexes] = new HexOption(edgeSize,
                            fieldOffsetX + hexSpacingX * j,
                            fieldOffsetY + hexSpacingY * i + hexOffsetY * (j % 2), noOfHexes, fieldIndex
                            , optionsHandler);
                    noOfHexes++;
                }
                hexIndex++;
            }
        }


        hexBackgroundArray=new HexOption[(noOfBackgroundColumns+2)*(noOfBackgroundRows+3)];


        int noOfBackgroundHexes=0;
        for(int i=0;i<noOfBackgroundRows+3;i++)
        {
            for(int j=0;j<noOfBackgroundColumns+2;j++)
            {
                hexBackgroundArray[noOfBackgroundHexes] = new HexOption(edgeSize,
                        +hexSpacingX * j,
                        -2*hexSpacingY+hexSpacingY * i + hexOffsetY * (j % 2)+backgroundOffsetY, noOfHexes, fieldIndex
                        , optionsHandler);
                noOfBackgroundHexes++;
            }

        }



        /**************
         i should make a new argument of the hexoptionfield and hex option so that we make a more lightweight version that  can't be interacted with and it's only purpose is to draw the background *****/










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

        this.edgeSize = deriveEdgeSize(width, height, noOfRows, noOfColumns);

    }

}
    public void asignOptionText(int index, String text)
    {
        hexOptionArray[index].text = text;
    }

    public String[] setupOptionsText(String[] textArray )
    {
        noOfOptions=textArray.length;
        return textArray;
    }
public void disableOptions()
{
    for(int j = 0;j<hexBackgroundArray.length;j++)
    {
        hexBackgroundArray[j].disable();
    }
    for (int i = 0;i<noOfOptions;i++)
    {
        hexOptionArray[i].disable();
    }
}
    public void enableOptions()
    {
        for(int j = 0;j<hexBackgroundArray.length;j++)
    {
        hexBackgroundArray[j].enable();
    }
        for (int i = 0;i<noOfOptions;i++)
        {
            hexOptionArray[i].enable();
        }
    }

    public void setSelectedIndex(int a)
    {
        selectedIndex=a;
    }
    public void draw(ShapeRenderer sr)
    {

        for(int j = 0;j<hexBackgroundArray.length;j++)
        {
            hexBackgroundArray[j].draw(sr,true,false);
        }
        for (int i = 0;i<noOfOptions;i++)
        {
            if(i==selectedIndex)
            {
                hexOptionArray[i].draw(sr,false,true);

            }
            else
            hexOptionArray[i].draw(sr,false,false);
        }


    }
    public void drawText(SpriteBatch sb)
    {
        for (int i = 0;i<noOfOptions;i++)
        {
            hexOptionArray[i].drawText(sb);
        }
    }






}//end class








