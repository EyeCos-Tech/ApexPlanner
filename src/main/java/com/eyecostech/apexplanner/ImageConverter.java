/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner;

/**
 *
 * @author Pau Savall
 */
import java.awt.Image;
import org.bytedeco.opencv.opencv_core.Mat;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;

public class ImageConverter {

    public BufferedImage matToBufferedImage(Mat mat) {
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter converterToBufferedImage = new Java2DFrameConverter();

        Frame frame = converterToMat.convert(mat);
        BufferedImage bufferedImage = converterToBufferedImage.convert(frame);
        return bufferedImage;

        // Los convertidores se pueden reutilizar, pero es buena práctica cerrarlos
        // cuando ya no los necesites en tu aplicación
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
}

//    public Image bufferedToImage() {
//        BufferedImage bufferedImage = matToBufferedImage(mat);
//        Image image = (Image) bufferedImage; // Cast directo o simplemente
//        Image image = bufferedImage; // Conversión implícita
//        return null;
//        
//    }

