/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PLAINFICAR UNA LA RUTA QUE FARA EL LASER TENINT EN COMPTE QUE CADA DISPARO HA
 * DE SEL LO MES LLUNY POSSIBLE DE L'ANTERIOR
 *
 * @author Pau Savall
 */
public class ShootPlanner {

    public List<List<Double>> matriz;

    static class Elemento {

        int row;
        int col;
        double value;

        Elemento(int row, int col, double value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        double distancia(Elemento other) {
            int dr = this.row - other.row;
            int dc = this.col - other.col;
            return Math.sqrt(dr * dr + dc * dc);
        }

        public int getX() {
            return row;
        }

        public int gety() {
            return col;
        }

        public Elemento getElemento() {
            return this;
        }
    }

    public List<Elemento> getElemento(List<Elemento> cells) {

        List<Elemento> elementos = cells;

        return elementos;

    }
    
    
    /*ordena els punts de la matriu sempre que el valor sigui diferent a 0 i retorna un List<> amb el valor.*/
    public static List<Double> ordenarXDistancia(List<List<Double>> matrix) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        List<Elemento> cells = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (matrix.get(i).get(j) != 0) {

                    cells.add(new Elemento(i, j, matrix.get(i).get(j)));
                }
            }
        }

        List<Double> result = new ArrayList<>();
        Set<Elemento> unvisited = new HashSet<>(cells);

        // Start from top-left or arbitrary point
        Elemento current = cells.get(0);
        unvisited.remove(current);
        result.add(current.value);

        while (!unvisited.isEmpty()) {
            Elemento next = null;
            double maxDist = -1;

            for (Elemento cell : unvisited) {
                double dist = current.distancia(cell);
                if (dist > maxDist) {
                    maxDist = dist;
                    next = cell;
                }
            }

            result.add(next.value);
            unvisited.remove(next);
            current = next;
        }

        return result;
    }

    public List<List<Double>> cargarMatriz(List<List<Double>> x) {
        matriz = x;

        return matriz;

    }

}

//
//import java.util.*;
//
//public class MatrixFarOrdering {
//
//    static class Elemento {
//        int row;
//        int col;
//        double value;
//
//        Elemento(int row, int col, double value) {
//            this.row = row;
//            this.col = col;
//            this.value = value;
//        }
//
//        double distancia(Elemento other) {
//            int dr = this.row - other.row;
//            int dc = this.col - other.col;
//            return Math.sqrt(dr * dr + dc * dc);
//        }
//    }
//
//    public static List<Double> orderByMaxDistance(List<List<Double>> matrix) {
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();
//
//        List<Cell> cells = new ArrayList<>();
//        for (int i = 0; i < rows; i++)
//            for (int j = 0; j < cols; j++)
//                cells.add(new Elemento(i, j, matrix.get(i).get(j)));
//
//        List<Double> result = new ArrayList<>();
//        Set<Cell> unvisited = new HashSet<>(cells);
//
//        // Start from top-left or arbitrary point
//        Elemento current = cells.get(0);
//        unvisited.remove(current);
//        result.add(current.value);
//
//        while (!unvisited.isEmpty()) {
//            Elemento next = null;
//            double maxDist = -1;
//
//            for (Elemento cell : unvisited) {
//                double dist = current.distancia(cell);
//                if (dist > maxDist) {
//                    maxDist = dist;
//                    next = cell;
//                }
//            }
//
//            result.add(next.value);
//            unvisited.remove(next);
//            current = next;
//        }
//
//        return result;
//    }
//
//    // Ejemplo de uso
//    public static void main(String[] args) {
//        List<List<Double>> matrix = List.of(
//            List.of(1.0, 2.0, 3.0),
//            List.of(4.0, 5.0, 6.0),
//            List.of(7.0, 8.0, 9.0)
//        );
//
//        List<Double> ordered = orderByMaxDistance(matrix);
//        System.out.println(ordered);
//    }
//}

