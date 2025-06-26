package com.eyecostech.apexplanner;

import java.util.Scanner;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASE PRINCIPAL I MAIN CLASSE PRINCIPAL DEL SPRINGBOOT
 *
 * @author Pau Savall
 */
@SpringBootApplication // ← SPRING BOOT
public class Apexplanner {

    private static Apexplanner instancia;

    static int pow = 193; //potencia del laser
    static double ab = 0.00025; //ablacio del laser per disparo
    static double numeroShoots = 1;
    static BufferedImage imagen;
    public static Calculador calc = new Calculador();
    static ImageAnalizer imageAnalizer = new ImageAnalizer();
    static int[] pixel;
    static JFrame frame;
    static CsvAnalizer csv = new CsvAnalizer();
    static ImageListCreator imageList;
    static FrameDeImagenes frameImg;
    //static Vista3DSimulada ventata3d;
    static Paciente paciente;
    static String path;
    private ImageService imagenesService = new ImageService();
    public static Laser laser;
    //List<List<Double>> matriz;

    //private Nomograma nomograma;
    public Apexplanner() {
        this.laser= new Laser();

    }

    public Apexplanner(String nombre) {
        System.out.println("=== CREANDO INSTANCIA DE APEXPLANNER ===");
        System.out.println(path);

        //matriz = csv.crearMatriz(path);
        this.paciente = new Paciente(nombre);
        this.path = paciente.getPath();
        this.laser= new Laser();

    }

//    public Nomograma getNomograma() {
//        if (nomograma == null) {
//            nomograma = new Nomograma();
//        }
//        return nomograma;
//    }
    public static Apexplanner getInstance() {
        if (instancia == null) {
            instancia = new Apexplanner();
        }
        return instancia;
    }

    public static Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(String nombre) {
        paciente = new Paciente(nombre);
        path = paciente.getPath();
    }

    public static String getPath() {
        return path;
    }

    public static FrameDeImagenes getFrameImg() {
        return frameImg;
    }

    public static void setFrameImg(FrameDeImagenes frameImg) {
        Apexplanner.frameImg = frameImg;
    }

    public void solicitudDeClaculos(Scanner sc,Laser laser) {
//        System.out.println("Dame la longtud de onda del LASER: ");
//        String potencia = sc.nextLine();
        System.out.println("Dame la cantidad de mm a ablacionar: ");
        String ablacionNecesaria = sc.nextLine();
//        Double numero = null;
//
//        while (numero == null) {
//            try {
//                numero = Double.valueOf(ablacionNecesaria);
//                System.out.println("Número convertido: " + numero);
//
//            } catch (NumberFormatException e) {
//                System.out.println("Error: El texto no es un número válido.");
//                System.out.println("Dame la cantidad de mm a ablacionar: ");
//                ablacionNecesaria = sc.nextLine();
//                numero = null;
//                
//            }
            
            //String ablacionNecesaria= "0.90";//mm
            System.out.println("tegido a ablacionar: "+ablacionNecesaria);

            pow = calc.setPotenciaLaser(laser);
            System.out.println("potencia laser: "+pow);
            
            ab = calc.mmXShoot(laser);            
            System.out.println("ablacion x shot: "+ab);
            
            numeroShoots = calc.numeroShoots(ablacionNecesaria, ab);
            System.out.println("numero de shots: "+numeroShoots);

        //}

    }

    public static CsvAnalizer getCsv() {
        return csv;
    }

    public void iniciarSpringboot(String[] args) {
        System.out.println("\n INICIANDO APLICACIÓN SPRING BOOT...");
        System.out.println("=====================================");
        /**
         * INICIAR SPRING BOOT *
         */
        SpringApplication.run(Apexplanner.class, args);
        System.out.println("\n APLICACIoN INICIADA CORRECTAMENTE");
        System.out.println("=====================================");
        System.out.println(" URL Principal: http://localhost:8080");
        System.out.println(" Documentacion: http://localhost:8080/info");
        System.out.println(" Para parar: Ctrl+C en consola");
        System.out.println("=====================================\n");

    }

    public FrameDeImagenes iniciarFrameSliderImagenes(String ruta) {
        String directorio = paciente.getDirectorioImageList(paciente.getNombre());
        frameImg = new FrameDeImagenes(directorio);
        return frameImg;
    }

    /*INICIA EL FRAME DE IMAFENES PERO SENSE TREURE EL FRAME A PANTALLA NOMER RETORNA UNA LISRT<iMAGEiCON>*/
    public FrameDeImagenes iniciarImagenes(String ruta) {
        String directorio = paciente.getDirectorioImageList(paciente.getNombre());
        frameImg = new FrameDeImagenes(directorio, true);
        return frameImg;
    }

    public static String getImagePath() {
        return paciente.getDirectorioImageList(paciente.getNombre());
    }

    public List<ImageIcon> getArrayImg() {
        String directorio = getImagePath();

        return imagenesService.cargarImagenes(directorio);
    }

    public static void crearLlistaImatges(String ruta) {
        try {
//            Path pathCsv = Paths.get(ruta);
//            Path carpetaPaciente = pathCsv.getParent().getParent();
//
//            // Crear path para carpeta img
//            Path carpetaImg = carpetaPaciente.resolve("imgList");
//
//            // Crear la carpeta (crea también directorios padres si no existen)
//            Files.createDirectories(carpetaImg);

            System.out.println("CREATROR: " + ruta);
            imageList = new ImageListCreator(paciente.getDirectorioImageList(paciente.getNombre()), paciente);
            ArrayList<BufferedImage> lista = imageList.crearListaImagenes(imageList.getMatriz(), laser);
            imageList.guardarImagenes(lista);

        } catch (Exception e) {
            e.printStackTrace(); // mostra la linea on ha saltat l'error
        }
    }

    public static void main(String[] args) {
        Apexplanner obj = Apexplanner.getInstance();
        obj.setPaciente("ari");
        csv.setNombre(obj.getPaciente().getNombre());

        Scanner sc = new Scanner(System.in);

//        FrameDeImagenes frame = obj.iniciarImagenes(getImagePath());
//        frame.cargarImagenes(getImagePath());
//        List<ImageIcon> array = new FrameDeImagenes(getImagePath(), true).getArrayImagenes(getImagePath());

        /*PROBA PER COMPROBAR EL CONTINGUT DE L'ARRAY*/
//        for (ImageIcon elemento : array) {
//            System.out.println(elemento+" 0 ");
//        }
//        System.out.println("numero elemetos array "+array.size());
//        for (int i = 0; i < array.size(); i++) {
//            System.out.println("Elemento " + i + ": " + array.get(i));
//        }
        /**
         * INICIALITZAR SPRINGBOOT*
         */
//        obj.getArrayImg();
/*SPRINGBOOT*/        obj.iniciarSpringboot(args);

        /*CALCULATOR*/
        //obj.solicitudDeClaculos(sc);
//        /*IMAGE ANALIZER*/
//        frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        imagen = imageAnalizer.cargarImagen("C:/Users/Usuario/WORCKSPACES/NetBeans26/apexplanner/img/gris.png"); // Cargar imagen
//        pixel= imageAnalizer.colorPixel(100, 150, imagen); //Diu el color en RGB del pixel 100/150
//        imageAnalizer.mostrarImagen(imagen, frame); //mostrar imatge i dir el color en RGB del pixel pulsat pel mouse
//        System.out.println("Codigo color pixel " + 100 + " " + 150 + ": \nRGB:" + pixel[0] + "/" + pixel[1] + "/" + pixel[2]);
        //System.out.println("per corretgir 4 dioptries: " + calc.mmXDioptria(5) + " mm d'ablacio"+"\nper tant: "+calc.numeroShoots(calc.mmXDioptria(5), calc.mmXShoot(pow))+" shoots");
        //System.out.println("per corretgir 4 dioptries: " + calc.mmXDioptriaFormulaMunnerlyn(-4, 6.5) + " mm d'ablacio sxegons la formula de Munnerlyn");
//        

        /*CSV ANALIZER*/
 /*CREAR MATRIZ*/        //List<List<Double>> matriz = csv.crearMatriz(paciente.getDirectorioCsv());
//        csv.leerPunto(150, 265, matriz);
//        List<Double> rutaLaser= sPlanner.ordenarXDistancia(matriz);
//        System.out.println("BREAK!");
//        System.out.println(rutaLaser.get(5).toString());
//        double valorMaximo = csv.getMaxValue(matriz);
//        System.out.println("Valor maximo: " + valorMaximo);
//        System.out.println("Numero total de disparos: "+calc.calcularNumeroShootsTotal(matriz));
//        System.out.println("total: "+calc.calcularNumeroShootsTotal(matriz));
//        System.out.println("Maxiomo numero de disparos: " +calc.numeroShoots(String.valueOf(valorMaximo), calc.mmXShoot(193)));
//        calc.maxShoots(matriz);
/*IMAGEN EN COLOR*/        //csv.imprimirImagen(csv.prepararImagen(matriz, paciente, laser), matriz, laser);
        //csv.imprimirImagen(csv.prepararImagen(matriz), matriz);

        /*IMAGE LIST CREATOR: CREAR LLISTA D'IMATGES D'1 BIT AMB EL TRACTAMENT*/
        //crearLlistaImatges(path);
        /*JFRAME CON SLIDER PARA MOSTRAR LAS IMAGENES*/
 /*FRAME AMB SLIDE*/        //obj.iniciarFrameSliderImagenes(path);
   

        
        /*PROBA CALCULATOR*/
        //obj.solicitudDeClaculos(sc, laser);
        
        sc.close();

    }

}
//    /**
//     * NOMOGRAMA*
//     */
//    /**
//     * Calcula los parámetros de tratamiento usando el nomograma
//     *
//     * @param datosPaciente Map con los datos del paciente
//     * @return Map con los parámetros calculados
//     * @throws Exception Si hay error en los datos o cálculos
//     * @author Pau Savall
//     */
//    public Map<String, Object> calcularTratamientoConNomograma(Map<String, Object> datosPaciente) throws Exception {
//        Nomograma nomograma = getNomograma();
//        nomograma.setDatosPacienteDesdeMap(datosPaciente);
//        return nomograma.calcularTratamientoComoMap();
//    }
//
//    /**
//     * Aplica el nomograma a la matriz de ablación actual
//     *
//     * @param datosPaciente Map con los datos del paciente
//     * @return Map con la matriz ajustada y estadísticas
//     * @throws Exception Si hay error en el proceso
//     * @author Pau Savall
//     */
//    public Map<String, Object> aplicarNomogramaAMatrizActual(Map<String, Object> datosPaciente) throws Exception {
//// Primero calcular los parámetros
//        calcularTratamientoConNomograma(datosPaciente);
//
//// Obtener la matriz actual
//        List<List<Double>> matrizOriginal = csv.crearMatriz(path);
//
//// Aplicar el nomograma
//        return getNomograma().aplicarNomogramaAMatriz(matrizOriginal);
//    }
//
//    /**
//     * Valida si un paciente es apto para el tratamiento
//     *
//     * @param datosPaciente Map con los datos del paciente
//     * @return Map con el resultado de la validación
//     * @throws Exception Si hay error en los datos
//     * @author Pau Savall
//     */
//    public Map<String, Object> validarPacienteParaTratamiento(Map<String, Object> datosPaciente) throws Exception {
//        Nomograma nomograma = getNomograma();
//        nomograma.setDatosPacienteDesdeMap(datosPaciente);
//        return nomograma.validarPaciente();
//    }
//
//    /**
//     * Obtiene información sobre los factores del nomograma
//     *
//     * @return Map con información de los factores
//     * @author Pau Savall
//     */
//    public Map<String, Object> obtenerInformacionNomograma() {
//        return Nomograma.obtenerInformacionFactores();
//    }
//// Añadir estos métodos a Apexplanner.java
//
//    /**
//     * Genera las imágenes de tratamiento aplicando el nomograma
//     *
//     * @param datosPaciente Map con los datos del paciente para el nomograma
//     * @return Map con información del proceso y path de las imágenes
//     * @author Pau Savall
//     */
//    public Map<String, Object> generarImagenesConNomograma(Map<String, Object> datosPaciente) throws Exception {
//        Map<String, Object> resultado = new HashMap<>();
//
//        try {
//// 1. Calcular parámetros del nomograma
//            System.out.println("=== APLICANDO NOMOGRAMA ===");
//            Nomograma nomograma = getNomograma();
//            nomograma.setDatosPacienteDesdeMap(datosPaciente);
//            Map<String, Object> parametrosNomograma = nomograma.calcularTratamientoComoMap();
//
//// 2. Cargar matriz original
//            List<List<Double>> matrizOriginal = csv.crearMatriz(path);
//            System.out.println("Matriz original cargada: " + matrizOriginal.size() + " filas");
//
//// 3. Aplicar nomograma a la matriz
//            Map<String, Object> resultadoNomograma = nomograma.aplicarNomogramaAMatriz(matrizOriginal);
//            List<List<Double>> matrizAjustada = (List<List<Double>>) resultadoNomograma.get("matrizAjustada");
//            double factorCorreccion = (Double) resultadoNomograma.get("factorCorreccion");
//
//            System.out.println("Factor de corrección aplicado: " + factorCorreccion);
//
//// 4. Crear las imágenes con la matriz ajustada
//            imageList = new ImageListCreator(path);
//            ArrayList<BufferedImage> listaImagenes = imageList.crearListaImagenes(matrizAjustada);
//
//// 5. Guardar las imágenes
//            imageList.guardarImagenes(listaImagenes);
//
//// 6. Guardar información del nomograma en archivo de texto
//            guardarInfoNomograma(parametrosNomograma, resultadoNomograma);
//
//// 7. Preparar respuesta
//            resultado.put("status", "success");
//            resultado.put("parametrosNomograma", parametrosNomograma);
//            resultado.put("factorCorreccion", factorCorreccion);
//            resultado.put("numeroImagenes", listaImagenes.size());
//            resultado.put("pathImagenes", getImagePath());
//            resultado.put("disparosTotales", resultadoNomograma.get("disparosTotales"));
//            resultado.put("disparosMaximos", resultadoNomograma.get("disparosMaximos"));
//            resultado.put("mensaje", "Imágenes generadas con nomograma aplicado");
//
//            System.out.println("=== PROCESO COMPLETADO ===");
//
//        } catch (Exception e) {
//            System.err.println("Error al generar imágenes con nomograma: " + e.getMessage());
//            throw e;
//        }
//
//        return resultado;
//    }
//
//    /**
//     * Guarda la información del nomograma en un archivo de texto junto a las
//     * imágenes
//     *
//     * @param parametrosNomograma Parámetros calculados por el nomograma
//     * @param resultadoNomograma Resultado de aplicar el nomograma
//     * @author Pau Savall
//     */
//    private void guardarInfoNomograma(Map<String, Object> parametrosNomograma,
//            Map<String, Object> resultadoNomograma) {
//        try {
//            File csvFile = new File(path);
//            File parentDir = csvFile.getParentFile();
//            File imgDir = new File(parentDir, "imgList");
//            File infoFile = new File(imgDir, "nomograma_info.txt");
//
//            Map<String, Object> parametros = (Map<String, Object>) parametrosNomograma.get("parametrosTratamiento");
//            Map<String, Object> datosOriginales = (Map<String, Object>) parametrosNomograma.get("datosOriginales");
//
//            try (FileWriter writer = new FileWriter(infoFile)) {
//                writer.write("=== INFORMACIÓN DEL NOMOGRAMA APLICADO ===\n");
//                writer.write("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n");
//
//                writer.write("DATOS ORIGINALES DEL PACIENTE:\n");
//                writer.write("- Esfera: " + datosOriginales.get("esfera") + " D\n");
//                writer.write("- Cilindro: " + datosOriginales.get("cilindro") + " D\n");
//                writer.write("- Eje: " + datosOriginales.get("eje") + "°\n");
//                writer.write("- Edad: " + datosOriginales.get("edad") + " años\n");
//                writer.write("- Paquimetría: " + datosOriginales.get("paquimetria") + " µm\n\n");
//
//                writer.write("PARÁMETROS AJUSTADOS:\n");
//                writer.write("- Esfera corregida: " + parametros.get("esferaCorregida") + " D\n");
//                writer.write("- Cilindro corregido: " + parametros.get("cilindroCorregido") + " D\n");
//                writer.write("- Zona óptica: " + parametros.get("zonaOptica") + " mm\n");
//                writer.write("- Zona transición: " + parametros.get("zonaTransicion") + " mm\n");
//                writer.write("- Profundidad ablación: " + parametros.get("profundidadAblacion") + " µm\n\n");
//
//                writer.write("RESULTADOS:\n");
//                writer.write("- Factor de corrección: " + resultadoNomograma.get("factorCorreccion") + "\n");
//                writer.write("- Disparos totales: " + resultadoNomograma.get("disparosTotales") + "\n");
//                writer.write("- Disparos máximos en un punto: " + resultadoNomograma.get("disparosMaximos") + "\n");
//            }
//
//            System.out.println("Información del nomograma guardada en: " + infoFile.getAbsolutePath());
//
//        } catch (IOException e) {
//            System.err.println("Error al guardar información del nomograma: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Obtiene una comparación visual entre la matriz original y la ajustada
//     *
//     * @param datosPaciente Datos del paciente para el nomograma
//     * @return Map con las imágenes de comparación
//     * @author Pau Savall
//     */
//    public Map<String, Object> generarComparacionNomograma(Map<String, Object> datosPaciente) throws Exception {
//        Map<String, Object> resultado = new HashMap<>();
//
//// 1. Configurar nomograma
//        Nomograma nomograma = getNomograma();
//        nomograma.setDatosPacienteDesdeMap(datosPaciente);
//
//// 2. Cargar matriz original
//        List<List<Double>> matrizOriginal = csv.crearMatriz(path);
//
//// 3. Generar imagen de la matriz original
//        Mat imagenOriginal = csv.prepararImagen(matrizOriginal);
//
//// 4. Aplicar nomograma
//        Map<String, Object> resultadoNomograma = nomograma.aplicarNomogramaAMatriz(matrizOriginal);
//        List<List<Double>> matrizAjustada = (List<List<Double>>) resultadoNomograma.get("matrizAjustada");
//
//// 5. Generar imagen de la matriz ajustada
//        Mat imagenAjustada = csv.prepararImagen(matrizAjustada);
//
//// 6. Calcular diferencias
//        double disparosOriginales = calc.calcularNumeroShootsTotal(matrizOriginal);
//        double disparosAjustados = (Double) resultadoNomograma.get("disparosTotales");
//        double porcentajeCambio = ((disparosAjustados - disparosOriginales) / disparosOriginales) * 100;
//
//        resultado.put("imagenOriginal", imagenOriginal);
//        resultado.put("imagenAjustada", imagenAjustada);
//        resultado.put("disparosOriginales", disparosOriginales);
//        resultado.put("disparosAjustados", disparosAjustados);
//        resultado.put("porcentajeCambio", porcentajeCambio);
//        resultado.put("factorCorreccion", resultadoNomograma.get("factorCorreccion"));
//
//// Liberar memoria
//        imagenOriginal.release();
//        imagenAjustada.release();
//
//        return resultado;
//    }
//
//    /**
//     * Actualiza las imágenes existentes aplicando el nomograma
//     *
//     * @param datosPaciente Datos del paciente
//     * @return true si se actualizaron correctamente
//     * @author Pau Savall
//     */
//    public boolean actualizarImagenesConNomograma(Map<String, Object> datosPaciente) {
//        try {
//// Generar nuevas imágenes con nomograma
//            generarImagenesConNomograma(datosPaciente);
//
//// Recargar el frame de imágenes si está activo
//            if (frameImg != null) {
//                String directorio = getImagePath();
//                frameImg.cargarImagenes(directorio);
//            }
//
//            return true;
//        } catch (Exception e) {
//            System.err.println("Error al actualizar imágenes: " + e.getMessage());
//            return false;
//        }
//    }


//    public void escribirLog(String mensaje) throws IOException {
//        try {
//            // Ruta relativa: se crea en la raíz del proyecto
//            FileWriter writer = new FileWriter("log.txt");
//            writer.write("mensaje");
//            writer.close();
//            System.out.println("Log creado correctamente.");
//        } catch (IOException e) {
//            System.out.println("Error al escribir el archivo:");
//            e.printStackTrace();
//        }
//    }
//    public List<ImageIcon> getArrayImg() {
//        List<ImageIcon> array = new FrameDeImagenes(getImagePath()).getArrayImagenes(getImagePath());
//
//        /*PROBA PER COMPROBAR EL CONTINGUT DE L'ARRAY*/
////        for (ImageIcon elemento : array) {
////            System.out.println(elemento+" 0 ");
////        }
//        //System.out.println("numero elemetos array " + array.size());
//        return array;
//    }

