package com.eyecostech.apexplanner;

/**
 * ENCARREGAT DE CREAR I SERVIR LES IMATGES
 * @author Pau Savall
 */

import static com.eyecostech.apexplanner.Apexplanner.calc;
import static com.eyecostech.apexplanner.Apexplanner.csv;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.opencv.global.opencv_core;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

public class ImagenesService {

    public List<ImageIcon> cargarImagenes(String path) {
        List<ImageIcon> imagenes = new ArrayList<>();
        File folder = new File(path);

        if (!folder.exists()) {
            return imagenes; // Retorna lista vacía si no existe
        }

        // Lista temporal para ordenar
        List<File> archivos = new ArrayList<>();
        for (File file : folder.listFiles((dir, name)
                -> name.toLowerCase().endsWith(".png"))) {
            archivos.add(file);
        }

        // Ordenar por número
        archivos.sort((f1, f2) -> {
            int num1 = obtenerNumero(f1.getName());
            int num2 = obtenerNumero(f2.getName());
            return Integer.compare(num1, num2);
        });

        // Cargar imágenes
        for (File file : archivos) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            imagenes.add(icon);
        }

        return imagenes;
    }

    private int obtenerNumero(String nombreArchivo) {
        String nombreSinExtension = nombreArchivo.split("\\.")[0];
        String numeroStr = nombreSinExtension.replaceAll("\\D+", "");
        return numeroStr.isEmpty() ? 0 : Integer.parseInt(numeroStr);
    }

    public BufferedImage girarImagen90Izquierda(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Crear nueva imagen con dimensiones invertidas
        BufferedImage imagenGirada = new BufferedImage(height, width, original.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Mover píxeles: columna -> fila inversa
                imagenGirada.setRGB(y, width - 1 - x, original.getRGB(x, y));
            }
        }

        return imagenGirada;

    }

    public void pintarPunto(int x, int y, double valor, BufferedImage image, int index) {

        double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
        //if (shoots > 0.0 && shoots <= index) { //pinta el fondo exterior del cercle blanc => te un pixel blanc al centre
        if (shoots >= 0.0 && shoots <= index) { //pinta el fondo exterior del cercle negre => no te un pixel blanc al centre
            image.setRGB(y, x, Color.BLACK.getRGB());
            //image.setRGB(x, y, Color.BLACK.getRGB());
            //System.out.println("blanc");
        } else {
            image.setRGB(y, x, Color.WHITE.getRGB());
            //System.out.println("negre");
        }
    }

    public BufferedImage crearImagen(List<List<Double>> matriz, int indice) {

        int rows = matriz.size();
        int cols = matriz.get(0).size();

        BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i <= cols - 1; i++) {

                for (int j = 0; j < rows - 1; j++) {

                    double valor = csv.leerPunto(i, j, matriz);
                    pintarPunto(i, j, valor, imagen, indice);

                }
            
            }
            
            /*forçar a negre el punt del centre*/
            int centroX = imagen.getWidth() / 2;
            int centroY = imagen.getHeight() / 2;
            // Si el punto del centro sigue siendo blanco, lo pintamos manualmente
            //imagen.setRGB(centroX, centroY, Color.WHITE.getRGB());
            //imagen.setRGB(centroX, centroY, Color.BLACK.getRGB());

            //pintarContorno(imagen, matriz); //necessari si es pinte l'exterior del cercle en blanc

            //lista.add(imagen);//guardar imatge 
            //            /*comprobar el colore del punt del centre*/
//            int centroX = imagen.getWidth() / 2;
//            int centroY = imagen.getHeight() / 2;
//            int rgb = imagen.getRGB(centroX, centroY);
//            System.out.println("Color en el centro (frame " + indice + "): " + Integer.toHexString(rgb));
        return imagen;

    }
        /*pintar el contorn de l'ull*/
    public void pintarContorno(BufferedImage image, List<List<Double>> matriz) {
        int altura = image.getHeight();
        int amplada = image.getWidth();
        int radio = 124; // ==========> Com extreure el radi de l'ull de la imatge?????????????
        //int radio = (int)  calcularRadio(matriz); 

        int centroX = amplada / 2;
        int centroY = altura / 2;

        // Obtener el contexto gráfico
        Graphics2D g2d = image.createGraphics();
        // Activar antialiasing para suavizar bordes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Configurar color del borde del círculo
        g2d.setColor(Color.BLACK);
        //g2d.setColor(Color.WHITE);

        // Dibujar un óvalo (círculo) vacío
        g2d.drawOval(centroX - radio, centroY - radio, radio * 2, radio * 2);

        // Liberar recursos gráficos
        g2d.dispose();

    }
    
    public Mat crearImagenColor(List<List<Double>> matrix){


        int rows = matrix.size();
        int cols = matrix.get(0).size();
        Mat image = new Mat(rows, cols, opencv_core.CV_8UC1);

        /*FET A PARTIR DELS VALORS (ablacio) DE CADA PUNT DE LA MATRIU*/
        // Escalado
//        double maxValue = matrix.stream()
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                int grayValue = (int) (255.0 * matrix.get(y).get(x) / maxValue);
//                grayValue = Math.min(255, Math.max(0, grayValue));
//                image.ptr(y, x).put((byte) grayValue);
//            }
//        }

        /*FET A PARTIR DELS NUMEROS DE SHOOT DE CADA PUNT DE LA MATRIU*/
        // Escalado
        double maxValue = calc.maxShoots(matrix);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int grayValue = (int) (255.0 * calc.numeroShoots(matrix.get(y).get(x), calc.mmXShoot(193)) / maxValue);
                grayValue = Math.min(255, Math.max(0, grayValue));
                image.ptr(y, x).put((byte) grayValue);
            }
        }
        System.out.println("el maxim de shoots que te algun punt de la matriu es : " + maxValue);

        // Colormap:c pasar la image de grisos a color
        Mat colorMap = new Mat();
        opencv_imgproc.applyColorMap(image, colorMap, opencv_imgproc.COLORMAP_JET);
        //addIsobaras(image, colorMap, matrix, 20); // 10 isobaras
        image = colorMap;

        return image;
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

}
