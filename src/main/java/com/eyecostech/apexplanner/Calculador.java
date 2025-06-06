package com.eyecostech.apexplanner;

import static com.eyecostech.apexplanner.Apexplanner.calc;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * CALCULAR LES EQUVALENCIES ENTRE MM I SHOOTS CALCULAR LA RELACIO ENTRE
 * POTENCIA DEL LASER I ABLACIO
 *
 * @author Pau Savall
 */
public class Calculador {

    int µ = 0;
    public static final int MAXIMO_COLOR = 255; //maixim valor rgb d'un color ->Blanc
    public static final int MINIMO_COLOR = 0; //minim valor rgb d'un color -> Negre

    public double total = 0;

    /*Alt + 0181*/
 /*DEMANA LA POTENCIA DEL LASER*/
    public int setPotenciaLaser(String potencia) {
        int numeroPotencia;

        if (potencia == "") {
            numeroPotencia = 193;
            System.out.println("Longitud de oda de laser Standard: 193");

        } else {

            numeroPotencia = Integer.parseInt(potencia);
        }

        System.out.println("Longitud de onda del laser: " + numeroPotencia);
        return numeroPotencia;
    }

    /*CALCULA QUAN ABLACIONA (en mm) AMB UN SHOOT EL LASER SEGONS LA POTENCIA*/
    public double mmXShoot(int potencia) {
        double referenciaPow = 193;
        double referenciaAbl = 0.00025;
        
        /*regla de 3 amb les referencies d'ablacio dels laser de 193*/
        double ablacion = (referenciaAbl / referenciaPow) * potencia;//???
        DecimalFormat df = new DecimalFormat("#.####");
        //System.out.println("mm d'ablacio per disparo: " + df.format(ablacion));

        return ablacion;
    }

    /*CALCULA SEGONS ELS mm DE ABLACIO QUE NECESSITA QUANS DISPAROS FAN FALTA*/
    public double numeroShoots(String mm, double mmXShoot) {
        if (mm.equals("")) {
            mm = "0.00025";
        }
        //System.out.println("mmXShoot= "+ mmXShoot+" mm= "+mm);
        //DecimalFormat df = new DecimalFormat("#.####");
        //System.out.println("Ablacio necesaria (mm): " + df.format(mm));
        //System.out.println("Ablacio necesaria (mm): " + mm);

        Double milimetros = 0.0;

        milimetros = Double.parseDouble(mm);

        double shoots = Math.ceil(milimetros / mmXShoot);// redondeig cap a sota
        //double shoots = milimetros / mmXShoot;

        BigDecimal bd = new BigDecimal(shoots);//Redondejem els decimals del no. de shoots
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        double shootsRound = bd.doubleValue();

        DecimalFormat df = new DecimalFormat("#.#######");
        //System.out.println("Per ablacionar: " + mm + " mm \nnecessito: " + shootsRound + " disparos\n");
        System.out.println("Per ablacionar: " + df.format(milimetros) + " mm \nnecessito: " + shootsRound + " disparos\n");
        total = total + shootsRound;

        return shoots;
    }

    /*calcula el numero de shoots segons els milimetres a ablacionar (mm a ablacionar, potencia d'ablacio (mm que ablaciona en un shoot) del laser )*/
    public double numeroShoots(double mm, double mmXShoot) {

        //System.out.println("mmXShoot= "+ mmXShoot+" mm= "+mm);
        //DecimalFormat df = new DecimalFormat("#.####");
        //System.out.println("Ablacio necesaria (mm): " + df.format(mm));
        //System.out.println("Ablacio necesaria (mm): " + mm);
        Double milimetros = 0.0;

        milimetros = mm;

        double shoots = Math.ceil(milimetros / mmXShoot);// redondeig cap a sota
        //double shoots = milimetros / mmXShoot;

        BigDecimal bd = new BigDecimal(shoots);//Redondejem els decimals del no. de shoots
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        double shootsRound = bd.doubleValue();

        DecimalFormat df = new DecimalFormat("#.#######");
        //System.out.println("Per ablacionar: " + mm + " mm \nnecessito: " + shootsRound + " disparos\n");
        //System.out.println("Per ablacionar: " + df.format(milimetros) + " mm \nnecessito: " + shootsRound + " disparos\n");

        return shoots;
    }

    public double calcularNumeroShootsTotal(List<List<Double>> matriz) {
        int rows = matriz.size();
        int cols = matriz.get(0).size();
        double total = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double ablacion = matriz.get(i).get(j);
                double shoots = numeroShoots(ablacion, calc.mmXShoot(193));
                System.out.println(shoots);
                total = total + shoots;
            }
        }
        //System.out.println("total: "+total);

        return total;
    }

    /*retorna el numero de shoots del punt que te mes shoots de tota la matriu */
    public double maxShoots(List<List<Double>> matriz) {
        double maxShoots = 0;
        int rows = matriz.size();
        int cols = matriz.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double ablacion = matriz.get(i).get(j);
                double shoots = numeroShoots(ablacion, calc.mmXShoot(193));
                //double shoots = numeroShoots(ablacion, calc.mmXShoot(213));
                if (shoots >= maxShoots) {
                    maxShoots = shoots;
                }
            }
        }

        //System.out.println("el maxim de shoots que te algun punt de la matriu es : " + maxShoots);

        return maxShoots;
    }

}
