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
@Route(value = "statistics-probability", layout = MainLayout.class)
public class StatisticsProbabilityView extends VerticalLayout implements HasUrlParameter<String> {

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

    public StatisticsProbabilityView() {
        getElement().getStyle()
                .set("background-image", "url('images/estadisticaGrande.jpg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }

    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "Media aritmética",
                        "Moda",
                        "Mediana",
                        "Sumar todos los valores y dividir entre el número total de datos",
                        "El valor que se encuentra en el centro del conjunto de datos ordenados",
                        "El valor o valores que más se repiten en el conjunto de datos",
                        "1/2",
                        "1/52",
                        "1/2",
                        "4/10"
                );
                return Arrays.asList(
                        "¿Cuál es la medida de tendencia central que representa el valor medio de un conjunto de datos?",
                        "¿Cuál es la medida de tendencia central que representa el valor que más se repite en un conjunto de datos?",
                        "¿Cuál es la medida de tendencia central que representa el valor que se encuentra en la posición central de un conjunto de datos ordenados?",
                        "¿Cuál es la fórmula para calcular la media aritmética de un conjunto de datos?",
                        "¿Cuál es la fórmula para calcular la mediana de un conjunto de datos?",
                        "¿Cuál es la fórmula para calcular la moda de un conjunto de datos?",
                        "Si lanzas un dado justo de 6 caras, ¿cuál es la probabilidad de obtener un número par?",
                        "Si seleccionas una carta al azar de una baraja de 52 cartas, ¿cuál es la probabilidad de obtener un as?",
                        "Si lanzas una moneda justa, ¿cuál es la probabilidad de obtener cara?",
                        "Si tienes una bolsa con 10 bolas, 4 rojas y 6 azules, ¿cuál es la probabilidad de sacar una bola roja?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "Sumar los cuadrados de las diferencias entre cada valor y la media, y dividir entre el número total de datos",
                        "Raíz cuadrada de la varianza",
                        "6",
                        "5",
                        "5",
                        "1/2",
                        "4/52",
                        "1/2",
                        "7/10",
                        "10"
                );
                return Arrays.asList(
                        "¿Cuál es la fórmula para calcular la varianza de un conjunto de datos?",
                        "¿Cuál es la fórmula para calcular la desviación estándar de un conjunto de datos?",
                        "Si tienes un conjunto de datos: [2, 4, 6, 8, 10], ¿cuál es la media aritmética de esos datos?",
                        "Si tienes un conjunto de datos: [1, 3, 5, 7, 9], ¿cuál es la mediana de esos datos?",
                        "Si tienes un conjunto de datos: [5, 7, 3, 5, 9], ¿cuál es la moda de esos datos?",
                        "Si lanzas un dado justo de 6 caras, ¿cuál es la probabilidad de obtener un número impar?",
                        "Si seleccionas una carta al azar de una baraja de 52 cartas, ¿cuál es la probabilidad de obtener un rey?",
                        "Si lanzas una moneda justa, ¿cuál es la probabilidad de obtener cruz?",
                        "Si tienes una bolsa con 10 bolas, 3 rojas y 7 azules, ¿cuál es la probabilidad de sacar una bola azul?",
                        "Si tienes un conjunto de datos: [2, 4, 6, 8, 10], ¿cuál es la varianza de esos datos?"
                );
            case "difícil":
                correctAnswers = Arrays.asList(
                        "Σ(xᵢ - ẋ)² / N",
                        "√(Σ(xᵢ - ẋ)² / N)",
                        "2.5",
                        "2.5",
                        "0",
                        "P(A|B) = P(A ∩ B) / P(B)",
                        "P(A ∪ B) = P(A) + P(B) - P(A ∩ B)",
                        "1/12",
                        "1/100",
                        "1/2704"
                );
                return Arrays.asList(
                        "¿Cuál es la fórmula para calcular la varianza poblacional?",
                        "¿Cuál es la fórmula para calcular la desviación estándar poblacional?",
                        "Si tienes un conjunto de datos: [1, 2, 3, 4, 5], ¿cuál es el coeficiente de variación de esos datos?",
                        "Si tienes un conjunto de datos: [1, 3, 5, 7, 9], ¿cuál es la desviación media absoluta de esos datos?",
                        "Si tienes un conjunto de datos: [4, 4, 4, 4, 4], ¿cuál es la varianza poblacional de esos datos?",
                        "¿Cuál es la fórmula para calcular la probabilidad condicional?",
                        "¿Cuál es la fórmula para calcular la probabilidad de al menos uno de dos eventos independientes?",
                        "Si lanzas dos dados justos de 6 caras, ¿cuál es la probabilidad de que la suma de los números sea mayor o igual a 10?",
                        "Si tienes una bolsa con 10 bolas, 3 rojas, 4 azules y 3 verdes, ¿cuál es la probabilidad de sacar dos bolas azules en dos intentos con reemplazo?",
                        "Si seleccionas tres cartas al azar con reemplazo de una baraja de 52 cartas, ¿cuál es la probabilidad de que todas sean ases?"
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<List<String>> getOptionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                return Arrays.asList(
                        Arrays.asList("Media aritmética", "Mediana", "Moda"),
                        Arrays.asList("Media aritmética", "Mediana", "Moda"),
                        Arrays.asList("Media aritmética", "Mediana", "Moda"),
                        Arrays.asList("Sumar todos los valores y dividir entre el número total de datos", "Calcular el promedio de los valores extremos", "Sumar los valores mayores y menores y dividir entre dos"),
                        Arrays.asList("El valor más pequeño del conjunto de datos", "El valor que se encuentra en el centro del conjunto de datos ordenados", "El valor más grande del conjunto de datos"),
                        Arrays.asList("El valor o valores que más se repiten en el conjunto de datos", "La suma de todos los valores dividida entre el número total de datos", "El valor medio del conjunto de datos"),
                        Arrays.asList("1/2", "1/6", "2/3"),
                        Arrays.asList("1/13", "1/52", "1/26"),
                        Arrays.asList("1/2", "1/3", "2/3"),
                        Arrays.asList("4/10", "3/10", "2/5")
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList(
                                "Sumar los cuadrados de las diferencias entre cada valor y la media, y dividir entre el número total de datos",
                                "Restar el valor mínimo del valor máximo",
                                "Multiplicar todos los valores",
                                "Dividir el valor máximo por el valor mínimo"
                        ),
                        Arrays.asList(
                                "Dividir la suma de todos los valores entre el número total de datos",
                                "Calcular el promedio de los valores",
                                "Sumar todos los valores",
                                "Dividir el valor máximo entre el valor mínimo"
                        ),
                        Arrays.asList(
                                "3",
                                "5",
                                "7",
                                "10"
                        ),
                        Arrays.asList(
                                "3",
                                "4",
                                "5",
                                "6"
                        ),
                        Arrays.asList(
                                "2",
                                "4",
                                "5",
                                "9"
                        ),
                        Arrays.asList(
                                "1/6",
                                "1/2",
                                "2/3",
                                "5/6"
                        ),
                        Arrays.asList(
                                "1/13",
                                "4/52",
                                "1/4",
                                "1/2"
                        ),
                        Arrays.asList(
                                "1/3",
                                "1/2",
                                "2/3",
                                "3/4"
                        ),
                        Arrays.asList(
                                "3/10",
                                "4/10",
                                "6/10",
                                "7/10"
                        ),
                        Arrays.asList(
                                "5",
                                "8",
                                "10",
                                "12"
                        )
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList(
                                "Σ(xᵢ - ẋ)² / N",
                                "Σ(xᵢ - ẋ)² / (N - 1)",
                                "Σ(xᵢ - ẋ) / N",
                                "Σ(xᵢ - ẋ) / (N - 1)"
                        ),
                        Arrays.asList(
                                "√(Σ(xᵢ - ẋ)² / N)",
                                "√(Σ(xᵢ - ẋ)² / (N - 1))",
                                "Σ(xᵢ - ẋ) / N",
                                "Σ(xᵢ - ẋ) / (N - 1)"
                        ),
                        Arrays.asList(
                                "2",
                                "2.5",
                                "3",
                                "3.5"
                        ),
                        Arrays.asList(
                                "2",
                                "2.5",
                                "3",
                                "3.5"
                        ),
                        Arrays.asList(
                                "0",
                                "0.5",
                                "1",
                                "1.5"
                        ),
                        Arrays.asList(
                                "P(A|B) = P(A ∩ B) / P(B)",
                                "P(A ∩ B) = P(A) * P(B)",
                                "P(A|B) = P(B|A) * P(A)",
                                "P(A ∩ B) = P(A') * P(B')"
                        ),
                        Arrays.asList(
                                "P(A ∪ B) = P(A) + P(B) - P(A ∩ B)",
                                "P(A ∩ B) = P(A) * P(B)",
                                "P(A ∪ B) = 1 - P(A') - P(B')",
                                "P(A ∩ B) = P(A') * P(B')"
                        ),
                        Arrays.asList(
                                "1/12",
                                "1/9",
                                "1/6",
                                "1/4"
                        ),
                        Arrays.asList(
                                "16/100",
                                "1/100",
                                "16/10000",
                                "1/10000"
                        ),
                        Arrays.asList(
                                "1/2704",
                                "1/2197",
                                "1/474552",
                                "1/17576"
                        )
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

        H2 header = new H2("Cuestionario de Estadística y Probabilidad: medidas de tendencia central y probabilidad - Nivel: " + level);
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
