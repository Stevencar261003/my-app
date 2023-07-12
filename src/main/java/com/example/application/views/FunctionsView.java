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
@Route(value = "functions", layout = MainLayout.class)
public class FunctionsView extends VerticalLayout implements HasUrlParameter<String> {

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

    public FunctionsView() {
        getElement().getStyle()
                .set("background-image", "url('images/Funciones.jpg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }

    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "y = mx + b",
                        "y = ax² + bx + c",
                        "2",
                        "(1, 2)",
                        "No se puede determinar",
                        "b² - 4ac, las raíces son reales y diferentes",
                        "10",
                        "4",
                        "0",
                        "x = -b / (2a)"
                );
                return Arrays.asList(
                        "¿Cuál es la forma general de una función lineal?",
                        "¿Cuál es la forma general de una función cuadrática?",
                        "Si la ecuación de una función lineal es y = 2x + 3, ¿cuál es la pendiente de la recta?",
                        "Si la ecuación de una función cuadrática es y = x² - 4x + 3, ¿cuál es el vértice de la parábola?",
                        "¿Cuál es la pendiente de una función lineal con una gráfica vertical?",
                        "¿Cuál es la discriminante de una función cuadrática y cómo se relaciona con las raíces de la ecuación?",
                        "Si la función f(x) = 3x - 2, ¿cuál es el valor de f(4)?",
                        "Si la función g(x) = x² + 2x + 1, ¿cuál es el valor de g(-1)?",
                        "¿Cuál es la pendiente de una función lineal paralela al eje x?",
                        "¿Cuál es el eje de simetría de una función cuadrática de la forma y = ax² + bx + c?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "x = (-b ± √(b² - 4ac)) / (2a)",
                        "y = aˣ",
                        "-1",
                        "(1, 2)",
                        "m = (y₂ - y₁) / (x₂ - x₁)",
                        "Creciente",
                        "(-1, 2), (3, -4)",
                        "5",
                        "√((x₂ - x₁)² + (y₂ - y₁)²)",
                        "x ≥ 3"
                );
                return Arrays.asList(
                        "¿Cuál es la fórmula general para encontrar las raíces de una función cuadrática?",
                        "¿Cuál es la forma general de una función exponencial?",
                        "Si la función f(x) = 3x + 2, ¿cuál es el valor de f(-1)?",
                        "Si la función g(x) = 2x² - x + 3, ¿cuál es el vértice de la parábola?",
                        "¿Cuál es la fórmula para calcular la pendiente de una función lineal?",
                        "¿Cuál es el crecimiento de una función exponencial con base mayor que 1?",
                        "Si la función f(x) = x² - 4x + 3, ¿cuáles son las raíces de la ecuación?",
                        "Si la función g(x) = 2x - 1, ¿cuál es el valor de g(3)?",
                        "¿Cuál es la fórmula para calcular la distancia entre dos puntos en un plano?",
                        "¿Cuál es el dominio de la función f(x) = √(x - 3)?"
                );
            case "difícil":
                correctAnswers = Arrays.asList(
                        "y = ax³ + bx² + cx + d",
                        "2",
                        "1",
                        "(2, -1), (-1, 4), (4, 0)",
                        "m = 2ax + b",
                        "Creciente",
                        "(-3/4, -19/8)",
                        "3",
                        "∫f(x)dx",
                        "x ≠ 3"
                );
                return Arrays.asList(
                        "¿Cuál es la forma general de una función cúbica?",
                        "Si la función f(x) = 2x³ - 3x² + 4x - 1, ¿cuál es el coeficiente principal?",
                        "Si la función g(x) = |x - 3|, ¿cuál es el valor de g(2)?",
                        "Si la función h(x) = (x - 2)(x + 1)(x - 4), ¿cuáles son las raíces de la ecuación?",
                        "¿Cuál es la fórmula para calcular la pendiente de una función cuadrática en un punto?",
                        "¿Cuál es el crecimiento de una función logarítmica con base mayor que 1?",
                        "Si la función f(x) = 2x² + 3x - 5, ¿cuáles son las coordenadas del vértice de la parábola?",
                        "Si la función g(x) = log₂(x), ¿cuál es el valor de g(8)?",
                        "¿Cuál es la fórmula para calcular el área bajo la curva de una función?",
                        "¿Cuál es el dominio de la función f(x) = 1 / (x - 3)?"
                );
            default:
                return new ArrayList<>();
        }
    }

    private List<List<String>> getOptionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                return Arrays.asList(
                        Arrays.asList("y = mx + b", "y = ax² + bx + c", "y = mx² + b", "y = ax + c"),
                        Arrays.asList("y = mx + b", "y = ax² + bx + c", "y = mx² + b", "y = ax + c"),
                        Arrays.asList("2", "3", "-2", "-3"),
                        Arrays.asList("(1, 2)", "(-1, 2)", "(2, 1)", "(2, -1)"),
                        Arrays.asList("0", "1", "-1", "No se puede determinar"),
                        Arrays.asList("b² - 4ac, las raíces son reales y diferentes", "b² - 4ac, las raíces son complejas conjugadas", "b² + 4ac, las raíces son reales y diferentes", "b² + 4ac, las raíces son complejas conjugadas"),
                        Arrays.asList("10", "11", "12", "13"),
                        Arrays.asList("4", "3", "2", "1"),
                        Arrays.asList("0", "1", "-1", "No se puede determinar"),
                        Arrays.asList("x = -b / (2a)", "x = -b / (2c)", "x = -b / (2ac)", "x = -c / (2a)")
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList("x = (-b ± √(b² - 4ac)) / (2a)", "x = (-b ± √(b² + 4ac)) / (2a)", "x = (-b ± √(4ac - b²)) / (2a)", "x = (-b ± √(4ac + b²)) / (2a)"),
                        Arrays.asList("y = mx + b", "y = aˣ", "y = ax² + bx + c", "y = √(x)"),
                        Arrays.asList("-1", "1", "2", "3"),
                        Arrays.asList("(1, 2)", "(-1, 2)", "(2, -1)", "(2, 1)"),
                        Arrays.asList("m = (y₂ - y₁) / (x₂ - x₁)", "m = (x₂ - x₁) / (y₂ - y₁)", "m = (x₁ - x₂) / (y₁ - y₂)", "m = (y₁ - y₂) / (x₁ - x₂)"),
                        Arrays.asList("Creciente", "Decreciente", "Depende de la base", "No se puede determinar"),
                        Arrays.asList("(1, 2), (3, 4)", "(2, 1), (4, 3)", "(-1, 2), (3, -4)", "(-2, 1), (4, -3)"),
                        Arrays.asList("5", "4", "3", "2"),
                        Arrays.asList("√((x₂ - x₁)² + (y₂ - y₁)²)", "√((y₂ - y₁)² - (x₂ - x₁)²)", "√((y₁ - y₂)² + (x₁ - x₂)²)", "√((x₁ - x₂)² + (y₁ - y₂)²)"),
                        Arrays.asList("x ≥ 3", "x > 3", "x ≤ 3", "x < 3")
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList("y = ax³ + bx² + cx + d", "y = aˣ", "y = √(x)", "y = logₐ(x)"),
                        Arrays.asList("2", "-3", "4", "-1"),
                        Arrays.asList("1", "2", "3", "4"),
                        Arrays.asList("(2, -1), (-1, 4), (4, 0)", "(-2, 1), (1, -4), (4, 0)", "(2, 1), (-1, -4), (4, 0)", "(2, -1), (-1, -4), (4, 0)"),
                        Arrays.asList("m = 2ax + b", "m = 2ax + c", "m = 2ax + d", "m = ax² + bx + c"),
                        Arrays.asList("Creciente", "Decreciente", "Depende de la base", "No se puede determinar"),
                        Arrays.asList("(-3/4, -19/8)", "(-1/2, -9/4)", "(3/4, 19/8)", "(1/2, 9/4)"),
                        Arrays.asList("3", "4", "5", "6"),
                        Arrays.asList("∫f(x)dx", "∫f'(x)dx", "∫f''(x)dx", "∫f'''(x)dx"),
                        Arrays.asList("x ≠ 3", "x > 3", "x < 3", "x ≥ 3")
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

        H2 header = new H2("Cuestionario de Funciones: lineales y cuadráticas - Nivel: " + level);
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
