package com.eyecostech.apexplanner;

import com.eyecostech.apexplanner.border.BorderPoint;
import com.eyecostech.apexplanner.border.Border;
import com.eyecostech.apexplanner.border.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.global.opencv_core;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

public class ColorIsobarDrawer {
    
    private Mat colorImage;
    private Mat isobarPlot;
    private Map<Integer, Integer> colorToValueMap;
    
    /**
     * Constructor
     * @param colorImage Imagen a color de entrada
     * @param colorToValueMap Mapa que asocia colores RGB con valores de isobara
     */
    public ColorIsobarDrawer(Mat colorImage, Map<Integer, Integer> colorToValueMap) {
        this.colorImage = colorImage;
        this.colorToValueMap = colorToValueMap;
        this.isobarPlot = Mat.zeros(colorImage.rows(), colorImage.cols(), CV_8UC4).asMat();
    }
    
    /**
     * Dibuja las isobaras en la imagen
     */
    public void drawColorIsobars() {
        List<Border> borders = detectColorBorders();
        drawBorders(borders, new Scalar(0, 0, 0, 255)); // Negro opaco
    }
    
    /**
     * Detecta los bordes entre diferentes colores/valores
     */
    private List<Border> detectColorBorders() {
        BorderPoint[][] borderGrid = prepareColorBorderGrid();
        List<Border> allBorders = new ArrayList<>();
        
        int rows = borderGrid.length;
        int cols = borderGrid[0].length;
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        for (int y = 1; y < rows - 1; y++) {
            for (int x = 1; x < cols - 1; x++) {
                BorderPoint centerBorderPoint = borderGrid[y][x];
                if (centerBorderPoint.isTransparent()) continue;
                
                int centerValue = centerBorderPoint.getValue();
                
                for (int i = 0; i < 8; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    BorderPoint neighborBorderPoint = borderGrid[ny][nx];
                    
                    if (!neighborBorderPoint.isTransparent() && neighborBorderPoint.getValue() != centerValue) {
                        int neighborValue = neighborBorderPoint.getValue();
                        
                        Border compatibleCenterBorder = centerBorderPoint.findCompatibleBorder(neighborValue);
                        Border compatibleNeighborBorder = neighborBorderPoint.findCompatibleBorder(centerValue);
                        
                        if (compatibleCenterBorder != null && compatibleNeighborBorder != null && 
                            compatibleCenterBorder != compatibleNeighborBorder) {
                            
                            // Fusionar borders
                            for (BorderPoint bp : compatibleNeighborBorder.getPixels()) {
                                compatibleCenterBorder.addBorderPoint(bp);
                                bp.removeBorder(compatibleNeighborBorder);
                                bp.addBorder(compatibleCenterBorder);
                            }
                            
                            allBorders.remove(compatibleNeighborBorder);
                            
                        } else if (compatibleCenterBorder != null) {
                            compatibleCenterBorder.addBorderPoint(neighborBorderPoint);
                            neighborBorderPoint.addBorder(compatibleCenterBorder);
                            
                        } else if (compatibleNeighborBorder != null) {                            
                            compatibleNeighborBorder.addBorderPoint(centerBorderPoint);
                            centerBorderPoint.addBorder(compatibleNeighborBorder);
                            
                        } else {
                            // Crear nuevo border
                            Border newBorder = new Border(centerValue, neighborValue);
                            newBorder.addBorderPoint(centerBorderPoint);
                            newBorder.addBorderPoint(neighborBorderPoint);
                            allBorders.add(newBorder);
                            
                            centerBorderPoint.addBorder(newBorder);
                            neighborBorderPoint.addBorder(newBorder);
                        }
                    }
                }
            }
        }        
        return allBorders;
    }
    
    /**
     * Prepara la grilla de BorderPoints basándose en los colores de la imagen
     * VERSIÓN CORREGIDA que maneja diferentes tipos de imágenes
     */
    private BorderPoint[][] prepareColorBorderGrid() {
        int rows = colorImage.rows();
        int cols = colorImage.cols();
        int channels = colorImage.channels();
        BorderPoint[][] borderGrid = new BorderPoint[rows][cols];
        
        System.out.println("Imagen: " + rows + "x" + cols + " con " + channels + " canales");
        
        // Verificar el tipo de imagen y convertir si es necesario
        Mat processedImage = colorImage;
        boolean needsConversion = false;
        
        if (channels == 3) {
            // BGR -> BGRA
            processedImage = new Mat();
            opencv_imgproc.cvtColor(colorImage, processedImage, opencv_imgproc.COLOR_BGR2BGRA);
            needsConversion = true;
            System.out.println("Convirtiendo de BGR a BGRA");
        } else if (channels == 1) {
            // Escala de grises -> BGRA
            processedImage = new Mat();
            Mat temp = new Mat();
            opencv_imgproc.cvtColor(colorImage, temp, opencv_imgproc.COLOR_GRAY2BGR);
            opencv_imgproc.cvtColor(temp, processedImage, opencv_imgproc.COLOR_BGR2BGRA);
            temp.release();
            needsConversion = true;
            System.out.println("Convirtiendo de escala de grises a BGRA");
        } else if (channels != 4) {
            throw new IllegalArgumentException("Formato de imagen no soportado: " + channels + " canales");
        }
        
        UByteIndexer colorIdx = processedImage.createIndexer();
        
        try {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    try {
                        // Ahora siempre tenemos 4 canales
                        int alpha = colorIdx.get(y, x, 3) & 0xFF;
                        
                        if (alpha > 0) {
                            // Obtener color RGB
                            int b = colorIdx.get(y, x, 0) & 0xFF;
                            int g = colorIdx.get(y, x, 1) & 0xFF;
                            int r = colorIdx.get(y, x, 2) & 0xFF;
                            
                            // Crear clave RGB
                            int rgbKey = (r << 16) | (g << 8) | b;
                            
                            // Obtener valor de isobara correspondiente
                            Integer isobarValue = colorToValueMap.get(rgbKey);
                            
                            if (isobarValue != null) {
                                borderGrid[y][x] = new BorderPoint(x, y, isobarValue);
                            } else {
                                // Si no hay mapeo, usar un valor por defecto
                                borderGrid[y][x] = new BorderPoint(x, y, 0);
                            }
                        } else {
                            borderGrid[y][x] = new BorderPoint(x, y, 0);
                        }
                    } catch (Exception e) {
                        System.err.println("Error en pixel (" + x + "," + y + "): " + e.getMessage());
                        borderGrid[y][x] = new BorderPoint(x, y, 0);
                    }
                }
            }
        } finally {
            colorIdx.release();
            if (needsConversion) {
                processedImage.release();
            }
        }
        
        // Aplicar filtro de ruido si es necesario
        borderGrid = filterNoisePixels(borderGrid, rows, cols);
        
        return borderGrid;
    }
    
    /**
     * Método alternativo que trabaja directamente con los datos de la imagen
     * Más robusto para diferentes formatos
     */
    private BorderPoint[][] prepareColorBorderGridAlternative() {
        int rows = colorImage.rows();
        int cols = colorImage.cols();
        int channels = colorImage.channels();
        BorderPoint[][] borderGrid = new BorderPoint[rows][cols];
        
        // Obtener los datos como array de bytes
        byte[] data = new byte[(int)(colorImage.total() * channels)];
        colorImage.data().get(data);
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int idx = (y * cols + x) * channels;
                
                // Verificar si tenemos suficientes datos
                if (idx + channels - 1 < data.length) {
                    int alpha = 255; // Por defecto opaco
                    
                    if (channels >= 4) {
                        alpha = data[idx + 3] & 0xFF;
                    }
                    
                    if (alpha > 0 && channels >= 3) {
                        int b = data[idx] & 0xFF;
                        int g = data[idx + 1] & 0xFF;
                        int r = data[idx + 2] & 0xFF;
                        
                        int rgbKey = (r << 16) | (g << 8) | b;
                        Integer isobarValue = colorToValueMap.get(rgbKey);
                        
                        if (isobarValue != null) {
                            borderGrid[y][x] = new BorderPoint(x, y, isobarValue);
                        } else {
                            borderGrid[y][x] = new BorderPoint(x, y, 0);
                        }
                    } else {
                        borderGrid[y][x] = new BorderPoint(x, y, 0);
                    }
                } else {
                    borderGrid[y][x] = new BorderPoint(x, y, 0);
                }
            }
        }
        
        return filterNoisePixels(borderGrid, rows, cols);
    }
    
    /**
     * Dibuja los bordes detectados
     */
    private void drawBorders(List<Border> borders, Scalar contourColor) {
        UByteIndexer resultIdx = isobarPlot.createIndexer();
        
        for (Border border : borders) {
            for (BorderPoint bp : border.getPixels()) {
                Point p = bp.getPoint();
                if (p.x() >= 0 && p.x() < isobarPlot.cols() && 
                    p.y() >= 0 && p.y() < isobarPlot.rows()) {
                    resultIdx.put(p.y(), p.x(), 0, (int) contourColor.get(0));
                    resultIdx.put(p.y(), p.x(), 1, (int) contourColor.get(1));
                    resultIdx.put(p.y(), p.x(), 2, (int) contourColor.get(2));
                    resultIdx.put(p.y(), p.x(), 3, (int) contourColor.get(3));
                }
            }
        }
        
        resultIdx.release();
    }
    
    /**
     * Dibuja isobaras con diferentes colores según el valor
     */
    public void drawColoredIsobars(Color[] valuePalette) {
        List<Border> borders = detectColorBorders();
        
        for (Border border : borders) {
            // Determinar el color basado en el valor de la isobara
            int borderValue = border.getLabel();
            Scalar borderColor = new Scalar(0, 0, 0, 255); // Negro por defecto
            
            // Buscar el color correspondiente en la paleta
            for (Color c : valuePalette) {
                if (c.getValue() == borderValue) {
                    int[] bgra = c.getBGRA();
                    borderColor = new Scalar(bgra[0], bgra[1], bgra[2], bgra[3]);
                    break;
                }
            }
            
            // Dibujar este border con su color específico
            drawSingleBorder(border, borderColor);
        }
    }
    
    /**
     * Dibuja un solo border con un color específico
     */
    private void drawSingleBorder(Border border, Scalar color) {
        UByteIndexer resultIdx = isobarPlot.createIndexer();
        
        for (BorderPoint bp : border.getPixels()) {
            Point p = bp.getPoint();
            if (p.x() >= 0 && p.x() < isobarPlot.cols() && 
                p.y() >= 0 && p.y() < isobarPlot.rows()) {
                resultIdx.put(p.y(), p.x(), 0, (int) color.get(0));
                resultIdx.put(p.y(), p.x(), 1, (int) color.get(1));
                resultIdx.put(p.y(), p.x(), 2, (int) color.get(2));
                resultIdx.put(p.y(), p.x(), 3, (int) color.get(3));
            }
        }
        
        resultIdx.release();
    }
    
    /**
     * Filtra píxeles de ruido
     */
    private BorderPoint[][] filterNoisePixels(BorderPoint[][] grid, int rows, int cols) {
        BorderPoint[][] filtered = new BorderPoint[rows][cols];
        
        // Copiar bordes
        for (int x = 0; x < cols; x++) {
            filtered[0][x] = grid[0][x];
            filtered[rows-1][x] = grid[rows-1][x];
        }
        for (int y = 0; y < rows; y++) {
            filtered[y][0] = grid[y][0];
            filtered[y][cols-1] = grid[y][cols-1];
        }
        
        // Filtrar interior
        for (int y = 1; y < rows - 1; y++) {
            for (int x = 1; x < cols - 1; x++) {
                BorderPoint current = grid[y][x];
                
                if (current.isTransparent()) {
                    filtered[y][x] = current;
                    continue;
                }
                
                int sameValueCount = 0;
                int totalNeighbors = 0;
                int mostCommonValue = current.getValue();
                Map<Integer, Integer> valueCount = new HashMap<>();
                
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        
                        BorderPoint neighbor = grid[y + dy][x + dx];
                        if (!neighbor.isTransparent()) {
                            int value = neighbor.getValue();
                            valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
                            totalNeighbors++;
                            
                            if (value == current.getValue()) {
                                sameValueCount++;
                            }
                        }
                    }
                }
                
                // Si el píxel está rodeado mayormente por otros valores, cambiarlo
                if (totalNeighbors > 0 && sameValueCount < totalNeighbors * 0.3) {
                    mostCommonValue = valueCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get().getKey();
                    
                    filtered[y][x] = new BorderPoint(x, y, mostCommonValue);
                } else {
                    filtered[y][x] = current;
                }
            }
        }
        
        return filtered;
    }
    
    /**
     * Obtiene la imagen con las isobaras dibujadas
     */
    public Mat getIsobarPlot() {
        return isobarPlot;
    }
    
    /**
     * Combina la imagen original con las isobaras
     */
    public Mat getImageWithIsobars() {
        Mat result = colorImage.clone();
        UByteIndexer resultIdx = result.createIndexer();
        UByteIndexer isobarIdx = isobarPlot.createIndexer();
        
        for (int y = 0; y < result.rows(); y++) {
            for (int x = 0; x < result.cols(); x++) {
                int isobarAlpha = isobarIdx.get(y, x, 3);
                if (isobarAlpha > 0) {
                    // Sobrescribir con el color de la isobara
                    for (int c = 0; c < 4; c++) {
                        resultIdx.put(y, x, c, isobarIdx.get(y, x, c));
                    }
                }
            }
        }
        
        resultIdx.release();
        isobarIdx.release();
        
        return result;
    }
    
    /**
     * Ejemplo de uso
     */
    public static void example() {
        // Cargar imagen a color
        Mat colorImage = imread("ruta/imagen_color.png", IMREAD_UNCHANGED);
        
        // Crear mapa de colores a valores de isobara
        Map<Integer, Integer> colorToValue = new HashMap<>();
        // Ejemplo: diferentes tonos de azul para el agua
        colorToValue.put((0 << 16) | (0 << 8) | 255, 10);      // Azul oscuro = profundo
        colorToValue.put((100 << 16) | (149 << 8) | 237, 20);  // Azul medio
        colorToValue.put((173 << 16) | (216 << 8) | 230, 30);  // Azul claro = poco profundo
        // Tonos de verde para vegetación
        colorToValue.put((34 << 16) | (139 << 8) | 34, 40);    // Verde oscuro
        colorToValue.put((124 << 16) | (252 << 8) | 0, 50);    // Verde claro
        // Tonos de marrón para tierra
        colorToValue.put((139 << 16) | (69 << 8) | 19, 60);    // Marrón
        
        // Crear el dibujador de isobaras
        ColorIsobarDrawer drawer = new ColorIsobarDrawer(colorImage, colorToValue);
        
        // Dibujar isobaras
        drawer.drawColorIsobars();
        
        // Obtener resultado
        Mat result = drawer.getImageWithIsobars();
        
        // Guardar resultado
        imwrite("resultado_con_isobaras.png", result);
    }
}