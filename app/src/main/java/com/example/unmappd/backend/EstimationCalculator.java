package com.example.unmappd.backend;

import android.location.Location;

import com.example.unmappd.data.Landmark;

import org.ejml.simple.SimpleMatrix;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * EstimationCalculator - This class calculates the estimated position of the player by using the information of distances to four landmarks.
 *
 * @author Franziska Barckmann, Ann-Kathrin Schmid, Petra Langenbacher
 */
public class EstimationCalculator {

    // TODO Andere Repräsentation Lat Long Werte (siehe Folien)
    // TODO Parameterübergabe guesses, landmarks, playerPos
    // TODO Parameterwerte in Arrays speichern

    private static final double MAX_DEVIATION = 0.001;

    public static void calculateEstimation(Location playerPosition, ArrayList<Landmark> landmarkList, ArrayList<Integer> distanceList) {
    //public static void main(String[] args){
        int [] guesses = {70,90,30,70};
        int [][] landmarks = {{-10,20},{100,30},{-20,-40},{120,-20}};
        double [] estimation = {0,0};


//        // TODO Refactoring
//        // Get offset from Lat / Long value
//        long base = Math.round(((12.118765 * 100) % 1) * 10000);
//        System.out.println(base);
//
//        // Get base from coordinates
//        double i = 12.118765 * 100;
//        int o = (int) i;
//        double p = o;
//        p = p / 100;
//        System.out.println(p);


        SimpleMatrix residualV;
        SimpleMatrix designM;
        SimpleMatrix correctionV;

        double correctionVLength = Double.MAX_VALUE; // set initial value to highest value so that loop always executes

        while(correctionVLength >= MAX_DEVIATION) {

            residualV = computeResDataMatrix(guesses, landmarks, estimation);
            designM = computeDesignMatrix(landmarks, estimation);

            correctionV = computeCorrectionVector(residualV, designM);

            correctionVLength = calcVecLength(correctionV);

            System.out.println(correctionVLength);

            estimation[0] = estimation[0] + correctionV.get(0);
            estimation[1] = estimation[1] + correctionV.get(1);

            System.out.println(estimation[0]);
            System.out.println(estimation[1]);
            System.out.println();
        }

    }

    private static double calcVecLength(SimpleMatrix correctionV) {

        return Math.sqrt(Math.pow(correctionV.get(0), 2) + Math.pow(correctionV.get(1), 2));
    }

    private static SimpleMatrix computeCorrectionVector(SimpleMatrix residualV, SimpleMatrix designM) {

        SimpleMatrix correctionV = designM.mult(designM.transpose()).invert().mult(designM).mult(residualV);

        return correctionV;
    }

    private static SimpleMatrix computeDesignMatrix(int[][] landmarks, double[] playerPos) {

        double [][] designMData = {{0,0,0,0},{0,0,0,0}};

        int x = 0;
        int y= 1;

        for(int i = 0; i < landmarks.length; i ++){

            double dmX = (playerPos[x] - landmarks[i][x]) /
                    ( Math.sqrt(
                            Math.pow((landmarks[i][x] - playerPos[x]),2) + Math.pow((landmarks[i][y] - playerPos[y]),2)
                    ));

            designMData[x][i] = dmX;


            double dmY = (playerPos[y] - landmarks[i][y]) /
                    ( Math.sqrt(
                            Math.pow((landmarks[i][x] - playerPos[x]),2) + Math.pow((landmarks[i][y] - playerPos[y]),2)
                    ));

            designMData[y][i] = dmY;
        }

        // put design matrix data in matrix
        return new SimpleMatrix(designMData);
    }

    private static SimpleMatrix computeResDataMatrix(int[] guesses, int[][] landmarks, double[] playerPos) {

        double residualVData [][] = {{0},{0},{0},{0}};

        for(int i = 0; i < residualVData.length; i++){
            residualVData[i][0] = guesses[i]
                    - Math.sqrt(Math.pow(landmarks[i][0] - playerPos[0], 2) + Math.pow(landmarks[i][1] - playerPos[1],2));
        }

        // put residual data in matrix
        SimpleMatrix residualV = new SimpleMatrix(residualVData);

        return residualV;
    }


}
