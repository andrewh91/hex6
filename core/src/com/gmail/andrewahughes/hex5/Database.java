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

    public Database( int noOfSymbols)
    {
//we need exactly 31 symbols. if we have a set of more than 31 symbols we need to play without some
//assign each datum a random symbol
        assignRandomSymbol(noOfSymbols);
    }//end constructor

    public void assignRandomSymbol(int noOfSymbols)
    {
//list of symbol ids, from 0 to whatever noOfSymbols is
        ArrayList<Integer> symbolIds = new ArrayList<Integer>();
        for(int i = 0; i < noOfSymbols;i++)
        {
            symbolIds.add(i);
        }
//random ids, assign 31 random values - make sure no value is repeated
        ArrayList<Integer> randomIds = new ArrayList<Integer>();
        for(int i = 0; i < 31;i++)
        {
            int j;
            if(symbolIds.size()==1)
            {
                j=0;
            }
            else {
                j = rand.nextInt(symbolIds.size() - 1);//get random integer between 0 and the size of symbolIds -1
            }
            randomIds.add(symbolIds.get(j));//get a random index position of the symbols id and assign that value to the random list.
            symbolIds.remove(j);//remove the value at that random index position from the symbol list.

        }
//now assign them to the 2d array randomly
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

    }//end assignRandomSymbol

    public int[] getHex(int hexId)
    {
        int[] hex= new int[]{randData[hexId][0],randData[hexId][1],randData[hexId][2],randData[hexId][3],randData[hexId][4],randData[hexId][5]};
        return hex;
    }

    public int getSymbol(int hexId, int symbolsId)
    {
        return randData[hexId][symbolsId];
    }

    public boolean compareSymbols(int hexId1, int symbolsId1, int hexId2, int symbolsId2)
    {
        if(randData[hexId1][symbolsId1]==randData[hexId2][symbolsId2])
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}//end class




















