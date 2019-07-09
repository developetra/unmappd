package com.example.unmappd.backend;

import android.location.Location;
import android.util.Log;

import com.example.unmappd.data.Landmark;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * EstimationCalculator - This class calculates the estimated position of the player by using the information of distances to four landmarks.
 *
 * @author Franziska Barckmann, Ann-Kathrin Schmid, Petra Langenbacher
 */
public class EstimationCalculator {

    private static final double MAX_DEVIATION = 0.01;
    private static final double MAX_ITERATIONS = 100;
    private static final double baseLong = 10;
    private static final double baseLat = 49;

    public static double[] calculateEstimation(Location playerPosition, ArrayList<Landmark> landmarkList, ArrayList<Integer> distanceList) {

        double [] playerPos = {getOffset(playerPosition.getLongitude()), getOffset(playerPosition.getLatitude())};
        int[] distances = {distanceList.get(0), distanceList.get(1), distanceList.get(2), distanceList.get(3)};
        int [][] landmarks = {
                {getOffset(landmarkList.get(0).getLongitude()), getOffset(landmarkList.get(0).getLatitude())},
                {getOffset(landmarkList.get(1).getLongitude()), getOffset(landmarkList.get(1).getLatitude())},
                {getOffset(landmarkList.get(2).getLongitude()), getOffset(landmarkList.get(2).getLatitude())},
                {getOffset(landmarkList.get(3).getLongitude()), getOffset(landmarkList.get(3).getLatitude())},
        };

        double[] estimation = calculateEstimationWithOffset(playerPos, distances, landmarks);

        return getLocationFromEstimationArray(estimation);

    }

    public static double[] calculateEstimationWithOffset(double[] playerPos, int[] distances, int[][] landmarkArray){

        // long = x
        // lat = y

        int counter = 0;

        double [] estimation = playerPos; // initiated with player position
        int [] guesses = distances;
        int [][] landmarks = landmarkArray;


        SimpleMatrix residualV;
        SimpleMatrix designM;
        SimpleMatrix correctionV;

        double correctionVLength = Double.MAX_VALUE; // set initial value to highest value so that loop always executes

        while(correctionVLength >= MAX_DEVIATION && counter < MAX_ITERATIONS) {

            residualV = computeResDataMatrix(guesses, landmarks, estimation);
            designM = computeDesignMatrix(landmarks, estimation);

            correctionV = computeCorrectionVector(residualV, designM);

            correctionVLength = calcVecLength(correctionV);

            System.out.println(correctionVLength);

            estimation[0] = estimation[0] + correctionV.get(0);
            estimation[1] = estimation[1] + correctionV.get(1);

            Log.d("test", Double.toString(estimation[0]));
            Log.d("test", Double.toString(estimation[1]));
            System.out.println();

            counter++;
        }

        return estimation;
    }

    public static int getOffset(double value) {

        // Get offset from Lat / Long value
        int offset = (int) Math.round(((value) % 1) * 100000);

        System.out.println(offset);

        return offset;

    }

    public static double[] getLocationFromEstimationArray(double[] estimation) {

        double longitude = baseLong + estimation[0] * 0.00001;
        double latitude = baseLat + estimation[1] * 0.00001;

        double[] estimationLoc = {longitude, latitude};

        System.out.println(longitude);
        System.out.println(latitude);

        return estimationLoc;
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
