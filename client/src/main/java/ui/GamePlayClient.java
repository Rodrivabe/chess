package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requests.JoinGameRequest;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class GamePlayClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final Session session;
    private Collection<GameData> lastGameList = new ArrayList<>();
    private BoardPrint boardPrinter;

    public GamePlayClient(String serverUrl, Session session) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.session = session;
        this.boardPrinter = new BoardPrint(session);
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

    private String showMoves(String[] params) {
        return "";
    }

    private String resign(String[] params) {
        return "";
    }

    private String move() {
        return "";
    }

    private String leave(String[] params) {
        return null;
    }

    private String redraw() {
        try{
            ChessGame currentGame = session.game;


            session.state = State.PLAYING;
            session.currentGameId = selectedGame.gameID();
            session.playerColor = color;

            boardPrinter.printBoard(selectedGame.game());

        } catch (ResponseException e) {
        return "Failed to redraw the board: " + e.getMessage();
    } catch (Exception e) {
        return "Could not connect to server: " + e.getMessage();
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


