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
@Route(value = "algebra", layout = MainLayout.class)
public class AlgebraView extends VerticalLayout implements HasUrlParameter<String> {

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

    public AlgebraView() {
        getElement().getStyle()
                .set("background-image", "url('images/Factorizar-polinomios.jpg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }

    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "6x² + 5x + 4",
                        "3x",
                        "6x² - 8x - 8",
                        "x + 2",
                        "4x",
                        "2x² + 5x - 7",
                        "(x + 3)²",
                        "2x⁴ - 8x³ + 10x² - 4x",
                        "x - 3",
                        "(2x + 3)(x - 5)"
                );
                return Arrays.asList(
                        "¿Cuál es el resultado de sumar los polinomios (2x² + 3x + 1) y (4x² + 2x + 3)?",
                        "¿Cuál es el factor común en los siguientes términos: 6x³, 9x², 12x?",
                        "¿Cuál es el resultado de multiplicar los polinomios (3x + 2) y (2x - 4)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (2x² + 4x) / 2x?",
                        "¿Cuál es el factor común en los siguientes términos: 8x² + 12xy?",
                        "¿Cuál es el resultado de restar los polinomios (3x² + 2x - 5) y (x² - 3x + 2)?",
                        "¿Cuál es el resultado de factorizar el polinomio x² + 6x + 9?",
                        "¿Cuál es el resultado de multiplicar los polinomios (2x³ - 4x² + 2x) y (x - 2)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (x² - 9) / (x + 3)?",
                        "¿Cuál es el resultado de factorizar el trinomio 2x² - 7x - 15?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "5x² + 2x - 2",
                        "3xy",
                        "12x² + 2x - 2",
                        "5x²-2x",
                        "6ab",
                        "x² - 7x + 7",
                        "(2x + 3)²",
                        "6x⁴ - 9x³ + 4x² - 8x + 3",
                        "x + 2y",
                        "(2x + 1)(x + 3)"
                );
                return Arrays.asList(
                        "¿Cuál es el resultado de sumar los polinomios (3x² - 2x + 1) y (2x² + 4x - 3)?",
                        "¿Cuál es el factor común en los siguientes términos: 9x³y, 6x²y², 12xy³?",
                        "¿Cuál es el resultado de multiplicar los polinomios (4x + 2)(3x - 1)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (5x³ - 2x²) / x?",
                        "¿Cuál es el factor común en los siguientes términos: 12a²b + 18ab²?",
                        "¿Cuál es el resultado de restar los polinomios (2x² - 3x + 5) y (x² + 4x - 2)?",
                        "¿Cuál es el resultado de factorizar el polinomio 4x² + 12x + 9?",
                        "¿Cuál es el resultado de multiplicar los polinomios (3x³ + 2x - 1) y (2x - 3)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (x² - 4y²) / (x - 2y)?",
                        "¿Cuál es el resultado de factorizar el trinomio 2x² + 7x + 3?"
                );
            case "difícil":
                correctAnswers = Arrays.asList(
                        "4x² - 12x + 9",
                        "5x³y",
                        "12x² - 5x - 2",
                        "4x³ - 2",
                        "8a²b²",
                        "5x³ - 6x² +6x + 1",
                        "(3x² - 2)²",
                        "6x⁶ - 4x⁵ + 9x² - 9x + 2",
                        "x² + y²",
                        "x(2x + 1)(3x - 4)"
                );
                return Arrays.asList(
                        "¿Cuál es el resultado de elevar al cuadrado el binomio (2x - 3)²?",
                        "¿Cuál es el factor común en los siguientes términos: 15x⁵y, 10x⁴y², 20x³y³?",
                        "¿Cuál es el resultado de multiplicar los polinomios (3x - 2)(4x + 1)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (8x⁶ - 4x³) / 2x³?",
                        "¿Cuál es el factor común en los siguientes términos: 16a³b² - 24a²b³?",
                        "¿Cuál es el resultado de restar los polinomios (5x³ - 4x² + 3x + 2) y (2x² - 3x + 1)?",
                        "¿Cuál es el resultado de factorizar el polinomio 9x⁴ - 12x² + 4?",
                        "¿Cuál es el resultado de multiplicar los polinomios (2x⁵ + 3x - 1) y (3x - 2)?",
                        "¿Cuál es el resultado de simplificar la expresión algebraica (x⁴ - y⁴) / (x² - y²)?",
                        "¿Cuál es el resultado de factorizar el trinomio 6x³ - 5x² - 4x?"
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<List<String>> getOptionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                return Arrays.asList(
                        Arrays.asList("6x² + 5x + 4", "6x² + 5x - 4", "2x² + 5x + 4"),
                        Arrays.asList("x", "3x", "6x"),
                        Arrays.asList("6x² - 8x - 8", "6x² + 2x - 8", "6x² + 10x + 8"),
                        Arrays.asList("2x + 4", "x + 2", "x² + 2x"),
                        Arrays.asList("4", "2x", "4x"),
                        Arrays.asList("2x² + 5x - 7", "2x² - x - 3", "4x² - 5x + 7"),
                        Arrays.asList("(x + 3)²", "(x - 3)²", "(x + 3)(x - 3)"),
                        Arrays.asList("2x⁴ - 8x³ + 10x² - 4x", "2x³ - 10x³ + 4x² - 20x", "2x³ + 10x³ + 4x² + 20x"),
                        Arrays.asList("x - 3", "x + 3", "x² - 3"),
                        Arrays.asList("(2x + 3)(x - 5)", "(2x - 3)(x + 5)", "(2x + 5)(x - 3)")
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList("5x² + 2x + 2", "5x² + 2x - 2", "5x² + 2x - 1"),
                        Arrays.asList("3xy", "6xy", "9xy"),
                        Arrays.asList("12x² + 2x - 2", "12x² + 6x - 3", "12x² + 2x + 3"),
                        Arrays.asList("5x² - 2x", "5x² + 2x", "5x"),
                        Arrays.asList("6ab", "12ab", "18ab"),
                        Arrays.asList("x² - 7x + 7", "x² - 7x + 7", "x² - 7x + 7"),
                        Arrays.asList("(2x + 3)²", "(2x - 3)²", "(2x + 3)(2x - 3)"),
                        Arrays.asList("6x⁴ - 5x³ - 6x² + 2x - 3", "6x⁴ + 5x³ - 6x² - 2x + 3", "6x⁴ - 9x³ + 4x² - 8x + 3"),
                        Arrays.asList("x + 2y", "x - 2y", "x² + 2xy"),
                        Arrays.asList("(2x + 1)(x + 3)", "(2x - 1)(x + 3)", "(2x + 1)(x - 3)")
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList("4x² - 12x + 9", "4x² - 12x - 9", "4x² + 12x + 9"),
                        Arrays.asList("5xy", "7x³y", "5x³y"),
                        Arrays.asList("12x² - 5x + 2", "12x² - 5x - 2", "12x² + 5x + 2"),
                        Arrays.asList("4x³ - 2", "4x³ + 2", "4x³"),
                        Arrays.asList("8ab²", "8a²b²", "24ab"),
                        Arrays.asList("3x³ - x² + 2x + 1", "3x³ - x² - 2x - 1", "5x³ - 6x² + 6x +1 "),
                        Arrays.asList("(3x² - 2)²", "(3x² + 2)²", "(3x² - 2)(3x² + 2)"),
                        Arrays.asList("6x⁶ - x⁵ - 2x⁴ - 4x³ + 7x² + 3x - 2", "6x⁶ - 4x⁵ + 9x² - 9x + 2", "6x⁶ - x⁵ + 2x⁴ - 4x³ + 7x² - 3x - 2"),
                        Arrays.asList("x² + y²", "x² - y²", "x² - 2xy + y²"),
                        Arrays.asList("(2x - 1)(3x + 2)", "(2x + 1)(3x + 2)", "x(2x + 1)(3x - 4)")
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

        H2 header = new H2("Cuestionario de Álgebra: polinomios y factorización - Nivel: " + level);
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
