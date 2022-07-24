package Orondo.Net;

import com.neovisionaries.ws.client.HostnameUnverifiedException;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;


/**
 *
 * @author Raul Alzate
 * 
 * clase de orondo para ofrecer servicios de red a la aplicacion.
 * el mas importante websockets para monitorear en tiempo real las ventas
 */
public class Wsocket {
    
    public String ws_host = "ws://localhost:8080";
    
    
    private final WebSocketFactory factory;
    
    private final WebSocket ws;
    
    
    public Wsocket() throws IOException{
        
        this.factory = new WebSocketFactory().setConnectionTimeout(5000);
        
        this.ws = factory.createSocket(this.ws_host);
        
        // Register a listener to receive WebSocket events.
        ws.addListener(new WSadapter());
        
    }
    
    /**
     * se conecta al servidor de websocket
     */
    public void ConectarWS(){
        try
        {
            // Connect to the server and perform an opening handshake.
            // This method blocks until the opening handshake is finished.
            ws.connect();
            
            Map<String, String> msg = new HashMap<>();
            //msg.put(K.Key_proposito, K.PROPOSITO_ID);
            //msg.put(K.Key_body, K.ORONDO_ORIG);
            
            // se cnvierte 
            ws.sendText(JSONValue.toJSONString(msg));
        }
        catch (OpeningHandshakeException e){
            // A violation against the WebSocket protocol was detected
            // during the opening handshake.
            Logger.getLogger(Wsocket.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (HostnameUnverifiedException e){
            // The certificate of the peer does not match the expected hostname.
            Logger.getLogger(Wsocket.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (WebSocketException e){
            Logger.getLogger(Wsocket.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void SendMessage(){
        
    }
    
    
    /**
     * encapsula los metodos para eventos websocket
     */
    private class WSadapter extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket websocket, String message) throws Exception {
            System.out.println("onMessage: ${message}");
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            System.out.println("onConnected: ${headers}");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            System.out.println("onConnectError: ${exception.getMessage()}");
        }
        
        
    }
    
}
