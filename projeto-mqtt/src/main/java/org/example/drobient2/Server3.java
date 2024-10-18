package org.example.drobient2;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Timer;
import java.util.TimerTask;

public class Server3 implements MqttCallback {
    private final int qos = 1;
    private String topicServer1 = "mqtt/ex3";
    private String topicoPressao = "mqtt/pressao";
    private String topicoTempUmid = "mqtt/temumid";
    private String topicoTodos = "mqtt/todos";
    private MqttClient clienteMqtt;
    private String broker = "tcp://mqtt.eclipseprojects.io:1883";

    public Server3() throws MqttException {
        this.conectar();
    }

    public void conectar() throws MqttException {
        String idCliente = MqttClient.generateClientId();
        System.out.println("[*] ID do Servidor 3: " + idCliente);

        MqttConnectOptions opcoesDaConexao = new MqttConnectOptions();
        opcoesDaConexao.setCleanSession(true);

        this.clienteMqtt = new MqttClient(this.broker, idCliente);
        this.clienteMqtt.setCallback(this);
        System.out.println("[*] Conectando-se ao broker " + broker);
        this.clienteMqtt.connect(opcoesDaConexao);
        System.out.println("[*] Conectado!");

        System.out.println("[*] Inscrevendo Servidor 3 no tópico do Servidor 1: " + topicServer1);
        clienteMqtt.subscribe(topicServer1, qos);
        System.out.println("[*] Inscrito no tópico do Servidor 1!");
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

                final var message = new String(mensagem.getPayload());
                DB.Write(message);

                final String[] dados = message.split(", ");
                // Armazenando os dados em variáveis separadas
                MqttMessage mensagemPressao = new MqttMessage(dados[2].getBytes());
                final var tempUmid = dados[0] + dados[1];
                MqttMessage mensagemTempUmid = new MqttMessage(tempUmid.getBytes());
                // Publica a mensagem recebida nos tópicos dos Servidores 2 e 3
                try {
                    clienteMqtt.publish(topicoPressao, mensagemPressao);
                    clienteMqtt.publish(topicoTempUmid, mensagemTempUmid);
                    clienteMqtt.publish(topicoTodos, mensagem);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("[*] Dados enviados topicos de temp/umi, pressao e todos");
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
        new Server3();
    }
}