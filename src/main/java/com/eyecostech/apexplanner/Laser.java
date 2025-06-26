/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner;

/**
 *
 * @author Pau Savall
 */
public class Laser {

    double spot; //tamany disparo laser -> 50-500-1000 micras
    double potencia; // potencia laser -> 1.5 jules
    double longitudOnda; //refractiva -> 213 -> llum ultravioleta

    public Laser() {
    
        this.longitudOnda = 193; //213 pero de momento lo dejamos asi
        
    
    }

    public double getSpot() {
        return spot;
    }

    public void setSpot(double spot) {
        this.spot = spot;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    public double getLongitudOna() {
        return longitudOnda;
    }

    public void setLongitudOna(double longitudOna) {
        this.longitudOnda = longitudOna;
    }

    

    

}
/*Un láser ArF de 193 nm con un spot de 100 μm y 1.5 mJ por pulso ablatará aproximadamente 0.00287 mm (2.87 μm) de tejido corneal por pulso */
/* Un láser de 213 nm con un spot de 100 μm y 1.5 mJ por pulso ablatará aproximadamente 0.00668 mm (6.68 μm) de tejido corneal por pulso. */

/**
 Para determinar la potencia en vatios (W)  y la longitud de onda en nanómetros (nm)  de un láser ultravioleta , necesitamos aclarar algunos conceptos: 
🔹 1. Potencia del láser  

La potencia  de un láser se mide normalmente en vatios (W)  o miliwatios (mW) , no en julios (J).   

    Julios (J)  es una unidad de energía , no de potencia.
    Potencia (W)  es energía por unidad de tiempo:  
    1 W=1 J/s 
     

Por ejemplo: 

    Un láser de 5 mW  entrega 0.005 julios por segundo .
     

Si tienes la cantidad de energía (en julios) y el tiempo (en segundos), puedes calcular la potencia con: 

* Potencia(W) = Energıa(J)/Tiempo(s)​ 
 
 

🔹 2. Longitud de onda de un láser ultravioleta  

El rango ultravioleta (UV)  del espectro electromagnético va aproximadamente desde los 10 nm hasta los 400 nm . Se divide en tres categorías principales: 
UVC
	
100 – 280 nm
UVB
	
280 – 315 nm
UVA
	
315 – 400 nm
 
 

Un láser ultravioleta típico  puede tener longitudes de onda como: 

    355 nm (UVA)
    266 nm (UVC)
    248 nm (UVC)
    193 nm (UVC)
     

Ejemplos reales: 

    Láser de ArF (Fluoruro de argón)  → 193 nm
    Láser de KrF (Fluoruro de kriptón)  → 248 nm
    Láser de Nd:YAG (tercera armónica)  → 355 nm
     

✅ Ejemplo completo: 

Supongamos que tienes un láser ultravioleta de 355 nm  y quieres saber su potencia : 

    Si te dicen que emite 1 J de energía en 1 ns (nanosegundo) , entonces:
     
    Potencia(W)=1J/1×10(−9)s ​= 1×10(9)W= 1 GW (gigavatio) 
     

Pero si es un láser continuo de 5 mW , entonces: 

    Potencia = 0.005 W = 0.005 J/s 
    Longitud de onda = por ejemplo, 355 nm 
     
 **/
