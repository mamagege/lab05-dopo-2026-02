package domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Pruebas de comportamiento para la clase EasySokoban.
 * Convencion Given-When-Then (BDD).
 *
 * @author gaitan-lasso
 * @version 16/04/2026
 */
public class EasySokobanTest {

    private static final int HEIGHT = 9;
    private static final int WIDTH  = 7;

    private EasySokoban game;

    @Before
    public void setUp() {
        game = EasySokoban.createDefault();
    }

    // ── Test 1: distribucion aleatoria al 10% ────────────────────────────────

    /**
     * Given un tablero de 9x7 (63 celdas).
     * When se crea con la densidad por omision del 10%.
     * Then debe haber exactamente 6 cajas, 6 destinos y 6 paredes interiores.
     */
    @Test
    public void testDefaultBoardHasCorrectNumberOfElements() {
        int totalCells = HEIGHT * WIDTH;
        int expected   = (int) Math.floor(0.10 * totalCells);

        assertEquals("Numero de cajas debe ser 10% del area",
            expected, game.getBoxes().size());
        assertEquals("Numero de destinos debe ser 10% del area",
            expected, game.getGoals().size());

        int wallCount = 0;
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                if (game.cellTypeAt(new Position(r, c)) == CellType.WALL) {
                    wallCount++;
                }
            }
        }
        assertEquals("Numero de paredes debe ser 10% del area",
            expected, wallCount);
    }

    // ── Test 2: movimiento valido a casilla libre ────────────────────────────

    /**
     * Given un tablero con el jugador en su posicion inicial.
     * When hay al menos una direccion libre disponible.
     * Then el jugador se mueve, el contador de movimientos incrementa en 1
     *      y la posicion cambia.
     */
    @Test
    public void testPlayerMovesToFreeCell() {
        Position before = game.getPlayer();

        Direction freeDir = findFreeDirection();
        assumeNotNull("Se necesita al menos una direccion libre para este test", freeDir);

        boolean moved = game.move(freeDir);

        assertTrue("El movimiento debe retornar true", moved);
        assertEquals("El contador de movimientos debe ser 1", 1, game.getMoveCount());
        assertNotEquals("La posicion del jugador debe haber cambiado", before, game.getPlayer());
    }

    // ── Test 3: jugador bloqueado por pared ─────────────────────────────────

    /**
     * Given un tablero con al menos una pared adyacente al jugador.
     * When el jugador intenta moverse hacia esa pared.
     * Then el movimiento es rechazado y la posicion no cambia.
     */
    @Test
    public void testPlayerCannotMoveIntoWall() {
        Direction wallDir = findWallDirection();
        assumeNotNull("Se necesita una pared adyacente para este test", wallDir);

        Position before = game.getPlayer();
        boolean moved   = game.move(wallDir);

        assertFalse("Moverse hacia una pared debe retornar false", moved);
        assertEquals("El jugador no debe haberse movido", before, game.getPlayer());
        assertEquals("El contador de movimientos no debe incrementar", 0, game.getMoveCount());
    }

    // ── Test 4: dimensiones del tablero ─────────────────────────────────────

    /**
     * Given un tablero por omision.
     * When se consultan sus dimensiones.
     * Then deben ser 9 filas y 7 columnas.
     */
    @Test
    public void testDefaultBoardDimensions() {
        assertEquals("Altura debe ser 9", HEIGHT, game.getHeight());
        assertEquals("Ancho debe ser 7",  WIDTH,  game.getWidth());
    }

    // ── Test 5: numero de cajas en destino al inicio ─────────────────────────

    /**
     * Given un tablero recien generado aleatoriamente.
     * When se consulta cuantas cajas estan en su destino.
     * Then el resultado debe ser mayor o igual a 0
     *      y menor o igual al total de cajas.
     */
    @Test
    public void testBoxesOnGoalsCountIsWithinBounds() {
        int count = game.boxesOnGoalsCount();
        int total = game.getBoxes().size();
        assertTrue("Cajas en destino debe ser >= 0", count >= 0);
        assertTrue("Cajas en destino no puede superar el total", count <= total);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Direction findFreeDirection() {
        for (Direction d : Direction.values()) {
            if (game.canMove(d) && !boxInDirection(d)) {
                return d;
            }
        }
        return null;
    }

    private Direction findWallDirection() {
        Position player = game.getPlayer();
        for (Direction d : Direction.values()) {
            Position next = player.move(d);
            try {
                if (game.isWall(next)) {
                    return d;
                }
            } catch (IllegalArgumentException e) {
                // fuera del tablero, no es pared valida
            }
        }
        return null;
    }

    private boolean boxInDirection(Direction d) {
        Position next = game.getPlayer().move(d);
        return game.getBoxes().contains(next);
    }

    private void assumeNotNull(String message, Object obj) {
        org.junit.Assume.assumeTrue(message, obj != null);
    }
}
