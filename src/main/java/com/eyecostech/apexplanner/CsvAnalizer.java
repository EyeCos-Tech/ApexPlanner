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

/**
 * CARREGAR I ANALITZAR UNA MATRIU CSV EXTRETA DEL THREE_I_TECH AMB EL MAPA
 * TOPOGRAFIC D'ABLACIO EXTREURE'N ELS SHOOTS NECESSARIS PER LA CORRECCIO
 *
 * @author Pau Savall
 */
public class CsvAnalizer {

    public Calculador calc = new Calculador();
    public Mat image = null;

    public Mat getImage() {

        return image;
    }

    public void setImage(Mat image) {

    }


    /*crea una matriu*/
    public List<List<Double>> cerarMatriz(String path) {
        String csvPath = path;
        List<List<Double>> matrix = loadCSV(csvPath);
        if (matrix == null) {
            System.out.println("No se pudo cargar el archivo.");

        } else {
            System.out.println("Csv " + csvPath + " carreget amb exit");
        }
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();
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
    // Agregar este método a tu clase CsvAnalizer

public BufferedImage prepararImagen(List<List<Double>> matriz) {
        int rows = matriz.size();
        int cols = matriz.get(0).size();

        // Crear imagen a COLOR (4 canales: BGRA)
        Mat colorImage = new Mat(rows, cols, CV_8UC4);

        // Encontrar valor máximo y mínimo
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;

        for (List<Double> row : matriz) {
            for (Double value : row) {
                if (value != null && value > 0) {
                    maxValue = Math.max(maxValue, value);
                    minValue = Math.min(minValue, value);
                }
            }
        }

        System.out.println("Preparando imagen a color: min=" + minValue + ", max=" + maxValue);

        // Definir paleta de colores tipo mapa de calor
        int[][] heatMapColors = {
            {139, 0, 0}, // Azul oscuro (valores bajos)
            {255, 0, 0}, // Azul
            {255, 128, 0}, // Azul claro
            {255, 255, 0}, // Cian
            {128, 255, 0}, // Verde-cian
            {0, 255, 0}, // Verde
            {0, 255, 128}, // Verde-amarillo
            {0, 255, 255}, // Amarillo
            {0, 200, 255}, // Naranja claro
            {0, 128, 255}, // Naranja
            {0, 64, 255}, // Naranja oscuro
            {0, 0, 255}, // Rojo
            {0, 0, 200}, // Rojo oscuro
            {0, 0, 139} // Rojo muy oscuro (valores altos)
        };

        UByteIndexer idx = colorImage.createIndexer();

        // Crear mapa para las isobaras
        Map<Integer, Integer> colorToValue = new HashMap<>();

        try {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    Double value = matriz.get(y).get(x);

                    if (value != null && value > 0) {
                        // Normalizar el valor al rango 0-1
                        double normalized = (value - minValue) / (maxValue - minValue);

                        // Obtener índice en la paleta
                        int colorIndex = (int) (normalized * (heatMapColors.length - 1));
                        colorIndex = Math.max(0, Math.min(heatMapColors.length - 1, colorIndex));

                        // Obtener color
                        int[] bgr = heatMapColors[colorIndex];

                        // Asignar color al pixel
                        idx.put(y, x, 0, bgr[0]); // B
                        idx.put(y, x, 1, bgr[1]); // G
                        idx.put(y, x, 2, bgr[2]); // R
                        idx.put(y, x, 3, 255);    // A (opaco)

                        // Agregar al mapa para isobaras
                        int rgbKey = (bgr[2] << 16) | (bgr[1] << 8) | bgr[0];
                        colorToValue.put(rgbKey, (int) (value * 10)); // Escalar valor para isobaras
                    } else {
                        // Pixel transparente
                        idx.put(y, x, 0, 0);
                        idx.put(y, x, 1, 0);
                        idx.put(y, x, 2, 0);
                        idx.put(y, x, 3, 0);
                    }
                }
            }
        } finally {
            idx.release();
        }

        // Aplicar suavizado si lo deseas
        Mat smoothed = new Mat();
        medianBlur(colorImage, smoothed, 5);

        // Dibujar isobaras
        ColorIsobarDrawer drawer = new ColorIsobarDrawer(smoothed, colorToValue);
        drawer.drawColorIsobars();

        // Obtener imagen con isobaras
        Mat result = drawer.getImageWithIsobars();

        // Convertir a BufferedImage
        BufferedImage bufferedImage = matToBufferedImage(result);

        // Liberar memoria
        colorImage.release();
        smoothed.release();
        result.release();

        return bufferedImage;
    }

// Método auxiliar para convertir Mat a BufferedImage
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
            if (mat.channels() == 4) {
                type = BufferedImage.TYPE_4BYTE_ABGR;
            }
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = new byte[mat.channels() * mat.cols() * mat.rows()];
        mat.data().get(data);

        if (mat.channels() == 4) {
            // Convertir BGRA a ABGR para BufferedImage
            for (int i = 0; i < data.length; i += 4) {
                byte b = data[i];
                byte g = data[i + 1];
                byte r = data[i + 2];
                byte a = data[i + 3];

                data[i] = a;
                data[i + 1] = b;
                data[i + 2] = g;
                data[i + 3] = r;
            }
        }

        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }
       public static Mat bufferedImageToMat(BufferedImage image) {
        Mat mat = null;
        
        // Determinar el tipo de Mat según el tipo de BufferedImage
        int type = -1;
        int channels = 0;
        
        switch (image.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                type = CV_8UC1;
                channels = 1;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                type = CV_8UC3;
                channels = 3;
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                type = CV_8UC4;
                channels = 4;
                break;
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_BGR:
                // Convertir a BGR
                BufferedImage converted = new BufferedImage(image.getWidth(), 
                    image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                converted.getGraphics().drawImage(image, 0, 0, null);
                image = converted;
                type = CV_8UC3;
                channels = 3;
                break;
            case BufferedImage.TYPE_INT_ARGB:
                // Convertir a ABGR
                BufferedImage convertedAlpha = new BufferedImage(image.getWidth(), 
                    image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                convertedAlpha.getGraphics().drawImage(image, 0, 0, null);
                image = convertedAlpha;
                type = CV_8UC4;
                channels = 4;
                break;
            default:
                throw new IllegalArgumentException("Tipo de imagen no soportado: " + image.getType());
        }
        
        // Crear Mat
        mat = new Mat(image.getHeight(), image.getWidth(), type);
        
        // Obtener datos de la imagen
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        
        // Si es ABGR, necesitamos convertir a BGRA para OpenCV
        if (channels == 4) {
            byte[] bgra = new byte[data.length];
            for (int i = 0; i < data.length; i += 4) {
                bgra[i] = data[i + 1];     // B
                bgra[i + 1] = data[i + 2]; // G
                bgra[i + 2] = data[i + 3]; // R
                bgra[i + 3] = data[i];     // A
            }
            mat.data().put(bgra);
        } else {
            // Para otros formatos, copiar directamente
            mat.data().put(data);
        }
        
        return mat;
    }
    

// Método alternativo más simple usando colores predefinidos
    public Mat prepararImagenColorSimple(List<List<Double>> matriz) {
        int rows = matriz.size();
        int cols = matriz.get(0).size();

        Mat image = new Mat(cols, rows, BufferedImage.TYPE_INT_RGB);

        // Encontrar valor máximo
        double maxValue = 0;
        for (List<Double> row : matriz) {
            for (Double value : row) {
                if (value != null) {
                    maxValue = Math.max(maxValue, value);
                }
            }
        }

        // Dibujar pixels con colores
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Double value = matriz.get(y).get(x);

                if (value != null && value > 0) {
                    // Normalizar valor entre 0 y 1
                    float normalized = (float) (value / maxValue);

                    // Crear color usando HSB (Hue, Saturation, Brightness)
                    // Hue va de 0.7 (azul) a 0.0 (rojo)
                    float hue = 0.7f * (1.0f - normalized);
                    int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);

                    //image.setRGB(x, y, rgb);
                    image.put(y, x,rgb);
                } else {
                    // Negro para valores nulos
                    image.setRGB(x, y, 0);
                }
                /*// En lugar de BufferedImage, crear directamente un Mat
Mat image = MatColorOperations.prepararImagenColorConMat(matriz);

// Ahora puedes usar este Mat con ColorIsobarDrawer
ColorIsobarDrawer drawer = new ColorIsobarDrawer(image, colorToValueMap);
drawer.drawColorIsobars();

// Obtener resultado
Mat resultado = drawer.getImageWithIsobars();

// Si necesitas BufferedImage al final
BufferedImage bufferedImage = matToBufferedImage(resultado);

// Liberar memoria
image.release();
resultado.release();*/
            }
        }

        return image;
    }

    /*mostra la imatge i imprimeix a consola la cantitat de disparos que has de fer en el punt on clickes */
    public void imprimirImagen(Mat imagen, List<List<Double>> matrix) {

        String windowName = "Mapa de Ablacion Corneal";
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);
//        BufferedImage bugImage= matToBufferedImage(image);
//        bugImage = prepararImagenColorSimple(matrix);
       
        //opencv_highgui.resizeWindow(windowName, 800, 600);

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

    public void addIsobaras(Mat grayImage, Mat colorImage, List<List<Double>> matrix, int numIsobaras) {
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();

        // Obtindre valor maxim dels elements de la matriu
//        double maxValue = matrix.stream()
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);

        /*passar aixo a disparos-> maxValue ha de ser maxShoots*/
        double maxValue = calc.maxShoots(matrix);
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);

        for (int i = 1; i < numIsobaras; i++) {
            double thresholdValue = maxValue * i / numIsobaras;
            //System.out.println("maxValue: "+maxValue+"thresholdValue= "+thresholdValue+" i= "+i+" numeroIsobaras: "+numIsobaras);

            // Convertimos el valor de isobara a escala de grises [0-255]
            double thresholdGray = thresholdValue * 255.0 / maxValue;

            // Umbralizar imagen -> pasar els pixels a blanc o negre
            Mat thresholded = new Mat();
            opencv_imgproc.threshold(grayImage, thresholded, thresholdGray, 255, opencv_imgproc.THRESH_BINARY);

            // Encontrar contornos
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresholded, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

            // Dibujar todos los contornos encontrados
            for (int j = 0; j < contours.size(); j++) {
                Scalar color = new Scalar(0, 0, 0, 0); //colo isobara(Negro)

                // Creamos una capa temporal (overlay)
                Mat overlay = colorImage.clone();

                // Dibujamos el contorno sobre la capa
                opencv_imgproc.drawContours(overlay, contours, j, color, 1, opencv_imgproc.LINE_8, hierarchy, 0, new Point());

                // Mezclamos (blend) la capa con la imagen original para simular línea más fina
                double alpha = 0.3;
                opencv_core.addWeighted(overlay, alpha, colorImage, 1.0 - alpha, 0, colorImage);
            }

        }
    }

    public void addIsobarasPersonalizadas(Mat grayImage, Mat colorImage, List<List<Double>> matrix, double[] niveles, Scalar[] colores, double alpha) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        // Calcular valor máximo de la matriz
        double maxValue = matrix.stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .max().orElse(1.0);

        for (int i = 0; i < niveles.length; i++) {
            double thresholdValue = niveles[i];

            // Convertir a escala de grises (0-255)
            double thresholdGray = thresholdValue * 255.0 / maxValue;

            // Umbralizar imagen
            Mat thresholded = new Mat();
            opencv_imgproc.threshold(grayImage, thresholded, thresholdGray, 255, opencv_imgproc.THRESH_BINARY);

            // Buscar contornos
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresholded, contours, hierarchy,
                    opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

            // Obtener color correspondiente
            Scalar color = colores[i];

            // Dibujar cada contorno con alpha blending
            for (int j = 0; j < contours.size(); j++) {
                Mat overlay = colorImage.clone();

                opencv_imgproc.drawContours(overlay, contours, j, color, 1, opencv_imgproc.LINE_8, hierarchy, 0, new Point());

                opencv_core.addWeighted(overlay, alpha, colorImage, 1.0 - alpha, 0, colorImage);
            }
        }
    }

    public void setPaciente(String nombre) {

    }

}
