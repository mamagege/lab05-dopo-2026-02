package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Clase de GUI (Interfaz gráfica para el juego EasySokoban
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public class SokobanGUI extends JFrame {

    /**
     * Constructor para la interfáz donde se coloca el titulo
     */
    public SokobanGUI(String title) {
        // Pasa el titulo
        super(title);

        prepareElements();
        prepareActions();
    }

    /**
     * Configura los oyentes de eventos de la ventana.
     */
    public void prepareActions() {
        WindowListener oyenteCierre = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        };

        this.addWindowListener(oyenteCierre);
    }

    /**
     * Muestra un diálogo de confirmación antes de cerrar la aplicación.
     */
    public void confirmarSalida() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Esta seguro de que desea salir de EasySokoban?",
            "Confirmacion de Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Configura el tamaño y posición inicial de la ventana.
     * La ventana ocupa un cuarto de la pantalla (mitad del ancho × mitad del alto)
     * y queda centrada.
     */
    public void prepareElements() {
        Toolkit miPantalla = Toolkit.getDefaultToolkit();
        Dimension tamanoPantalla = miPantalla.getScreenSize();

        int anchoPantalla = tamanoPantalla.width;
        int altoPantalla = tamanoPantalla.height;

        int anchoVentana = anchoPantalla / 2;
        int altoVentana = altoPantalla / 2;

        this.setSize(anchoVentana, altoVentana);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Punto de entrada principal para ejecutar la aplicación desde consola.
     *
     * @param args argumentos de la línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SokobanGUI ventana = new SokobanGUI("EasySokoban");
        ventana.setVisible(true);
    }
}
