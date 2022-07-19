/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mejorana.Orondo2.OrondoDb;

import java.util.ArrayList;

/**
 *
 * @author Raul Alzate
 */
public class POSdocument {
    
    public String nit;
    public String razon_social;
    public String fecha;
    
    public ArrayList<ItemVenta> items;
    public int totalValor;
    
    public String resolucionDian;
    
    /**
     * si se tienen varias sedes es obligatorio diferenciar la sede de emision
     * de la factura usando un codigo alfanumerico
     */
    public String consecutivo;
}
