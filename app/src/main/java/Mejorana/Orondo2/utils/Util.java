/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Orondo.utils;

/**
 *
 * @author Raul Alzate
 */
public class Util {
    
    public static double dividir(int a, int b){
        return (a*1.0)/(b*1.0);
    }
    
    /**
     * este metodo hace un redondeo especial en el sentido que
     * retorna el entero superior mas cercano tal que 
     * @param a
     * @return 
     */
    public static int COP_Round(int a){
        int r=a;
        try{
            String[] lx = Integer.toString(a).split("");
            int dg = Integer.parseInt(lx[lx.length-2]+lx[lx.length-1]);
            if(dg>50) r = a+(100-dg);
            if(dg>0 && dg<50) r = a+(50-dg);
        } catch(ArrayIndexOutOfBoundsException e){
            //do nothing, just returns the same input
        }
        return r;
    }
    
}
