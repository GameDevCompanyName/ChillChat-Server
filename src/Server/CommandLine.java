package Server;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Обработка сканнера с сервера.
 */

public class CommandLine extends Thread{
    private String text;
    private Scanner scanner;
    private Broadcaster broadcaster;

    public  CommandLine(Broadcaster broadcaster){
        this.scanner = new Scanner(System.in);
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {
        //Регулярное выражение для команд
        Pattern pattern = Pattern.compile("^/[a-zа-яА-ЯA-Z0-9_\\s]+$");
        Matcher m;
        while (true){
            text = scanner.nextLine();
            m = pattern.matcher(text);
            //Если команда, то пытаемся вызвать ее
            if(m.matches()) {
                text = text.substring(1);
                String[] command = text.split(" ");
                Commands.invoke(command, "ServerAdminRootSuperKeyForSecurity");

            }
            //Иначе отправляем как сообщение в чат
            else {
                broadcaster.broadcastMessage("0", ServerMessage.serverMessageSend(text));
                java.util.Date date = new java.util.Date();
                System.out.println("["+date+"]SERVER: "+text);
            }
        }
    }
}
