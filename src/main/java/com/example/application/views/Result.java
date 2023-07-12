package com.example.application.views;

// Clase Result para representar un resultado en la tabla
public class Result {
    private String cuestionario;
    private String nivel;
    private int puntaje;

    public Result(String questionnaire, String level, int score) {
        this.cuestionario = questionnaire;
        this.nivel = level;
        this.puntaje = score;
    }

    public String getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(String cuestionario) {
        this.cuestionario = cuestionario;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}