package com.example.application;

import com.example.application.service.DataBaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // Obtén una conexión a la base de datos utilizando la clase DataBaseConfig
            Connection connection = DataBaseConfig.getConnection();

            // Crea una consulta para obtener los cuestionarios guardados en la tabla 'cuestionarios'
            String query = "SELECT * FROM cuestionarios";
            PreparedStatement statement = connection.prepareStatement(query);

            // Ejecuta la consulta y obtén los resultados
            ResultSet resultSet = statement.executeQuery();

            // Itera sobre los resultados y muestra la información
            while (resultSet.next()) {
                String usuario = resultSet.getString("usuario");
                String nivel = resultSet.getString("nivel");
                int puntaje = resultSet.getInt("puntaje");
                System.out.println("Usuario: " + usuario + ", Nivel: " + nivel + ", Puntaje: " + puntaje);
            }

            // Cierra los recursos
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
