/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner.border;

//package com.eyecos.analyzer.analysis;

public class Color implements Comparable<Color>{
    public enum EyeColorType { BROWN, GREEN, BLUE }

    private int r;
    private int g;
    private int b;
    private int value;
    private int scale;

    public Color(int r, int g, int b, int value) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.value = value;
        this.scale = 0;
    }
    
    public Color(int r, int g, int b, Color[] valuePalette) {
        this.r = r;
        this.g = g;
        this.b = b;
        assignNearestValue(valuePalette);
        this.scale = 0;
    }

    public Color(int r, int g, int b, int value, int scale) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.value = value;
        this.scale = scale;
    }
    
    private void assignNearestValue(Color[] valuePalette) {
        int minDist = Integer.MAX_VALUE;
        int closestValue = 0;
        for (Color c : valuePalette) {
            int dr = this.r - c.r;
            int dg = this.g - c.g;
            int db = this.b - c.b;
            int dist = dr * dr + dg * dg + db * db;
            if (dist < minDist) {
                minDist = dist;
                closestValue = c.value;
            }
        }
        this.value = closestValue;
    }
        
    @Override
    public int compareTo(Color other) {
        if (this.value != other.value)
            return Integer.compare(this.value, other.value);
        
        float[] hsv1 = this.getHSV();
        float[] hsv2 = other.getHSV();
        int g1 = groupIndex(hsv1[0]);
        int g2 = groupIndex(hsv2[0]);
        if (g1 != g2) {
            return Integer.compare(g1, g2);
        }
        float v1 = this.getIntensity();
        float v2 = other.getIntensity();
        if (g1 == 0) {
            return Float.compare(v1, v2);    // blue: dark→light 
        } else {
            return Float.compare(v2, v1);    // green/red: light→dark
        }
    }

    private static int groupIndex(float hue) {
        float hDeg = hue * 360f;
        if (hDeg >= 180f && hDeg < 300f) return 0;
        if (hDeg >= 60f  && hDeg < 180f) return 1;
        return 2;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getValue() {
        return value;
    }

    public int getScale() {
        return scale;
    }

    public EyeColorType getBasic() {
        if (value >= 1 && value <= 20) {
            return EyeColorType.BLUE;
        } else if (value >= 21 && value <= 40) {
            return EyeColorType.GREEN;
        } else {
            return EyeColorType.BROWN;
        }
    }

    public int[] getRGB() {
        return new int[]{ r, g, b };
    }

    public int[] getRGBA() {
        return new int[]{ r, g, b, 255 };
    }

    public int[] getBGRA() {
        return new int[]{ b, g, r, 255 };
    }
    
    public float[] getHSV() {
        return java.awt.Color.RGBtoHSB(r, g, b, null);
    }
    
    public int getIntensity(){
//        return (int) (0.299*r + 0.587*g + 0.114*b);
        return (int) (r + g + b)/3;
    }

    @Override
    public String toString() {
        return toHex();
    }

    public String toHex() {
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
