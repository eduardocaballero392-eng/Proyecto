package com.pen.proyecto;

public class Registro {
    private int id;
    private String descripcion;
    private double peso;
    private String fecha;
    private String imagen;
    private int empleadoId;

    public Registro() {
    }

    public Registro(int id, String descripcion, double peso, String fecha, String imagen, int empleadoId) {
        this.id = id;
        this.descripcion = descripcion;
        this.peso = peso;
        this.fecha = fecha;
        this.imagen = imagen;
        this.empleadoId = empleadoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }
}
