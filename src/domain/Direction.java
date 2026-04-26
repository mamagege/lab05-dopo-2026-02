package domain;

/**
 * Direcciones de movimiento posibles para el jugador.
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dr;
    private final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }

    /**
     * Devuelve el desplazamiento en filas.
     *
     * @return delta de fila
     */
    public int dr() {
        return dr;
    }

    /**
     * Devuelve el desplazamiento en columnas.
     *
     * @return delta de columna
     */
    public int dc() {
        return dc;
    }
}
