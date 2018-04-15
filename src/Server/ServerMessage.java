package Server;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/*
Статический класс для чтения и упаковки сообщений.
 */

public class ServerMessage {

    public static TimeoutChecker timeoutChecker;

    //Читаем входное сообщение от клиента
    public static String read(String input, Connection connection){
        timeoutChecker.refreshCounter(connection.getUserName());
        JSONObject incomingMessage = (JSONObject) JSONValue.parse(input);
        String type = incomingMessage.get("type").toString();
        switch (type){
            case "version":
                return "version:"+ServerMethods.versionReceived(incomingMessage.get("first").toString());
            case "loginAttempt":
                return "loginAttempt:"+ServerMethods.loginAttemptReceived(
                        incomingMessage.get("first").toString(),
                        incomingMessage.get("second").toString()
                );
            case "message":
                ServerMethods.messageReceived(
                        incomingMessage.get("first").toString(),
                        connection.getUserName(),
                        connection.getUserColor()
                );
                return "true";
            case "disconnect":
                ServerMethods.disconnectReceived(connection, "пользователь разорвал соединение");
                return "disconnect";
            case "joinRoom":
                ServerMethods.joinRoomReceived(
                        connection.getUserName(),
                        incomingMessage.get("first").toString()
                );
                return "true";
            case "ping":
                ServerMethods.pingReceived(connection);
                return "true";
            default:
                return "false";
        }
    }

    public static void setTimeoutChecker(TimeoutChecker timeoutChecker){
        ServerMessage.timeoutChecker = timeoutChecker;
    }

    //Все статичные методы описаны в документации
    public static String clientVersionRequestSend(){
        JSONObject object = new JSONObject();
        object.put("type", "clientVersionRequest");
        return object.toJSONString();
    }

    public static String loginWrongErrorSend(){
        JSONObject object = new JSONObject();
        object.put("type", "loginWrongError");
        return object.toJSONString();
    }

    public static String loginAlreadyErrorSend(){
        JSONObject object = new JSONObject();
        object.put("type", "loginAlreadyError");
        return object.toJSONString();
    }

    public static String loginSuccessSend(){
        JSONObject object = new JSONObject();
        object.put("type", "loginSuccess");
        return object.toJSONString();
    }

    public static String userRegistrationSuccessSend(){
        JSONObject object = new JSONObject();
        object.put("type", "userRegistrationSuccess");
        return object.toJSONString();
    }
    public static String userColorSend(String login, String color){
        JSONObject object = new JSONObject();
        object.put("type", "userColor");
        object.put("first", login);
        object.put("second", color);
        return object.toJSONString();
    }
    public static String userMessageSend(String login, String message, String color){
        JSONObject object = new JSONObject();
        object.put("type", "userMessage");
        object.put("first", login);
        object.put("second", message);
        object.put("third", color);
        return object.toJSONString();
    }
    public static String userActionSend (String login, String action){
        JSONObject object = new JSONObject();
        object.put("type", "userAction");
        object.put("first", login);
        object.put("second", action);
        return object.toJSONString();
    }
    public static String serverMessageSend(String message){
        JSONObject object = new JSONObject();
        object.put("type", "serverMessage");
        object.put("first", message);
        return object.toJSONString();
    }
    public static String serverEventSend(String event){
        JSONObject object = new JSONObject();
        object.put("type", "serverEvent");
        object.put("first", event);
        return object.toJSONString();
    }
    public static String serverUserKickedSend(String login, String reason){
        JSONObject object = new JSONObject();
        object.put("type", "serverUserKicked");
        object.put("first", login);
        object.put("second", reason);
        return object.toJSONString();
    }
    public static String userDisconnectSend(String reason){
        JSONObject object = new JSONObject();
        object.put("type", "userDisconnect");
        object.put("first", reason);
        return object.toJSONString();
    }
    public static String serverUserLoginSend(String login){
        JSONObject object = new JSONObject();
        object.put("type", "serverUserLogin");
        object.put("first", login);
        return object.toJSONString();
    }
    public static String serverUserDisconnectSend(String login){
        JSONObject object = new JSONObject();
        object.put("type", "serverUserDisconnect");
        object.put("first", login);
        return object.toJSONString();
    }

    public static String serverPingSend(){
        JSONObject object = new JSONObject();
        object.put("type", "ping");
        return object.toJSONString();
    }

    public static String serverPongSend(){
        JSONObject object = new JSONObject();
        object.put("type", "pong");
        return object.toJSONString();
    }

    public static String userRoleSend(String login, String role){
        JSONObject object = new JSONObject();
        object.put("type", "userColor");
        object.put("first", login);
        object.put("second", role);
        return object.toJSONString();
    }

    public static String userChangedRoomSend(String roomId, String roomName){
        JSONObject object = new JSONObject();
        object.put("type", "userChangedRoom");
        object.put("first", roomId);
        object.put("second", roomName);
        return object.toJSONString();
    }


}
