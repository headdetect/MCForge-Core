/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.mcforge.server.Server;

public class SQLite implements ISQL {

    protected Connection connection;
    private Server server;
    private String prefix;
    private String file;
    private final String PATH = "jdbc:sqlite:";
    @Override
    public void executeQuery(String command) {
        try {
            if (connection.isClosed())
                connect(server);
        } catch (SQLException e) {//
            connect(server);
        }
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(command);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Set the file SQLite will use to save the database
     * to.
     * @param file
     */
    public void setFile(String file) {
        this.file = file;
    }
    
    /**
     * Get the file SQLite is saving the database to.
     * @return
     */
    public String getFile() {
        return file;
    }

    @Override
    public void executeQuery(String[] commands) {
        try {
            if (connection.isClosed())
                connect(server);
        } catch (SQLException e) {
            connect(server);
        }
        try {
            Statement statement = connection.createStatement();
            for (String s : commands) {
                try {
                    statement.executeUpdate(s);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet fillData(String command) {
        try {
            if (connection.isClosed())
                connect(server);
        } catch (SQLException e) {
            connect(server);
        }
        try {
            return connection.prepareStatement(command).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void connect(Server server) {
        this.server = server;
        try {
            if (!new File("MCForge.db").exists())
                new File("MCForge.db").createNewFile();
            DriverManager.registerDriver(new org.sqlite.JDBC());
            connection = DriverManager.getConnection(PATH + getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Get the connection
     * @return java.sql.connection
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public String getPrefix() {
        return prefix;
    }
}

