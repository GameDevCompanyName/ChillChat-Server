package Server;

import java.util.*;

/*
Броадкастер работает с соединениями. Содержит в себе их список и методы для работы с ними.
 */
public class Broadcaster {

    public static TimeoutChecker timeoutChecker;
    //Список соединений

    private Map<String, Connection> connections;
    private String startText;
    private Map<String, String> usersRooms;
    private Map<String, List<String>> rooms;
    private Map<String, String> roomsNames;

    public Broadcaster() {
        connections = new HashMap<>();
        startText = Utilities.getStartText("Broadcaster");
        usersRooms = new HashMap<>();
        rooms = new HashMap<>();
        roomsNames = new HashMap<>();
        createRoom("0", "Главная");
        createRoom("1", "Беседка");
        createRoom("2", "АФК");


    }

    public void createRoom(String id, String name) {
        roomsNames.put(id, name);
        rooms.put(id, new ArrayList<>());
    }

    public void addClientToRoom(String userName, String roomId) {
        usersRooms.put(userName, roomId);
        rooms.get(roomId).add(userName);
        sendConnectionMessage(userName, ServerMessage.userChangedRoomSend(roomId, roomsNames.get(roomId)));
        broadcastMessage(roomId, ServerMessage.serverUserLoginSend(userName));

    }

    public void removeClientFromRoom(String userName) {
        if(!usersRooms.containsKey(userName))
            return;
        String roomId = usersRooms.get(userName);
        rooms.get(roomId).remove(userName);
        usersRooms.remove(userName);
        //broadcastMessage(roomId, ServerMessage.serverUserDisconnectSend(userName));
    }

    public String getRoomNameByUser(String userName){
        return roomsNames.get(usersRooms.get(userName));
    }

    public String getRoomIdByUser(String userName){
        return usersRooms.get(userName);
    }


    //Добавить нового клиента в список соединений
    public void connectClient(Connection client){
        connections.put(client.getUserName(), client);
        timeoutChecker.addClient(client.getUserName());
        addClientToRoom(client.getUserName(), "0");
        //broadcastMessage(ServerMessage.serverUserLoginSend(client.getUserName()));
        printClients();
    }



    public static void setTimeoutChecker(TimeoutChecker timeoutChecker){
        Broadcaster.timeoutChecker=timeoutChecker;
    }


    //Удалить клиента из списка
    public void disconnectClient(String name){
        if(!connections.containsKey(name))
            return;
        removeClientFromRoom(name);
        connections.remove(name);
        //broadcastMessage(ServerMessage.serverUserDisconnectSend(name));
        printClients();

    }



    private void printClients() {
        System.out.println(startText+"Клиентов: " + connections.size());
    }

    //Передача сообщения всем клиентам
    public void broadcastMessage(String roomId, String message){
        if(roomId==null)
            return;
        for (String name: rooms.get(roomId)) {
            sendConnectionMessage(name, message);
        }
//        try {
//            for (Connection client: connections.values()){
//                client.sendMessage(message);
//            }
//        } catch (Exception e){
//            System.out.println(startText+"Ошибка при отправке сообщения");
//            e.printStackTrace();
//        }
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

    public Collection<String> getUsersByRoomId(String roomId) {
        return rooms.get(roomId);
    }
}
