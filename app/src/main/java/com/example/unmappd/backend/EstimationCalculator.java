package com.example.unmappd.backend;

import org.ejml.simple.SimpleMatrix;

public class EstimationCalculator {

    public static void calculateEstimation(String[] args) {

        int [] guesses = {70,90,30,70};
        int [][] landmarks = {{-10,20},{100,30},{-20,-40},{120,-20}};
        double [] playerPos = {0,0};

        SimpleMatrix residualV;
        SimpleMatrix designM;
        SimpleMatrix correctionV;

        double correctionVLength = 100; // set initial value so that while loop runs at least once

        while(correctionVLength >= 0.001) {

            residualV = computeResDataMatrix(guesses, landmarks, playerPos);
            designM = computeDesignMatrix(landmarks, playerPos);

            correctionV = computeCorrectionVector(residualV, designM);

            correctionVLength = (Math.sqrt(Math.pow(correctionV.get(0), 2) + Math.pow(correctionV.get(1), 2)));

            System.out.println(correctionVLength);

            playerPos[0] = playerPos[0] + correctionV.get(0);
            playerPos[1] = playerPos[1] + correctionV.get(1);

            System.out.println(playerPos[0]);
            System.out.println(playerPos[1]);
            System.out.println();
        }

    }

    private static SimpleMatrix computeCorrectionVector(SimpleMatrix residualV, SimpleMatrix designM) {
        // compute correction vector
        SimpleMatrix correctionV = designM.mult(designM.transpose()).invert().mult(designM).mult(residualV);

        return correctionV;
    }

    private static SimpleMatrix computeDesignMatrix(int[][] landmarks, double[] playerPos) {
        // calculate design matrix data
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
        // calculate residuals data
        double residualVData [][] = {{0},{0},{0},{0}};

        for(int i = 0; i < residualVData.length; i++){
            residualVData[i][0] = guesses[i]
                    - Math.sqrt(Math.pow(landmarks[i][0] - playerPos[0], 2) + Math.pow(landmarks[i][1] - playerPos[1],2));
        }

        // put residual data in matrix
        SimpleMatrix residualV = new SimpleMatrix(residualVData);

        // residualV.print();
        return residualV;
    }


}
