package com.example.application.views.empty;

import com.example.application.service.DataBaseConfig;
import com.example.application.views.MainLayout;
import com.example.application.views.QuestionnaireListView;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@PageTitle("INICIO")
@Route(value = "empty", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class EmptyView extends VerticalLayout {

    public EmptyView() {
        setSpacing(false);

        Image img = new Image("images/Inicio.png", "Inicio");
        img.setWidth("300px");
        add(img);

        H2 header = new H2("Desbloqueando el Poder de las Matemáticas: " +
                "Aprende, Comprende y Conquista el Mundo Numérico");
        header.addClassNames("margin-top-xlarge", "margin-bottom-medium");
        add(header);
        add(new Paragraph("Descifra el Universo a través del Lenguaje Universal de las Matemáticas🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        Button button = new Button("Empezar a Aprender");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> showOptions());
        add(button);
    }

    private void showOptions() {
        // Verificar si el usuario ya ha iniciado sesión
        if (isUserLoggedIn()) {
            // El usuario ya ha iniciado sesión, redirigir a la página de lista de cuestionarios
            getUI().ifPresent(ui -> ui.navigate(QuestionnaireListView.class));
        } else {
            // Mostrar el diálogo de opciones
            Dialog dialog = new Dialog();
            VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(false);

            TextField usernameField = new TextField("Usuario");
            PasswordField passwordField = new PasswordField("Contraseña");
            Button registerButton = new Button("Registrarse");
            registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button loginButton = new Button("Iniciar sesión");
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button continueButton = new Button("Continuar sin registrarse");
            continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            registerButton.getStyle().set("margin", "0 auto").set("margin-bottom", "10px").set("margin-top", "20px");
            loginButton.getStyle().set("margin", "0 auto").set("margin-bottom", "10px");
            continueButton.getStyle().set("margin", "0 auto").set("margin-bottom", "10px");

            registerButton.addClickListener(e -> {
                String username = usernameField.getValue();
                String password = passwordField.getValue();

                if (isValidRegistration(username, password)) {
                    dialog.close();

                    try {
                        // Obtén una conexión a la base de datos
                        Connection connection = DataBaseConfig.getConnection();

                        // Verificar si el usuario ya existe en la base de datos
                        String checkQuery = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                        checkStatement.setString(1, username);
                        ResultSet checkResult = checkStatement.executeQuery();
                        checkResult.next();
                        int count = checkResult.getInt(1);

                        if (count > 0) {
                            // El usuario ya existe, mostrar mensaje de error
                            Notification.show("Ya existe un usuario con ese nombre", 3000, Position.MIDDLE);
                            // Limpiar el campo de nombre de usuario
                            usernameField.clear();
                            // Abrir nuevamente el diálogo para permitir que el usuario ingrese otro nombre
                            dialog.open();
                        } else {
                            // El usuario no existe, realizar el registro
                            // Crea una declaración para ejecutar una inserción en la tabla 'usuarios'
                            String insertQuery = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
                            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                            insertStatement.setString(1, username);
                            insertStatement.setString(2, password);

                            // Ejecuta la inserción
                            int rowsAffected = insertStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                Notification.show("Usuario registrado exitosamente", 3000, Position.MIDDLE);
                                // Establecer el nombre de usuario en la sesión
                                VaadinSession.getCurrent().setAttribute("username", username);
                                // Redirige a la lista de cuestionarios
                                getUI().ifPresent(ui -> ui.navigate(QuestionnaireListView.class));
                            } else {
                                Notification.show("Error al registrar el usuario", 3000, Position.MIDDLE);
                            }

                            // Cierra los recursos
                            insertStatement.close();
                            connection.close();
                        }

                        // Cierra los recursos
                        checkResult.close();
                        checkStatement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Notification.show("Registro inválido", 3000, Position.MIDDLE);
                }
            });

            loginButton.addClickListener(e -> {
                String username = usernameField.getValue();
                String password = passwordField.getValue();

                try {
                    // Obtén una conexión a la base de datos
                    Connection connection = DataBaseConfig.getConnection();

                    // Crea una consulta para verificar el nombre de usuario
                    String selectQuery = "SELECT username, password FROM usuarios WHERE username = ?";
                    PreparedStatement statement = connection.prepareStatement(selectQuery);
                    statement.setString(1, username);

                    // Ejecuta la consulta
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        // El nombre de usuario existe en la base de datos
                        String storedUsername = resultSet.getString("username");
                        String storedPassword = resultSet.getString("password");

                        if (username.equals(storedUsername)) {
                            // Las credenciales son válidas, verificar la contraseña
                            if (password.equals(storedPassword)) {
                                // Contraseña correcta, inicia sesión y redirige a la lista de cuestionarios
                                VaadinSession.getCurrent().setAttribute("username", username);
                                dialog.close();
                                getUI().ifPresent(ui -> ui.navigate(QuestionnaireListView.class));
                            } else {
                                // Contraseña incorrecta, muestra un mensaje de error
                                Notification.show("Contraseña incorrecta", 3000, Position.MIDDLE);
                            }
                        } else {
                            // El nombre de usuario no coincide, muestra un mensaje de error
                            Notification.show("Usuario no encontrado", 3000, Position.MIDDLE);
                        }
                    } else {
                        // El nombre de usuario no existe, muestra un mensaje de error
                        Notification.show("Usuario no encontrado", 3000, Position.MIDDLE);
                    }

                    // Cierra los recursos
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });


            continueButton.addClickListener(e -> {
                dialog.close();
                // Lógica para continuar sin registrarse
                getUI().ifPresent(ui -> ui.navigate(QuestionnaireListView.class));
            });

            layout.add(usernameField, passwordField, registerButton, loginButton, continueButton);
            dialog.add(layout);
            dialog.open();
        }
    }

    private boolean isValidRegistration(String username, String password) {
        // Verificar que el nombre de usuario no esté vacío
        if (username.isEmpty()) {
            return false;
        }

        // Verificar que el nombre de usuario solo contenga letras
        if (!username.matches("[a-zA-Z]+")) {
            return false;
        }

        // Verificar que la contraseña tenga al menos 8 caracteres
        if (password.length() < 8) {
            return false;
        }

        // Si todas las validaciones pasan, se considera un registro válido
        return true;
    }

    private boolean isUserLoggedIn() {
        // Verificar si hay una sesión activa y si el usuario ha iniciado sesión
        return VaadinSession.getCurrent().getAttribute("username") != null;
    }

}
