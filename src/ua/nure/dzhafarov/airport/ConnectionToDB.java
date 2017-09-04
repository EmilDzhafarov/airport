package ua.nure.dzhafarov.airport;

import java.sql.*;

/**
 * Created by Emil on 07.11.2016.
 */
public class ConnectionToDB
{
    private  Connection connection = null;
    private  Statement statement = null;
    private  ResultSet resultSet = null;

    public ConnectionToDB(String login, String password) throws Exception
    {
        String url = "jdbc:mysql://localhost:3306/airport2?useSSL=false";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(url, login, password);
        statement = connection.createStatement();
    }

    public void executeStatement(String SQL) throws SQLException
    {
        statement.execute(SQL);
    }

    public ResultSet retriveData(String SQL) throws SQLException
    {
        resultSet = statement.executeQuery(SQL);

        return resultSet;
    }

    public void destroy() throws SQLException
    {
        if (connection != null)
            connection.close();
        if (statement != null)
            statement.close();
        if (resultSet != null)
            resultSet.close();
    }
}
