package org.example.drobient2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Drone {
    String broker = "tcp://mqtt.eclipseprojects.io:1883";
    MqttClient clienteMqtt;

    public Drone() throws InterruptedException {
        this.init();
    }

    public void init() throws InterruptedException {
        System.out.println("[*] Inicializando um publisher...");
        try {
            String idCliente = MqttClient.generateClientId();
            System.out.println("[*] ID do Cliente: " + idCliente);
            clienteMqtt = new MqttClient(broker, idCliente);

            MqttConnectOptions opcoesConexao = new MqttConnectOptions();
            opcoesConexao.setCleanSession(true);

            System.out.println("[*] Conectando-se ao broker " + broker);
            clienteMqtt.connect(opcoesConexao);
            System.out.println("[*] Conectado: " + clienteMqtt.isConnected());

            // Timer para enviar dados a cada 3 segundos
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    enviarDados();
                }
            }, 0, 3000); // Inicia imediatamente e repete a cada 3000 ms (3 segundos)

        } catch (MqttException me) {
            System.out.println("Razão: " + me.getReasonCode());
            System.out.println("Mensagem: " + me.getMessage());
            System.out.println("Local: " + me.getLocalizedMessage());
            System.out.println("Causa: " + me.getCause());
            System.out.println("Exceção: " + me);
        }
    }

    public void enviarDados() {
        // Simulação de coleta de dados climáticos
        Random random = new Random();
        double temperatura = 15 + (30 - 15) * random.nextDouble(); // Temperatura entre 15 e 30 graus
        double umidade = 40 + (100 - 40) * random.nextDouble(); // Umidade entre 40% e 100%
        double pressao = 1000 + (50 - 0) * random.nextDouble(); // Pressão entre 1000 hPa e 1050 hPa
        double radiacaoSolar = random.nextDouble() * 1000; // Radiação solar entre 0 e 1000 W/m²

        // Formatação da mensagem
        String conteudo = String.format("Temperatura: %.2f°C, Umidade: %.2f%%, Pressão: %.2f hPa, Radiação Solar: %.2f W/m²",
                temperatura, umidade, pressao, radiacaoSolar);

        try {
            MqttMessage mensagem = new MqttMessage(conteudo.getBytes());
            mensagem.setQos(0);
            System.out.println("[*] Publicando mensagem: " + conteudo);
            clienteMqtt.publish("mqtt/ex1", mensagem);
        } catch (MqttException e) {
            System.out.println("Erro ao publicar mensagem: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Drone();
    }
}