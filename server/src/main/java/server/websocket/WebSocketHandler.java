package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;



import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private  final Gson gSon = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final WebSocketSessionState sessionState;
    private final GameService gameService;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO,
                            GameService gameService, WebSocketSessionState sessionState){
        this.authDAO = authDAO;
        this.gameService = gameService;
        this.gameDAO = gameDAO;

        this.sessionState = sessionState;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            UserGameCommand.CommandType type = command.getCommandType();
            String username = getUsername(command.getAuthToken());

            //Retrieve game from database
            GameData game;
            int gameID = command.getGameID();
            try {
                game = gameService.getGame(gameID);
            }catch (ResponseException e){
                throw new ResponseException(e.statusCode(), e.getMessage());
            }

            //Get User's color
            ChessGame.TeamColor colorFlag = getUsersColor(username, game);

            connections.saveSession(command.getGameID(), username, session);

            switch (type) {
                case CONNECT -> connect(username, command, colorFlag, game);
                case MAKE_MOVE -> {
                    if (sessionState.gameState == GameState.GAME_OVER){
                        throw new InvalidMoveException("The game is Over");
                    }
                    if (colorFlag == null){
                        throw new InvalidMoveException("You are an observer. You can't make moves");
                    }

                    makeMove(username, command, message, colorFlag, game);
                }

                case LEAVE -> leaveGame(username, command, colorFlag, game);
                case RESIGN -> {
                    if (sessionState.gameState == GameState.GAME_OVER){
                        throw new InvalidMoveException("The game is Over");
                    }
                    if (colorFlag == null){
                        throw new InvalidMoveException("You are an observer. You can't resign a game");
                    }
                    resign(username, command, colorFlag, game);
                }
            }
        } catch (JsonSyntaxException | IOException | ResponseException | InvalidMoveException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));
        }

    }



    String getUsername(String authToken) throws ResponseException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Invalid auth token");
        }
        return auth.username();
    }

    private void connect(String username, UserGameCommand command,
                         ChessGame.TeamColor colorFlag, GameData game) throws IOException, ResponseException {

        int gameID = command.getGameID();

        //Server sends a LOAD_GAME message back to the root client.
        LoadGameMessage.sendLoadGameMessage(gSon, game, connections, username, command);
        sessionState.gameState = GameState.PLAYING;

        // Server sends a Notification message to all other clients in that game informing them the root client
        // connected to the game, either as a player (in which case their color must be specified) or as an observer.
        NotificationMessage notification = NotificationMessage.getServerMessage(username, game, command, "",
                colorFlag, null);
        sendNotification(notification, connections, gameID, username);

    }


    private void makeMove(String username, UserGameCommand command,
                          String message, ChessGame.TeamColor teamColor, GameData game)
            throws InvalidMoveException, ResponseException, IOException {

        MakeMoveCommand moveCommand = gSon.fromJson(message, MakeMoveCommand.class);
        int gameID = moveCommand.getGameID();
        sessionState.gameState = GameState.PLAYING;

        ChessGame chessGame = game.game();
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();
        if((teamColor == WHITE && teamTurn != WHITE)
                || (teamColor == BLACK && teamTurn != BLACK)){
            throw new InvalidMoveException("It's not your turn!");
        }

        //Server verifies validity of move
        chessGame.makeMove(moveCommand.getMove());

        //Game is updated to represent the move
        GameData updatedGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(),
                chessGame);

        //Game is updated in the database.
        gameDAO.updateGame(gameID, updatedGame);
        ChessGame updatedChessGame = updatedGame.game();

        //Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
        LoadGameMessage.sendLoadGameMessage(gSon, updatedGame, connections, username, command);

        //Server sends a Notification message to all other clients in that game informing them what move was made.
        NotificationMessage notification = NotificationMessage.getServerMessage(username, game,
                command, message, null, "");
        sendNotification(notification, connections, gameID, username);

        ChessGame.TeamColor opponentColor = (teamColor == WHITE) ? BLACK : WHITE;

        //If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
        if(updatedChessGame.isInCheck(opponentColor)){
            NotificationMessage notificationInCheck = NotificationMessage.getServerMessage(username, game,
                    command, message, null, "inCheck");
            sendNotification(notificationInCheck, connections, gameID, null);


        } else if (updatedChessGame.isInCheckmate(opponentColor)) {
            NotificationMessage notificationInCheckMate = NotificationMessage.getServerMessage(username, game, command,
                    message,
                    null, "inCheckMate");
            sendNotification(notificationInCheckMate, connections, gameID, null);

            sessionState.gameState = GameState.GAME_OVER;
        } else if (updatedChessGame.isInStalemate(opponentColor)) {
            NotificationMessage notificationInStaleMate = NotificationMessage.getServerMessage(username, game, command,
                    message,
                    null, "inStaleMate");

            sendNotification(notificationInStaleMate, connections, gameID, null);
            sessionState.gameState = GameState.GAME_OVER;

        }


    }



    private void leaveGame(String username, UserGameCommand command, ChessGame.TeamColor teamColor,
                           GameData game) throws ResponseException {
        int gameID = command.getGameID();
        connections.remove(gameID, username);
        GameData updatedGame = game;

        //If a player is leaving, then the game is updated to remove the root client
        if(teamColor == WHITE){
            updatedGame = new GameData(gameID, null, game.blackUsername(), game.gameName(),
                    game.game());
        } else if (teamColor == BLACK) {
            updatedGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(),
                    game.game());
        }
        //Game is updated in the database.
        gameDAO.updateGame(gameID, updatedGame);

        //Server sends a Notification message to all other clients in that game informing them that the root
        // client left. This applies to both players and observers.
        NotificationMessage leaveNotification = NotificationMessage.getServerMessage(username, updatedGame,
                command, "", teamColor, "");
        sendNotification(leaveNotification, connections, gameID, username);

    }

    private void resign(String username, UserGameCommand command, ChessGame.TeamColor teamColor, GameData game)
            throws ResponseException {
        int gameID = command.getGameID();

        //Server marks the game as over
        sessionState.gameState = GameState.GAME_OVER;
        GameData updatedGame = game;

        //Game is updated in the database.
        if(teamColor == WHITE){
            updatedGame = new GameData(gameID, null, game.blackUsername(), game.gameName(),
                    game.game());
        } else if (teamColor == BLACK) {
            updatedGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(),
                    game.game());
        }

        gameDAO.updateGame(gameID, updatedGame);

        //Server sends a Notification message to all clients in that game informing them that the root client resigned.
        // This applies to both players and observers.
        NotificationMessage resignNotification = NotificationMessage.getServerMessage(username, game,
                command, "", teamColor, "");
        sendNotification(resignNotification, connections, gameID, null);
    }

    private ChessGame.TeamColor getUsersColor(String username, GameData game) {
        ChessGame.TeamColor playerColor = null;
        if(Objects.equals(game.whiteUsername(), username)){
            playerColor = WHITE;
        }if (Objects.equals(game.blackUsername(), username)) {
            playerColor = BLACK;
        }
        return playerColor;
    }


    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        String json = gSon.toJson(message);
        remote.sendString(json);
    }

    public void sendNotification(NotificationMessage notification, ConnectionManager connections, int gameID, String username){
        String notificationJsonInMate = gSon.toJson(notification);
        connections.broadcast(gameID, notificationJsonInMate, username);
    }
}