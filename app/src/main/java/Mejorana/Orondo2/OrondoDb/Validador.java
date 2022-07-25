package Mejorana.Orondo2.OrondoDb;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;



/**
 *
 * @author Raul Alzate
 * clase para validacion para los objetos que se insertaran en mongodb
 */
public class Validador {
    
    
    /**
     * retorna un arrayList que en su primera posicion contiene un boolean
     * indicando si el producto es valido para su insersion en la base de 
     * datos mongodb. en la segunda posicion entrega un String 
     * describiendo que campos del producto deben ser corregidos.
     * @param p
     * @return 
     */
    public static ArrayList CodificacionValida(Producto p){
        
        dbMapper dbm = new dbMapper();
        ArrayList a = ModificacionValida(p);
        ArrayList ag = new ArrayList();
        
        boolean r = (boolean) a.get(0);
        String msg = (String) a.get(1);
        
        // verificacion codigo o _id es irrepetible
        ArrayList tmp = dbm.GetProductById(p._id);
        if( !(tmp.isEmpty()) ){
            r = false;
            msg += "\n- el codigo introducido ya esta asignado al prodcuto:\n "
                    + ((Producto) tmp.get(0)).descripcion;
        }
        
        ag.add(r);
        ag.add(msg);
        return ag;
    }
    
    /**
     * retorna un arraylist, en la primera posicion un boolean indicando
     * si es valido hacer la modificacion del producto.
     * en el segundo lugar retorna un String indicando que campos se deben 
     * corregir.
     * @param p
     * @return 
     */
    public static ArrayList ModificacionValida(Producto p){
        boolean r = true;
        String msg = "";
        ArrayList a = new ArrayList();
        
        if( p._id.isBlank() || p._id.isEmpty()){
            r=false;
            msg += "\n - el codigo del pruducto no puede estar vacio";
        }
        
        // el codigo de un producto solo puede contener caracteres alfanumericos
        if(!StringUtils.isAlphanumeric(p._id)){
            r=false;
            msg+="\n- El codigo de un producto solo puede contener caracteres de la a-z\n"
                    + "y numeros 0-9. caracteres especiales no estan permitidos";
        }
        
        if(p.costo < 0 || p.pv_mayor < 0 || p.pv_publico < 0){
            r=false;
            msg +="\n- el precio de costo, venta publico y pormayor no puedens ser numeros negativos";
        }
        
        if(p.costo >= p.pv_mayor){
            r=false;
            msg +="\n- el valor de venta al pormayor no puede ser menor que el costo del producto";
        }
        
        if(p.costo >= p.pv_publico){
            r=false;
            msg += "\n-el precio de venta al publico no puede ser menor al costo del producto";
        }
        
        if(p.pv_mayor > p.pv_publico){
            r=false;
            msg += "\n-el precio de venta pormayor no puede superar el precio de venta al publico";
        }
        
        // validacion del peso unitario deacuerdo al valor de fraccionable
        if(p.fraccionable){
            if(p.PesoUnitario <= 1 ){
                r=false;
                msg += "El peso unitario debe ser mayor o igual a 1";
            } 
        }
        
        if(!p.fraccionable){
            if(p.PesoUnitario != 1){
                r=false;
                msg += "Ha ocurrido un inconveniente con el peso unitario, por favor comuniquese con soporte tecnico";
            }
        }
        
        a.add(r);
        a.add(msg);
        return a;
    }
    
}
