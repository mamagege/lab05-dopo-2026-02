package presentation;

import domain.CellType;
import domain.Direction;
import domain.EasySokoban;
import domain.Position;

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

    // ── Atributos de menu ────────────────────────────────────────────────────
    private JMenuItem itemNuevo;
    private JMenuItem itemAbrir;
    private JMenuItem itemSalvar;
    private JMenuItem itemSalir;
    private JMenuItem itemColor;
    private JMenuItem itemDimension;

    // ── Atributos de vista ───────────────────────────────────────────────────
    private JPanel  panelTablero;
    private Color   colorPiezas = Color.BLUE;

    private JPanel   panelEstado;
    private JLabel   labelNivel;
    private JLabel   labelMovimientos;
    private JLabel   labelCajas;
    private JButton  botonReiniciar;

    // ── Atributos del modelo ─────────────────────────────────────────────────
    private EasySokoban juego;
    private boolean     enJuego;

    // ────────────────────────────────────────────────────────────────────────

    /**
     * Constructor para la interfaz donde se coloca el titulo.
     *
     * @param title titulo de la ventana
     */
    public SokobanGUI(String title) {
        super(title);
        juego   = EasySokoban.createDefault();
        enJuego = true;
        prepareElements();
        prepareActions();
        this.setVisible(true);
    }

    /**
     * Configura el tamanio y la posicion inicial de la ventana.
     */
    public void prepareElements() {
        Toolkit miPantalla    = Toolkit.getDefaultToolkit();
        Dimension tamPantalla = miPantalla.getScreenSize();

        int anchoVentana = tamPantalla.width  / 2;
        int altoVentana  = tamPantalla.height / 2;

        this.setSize(anchoVentana, altoVentana);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.setFocusable(true);

        prepareElementsMenu();
        prepareElementsBoard();
        prepareElementsStatus();
        refresh();
    }

    /**
     * Construye y asigna la barra de menu a la ventana.
     */
    private void prepareElementsMenu() {
        JMenuBar barraMenu = new JMenuBar();
        JMenu menuArchivo  = new JMenu("Archivo");

        itemNuevo  = new JMenuItem("Nuevo");
        itemAbrir  = new JMenuItem("Abrir");
        itemSalvar = new JMenuItem("Salvar");
        itemSalir  = new JMenuItem("Salir");

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemSalvar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuOpciones = new JMenu("Opciones");
        itemColor = new JMenuItem("Cambiar Color de Piezas");
        itemDimension = new JMenuItem("Cambiar Tamaño");
        menuOpciones.add(itemColor);
        menuOpciones.add(itemDimension);

        barraMenu.add(menuArchivo);
        barraMenu.add(menuOpciones);
        this.setJMenuBar(barraMenu);
    }

    /**
     * Inicializa el panel del tablero con dibujo personalizado del juego.
     */
    private void prepareElementsBoard() {
        panelTablero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarTablero(g);
            }
        };
        panelTablero.setBackground(Color.DARK_GRAY);
        this.getContentPane().add(panelTablero, BorderLayout.CENTER);
    }

    /**
     * Dibuja el estado actual del tablero sobre el panel.
     *
     * @param g contexto grafico
     */
    private void dibujarTablero(Graphics g) {
        int filas    = juego.getHeight();
        int columnas = juego.getWidth();
        int ancho    = panelTablero.getWidth();
        int alto     = panelTablero.getHeight();

        int celdaAncho = ancho  / columnas;
        int celdaAlto  = alto   / filas;
        int tamCelda   = Math.min(celdaAncho, celdaAlto);

        int offsetX = (ancho  - tamCelda * columnas) / 2;
        int offsetY = (alto   - tamCelda * filas)    / 2;

        Position jugadorPos = juego.getPlayer();

        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                int x = offsetX + col * tamCelda;
                int y = offsetY + fila * tamCelda;

                Position pos = new Position(fila, col);
                CellType tipo = juego.cellTypeAt(pos);

                // Fondo de la celda
                if (tipo == CellType.WALL) {
                    g.setColor(new Color(60, 60, 60));
                    g.fillRect(x, y, tamCelda, tamCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, tamCelda, tamCelda);
                } else if (tipo == CellType.GOAL) {
                    g.setColor(new Color(240, 200, 220));
                    g.fillRect(x, y, tamCelda, tamCelda);
                    g.setColor(new Color(180, 80, 120));
                    g.drawRect(x, y, tamCelda - 1, tamCelda - 1);
                } else {
                    g.setColor(new Color(220, 210, 190));
                    g.fillRect(x, y, tamCelda, tamCelda);
                    g.setColor(new Color(180, 170, 150));
                    g.drawRect(x, y, tamCelda, tamCelda);
                }

                // Caja
                if (juego.getBoxes().contains(pos)) {
                    boolean enDestino = juego.getGoals().contains(pos);
                    g.setColor(enDestino ? new Color(80, 160, 80) : new Color(180, 100, 30));
                    int margen = tamCelda / 8;
                    g.fillRoundRect(x + margen, y + margen,
                        tamCelda - 2 * margen, tamCelda - 2 * margen, 6, 6);
                    g.setColor(enDestino ? new Color(40, 100, 40) : new Color(120, 60, 10));
                    g.drawRoundRect(x + margen, y + margen,
                        tamCelda - 2 * margen, tamCelda - 2 * margen, 6, 6);
                }

                // Jugador
                if (pos.equals(jugadorPos)) {
                    g.setColor(colorPiezas);
                    int margen = tamCelda / 6;
                    g.fillOval(x + margen, y + margen,
                        tamCelda - 2 * margen, tamCelda - 2 * margen);
                    g.setColor(colorPiezas.darker());
                    g.drawOval(x + margen, y + margen,
                        tamCelda - 2 * margen, tamCelda - 2 * margen);
                }
            }
        }
    }

    /**
     * Inicializa el panel de estado inferior con nivel, movimientos, cajas y boton reiniciar.
     */
    private void prepareElementsStatus() {
        panelEstado = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        panelEstado.setBackground(new Color(50, 50, 50));

        labelNivel        = new JLabel("Nivel: 1");
        labelMovimientos  = new JLabel("Movimientos: 0");
        labelCajas        = new JLabel("Cajas: 0 / " + juego.getBoxes().size());
        botonReiniciar    = new JButton("Reiniciar");

        Font fuenteStatus = new Font("SansSerif", Font.BOLD, 13);
        labelNivel.setFont(fuenteStatus);
        labelMovimientos.setFont(fuenteStatus);
        labelCajas.setFont(fuenteStatus);

        labelNivel.setForeground(Color.WHITE);
        labelMovimientos.setForeground(Color.WHITE);
        labelCajas.setForeground(Color.WHITE);

        panelEstado.add(labelNivel);
        panelEstado.add(labelMovimientos);
        panelEstado.add(labelCajas);
        panelEstado.add(botonReiniciar);

        this.getContentPane().add(panelEstado, BorderLayout.SOUTH);
    }

    /**
     * Actualiza la vista del tablero.
     */
    public void refresh() {
        panelTablero.setBackground(Color.DARK_GRAY);
        panelTablero.revalidate();
        panelTablero.repaint();

        if (panelEstado != null) {
            labelMovimientos.setText("Movimientos: " + juego.getMoveCount());
            labelCajas.setText("Cajas: " + juego.boxesOnGoalsCount()
                + " / " + juego.getBoxes().size());
            panelEstado.repaint();
        }
    }

    /**
     * Configura los oyentes de eventos de la ventana.
     */
    public void prepareActions() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        prepareActionsMenu();
        prepareActionsBoard();
    }

    /**
     * Configura los oyentes de eventos del menu.
     */
    private void prepareActionsMenu() {
        itemNuevo.addActionListener(e -> {
            nuevoJuego();
        });

        botonReiniciar.addActionListener(e -> {
            juego.restart();
            enJuego = true;
            refresh();
            this.requestFocusInWindow();
        });

        itemSalir.addActionListener(e -> confirmarSalida());

        itemAbrir.addActionListener(e -> {
            JFileChooser selector = new JFileChooser();
            int resultado = selector.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                String nombre = selector.getSelectedFile().getName();
                JOptionPane.showMessageDialog(this,
                    "Funcion de Abrir en construccion. Archivo: " + nombre);
            }
        });

        itemSalvar.addActionListener(e -> {
            JFileChooser selector = new JFileChooser();
            int resultado = selector.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                String nombre = selector.getSelectedFile().getName();
                JOptionPane.showMessageDialog(this,
                    "Funcion de Salvar en construccion. Archivo: " + nombre);
            }
        });

        itemColor.addActionListener(e -> {
            Color seleccionado = JColorChooser.showDialog(
                this, "Seleccione un color", colorPiezas);
            if (seleccionado != null) {
                colorPiezas = seleccionado;
                refresh();
            }
        });

        itemDimension.addActionListener(e -> {
            String anchoStr = JOptionPane.showInputDialog(this, "Ingrese el ancho del tablero:");
            String altoStr = JOptionPane.showInputDialog(this, "Ingrese el alto del tablero:");

            try {
                if (anchoStr != null && altoStr != null) {
                    int ancho = Integer.parseInt(anchoStr);
                    int alto = Integer.parseInt(altoStr);
                    if (ancho > 0 && alto > 0) {
                        juego.newBoard(alto, ancho); // EasySokoban usa (height, width)
                        enJuego = true;
                        this.pack();
                        refresh();
                        this.requestFocusInWindow();
                    } else {
                        JOptionPane.showMessageDialog(this, "Las dimensiones deben ser positivas.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese números válidos.");
            }
        });
    }

    /**
     * Configura el oyente de teclado para mover al jugador.
     */
    private void prepareActionsBoard() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!enJuego) {
                    return;
                }

                Direction dir = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:    dir = Direction.UP;    break;
                    case KeyEvent.VK_DOWN:  dir = Direction.DOWN;  break;
                    case KeyEvent.VK_LEFT:  dir = Direction.LEFT;  break;
                    case KeyEvent.VK_RIGHT: dir = Direction.RIGHT; break;
                    default: break;
                }

                if (dir != null) {
                    boolean movio = juego.move(dir);
                    if (movio) {
                        refresh();
                        verificarVictoria();
                    }
                }
            }
        });

        this.requestFocusInWindow();
    }

    /**
     * Inicia un nuevo tablero aleatorio.
     */
    private void nuevoJuego() {
        juego   = EasySokoban.createDefault();
        enJuego = true;
        refresh();
        this.requestFocusInWindow();
    }

    /**
     * Verifica si el jugador gano y muestra el mensaje de victoria.
     */
    private void verificarVictoria() {
        if (juego.isSolved()) {
            enJuego = false;
            JOptionPane.showMessageDialog(this,
                "Felicitaciones! Resolviste el Sokoban en "
                    + juego.getMoveCount() + " movimientos.",
                "Victoria",
                JOptionPane.INFORMATION_MESSAGE);
        }
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
