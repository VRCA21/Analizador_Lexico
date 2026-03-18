import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// 1. Tipos de Token (Regresamos a Palabras Clave e Identificadores)
enum TipoToken {
    PALABRA_CLAVE,
    IDENTIFICADOR,
    OTRO_SIMBOLO,
    FIN_DE_ARCHIVO
}

// 2. Estructura del Token
class Token {
    TipoToken tipo;
    String lexema;

    public Token(TipoToken tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }
}

// 3. Analizador Léxico para Palabras Clave e Identificadores
class AnalizadorLexicoReservadas {
    private BufferedReader archivo;
    private int caracterActual;
    private Set<String> palabrasClave;

    public AnalizadorLexicoReservadas(String nombreArchivo) {
        try {
            archivo = new BufferedReader(new FileReader(nombreArchivo));
            avanzar(); // Leer el primer carácter

            // Tus 12 palabras clave
            palabrasClave = new HashSet<>(Arrays.asList(
                    "int", "float", "char", "void", "if", "else",
                    "while", "for", "return", "class", "public", "struct"
            ));

        } catch (IOException e) {
            System.err.println("Error al abrir el archivo: " + nombreArchivo);
            caracterActual = -1; // Forzar EOF si hay error
        }
    }

    private void avanzar() {
        try {
            if (archivo != null) caracterActual = archivo.read();
        } catch (IOException e) {
            caracterActual = -1;
        }
    }

    private void saltarEspacios() {
        while (caracterActual != -1 && Character.isWhitespace(caracterActual)) {
            avanzar();
        }
    }

    // Lógica del Autómata Explícito (con el estado q96)
    public Token obtenerSiguienteToken() {
        saltarEspacios();

        if (caracterActual == -1) {
            return new Token(TipoToken.FIN_DE_ARCHIVO, "EOF");
        }

        int estado = 0;
        StringBuilder lexema = new StringBuilder();

        System.out.println("\n--- Iniciando evaluación de nuevo lexema ---");

        while (true) {
            char c = (char) caracterActual;
            int estadoAnterior = estado;

            boolean esCaracterValido = (caracterActual != -1) && (Character.isLetterOrDigit(c) || c == '_');

            switch (estado) {
                case 0:
                    if (!esCaracterValido) {
                        String simbolo = String.valueOf(c);
                        System.out.printf("[q0] -- lee '%c' --> [OTRO_SIMBOLO]%n", c);
                        avanzar();
                        return new Token(TipoToken.OTRO_SIMBOLO, simbolo);
                    }

                    if (c == 'i') estado = 10;      // int, if
                    else if (c == 'f') estado = 20; // float, for
                    else if (c == 'c') estado = 30; // char, class
                    else if (c == 'v') estado = 40; // void
                    else if (c == 'e') estado = 50; // else
                    else if (c == 'w') estado = 60; // while
                    else if (c == 'r') estado = 70; // return
                    else if (c == 'p') estado = 80; // public
                    else if (c == 's') estado = 90; // struct
                    else estado = 96;              // Identificador genérico (q96)
                    break;

                // --- RAMA: i (int, if) ---
                case 10:
                    if (esCaracterValido) {
                        if (c == 'n') estado = 11;
                        else if (c == 'f') estado = 96; // 'if' completado
                        else estado = 96;
                    }
                    break;
                case 11: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break;

                // --- RAMA: f (float, for) ---
                case 20:
                    if (esCaracterValido) {
                        if (c == 'l') estado = 21;
                        else if (c == 'o') estado = 25;
                        else estado = 96;
                    }
                    break;
                case 21: if (esCaracterValido) estado = (c == 'o') ? 22 : 96; break;
                case 22: if (esCaracterValido) estado = (c == 'a') ? 23 : 96; break;
                case 23: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break;
                case 25: if (esCaracterValido) estado = (c == 'r') ? 96 : 96; break;

                // --- RAMA: c (char, class) ---
                case 30:
                    if (esCaracterValido) {
                        if (c == 'h') estado = 31;
                        else if (c == 'l') estado = 35;
                        else estado = 96;
                    }
                    break;
                case 31: if (esCaracterValido) estado = (c == 'a') ? 32 : 96; break;
                case 32: if (esCaracterValido) estado = (c == 'r') ? 96 : 96; break;
                case 35: if (esCaracterValido) estado = (c == 'a') ? 36 : 96; break;
                case 36: if (esCaracterValido) estado = (c == 's') ? 37 : 96; break;
                case 37: if (esCaracterValido) estado = (c == 's') ? 96 : 96; break;

                // --- RAMA: void ---
                case 40: if (esCaracterValido) estado = (c == 'o') ? 41 : 96; break;
                case 41: if (esCaracterValido) estado = (c == 'i') ? 42 : 96; break;
                case 42: if (esCaracterValido) estado = (c == 'd') ? 96 : 96; break;

                // --- RAMA: else ---
                case 50: if (esCaracterValido) estado = (c == 'l') ? 51 : 96; break;
                case 51: if (esCaracterValido) estado = (c == 's') ? 52 : 96; break;
                case 52: if (esCaracterValido) estado = (c == 'e') ? 96 : 96; break;

                // --- RAMA: while ---
                case 60: if (esCaracterValido) estado = (c == 'h') ? 61 : 96; break;
                case 61: if (esCaracterValido) estado = (c == 'i') ? 62 : 96; break;
                case 62: if (esCaracterValido) estado = (c == 'l') ? 63 : 96; break;
                case 63: if (esCaracterValido) estado = (c == 'e') ? 96 : 96; break;

                // --- RAMA: return ---
                case 70: if (esCaracterValido) estado = (c == 'e') ? 71 : 96; break;
                case 71: if (esCaracterValido) estado = (c == 't') ? 72 : 96; break;
                case 72: if (esCaracterValido) estado = (c == 'u') ? 73 : 96; break;
                case 73: if (esCaracterValido) estado = (c == 'r') ? 74 : 96; break;
                case 74: if (esCaracterValido) estado = (c == 'n') ? 96 : 96; break;

                // --- RAMA: public ---
                case 80: if (esCaracterValido) estado = (c == 'u') ? 81 : 96; break;
                case 81: if (esCaracterValido) estado = (c == 'b') ? 82 : 96; break;
                case 82: if (esCaracterValido) estado = (c == 'l') ? 83 : 96; break;
                case 83: if (esCaracterValido) estado = (c == 'i') ? 84 : 96; break;
                case 84: if (esCaracterValido) estado = (c == 'c') ? 96 : 96; break;

                // --- RAMA: struct ---
                case 90: if (esCaracterValido) estado = (c == 't') ? 91 : 96; break;
                case 91: if (esCaracterValido) estado = (c == 'r') ? 92 : 96; break;
                case 92: if (esCaracterValido) estado = (c == 'u') ? 93 : 96; break;
                case 93: if (esCaracterValido) estado = (c == 'c') ? 94 : 96; break;
                case 94: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break;

                // --- RAMA: Identificador genérico (q96) ---
                case 96:
                    if (esCaracterValido) estado = 96;
                    break;
            }

            // --- EJECUCIÓN DEL MOVIMIENTO ---
            if (esCaracterValido) {
                System.out.printf("[q%d] -- lee '%c' --> [q%d]%n", estadoAnterior, c, estado);
                lexema.append(c);
                avanzar();
            } else {
                String palabraFinal = lexema.toString();
                String caracterRuptura = (caracterActual == -1) ? "EOF" : "'" + (char)caracterActual + "'";
                System.out.printf("[q%d] -- lee %s --> (Fin del lexema)%n", estadoAnterior, caracterRuptura);

                if (palabrasClave.contains(palabraFinal)) {
                    System.out.println("      -> ACEPTADO: PALABRA_CLAVE (" + palabraFinal + ")");
                    return new Token(TipoToken.PALABRA_CLAVE, palabraFinal);
                } else {
                    System.out.println("      -> ACEPTADO: IDENTIFICADOR (" + palabraFinal + ")");
                    return new Token(TipoToken.IDENTIFICADOR, palabraFinal);
                }
            }
        }
    }

    public void cerrar() {
        try {
            if (archivo != null) archivo.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el archivo.");
        }
    }
}

// 4. Clase Principal
public class Main {

    // Método para generar el diagrama con Graphviz
    public static void generarImagenAutomata(List<Transicion> transiciones, String nombreArchivo) {
        StringBuilder dotFormat = new StringBuilder();
        dotFormat.append("digraph AutomataLexico {\n");
        dotFormat.append("  rankdir=LR;\n");
        dotFormat.append("  size=\"10,10\";\n"); // Para que la imagen no quede aplastada

        // El estado q96 es nuestro estado de aceptación principal para los identificadores
        dotFormat.append("  node [shape = doublecircle]; q96;\n");
        dotFormat.append("  node [shape = circle];\n");

        for (Transicion t : transiciones) {
            dotFormat.append("  ")
                    .append(t.getOrigen().getNombre())
                    .append(" -> ")
                    .append(t.getDestino().getNombre())
                    .append(" [label=\"").append(t.getSimbolo().replace("\"", "\\\"")).append("\"];\n");
        }

        dotFormat.append("}\n");

        try {
            FileWriter writer = new FileWriter(nombreArchivo + ".dot");
            writer.write(dotFormat.toString());
            writer.close();

            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tpng", nombreArchivo + ".dot", "-o", nombreArchivo + ".png");
            Process process = processBuilder.start();
            process.waitFor();

            System.out.println("\n[!] ¡Éxito! Se ha generado la imagen del autómata: " + nombreArchivo + ".png");

        } catch (IOException | InterruptedException e) {
            System.err.println("\n[X] Error al generar la imagen. ¿Instalaste Graphviz y lo agregaste al PATH?");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.print("Ingrese el nombre del archivo a analizar: ");
        String nombreArchivo = teclado.nextLine();

        AnalizadorLexicoReservadas lexer = new AnalizadorLexicoReservadas(nombreArchivo);
        Token token;

        List<Token> tokensEncontrados = new ArrayList<>();

        System.out.println("\n--------------------------------------------------");
        System.out.println("            TRAZA DEL ANALIZADOR LÉXICO           ");
        System.out.println("--------------------------------------------------");

        // 1. Análisis del archivo
        do {
            token = lexer.obtenerSiguienteToken();

            if (token.tipo == TipoToken.PALABRA_CLAVE || token.tipo == TipoToken.IDENTIFICADOR) {
                tokensEncontrados.add(token);
            }

        } while (token.tipo != TipoToken.FIN_DE_ARCHIVO);

        System.out.println("\n==================================================");
        System.out.println("                 RESUMEN FINAL                    ");
        System.out.println("==================================================");
        System.out.println("Total de tokens válidos encontrados: " + tokensEncontrados.size());

        for (int i = 0; i < tokensEncontrados.size(); i++) {
            Token t = tokensEncontrados.get(i);
            System.out.printf(" %2d. %-20s | %s%n", (i + 1), t.lexema, t.tipo);
        }
        System.out.println("==================================================");

        lexer.cerrar();
        teclado.close();

        // 2. Generación Visual del Autómata COMPLETO
        System.out.println("\nGenerando representación visual del autómata completo...");

        Estado q0 = new Estado("q0");
        Estado q96 = new Estado("q96"); // Identificador Genérico

        // Declaración de todos los estados de las 12 palabras clave
        Estado q10 = new Estado("q10"); Estado q11 = new Estado("q11");
        Estado q20 = new Estado("q20"); Estado q21 = new Estado("q21"); Estado q22 = new Estado("q22"); Estado q23 = new Estado("q23"); Estado q25 = new Estado("q25");
        Estado q30 = new Estado("q30"); Estado q31 = new Estado("q31"); Estado q32 = new Estado("q32"); Estado q35 = new Estado("q35"); Estado q36 = new Estado("q36"); Estado q37 = new Estado("q37");
        Estado q40 = new Estado("q40"); Estado q41 = new Estado("q41"); Estado q42 = new Estado("q42");
        Estado q50 = new Estado("q50"); Estado q51 = new Estado("q51"); Estado q52 = new Estado("q52");
        Estado q60 = new Estado("q60"); Estado q61 = new Estado("q61"); Estado q62 = new Estado("q62"); Estado q63 = new Estado("q63");
        Estado q70 = new Estado("q70"); Estado q71 = new Estado("q71"); Estado q72 = new Estado("q72"); Estado q73 = new Estado("q73"); Estado q74 = new Estado("q74");
        Estado q80 = new Estado("q80"); Estado q81 = new Estado("q81"); Estado q82 = new Estado("q82"); Estado q83 = new Estado("q83"); Estado q84 = new Estado("q84");
        Estado q90 = new Estado("q90"); Estado q91 = new Estado("q91"); Estado q92 = new Estado("q92"); Estado q93 = new Estado("q93"); Estado q94 = new Estado("q94");

        List<Transicion> graficas = new ArrayList<>();

        // Salidas desde q0 a las ramas principales
        graficas.add(new Transicion(q0, "i", q10));
        graficas.add(new Transicion(q0, "f", q20));
        graficas.add(new Transicion(q0, "c", q30));
        graficas.add(new Transicion(q0, "v", q40));
        graficas.add(new Transicion(q0, "e", q50));
        graficas.add(new Transicion(q0, "w", q60));
        graficas.add(new Transicion(q0, "r", q70));
        graficas.add(new Transicion(q0, "p", q80));
        graficas.add(new Transicion(q0, "s", q90));
        graficas.add(new Transicion(q0, "otras_letras", q96)); // Identificador directo

        // Rama: i (int, if)
        graficas.add(new Transicion(q10, "n", q11));
        graficas.add(new Transicion(q11, "t", q96));
        graficas.add(new Transicion(q10, "f", q96));

        // Rama: f (float, for)
        graficas.add(new Transicion(q20, "l", q21));
        graficas.add(new Transicion(q21, "o", q22));
        graficas.add(new Transicion(q22, "a", q23));
        graficas.add(new Transicion(q23, "t", q96));
        graficas.add(new Transicion(q20, "o", q25));
        graficas.add(new Transicion(q25, "r", q96));

        // Rama: c (char, class)
        graficas.add(new Transicion(q30, "h", q31));
        graficas.add(new Transicion(q31, "a", q32));
        graficas.add(new Transicion(q32, "r", q96));
        graficas.add(new Transicion(q30, "l", q35));
        graficas.add(new Transicion(q35, "a", q36));
        graficas.add(new Transicion(q36, "s", q37));
        graficas.add(new Transicion(q37, "s", q96));

        // Rama: v (void)
        graficas.add(new Transicion(q40, "o", q41));
        graficas.add(new Transicion(q41, "i", q42));
        graficas.add(new Transicion(q42, "d", q96));

        // Rama: e (else)
        graficas.add(new Transicion(q50, "l", q51));
        graficas.add(new Transicion(q51, "s", q52));
        graficas.add(new Transicion(q52, "e", q96));

        // Rama: w (while)
        graficas.add(new Transicion(q60, "h", q61));
        graficas.add(new Transicion(q61, "i", q62));
        graficas.add(new Transicion(q62, "l", q63));
        graficas.add(new Transicion(q63, "e", q96));

        // Rama: r (return)
        graficas.add(new Transicion(q70, "e", q71));
        graficas.add(new Transicion(q71, "t", q72));
        graficas.add(new Transicion(q72, "u", q73));
        graficas.add(new Transicion(q73, "r", q74));
        graficas.add(new Transicion(q74, "n", q96));

        // Rama: p (public)
        graficas.add(new Transicion(q80, "u", q81));
        graficas.add(new Transicion(q81, "b", q82));
        graficas.add(new Transicion(q82, "l", q83));
        graficas.add(new Transicion(q83, "i", q84));
        graficas.add(new Transicion(q84, "c", q96));

        // Rama: s (struct)
        graficas.add(new Transicion(q90, "t", q91));
        graficas.add(new Transicion(q91, "r", q92));
        graficas.add(new Transicion(q92, "u", q93));
        graficas.add(new Transicion(q93, "c", q94));
        graficas.add(new Transicion(q94, "t", q96));

        // El bucle infinito del Identificador Genérico
        graficas.add(new Transicion(q96, "digito, letra, _", q96));

        generarImagenAutomata(graficas, "Automata_Completo");
    }
}