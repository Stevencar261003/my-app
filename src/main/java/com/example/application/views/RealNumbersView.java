package com.example.application.views;

import com.example.application.service.DataBaseConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.notification.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("DC-MateFácil")
@Route(value = "real-numbers", layout = MainLayout.class)

public class RealNumbersView extends VerticalLayout implements HasUrlParameter<String> {

    private String level = "";
    private String questionnaire = "";
    private static final Map<String, List<String>> questionOptions = new HashMap<>();
    private List<String> correctAnswers = new ArrayList<>();
    private int score = 0;
    private Notification notification;


    static {
        questionOptions.put("Fácil", Arrays.asList("Opción A", "Opción B", "Opción C"));
        questionOptions.put("Medio", Arrays.asList("Opción W", "Opción X", "Opción Y", "Opción Z"));
        questionOptions.put("Difícil", Arrays.asList("Opción 1", "Opción 2", "Opción 3", "Opción 4"));
    }

    public RealNumbersView() {
        getElement().getStyle()
                .set("background-image", "url('images/fondo.jpeg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }



    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "5",
                        "4",
                        "5",
                        "8",
                        "0",
                        "5",
                        "3",
                        "-10",
                        "-18",
                        "-4"
                );
                return Arrays.asList(
                        "¿Cuál es el valor absoluto de -5?",
                        "¿Cuál es la raíz cuadrada de 16?",
                        "¿Cuál es el resultado de 2 + 3?",
                        "¿Cuál es el opuesto aditivo de -8?",
                        "¿Cuál es el valor absoluto de 0?",
                        "¿Cuál es la raíz cuadrada de 25?",
                        "¿Cuál es el resultado de 7 - 4?",
                        "¿Cuál es el opuesto aditivo de 10?",
                        "¿Cuál es el resultado de -3 * 6?",
                        "¿Cuál es el cociente de 20 entre -5?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "3/4",
                        "3",
                        "1",
                        "12",
                        "16",
                        "4/10",
                        "5",
                        "3/4",
                        "8",
                        "-5"
                );
                return Arrays.asList(
                        "¿Cuál es la fracción irreducible de 6/8?",
                        "¿Cuál es la raíz cuadrada de 9?",
                        "¿Cuál es el resultado de 1/4 + 3/4?",
                        "¿Cuál es el valor absoluto de -12?",
                        "¿Cuál es el resultado de 2 * (5 + 3)?",
                        "¿Cuál es la fracción equivalente a 2/5 con denominador 10?",
                        "¿Cuál es el resultado de 10 - 7 + 2?",
                        "¿Cuál es la fracción irreducible de 12/16?",
                        "¿Cuál es el resultado de -2 * (-4)?",
                        "¿Cuál es el cociente de -15 entre 3?"
                );
            case "difícil":
                correctAnswers = Arrays.asList(
                        "0.67",
                        "3",
                        "3/2",
                        "15",
                        "-8",
                        "3/4",
                        "1",
                        "0.625",
                        "6",
                        "-5"
                );
                return Arrays.asList(
                        "¿Cuál es la fracción decimal de 2/3?",
                        "¿Cuál es la raíz cúbica de 27?",
                        "¿Cuál es el resultado de (3/4) / (1/2), redondeado a dos decimales?",
                        "¿Cuál es el valor absoluto de -15?",
                        "¿Cuál es el resultado de (-2)³?",
                        "¿Cuál es la fracción irreducible de 18/24?",
                        "¿Cuál es el resultado de 4 - (2 + 1)?",
                        "¿Cuál es la fracción decimal de 5/8?",
                        "¿Cuál es el resultado de (-3) * (2 - 4)?",
                        "¿Cuál es el cociente de 30 entre -6?"
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<List<String>> getOptionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                return Arrays.asList(
                        Arrays.asList("-5", "0", "5"),
                        Arrays.asList("4", "2", "8"),
                        Arrays.asList("6", "4", "5"),
                        Arrays.asList("8", "-8", "0"),
                        Arrays.asList("1", "0", "-1"),
                        Arrays.asList("5", "4", "25"),
                        Arrays.asList("4", "-3", "3"),
                        Arrays.asList("10", "1", "-10"),
                        Arrays.asList("-9", "18", "-18"),
                        Arrays.asList("4", "-4", "5")
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList("3/4", "2/4", "1/4", "2/3"),
                        Arrays.asList("1", "4", "3", "6"),
                        Arrays.asList("7/4", "1", "-1", "1/2"),
                        Arrays.asList("12", "0", "-12", "2"),
                        Arrays.asList("10", "16", "26", "6"),
                        Arrays.asList("1/2", "4/10", "3/5", "6/7"),
                        Arrays.asList("5", "10", "15", "20"),
                        Arrays.asList("1/8", "3/4", "2/3", "3/5"),
                        Arrays.asList("-8", "8", "16", "4"),
                        Arrays.asList("-5", "-3", "15", "5")
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList("0.5", "0.55", "0.66", "0.67"),
                        Arrays.asList("3", "6", "9", "4"),
                        Arrays.asList("1/8", "3/2", "6/4", "4/5"),
                        Arrays.asList("10", "-15", "20", "15"),
                        Arrays.asList("-2", "-8", "6", "8"),
                        Arrays.asList("3/4", "7/12", "1/2", "5/9"),
                        Arrays.asList("1", "3", "5", "7"),
                        Arrays.asList("0.625", "0.75", "0.8", "0.55"),
                        Arrays.asList("6", "0", "-4", "-6"),
                        Arrays.asList("-10", "-8", "-5", "-2")
                );
            default:
                return new ArrayList<>();
        }
    }

    private void showQuestions(List<String> questions, List<List<String>> options, String level) {
        removeAll();

        VerticalLayout contentLayout = new VerticalLayout();
        //contentLayout.setAlignItems(Alignment.CENTER);
        contentLayout.setWidth("800px");
        contentLayout.getStyle().set("margin-left", "auto").set("margin-right", "auto");
        contentLayout.getStyle().set("background-color", "white"); // Agregar fondo blanco

        H2 header = new H2("Cuestionario de Números Reales - Nivel: " + level);
        header.getStyle().set("font-family", "Arial, sans-serif");
        contentLayout.add(header);

        for (int i = 0; i < questions.size(); i++) {
            H2 questionHeader = new H2("Pregunta " + (i + 1));
            contentLayout.add(questionHeader);

            String question = questions.get(i);
            Paragraph questionText = new Paragraph(question);
            questionText.getStyle().set("font-weight", "bold");
            contentLayout.add(questionText);

            List<String> questionOptions = options.get(i);
            Collections.shuffle(questionOptions); // Ordenar aleatoriamente las opciones
            RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
            radioButtonGroup.setItems(questionOptions);
            radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            contentLayout.add(radioButtonGroup);
        }

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button backButton = new Button("Atrás", event -> {
            getUI().ifPresent(ui -> ui.navigate("questionnaires"));
        });
        buttonLayout.add(backButton);

        Button submitButton = new Button("Enviar", event -> {
            List<String> answers = new ArrayList<>();
            List<RadioButtonGroup<String>> radioGroups = contentLayout.getChildren()
                    .filter(component -> component instanceof RadioButtonGroup)
                    .map(component -> (RadioButtonGroup<String>) component)
                    .collect(Collectors.toList());

            boolean allQuestionsAnswered = radioGroups.stream()
                    .allMatch(radioGroup -> radioGroup.getValue() != null);

            if (!allQuestionsAnswered) {
                if (notification == null) {
                    notification = new Notification();
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.setDuration(3000);
                }
                notification.setText("Faltan preguntas por responder");
                notification.open();
                return;
            }


            for (RadioButtonGroup<String> radioGroup : radioGroups) {
                String selectedOption = radioGroup.getValue();
                answers.add(selectedOption);
            }

            int score = calculateScore(answers, correctAnswers);

            // Obtiene el nombre de usuario de la sesión o establece "Invitado" si está vacío
            Object usernameObj = VaadinSession.getCurrent().getAttribute("username");
            String username = usernameObj != null ? usernameObj.toString() : "Invitado";

            if (username.isEmpty() || username.equals("Invitado")) {
                showScore(score); // Mostrar el puntaje sin guardar en la base de datos
            } else {
                // El usuario ha iniciado sesión, guardar el puntaje en la base de datos
                try {
                    // Obtén una conexión a la base de datos
                    Connection connection = DataBaseConfig.getConnection();

                    // Crea una declaración para ejecutar una inserción en la tabla 'cuestionarios'
                    String insertQuery = "INSERT INTO cuestionarios (username, cuestionario, nivel, puntaje) VALUES (?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(insertQuery);
                    statement.setString(1, username); // Utiliza el nombre de usuario obtenido de la sesión
                    statement.setString(2, questionnaire);
                    statement.setString(3, level);
                    statement.setInt(4, score);

                    // Ejecuta la inserción
                    statement.executeUpdate();

                    // Cierra la conexión y la declaración
                    statement.close();
                    connection.close();

                } catch (SQLException e) {
                    // Maneja cualquier excepción de la base de datos
                    e.printStackTrace();
                }

                showScore(score); // Mostrar el puntaje guardado en la base de datos
            }
        });


        submitButton.getStyle().set("background-color", "blue");
        submitButton.getStyle().set("color", "white");
        buttonLayout.add(submitButton);

        contentLayout.add(buttonLayout);
        add(contentLayout);
    }


    private int calculateScore(List<String> answers, List<String> correctAnswers) {
        int score = 0;
        for (int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);
            String correctAnswer = correctAnswers.get(i);
            if (answer.equalsIgnoreCase(correctAnswer)) {
                score++;
            }
        }
        return score;
    }

    private void showScore(int score) {
        removeAll();

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.getStyle().set("background-color", "white");

        H2 scoreHeader = new H2("Puntaje obtenido: " + score);
        scoreHeader.getStyle().set("font-family", "Arial, sans-serif");
        contentLayout.add(scoreHeader);

        // Obtén el nombre de usuario del usuario actualmente autenticado, o "Invitado" si no hay usuario autenticado
        /*String username = VaadinSession.getCurrent().getAttribute("username").toString();

        try {
            // Obtén una conexión a la base de datos
            Connection connection = DataBaseConfig.getConnection();

            // Crea una declaración para ejecutar una inserción en la tabla 'cuestionarios'
            String insertQuery = "INSERT INTO cuestionarios (username, cuestionario, nivel, puntaje) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, username); // Utiliza el nombre de usuario obtenido de la sesión
            statement.setString(2, questionnaire);
            statement.setString(3, level);
            statement.setInt(4, score);

            // Ejecuta la inserción
            statement.executeUpdate();

            // Cierra la conexión y la declaración
            statement.close();
            connection.close();

        } catch (SQLException e) {
            // Maneja cualquier excepción de la base de datos
            e.printStackTrace();
        }*/

        VerticalLayout scoreLayout = new VerticalLayout();
        scoreLayout.setSizeFull();
        scoreLayout.getStyle().set("background-color", "white");

        scoreLayout.add(scoreHeader);

        Button backButton = new Button("Volver", event -> {
            getUI().ifPresent(ui -> ui.navigate("questionnaires"));
        });
        scoreLayout.add(backButton);

        // Estructura condicional para mostrar diferentes mensajes según el puntaje obtenido
        if (score == 10) {
            // Puntaje máximo
            H3 congratulationsMessage = new H3("¡Felicitaciones! Has alcanzado el puntaje más alto posible.");
            contentLayout.add(congratulationsMessage);
        } else if (score >= 7 && score <= 9) {
            // Puntaje entre 7 y 9
            H3 congratulationsMessage = new H3("¡Felicitaciones! Has obtenido un buen puntaje.");
            contentLayout.add(congratulationsMessage);
        } else {
            // Puntaje menor o igual a 6
            H3 encouragementMessage = new H3("Sigue practicando. ¡Puedes mejorar!");
            contentLayout.add(encouragementMessage);
        }

        contentLayout.add(scoreLayout);
        add(contentLayout);
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        QueryParameters parameters = event.getLocation().getQueryParameters();
        if (parameters.getParameters().containsKey("questionnaire")) {
            questionnaire = parameters.getParameters().get("questionnaire").get(0);
        }
        if (parameters.getParameters().containsKey("level")) {
            level = parameters.getParameters().get("level").get(0);
        }
        List<String> questions = getQuestionsForLevel(level);
        List<List<String>> options = getOptionsForLevel(level);
        if (!questions.isEmpty() && !options.isEmpty()) {
            showQuestions(questions, options, level);
        } else {
            Notification.show("No se encontraron preguntas para el nivel especificado.");
        }
    }
}
