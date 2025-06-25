package com.eyecostech.apexplanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_highgui.MouseCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import static org.bytedeco.opencv.global.opencv_imgproc.medianBlur;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

/**
 * CARREGAR I ANALITZAR UNA MATRIU CSV EXTRETA DEL THREE_I_TECH AMB EL MAPA
 * TOPOGRAFIC D'ABLACIO EXTREURE'N ELS SHOOTS NECESSARIS PER LA CORRECCIO
 *
 * @author Pau Savall
 */
public class CsvAnalizer {

    public Calculador calc = new Calculador();
    public Mat image = null;
    public String nombre;
    public ImageConverter converter = new ImageConverter();

    public CsvAnalizer(String nombre) {
        this.nombre = nombre;
    }

    public CsvAnalizer() {
        this.nombre = nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setImage(Mat image) {

    }


    /*crea una matriu*/
    public List<List<Double>> crearMatriz(String pathCsv) {
        System.out.println("path recibido: " + pathCsv);

        File csvFile = new File(pathCsv);

        // Verificar que el archivo existe
        if (!csvFile.exists()) {
            System.err.println("ERROR: El archivo no existe: " + pathCsv);
            return null;
        }

        List<List<Double>> matrix = loadCSV(pathCsv);
        if (matrix == null) {
            System.out.println("No se pudo cargar el archivo.");
        } else {
            System.out.println("CSV " + pathCsv + " cargado con éxito");
            System.out.println("Filas: " + matrix.size() + ", Columnas: "
                    + (matrix.isEmpty() ? 0 : matrix.get(0).size()));
        }
        return matrix;
    }

    /*carrega un csv i el transforma en matriu*/
    private static List<List<Double>> loadCSV(String filePath) {
        List<List<Double>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine(); // Saltar encabezados
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                List<Double> row = new ArrayList<>();
                for (String t : tokens) {
                    row.add(Double.parseDouble(t.trim()));
                }
                data.add(row);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getMaxValue(List<List<Double>> matrix) {
        double maxValue = 0.0;
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = matrix.get(i).get(j);
                System.out.println(value);

                if (value > maxValue) {
                    maxValue = value;
                    //calc.numeroShoots(String.valueOf(maxValue), 197);
                }
            }
        }

        return maxValue;

    }

    /*Llegir un punt segons coordenada- dona lo mateix que capturant punt amb mouse a la imatge*/
    public double leerPunto(int x, int y, List<List<Double>> matrix) {
        // Leer ablación en X=0.5, Y=fila 100
        int filaY = y;
        int columnaX = x; // por ejemplo, X=0.5 está en columna 5

        double ablacion = matrix.get(filaY).get(columnaX);
        //System.out.println("Ablacion en x= " + x + " y=  " + y + ": " + ablacion + " micras");
        return ablacion;
    }

    /*prepara una imatge en color representant la matriu del csv i retorna la  imatge*/
    public Mat prepararImagen(List<List<Double>> matrix) {

        Mat image = new ImageService().crearImagenColor(matrix);

        /*si necessito girar la imatge*/
//        BufferedImage imageBuff= converter.matToBufferedImage(image);
//        imageBuff= converter.rotarImagen(imageBuff, 180.0, false);
//        image= converter.bufferedImageToMat(imageBuff);
        return image;
    }

    /*mostra la imatge i imprimeix a consola la cantitat de disparos que has de fer en el punt on clickes */
    public void imprimirImagen(Mat imagen, List<List<Double>> matrix) {

        String windowName = "Mapa de Ablacion Corneal: " + getNombre();
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);

        /*capturar punt de la imatge x/y i retorna valor. Dona lo mateix que el metode leerPunto()*/
        // Callback del mouse 
        opencv_highgui.setMouseCallback(windowName, new MouseCallback() {
            @Override
            public void call(int event, int x, int y, int flags, Pointer userdata) {
                if (event == opencv_highgui.EVENT_LBUTTONDOWN) {
                    if (y < matrix.size() && x < matrix.get(0).size()) {
                        double valor = matrix.get(y).get(x);

                        // Redondea a 4 decimales
                        BigDecimal bd = new BigDecimal(valor);
                        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
                        double valorRedondo = bd.doubleValue();

                        DecimalFormat df = new DecimalFormat("#.####");
                        System.out.println("Click en: X=" + x + ", Y=" + y + " -> Valor: " + df.format(valorRedondo) + " mm");

                        double mm = valorRedondo; //????????????

                        String numero = String.valueOf(mm);
                        calc.numeroShoots(numero, calc.mmXShoot(193));//calcula el numero de disparos que necessita per un laser de potencia estandard (193 de longitud d'ona)

                    }
                }
            }
        });

        opencv_highgui.imshow(windowName, imagen);
        opencv_highgui.waitKey(0);
        opencv_highgui.destroyAllWindows();

    }
}
// Método alternativo más simple usando colores predefinidos

//    public Mat prepararImagenColorSimple(List<List<Double>> matriz) {
//        int rows = matriz.size();
//        int cols = matriz.get(0).size();
//        
//        Mat image = new Mat(cols, rows, CvType.CV_8UC3);
//
//        // Encontrar valor máximo
//        double maxValue = 0;
//        for (List<Double> row : matriz) {
//            for (Double value : row) {
//                if (value != null) {
//                    maxValue = Math.max(maxValue, value);
//                }
//            }
//        }
//
//        // Dibujar pixels con colores
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                Double value = matriz.get(y).get(x);
//                
//                UByteIndexer indexer = image.createIndexer();
//                if (value != null && value > 0) {
//                    // Normalizar valor entre 0 y 1
//                    float normalized = (float) (value / maxValue);
//
//                    // Crear color usando HSB (Hue, Saturation, Brightness)
//                    // Hue va de 0.7 (azul) a 0.0 (rojo)
//                    float hue = 0.7f * (1.0f - normalized);
//                    int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
//                    
//                    int red = (rgb >> 16) & 0xFF;
//                    int green = (rgb >> 8) & 0xFF;
//                    int blue = rgb & 0xFF;
//
//                    // BGR order
//                    indexer.put(y, x, 0, blue);
//                    indexer.put(y, x, 1, green);
//                    indexer.put(y, x, 2, red);
//                } else {
//                    indexer.put(y, x, 0, 0);
//                    indexer.put(y, x, 1, 0);
//                    indexer.put(y, x, 2, 0);
//                }
//                
//                indexer.release();
////                    //image.setRGB(x, y, rgb);
////                    image.put(y, x, blue, green, red);
////                } else {
////                    // Negro para valores nulos
////                    image.put(y, x, 0, 0, 0);                    
////                    //image.setRGB(x, y, 0);
////                }
//                /*// Establecer píxel con color
//if (condicion) {
//    // Extraer componentes RGB
//    int red = (rgb >> 16) & 0xFF;
//    int green = (rgb >> 8) & 0xFF;
//    int blue = rgb & 0xFF;
//    
//    // OpenCV usa BGR, no RGB
//    image.put(y, x, new byte[]{(byte)blue, (byte)green, (byte)red});
//} else {
//    // Negro para valores nulos
//    image.put(y, x, new byte[]{0, 0, 0});
//}*/
// /*// En lugar de BufferedImage, crear directamente un Mat
//Mat image = MatColorOperations.prepararImagenColorConMat(matriz);
//
//// Ahora puedes usar este Mat con ColorIsobarDrawer
//ColorIsobarDrawer drawer = new ColorIsobarDrawer(image, colorToValueMap);
//drawer.drawColorIsobars();
//
//// Obtener resultado
//Mat resultado = drawer.getImageWithIsobars();
//
//// Si necesitas BufferedImage al final
//BufferedImage bufferedImage = matToBufferedImage(resultado);
//
//// Liberar memoria
//image.release();
//resultado.release();*/
//            }
//        }
//        
//        return image;
//    }
    /**
     * CODI COMENTEAT PER TREURE LES ISOBARES
     *
     */
    //public BufferedImage prepararImagen(List<List<Double>> matriz) {
//        int rows = matriz.size();
//        int cols = matriz.get(0).size();
//
//        // Crear imagen a COLOR (4 canales: BGRA)
//        Mat colorImage = new Mat(rows, cols, CV_8UC4);
//
//        // Encontrar valor máximo y mínimo
//        double maxValue = Double.MIN_VALUE;
//        double minValue = Double.MAX_VALUE;
//
//        for (List<Double> row : matriz) {
//            for (Double value : row) {
//                if (value != null && value > 0) {
//                    maxValue = Math.max(maxValue, value);
//                    minValue = Math.min(minValue, value);
//                }
//            }
//        }
//
//        System.out.println("Preparando imagen a color: min=" + minValue + ", max=" + maxValue);
//
//        // Definir paleta de colores tipo mapa de calor
//        int[][] heatMapColors = {
//            {139, 0, 0}, // Azul oscuro (valores bajos)
//            {255, 0, 0}, // Azul
//            {255, 128, 0}, // Azul claro
//            {255, 255, 0}, // Cian
//            {128, 255, 0}, // Verde-cian
//            {0, 255, 0}, // Verde
//            {0, 255, 128}, // Verde-amarillo
//            {0, 255, 255}, // Amarillo
//            {0, 200, 255}, // Naranja claro
//            {0, 128, 255}, // Naranja
//            {0, 64, 255}, // Naranja oscuro
//            {0, 0, 255}, // Rojo
//            {0, 0, 200}, // Rojo oscuro
//            {0, 0, 139} // Rojo muy oscuro (valores altos)
//        };
//
//        UByteIndexer idx = colorImage.createIndexer();
//
//        // Crear mapa para las isobaras
//        Map<Integer, Integer> colorToValue = new HashMap<>();
//
//        try {
//            for (int y = 0; y < rows; y++) {
//                for (int x = 0; x < cols; x++) {
//                    Double value = matriz.get(y).get(x);
//
//                    if (value != null && value > 0) {
//                        // Normalizar el valor al rango 0-1
//                        double normalized = (value - minValue) / (maxValue - minValue);
//
//                        // Obtener índice en la paleta
//                        int colorIndex = (int) (normalized * (heatMapColors.length - 1));
//                        colorIndex = Math.max(0, Math.min(heatMapColors.length - 1, colorIndex));
//
//                        // Obtener color
//                        int[] bgr = heatMapColors[colorIndex];
//
//                        // Asignar color al pixel
//                        idx.put(y, x, 0, bgr[0]); // B
//                        idx.put(y, x, 1, bgr[1]); // G
//                        idx.put(y, x, 2, bgr[2]); // R
//                        idx.put(y, x, 3, 255);    // A (opaco)
//
//                        // Agregar al mapa para isobaras
//                        int rgbKey = (bgr[2] << 16) | (bgr[1] << 8) | bgr[0];
//                        colorToValue.put(rgbKey, (int) (value * 10)); // Escalar valor para isobaras
//                    } else {
//                        // Pixel transparente
//                        idx.put(y, x, 0, 0);
//                        idx.put(y, x, 1, 0);
//                        idx.put(y, x, 2, 0);
//                        idx.put(y, x, 3, 0);
//                    }
//                }
//            }
//        } finally {
//            idx.release();
//        }
//
//        // Aplicar suavizado si lo deseas
//        Mat smoothed = new Mat();
//        medianBlur(colorImage, smoothed, 5);
//
//        // Dibujar isobaras
//        ColorIsobarDrawer drawer = new ColorIsobarDrawer(smoothed, colorToValue);
//        drawer.drawColorIsobars();
//
//        // Obtener imagen con isobaras
//        Mat result = drawer.getImageWithIsobars();
//
//        // Convertir a BufferedImage
//        BufferedImage bufferedImage = matToBufferedImage(result);
//
//        // Liberar memoria
//        colorImage.release();
//        smoothed.release();
//        result.release();
//
//        return bufferedImage;
//    }
    /*prepara una imatge en color representant la matriu del csv i retorna la  imatge*/
//    public Mat prepararImagen(List<List<Double>> matrix) {
//        //double ablacion;
//
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();
//            Color[] colorPalette = ColorImageGenerator.createHeatMapPalette();
//
//        Mat image = new Mat(rows, cols, opencv_core.CV_8UC1);
//

////        // Escalar todos los valores (por ejemplo, de mm a µm) per traballar mes comodament
////        for (int y = 0; y < rows; y++) {
////            for (int x = 0; x < cols; x++) {
////                double valor = matrix.get(y).get(x);
////                matrix.get(y).set(x, valor * 1000); // Escala ×1000
////            }
////        }
//
//        /*FET A PARTIR DELS VALORS (ablacio) DE CADA PUNT DE LA MATRIU*/
//        // Escalado
////        double maxValue = matrix.stream()
////                .flatMap(List::stream)
////                .mapToDouble(Double::doubleValue)
////                .max().orElse(1.0);
////        for (int y = 0; y < rows; y++) {
////            for (int x = 0; x < cols; x++) {
////                int grayValue = (int) (255.0 * matrix.get(y).get(x) / maxValue);
////                grayValue = Math.min(255, Math.max(0, grayValue));
////                image.ptr(y, x).put((byte) grayValue);
////            }
////        }
//
//        /*FET A PARTIR DELS NUMEROS DE SHOOT DE CADA PUNT DE LA MATRIU*/
//        // Escalado
//        double maxValue = calc.maxShoots(matrix);
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                int grayValue = (int) (255.0 * calc.numeroShoots(matrix.get(y).get(x), calc.mmXShoot(193)) / maxValue);
//                grayValue = Math.min(255, Math.max(0, grayValue));
//                image.ptr(y, x).put((byte) grayValue);
//            }
//        }
//        System.out.println("el maxim de shoots que te algun punt de la matriu es : " + maxValue);
//
//        // Colormap:c pasar la image de grisos a color
//        Mat color = new Mat();
//        opencv_imgproc.applyColorMap(image, color, opencv_imgproc.COLORMAP_JET);
//        //addIsobaras(image, colorMap, matrix, 20); // 10 isobaras
//        /**
//         * ISOBARAS DE COLOR
//         */
//        Map<Integer, Integer> colorMap = new HashMap<>();
//        colorMap.put((255 << 16) | (0 << 8) | 0, 50);    // Rojo = valor 50
//        colorMap.put((0 << 16) | (255 << 8) | 0, 30);    // Verde = valor 30
//        colorMap.put((0 << 16) | (0 << 8) | 255, 10);    // Azul = valor 10
//        ColorIsobarDrawer drawer = new ColorIsobarDrawer(image, colorMap);
//        // Dibujar isobaras
//        drawer.drawColorIsobars();
//
//        // Obtener resultado
//        Mat resultado = drawer.getImageWithIsobars();
//        
//        image = resultado;
//
////        double[] niveles = {0.0003,0.0008, 0.001,0.005,0.009,0.01,0.05,0.09,0.1,0.5,0.9,1.0}; // mm u otra unidad
////        //Scalar rojo = new Scalar(0.0, 0.0, 255.0, 0.0);  // B, G, R, Alpha
////        Scalar[] colores = {
////            
////            new Scalar(255.0, 0.0, 0.0,0.0), // Azul
////            new Scalar(0.0, 255.0, 0.0,0.0), // Verde
////            new Scalar(0.0, 0.0, 255.0,0.0), // Rojo
////            new Scalar(255.0, 255.0, 0.0,0.0), // Cyan
////            new Scalar(255.0, 0.0, 255.0,0.0) // Magenta
////        };
////        double alpha = 0.5;
//        //addIsobarasPersonalizadas(image, colorMap, matrix, niveles, colores, alpha);
////        String windowName = "Mapa de Ablacion Corneal";
////        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);
//        //opencv_highgui.resizeWindow(windowName, 800, 600);
//
//        /*capturar punt de la imatge x/y i retorna valor. Dona lo mateix que el metode leerPunto()*/
//        // Callback del mouse 
////        opencv_highgui.setMouseCallback(windowName, new MouseCallback() {
////            @Override
////            public void call(int event, int x, int y, int flags, Pointer userdata) {
////                if (event == opencv_highgui.EVENT_LBUTTONDOWN) {
////                    if (y < matrix.size() && x < matrix.get(0).size()) {
////                        double valor = matrix.get(y).get(x);
////
////                        // Redondea a 4 decimales
////                        BigDecimal bd = new BigDecimal(valor);
////                        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
////                        double valorRedondo = bd.doubleValue();
////
////                        DecimalFormat df = new DecimalFormat("#.####");
////                        System.out.println("Click en: X=" + x + ", Y=" + y + " -> Valor: " + df.format(valorRedondo) + " mm");
////
////                        double mm = valorRedondo; //????????????
////
////                        String numero = String.valueOf(mm);
////                        calc.numeroShoots(numero, calc.mmXShoot(193));//calcula el numero de disparos que necessita per un laser de potencia estandard (193 de longitud d'ona)
////
////                    }
////                }
////            }
////        });
////        opencv_highgui.imshow(windowName, colorMap);
////        opencv_highgui.waitKey(0);
////        opencv_highgui.destroyAllWindows();
//        this.setImage(image);
//
//        return image;
//    }

