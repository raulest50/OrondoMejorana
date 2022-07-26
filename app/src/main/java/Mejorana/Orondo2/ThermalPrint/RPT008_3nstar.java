/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mejorana.Orondo2.ThermalPrint;

import Mejorana.Orondo2.OrondoDb.ItemVenta;
import java.util.LinkedList;

import com.google.common.primitives.Bytes;
import java.awt.print.PrinterJob;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;

/**
 *
 * @author R Esteban A
 * 
 * impresora de 3nstar
 */
public class RPT008_3nstar {
    
    
    final String TITLE = """
                        ,__ __                                        
                       /|  |  |      o                                
                        |  |  |   _     __   ,_    __,   _  _    __,  
                        |  |  |  |/  | /  \\_/  |  /  |  / |/ |  /  |  
                        |  |  |_/|__/|/\\__/    |_/\\_/|_/  |  |_/\\_/|_/
                                    /|                                
                                    \\|                                
                      """;
    
    final String PRINTER_NAME = "POS-80C";
        
    final String REMISION_HEADER = 
            """
                        Mercado Saludable \n
            NIT: 43475437 
            WhastApp: 3052626126
            instagram: @mejorana
            Direccion: Av 2BN #73C-02
            Barrio: Brisas de los Alamos
            \n
            """;
        
    // byte commands para imprimir y cortar
    byte pcut[] = {0x0D, 0x0A, 0x1D, 0x56, 0x42, 0x7f};
    
    
    public void ImprimirRemision(LinkedList<ItemVenta> li)
        throws NullPointerException, PrintException{
        
        String body = "";
        
        int suma = 0;
        
        for(ItemVenta iv : li){
            if(body.length() >=47 ) body = body + iv.getDescripcion().substring(0, 46) + "\n";
            else body = body + iv.getDescripcion() + "\n";
            body = body + iv.getCantidad() + "  x  "  + iv.getUnitPrecio() + "  =  " + iv.getSubTotal() + "\n";
            suma += iv.getSubTotal();
        }
        
        body += "TOTAL A PAGAR = " + Integer.toString(suma);
        
        String text = TITLE + REMISION_HEADER + body;
        
        
        PrintService printer = GetPrintService();
        
        DocPrintJob job = printer.createPrintJob();  
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(Bytes.concat(text.getBytes(), pcut), flavor, null);
        job.print(doc, null);
        
    }
    
    
    
    /**
     * 
     * @return
     * @throws NullPointerException 
     */
    public PrintService GetPrintService() throws NullPointerException{
        // Lookup for the available print services.
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        
        PrintService printer = null;

        // Iterates the print services and print out its name.
        for (PrintService printService : printServices) {
            String name = printService.getName();
            // System.out.println("Name = " + name);
            // se busca la impresaora que coincida con el nombre
            if( name.equals(PRINTER_NAME) ) printer = printService;
        }
        
        if(printer == null) throw new NullPointerException();
        
        return printer;
    }
        
    
}
