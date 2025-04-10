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
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;



import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final WebSocketSessionState sessionState;
    private final GameService gameService;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, GameService gameService, WebSocketSessionState sessionState){
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
            String colorFlag = getUsersColor(username, game);

            connections.saveSession(command.getGameID(), username, session);

            switch (type) {
                case CONNECT -> connect(username, session, command, colorFlag, game);
                case MAKE_MOVE -> {
                    if (sessionState.gameState == GameState.GAME_OVER){
                        throw new InvalidMoveException("The game is Over");
                    }
                    if(Objects.equals(colorFlag, "OBSERVER")){
                        throw new InvalidMoveException("You are an observer. You can't make moves");
                    }

                    makeMove(username, command, message, colorFlag, game);
                }

                case LEAVE -> leaveGame(username, command, colorFlag);
                case RESIGN -> resign(username, command, colorFlag);
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

    private void connect(String username, Session session, UserGameCommand command, String colorFlag, GameData game) throws IOException, ResponseException {
        int gameID = command.getGameID();

        LoadGameMessage.sendLoadGameMessage(gson, game, connections, username, command, colorFlag);
        sessionState.gameState = GameState.PLAYING;

        ServerMessage notification = NotificationMessage.getServerMessage(username, game, command, "", colorFlag);
        String notificationJson = gson.toJson(notification);

        connections.broadcast(gameID, notificationJson, username);
    }


    private void makeMove(String username, UserGameCommand command, String message, String colorFlag, GameData game)
            throws InvalidMoveException, ResponseException, IOException {

        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
        int gameID = moveCommand.getGameID();
        sessionState.gameState = GameState.PLAYING;

        ChessGame chessGame = game.game();
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();
        if((Objects.equals(colorFlag, "WHITE") && teamTurn != WHITE)
                || (Objects.equals(colorFlag, "BLACK") && teamTurn != BLACK)){
            throw new InvalidMoveException("It's not your turn!");
        }

        //Server verifies validity of move
        chessGame.makeMove(moveCommand.getMove());


        GameData updatedGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(),
                chessGame);

        gameDAO.updateGame(gameID, updatedGame);
        ChessGame updatedChessGame = updatedGame.game();


        LoadGameMessage.sendLoadGameMessage(gson, updatedGame, connections, username, command, "");

        ServerMessage notification = NotificationMessage.getServerMessage(username, game, command, message, "");
        String notificationJson = gson.toJson(notification);
        connections.broadcast(gameID, notificationJson, username);

        if(updatedChessGame.isInCheck(teamTurn)){
            ServerMessage notification_inCheck = NotificationMessage.getServerMessage(username, game, command, message,
                    "inCheck");
            String notificationJsonInCheck = gson.toJson(notification_inCheck);
            connections.broadcast(gameID, notificationJsonInCheck, username);

        } else if (updatedChessGame.isInCheckmate(teamTurn)) {
            ServerMessage notification_inCheckMate = NotificationMessage.getServerMessage(username, game, command,
                    message,
                    "inCheckMate");
            String notificationJsonInMate = gson.toJson(notification_inCheckMate);
            connections.broadcast(gameID, notificationJsonInMate, username);
            sessionState.gameState = GameState.GAME_OVER;
        } else if (updatedChessGame.isInStalemate(teamTurn)) {
            ServerMessage notification_inStaleMate = NotificationMessage.getServerMessage(username, game, command,
                    message,
                    "inStaleMate");
            String notificationJsonInStale = gson.toJson(notification_inStaleMate);
            connections.broadcast(gameID, notificationJsonInStale, username);
            sessionState.gameState = GameState.GAME_OVER;

        }






    }

    private String getUsersColor(String username, GameData game) {
        String color = "";
        if(Objects.equals(game.whiteUsername(), username)){
            color = "WHITE";
        } else if (Objects.equals(game.blackUsername(), username)) {
            color = "BLACK";
        }else if(!Objects.equals(game.blackUsername(), username) && !Objects.equals(game.whiteUsername(), username)){
            color = "OBSERVER";
        }
        return color;
    }


    private void leaveGame(String username, UserGameCommand command, String colorFlag) {
    }


    private void resign(String username, UserGameCommand command, String colorFlag) {
    }



    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        remote.sendString(json);
    }
}