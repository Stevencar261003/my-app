package com.example.application.views;

import com.example.application.service.DataBaseConfig;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.grid.Grid;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PageTitle("DC-MateFácil")
@Route(value = "questionnaires", layout = MainLayout.class)
public class QuestionnaireListView extends VerticalLayout {

    private int practicedQuestionnairesCount = 0;
    private int accumulatedPoints = 0;
    private int userLevel = 1;

    private List<Result> results = new ArrayList<>();

    public QuestionnaireListView() {
        setSpacing(false);

        // Verificar si el usuario ha iniciado sesión antes de mostrar el icono y el diálogo de opciones de usuario
        if (isUserLoggedIn()) {
            // Obtener el nombre de usuario de la sesión
            String username = VaadinSession.getCurrent().getAttribute("username").toString();

            // Crear un componente de encabezado para mostrar el nombre de usuario
            Div headerContainer = new Div();
            headerContainer.setWidth("100%");
            headerContainer.getStyle().set("text-align", "center");
            headerContainer.getStyle().set("margin-top", "5px").set("margin-bottom", "20px"); // Agregar un margen superior

            H2 usernameHeader = new H2("Bienvenido, " + username);
            usernameHeader.getStyle().set("font-family", "Arial, sans-serif");
            headerContainer.add(usernameHeader);

            add(headerContainer);
        }

        Div imageContainer = new Div();
        imageContainer.getStyle().set("display", "flex");
        imageContainer.getStyle().set("justify-content", "center");
        imageContainer.setWidth("100%");

        Image gifImage = new Image("images/tets-de-matematicas.gif", "tets-de-matematicas");
        gifImage.getStyle().set("max-width", "100%");
        imageContainer.add(gifImage);

        add(imageContainer);

        H2 header = new H2("Lista de temas para aprender: ");
        add(header);

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidth("100%");

        List<String> questionnaireList = Arrays.asList(
                "Números reales",
                "Álgebra: polinomios y factorización",
                "Geometría: área y perímetro de figuras planas",
                "Trigonometría: identidades trigonométricas",
                "Estadística y Probabilidad: medidas de tendencia central y probabilidad",
                "Funciones: lineales y cuadráticas"
        );

        VerticalLayout questionnaireLayout = new VerticalLayout();
        for (String questionnaire : questionnaireList) {
            Button questionnaireButton = new Button(questionnaire);
            questionnaireButton.setWidth("600px"); // Ajustar el tamaño del botón
            questionnaireButton.addClickListener(event -> showLevelSelectionDialog(questionnaire));
            questionnaireButton.getStyle().set("margin-bottom", "10px"); // Agregar espacio inferior entre los botones
            questionnaireLayout.add(questionnaireButton);
        }
        contentLayout.add(questionnaireLayout);

        Image secondImage = new Image("images/g.gif", "g");
        secondImage.getStyle().set("max-width", "100%");
        secondImage.getStyle().set("margin-left", "auto"); // Alinea la imagen a la derecha
        contentLayout.add(secondImage);

        add(contentLayout);

        Button backButton = new Button("Atrás", event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        backButton.getStyle().set("background-color", "blue"); // Cambiar el color del botón
        backButton.getStyle().set("color", "white"); // Cambiar el color de la letra del botón
        add(backButton);

        // Verificar si el usuario ha iniciado sesión antes de mostrar el icono y el diálogo de opciones de usuario
        if (isUserLoggedIn()) {
            // Obtener el nombre de usuario de la sesión
            String username = VaadinSession.getCurrent().getAttribute("username").toString();

            // Crear un HorizontalLayout para el botón del nombre de usuario
            HorizontalLayout userLayout = new HorizontalLayout();
            userLayout.setWidthFull();
            userLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            // Mostrar el nombre de usuario en el botón
            Button usernameButton = new Button(username);
            usernameButton.getStyle().set("cursor", "pointer");
            usernameButton.addClickListener(e -> showUserOptionsDialog());
            userLayout.add(usernameButton);

            // Agregar el HorizontalLayout al diseño principal
            add(userLayout);
        }
    }

    private boolean isUserLoggedIn() {
        // Verificar si hay una sesión activa y si el usuario ha iniciado sesión
        return VaadinSession.getCurrent().getAttribute("username") != null;
    }

    private void showUserOptionsDialog() {
        Dialog userOptionsDialog = new Dialog();
        userOptionsDialog.setCloseOnOutsideClick(true);

        // Obtén el nombre de usuario de la sesión
        String username = VaadinSession.getCurrent().getAttribute("username").toString();

        // Obtén los resultados actualizados del usuario
        List<Result> results = getUserResults(username);

        // Obtén los valores actualizados de la sesión de Vaadin y realiza la conversión de tipo
        practicedQuestionnairesCount = VaadinSession.getCurrent().getAttribute("practicedQuestionnairesCount") != null ?
                (int) VaadinSession.getCurrent().getAttribute("practicedQuestionnairesCount") : 0;
        accumulatedPoints = VaadinSession.getCurrent().getAttribute("accumulatedPoints") != null ?
                (int) VaadinSession.getCurrent().getAttribute("accumulatedPoints") : 0;

        // Crear un contenedor para centrar la imagen
        Div imageContainer = new Div();
        imageContainer.getStyle().set("display", "flex");
        imageContainer.getStyle().set("justify-content", "center");

        // Crear el componente Image y establecer la imagen deseada
        Image userImage = new Image("images/user.png", "user");
        userImage.setWidth("100px"); // Ajustar el tamaño de la imagen según tus necesidades

        // Agregar la imagen al contenedor
        imageContainer.add(userImage);

        // Agregar el contenedor al cuadro de diálogo
        userOptionsDialog.add(imageContainer);

        Button resultsButton = new Button("Ver resultados", event -> {
            userOptionsDialog.close();
            showResults(VaadinSession.getCurrent().getAttribute("username").toString());
        });

        Button logoutButton = new Button("Cerrar sesión", event -> {
            userOptionsDialog.close();
            VaadinSession.getCurrent().close();
            UI.getCurrent().getPage().setLocation("empty"); // Navega a la página de inicio (EmptyView)
        });

        resultsButton.getStyle().set("margin", "0 auto").set("margin-bottom", "10px");
        logoutButton.getStyle().set("margin", "0 auto").set("margin-bottom", "10px");
        VerticalLayout userOptionsLayout = new VerticalLayout(resultsButton, logoutButton);
        userOptionsLayout.setSpacing(true);

        int highScoreQuestionnairesCount = 0;
        int totalPoints = 0;
        accumulatedPoints = 0; // Reinicializar los puntos acumulados


        // Calcular la cantidad de cuestionarios con puntaje mayor o igual a 8 y el puntaje total
        for (Result result : results) {
            if (result.getPuntaje() >= 8) {
                highScoreQuestionnairesCount++;
            }
            totalPoints += result.getPuntaje();

            if (result.getPuntaje() == 10) {
                accumulatedPoints += 100;
            }
        }

        // Calcular los puntos acumulados después de agregar los puntos adicionales
        accumulatedPoints += (highScoreQuestionnairesCount / 2) * 100;


        // Actualizar los valores en la sesión de Vaadin
        VaadinSession.getCurrent().setAttribute("practicedQuestionnairesCount", highScoreQuestionnairesCount);
        VaadinSession.getCurrent().setAttribute("accumulatedPoints", accumulatedPoints);

        // Mostrar el puntaje acumulado y los puntos en el cuadro de diálogo
        Paragraph accumulatedPointsParagraph = new Paragraph("Puntos acumulados: " + accumulatedPoints);
        accumulatedPointsParagraph.getStyle().set("font-weight", "bold");
        accumulatedPointsParagraph.getStyle().set("text-align", "center");
        userOptionsLayout.add(accumulatedPointsParagraph);

        // Calcular el nivel del usuario en función de los puntos acumulados
        int userLevel = (int) Math.floor(accumulatedPoints / 200.0);

        // Mostrar el nivel del usuario en el cuadro de diálogo
        Paragraph userLevelParagraph = new Paragraph("Nivel: " + userLevel);
        userLevelParagraph.getStyle().set("font-weight", "bold");
        userLevelParagraph.getStyle().set("text-align", "center");
        userOptionsLayout.add(userLevelParagraph);


        userOptionsDialog.add(userOptionsLayout);
        userOptionsDialog.open();
    }

    private void showResults(String username) {
        List<Result> results = getUserResults(username);

        Dialog resultsDialog = new Dialog();
        resultsDialog.setWidth("600px");
        resultsDialog.setHeight("400px");

        if (!results.isEmpty()) {
            Grid<Result> grid = new Grid<>(Result.class);
            grid.setItems(results);
            grid.setColumns("cuestionario", "nivel", "puntaje");

            resultsDialog.add(grid);

        } else {
            Paragraph noResultsMessage = new Paragraph("No hay resultados disponibles para este usuario.");
            resultsDialog.add(noResultsMessage);
        }

        resultsDialog.open();
    }


    private void showLevelSelectionDialog(String questionnaire) {
        Dialog levelDialog = new Dialog();
        levelDialog.setCloseOnOutsideClick(true); // Habilitar el cierre al hacer clic fuera del cuadro de diálogo

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Centrar los botones horizontalmente

        H2 header = new H2("Selecciona un nivel para " + questionnaire);
        levelDialog.add(header);

        List<String> levelList = Arrays.asList("Fácil", "Medio", "Difícil");

        for (String level : levelList) {
            Button levelButton = new Button(level);
            levelButton.setWidth("200px"); // Ajustar el tamaño del botón
            levelButton.addClickListener(event -> {
                levelDialog.close();
                navigateToQuestionnaireCompleteView(questionnaire, level);

            });
            buttonLayout.add(levelButton);
        }

        levelDialog.add(buttonLayout);
        levelDialog.open();

    }

    private void navigateToQuestionnaireCompleteView(String questionnaire, String level) {
        String route;
        switch (questionnaire) {
            case "Números reales":
                route = "real-numbers";
                break;
            case "Álgebra: polinomios y factorización":
                route = "algebra";
                break;
            case "Geometría: área y perímetro de figuras planas":
                route = "geometry";
                break;
            case "Trigonometría: identidades trigonométricas":
                route = "trigonometry";
                break;
            case "Estadística y Probabilidad: medidas de tendencia central y probabilidad":
                route = "statistics-probability";
                break;
            case "Funciones: lineales y cuadráticas":
                route = "functions";
                break;
            default:
                // Si no se encuentra el tema, redirige a una página de error o a la lista de temas
                route = "questionnaires";
                break;
        }
        String navigationRoute = route + "?questionnaire=" + questionnaire.replaceAll(" ", "%20") +
                "&level=" + level.toLowerCase();
        getUI().ifPresent(ui -> ui.navigate(navigationRoute));

    }

    private void navigateToResultsView() {
        String username = VaadinSession.getCurrent().getAttribute("username").toString();

        // Cierra el cuadro de diálogo
        UI.getCurrent().getPage().executeJs("setTimeout(function() { $0.close(); }, 0);", getElement());

        // Navega a la vista de resultados directamente
        getUI().ifPresent(ui -> ui.navigate("results?username=" + username));
    }

    private List<Result> getUserResults(String username) {
        List<Result> results = new ArrayList<>();

        try {
            Connection connection = DataBaseConfig.getConnection();
            String selectQuery = "SELECT cuestionario, nivel, puntaje FROM cuestionarios WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, username);

            System.out.println("Executing query: " + selectQuery);
            System.out.println("Username parameter: " + username);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String questionnaire = resultSet.getString("cuestionario");
                String level = resultSet.getString("nivel");
                int score = resultSet.getInt("puntaje");
                Result result = new Result(questionnaire, level, score);
                results.add(result);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Number of results: " + results.size());
        for (Result result : results) {
            System.out.println(result.getCuestionario() + " - " + result.getNivel() + " - " + result.getPuntaje());
        }

        // Asignar los resultados obtenidos a la variable de instancia 'results'
        this.results = results;

        return results;
    }


}
