package com.worthant.javaee;

import com.worthant.javaee.utils.AreaChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AreaCheckerTest {

    @Test
    public void testIsInAreaForRectangle() {
        // Inside the rectangle
        assertTrue(AreaChecker.isInArea(-5, -1, 10));
        assertTrue(AreaChecker.isInArea(-10, -1, 10));
        assertTrue(AreaChecker.isInArea(-2, -5, 10));

        // Outside the rectangle
        assertFalse(AreaChecker.isInArea(-11, -1, 10));
        assertFalse(AreaChecker.isInArea(-7, -6, 10));
    }

    @Test
    public void testIsInAreaForSemiCircle() {
        // Inside the semi-circle
        assertTrue(AreaChecker.isInArea(5, 0, 10));
        assertTrue(AreaChecker.isInArea(0, 5, 10));
        assertTrue(AreaChecker.isInArea(1, 1, 10));
        assertTrue(AreaChecker.isInArea(0, 0, 10));

        // Outside the semi-circle
        assertFalse(AreaChecker.isInArea(5, 1, 10));
        assertFalse(AreaChecker.isInArea(1, 5, 10));
        assertFalse(AreaChecker.isInArea(5, 5, 10));
    }

    @Test
    public void testIsInAreaForTriangle() {
        // Inside the triangle
        assertTrue(AreaChecker.isInArea(5, 0, 10));
        assertTrue(AreaChecker.isInArea(0, -5, 10));
        assertTrue(AreaChecker.isInArea(-1, -1, 10));
        assertTrue(AreaChecker.isInArea(-2.5, -2.5, 10));

        // Outside the triangle
        assertFalse(AreaChecker.isInArea(5, -0.1, 10));
        assertFalse(AreaChecker.isInArea(5, 0.0000001, 10));
        assertFalse(AreaChecker.isInArea(2.6, -2.5, 10));
    }

    @Test
    public void testIsInAreaOutsideAllRegions() {
        assertTrue(AreaChecker.isInArea(-10, 0, 10));
        assertTrue(AreaChecker.isInArea(0, 5, 10));

        // Outside all regions
        assertFalse(AreaChecker.isInArea(-0.000001, 0.0000001, 4));
        assertFalse(AreaChecker.isInArea(-4, 0.00000001, 10));
        assertFalse(AreaChecker.isInArea(-456, 0, 10));
        assertFalse(AreaChecker.isInArea(4564, 0, 43));
        assertFalse(AreaChecker.isInArea(0, 354634563, 43));
        assertFalse(AreaChecker.isInArea(0, -345635634, 43));
    }
}

