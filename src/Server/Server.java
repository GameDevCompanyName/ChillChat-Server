package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/*
Главный класс. Начальная инициализация основных элементов.
 */

public class Server {

    Broadcaster broadcaster;
    DBConnector dbConnector;
    TimeoutChecker timeoutChecker;

    public Server(){
        broadcaster = new Broadcaster();
        timeoutChecker = new TimeoutChecker(broadcaster);
        try {
            dbConnector = new DBConnector(broadcaster);
            //Инициализируем командную строку сервера в отдельном потоке
            CommandLine cmd = new CommandLine(broadcaster);
            cmd.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Broadcaster.setTimeoutChecker(timeoutChecker);
        ServerMessage.setTimeoutChecker(timeoutChecker);
        ServerMethods.setBroadcaster(broadcaster);
        ServerMethods.setDbConnector(dbConnector);
        Commands.setBroadcaster(broadcaster);
        Commands.setDbConnector(dbConnector);
    }


    public static void main(String[] ar) {

        Server server = new Server();
        String startText = Utilities.getStartText("Server");

        try {
            ServerSocket ss = new ServerSocket(1488);

            //В цикле на каждое подключение по сокету выделяем поток с новым соединением
            while (true) {

                System.out.println(startText+"Ожидаю нового клиента");

                //Ждем подключение по сокету
                Socket socket = ss.accept();

                System.out.println(startText+"Соединение установлено с клиентом: " + socket.getInetAddress());

                //Создаем соединение с клиентом
                Connection con = new Connection(socket, server.broadcaster, server.dbConnector);
                con.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(startText+"Серверу пиздец");
            System.exit(1);
        }

    }


}