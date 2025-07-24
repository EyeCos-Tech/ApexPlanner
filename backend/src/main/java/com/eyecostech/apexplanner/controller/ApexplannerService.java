
package com.eyecostech.apexplanner.controller;

import com.eyecostech.apexplanner.Apexplanner;
import com.eyecostech.apexplanner.Paciente;

/**
 *  AQUI HAN D?ANAR LES PETICIONS DEL CONTROLLER (QUE LES REP DEL FRONT) I  ENVIAR-LES AL BACK
 * @author Pau Savall
 */
public class ApexplannerService {
    
    Apexplanner apex;
    
    
    public Paciente getPaciente (String pacienteId){
        
        
        return apex.getPaciente();
    }
    
}
