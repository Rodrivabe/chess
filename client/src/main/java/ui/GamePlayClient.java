package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requests.JoinGameRequest;
import server.ServerFacade;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class GamePlayClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final ClientSession session;
    private final Collection<GameData> lastGameList = new ArrayList<>();
    private final BoardPrint boardPrinter;

    public GamePlayClient(String serverUrl, ClientSession session) {
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


    private String redraw() {
        try {
            ChessGame currentGame = session.game;
            session.state = State.PLAYING;
            boardPrinter.printBoard(currentGame);
            return "Here is the game";


        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }

    private String leave(String[] params) {

//        var leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, session.authToken, session.currentGameId);
//        session.ws.send(leaveCommand);
//        var gameList = session.lastGameList;
//        int gameNumber = session.currentGameId;
//        GameData gameToLeave = null;
//        int i = 0;
//        for(GameData game : gameList){
//            if (i == gameNumber - 1) {
//                gameToLeave = game;
//                break;
//            }
//            i++;
//        }
//        session.state = State.LOGEDIN;
//        if (session.playerColor.equals("WHITE")) {
//            gameToLeave.whiteUsername() = null;
//        } else if ("BLACK".equals(session.playerColor)) {
//            color = BLACK;
//        } else {
//            return "Invalid color. Choose WHITE or BLACK.";
//        }


        return "";
    }

    private String move() {
        return "";
    }

    private String resign(String[] params) {
        return null;
    }

    private String showMoves(String[] params) {
        return "";
    }



    public String help() {
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


