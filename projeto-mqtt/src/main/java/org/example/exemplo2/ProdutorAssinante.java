package org.example.exemplo2;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ProdutorAssinante implements MqttCallback {

    private final int qos = 1;
    private String topico = "mqtt/ex2";
    private MqttClient clienteMqtt;
    private MqttMessage mensagem;
    private String broker = "tcp://mqtt.eclipseprojects.io:1883";


    public ProdutorAssinante() throws MqttException {

        this.conectar();

    }

    public void conectar() throws MqttException {

        String idCliente = MqttClient.generateClientId();
        System.out.println("[*] ID do Cliente: " + idCliente);

        MqttConnectOptions opcoesDaConexao = new MqttConnectOptions();
        opcoesDaConexao.setCleanSession(true);

        this.clienteMqtt = new MqttClient(this.broker, idCliente, new MemoryPersistence());
        this.clienteMqtt.setCallback(this);
        System.out.println("[*] Conectando-se ao broker " + broker);
        this.clienteMqtt.connect(opcoesDaConexao);
        System.out.println("[*] Conectado!");

        System.out.println("[*] Inscrevendo cliente no tópico: " + topico);
        clienteMqtt.subscribe(topico, qos);
        System.out.println("[*] Incrito!");

    }

    public void desconectar() throws MqttException {
        /* Desconecta o cliente
         * */
        this.clienteMqtt.disconnect();
        System.out.println("[*] Finalizando...");
        System.exit(0);
    }

    public void publicarMensagem(String payload) throws MqttException {

        this.mensagem = new MqttMessage(payload.getBytes());
        //System.out.println("[*] Publicando no tópico: " + this.topico);

        this.mensagem.setQos(qos);
        this.clienteMqtt.publish(this.topico, this.mensagem);

        System.out.println("[*] Publicado em: " + this.topico);
    }
    /**
     * MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable causa) {
        System.err.println("[erro] Conexão com o broker foi perdida: " + causa);
        System.exit(1);
    }

    /**
     * MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    /**
     * MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topico, MqttMessage mensagem) throws MqttException {

        String marcaTempo = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("\n[--->] Uma mensagem foi recebida!" +
                "\n\t[*] Data/Hora: " + marcaTempo +
                "\n\t[*] Tópico: " + topico +
                "\n\t[*] Mensagem: " + new String(mensagem.getPayload()) +
                "\n\t[*] QoS: " + mensagem.getQos() + "\n");

    }

    public static String getMensagem(){
        return new StringBuilder()
                .append("Temperatura: ")
                .append(new Random().nextInt(20, 40))
                .append("º")
                .append(" | Umidade do ar: ")
                .append(new Random().nextInt(30, 75))
                .append("%").toString();
    }

    public static void main(String[] args) throws MqttException, InterruptedException {

        ProdutorAssinante processo = new ProdutorAssinante();

        for (int i = 0; i <= 10; i++) {
            //Thread.sleep(new Random().nextInt(500, 2500));
            processo.publicarMensagem(getMensagem());
        }
        processo.desconectar();
    }
}
