package Server;

/*
Класс с методами, вызываемыми клиентами.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMethods {

    public static Broadcaster broadcaster;
    public static DBConnector dbConnector;

    public static void setBroadcaster(Broadcaster broadcaster) {
        ServerMethods.broadcaster = broadcaster;
    }

    public static void setDbConnector(DBConnector dbConnector) {
        ServerMethods.dbConnector = dbConnector;
    }

    //Все методы в документации
    public static String versionReceived(String version){
        String result = "false";
        switch (version){
            case "JC0.1":
                result = version;
                break;
            case "AC0.1":
                result = version;
                break;
                default:
                    result="false";
                    break;
        }
        return result;
    }

    public static String loginAttemptReceived(String login, String password){
        if(dbConnector.checkLoginAttempt(login, password)){
            return login;
        }
        return "false";
    }

    public static void messageReceived(String message, String login, String userColor){
        Pattern pattern = Pattern.compile("^/[a-zа-яА-ЯA-Z0-9_\\s]+$");
        Matcher m;
        m = pattern.matcher(message);
        //Если команда, то пытаемся вызвать ее
        if(m.matches()) {
            message = message.substring(1);
            String[] command = message.split(" ");
            Commands.invoke(command, login);
        }
        //Иначе отправляем как сообщение в чат
        else {
            broadcaster.broadcastMessage(ServerMessage.userMessageSend(login, message, userColor));
            java.util.Date date = new java.util.Date();
            System.out.println("[" + date + "]" + login + ": " + message);
        }
    }

    public static void disconnectReceived(Connection connection, String reason){
        System.out.println(Utilities.getStartText("ServerMethods")+connection.getUserName()+" отключился: "+reason);
        broadcaster.disconnectClient(connection.getUserName());
        connection.disconnect(reason);
    }
}
