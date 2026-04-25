package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Clase de GUI para el juego EasySokoban.
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public class SokobanGUI extends JFrame {

    private JMenuItem itemNuevo;
    private JMenuItem itemAbrir;
    private JMenuItem itemSalvar;
    private JMenuItem itemSalir;

    /**
     * Constructor para la interfaz donde se coloca el titulo.
     *
     * @param title titulo de la ventana
     */
    public SokobanGUI(String title) {
        super(title);
        prepareElements();
        prepareActions();
        this.setVisible(true);
    }

    /**
     * Configura el tamanio y la posicion inicial de la ventana.
     */
    public void prepareElements() {
        Toolkit miPantalla = Toolkit.getDefaultToolkit();
        Dimension tamanoPantalla = miPantalla.getScreenSize();

        int anchoPantalla = tamanoPantalla.width;
        int altoPantalla  = tamanoPantalla.height;

        int anchoVentana = anchoPantalla / 2;
        int altoVentana  = altoPantalla  / 2;

        this.setSize(anchoVentana, altoVentana);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        prepareElementsMenu();
    }

    /**
     * Construye y asigna la barra de menu a la ventana.
     */
    private void prepareElementsMenu() {
        JMenuBar barraMenu = new JMenuBar();
        JMenu menuArchivo  = new JMenu("Archivo");

        itemNuevo  = new JMenuItem("Nuevo");
        itemAbrir  = new JMenuItem("Abrir");
        itemSalvar = new JMenuItem("Guardar");
        itemSalir  = new JMenuItem("Salir");

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemSalvar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        barraMenu.add(menuArchivo);
        this.setJMenuBar(barraMenu);
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
        prepareActionsMenu();
    }

    /**
     * Configura los oyentes de eventos del menu.
     */
    private void prepareActionsMenu() {
        itemSalir.addActionListener(e -> confirmarSalida());
    }

    /**
     * Muestra un dialogo de confirmacion antes de cerrar la aplicacion.
     */
    public void confirmarSalida() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "Esta seguro de que desea salir de EasySokoban?",
            "Confirmacion de Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Punto de entrada principal para ejecutar la aplicacion desde consola.
     *
     * @param args argumentos de la linea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SokobanGUI("EasySokoban"));
    }
}
