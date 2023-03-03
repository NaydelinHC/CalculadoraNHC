package com.example.calculadoranhc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_resultado = findViewById(R.id.tv_resultado);
    }

    public void calcular(View view) {
        Button boton = (Button) view;
        String textoBoton = boton.getText().toString();
        String concatenar = tv_resultado.getText().toString() + textoBoton;
        String concatenarSinCeros = Eliminar(concatenar);
        if (textoBoton.equals("=")) {
            double resultado = 0.0;
            try {
                resultado = evaluarExpresion(tv_resultado.getText().toString());
                tv_resultado.setText(Double.toString(resultado));
            } catch (Exception e) {
                tv_resultado.setText(e.toString());
            }
        } else if (textoBoton.equals("AC")) {
            tv_resultado.setText("0");
        }else if (textoBoton.equals("x")) {
            tv_resultado.setText(tv_resultado.getText().toString().substring(0, tv_resultado.getText().length() - 1));
        } else {
            tv_resultado.setText(concatenarSinCeros);
        }
    }

    public String Eliminar(String str) {
        int i = 0;
        // Encontrar el índice del primer carácter que no es "0"
        while (i < str.length() && str.charAt(i) == '0') {
            i++;
        }
        // Eliminar los ceros sobrantes al inicio de la expresión
        StringBuffer sb = new StringBuffer(str);
        sb.replace(0, i, "");
        return sb.toString();
    }

    public double evaluarExpresion(String expresion) {
        return new Object() {
            int pos = -1; // posición actual en la cadena
            char ch; // caracter actual

            // Avanza al siguiente caracter en la cadena
            void siguienteCaracter() {
                ch = (++pos < expresion.length()) ? expresion.charAt(pos) : 0;
            }

            // Avanza al siguiente caracter en la cadena si es el que se espera
            boolean consumir(char charEsperado) {
                while (ch == ' ') {
                    siguienteCaracter();
                }
                if (ch == charEsperado) {
                    siguienteCaracter();
                    return true;
                }
                return false;
            }

            // Analiza y evalúa toda la expresión
            double parsearExpresion() {
                siguienteCaracter();
                double resultado = parsearTermino();
                if (pos < expresion.length()) {
                    throw new RuntimeException("Caracteres incorrectos: " + ch);
                }
                return resultado;
            }

            // Analiza y evalúa un término en la expresión
            double parsearTermino() {
                double resultado = parsearFactor();
                double operandoAnterior = resultado;
                char operadorAnterior = 0;
                while (true) {
                    if (consumir('*')) {
                        resultado *= parsearFactor(); // multiplicación
                    } else if (consumir('/')) {
                        resultado /= parsearFactor(); // división
                    } else if (consumir('+')) {
                        operadorAnterior = '+'; // actualizamos el operador anterior
                        operandoAnterior = resultado;
                        resultado = parsearFactor();
                    } else if (consumir('-')) {
                        operadorAnterior = '-'; // actualizamos el operador anterior
                        operandoAnterior = resultado;
                        resultado = -parsearFactor();
                    } else {
                        if (operadorAnterior == '+') {
                            resultado = operandoAnterior + resultado; // sumamos el resultado al operando anterior
                        } else if (operadorAnterior == '-') {
                            resultado = operandoAnterior - resultado; // restamos el resultado al operando anterior
                        }
                        return resultado;
                    }
                }
            }

            // Analiza y evalúa un factor en la expresión
            double parsearFactor() {
                if (consumir('+')) {
                    return parsearFactor(); // operador de signo positivo
                }
                if (consumir('-')) {
                    return -parsearFactor(); // operador de signo negativo
                }
                double resultado;
                int inicioPosicion = pos;
                if (consumir('(')) { // paréntesis
                    resultado = parsearExpresion();
                    if (!consumir(')')) {
                        throw new RuntimeException("Falta cerrar el paréntesis en la posición: " + (pos-1));
                    }
                } else if (ch >= '0' && ch <= '9' || ch == '.') { // números
                    while (ch >= '0' && ch <= '9' || ch == '.') {
                        siguienteCaracter();
                    }
                    resultado = Double.parseDouble(expresion.substring(inicioPosicion, pos));
                } else if (ch >= 'a' && ch <= 'z') { // funciones
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        siguienteCaracter();
                    }
                    resultado = Double.parseDouble(expresion.substring(inicioPosicion, this.pos));
                } else {
                    throw new RuntimeException("Expresión inesperada: " + ch);
                }
                return resultado;
            }

        }.parsearExpresion();
    }
}