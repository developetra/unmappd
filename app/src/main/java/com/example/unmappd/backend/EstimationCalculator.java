package com.example.unmappd.backend;

import android.location.Location;

import com.example.unmappd.data.Landmark;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * EstimationCalculator - This class calculates the estimated position of the player by using the information of distances to four landmarks.
 *
 * @author Franziska Barckmann, Ann-Kathrin Schmid, Petra Langenbacher
 */
public class EstimationCalculator {

    private static final double THRESHOLD = 0.01;
    private static final double MAX_ITERATIONS = 100;
    private static final double baseLong = 10;
    private static final double baseLat = 49;

    /**
     * This method adapts the information for the estimation calculation and then calls the calculation method.
     * @param playerPosition as Location
     * @param landmarkList as ArrayList of Landmarks
     * @param distanceList as ArrayList of Integer
     * @return estimation location as double[]
     */
    public static double[] calculateEstimation(Location playerPosition, ArrayList<Landmark> landmarkList, ArrayList<Integer> distanceList) {

        //double [] playerPos = {getOffset(playerPosition.getLongitude()), getOffset(playerPosition.getLatitude())};
        double [] playerPos = {getOffset(10.88572), getOffset(49.89259)};
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

    /**
     * This method takes four positions and four distances to those positions and calculates a resulting estimated position.
     * @param playerPos as double[]
     * @param distances as int[]
     * @param landmarkArray as int[][]
     * @return estimated position as double[][]
     */
    public static double[] calculateEstimationWithOffset(double[] playerPos, int[] distances, int[][] landmarkArray){

        int counter = 0;

        double [] estimation = playerPos; // initiated with player position
        int [] guesses = distances;
        int [][] landmarks = landmarkArray;


        SimpleMatrix residualV;
        SimpleMatrix designM;
        SimpleMatrix correctionV;

        double correctionVLength = Double.MAX_VALUE; // set initial value to highest value so that loop always executes

        while(correctionVLength >= THRESHOLD && counter < MAX_ITERATIONS) {

            residualV = computeResVec(guesses, landmarks, estimation);
            designM = computeDesignMatrix(landmarks, estimation);

            correctionV = computeCorrectionVector(residualV, designM);

            correctionVLength = calcVecLength(correctionV);

            System.out.println(correctionVLength);

            estimation[0] = estimation[0] + correctionV.get(0);
            estimation[1] = estimation[1] + correctionV.get(1);

            System.out.println();

            counter++;
        }

        return estimation;
    }

    /**
     * This method computes the offset of a Lat/Long value by discarding the base (all digits left of the comma).
     * @param value as double
     * @return offset as int of 5 digits
     */
    public static int getOffset(double value) {

        // Get offset from Lat / Long value
        int offset = (int) Math.round(((value) % 1) * 100000);

        System.out.println(offset);

        return offset;

    }

    /**
     * This method turns an array of offset values to an array of Lat/Long values by adding the bases.
     * @param estimation as double[]
     * @return estimation location as double[]
     */
    public static double[] getLocationFromEstimationArray(double[] estimation) {

        double longitude = baseLong + estimation[0] * 0.00001;
        double latitude = baseLat + estimation[1] * 0.00001;

        double[] estimationLoc = {longitude, latitude};

        System.out.println(longitude);
        System.out.println(latitude);

        return estimationLoc;
    }

    /**
     * This method calculates the length of a vector.
     * @param correctionV as SimpleMatrix
     * @return vector length as double
     */
    private static double calcVecLength(SimpleMatrix correctionV) {

        return Math.sqrt(Math.pow(correctionV.get(0), 2) + Math.pow(correctionV.get(1), 2));
    }

    /**
     * This method computes the correction vector as orthogonal projection of the residual
     * vector onto the vector space described by the design matrix.
     * @param residualV as SimpleMatrix
     * @param designM as SimpleMatrix
     * @return correction vector as Simple Matrix.
     */
    private static SimpleMatrix computeCorrectionVector(SimpleMatrix residualV, SimpleMatrix designM) {

        SimpleMatrix correctionV = designM.mult(designM.transpose()).invert().mult(designM).mult(residualV);

        return correctionV;
    }

    /**
     * This method computes the design matrix.
     * @param landmarks as int[][]
     * @param estimation as double[]
     * @return design matrix as SimpleMatrix
     */
    private static SimpleMatrix computeDesignMatrix(int[][] landmarks, double[] estimation) {

        double [][] designMData = {{0,0,0,0},{0,0,0,0}};

        int x = 0;
        int y= 1;

        for(int i = 0; i < landmarks.length; i ++){

            double dmX = (estimation[x] - landmarks[i][x]) /
                    ( Math.sqrt(
                            Math.pow((landmarks[i][x] - estimation[x]),2) + Math.pow((landmarks[i][y] - estimation[y]),2)
                    ));

            designMData[x][i] = dmX;


            double dmY = (estimation[y] - landmarks[i][y]) /
                    ( Math.sqrt(
                            Math.pow((landmarks[i][x] - estimation[x]),2) + Math.pow((landmarks[i][y] - estimation[y]),2)
                    ));

            designMData[y][i] = dmY;
        }

        // put design matrix data in matrix
        return new SimpleMatrix(designMData);
    }

    /**
     * This method computes the residual vector (error of the distance guess).
     * @param guesses as int[]
     * @param landmarks as int[][]
     * @param estimation as double[]
     * @return residual vector as Simple Matrix
     */
    private static SimpleMatrix computeResVec(int[] guesses, int[][] landmarks, double[] estimation) {

        double residualVData [][] = {{0},{0},{0},{0}};

        for(int i = 0; i < residualVData.length; i++){
            residualVData[i][0] = guesses[i]
                    - Math.sqrt(Math.pow(landmarks[i][0] - estimation[0], 2) + Math.pow(landmarks[i][1] - estimation[1],2));
        }

        // put residual data in matrix
        SimpleMatrix residualV = new SimpleMatrix(residualVData);

        return residualV;
    }


}
