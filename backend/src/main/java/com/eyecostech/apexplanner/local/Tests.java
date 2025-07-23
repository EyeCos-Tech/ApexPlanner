
package com.eyecostech.apexplanner.local;

import com.eyecostech.apexplanner.Apexplanner;

/**
 *
 * @author Tests Savall
 */
public class Tests {
     public static Apexplanner apex;
     
    public static void main(String[] args) {
        apex = new Apexplanner();
        apex.setPaciente("ari");
        apex.getCsv().setNombre(apex.getPaciente().getNombre());
        
        apex.iniciarSpringboot(args);
    
    
    }
    
}
