package com.game.tictactoe;

public class GameState {

    public enum Status {
        SELECTING,     // Player is selecting symbol
        PLAYING,       // Game in progress
        PLAYER_WINS,   // Player won
        COMPUTER_WINS, // Computer won
        DRAW           // Draw
    }

    private String[] board;         // 9 cells: "", "X", or "O"
    private String playerSymbol;    // "X" or "O"
    private String computerSymbol;  // "X" or "O"
    private boolean playerTurn;     // true = player's turn
    private Status status;
    private int[] winningCells;     // indices of winning line (null if no winner yet)

    public GameState() {
        this.board = new String[]{"","","","","","","","",""};
        this.status = Status.SELECTING;
        this.playerTurn = true;
        this.winningCells = null;
    }

    // --- Getters & Setters ---

    public String[] getBoard() { return board; }
    public void setBoard(String[] board) { this.board = board; }

    public String getPlayerSymbol() { return playerSymbol; }
    public void setPlayerSymbol(String playerSymbol) { this.playerSymbol = playerSymbol; }

    public String getComputerSymbol() { return computerSymbol; }
    public void setComputerSymbol(String computerSymbol) { this.computerSymbol = computerSymbol; }

    public boolean isPlayerTurn() { return playerTurn; }
    public void setPlayerTurn(boolean playerTurn) { this.playerTurn = playerTurn; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int[] getWinningCells() { return winningCells; }
    public void setWinningCells(int[] winningCells) { this.winningCells = winningCells; }
}
