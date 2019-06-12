package com.example.unmappd.backend;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

public class EstimationCalculator {

    public static void main(String[] args) {

        int [] guesses = {70,90,30,70};
        int [][] landmarks = {{-10,20},{100,30},{-20,-40},{120,-20}};
        int [] playerPos = {0,0};


        // calculate residuals data
        double residualVData [][] = {{0},{0},{0},{0}};

        for(int i = 0; i < residualVData.length; i++){
            residualVData[i][0] = guesses[i]
                    - Math.sqrt(Math.pow(landmarks[i][0] - playerPos[0], 2) + Math.pow(landmarks[i][1] - playerPos[1],2));
        }

        // put residual data in matrix
        SimpleMatrix residualV = new SimpleMatrix(residualVData);

        residualV.print();

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
        SimpleMatrix designM = new SimpleMatrix(designMData);

        designM.print();

        // compute correction vector
        SimpleMatrix correctionV = designM.mult(designM.transpose()).invert().mult(designM).mult(residualV);
        correctionV.print();
    }
}
