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
}

//    public Image bufferedToImage() {
//        BufferedImage bufferedImage = matToBufferedImage(mat);
//        Image image = (Image) bufferedImage; // Cast directo o simplemente
//        Image image = bufferedImage; // Conversión implícita
//        return null;
//        
//    }

