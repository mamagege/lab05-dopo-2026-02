package domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Modelo principal del juego EasySokoban.
 *
 * Reglas del negocio:
 * - La bodega esta limitada por paredes en los bordes.
 * - Contiene paredes interiores, casillas vacias, jugador, cajas y destinos.
 * - El numero de destinos es igual al numero de cajas.
 * - El jugador se mueve horizontal o verticalmente a casillas vacias.
 * - El jugador puede empujar una caja a una casilla vacia adyacente.
 * - Las paredes internas, destinos y cajas se generan aleatoriamente (10% del area total cada uno).
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public class EasySokoban {

    /** Numero de filas del tablero por omision. */
    public static final int DEFAULT_HEIGHT = 9;

    /** Numero de columnas del tablero por omision. */
    public static final int DEFAULT_WIDTH = 7;

    private static final double DEFAULT_DENSITY = 0.10;
    private static final int MIN_PLACEMENT_ATTEMPTS = 1000;
    private static final int ATTEMPTS_PER_ITEM = 20;

    private int height;
    private int width;
    private CellType[][] boardBase;

    private final Set<Position> boxes = new HashSet<>();
    private final Set<Position> goals = new HashSet<>();
    private Position player;
    private int moveCount;

    /**
     * Crea un tablero de 9 x 7 con generacion aleatoria al 10%.
     *
     * @return instancia lista para jugar
     */
    public static EasySokoban createDefault() {
        EasySokoban game = new EasySokoban();
        game.newBoard(DEFAULT_HEIGHT, DEFAULT_WIDTH);
        return game;
    }

    /**
     * Crea y aleatoriza un tablero con las dimensiones indicadas.
     *
     * @param height numero de filas
     * @param width  numero de columnas
     */
    public void newBoard(int height, int width) {
        resize(height, width);
        randomize(DEFAULT_DENSITY);
    }

    /**
     * Inicializa la estructura interna del tablero.
     *
     * @param height numero de filas
     * @param width  numero de columnas
     */
    public void resize(int height, int width) {
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("Height and width must be greater than zero.");
        }
        this.height = height;
        this.width  = width;
        this.boardBase = new CellType[height][width];

        for (int row = 0; row < height; row++) {
            Arrays.fill(boardBase[row], CellType.FLOOR);
        }

        boxes.clear();
        goals.clear();
        player    = null;
        moveCount = 0;
    }

    /**
     * Puebla el tablero aleatoriamente con paredes, destinos y cajas.
     *
     * @param density proporcion de cada elemento respecto al area total
     */
    public void randomize(double density) {
        ensureBoardInitialized();
        if (density < 0.0 || density > 1.0) {
            throw new IllegalArgumentException("Density must be between 0.0 and 1.0");
        }

        int total = height * width;
        int k = (int) Math.floor(density * total);
        Random rand = new Random();

        placeWalls(rand, k);
        placeGoals(rand, k);
        placeBoxes(rand, k);
        player = randomFree(rand);
    }

    // ── Movimiento ──────────────────────────────────────────────────────────

    /**
     * Indica si el jugador puede moverse en la direccion dada.
     *
     * @param direction direccion a evaluar
     * @return true si el movimiento es valido
     */
    public boolean canMove(Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        ensurePlayerInitialized();

        Position next = player.move(direction);
        if (!isInside(next) || isWall(next)) {
            return false;
        }
        if (boxes.contains(next)) {
            Position beyond = next.move(direction);
            return isInside(beyond) && isFreeCell(beyond);
        }
        return true;
    }

    /**
     * Mueve al jugador en la direccion indicada si es posible.
     *
     * @param direction direccion del movimiento
     * @return true si el movimiento fue ejecutado, false si estaba bloqueado
     */
    public boolean move(Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");
        ensurePlayerInitialized();

        if (!canMove(direction)) {
            return false;
        }

        Position next = player.move(direction);
        if (boxes.contains(next)) {
            Position beyond = next.move(direction);
            boxes.remove(next);
            boxes.add(beyond);
        }

        player = next;
        moveCount++;
        return true;
    }

    // ── Consultas ───────────────────────────────────────────────────────────

    /**
     * Devuelve el numero de cajas que ya estan en su destino.
     *
     * @return cajas en destino
     */
    public int boxesOnGoalsCount() {
        return (int) boxes.stream().filter(goals::contains).count();
    }

    /**
     * Indica si el juego esta resuelto.
     *
     * @return true si todas las cajas estan en sus destinos
     */
    public boolean isSolved() {
        return !boxes.isEmpty() && boxesOnGoalsCount() == boxes.size();
    }

    /**
     * Devuelve el numero de filas del tablero.
     *
     * @return filas
     */
    public int getHeight() {
        return height;
    }

    /**
     * Devuelve el numero de columnas del tablero.
     *
     * @return columnas
     */
    public int getWidth() {
        return width;
    }

    /**
     * Devuelve el numero de movimientos realizados.
     *
     * @return movimientos
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Devuelve la posicion actual del jugador.
     *
     * @return posicion del jugador
     */
    public Position getPlayer() {
        return player;
    }

    /**
     * Devuelve una vista inmutable del conjunto de cajas.
     *
     * @return cajas
     */
    public Set<Position> getBoxes() {
        return Collections.unmodifiableSet(boxes);
    }

    /**
     * Devuelve una vista inmutable del conjunto de destinos.
     *
     * @return destinos
     */
    public Set<Position> getGoals() {
        return Collections.unmodifiableSet(goals);
    }

    /**
     * Devuelve el tipo de casilla en la posicion indicada.
     *
     * @param position posicion a consultar
     * @return tipo de casilla
     */
    public CellType cellTypeAt(Position position) {
        validatePosition(position);
        return boardBase[position.row()][position.col()];
    }

    /**
     * Indica si la casilla en la posicion dada es una pared.
     *
     * @param position posicion a consultar
     * @return true si es pared
     */
    public boolean isWall(Position position) {
        validatePosition(position);
        return boardBase[position.row()][position.col()] == CellType.WALL;
    }

    /**
     * Indica si la casilla esta libre (no es pared ni tiene caja).
     *
     * @param position posicion a consultar
     * @return true si esta libre
     */
    public boolean isFreeCell(Position position) {
        validatePosition(position);
        return isInside(position) && !isWall(position) && !boxes.contains(position);
    }

    // ── Helpers privados ────────────────────────────────────────────────────

    private void placeWalls(Random rand, int count) {
        placeRandom(rand, count, this::isFloorCell,
            pos -> boardBase[pos.row()][pos.col()] = CellType.WALL);
    }

    private void placeGoals(Random rand, int count) {
        placeRandom(rand, count, this::isFloorCell, pos -> {
            boardBase[pos.row()][pos.col()] = CellType.GOAL;
            goals.add(pos);
        });
    }

    private void placeBoxes(Random rand, int count) {
        validateBoxCapacity(count);
        placeRandom(rand, count, this::isInnerFreeCell, boxes::add);
    }

    private void placeRandom(Random rand, int count,
                              Predicate<Position> canPlace, Consumer<Position> action) {
        int placed  = 0;
        int attempts = 0;
        int maxAttempts = Math.max(MIN_PLACEMENT_ATTEMPTS, count * ATTEMPTS_PER_ITEM);

        while (placed < count) {
            if (attempts++ > maxAttempts) {
                throw new IllegalStateException(
                    "Unable to place all elements with the current board density.");
            }
            Position pos = randomPosition(rand);
            if (canPlace.test(pos)) {
                action.accept(pos);
                placed++;
            }
        }
    }

    private boolean isFloorCell(Position position) {
        return isInside(position)
            && boardBase[position.row()][position.col()] == CellType.FLOOR
            && !boxes.contains(position);
    }

    private boolean isInnerFreeCell(Position position) {
        return isFreeCell(position) && !isBorder(position);
    }

    private boolean isInside(Position position) {
        return position.row() >= 0 && position.row() < height
            && position.col() >= 0 && position.col() < width;
    }

    private boolean isBorder(Position position) {
        return position.row() == 0
            || position.row() == height - 1
            || position.col() == 0
            || position.col() == width  - 1;
    }

    private void validateBoxCapacity(int requestedBoxes) {
        int maxInner = Math.max(0, height - 2) * Math.max(0, width - 2);
        if (requestedBoxes > maxInner) {
            throw new IllegalStateException(
                "Cannot place " + requestedBoxes + " boxes. Max inner cells: " + maxInner);
        }
    }

    private Position randomPosition(Random rand) {
        return new Position(rand.nextInt(height), rand.nextInt(width));
    }

    private Position randomFree(Random rand) {
        Position position;
        do {
            position = randomPosition(rand);
        } while (!isFreeCell(position));
        return position;
    }

    private void ensureBoardInitialized() {
        if (boardBase == null) {
            throw new IllegalStateException("Board not initialized. Call newBoard() first.");
        }
    }

    private void ensurePlayerInitialized() {
        if (player == null) {
            throw new IllegalStateException("Player not initialized. Call newBoard() first.");
        }
    }

    private void validatePosition(Position position) {
        Objects.requireNonNull(position, "position must not be null");
        ensureBoardInitialized();
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position outside the board: " + position);
        }
    }

    @Override
    public String toString() {
        ensureBoardInitialized();
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Position pos = new Position(row, col);
                if (player != null && player.equals(pos)) {
                    sb.append('P');
                } else if (boxes.contains(pos)) {
                    sb.append(goals.contains(pos) ? '*' : 'B');
                } else {
                    switch (boardBase[row][col]) {
                        case WALL:  sb.append('#'); break;
                        case GOAL:  sb.append('G'); break;
                        default:    sb.append('.'); break;
                    }
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
