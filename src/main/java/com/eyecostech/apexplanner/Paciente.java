package com.eyecostech.apexplanner;

import static com.eyecostech.apexplanner.Apexplanner.path;
import java.io.File;
import java.util.HashSet;

/**
 *
 * @author Pau Savall
 */
public class Paciente {

    public String nombre;
    public String apellidos;
    public String path;
    public String edad;
    public String directorioBase= "C:/Users/Usuario/Documents/Topografias/OPD Scan III/";

    /**
     * CONSRTUCTOR GETTERS Y SETTERES*
     */
    public Paciente(String nombre, String apellidos, String edad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        path= setPath(nombre);
        this.edad = edad;
    }

    public Paciente(String nombre) {
        this.nombre = nombre;
        System.out.println("nombre: "+nombre);
        path= setPath(nombre);        
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPath() {
        return path;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String setPath(String nombre) {
        
        String dir= directorioBase+nombre.toLowerCase()+"/csv/"+nombre.toLowerCase()+".csv";
        return dir;     
    }

}
