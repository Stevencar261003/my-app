package com.example.application.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@PageTitle("Questionnaire Dialog")
@Route(value = "questionnaire-dialog", layout = MainLayout.class)
public class QuestionnaireDialog extends Dialog implements HasUrlParameter<String>, RouterLayout {

    private static final int NUM_QUESTIONS = 10;

    private String questionnaire;

    public QuestionnaireDialog() {
        setWidth("400px");
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            questionnaire = parameter;
            showQuestionnaireDialog();
        } else {
            close();
            navigateToQuestionnaireListView();
        }
    }

    private void showQuestionnaireDialog() {
        // Si ya hay componentes agregados, no es necesario volver a agregarlos
        if (!getChildren().findFirst().isPresent()) {
            H2 header = new H2("Questionnaire: " + questionnaire);
            add(header);

            // Crear los componentes para las preguntas (puedes personalizar según tus necesidades)
            for (int i = 1; i <= NUM_QUESTIONS; i++) {
                Paragraph questionField = new Paragraph("Question " + i);
                add(questionField);
            }

            Button backButton = new Button("Back", event -> {
                close();
                navigateToQuestionnaireListView();
            });
            add(backButton);
        }
    }

    private void navigateToQuestionnaireListView() {
        UI.getCurrent().navigate(QuestionnaireListView.class);
    }
}
