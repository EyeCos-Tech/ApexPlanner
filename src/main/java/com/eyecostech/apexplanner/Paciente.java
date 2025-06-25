package com.eyecostech.apexplanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Patient management class with automatic directory structure creation
 *
 * @author Pau Savall
 */
public class Paciente {

    public String nombre;
    public String apellidos;
    public String path; //directori de l'arxiu .csv
    public String edad;
    //public String directorioBase = "C:/Users/Usuario/Documents/Topografias/OPD Scan III/";
    public String directorioBase = "C:\\Users\\Usuario\\WORCKSPACES\\NetBeans26\\apexplanner\\pacientes";
    public String directorioImageList;
    public String directorioCsv;
    public String directorioDeGuardado;
    public List<List<Double>> matriz;

    /**
     * CONSTRUCTOR GETTERS AND SETTERS
     */
    public Paciente(String nombre, String apellidos, String edad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.path = setPath(nombre);
        this.edad = edad;
    }

    public Paciente(String nombre) {
        this.nombre = nombre;
        System.out.println("nombre: " + nombre);
        this.path = setPath(nombre);
    }

    public List<List<Double>> getMatriz() {
        return matriz;
    }

    public void setMatriz(List<List<Double>> matriz) {
        this.matriz = matriz;
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

    public String getDirectorioImageList(String nombre) {
        try {
            // Usar el nombre del paciente actual (this.nombre)
            String nombreLwr = this.nombre.toLowerCase();

            // Crear path
            Path patientPath = Paths.get(directorioBase, nombreLwr);
            Path imgListPath = patientPath.resolve("imgList");

            // Crear directorio si no existe
            if (!Files.exists(imgListPath)) {
                Files.createDirectories(imgListPath);
                System.out.println("Directorio creado: " + imgListPath);
            }

            return imgListPath.toString();

        } catch (Exception e) {
            System.err.println("Error al obtener/crear directorio: " + e.getMessage());
            return null;
        }
    }

    public String getDirectorioCsv() {
        try {
            String nombreLwr = this.nombre.toLowerCase();
            Path patientPath = Paths.get(directorioBase, nombreLwr);
            Path csvPath = patientPath.resolve("csv");

            if (!Files.exists(csvPath)) {
                Files.createDirectories(csvPath);
                System.out.println("Directorio CSV creado: " + csvPath);
            }

            // Retornar la ruta completa al archivo CSV
            return csvPath.resolve(nombreLwr + ".csv").toString();
        } catch (Exception e) {
            System.err.println("Error al obtener/crear directorio CSV: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the path for the patient and creates the necessary directory
     * structure Creates the following structure: -
     * directorioBase/nombrePaciente/ - directorioBase/nombrePaciente/csv/ -
     * directorioBase/nombrePaciente/imgList/
     *
     * @param nombre Patient name
     * @return Full path to the patient's CSV file
     */
    public String setPath(String nombre) {
        try {
            // Convert name to lowercase for directory
            String nombreLowerCase = nombre.toLowerCase();

            // Create patient base directory
            Path patientDir = Paths.get(directorioBase, nombreLowerCase);
            if (!Files.exists(patientDir)) {
                Files.createDirectories(patientDir);
                System.out.println("Created patient directory: " + patientDir);
            }

            // Create csv subdirectory
            Path csvDir = patientDir.resolve("csv");
            if (!Files.exists(csvDir)) {
                Files.createDirectories(csvDir);
                System.out.println("Created CSV directory: " + csvDir);
            }

            // CAMBIO IMPORTANTE: Retornar la ruta completa al archivo CSV, no solo al directorio
            // Asumiendo que el archivo CSV tiene el mismo nombre que el paciente
            return csvDir.toString() + File.separator + nombreLowerCase + ".csv";

        } catch (IOException e) {
            System.err.println("Error creating directory structure: " + e.getMessage());
            e.printStackTrace();

            // Return default path even if creation fails
            // CAMBIO: También aquí incluir el nombre del archivo
            return directorioBase + nombre.toLowerCase() + "/csv/" + nombre.toLowerCase() + ".csv";
        }
    }

    /**
     * Checks if the patient directory structure exists
     *
     * @return true if all directories exist, false otherwise
     */
    public boolean verifyDirectoryStructure() {
        String nombreLowerCase = nombre.toLowerCase();
        Path patientDir = Paths.get(directorioBase, nombreLowerCase);
        Path csvDir = patientDir.resolve("csv");
        Path imgListDir = patientDir.resolve("imgList");

        return Files.exists(patientDir)
                && Files.exists(csvDir)
                && Files.exists(imgListDir);
    }

    /**
     * Gets the path to the imgList directory
     *
     * @return Path to imgList directory
     */
    public String getImgListPath() {
        String nombreLowerCase = nombre.toLowerCase();
        return Paths.get(directorioBase, nombreLowerCase, "imgList").toString();
    }

    /**
     * Gets the path to the csv directory
     *
     * @return Path to csv directory
     */
    public String getCsvDirPath() {
        String nombreLowerCase = nombre.toLowerCase();
        return Paths.get(directorioBase, nombreLowerCase, "csv").toString();
    }
}
