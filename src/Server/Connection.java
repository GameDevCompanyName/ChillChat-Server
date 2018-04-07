package Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/*
Класс соединения. Создается на время соединения с клиентом в отдельном потоке. В нем происходит обмен сообщениями.
 */

public class Connection extends Thread {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Broadcaster broadcaster;
    private DBConnector dbConnector;
    private String startText = Utilities.getStartText("Connection");

    private String userName;  //Имя пользователя
    private String userColor;  //Цвет пользователя
    private String userRole; //Роль пользователя

    public Connection(Socket socket, Broadcaster broadcaster, DBConnector dbConnector) {

        this.broadcaster = broadcaster;
        this.dbConnector = dbConnector;
        this.socket = socket;

        //Потоки ввода-вывода
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(),
                    Charset.forName("UTF-8"))),
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println(startText+"Обработка клиента " + socket.getInetAddress());
            //Ожидаем получение версии клиента
            String version = in.readLine();
            String[] result = ServerMessage.read(version, this).split(":");
            if (!result[0].equals("version")||result[1].equals("false")){
                if(!result[0].equals("disconnect")){
                    disconnect("Несовместимая версия");
                    return;
                }
                return;
            }
            System.out.println(startText+"Версия клиента: "+result[1]);
            boolean loggedIn = false;

            while (!loggedIn){
                System.out.println(startText+"Ожидание попытки залогинится.");
                //Ожидаем получение логина и пароля
                String loginAttempt = in.readLine();
                String[] loginResult = ServerMessage.read(loginAttempt, this).split(":");
                if(!loginResult[0].equals("loginAttempt") || loginResult[1].equals("false")){
                    out.println(ServerMessage.loginWrongErrorSend());
                    System.out.println(startText+"Неверный пароль");
                    continue;
                }
                if(broadcaster.getConnectionByLogin(loginResult[1])!=null){
                    out.println(ServerMessage.loginAlreadyErrorSend());
                    System.out.println(startText+"Пользователь уже онлайн");
                    continue;
                }

                //Успешный логин
                userName = loginResult[1];
                userColor = dbConnector.getUserColor(loginResult[1]);
                userRole = dbConnector.getUserRole(loginResult[1]);
                loggedIn = true;
                out.println(ServerMessage.loginSuccessSend());
                System.out.println(startText+"Успешный логин");
            }

            //Добавляем в список соединений
            broadcaster.connectClient(this);
            out.println(ServerMessage.userColorSend(userName, userColor));

            //В цикле читаем сообщений пользователя
            while (true){

                String incomingMessage = in.readLine();
                ServerMessage.read(incomingMessage, this);

            }


        } catch (IOException e) {
            broadcaster.disconnectClient(userName);
            this.interrupt();
        }

    }

    //Обновление цвета в соединении и отправка пользователю его нового цвета
    public void updateColor(String color){
        userColor = color;
        System.out.println(startText+"Цвет пользователя "+ userName +" изменен на "+userColor);
        out.println(ServerMessage.serverMessageSend("Ваш цвет обновлен"));
        out.println(ServerMessage.userColorSend(userName,color));
    }

    public void updateRole(String role){
        userRole = role;
        System.out.println(startText+"Роль пользователя "+ userName +" изменена на "+userRole);
        out.println(ServerMessage.serverMessageSend("Ваша роль обновлена на "+role));

    }

    //Отправка сообщения
    public void sendMessage(String message){
        out.println(message);
    }

    public String getUserName(){
        return userName;
    }

    public String getUserColor() {
        return userColor;
    }

    public String getUserRole() {
        return userRole;
    }

    //Отключение пользователя, закрытие соединения и потока
    public void disconnect(String reason) {
        out.println(ServerMessage.userDisconnectSend(reason));
        try {

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }
}
