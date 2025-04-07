package ui;

import chess.ChessGame;

import java.util.Arrays;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class GamePlayClient {
    public GamePlayClient(String serverUrl, Session session) {
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "redrawBoard" -> redraw();
            case "leave" -> leave(params);
            case "move" -> move();
            case "resign" -> resign(params);
            case "showMoves" -> showMoves(params);
            default -> help();
        };

    }

    private String redraw() {
    }

    private String help() {
        return """
                    
                    - redraw - the chess board
                    - leave - the game
                    - move <col, row> - a piece
                    - resign - a game and game is over
                    - showMoves - highlight possible legal moves of a piece
                    - help - with possible commands
                    
                    """;
    }
}


