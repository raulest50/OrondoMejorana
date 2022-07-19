package Mejorana.Orondo2.Net;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * 
 * @author esteban
 * 
 * https://www.nociones.de/introduccion-paho-mqtt-iot/
 * https://www.infoq.com/articles/practical-mqtt-with-paho/
 * 
 * https://docs.emqx.io/broker/latest/en/development/java.html
 * 
 * String publisherId = UUID.randomUUID().toString(); para obtener id aleatorio
 */
public class MqttOrondoClient {
    
    public final String subTopic = "listasVentas/"; // topico a subcribir 
    public final String pubTopic = "orondo/comandos"; // topico a publicar
    public final String content = "Hola mundo mqtt";
    int qos = 0; // grado de prioridad
    public String broker = "tcp://localhost:1883"; // direccion del broker
    public final String clientId = "orondo"; // identificacion del cliente
    private MemoryPersistence persistence = new MemoryPersistence();
    
    private MqttClient client;
    private PushCallback pb;

    public MqttOrondoClient(Consumer onMessage) {
        pb = new PushCallback(onMessage);
    }
    
    public void Conectar() throws MqttException{
        client = new MqttClient(broker, clientId, persistence);
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        
        client.setCallback(pb);
        
        System.out.println("Connecting to broker: " + broker);
        client.connect(options);
    }
    
    public void Desconectar() throws MqttException{
        client.disconnect();
        System.out.println("Disconnected");
        client.close();
    }
    
    public void Send(String msg) throws MqttException{
        // Required parameters for message publishing
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        client.publish(pubTopic, message);
        System.out.println("Message published");
    }
    
    public void subscribirse() throws MqttException{
        // Subscribe
        client.subscribe(subTopic);
    }
    
    
    /**
     * Clase que define como se reacciona a los eventos MQTT. se debe pasar
     * a MqttOrondoClient
     */
    private class PushCallback implements MqttCallbackExtended{
        
        private Consumer onMessage;

        public PushCallback(Consumer onMessage) {
            this.onMessage = onMessage;
        }
        
        /**
         * gracias a options.setAutomaticReconnect(true); orondo se reconecta
         * de manera robusta al broker cada que hay un problema, sin embargo
         * en la desconexion se pirde la subscripcion al 
         * @param bln
         * @param string 
         */
        @Override
        public void connectComplete(boolean bln, String string) {
            try {
                System.out.println("Connected");
                subscribirse();
            } catch (MqttException ex) {
                Logger.getLogger(MqttOrondoClient.class.getName()).
                        log(Level.SEVERE, "callback connect Complete", ex);
            }
        }
        
        @Override
        public void connectionLost(Throwable cause) {
            // After the connection is lost, it usually reconnects here
            System.out.println("disconnect?you can reconnect");
        }

        @Override // se ejecuta cada que una tablet publica a ListasVentas
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String content = new String(message.getPayload());
            // The messages obtained after subscribe will be executed here
            //System.out.println("Received message topic:" + topic);
            //System.out.println("Received message Qos:" + message.getQos());
            //System.out.println("Received message content:${content}");
            onMessage.accept(content);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete---------" + token.isComplete());
        }
    }
    
}
