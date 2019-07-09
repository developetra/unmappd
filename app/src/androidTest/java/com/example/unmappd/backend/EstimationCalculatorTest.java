package com.example.unmappd.backend;

import android.location.Location;

import com.example.unmappd.data.Landmark;

import org.junit.Test;

import java.lang.reflect.Executable;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class EstimationCalculatorTest {

    @Test
    public void calculateEstimation() {

        // given
        Location playerPosition = new Location("");
        playerPosition.setLongitude(10.88446);
        playerPosition.setLatitude(49.89168);

        Landmark l1 = new Landmark("Dom", 10.88300, 49.89071, "path");
        Landmark l2 = new Landmark("Residenz", 10.88186, 49.89184, "path");
        Landmark l3 = new Landmark("Elisabeth", 10.88305, 49.89271, "path");
        Landmark l4 = new Landmark("Brücke", 10.88695, 49.89142, "path");

        ArrayList<Landmark> landmarkList = new ArrayList<Landmark>();
        landmarkList.add(l1);
        landmarkList.add(l2);
        landmarkList.add(l3);
        landmarkList.add(l4);

        ArrayList<Integer> distanceList = new ArrayList<Integer>();
        distanceList.add(149);
        distanceList.add(183);
        distanceList.add(149);
        distanceList.add(183);

        double[] expected = {10.88446, 49.89168};

        // when, then
        assertEquals(expected[1], EstimationCalculator.calculateEstimation(playerPosition, landmarkList, distanceList)[1], 0.0001);
        //assertArrayEquals(expected, EstimationCalculator.calculateEstimation(playerPosition, landmarkList, distanceList), 0.0001);

    }

    @Test
    public void calculateEstimationLinearLandmarks() {

        // given
        Location playerPosition = new Location("");
        playerPosition.setLongitude(10.8807);
        playerPosition.setLatitude(49.8908);

        Landmark l1 = new Landmark("Dom", 10.8865525, 49.8914738, "path");
        Landmark l2 = new Landmark("Residenz", 10.8876731, 49.8925749, "path");
        Landmark l3 = new Landmark("Elisabeth", 10.8888355, 49.8931644, "path");
        Landmark l4 = new Landmark("Brücke", 10.8875033, 49.8925600, "path");

        ArrayList<Landmark> landmarkList = new ArrayList<Landmark>();
        landmarkList.add(l1);
        landmarkList.add(l2);
        landmarkList.add(l3);
        landmarkList.add(l4);

        ArrayList<Integer> distanceList = new ArrayList<Integer>();
        distanceList.add(429);
        distanceList.add(538);
        distanceList.add(641);
        distanceList.add(527);

        double[] expected = {10.8807, 49.8908};

        // when, then
        assertEquals(expected[1], EstimationCalculator.calculateEstimation(playerPosition, landmarkList, distanceList)[1], 0.0001);
        //assertArrayEquals(expected, EstimationCalculator.calculateEstimation(playerPosition, landmarkList, distanceList), 0.0001);

    }

    @Test
    public void calculateEstimationWithOffset() {

        // given
        double[] estimation = {0,0};
        int[] guesses = {70,90,30,70};
        int [][] landmarks = {{-10,20}, {100,30}, {-20,-40}, {120,-20}};
        double [] expected = {31.987284130589124, -33.145309242574974};

        assertArrayEquals(expected, EstimationCalculator.calculateEstimationWithOffset(estimation, guesses, landmarks),0.1);
    }

    @Test
    public void getOffset(){

        // given
        double value = 10.88300;

        // when, then
        assertEquals(88300, EstimationCalculator.getOffset(value));

        // given
        double value2 = 10.8831232;

        // when, then
        assertEquals(88312, EstimationCalculator.getOffset(value2));
    }

    @Test
    public void getLocationFromEstimationArray(){

        // given
        double[] estimation = {12345, 67891};
        double[] estimationLoc = {10.12345, 49.67891};

        // when, then
        assertArrayEquals(estimationLoc, EstimationCalculator.getLocationFromEstimationArray(estimation), 0.0001);
    }
}