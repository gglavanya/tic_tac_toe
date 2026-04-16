package com.game.tictactoe;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private static final int[][] WIN_PATTERNS = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
        {0, 4, 8}, {2, 4, 6}              // diagonals
    };

    /**
     * Start a new game session with selected symbol.
     */
    public GameState startGame(String playerSymbol) {
        GameState state = new GameState();
        state.setPlayerSymbol(playerSymbol);
        state.setComputerSymbol(playerSymbol.equals("X") ? "O" : "X");
        state.setStatus(GameState.Status.PLAYING);
        // X always goes first; if computer is X, make computer move
        state.setPlayerTurn(playerSymbol.equals("X"));

        if (!state.isPlayerTurn()) {
            makeComputerMove(state);
        }
        return state;
    }

    /**
     * Player makes a move at the given cell index (0-8).
     */
    public GameState playerMove(GameState state, int cellIndex) {
        if (state.getStatus() != GameState.Status.PLAYING) return state;
        if (!state.isPlayerTurn()) return state;
        if (!state.getBoard()[cellIndex].isEmpty()) return state;

        state.getBoard()[cellIndex] = state.getPlayerSymbol();

        int[] winLine = checkWinner(state.getBoard(), state.getPlayerSymbol());
        if (winLine != null) {
            state.setStatus(GameState.Status.PLAYER_WINS);
            state.setWinningCells(winLine);
            return state;
        }

        if (isBoardFull(state.getBoard())) {
            state.setStatus(GameState.Status.DRAW);
            return state;
        }

        state.setPlayerTurn(false);
        makeComputerMove(state);
        return state;
    }

    /**
     * Computer uses Minimax AI to choose the best move.
     */
    private void makeComputerMove(GameState state) {
        int bestMove = getBestMove(state.getBoard(), state.getComputerSymbol(), state.getPlayerSymbol());
        state.getBoard()[bestMove] = state.getComputerSymbol();

        int[] winLine = checkWinner(state.getBoard(), state.getComputerSymbol());
        if (winLine != null) {
            state.setStatus(GameState.Status.COMPUTER_WINS);
            state.setWinningCells(winLine);
            return;
        }

        if (isBoardFull(state.getBoard())) {
            state.setStatus(GameState.Status.DRAW);
            return;
        }

        state.setPlayerTurn(true);
    }

    /**
     * Minimax algorithm to find the best move for the computer.
     */
    private int getBestMove(String[] board, String computerSym, String playerSym) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i].isEmpty()) {
                board[i] = computerSym;
                int score = minimax(board, 0, false, computerSym, playerSym);
                board[i] = "";
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    private int minimax(String[] board, int depth, boolean isMaximizing, String computerSym, String playerSym) {
        if (checkWinner(board, computerSym) != null) return 10 - depth;
        if (checkWinner(board, playerSym) != null) return depth - 10;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i].isEmpty()) {
                    board[i] = computerSym;
                    best = Math.max(best, minimax(board, depth + 1, false, computerSym, playerSym));
                    board[i] = "";
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i].isEmpty()) {
                    board[i] = playerSym;
                    best = Math.min(best, minimax(board, depth + 1, true, computerSym, playerSym));
                    board[i] = "";
                }
            }
            return best;
        }
    }

    /**
     * Returns the winning line indices, or null if no winner.
     */
    private int[] checkWinner(String[] board, String symbol) {
        for (int[] pattern : WIN_PATTERNS) {
            if (board[pattern[0]].equals(symbol)
                    && board[pattern[1]].equals(symbol)
                    && board[pattern[2]].equals(symbol)) {
                return pattern;
            }
        }
        return null;
    }

    private boolean isBoardFull(String[] board) {
        for (String cell : board) {
            if (cell.isEmpty()) return false;
        }
        return true;
    }
}
