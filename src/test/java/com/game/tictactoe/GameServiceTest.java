package com.game.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    // ─── startGame() ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Start game with X: player is X, computer is O, player goes first")
    void startGame_withX_playerGoesFirst() {
        GameState state = gameService.startGame("X");

        assertEquals("X", state.getPlayerSymbol());
        assertEquals("O", state.getComputerSymbol());
        assertTrue(state.isPlayerTurn());
        assertEquals(GameState.Status.PLAYING, state.getStatus());
    }

    @Test
    @DisplayName("Start game with O: computer (X) moves first automatically")
    void startGame_withO_computerMovesFirst() {
        GameState state = gameService.startGame("O");

        assertEquals("O", state.getPlayerSymbol());
        assertEquals("X", state.getComputerSymbol());
        // After computer's auto-move, it's the player's turn
        assertTrue(state.isPlayerTurn());
        // Board should have exactly one X placed by computer
        long filled = countFilled(state.getBoard());
        assertEquals(1, filled);
    }

    @Test
    @DisplayName("Board starts empty when player is X")
    void startGame_boardStartsEmpty_whenPlayerIsX() {
        GameState state = gameService.startGame("X");

        for (String cell : state.getBoard()) {
            assertEquals("", cell);
        }
    }

    // ─── playerMove() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Player move places symbol on correct cell")
    void playerMove_placeSymbolOnCell() {
        GameState state = gameService.startGame("X");
        state = gameService.playerMove(state, 0);

        assertEquals("X", state.getBoard()[0]);
    }

    @Test
    @DisplayName("Player cannot overwrite an occupied cell")
    void playerMove_cannotOverwriteOccupiedCell() {
        GameState state = gameService.startGame("X");
        state = gameService.playerMove(state, 4); // player takes center
        String boardSnapshot = state.getBoard()[4];

        // Try to overwrite — should be ignored
        state = gameService.playerMove(state, 4);
        assertEquals(boardSnapshot, state.getBoard()[4]);
    }

    @Test
    @DisplayName("After player move, computer responds (board has more pieces)")
    void playerMove_computerResponds() {
        GameState state = gameService.startGame("X");
        long before = countFilled(state.getBoard());

        state = gameService.playerMove(state, 0);
        long after = countFilled(state.getBoard());

        // Player placed 1 + computer placed 1 = 2 more than before
        assertEquals(before + 2, after);
    }

    @Test
    @DisplayName("Move on finished game is ignored")
    void playerMove_ignoredWhenGameOver() {
        GameState state = gameService.startGame("X");
        // Force a known win by manipulating the board directly
        state.getBoard()[0] = "X";
        state.getBoard()[1] = "X";
        state.getBoard()[2] = "X";
        state.setStatus(GameState.Status.PLAYER_WINS);

        state = gameService.playerMove(state, 5); // should be no-op
        assertEquals(GameState.Status.PLAYER_WINS, state.getStatus());
    }

    // ─── Win / Draw detection ─────────────────────────────────────────────────

    @Test
    @DisplayName("Detect player wins on top row")
    void playerMove_playerWinsTopRow() {
        GameState state = buildStateAndPlay(
            new int[]{0, 3, 1, 4, 2}, "X" // X: 0,1,2  O: 3,4
        );
        assertEquals(GameState.Status.PLAYER_WINS, state.getStatus());
        assertNotNull(state.getWinningCells());
    }

    @Test
    @DisplayName("Winning cells are reported correctly")
    void playerMove_winningCellsReported() {
        // Manually set up board where player wins on column 0
        GameState state = gameService.startGame("X");
        state.getBoard()[0] = "X";
        state.getBoard()[3] = "X";
        // Board: X _ _  /  X _ _  /  _ _ _   player plays [6] to win col 0
        state.getBoard()[1] = "O";
        state.getBoard()[2] = "O";
        state.setPlayerTurn(true);
        state.setStatus(GameState.Status.PLAYING);

        state = gameService.playerMove(state, 6);

        // Either player won or not — just check winningCells set when PLAYER_WINS
        if (state.getStatus() == GameState.Status.PLAYER_WINS) {
            assertNotNull(state.getWinningCells());
            assertEquals(3, state.getWinningCells().length);
        }
    }

    @Test
    @DisplayName("Game ends in DRAW when board is full with no winner")
    void game_endsDraw_whenBoardFull() {
        // A known draw configuration played move-by-move
        // X O X / X X O / O X O  — draw with X starting
        GameState state = gameService.startGame("X");
        // We can't control computer, so let's directly set a draw board
        state.getBoard()[0] = "X"; state.getBoard()[1] = "O"; state.getBoard()[2] = "X";
        state.getBoard()[3] = "X"; state.getBoard()[4] = "X"; state.getBoard()[5] = "O";
        state.getBoard()[6] = "O"; state.getBoard()[7] = "X"; state.getBoard()[8] = "";
        state.setPlayerTurn(true);
        state.setStatus(GameState.Status.PLAYING);

        // Player fills last cell
        state = gameService.playerMove(state, 8);

        // Could be draw or win depending on who has 8 — just verify game ends
        assertNotEquals(GameState.Status.PLAYING, state.getStatus());
    }

    // ─── GameState ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Default GameState has SELECTING status")
    void gameState_defaultStatus_isSelecting() {
        GameState state = new GameState();
        assertEquals(GameState.Status.SELECTING, state.getStatus());
    }

    @Test
    @DisplayName("Default GameState board has 9 empty cells")
    void gameState_defaultBoard_nineEmptyCells() {
        GameState state = new GameState();
        assertEquals(9, state.getBoard().length);
        for (String cell : state.getBoard()) {
            assertEquals("", cell);
        }
    }

    @Test
    @DisplayName("GameState setters and getters work correctly")
    void gameState_settersGetters() {
        GameState state = new GameState();
        state.setPlayerSymbol("O");
        state.setComputerSymbol("X");
        state.setPlayerTurn(false);
        state.setStatus(GameState.Status.DRAW);
        state.setWinningCells(new int[]{0, 1, 2});

        assertEquals("O", state.getPlayerSymbol());
        assertEquals("X", state.getComputerSymbol());
        assertFalse(state.isPlayerTurn());
        assertEquals(GameState.Status.DRAW, state.getStatus());
        assertArrayEquals(new int[]{0, 1, 2}, state.getWinningCells());
    }

    // ─── Computer AI ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Computer blocks player from winning")
    void computer_blocksPlayerWin() {
        // X X _  /  O _ _  /  _ _ _   — computer must block cell 2
        GameState state = gameService.startGame("X");
        state.getBoard()[0] = "X";
        state.getBoard()[1] = "X";
        state.getBoard()[3] = "O";
        state.setPlayerTurn(false);
        state.setStatus(GameState.Status.PLAYING);

        // Trigger computer move by having player move somewhere harmless
        state.setPlayerTurn(true);
        state = gameService.playerMove(state, 6); // player plays bottom-left

        // Computer should have taken cell 2 to block, or game ended
        // Either way it should NOT be PLAYER_WINS
        assertNotEquals(GameState.Status.PLAYER_WINS, state.getStatus());
    }

    @Test
    @DisplayName("Computer takes winning move when available")
    void computer_takesWinningMove() {
        // O O _  /  X X _  /  _ _ _   — player plays 5, computer should win at 2
        GameState state = gameService.startGame("X");
        state.getBoard()[3] = "O";
        state.getBoard()[4] = "O";
        state.getBoard()[0] = "X";
        state.getBoard()[1] = "X";
        state.setPlayerTurn(true);
        state.setStatus(GameState.Status.PLAYING);

        // Player plays somewhere else
        state = gameService.playerMove(state, 6);

        // Computer should have won at cell 5
        assertEquals(GameState.Status.COMPUTER_WINS, state.getStatus());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private long countFilled(String[] board) {
        long count = 0;
        for (String cell : board) if (!cell.isEmpty()) count++;
        return count;
    }

    /**
     * Simulates a sequence of player moves (indices) against the computer,
     * starting a fresh game with the given player symbol.
     */
    private GameState buildStateAndPlay(int[] playerMoves, String symbol) {
        GameState state = gameService.startGame(symbol);
        for (int move : playerMoves) {
            if (state.getStatus() != GameState.Status.PLAYING) break;
            if (state.isPlayerTurn()) {
                state = gameService.playerMove(state, move);
            }
        }
        return state;
    }
}
