package Server;

import java.util.Collection;

/*
Класс команд, отвечает за выполнение команд сервера.
 */

public class Commands {
    public static Broadcaster broadcaster;
    public static DBConnector dbConnector;

    public static void setBroadcaster(Broadcaster broadcaster) {
        Commands.broadcaster = broadcaster;
    }

    public static void setDbConnector(DBConnector dbConnector) {
        Commands.dbConnector = dbConnector;
    }

    //Пытаемся выполнить команду, разбитую на части
    //comms[0] - название команды
    //comms[1] - первый аргумент
    //comms[2] - второй аргумент
    public static void invoke(String[] comms, String name) {
       switch (comms[0]) {
           case "updateusercolor":
               updateUserColor(comms, name);
               break;
           case "updateuserrole":
               updateUserRole(comms, name);
               break;
           case "clients":
               clients(name);
               break;
           case "discall":
               discall(name);
               break;
           case "kick":
               kick(comms, name);
               break;
           case "role":
               role(name);
               break;
           case "color":
               color(name);
               break;
           case "help":
               help(name);
               break;
           case "room":
               room(comms, name);
               break;
           default:
               if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
                   Connection conn = broadcaster.getConnectionByLogin(name);
                   conn.sendMessage(ServerMessage.serverMessageSend("Команда не найдена!\nСписок команд - /help"));
                   break;
               }
               System.out.println(Utilities.getStartText("Commands")+"Команда не найдена");
               break;
       }
   }


    private static void updateUserColor(String[] comms, String name){
        Connection caster;
        if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
            caster = broadcaster.getConnectionByLogin(name);
            String role = caster.getUserRole();
            switch (role){
                case "server": //Серверный админ
                    break;
                case "admin": //Админ чата
                    break;
                case "mod": //Модератор чата
                    break;
                    default:
                        caster.sendMessage(ServerMessage.serverMessageSend(role + " не имеет прав на эту команду"));
                        return;
            }
        }
        else{
            caster = null;
        }
        if(comms.length == 3)
        {
            if(dbConnector.searchForUser(comms[1])){
                dbConnector.updateUserColor(comms[1], comms[2]);
                if(caster!=null){
                    caster.sendMessage(ServerMessage.serverMessageSend("Цвет "+comms[1]+" обновлен на "+comms[2]));
                }
                return;
            }
            else
                System.out.println(Utilities.getStartText("Commands")+comms[1]+" - пользователь не найден!");
                if(caster!=null){
                    caster.sendMessage(ServerMessage.serverMessageSend(comms[1]+" - пользователь не найден!"));
                }
            return;
        }
        System.out.println("Format: /updateusercolor <login> <color>");
        if(caster!=null){
            caster.sendMessage(ServerMessage.serverMessageSend("Format: /updateusercolor <login> <color>"));
        }
    }
    private static void updateUserRole(String[] comms, String name){
        Connection caster;
        if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
            caster = broadcaster.getConnectionByLogin(name);
            String role = caster.getUserRole();
            switch (role){
                case "server": //Серверный админ
                    break;
                case "admin": //Админ чата
                    break;
                default:
                    caster.sendMessage(ServerMessage.serverMessageSend(role + " не имеет прав на эту команду"));
                    return;
            }
        }
        else{
            caster = null;
        }
        if(comms.length == 3)
        {
            if(dbConnector.searchForUser(comms[1])){
                if(!comms[2].equals("server")) {
                    dbConnector.updateUserRole(comms[1], (comms[2]));
                    if(caster!=null){
                        caster.sendMessage(ServerMessage.serverMessageSend("Роль "+comms[1]+" обновлена на "+comms[2]));
                    }
                    return;
                }
                else{
                    System.out.println(Utilities.getStartText("Commands")+"Невозможно установить права сервера");
                    if(caster!=null){
                        caster.sendMessage(ServerMessage.serverMessageSend("Невозможно установить права сервера"));
                    }
                    return;
                }
            }
            else
                System.out.println(Utilities.getStartText("Commands")+comms[1]+" - пользователь не найден!");
                if(caster!=null){
                    caster.sendMessage(ServerMessage.serverMessageSend(comms[1]+" - пользователь не найден!"));
                }
            return;
        }
        System.out.println("Format: /updateuserrole <login> <role>");
        if(caster!=null){
            caster.sendMessage(ServerMessage.serverMessageSend("Format: /updateuserrole <login> <role>"));
        }
    }
    private static void clients(String name){
        Connection caster;
        if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
            caster = broadcaster.getConnectionByLogin(name);
            String role = caster.getUserRole();
            switch (role){
                case "server": //Серверный админ
                    break;
                case "user": //Пользователь
                    break;
                case "admin": //Админ чата
                    break;
                case "mod": //Модератор чата
                    break;
                default:
                    caster.sendMessage(ServerMessage.serverMessageSend(role + " не имеет прав на эту команду"));
                    return;
            }
        }
        else{
            caster=null;
        }
        Collection<String> users = broadcaster.getUsersByRoomId(broadcaster.getRoomIdByUser(name));

        if(users.isEmpty())
        {
            System.out.println(Utilities.getStartText("Commands")+"Никого нет");
            broadcaster.broadcastMessage(broadcaster.getRoomIdByUser(name),
                    ServerMessage.serverMessageSend("Никого нет в комнате "+broadcaster.getRoomNameByUser(name)));
            return;
        }
        System.out.println(Utilities.getStartText("Commands")+"В сети:");
        broadcaster.broadcastMessage(broadcaster.getRoomIdByUser(name),
                ServerMessage.serverMessageSend("В комнате "+broadcaster.getRoomNameByUser(name)+":"));
        StringBuilder string= new StringBuilder();
        for (String user: users) {
            System.out.println(Utilities.getStartText("Commands")+user);
            string.append(user).append("\n");
        }
        broadcaster.broadcastMessage(broadcaster.getRoomIdByUser(name), ServerMessage.serverMessageSend(string.toString().trim()));

    }
    private static void discall(String name){
        Connection caster;
        if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
            caster = broadcaster.getConnectionByLogin(name);
            String role = caster.getUserRole();
            switch (role){
                case "server": //Серверный админ
                    break;
                default:
                    caster.sendMessage(ServerMessage.serverMessageSend(role + " не имеет прав на эту команду"));
                    return;
            }
        }
        else{
            caster = null;
        }
        broadcaster.disconnectAll();
    }
    private static void kick(String[] comms, String name){
        Connection caster;
        if(!name.equals("ServerAdminRootSuperKeyForSecurity")) {
            caster = broadcaster.getConnectionByLogin(name);
            String role = caster.getUserRole();
            switch (role){
                case "server": //Серверный админ
                    break;
                case "admin": //Админ чата
                    break;
                case "mod": //Модератор чата
                    break;
                default:
                    caster.sendMessage(ServerMessage.serverMessageSend(role + " не имеет прав на эту команду"));
                    return;
            }
        }
        else{
            caster = null;
        }
        if(comms.length == 3){
            if(dbConnector.searchForUser(comms[1])){
                Connection conn = broadcaster.getConnectionByLogin(comms[1]);
                if(conn!=null) {
                    String kickMsg = comms[1]+" кикнут по причине: "+comms[2];
                    System.out.println(Utilities.getStartText("Commands")+kickMsg);
                    broadcaster.broadcastMessage(broadcaster.getRoomIdByUser(name), ServerMessage.serverUserKickedSend(comms[1], comms[2]));
                    conn.disconnect("кикнут");
                    return;
                }
                System.out.println(Utilities.getStartText("Commands")+"Пользователь не в сети");
                if(caster!=null){
                    caster.sendMessage("Пользователь не в сети");
                }
                return;
            }
        }
        System.out.println("Format: /kick <login> <reason>");
        if(caster!=null){
            caster.sendMessage(ServerMessage.serverMessageSend("Format: /kick <login> <reason>"));
        }
    }
    private static void role(String name){
        if(name.equals("ServerAdminRootSuperKeyForSecurity")) {
            System.out.println(Utilities.getStartText("Commands") + "(role)Вы сервер!");
            return;
        }
        Connection connection = broadcaster.getConnectionByLogin(name);
        String role = connection.getUserRole();
        connection.sendMessage(ServerMessage.serverMessageSend("Ваша роль - "+role));
    }
    private static void color(String name){
        if(name.equals("ServerAdminRootSuperKeyForSecurity")) {
            System.out.println(Utilities.getStartText("Commands") + "(color)Вы сервер!");
            return;
        }
        Connection connection = broadcaster.getConnectionByLogin(name);
        String color = connection.getUserColor();
        connection.sendMessage(ServerMessage.serverMessageSend("Ваш цвет - "+color));
    }

    private static void room(String[] comms, String name) {
        Connection connection = broadcaster.getConnectionByLogin(name);
        if(comms.length == 2) {
            broadcaster.removeClientFromRoom(name);
            broadcaster.addClientToRoom(name, comms[1]);
        }

        else if(comms.length==1){
            connection.sendMessage(ServerMessage.serverMessageSend("Ты в комнате "+broadcaster.getRoomNameByUser(name)));
        }
        else{
        connection.sendMessage(ServerMessage.serverMessageSend("Format: /room <roomId>"));
        }

    }

    private static void help(String name){
        if(name.equals("ServerAdminRootSuperKeyForSecurity")) {
            System.out.println(Utilities.getStartText("Commands") + "(help)Вы сервер!");
            return;
        }
        Connection connection = broadcaster.getConnectionByLogin(name);
        String role = connection.getUserRole();
        switch (role){
            case "user":
                connection.sendMessage(ServerMessage.serverMessageSend(""
                        +"Команды роли user:\n"
                        +"/clients - Список онлайн пользователей\n"
                        +"/role - Ваша роль на сервере\n"
                        +"/color - Ваш цвет на сервере\n"
                        +"/help - Список доступных вам команд"
                        +""));
                break;
            case "mod":
                connection.sendMessage(ServerMessage.serverMessageSend(""
                        +"Команды роли mod:\n"
                        +"/updateusercolor <login> <color> - Сменить цвет пользователя\n"
                        +"/kick <login> <reason> - Кикнуть пользователя\n"
                        +"/clients - Список онлайн пользователей\n"
                        +"/role - Ваша роль на сервере\n"
                        +"/color - Ваш цвет на сервере\n"
                        +"/help - Список доступных вам команд"
                        +""));
                break;
            case "admin":
                connection.sendMessage(ServerMessage.serverMessageSend(""
                        +"Команды роли admin:\n"
                        +"/updateuserrole <login> <role> - Сменить роль пользователя\n"
                        +"/updateusercolor <login> <color> - Сменить цвет пользователя\n"
                        +"/kick <login> <reason> - Кикнуть пользователя\n"
                        +"/clients - Список онлайн пользователей\n"
                        +"/role - Ваша роль на сервере\n"
                        +"/color - Ваш цвет на сервере\n"
                        +"/help - Список доступных вам команд"
                        +""));
                break;
                default:
                    connection.sendMessage(ServerMessage.serverMessageSend(""
                            +"Команды роли "+role+"\n"
                            +"/role - Ваша роль на сервере\n"
                            +"/color - Ваш цвет на сервере\n"
                            +"/help - Список доступных вам команд"
                            +""));
                    break;

        }

    }
}
