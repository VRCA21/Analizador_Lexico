import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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

    // Lógica del Autómata / Máquina de Estados
    public Token obtenerSiguienteToken() {
        saltarEspacios();

        if (caracterActual == -1) {
            return new Token(TipoToken.FIN_DE_ARCHIVO, "EOF");
        }

        int estado = 0;
        StringBuilder lexema = new StringBuilder();

        while (true) {
            char c = (char) caracterActual;

            switch (estado) {
                case 0: // Estado inicial
                    if (Character.isLetter(c) || c == '_') {
                        estado = 1; // Transición para construir identificador
                        lexema.append(c);
                        avanzar();
                    } else {
                        // Es un símbolo distinto (ej. llaves, operadores, números sueltos)
                        String simbolo = String.valueOf(c);
                        avanzar();
                        return new Token(TipoToken.OTRO_SIMBOLO, simbolo);
                    }
                    break;

                case 1: // Estado de aceptación y construcción
                    if (caracterActual != -1 && (Character.isLetterOrDigit(c) || c == '_')) {
                        lexema.append(c);
                        avanzar(); // Seguir leyendo caracteres válidos
                    } else {
                        // Terminó la lectura del token. Validar si es palabra clave
                        String palabraFinal = lexema.toString();
                        if (palabrasClave.contains(palabraFinal)) {
                            return new Token(TipoToken.PALABRA_CLAVE, palabraFinal);
                        } else {
                            return new Token(TipoToken.IDENTIFICADOR, palabraFinal);
                        }
                    }
                    break;
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

        System.out.println("\n--------------------------------------------------");
        System.out.println("            RESULTADOS DEL ANALISIS               ");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-20s | %s%n", "LEXEMA", "TIPO DE TOKEN");
        System.out.println("--------------------------------------------------");

        do {
            token = lexer.obtenerSiguienteToken();

            // Filtramos para mostrar solo los tokens que nos interesan
            if (token.tipo == TipoToken.PALABRA_CLAVE || token.tipo == TipoToken.IDENTIFICADOR) {
                System.out.printf("%-20s | %s%n", token.lexema, token.tipo);
            }

        } while (token.tipo != TipoToken.FIN_DE_ARCHIVO);

        System.out.println("--------------------------------------------------");

        lexer.cerrar();
        teclado.close();
    }
}