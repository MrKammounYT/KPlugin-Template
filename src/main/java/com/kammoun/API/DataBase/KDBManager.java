package com.kammoun.API.DataBase;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class KDBManager {


    @Getter
    protected Connection connection;
    protected String dbName;
    protected String dbUser;
    protected String dbPassword;
    protected String dbHost;
    protected String dbPort;

    public KDBManager(FileConfiguration config) {
        loadDatabaseInfo(config);
    }


    protected abstract void loadDatabaseInfo(FileConfiguration configuration);
    protected abstract void connect();
    protected abstract void disconnect();

    public boolean isConnected(){
        return connection != null;
    }



    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        if(connection == null || sql == null || sql.isEmpty())return null;
        return connection.prepareStatement(sql);
    }




}
