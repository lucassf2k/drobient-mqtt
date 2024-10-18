package org.example.exemplo1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class ProdutorB {
    String broker = "tcp://mqtt.eclipseprojects.io:1883";

    public ProdutorB() throws InterruptedException {
        this.init();
    }

    public void init() throws InterruptedException {

        System.out.println("[*] Inicializando um publisher...");

        try {
            /*
             MqttClient.generateClientId(): gera um ID único para identificar
             o cliente que está se conectando ao broker MQTT.
             Esse ID é necessário para que o broker saiba quem
             está publicando ou assinando as mensagens.
             */

            String idCliente = MqttClient.generateClientId();
            System.out.println("[*] ID do Cliente: " + idCliente);
            /*
            Cria um cliente Mqtt
             */
            MqttClient clienteMqtt = new MqttClient(broker, idCliente);
            /*
            Um objeto MqttConnectOptions é criado para configurar
            as opções de conexão.

             */
            MqttConnectOptions opcoesConexao = new MqttConnectOptions();
            /*
            setCleanSession(true) indica que o cliente deseja
            uma sessão limpa, ou seja, não vai manter informações
            de estado persistente.
            Toda vez que se conectar, será uma nova sessão
            sem armazenar assinaturas ou mensagens anteriores.
             */
            opcoesConexao.setCleanSession(true);

            // Conectar cliente ao broker
            System.out.println("[*] Conectando-se ao broker " + broker);
            /*
            O cliente é conectado ao broker MQTT usando as opções
            de conexão configuradas.
             */
            clienteMqtt.connect(opcoesConexao);
            /*
            clienteMqtt.connect(opcoesConexao) tenta estabelecer
            a conexão com o broker.
            Imprime no console se o cliente foi conectado
            com sucesso
            (clienteMqtt.isConnected() retorna true se a
            conexão foi estabelecida).
             */
            System.out.println("[*] Conectado: " + clienteMqtt.isConnected());

            String conteudo;
            MqttMessage mensagem;
            for (int i = 0; i < 10; i++) {
                // Cria uma mensagem Mqtt
                conteudo = "\n Temperatura: " + (20 + new Random().nextInt(45)) +
                "\n Umidade do ar: " + (30 + new Random().nextInt(50)) + "%";
                mensagem = new MqttMessage(conteudo.getBytes());
                mensagem.setQos(0);
                System.out.println("[*] Publicando mensagem: " + conteudo);
                clienteMqtt.publish("mqtt/ex1", mensagem);
                Thread.sleep(1000);
            }

            // Desconecta o cliente
            clienteMqtt.disconnect();

            System.out.println("[*] Mensagem publicada. Saindo...");

            System.exit(0);

        } catch (MqttException me) {

            System.out.println("razão "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new ProdutorB();

    }
}
