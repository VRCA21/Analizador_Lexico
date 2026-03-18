import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneradorAutomata {

    public static void generarImagen(List<Transicion> transiciones, String nombreArchivo) {
        StringBuilder dotFormat = new StringBuilder();
        // Configuramos el grafo para que se dibuje de izquierda a derecha (LR)
        dotFormat.append("digraph Autómata {\n");
        dotFormat.append("  rankdir=LR;\n");

        // Coloreamos los nodos finales (doble círculo) para que se vea profesional
        dotFormat.append("  node [shape = doublecircle]; q1 q6 q7 q8 q9 q10;\n");
        dotFormat.append("  node [shape = circle];\n"); // Los demás son círculos normales

        for (Transicion t : transiciones) {
            dotFormat.append("  ")
                    .append(t.getOrigen().getNombre())
                    .append(" -> ")
                    .append(t.getDestino().getNombre())
                    // En Graphviz, algunos símbolos como < o > necesitan comillas para no romper el formato
                    .append(" [label=\"").append(t.getSimbolo().replace("\"", "\\\"")).append("\"];\n");
        }

        dotFormat.append("}\n");

        try {
            // 1. Escribir el archivo .dot
            FileWriter writer = new FileWriter(nombreArchivo + ".dot");
            writer.write(dotFormat.toString());
            writer.close();

            // 2. Ejecutar Graphviz para crear el PNG
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", nombreArchivo + ".dot", "-o", nombreArchivo + ".png");
            Process process = processBuilder.start();
            process.waitFor();

            System.out.println("¡Éxito! Imagen generada correctamente: " + nombreArchivo + ".png");

        } catch (IOException | InterruptedException e) {
            System.err.println("Error al generar la imagen. ¿Instalaste Graphviz y lo agregaste al PATH?");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Creamos todos nuestros estados
        Estado q0 = new Estado("q0");
        Estado q1 = new Estado("q1"); // Identificadores
        Estado q2 = new Estado("q2"); // <
        Estado q3 = new Estado("q3"); // >
        Estado q4 = new Estado("q4"); // =
        Estado q5 = new Estado("q5"); // !
        Estado q6 = new Estado("q6"); // <=
        Estado q7 = new Estado("q7"); // >=
        Estado q8 = new Estado("q8"); // ==
        Estado q9 = new Estado("q9"); // !=
        Estado q10 = new Estado("q10"); // +, -, *, /

        List<Transicion> transiciones = new ArrayList<>();

        // --- TRANSICIONES DESDE q0 ---
        transiciones.add(new Transicion(q0, "letra, _", q1));
        transiciones.add(new Transicion(q0, "<", q2));
        transiciones.add(new Transicion(q0, ">", q3));
        transiciones.add(new Transicion(q0, "=", q4));
        transiciones.add(new Transicion(q0, "!", q5));
        transiciones.add(new Transicion(q0, "+, -, *, /", q10));

        // --- TRANSICIONES DE IDENTIFICADORES (q1) ---
        transiciones.add(new Transicion(q1, "letra, dígito, _", q1));

        // --- TRANSICIONES DE OPERADORES RELACIONALES (q2 a q5 hacia q6 a q9) ---
        transiciones.add(new Transicion(q2, "=", q6));
        transiciones.add(new Transicion(q3, "=", q7));
        transiciones.add(new Transicion(q4, "=", q8));
        transiciones.add(new Transicion(q5, "=", q9));

        // Generamos la imagen
        generarImagen(transiciones, "MiAnalizadorLexico");
    }
}