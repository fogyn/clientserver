package task2.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class ServerOverflow extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток завписи в сокет
    private Random rnd = new Random();
    //
    private Date time;
    private SimpleDateFormat dt1;





    public ServerOverflow(Socket socket) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию искдючения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }

    @Override
    public void run() {
        String name = "";
        try {
            // первое сообщение отправленное сюда - это никнейм
            name = in.readLine();
            time = new Date(); // текущая дата
            dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
            String dtime = dt1.format(time); // время
            send("close");
            //send(name + ", количество соединений достигла максимума. Позвоните попозже. - "+dt1);
            downService();

        } catch (NullPointerException | IOException ignored)
        {

        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {
        }

    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();

            }
        } catch (IOException ignored) {
        }
    }
}
