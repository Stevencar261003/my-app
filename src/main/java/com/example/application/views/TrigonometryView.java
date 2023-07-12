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
@Route(value = "trigonometry", layout = MainLayout.class)
public class TrigonometryView extends VerticalLayout implements HasUrlParameter<String> {

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

    public TrigonometryView() {
        getElement().getStyle()
                .set("background-image", "url('images/Trigonometry.jpeg')")
                .set("background-color", "rgba(255, 255, 255, 0.5)") // Color blanco semitransparente
                .set("background-blend-mode", "overlay")
                .set("filter", "saturate(2)");
    }

    private List<String> getQuestionsForLevel(String level) {
        switch (level.toLowerCase()) {
            case "fácil":
                correctAnswers = Arrays.asList(
                        "sen²θ + cos²θ = 1",
                        "sen²θ = 1 - cos²θ",
                        "cos²θ = 1 - sen²θ",
                        "tan²θ = sen²θ + cos²θ",
                        "sen(α + β) = senα × cosβ + cosα × senβ",
                        "sen(α - β) = senα × cosβ - cosα × senβ",
                        "cos(α + β) = cosα × cosβ - senα × senβ",
                        "sen(α - β) = senα × cosβ - cosα × senβ",
                        "tan(α + β) = (tanα + tanβ) / (1 - tanα × tanβ)",
                        "tan(α - β) = (tanα - tanβ) / (1 + tanα × tanβ)"
                );
                return Arrays.asList(
                        "¿Cuál es la identidad trigonométrica fundamental?",
                        "¿Cuál es la identidad trigonométrica del seno al cuadrado?",
                        "¿Cuál es la identidad trigonométrica del coseno al cuadrado?",
                        "¿Cuál es la identidad trigonométrica de la tangente al cuadrado?",
                        "¿Cuál es la identidad trigonométrica de la suma de dos ángulos?",
                        "¿Cuál es la identidad trigonométrica de la diferencia de dos ángulos?",
                        "¿Cuál es la identidad trigonométrica del coseno de la suma de dos ángulos?",
                        "¿Cuál es la identidad trigonométrica del seno de la diferencia de dos ángulos?",
                        "¿Cuál es la identidad trigonométrica de la tangente de la suma de dos ángulos?",
                        "¿Cuál es la identidad trigonométrica de la tangente de la diferencia de dos ángulos?"
                );
            case "medio":
                correctAnswers = Arrays.asList(
                        "1",
                        "1/cosθ",
                        "(tanθ + tanφ) / (1 - tanθ * tanφ)",
                        "1/sinθ",
                        "2cos²θ - 1",
                        "-1/tanθ",
                        "-senθ",
                        "-cotθ",
                        "cosθ",
                        "secθ * secφ"
                );
                return Arrays.asList(
                        "¿Cuál es la identidad trigonométrica fundamental sen²θ + cos²θ?",
                        "¿Cuál es la identidad trigonométrica secθ?",
                        "¿Cuál es la identidad trigonométrica tan(θ + φ)?",
                        "¿Cuál es la identidad trigonométrica csc(π/2 - θ)?",
                        "¿Cuál es la identidad trigonométrica cos(2θ)?",
                        "¿Cuál es la identidad trigonométrica cot(π - θ)?",
                        "¿Cuál es la identidad trigonométrica sen(-θ)?",
                        "¿Cuál es la identidad trigonométrica tan(π/2 + θ)?",
                        "¿Cuál es la identidad trigonométrica cos(-θ)?",
                        "¿Cuál es la identidad trigonométrica sec(θ - φ)?"
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
                        "¿Cuál es la identidad trigonométrica para el ángulo doble del seno?",
                        "¿Cuál es la identidad trigonométrica para el ángulo medio del coseno?",
                        "¿Cuál es la identidad trigonométrica para el ángulo complementario de la tangente?",
                        "¿Cuál es la identidad trigonométrica para el ángulo suplementario del secante?",
                        "¿Cuál es la identidad trigonométrica para el ángulo opuesto de la cosecante?",
                        "Demuestra la identidad trigonométrica: cos^2(θ) - sen^2(θ) = 1",
                        "Resuelve la siguiente ecuación trigonométrica para θ: 2cos(2θ) - 1 = 0",
                        "Demuestra la identidad trigonométrica: tan(θ - φ) = (tan(θ) - tan(φ)) / (1 + tan(θ)tan(φ))",
                        "Resuelve la siguiente ecuación trigonométrica para θ: sec(π/2 + θ) = -1",
                        "Demuestra la identidad trigonométrica: csc(θ + φ) = csc(θ)csc(φ) - cot(θ)cot(φ)"
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
                                "sen²θ + cos²θ = 1",
                                "senθ = cosθ",
                                "senθ = 1 / cosθ"
                        ),
                        Arrays.asList(
                                "sen²θ = 1 - cos²θ",
                                "sen²θ = cos²θ",
                                "sen²θ = 1 / cos²θ"
                        ),
                        Arrays.asList(
                                "cos²θ = 1 - sen²θ",
                                "cos²θ = sen²θ",
                                "cos²θ = 1 / sen²θ"
                        ),
                        Arrays.asList(
                                "tan²θ = sen²θ + cos²θ",
                                "tan²θ = sen²θ",
                                "tan²θ = 1 / sen²θ"
                        ),
                        Arrays.asList(
                                "sen(α + β) = senα × cosβ + cosα × senβ",
                                "sen(α + β) = senα × cosβ - cosα × senβ",
                                "sen(α + β) = senα + senβ"
                        ),
                        Arrays.asList(
                                "sen(α - β) = senα × cosβ + cosα × senβ",
                                "sen(α - β) = senα × cosβ - cosα × senβ",
                                "sen(α - β) = senα - senβ"
                        ),
                        Arrays.asList(
                                "cos(α + β) = cosα × cosβ - senα × senβ",
                                "cos(α + β) = cosα × cosβ + senα × senβ",
                                "cos(α + β) = cosα + cosβ"
                        ),
                        Arrays.asList(
                                "sen(α - β) = senα × cosβ + cosα × senβ",
                                "sen(α - β) = senα × cosβ - cosα × senβ",
                                "sen(α - β) = senα - senβ"
                        ),
                        Arrays.asList(
                                "tan(α + β) = (tanα + tanβ) / (1 - tanα × tanβ)",
                                "tan(α + β) = (tanα - tanβ) / (1 + tanα × tanβ)",
                                "tan(α + β) = tanα + tanβ"
                        ),
                        Arrays.asList(
                                "tan(α - β) = (tanα + tanβ) / (1 - tanα × tanβ)",
                                "tan(α - β) = (tanα - tanβ) / (1 + tanα × tanβ)",
                                "tan(α - β) = tanα - tanβ"
                        )
                );
            case "medio":
                return Arrays.asList(
                        Arrays.asList("1", "0", "tanθ"),
                        Arrays.asList("1", "1/cosθ", "sinθ"),
                        Arrays.asList("(tanθ + tanφ) / (1 - tanθ * tanφ)", "secθ", "cscθ"),
                        Arrays.asList("1/sinθ", "secθ", "cscθ"),
                        Arrays.asList("2cos²θ - 1", "sin²θ - cos²θ", "1 - 2sin²θ"),
                        Arrays.asList("-1/tanθ", "cotθ", "-tanθ"),
                        Arrays.asList("-senθ", "cosθ", "tanθ"),
                        Arrays.asList("-cotθ", "tanθ", "secθ"),
                        Arrays.asList("cosθ", "1/cosθ", "senθ"),
                        Arrays.asList("secθ * secφ", "tanθ * tanφ", "cotθ * cotφ")
                );
            case "difícil":
                return Arrays.asList(
                        Arrays.asList(
                                " 2sin(θ)cos(θ)",
                                " sin(θ)cos(θ)",
                                " sin(2θ)",
                                " cos(2θ)"
                        ),
                        Arrays.asList(
                                " -sin(θ)",
                                " sin(2θ)",
                                " cos(θ)",
                                " cos(2θ)"
                        ),
                        Arrays.asList(
                                " cot(θ)",
                                " tan(θ)",
                                " sec(θ)",
                                " csc(θ)"
                        ),
                        Arrays.asList(
                                " sec(θ)",
                                " csc(θ)",
                                " tan(θ)",
                                " cot(θ)"
                        ),
                        Arrays.asList(
                                " -csc(θ)",
                                " sec(θ)",
                                " cot(θ)",
                                " tan(θ)"
                        ),
                        Arrays.asList(
                                " cos^2(θ) - sen^2(θ) = 1",
                                " sen^2(θ) - cos^2(θ) = 1",
                                " tan^2(θ) + 1 = sec^2(θ)",
                                " cot^2(θ) + 1 = csc^2(θ)"
                        ),
                        Arrays.asList(
                                " θ = π/4",
                                " θ = π/8",
                                " θ = π/2",
                                " θ = π"
                        ),
                        Arrays.asList(
                                " tan(θ - φ) = (tan(θ) - tan(φ)) / (1 + tan(θ)tan(φ))",
                                " tan(θ - φ) = (tan(θ) + tan(φ)) / (1 - tan(θ)tan(φ))",
                                " tan(θ - φ) = (tan(θ) - tan(φ)) / (1 - tan(θ)tan(φ))",
                                " tan(θ - φ) = (tan(θ) + tan(φ)) / (1 + tan(θ)tan(φ))"
                        ),
                        Arrays.asList(
                                " θ = π/4",
                                " θ = π/3",
                                " θ = 3π/4",
                                " θ = 2π/3"
                        ),
                        Arrays.asList(
                                " csc(θ)csc(φ) - cot(θ)cot(φ)",
                                " csc(θ)csc(φ) + cot(θ)cot(φ)",
                                " csc(θ)csc(φ) - tan(θ)tan(φ)",
                                " sec(θ)sec(φ) - cot(θ)cot(φ)"
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

        H2 header = new H2("Cuestionario de Trigonometría: identidades trigonométricas - Nivel: " + level);
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
