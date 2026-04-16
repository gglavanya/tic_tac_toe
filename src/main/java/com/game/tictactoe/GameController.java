package com.game.tictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    /** Serve the main HTML page */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /** Start a new game with chosen symbol */
    @PostMapping("/api/start")
    @ResponseBody
    public ResponseEntity<GameState> startGame(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        String symbol = body.getOrDefault("symbol", "X").toUpperCase();
        if (!symbol.equals("X") && !symbol.equals("O")) {
            return ResponseEntity.badRequest().build();
        }

        GameState state = gameService.startGame(symbol);
        session.setAttribute("gameState", state);
        return ResponseEntity.ok(state);
    }

    /** Player makes a move */
    @PostMapping("/api/move")
    @ResponseBody
    public ResponseEntity<GameState> makeMove(
            @RequestBody Map<String, Integer> body,
            HttpSession session) {

        GameState state = (GameState) session.getAttribute("gameState");
        if (state == null) {
            return ResponseEntity.badRequest().build();
        }

        Integer cellIndex = body.get("cell");
        if (cellIndex == null || cellIndex < 0 || cellIndex > 8) {
            return ResponseEntity.badRequest().build();
        }

        GameState updated = gameService.playerMove(state, cellIndex);
        session.setAttribute("gameState", updated);
        return ResponseEntity.ok(updated);
    }

    /** Reset / go back to symbol selection */
    @PostMapping("/api/reset")
    @ResponseBody
    public ResponseEntity<GameState> reset(HttpSession session) {
        session.removeAttribute("gameState");
        GameState fresh = new GameState();
        return ResponseEntity.ok(fresh);
    }
}
