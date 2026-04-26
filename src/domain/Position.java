package domain;

import java.util.Objects;

/**
 * Representa una posicion inmutable en el tablero mediante fila y columna.
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public final class Position {

    private final int row;
    private final int col;

    /**
     * Crea una posicion con la fila y columna indicadas.
     *
     * @param row fila
     * @param col columna
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Devuelve la fila de esta posicion.
     *
     * @return fila
     */
    public int row() {
        return row;
    }

    /**
     * Devuelve la columna de esta posicion.
     *
     * @return columna
     */
    public int col() {
        return col;
    }

    /**
     * Calcula la posicion resultante de moverse en la direccion indicada.
     *
     * @param direction direccion del movimiento
     * @return nueva posicion
     */
    public Position move(Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        return new Position(row + direction.dr(), col + direction.dc());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position other = (Position) o;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Position[row=" + row + ", col=" + col + "]";
    }
}
