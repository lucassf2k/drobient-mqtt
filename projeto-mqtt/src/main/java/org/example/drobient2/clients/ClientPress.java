package org.example.drobient2.clients;

import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

public class ClientPress {

    String broker = "tcp://mqtt.eclipseprojects.io:1883";

    public ClientPress() {
        this.init();
    }

    public void init() {

        System.out.println("[*] Inicializando um assinante...");

        try {
            // Cria um cliente MQTT

            String idCliente = MqttClient.generateClientId();
            System.out.println("[*] ID do Cliente: " + idCliente);
            MqttClient clienteMqtt = new MqttClient(broker, idCliente);

            MqttConnectOptions opcoesDaConexao = new MqttConnectOptions();
            opcoesDaConexao.setCleanSession(true);

            // Conecta o cliente ao broker
            System.out.println("[*] Conectando-se ao broker " + broker);
            clienteMqtt.connect(opcoesDaConexao);
            System.out.println("[*] Conectado!");

            // Trava regressiva usada para sincronizar threads
            final CountDownLatch trava = new CountDownLatch(20);

            // Tópico que o cliente vai assinar
            final String topicoSub = "mqtt/pressao";

            // Callback - Classe interna anônima para receber mensagens
            clienteMqtt.setCallback(new MqttCallback() {
                public void messageArrived(String topico, MqttMessage mensagem) throws Exception {
                    /* Chamado quando chega uma mensagem do servidor que
                     * corresponde a qualquer assinatura feita pelo cliente
                     */
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println("\nUma mensagem foi recebida!" +
                            "\n\tData/Hora:    " + time +
                            "\n\tTópico:   " + topico +
                            "\n\tMensagem: " + new String(mensagem.getPayload()) +
                            "\n\tQoS:     " + mensagem.getQos() + "\n");
                    trava.countDown(); // desbloqueia a thread main
                }

                public void connectionLost(Throwable causa) {
                    System.out.println("Conexão com o broker foi perdida!" + causa.getMessage());
                    trava.countDown();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {

                }

            });
            /* Inscreva o cliente no tópico com QoS nível 0
             */
            System.out.println("[*] Inscrevendo cliente no tópico: " + topicoSub);
            clienteMqtt.subscribe(topicoSub, 1);
            System.out.println("[*] Incrito!");

            /* Aguarda a mensagem ser recebida
             * */

            try {
                trava.await(); // bloqueia até a mensagem ser recebida
            } catch (InterruptedException e) {
                System.out.println("[*] Me acordaram enquanto eu esperava zzzzz");
            }

            /* Desconecta o cliente
             * */
            clienteMqtt.disconnect();
            System.out.println("[*] Finalizando...");

            System.exit(0);

        } catch (MqttException me) {

            throw new RuntimeException(me);

        }
    }

    public static void main(String[] args) {
        new ClientPress();
    }
}
