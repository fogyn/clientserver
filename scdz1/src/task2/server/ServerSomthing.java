package task2.server;

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
    //

    //
    private int count;
    private int current = 0;
    LinkedList<String> strings;
    String name;




    public ServerSomthing(Socket socket, Story story, LinkedList<ServerSomthing> serverList, LinkedList<String> strings) throws IOException {

        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию искдючения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.story = story;
        //this.story.printStory(out);
        this.serverList = serverList;
        this.strings = strings;

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
        if(chekLoginPassword()){
            send("Логин и пароль правильные! Вы в системе. Ok"+"\n");
            time = new Date(); // текущая дата
            dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
            String dtime = dt1.format(time); // время

            story.addStoryEl(name + " - подключился - " +  dtime);
            System.out.println(name + " - подключился - " +  dtime);
            start(); // вызываем run()
        }
        else{
            send("Данные не верны. Подключитесь заново");
            downService();
        }

    }


    private boolean chekLoginPassword() {
        boolean bool = false;
        try {
            this.name = in.readLine();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        send("Введите логин и пароль в одну строку через пробел");
        try {
            String logpas = in.readLine();
            String[] mas = logpas.split(" ");
            if(mas.length == 2 ){
                for(int i=0; i<strings.size();i++){
                    String s = strings.get(i);

                    if(logpas.equals(s)){
                        bool = true;
                        break;
                    }
                }

            }

        } catch (IOException ignored) {
        }
        return bool;
    }

    @Override
    public void run() {
        String text;
        //String name;
        String word;
        String stop;
            try {
                //число цитат


                while(true){
                    out.write("Введите число цитат" + "\n");
                    out.flush(); // flush() нужен для выталкивания оставшихся данных
                    text = in.readLine();
                    try {
                        String num[] = text.split(" ");

                        count = Integer.parseInt(num[num.length-1].trim());
                        if(count>0){
                            break;
                        }
                        else{
                            out.write("Количество цитат не может быть меньше 1" + "\n");
                            out.flush(); // flush() нужен для выталкивания оставшихся данных
                        }

                    }
                    catch (NumberFormatException e){
                        out.write("Вы ввели не тот формат числа" + "\n");
                        out.flush(); // flush() нужен для выталкивания оставшихся данных
                    }
                }



                // если такие есть, и очистки потока для дьнейших нужд


            } catch (IOException ignored) {}
            boolean bool = true;

            try {
                while (current<count) {
                    Thread.sleep(3000);
                    word = textList.get(rnd.nextInt(10));

                    if(in.ready()){

                        text = in.readLine();
                        String mas[] = text.split(" ");
                        stop = mas[mas.length-1].trim();

                        if(stop.equals("stop")) {
                            System.out.println(text + " - Вышел");
                            story.addStoryEl(text + " - Вышел");
                            bool=false;
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
                    current++;

                }
                if(bool){
                    send("Количество цитат достигло требуемой величины. До свидания" + "\n");

                    time = new Date(); // текущая дата
                    dt1 = new SimpleDateFormat("HH:mm:ss"); // берем только время до секунд
                    String dtime = dt1.format(time); // время
                    String s = "Количество цитат достигло требуемой величины., "+ dtime+" : "+name+" отключен";
                    System.out.println(s);
                    story.addStoryEl(s);
                    send("stop");
                    downService();
                }


            } catch (NullPointerException ignored) {} catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException ioException) {
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