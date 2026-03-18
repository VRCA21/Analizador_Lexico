import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// 1. Tipos de Token
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

// 3. Analizador Léxico
class AnalizadorLexicoReservadas {
    private BufferedReader archivo;
    private int caracterActual;
    private Set<String> palabrasClave;

    public AnalizadorLexicoReservadas(String nombreArchivo) {
        try {
            archivo = new BufferedReader(new FileReader(nombreArchivo));
            avanzar(); // Leer el primer carácter

            // Palabras clave comunes a validar
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

    // Lógica del Autómata / Máquina de Estados con Traza Visual
    // Lógica del Autómata Explícito para TODAS las palabras clave del proyecto
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

                    // Transiciones desde q0 para las primeras letras de TODAS tus palabras clave
                    if (c == 'i') estado = 10;      // int, if
                    else if (c == 'f') estado = 20; // float, for
                    else if (c == 'c') estado = 30; // char, class
                    else if (c == 'v') estado = 40; // void
                    else if (c == 'e') estado = 50; // else
                    else if (c == 'w') estado = 60; // while
                    else if (c == 'r') estado = 70; // return
                    else if (c == 'p') estado = 80; // public
                    else if (c == 's') estado = 90; // struct
                    else estado = 96;              // Identificador genérico (Ahora q96)
                    break;

                // --- RAMA: i (int, if) ---
                case 10:
                    if (esCaracterValido) {
                        if (c == 'n') estado = 11;
                        else if (c == 'f') estado = 96; // 'if' completado
                        else estado = 96;
                    }
                    break;
                case 11: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break; // 'int' completado

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
                case 23: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break; // 'float'
                case 25: if (esCaracterValido) estado = (c == 'r') ? 96 : 96; break; // 'for'

                // --- RAMA: c (char, class) ---
                case 30:
                    if (esCaracterValido) {
                        if (c == 'h') estado = 31;
                        else if (c == 'l') estado = 35;
                        else estado = 96;
                    }
                    break;
                case 31: if (esCaracterValido) estado = (c == 'a') ? 32 : 96; break;
                case 32: if (esCaracterValido) estado = (c == 'r') ? 96 : 96; break; // 'char'
                case 35: if (esCaracterValido) estado = (c == 'a') ? 36 : 96; break;
                case 36: if (esCaracterValido) estado = (c == 's') ? 37 : 96; break;
                case 37: if (esCaracterValido) estado = (c == 's') ? 96 : 96; break; // 'class'

                // --- RAMA: void ---
                case 40: if (esCaracterValido) estado = (c == 'o') ? 41 : 96; break;
                case 41: if (esCaracterValido) estado = (c == 'i') ? 42 : 96; break;
                case 42: if (esCaracterValido) estado = (c == 'd') ? 96 : 96; break; // 'void'

                // --- RAMA: else ---
                case 50: if (esCaracterValido) estado = (c == 'l') ? 51 : 96; break;
                case 51: if (esCaracterValido) estado = (c == 's') ? 52 : 96; break;
                case 52: if (esCaracterValido) estado = (c == 'e') ? 96 : 96; break; // 'else'

                // --- RAMA: while ---
                case 60: if (esCaracterValido) estado = (c == 'h') ? 61 : 96; break;
                case 61: if (esCaracterValido) estado = (c == 'i') ? 62 : 96; break;
                case 62: if (esCaracterValido) estado = (c == 'l') ? 63 : 96; break;
                case 63: if (esCaracterValido) estado = (c == 'e') ? 96 : 96; break; // 'while'

                // --- RAMA: return ---
                case 70: if (esCaracterValido) estado = (c == 'e') ? 71 : 96; break;
                case 71: if (esCaracterValido) estado = (c == 't') ? 72 : 96; break;
                case 72: if (esCaracterValido) estado = (c == 'u') ? 73 : 96; break;
                case 73: if (esCaracterValido) estado = (c == 'r') ? 74 : 96; break;
                case 74: if (esCaracterValido) estado = (c == 'n') ? 96 : 96; break; // 'return'

                // --- RAMA: public ---
                case 80: if (esCaracterValido) estado = (c == 'u') ? 81 : 96; break;
                case 81: if (esCaracterValido) estado = (c == 'b') ? 82 : 96; break;
                case 82: if (esCaracterValido) estado = (c == 'l') ? 83 : 96; break;
                case 83: if (esCaracterValido) estado = (c == 'i') ? 84 : 96; break;
                case 84: if (esCaracterValido) estado = (c == 'c') ? 96 : 96; break; // 'public'

                // --- RAMA: struct ---
                case 90: if (esCaracterValido) estado = (c == 't') ? 91 : 96; break;
                case 91: if (esCaracterValido) estado = (c == 'r') ? 92 : 96; break;
                case 92: if (esCaracterValido) estado = (c == 'u') ? 93 : 96; break;
                case 93: if (esCaracterValido) estado = (c == 'c') ? 94 : 96; break;
                case 94: if (esCaracterValido) estado = (c == 't') ? 96 : 96; break; // 'struct'

                // --- RAMA: Identificador genérico (q96) ---
                case 96:
                    if (esCaracterValido) estado = 96; // Bucle infinito hasta terminar la palabra
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
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // Pedir archivo de forma interactiva
        System.out.print("Ingrese el nombre del archivo a analizar: ");
        String nombreArchivo = teclado.nextLine();

        AnalizadorLexicoReservadas lexer = new AnalizadorLexicoReservadas(nombreArchivo);
        Token token;

        // Lista para guardar los tokens encontrados
        List<Token> tokensEncontrados = new ArrayList<>();

        System.out.println("\n--------------------------------------------------");
        System.out.println("            TRAZA DEL ANALIZADOR LÉXICO           ");
        System.out.println("--------------------------------------------------");

        do {
            token = lexer.obtenerSiguienteToken();

            // Filtramos para guardar y contabilizar solo los que nos interesan
            if (token.tipo == TipoToken.PALABRA_CLAVE || token.tipo == TipoToken.IDENTIFICADOR) {
                tokensEncontrados.add(token);
            }

        } while (token.tipo != TipoToken.FIN_DE_ARCHIVO);

        // Resumen Final
        System.out.println("\n==================================================");
        System.out.println("                 RESUMEN FINAL                    ");
        System.out.println("==================================================");

        System.out.println("Total de tokens válidos encontrados: " + tokensEncontrados.size());
        System.out.println("\nDesglose de tokens:");

        for (int i = 0; i < tokensEncontrados.size(); i++) {
            Token t = tokensEncontrados.get(i);
            System.out.printf(" %2d. %-20s | %s%n", (i + 1), t.lexema, t.tipo);
        }
        System.out.println("==================================================");

        lexer.cerrar();
        teclado.close();
    }
}