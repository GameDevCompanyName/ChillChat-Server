package Server;

import java.sql.*;
import java.sql.Connection;
import java.util.Random;

/*
Класс для работы с БД.
 */

public class DBConnector {

    private Connection connection;
    private Broadcaster broadcaster;
    private String startText = Utilities.getStartText("DBConnector");

    public DBConnector(Broadcaster broadcaster) throws SQLException {
        setConnection();
        this.broadcaster = broadcaster;
    }

    //Установка соединения с бд
    private void setConnection() throws SQLException {
        String url = "jdbc:sqlite:ChillChat.db";
        connection = DriverManager.getConnection(url);
        if (connection != null) {
            DatabaseMetaData meta = connection.getMetaData();
            System.out.println(startText+"Подключен к БД");
        }
        checkIfTableExists();
    }

    //Проверка логина и пароля
    public boolean checkLoginAttempt(String login, String password) {

        String sql = "SELECT login, password FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                insertNewUser(login, password);
                return true;
            }
            while (rs.next()) {
                String dbPassword = rs.getString("password");
                if (password.equals(dbPassword)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Проверка наличия таблицы пользователей
    private void checkIfTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + "	id INTEGER PRIMARY KEY,\n"
                + "	login VARCHAR(20) NOT NULL UNIQUE,\n"
                + "	password VARCHAR(20) NOT NULL,\n"
                + "	color VARCHAR(6) NOT NULL,\n"
                + " role VARCHAR(20) NOT NULL,\n"
                + " regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println(startText+"Таблица Users подключена");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Добавление нового пользователя
    public void insertNewUser(String login, String password) {

        String sql = "INSERT INTO Users ("
                + "login,"
                + "password,"
                + "color,"
                + "role) VALUES(?,?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int colorCode = 1 + (new Random().nextInt(7));
            String color = Integer.toString(colorCode);
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, color);
            pstmt.setString(4, "user");
            pstmt.executeUpdate();
            System.out.println(startText+"Пользователь " + login + " добавлен в таблицу");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Сменить пароль юзера
    public void updateUserPassword(String login, String password) {

        String sql = "UPDATE Users SET password = ? WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, password);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
            System.out.println(startText+"Обновление пароля пользователя " + login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Сменить роль юзера
    public void updateUserRole(String login, String role) {

        String sql = "UPDATE Users SET role = ? WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
            broadcaster.getConnectionByLogin(login).updateRole(role);
            System.out.println(startText+"Обновление роли пользователя " + login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Сменить цвет юзера
    public void updateUserColor(String login, String color) {

        String sql = "UPDATE Users SET color = ? WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, color);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
            broadcaster.getConnectionByLogin(login).updateColor(color);
            System.out.println(startText+"Обновление цвета пользователя " + login);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Проверить наличие юзера
    public boolean searchForUser(String login) {
        String sql = "SELECT * FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                return false;
            }
            else
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Получить цвет пользователя
    public String getUserColor(String login){
        String sql = "SELECT color FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()){
                return "false";
            }
            return rs.getString("color");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }

    //Получить роль пользователя
    public String getUserRole(String login){
        String sql = "SELECT role FROM Users WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()){
                return "false";
            }
            return rs.getString("role");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }
}


