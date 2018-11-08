package com.gmail.andrewahughes.hex5;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrew Hughes on 05/11/2018.
 */



public class Database{

    Random rand = new Random();

    //hardcode data
    int[][] data= new int[][]{
            {1,	2,	3,	4,	5,	26},
            {6,	7,	8,	9,	10,	26},
            {11,	12,	13,	14,	15,	26},
            {16,	17,	18,	19,	20,	26},
            {21,	22,	23,	24,	25,	26},
            {1,	7,	13,	19,	25,	27},
            {5,	6,	12,	18,	24,	27},
            {4,	10,	11,	17,	23,	27},
            {3,	9,	15,	16,	22,	27},
            {2,	8,	14,	20,	21,	27},
            {1,	9,	12,	20,	23,	28},
            {3,	6,	14,	17,	25,	28},
            {5,	8,	11,	19,	22,	28},
            {2,	10,	13,	16,	24,	28},
            {4,	7,	15,	18,	21,	28},
            {1,	8,	15,	17,	24,	29},
            {4,	6,	13,	20,	22,	29},
            {2,	9,	11,	18,	25,	29},
            {5,	7,	14,	16,	23,	29},
            {3,	10,	12,	19,	21,	29},
            {1,	10,	14,	18,	22,	30},
            {2,	6,	15,	19,	23,	30},
            {3,	7,	11,	20,	24,	30},
            {4,	8,	12,	16,	25,	30},
            {5,	9,	13,	17,	21,	30},
            {1,	6,	11,	16,	21,	0},
            {2,	7,	12,	17,	22,	0},
            {3,	8,	13,	18,	23,	0},
            {4,	9,	14,	19,	24,	0},
            {5,	10,	15,	20,	25,	0},
            {26,	27,	28,	29,	30,	0}
    };


    int[][] randData;
    int r;
    int setOfSymbolIndex;
    ArrayList<Integer> refList= new ArrayList<Integer>();//stores a reference to the  index of the set of symbols each hex uses
    ArrayList<Integer> randomIds = new ArrayList<Integer>();
    ArrayList<Integer>[] symbolShuffle= new ArrayList[31];//stores a reference to the 31 different random orders the 6 symbols where shuffled into

    int noOfColumns,noOfRows,totalHexes;

    public Database( int noOfSymbols,int columns ,int rows)
    {
        noOfColumns=columns;
        noOfRows = rows;
        totalHexes=rows*columns;
//we need exactly 31 symbols. if we have a set of more than 31 symbols we need to play without some
//assign each datum a random symbol
        assignRandomSymbol(noOfSymbols);
    }//end constructor

    public void assignRandomSymbol(int noOfSymbols)
    {
        //list of number from 0 to30 sorted randomly
        randomIds=randomlySortedList(31);
//this is hardcoding the symbols so that all sets have one symbol in common with each other, the symbols are randomised but still maintain these
        //important characteristics of having exactly one symbol in common
      randData= new int[][]{
                {randomIds.get(1),	randomIds.get(2),	randomIds.get(3),	randomIds.get(4),	randomIds.get(5),	randomIds.get(26)},
                {randomIds.get(6),	randomIds.get(7),	randomIds.get(8),	randomIds.get(9),	randomIds.get(10),	randomIds.get(26)},
                {randomIds.get(11),	randomIds.get(12),	randomIds.get(13),	randomIds.get(14),	randomIds.get(15),	randomIds.get(26)},
                {randomIds.get(16),	randomIds.get(17),	randomIds.get(18),	randomIds.get(19),	randomIds.get(20),	randomIds.get(26)},
                {randomIds.get(21),	randomIds.get(22),	randomIds.get(23),	randomIds.get(24),	randomIds.get(25),	randomIds.get(26)},
                {randomIds.get(1),	randomIds.get(7),	randomIds.get(13),	randomIds.get(19),	randomIds.get(25),	randomIds.get(27)},
                {randomIds.get(5),	randomIds.get(6),	randomIds.get(12),	randomIds.get(18),	randomIds.get(24),	randomIds.get(27)},
                {randomIds.get(4),	randomIds.get(10),	randomIds.get(11),	randomIds.get(17),	randomIds.get(23),	randomIds.get(27)},
                {randomIds.get(3),	randomIds.get(9),	randomIds.get(15),	randomIds.get(16),	randomIds.get(22),	randomIds.get(27)},
                {randomIds.get(2),	randomIds.get(8),	randomIds.get(14),	randomIds.get(20),	randomIds.get(21),	randomIds.get(27)},
                {randomIds.get(1),	randomIds.get(9),	randomIds.get(12),	randomIds.get(20),	randomIds.get(23),	randomIds.get(28)},
                {randomIds.get(3),	randomIds.get(6),	randomIds.get(14),	randomIds.get(17),	randomIds.get(25),	randomIds.get(28)},
                {randomIds.get(5),	randomIds.get(8),	randomIds.get(11),	randomIds.get(19),	randomIds.get(22),	randomIds.get(28)},
                {randomIds.get(2),	randomIds.get(10),	randomIds.get(13),	randomIds.get(16),	randomIds.get(24),	randomIds.get(28)},
                {randomIds.get(4),	randomIds.get(7),	randomIds.get(15),	randomIds.get(18),	randomIds.get(21),	randomIds.get(28)},
                {randomIds.get(1),	randomIds.get(8),	randomIds.get(15),	randomIds.get(17),	randomIds.get(24),	randomIds.get(29)},
                {randomIds.get(4),	randomIds.get(6),	randomIds.get(13),	randomIds.get(20),	randomIds.get(22),	randomIds.get(29)},
                {randomIds.get(2),	randomIds.get(9),	randomIds.get(11),	randomIds.get(18),	randomIds.get(25),	randomIds.get(29)},
                {randomIds.get(5),	randomIds.get(7),	randomIds.get(14),	randomIds.get(16),	randomIds.get(23),	randomIds.get(29)},
                {randomIds.get(3),	randomIds.get(10),	randomIds.get(12),	randomIds.get(19),	randomIds.get(21),	randomIds.get(29)},
                {randomIds.get(1),	randomIds.get(10),	randomIds.get(14),	randomIds.get(18),	randomIds.get(22),	randomIds.get(30)},
                {randomIds.get(2),	randomIds.get(6),	randomIds.get(15),	randomIds.get(19),	randomIds.get(23),	randomIds.get(30)},
                {randomIds.get(3),	randomIds.get(7),	randomIds.get(11),	randomIds.get(20),	randomIds.get(24),	randomIds.get(30)},
                {randomIds.get(4),	randomIds.get(8),	randomIds.get(12),	randomIds.get(16),	randomIds.get(25),	randomIds.get(30)},
                {randomIds.get(5),	randomIds.get(9),	randomIds.get(13),	randomIds.get(17),	randomIds.get(21),	randomIds.get(30)},
                {randomIds.get(1),	randomIds.get(6),	randomIds.get(11),	randomIds.get(16),	randomIds.get(21),	randomIds.get(0)},
                {randomIds.get(2),	randomIds.get(7),	randomIds.get(12),	randomIds.get(17),	randomIds.get(22),	randomIds.get(0)},
                {randomIds.get(3),	randomIds.get(8),	randomIds.get(13),	randomIds.get(18),	randomIds.get(23),	randomIds.get(0)},
                {randomIds.get(4),	randomIds.get(9),	randomIds.get(14),	randomIds.get(19),	randomIds.get(24),	randomIds.get(0)},
                {randomIds.get(5),	randomIds.get(10),	randomIds.get(15),	randomIds.get(20),	randomIds.get(25),	randomIds.get(0)},
                {randomIds.get(26),	randomIds.get(27),	randomIds.get(28),	randomIds.get(29),	randomIds.get(30),	randomIds.get(0)}
        };

// basically creates a number from 0 to 30 randomly for each hex
        for(int i =0;i<totalHexes;i++)
        {setRefList(i,noOfColumns);}
//this is 31 item array of 6 item arraylists, this will allow me to alter the position of symbols so even 2 sets of identical symbols
// will at least appear in a different order
for(int j =0;j<31;j++)
{symbolShuffle[j] =randomlySortedList(6);}//this is purely to mix the symbols


    }//end assignRandomSymbol

    public int[] getHex(int hexId)
    {//gets the specified hex e.g. a set of 6 symbols, un randomises everything so we can get the correct result
        hexId = getRandFromIndex(hexId);

        int[] hex= new int[]{randData[hexId][symbolShuffle[hexId].get(0)],randData[hexId][symbolShuffle[hexId].get(1)],
                randData[hexId][symbolShuffle[hexId].get(2)],randData[hexId][symbolShuffle[hexId].get(3)],
                randData[hexId][symbolShuffle[hexId].get(4)],randData[hexId][symbolShuffle[hexId].get(5)]};
        return hex;
    }

    public int getSymbol(int hexId, int symbolsId)
    {//gets the specified symbol of the specified hex, un randomises everything so we can get the correct result
        hexId = getRandFromIndex(hexId);

        return randData[hexId][symbolShuffle[hexId].get(symbolsId)];
    }
    public int getRandFromIndex(int index)
            //used to get a random number that is tied to the hex index and is from 0 to 30
    {
        return refList.get(index);
    }

    public int setRefList(int index, int noOfColumns)
    {//the refList is a list of equal length to the number of hexes and contains a random number from 0 to 30 at each index position,
        // so you can use the index position of each hex to retrieve a random number - and we can use this random number
        // to choose which set of symbols to use. the method prevents adjacent hexes from having the same random number
        // and therefore the same set of symbols - because that would negatively effect gameplay
        if(index>30)//if the index is over 30 then we need to make sure we're not putting sets of symbols adjacent to identical sets of symbols
        {
            if(index<noOfColumns)//if still on the first row - which is unlikely if this is >31st hex but still possible.
            {
                setOfSymbolIndex=getUniqueSetOfSymbolsFirstRow(index);//use this recursive method to return a unique set of symbols which will not match the adjacent sets of symbols
            }
            else//if index > 30 but not on first row
            {
                setOfSymbolIndex=getUniqueSetOfSymbols(index,noOfColumns);
            }
        }
        else//if index is between 0 and 30 just return the random numbers in order(it's a random order)
        {


            setOfSymbolIndex= randomIds.get(index);
        }
        refList.add(index,setOfSymbolIndex);//add the random number to the ref list for easy retrieval later, the random number will coincidentally have the same index number as the hex's index number in the argument for this method, but just incase we will add it at the index position anyway
        return setOfSymbolIndex;
    }

    public int getUniqueSetOfSymbolsFirstRow(int index)
    {
        r = rand.nextInt(30);
        if(r!=refList.get(index-1))
        {
//continue
        }
        else//if the proposed symbols are not unique try again
        {
            r = getUniqueSetOfSymbolsFirstRow(index);
        }
        return r;//the known adjacents are unique return the random number
    }

    public int getUniqueSetOfSymbols(int index,int noOfColumns)
    {
        r = rand.nextInt(30);
        if((index%noOfColumns)%2==0)//if in even column, 0, 2,4 etc
        {// if the proposed random number does not match the random numbers of the adjacent hexes then continue, otherwise recursively make new
            // random numbers until one fits, there should be up to 4 adjacents that have already been assigned to if the hex is in the even column,
            // if it's in the left most column or the bottom row then we need to make sure we don't try and get those adjacents because you will get
            // index out of bounds. i have a whole method to deal with the bottom row, with the left edge i'm only concerned about the bottom left
            // not just left in general as that's the only one that would be out of bounds, if it's on the left middle then it will just return a
            // hex on the far right as an adjacent, and i don't care about that
            if(r!=refList.get(index-1)&&r!=refList.get(index-noOfColumns)&&r!=refList.get(index-noOfColumns+1)&&(index-noOfColumns==0||r!=refList.get(index-noOfColumns-1)))//if none of the adjacents' sets of symbols match the proposed set of symbols then continue
            {
//continue
            }
            else//if the proposed symbols are not unique try a new proposed set
            {
                r = getUniqueSetOfSymbols(index,noOfColumns);
            }
        }
else //if (odd)
        {
            if(r!=refList.get(index-1)&&r!=refList.get(index-noOfColumns))
            {
//continue
            }
            else//if the proposed symbols are not unique try again
            {
                r = getUniqueSetOfSymbols(index,noOfColumns);
            }
        }

        return r;//the known adjacents are unique return the random number
    }
    ArrayList<Integer> randomlySortedList(int limitExclusive)
    {
        ArrayList<Integer> numberList= new ArrayList<Integer>();
        for(int i = 0; i < limitExclusive;i++)
        {
            numberList.add(i);
        }
//random number, assign given amount of random numbers- make sure no value is repeated
        ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
        for(int i = 0; i < limitExclusive;i++) {
            int j;
            if (numberList.size() == 1) {
                j = 0;
            } else {
                j = rand.nextInt(numberList.size() - 1);//get random integer between 0 and limit - exclusive
            }
            randomNumbers.add(numberList.get(j));//get a random number from the list
            numberList.remove(j);//remove the value at that random index position from the number list
        }
//end up with numbers 0 to limit exclusive randomly sorted in a list
            return randomNumbers;

    }
    public boolean compareSymbols(int hexId1, int symbolsId1, int hexId2, int symbolsId2)
    {
        // its possible for arguments above to be negative, which would be bad
        if(hexId1<0||symbolsId1<0||hexId2<0||symbolsId2<0)
        {
            return false;
        }
        else {
            //needex to randomise the access to the database otherwise hex location becomes predictable. so now i have to unrandomise it to compare
            hexId1 = getRandFromIndex(hexId1);
            hexId2 = getRandFromIndex(hexId2);
//the symbols of each hex are also randomised,need to unrandomise that too
            if (randData[hexId1][symbolShuffle[hexId1].get(symbolsId1)] == randData[hexId2][symbolShuffle[hexId2].get(symbolsId2)]) {
                return true;
            } else {
                return false;
            }
        }
    }

}//end class




















