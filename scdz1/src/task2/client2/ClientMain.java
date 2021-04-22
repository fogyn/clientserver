package task2.client2;

public class ClientMain {


    public static String ipAddr = "localhost";
    public static int port = 8080;

    /**
     * создание клиент-соединения с узананными адресом и номером порта
     *
     * @param args
     */

    public static void main(String[] args) {
        new ClientSomthing(ipAddr, port);
    }

}
