package com.esmeralda.ceramicpro.model;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionVM {
    public String nombre;
    public String garantia;
    public String descripcion;
    public String incluye;
    public List<String> elementos;
    public String elements;

    public SubscriptionVM(String nombre, String garantia, String descripcion, String incluye, List<String> elementos, String elements) {
        this.nombre = nombre;
        this.garantia = garantia;
        this.descripcion = descripcion;
        this.incluye = incluye;
        this.elementos = elementos;
        this.elements = elements;
    }

    public SubscriptionVM() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIncluye() {
        return incluye;
    }

    public void setIncluye(String incluye) {
        this.incluye = incluye;
    }

    public List<String> getElementos() {
        return elementos;
    }

    public void setElementos(List<String> elementos) {
        this.elementos = elementos;
    }

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }
}
