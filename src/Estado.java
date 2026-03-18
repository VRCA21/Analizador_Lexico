import java.util.Objects;

public class Estado {
    private String nombre;

    public Estado(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Estado estado = (Estado) obj;
        return nombre.equals(estado.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}