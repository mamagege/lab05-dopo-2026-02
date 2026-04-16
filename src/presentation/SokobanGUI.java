import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



/**
 * Clase de GUI (Interfaz gráfica para el juego EasySkoban
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public class SokobanGUI extends JFrame{


    /**
     * Constructor para la interfáz donde se coloca el titulo
     */
    public SokobanGUI(String title)
    {   
        //Pasa el titulo
        super(title);
        
        prepareElements();
        prepareActions();
        
    }

    public void prepareActions() {
        WindowListener oyenteCierre = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        };

        this.addWindowListener(oyenteCierre);

    }

    public void confirmarSalida() {

        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Estas 99.999999 infinito seguro?",
            "Confirmación de salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            System.out.println("Pero muuuy seguro?");
            int respuesta2 = JOptionPane.showConfirmDialog(
                this,
                "No ya enserio",
                "Confirmación de salida segurisima",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta2 == JOptionPane.YES_OPTION) {
                System.out.println("Ya te extraño :(");
                System.exit(0);
            }

            if (respuesta2 == JOptionPane.NO_OPTION) {
                System.out.println("Lo sabia mi amOoOoooooor");
            }
        }

        if (respuesta == JOptionPane.NO_OPTION) {
            System.out.println("Te la creiste puta!!!!!!!");
            System.exit(0);
        }



    }
    
    public void prepareElements(){
        
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
    
    
    public static void main (String [] args)
    {
        SokobanGUI ventana = new SokobanGUI("EasySokoban");
        ventana.setVisible(true);
    }
}
