package es.whoisalex.infonotes.POJOS;

/**
 * Created by Alex on 20/07/2017.
 */

public class Notas {

    private long idNota;
    private String nombre;
    private String localidad;
    private String categoria;
    private String fecha;
    private String descripcion;
    private double coordY;
    private double coordX;

    public Notas(long idNota, String nombre, String localidad, String categoria, String fecha, String descripcion, long coordY, long coordX) {
        this.idNota = idNota;
        this.nombre = nombre;
        this.localidad = localidad;
        this.categoria = categoria;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.coordY = coordY;
        this.coordX = coordX;
    }

    public Notas(){}

    public long getId() {
        return idNota;
    }

    public void setId(long idNota) {this.idNota = idNota;}

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

    public void setCategoria(String categoria) {this.categoria = categoria;}

    public String getDate() {
        return fecha;
    }

    public void setDate(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    @Override
    public String toString() {
        return "Notas{" +
                "id=" + idNota +
                ", nombre='" + nombre + '\'' +
                ", localidad='" + localidad + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", coordY=" + coordY +
                ", coordX=" + coordX +
                '}';
    }
}
