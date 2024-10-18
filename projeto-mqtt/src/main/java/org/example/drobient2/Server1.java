package org.example.drobient2;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Timer;
import java.util.TimerTask;

public class Server1 implements MqttCallback {
    private final int qos = 1;
    private String topicoDrone = "mqtt/ex1"; // Tópico onde o drone publica os dados
    private String topicoServidor2 = "mqtt/ex2"; // Tópico do Servidor 2
    private String topicoServidor3 = "mqtt/ex3"; // Tópico do Servidor 3
    private MqttClient clienteMqtt;
    private String broker = "tcp://mqtt.eclipseprojects.io:1883";

    public Server1() throws MqttException {
        this.conectar();
    }

    public void conectar() throws MqttException {
        String idCliente = MqttClient.generateClientId();
        System.out.println("[*] ID do Cliente: " + idCliente);

        MqttConnectOptions opcoesDaConexao = new MqttConnectOptions();
        opcoesDaConexao.setCleanSession(true);

        this.clienteMqtt = new MqttClient(this.broker, idCliente);
        this.clienteMqtt.setCallback(this);
        System.out.println("[*] Conectando-se ao broker " + broker);
        this.clienteMqtt.connect(opcoesDaConexao);
        System.out.println("[*] Conectado!");

        System.out.println("[*] Inscrevendo cliente no tópico do drone: " + topicoDrone);
        clienteMqtt.subscribe(topicoDrone, qos);
        System.out.println("[*] Inscrito no tópico do drone!");
    }

    public void desconectar() throws MqttException {
        this.clienteMqtt.disconnect();
        System.out.println("[*] Finalizando...");
        System.exit(0);
    }

    public void messageArrived(String topico, MqttMessage mensagem) throws MqttException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\n[--->] Uma mensagem foi recebida do drone!" +
                        "\n\t[*] Tópico: " + topico +
                        "\n\t[*] Mensagem: " + new String(mensagem.getPayload()) +
                        "\n\t[*] QoS: " + mensagem.getQos() + "\n");

                // Publica a mensagem recebida nos tópicos dos Servidores 2 e e3
                try {
                    clienteMqtt.publish(topicoServidor2, mensagem);
                    clienteMqtt.publish(topicoServidor3, mensagem);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }

            }
        }, 0, 3000);
    }

    public void connectionLost(Throwable causa) {
        System.err.println("[erro] Conexão com o broker foi perdida: " + causa);
        System.exit(1);
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // Método vazio, não precisamos lidar com confirmação de entrega aqui
    }

    public static void main(String[] args) throws MqttException {
        new Server1();

    }
}