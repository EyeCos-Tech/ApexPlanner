
package com.eyecostech.apexplanner.border;

import java.util.HashSet;
import java.util.Set;
import org.bytedeco.opencv.opencv_core.Point;

public class BorderPoint {
    private final int x, y;
    private final int value;
    private final Set<Border> borders;
    
    public BorderPoint(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.borders = new HashSet<>();
    }
    
    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    public int getValue() { 
        return value; 
    }
    
    public Point getPoint() {
        return new Point(x, y);
    }
    
    public void addBorder(Border border) {
        borders.add(border);
    }
    
    public void removeBorder(Border border) {
        borders.remove(border);
    }
    
    public Set<Border> getBorders() {
        return borders;
    }
    
    public Border findCompatibleBorder(int neighborValue) {
        for (Border border : borders) {
            if (border.isCompatible(neighborValue)) {
                return border;
            }
        }
        return null;
    }
    
    public boolean hasBorders() {
        return !borders.isEmpty();
    }
    
    public boolean isTransparent() {
        return value == 0;
    }
    
    @Override
    public String toString() {
        return String.format("BP(%d,%d)=%d", x, y, value);
    }
}