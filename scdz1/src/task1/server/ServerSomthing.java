package task1.server;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

class ServerSomthing extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток завписи в сокет
    private Random rnd = new Random();
    //
    private Date time;
    private SimpleDateFormat dt1;
    //
    private Story story;
    private LinkedList<ServerSomthing> serverList;
    private LinkedList<String> textList = new LinkedList<>();

    /**
     * для общения с клиентом необходим сокет (адресные данные)
     * @param socket
     * @throws IOException
     */

    public ServerSomthing(Socket socket, Story story, LinkedList<ServerSomthing> serverList) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию искдючения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.story = story;
        this.story.printStory(out);
        this.serverList = serverList;
        //
        textList.add("В любом процессе важна не скорость, а удовольствие." );
        textList.add("Люди-идиоты плюс алкоголь - вот вам рецепт любой потасовки.");
        textList.add("Не так важно, как тебя ударили, - важно, как ты встал и ответил.");
        textList.add("Если бы мне представилась возможность самому выбирать фильм, я бы, наверное, не стал искушать " +
                "судьбу творческим поиском и взялся бы за то, что умею делать хорошо.");
        textList.add("Если стараться обходить все неприятности, то можно пройти мимо всех удовольствий.");
        textList.add("Ты свободен, а значит, всерьёз за себя отвечаешь.");
        textList.add("Молчание - лучший способ ответа на бессмысленные вопросы.");
        textList.add("Тем, кого вдохновляют мои герои, не помешало бы лишний раз подумать.");
        textList.add("Умение слушать - большой плюс, а умение делать вид, что слушаешь, и в нужный момент вставлять слово - талант.");
        textList.add("Работать на улице - не одно и тоже, что протирать штаны с продюсерами и режиссёрами.");

        //Server.story.printStory(out); // поток вывода передаётся для передачи истории последних 10
        // сооюбщений новому поключению
        start(); // вызываем run()
    }
    @Override
    public void run() {
        String text;
        String name;
        String word;
        String stop;
        try {
            // первое сообщение отправленное сюда - это никнейм
            text = in.readLine();
            name = text;
            try {

                out.write(name + "\n");
                out.flush(); // flush() нужен для выталкивания оставшихся данных
                System.out.println("Зашел клиент " + name);
                // если такие есть, и очистки потока для дьнейших нужд


            } catch (IOException ignored) {}
            try {
                while (true) {
                    Thread.sleep(3000);
                    word = textList.get(rnd.nextInt(10));

                    if(in.ready()){

                        text = in.readLine();
                        String mas[] = text.split(" ");
                        stop = mas[mas.length-1].trim();

                        if(stop.equals("stop")) {
                            System.out.println(text + " - Вышел");
                            story.addStoryEl(text + " - Вышел");
                            downService(); // харакири
                            break; // если пришла пустая строка - выходим из цикла прослушки
                        }

                    }


                    System.out.println("Echoing: " + word);
                    story.addStoryEl(word);
                    try {

                        out.write(word + "\n");
                        out.flush(); // flush() нужен для выталкивания оставшихся данных

                    } catch (IOException ignored) {
                        time = new Date(); // текущая дата
                        dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
                        String dtime = dt1.format(time); // время
                        String s = "Соединение прервано, "+ dtime+" : "+name+" вышел";
                        System.out.println(s);
                        story.addStoryEl(s);
                        downService();
                        break;
                    }

                    //Server.story.addStoryEl(word);
//                    for (ServerSomthing vr : serverList) {
//                        vr.send(word); // отослать принятое сообщение с привязанного клиента всем остальным влючая его
//                    }

                }
            } catch (NullPointerException ignored) {} catch (InterruptedException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            downService();
        }
    }

    /**
     * отсылка одного сообщения клиенту по указанному потоку
     * @param msg
     */
    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    /**
     * закрытие сервера
     * прерывание себя как нити и удаление из списка нитей
     */
    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerSomthing vr : serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}