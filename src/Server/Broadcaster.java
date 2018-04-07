package Server;

import java.util.*;

/*
Броадкастер работает с соединениями. Содержит в себе их список и методы для работы с ними.
 */
public class Broadcaster {

    public static TimeoutChecker timeoutChecker;
    //Список соединений

    private Map<String, Connection> connections = new HashMap<>();
    private String startText = Utilities.getStartText("Broadcaster");
    //Добавить нового клиента в список соединений
    public void connectClient(Connection client){
        connections.put(client.getUserName(), client);
        timeoutChecker.addClient(client.getUserName());
        broadcastMessage(ServerMessage.serverUserLoginSend(client.getUserName()));
        printClients();
    }

    public static void setTimeoutChecker(TimeoutChecker timeoutChecker){
        Broadcaster.timeoutChecker=timeoutChecker;
    }


    //Удалить клиента из списка
    public void disconnectClient(String name){
        if(!connections.containsKey(name))
            return;
        connections.remove(name);
        broadcastMessage(ServerMessage.serverUserDisconnectSend(name));
        printClients();

    }

    private void printClients() {
        System.out.println(startText+"Клиентов: " + connections.size());
    }

    //Передача сообщения всем клиентам
    public void broadcastMessage(String message){
        try {
            for (Connection client: connections.values()){
                client.sendMessage(message);
            }
        } catch (Exception e){
            System.out.println(startText+"Ошибка при отправке сообщения");
            e.printStackTrace();
        }
    }

    public Collection<Connection> getConnections(){
        return connections.values();
    }

    //Получить соединение по логину
    public Connection getConnectionByLogin(String login){
        return connections.get(login);
    }

    //Отключить всех
    public void disconnectAll(){
        Iterator<Connection> i = connections.values().iterator();
        while (i.hasNext()) {
            Connection value = i.next();
            value.disconnect("Сервер закрыл соединение");
            i.remove();
        }
        connections.clear();
        System.out.println(startText+"Соединения закрыты(DiscAll)");
        printClients();
    }

    public void sendConnectionMessage(String name, String message){
        connections.get(name).sendMessage(message);
    }
}
