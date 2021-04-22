package task2.client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

class ClientSomthing {

    private Socket socket;
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток чтения в сокет
    private BufferedReader inputUser; // поток чтения с консоли
    private String addr; // ip адрес клиента
    private int port; // порт соединения
    private String nickname; // имя клиента
    private Date time;
    private String dtime;
    private SimpleDateFormat dt1;
    private boolean off = false;

    /**
     * для создания необходимо принять адрес и номер порта
     *
     * @param addr
     * @param port
     */

    public ClientSomthing(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            // потоки чтения из сокета / записи в сокет, и чтения с консоли
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


             // перед началом необходимо спросит имя
            if(this.pressNickname()){
                new ReadMsg().start(); // нить читающая сообщения из сокета в бесконечном цикле
                new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
            }
            else{
                downService();
            }



        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            ClientSomthing.this.downService();
        }
        // В противном случае сокет будет закрыт
        // в методе run() нити.
    }

    /**
     * просьба ввести имя,
     * и отсылка эхо с приветсвием на сервер
     */

    private boolean pressNickname() {
        boolean bool = false;
        System.out.print("Press your nick: ");
        try {
            nickname = inputUser.readLine();
            out.write("Hello " + nickname + "\n");
            out.flush();
            String s = in.readLine();
            if(!s.equals("close")){
                System.out.println("Добро пожаловать");
                //System.out.println(s);
                //логин и пароль
                String logpas;
                String[] strings;

                while (true) {
                    System.out.println(s);
                    logpas = inputUser.readLine();
                    strings = logpas.split(" ");
                    if (strings.length != 2) {
                        System.out.println("Нужно ввести два значени через пробел");
                        continue;
                    } else {
                        System.out.println("Данные приняты");
                        break;
                    }
                }
                out.write(logpas + "\n");
                out.flush();
                String ss = in.readLine();
                System.out.println(ss);
                if(ss.contains("Ok")){
                    bool = true;
                }
                else{
                    System.out.println("Что то не так с логином и паролем");
                }

            }
            else{

                System.out.println("Соединение переполнено. Позвоните попозже");

            }


        } catch (IOException ignored) {

        }
        return bool;
    }



    /**
     * закрытие сокета
     */
    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                //System.out.println("Сработало");
            }
        } catch (IOException ignored) {
            System.out.println("не сработало");
        }
    }

    // нить чтения сообщений с сервера
    private class ReadMsg extends Thread {
        @Override
        public void run() {

            String str;
            try {
                while (true) {
                    str = in.readLine(); // ждем сообщения с сервера
                    if (str.equals("stop")) {
                        System.out.println("остановка клиента");
                        //
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("stop2");
//                        String data = sb.toString();
//                        InputStream is = new ByteArrayInputStream(data.getBytes());
//                        System.setIn(is);
                        //
                        //System.out.println("Отправил в консоль");
                        //System.out.println(System.in);
                        //
                        off=true;
                        ClientSomthing.this.downService(); // харакири
                        break; // выходим из цикла если пришло "stop"
                    }
                    System.out.println(str); // пишем сообщение с сервера на консоль
                }
                ClientSomthing.this.downService();
            } catch (IOException e) {
                ClientSomthing.this.downService();
            }
        }

    }

    // нить отправляющая сообщения приходящие с консоли на сервер
    public class WriteMsg extends Thread {

        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    if(inputUser.ready()){
                         userWord = inputUser.readLine(); // сообщения с консоли
                        time = new Date(); // текущая дата
                        dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
                        dtime = dt1.format(time); // время
                        if (userWord.equals("stop")) {
                            //out.write("stop" + "\n");
                            out.write("(" + dtime + ") " + nickname + ": " +"stop");
                            out.flush();
                            ClientSomthing.this.downService(); // харакири
                            break; // выходим из цикла если пришло "stop"
                        }
                        else {
                            //System.out.println("Пришло это - "+userWord);
                            out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n"); // отправляем на сервер
                        }
                        out.flush(); // чистим

                    }
                    else{
                        if(off){
                            ClientSomthing.this.downService(); // харакири
                            break; // выходим из цикла если пришло "stop"
                        }
                    }
                } catch (IOException e) {
                    ClientSomthing.this.downService();
                }

//                try {
//                    time = new Date(); // текущая дата
//                    dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
//                    dtime = dt1.format(time); // время
//                    userWord = inputUser.readLine(); // сообщения с консоли
//                    if (userWord.equals("stop")) {
//                        //out.write("stop" + "\n");
//                        out.write("(" + dtime + ") " + nickname + ": " +"stop");
//                        out.flush();
//                        ClientSomthing.this.downService(); // харакири
//                        break; // выходим из цикла если пришло "stop"
//                    }
//
////                    else if(userWord.equals("stop2")){
////                        System.out.println("выход - "+userWord);
////                        ClientSomthing.this.downService(); // харакири
////                        break; // выходим из цикла если пришло "stop"
////                    }
//                    else {
//                        //System.out.println("Пришло это - "+userWord);
//                        out.write("(" + dtime + ") " + nickname + ": " + userWord + "\n"); // отправляем на сервер
//                    }
//                    out.flush(); // чистим
//                } catch (IOException e) {
//                    ClientSomthing.this.downService(); // в случае исключения тоже харакири
//
//                }

            }
            downService();
        }
    }
}
