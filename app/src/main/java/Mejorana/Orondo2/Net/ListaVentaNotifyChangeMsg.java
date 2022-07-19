package Mejorana.Orondo2.Net;

import Mejorana.Orondo2.OrondoDb.ItemVenta;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author esteban
 * 
 * Este objeto representa un mensaje de comunicacion de los puntos de venta
 * para notificar a orondo el estado actual de su lista de compra, en tiempo real.
 * el mensaje consta de una identificacion del punto de pago y de la lista
 * de itemsVenta, es decir la lista de compra. Esta informacion se transmite
 * por mqtt en formato json y este objeto permite la representacion de estos datos
 * dentro de orondo.
 */
public class ListaVentaNotifyChangeMsg {
    
    public String mqttClientId;
    
    public LinkedList<ItemVenta> lv;
    
    
    public ListaVentaNotifyChangeMsg(String mqttClientId, LinkedList<ItemVenta> lv){
        this.mqttClientId = mqttClientId;
        this.lv = lv;
    }

    /**
     * Para pasar de Json a objeto.Hacerlo con gson requiere remover una
     * anotacion transitoria y demas asi que en este caso en particular
     * perfiero usar simpleJson.
     * @param msg este objeto en formato json
     * @exception ParseException
     */
    public ListaVentaNotifyChangeMsg(String msg) throws ParseException {
        LinkedList<ItemVenta> t_lv =  new LinkedList<>();
        // se pasa de String a objeto json para poder acceder a los respectivos keys
        JSONObject json_lvnm = (JSONObject) new JSONParser().parse(msg);
        this.mqttClientId = (String) json_lvnm.get("mqttClientId");
        JSONArray json_list = (JSONArray) json_lvnm.get("lv");
        
        json_list.forEach( (x) ->{ // se Construye cada lista ventay se agrega al LinkedList
            JSONObject json_itv = (JSONObject) x;
            ItemVenta itv = new ItemVenta(json_itv);
            t_lv.addFirst(itv);
        });
        this.lv = t_lv;
    }
}


/**
 * {"lv":
 *      [{"Cantidad":2,
    *      "UnitPrecio":1300,
    *      "fraccionado":false,
    *      "p":
    *          {"PesoUnitario":1,
    *          "_id":"1",
    *          "costo":1010,
    *          "descripcion":"AZUCAR BULTO*500",
    *          "fraccionable":false,
    *          "grupo":"normal",
    *          "iva":5.0,
    *          "keywords":"Abarrotes",
    *          "last_updt":"2020-05-09 07:52:47",
    *          "pv_mayor":1200,
    *          "pv_publico":1300},
    *      "producto_id":"1",
    *      "subTotal":2600}],
 * 
 * "mqttClientId":"cel esteban"}
 */
