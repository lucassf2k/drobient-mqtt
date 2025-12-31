# MQTT Drone and Sensors Simulation Project

This project is a simulation of a climate monitoring system that uses the MQTT protocol for communication between a drone, processing servers, and visualization clients.

## Architecture

The system is composed of the following components:

- **Drone**: Simulates a drone that collects climate data (temperature, humidity, pressure, and solar radiation) and publishes it to an MQTT topic.
- **Server 1**: Acts as a gateway, receiving data from the drone and forwarding it to other servers.
- **Server 2 and 3**: Process the data received from Server 1, store it in a "database" file (`db.txt`), and republish it to more specific topics (pressure, temperature/humidity, and all data).
- **Clients**: Subscribe to specific topics to receive and display the data of interest.

## How to Run

This is a Maven project. To build and run the components, follow the steps below:

1. **Build the project:**

   ```bash
   mvn clean install
   ```

2. **Run the components:**

   Open a terminal for each component you want to run.

   - **Run the Drone:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.Drone
     ```

   - **Run Server 1:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.Server1
     ```

   - **Run Server 2:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.Server2
     ```

   - **Run Server 3:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.Server3
     ```

   - **Run the Pressure Client:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.clients.ClientPress
     ```

   - **Run the Temperature and Humidity Client:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.clients.ClientTempUmid
     ```

   - **Run the All Data Client:**

     ```bash
     java -cp target/projeto-mqtt-1.0-SNAPSHOT.jar org.example.drobient2.clients.ClientTodos
     ```

## Main Components

- `org.example.drobient2.Drone`: Publishes simulated climate data every 3 seconds to the topic `mqtt/ex1`.
- `org.example.drobient2.Server1`: Subscribes to the topic `mqtt/ex1`, receives data from the drone, and republishes it to the topics `mqtt/ex2` and `mqtt/ex3`.
- `org.example.drobient2.Server2`: Subscribes to the topic `mqtt/ex2`, persists the data to `db.txt`, and republishes it to the topics `mqtt/pressao`, `mqtt/temumid`, and `mqtt/todos`.
- `org.example.drobient2.Server3`: Subscribes to the topic `mqtt/ex3`, persists the data to `db.txt`, and republishes it to the topics `mqtt/pressao`, `mqtt/temumid`, and `mqtt/todos`.
- `org.example.drobient2.clients.ClientPress`: Subscribes to the topic `mqtt/pressao` and displays pressure data.
- `org.example.drobient2.clients.ClientTempUmid`: Subscribes to the topic `mqtt/temumid` and displays temperature and humidity data.
- `org.example.drobient2.clients.ClientTodos`: Subscribes to the topic `mqtt/todos` and displays all climate data.
- `org.example.drobient2.DB`: Utility class for writing the data received by the servers to a text file (`db.txt`).

## Additional Examples

The project also includes packages with simpler MQTT publisher and subscriber examples:

- `org.example.exemplo1`: Contains a basic subscriber (`Assinante`) and producer (`ProdutorB`).
- `org.example.exemplo2`: Contains a class (`ProdutorAssinante`) that acts as both a publisher and a subscriber on the same topic.

---

Se quiser, posso adaptar o texto para um **README mais formal**, **artigo acadêmico**, ou **documentação técnica em inglês**.
