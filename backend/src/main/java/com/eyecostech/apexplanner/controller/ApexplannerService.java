
package com.eyecostech.apexplanner.controller;

import com.eyecostech.apexplanner.Apexplanner;
import static com.eyecostech.apexplanner.Apexplanner.getImageAnalizer;
import com.eyecostech.apexplanner.Paciente;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 *  AQUI HAN D'ANAR LES PETICIONS DEL CONTROLLER (QUE LES REP DEL FRONT) I  ENVIAR-LES AL BACK
 * @author Pau Savall
 */
public class ApexplannerService {
    
    Apexplanner apex;
    
    
    public Paciente getPaciente (String pacienteId){
        
        
        return apex.getPaciente();
    }
    public double getPowerValue(){
        double value;
        value= apex.getLaser().getPotencia();
        
        
        return value;
    }
    
    public void setPowerValue(double value){
        apex.getLaser().setPotencia(value);
    }
    
    public Mat getColorImage(){
        Mat imagen;
        List<List<Double>> matriz= apex.getCsv().crearMatriz(apex.getPaciente().getPath());
        imagen= apex.getCsv().prepararImagen(matriz, apex.getPaciente(), apex.getLaser());
        return imagen;
    }
    public void setPaciente(String pacienteId) {
    
            apex.setPaciente(pacienteId);
        
    }
    
    
}
