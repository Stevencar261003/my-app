package com.example.application.views;

import com.example.application.service.DataBaseConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("DC-MateFácil")
@Route(value = "geometry", layout = MainLayout.class)
public class GeometryView extends VerticalLayout implements HasUrlParameter<String> {

    private String level = "";
    private String questionnaire = "";
    private static final Map<String, List<String>> questionOptions = new HashMap<>();
    private List<String> correctAnswers = new ArrayList<>();
    private int score = 0;
    private Notification notification;

    static {
        questionOptions.put("Fácil", Arrays.asList("Opción A", "Opción B", "Opción C"));
        questionOptions.put("Medio", Arrays.asList("Opción X", "Opción Y", "Opción Z", "Opción W"));
        questionOptions.put("Difícil", Arrays.asList("Opción 1", "Opción 2", "Opción 3", "Opción 4"));
    }

    public GeometryView() {
        getElement().getStyle()
                .set("background-image", "url('images/Geometry.jpeg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }

    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "La suma de las longitudes de todos los lados de la figura",
                        "Área = (base × altura) / 2",
                        "Un polígono con lados y ángulos congruentes",
                        "El diámetro es el doble del radio",
                        "Área = π × radio²",
                        "Una línea que une dos vértices no adyacentes del polígono",
                        "Área = (base mayor + base menor) × altura / 2",
                        "Área = base × altura",
                        "Un ángulo que mide menos de 90 grados",
                        "Área = (perímetro × apotema) / 2"
                );
                return Arrays.asList(
                        "¿Qué es el perímetro de una figura plana?",
                        "¿Cuál es la fórmula para calcular el área de un triángulo?",
                        "¿Qué es un polígono regular?",
                        "¿Cuál es la relación entre el radio y el diámetro de un círculo?",
                        "¿Cuál es la fórmula para calcular el área de un círculo?",
                        "¿Qué es la diagonal de un polígono?",
                        "¿Cuál es la fórmula para calcular el área de un trapecio?",
                        "¿Cuál es la fórmula para calcular el área de un romboide?",
                        "¿Qué es un ángulo agudo?",
                        "¿Cuál es la fórmula para calcular el área de un polígono regular?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "12 cm",
                        "113.04 cm²",
                        " cm",
                        " cm²",
                        "25 cm",
                        "24 cm²",
                        " cm",
                        " cm²",
                        "28 cm",
                        " cm²"
                );
                return Arrays.asList(
                        "¿Cuál es el perímetro de un triángulo rectángulo con catetos de longitud 3 cm y 4 cm?",
                        "¿Cuál es el área de un círculo con radio de longitud 6 cm? (Usa π ≈ 3.14)",
                        "¿Cuál es el perímetro de un trapecio isósceles con lados paralelos de longitud 5 cm y 7 cm, y altura de 4 cm?",
                        "¿Cuál es el área de un triángulo equilátero con lado de longitud 8 cm?",
                        "¿Cuál es el perímetro de un pentágono regular con lado de longitud 5 cm?",
                        "¿Cuál es el área de un rombo con diagonales de longitud 6 cm y 8 cm?",
                        "¿Cuál es el perímetro de un trapecio rectángulo con bases de longitud 4 cm y 6 cm, y altura de 3 cm?",
                        "¿Cuál es el área de un hexágono regular con lado de longitud 9 cm?",
                        "¿Cuál es el perímetro de un polígono de 7 lados iguales, cada uno con longitud 4 cm?",
                        "¿Cuál es el área de un trapecio con bases de longitud 5 cm y 9 cm, y altura de 6 cm?"
                );
            case "difícil":
                correctAnswers = Arrays.asList(
                        "35 cm",
                        "40 cm²",
                        "62.8 cm",
                        "13.5 cm²",
                        "36 cm",
                        "37.68 cm²",
                        "24 cm",
                        "54 cm²",
                        "46.8 cm",
                        "32.5 cm²"
                );
                return Arrays.asList(
                        "¿Cuál es el perímetro de un pentágono regular inscrito en una circunferencia de radio 5 cm? (Usa π ≈ 3.14)",
                        "¿Cuál es el área de un romboide con base de longitud 8 cm, altura de 5 cm y un ángulo interno de 60 grados?",
                        "¿Cuál es el perímetro de un círculo cuyo radio mide 10 cm? (Usa π ≈ 3.14)",
                        "¿Cuál es el área de un triángulo isósceles con lados de longitud 6 cm y un ángulo opuesto de 45 grados?",
                        "¿Cuál es el perímetro de un trapecio con bases de longitud 4 cm y 10 cm, y altura de 8 cm?",
                        "¿Cuál es el área de un sector circular de radio 6 cm y ángulo central de 120 grados? (Usa π ≈ 3.14)",
                        "¿Cuál es el perímetro de un polígono regular de 8 lados, cada uno con longitud 3 cm?",
                        "¿Cuál es el área de un trapezoide con bases de longitud 6 cm y 12 cm, y altura de 9 cm?",
                        "¿Cuál es el perímetro de un hexágono regular inscrito en una circunferencia de diámetro 10 cm? (Usa π ≈ 3.14)",
                        "¿Cuál es el área de un triángulo escaleno con lados de longitud 7 cm, 9 cm y 12 cm?"
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<List<String>> getOptionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                return Arrays.asList(
                        Arrays.asList(
                                "La suma de las longitudes de todos los lados de la figura",
                                "La distancia más corta entre dos puntos de la figura",
                                "La medida de la curvatura de la figura"
                        ),
                        Arrays.asList(
                                "Área = base × altura",
                                "Área = (base × altura) / 2",
                                "Área = lado × lado"
                        ),
                        Arrays.asList(
                                "Un polígono con lados y ángulos congruentes",
                                "Un polígono con lados de igual longitud",
                                "Un polígono con ángulos rectos"
                        ),
                        Arrays.asList(
                                "El radio es el doble del diámetro",
                                "El diámetro es el doble del radio",
                                "El radio y el diámetro son iguales"
                        ),
                        Arrays.asList(
                                "Área = π × radio²",
                                "Área = 2 × π × radio",
                                "Área = π × diámetro"
                        ),
                        Arrays.asList(
                                "Una línea que une dos vértices no adyacentes del polígono",
                                "Una línea que divide al polígono en dos partes congruentes",
                                "Una línea que une los puntos medios de dos lados opuestos del polígono"
                        ),
                        Arrays.asList(
                                "Área = (base mayor + base menor) × altura / 2",
                                "Área = (base mayor + base menor) × altura",
                                "Área = (base mayor - base menor) × altura"
                        ),
                        Arrays.asList(
                                "Área = base × altura",
                                "Área = lado × lado",
                                "Área = base × altura / 2"
                        ),
                        Arrays.asList(
                                "Un ángulo que mide menos de 90 grados",
                                "Un ángulo que mide exactamente 90 grados",
                                "Un ángulo que mide más de 90 grados"
                        ),
                        Arrays.asList(
                                "Área = (perímetro × apotema) / 2",
                                "Área = (perímetro × radio) / 2",
                                "Área = (lado × apotema) / 2"
                        )
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList("12 cm", "14 cm", "18 cm"),
                        Arrays.asList("113.04 cm²", "226.08 cm²", "452.16 cm²"),
                        Arrays.asList("26 cm", "28 cm", "30 cm"),
                        Arrays.asList("27.71 cm²", "34.64 cm²", "69.28 cm²"),
                        Arrays.asList("25 cm", "30 cm", "35 cm"),
                        Arrays.asList("24 cm²", "28 cm²", "32 cm²"),
                        Arrays.asList("14 cm", "16 cm", "18 cm"),
                        Arrays.asList("279.36 cm²", "392.04 cm²", "508.02 cm²"),
                        Arrays.asList("28 cm", "32 cm", "36 cm"),
                        Arrays.asList("27 cm²", "30 cm²", "36 cm²")
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList("15 cm", "25 cm", "35 cm"),
                        Arrays.asList("30 cm²", "40 cm²", "20 cm²"),
                        Arrays.asList("62.8 cm", "31.4 cm", "78.5 cm"),
                        Arrays.asList("13.5 cm²", "16.5 cm²", "18.5 cm²"),
                        Arrays.asList("28 cm", "32 cm", "36 cm"),
                        Arrays.asList("37.68 cm²", "45.24 cm²", "56.55 cm²"),
                        Arrays.asList("24 cm", "30 cm", "36 cm"),
                        Arrays.asList("54 cm²", "72 cm²", "108 cm²"),
                        Arrays.asList("31.4 cm", "46.8 cm", "62.8 cm"),
                        Arrays.asList("25.5 cm²", "32.5 cm²", "39.5 cm²")
                );
            default:
                return new ArrayList<>();
        }
    }

    private void showQuestions(List<String> questions, List<List<String>> options, String level) {
        removeAll();

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setWidth("800px");
        contentLayout.getStyle().set("margin-left", "auto").set("margin-right", "auto");
        contentLayout.getStyle().set("background-color", "white");

        H2 header = new H2("Cuestionario de Geometría: área y perímetro de figuras planas - Nivel: " + level);
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

        // Obtén el nombre de usuario del usuario actualmente autenticado
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
