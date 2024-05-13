package accesscontrol;

import accesscontrol.controller.ExitController;
import accesscontrol.dto.DateTimeMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;

public class MQTT {
    public static Gson gson = new Gson();
    public static void main(String[] args) {
        String broker = "tcp://3.80.25.82:1883";
        String clientId = "JavaClient";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(options);
            System.out.println("Connected");

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!");
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messageString = new String(message.getPayload());
                    System.out.println("Message received:\n\tTopic: " + topic + "\n\tMessage: " + messageString);

                    if (topic.equals("exit")) {
                        DateTimeMessage dateTimeMessage = gson.fromJson(messageString, DateTimeMessage.class);
                        System.out.println("Date: " + dateTimeMessage.getDate());
                        System.out.println("Time: " + dateTimeMessage.getTime());

                    }

                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not needed for a subscriber
                }
            });

            client.subscribe("#");
            Thread.sleep(60000);
            client.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException me) {
            System.out.println("Reason " + me.getReasonCode());
            System.out.println("Msg " + me.getMessage());
            System.out.println("Loc " + me.getLocalizedMessage());
            System.out.println("Cause " + me.getCause());
            System.out.println("Excep " + me);
            me.printStackTrace();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted");
        }
    }


}
