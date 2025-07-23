package com.eyecostech.apexplanner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Patient management class with automatic directory structure creation
 * 
 * estructura carpeta
 * 
 * projecte
 *  |
 *  |_ ***
 *  |
 *  |_ pacientes
 *      |
 *      |_nombre
 *          |_csv
 *          |
 *          |_img
 *          |
 *          |_imgList
 * 
 * @author Pau Savall
 */
public class Paciente {

    public String nombre;
    public String apellidos;
    public String path; //directori de l'arxiu .csv
    public String edad;
    //public String directorioBase = "C:/Users/Usuario/Documents/Topografias/OPD Scan III/";
    public String directorioBase = "C:\\Users\\Usuario\\WORCKSPACES\\NetBeans26\\Apexplanner\\apexplanner\\pacientes";
    public String directorioImageList;
    public String directorioCsv;
    public String directorioDeGuardado;
    public String directorioImg;
    //public List<List<Double>> matriz;
    //public File csv;

    /**
     * CONSTRUCTOR GETTERS AND SETTERS
     */
    public Paciente(String nombre, String apellidos, String edad) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.path = setPath(nombre);
        this.edad = edad;
//        csv = new File("/pacientes/csv/" + nombre + ".csv");
//        if (csv == null) {
//            System.out.println("null");
//        } else {
//            System.out.println("csv paciente: "+csv.getAbsolutePath());
//        }
    }

    public Paciente(String nombre) {
        this.nombre = nombre;
        System.out.println("nombre: " + nombre);
        this.path = setPath(nombre);
//        csv = new File("/pacientes/csv/" + nombre + ".csv");
//        if (csv == null) {
//            System.out.println("null");
//        } else {
//            System.out.println("csv paciente: "+csv.getAbsolutePath());
//        }
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
    
    public String getDirectorioImg(String nombre){
        try {
            // Usar el nombre del paciente actual (this.nombre)
            String nombreLwr = this.nombre.toLowerCase();

            // Crear path
            Path patientPath = Paths.get(directorioBase, nombreLwr);
            Path imgPath = patientPath.resolve("img");

            // Crear directorio si no existe
            if (!Files.exists(imgPath)) {
                Files.createDirectories(imgPath);
                System.out.println("Directorio creado: " + imgPath);
            }

            return imgPath.toString();

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

    public String getImgListPath() {
        String nombreLowerCase = nombre.toLowerCase();
        return Paths.get(directorioBase, nombreLowerCase, "imgList").toString();
    }

    public String getCsvDirPath() {
        String nombreLowerCase = nombre.toLowerCase();
        return Paths.get(directorioBase, nombreLowerCase, "csv").toString();
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
}
