package task2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerMain {

        public static final int PORT = 8080;
        public static LinkedList<ServerSomthing> serverList = new LinkedList<>(); // список всех нитей - экземпляров
        // сервера, слушающих каждый своего клиента
        public static Story story; // история переписки

        /**
         * @param args
         * @throws IOException
         */

        public static void main(String[] args) throws IOException {
            ServerSocket server = new ServerSocket(PORT);
            story = new Story();
            System.out.println("Server Started");

            Scanner scanner = new Scanner(System.in);
            String num = "";
            while(true){
                System.out.println("Введите количество обрабатываемых клиентов");
                num = scanner.nextLine();
                if(parsInt(num)){
                    break;
                }
                else{
                    System.out.println("Вы ввели не подходящий формат, или число меньше 1");
                }

            }
            int number = Integer.parseInt(num);
            System.out.println("Можем работать с "+num+" соединениями (клиентами)");
            System.out.println("-----------------");
            LinkedList<String> list_logpass = new LinkedList<>();
            list_logpass.add("www 111");
            list_logpass.add("eee 222");
            list_logpass.add("rrr 333");
            list_logpass.add("ttt 444");

//            String text = null;
//            String[] strings;
//            int iduser = -1;
//            while (true) {
//                System.out.println("Введите логин и пароль через пробел");
//                text = scanner.nextLine();
//                strings = text.split(" ");
//                if (strings.length != 2) {
//                    System.out.println("Нужно ввести два значени через пробел");
//                    continue;
//                } else {
//                    System.out.println("Данные приняты");
//                    break;
//                }
//            }
//            System.out.println("Логин - "+strings[0]);
//            System.out.println("---------------");
//            System.out.println("Пароль - "+strings[1]);

            System.out.println("Ожидание соединений");

            try {
                while (true) {
                    // Блокируется до возникновения нового соединения:
                    Socket socket = server.accept();
                    if(serverList.size()<number){


                        try {
                           //serverList.add(new ServerSomthing(socket, story, serverList, strings)); // добавить новое соединенние в список
                            serverList.add(new ServerSomthing(socket, story, serverList, list_logpass)); // добавить новое соединенние в список
                        } catch (IOException e) {
                            // Если завершится неудачей, закрывается сокет,
                            // в противном случае, нить закроет его:
                            socket.close();
                        }
                    }
                    else{
                        System.out.println("Перполнение");
                        new ServerOverflow(socket);
                    }

                }
            } finally {
                server.close();
            }
        }

        private static boolean parsInt(String s){
            try{
                int i = Integer.parseInt(s);
                if(i>0){
                    return  true;
                }
                else{
                    return false;
                }
            }
            catch (Exception e){
                return false;
            }
        }

}
