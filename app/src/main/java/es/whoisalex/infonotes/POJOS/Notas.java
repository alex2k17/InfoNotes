package es.whoisalex.infonotes.POJOS;

import java.util.Date;

/**
 * Created by Alex on 20/07/2017.
 */

public class Notas {

    private long id;
    private String nombre;
    private String localidad;
    private String categoria;
    private String date;
    private String descripcion;
    private long coordY;
    private long coordX;

    public Notas(long id, String nombre, String localidad, String categoria, String date, String descripcion, long coordY, long coordX) {
        this.id = id;
        this.nombre = nombre;
        this.localidad = localidad;
        this.categoria = categoria;
        this.date = date;
        this.descripcion = descripcion;
        this.coordY = coordY;
        this.coordX = coordX;
    }

    public Notas(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getCoordY() {
        return coordY;
    }

    public void setCoordY(long coordY) {
        this.coordY = coordY;
    }

    public long getCoordX() {
        return coordX;
    }

    public void setCoordX(long coordX) {
        this.coordX = coordX;
    }

    @Override
    public String toString() {
        return "Notas{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", localidad='" + localidad + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fecha=" + date +
                ", descripcion='" + descripcion + '\'' +
                ", coordY=" + coordY +
                ", coordX=" + coordX +
                '}';
    }
}
