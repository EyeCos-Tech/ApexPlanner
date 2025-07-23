
package com.eyecostech.apexplanner.border;
//package com.eyecostech.apexplanner.Border;

import com.eyecostech.apexplanner.border.BorderPoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bytedeco.opencv.opencv_core.Point;

public class Border {
    private int value1, value2;
    private Set<BorderPoint> pixels;
    
    public Border(int v1, int v2) {
        value1 = Math.min(v1, v2);
        value2 = Math.max(v1, v2);
        pixels = new HashSet<>();
    }
    
    public boolean isCompatible(int value) {
        return value1 == value || value2 == value;
    }    
    
    public boolean isCompatible(Border other) {
        return value1 == other.value1 && value2 == other.value2;
    }     
    
    public void addBorderPoint(BorderPoint bp) {
        pixels.add(bp);
    }
    
    public void merge(Border other) {
        pixels.addAll(other.pixels);
    }
    
    public double getArea() {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (BorderPoint bp : pixels) {
            int x = bp.getX();
            int y = bp.getY();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        return width * height;
    }    
    
    public int getLabel(){
        return (int) Math.round((value1 + value2)/2.0);
    }
   
    public Point getlabelPosition(){
        BorderPoint highestBorderPoint = null;
        int minY = Integer.MAX_VALUE;
        
        for (BorderPoint bp : pixels) {
            if (bp.getY() < minY) {
                minY = bp.getY();
                highestBorderPoint = bp;
            }
        }
        
        return highestBorderPoint != null ? new Point(highestBorderPoint.getX(), highestBorderPoint.getY()) : null;           
    }
    
    public Set<BorderPoint> getPixels() {
        return pixels;
    }
    
    public int getValue1() {
        return value1;
    }
    
    public int getValue2() {
        return value2;
    }
    
    @Override
    public String toString() {
        return String.format("Border(%d-%d, %d pixels, area=%.1f)@%s", value1, value2, pixels.size(), getArea(),
                           Integer.toHexString(System.identityHashCode(this)));
    }
}