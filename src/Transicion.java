public class Transicion {
    private Estado origen;
    private String simbolo;
    private Estado destino;

    public Transicion(Estado origen, String simbolo, Estado destino) {
        this.origen = origen;
        this.simbolo = simbolo;
        this.destino = destino;
    }

    public Estado getOrigen() { return origen; }
    public String getSimbolo() { return simbolo; }
    public Estado getDestino() { return destino; }
}