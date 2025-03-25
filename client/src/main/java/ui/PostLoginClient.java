package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PostLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final Session session;
    private Collection<GameData> lastGameList = new ArrayList<>();


    public PostLoginClient(String serverUrl, Session session) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.session = session;





    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> playGame(params);
            case "observe" -> observeGame(params);
            default -> help();
        };
    }

    private String quit() {
        session.state = State.LOGEDOUT;
        return "You are now out of your game";
    }


    private String logout() {
        try {
            server.logout(session.authToken);
            session.state = State.LOGEDOUT;
            session.username = null;
            session.authToken = null;
            return "You have been logged out.";
        } catch (ResponseException e) {
            return "Logout failed: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }

    }
    private String createGame(String[] params) {
        if (params.length < 1) {
            return "You need to provide information like this: create <NAME>";
        }

        String gameName = String.join(" ", params); // In case the name has spaces

        try {
            CreateGameRequest request = new CreateGameRequest(gameName);
            CreateGameResult result = server.createGame(request, session.authToken);

            return String.format("Game '%s' created with ID %d.", gameName, result.gameID());
        } catch (ResponseException e) {
            return "Failed to create game: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }


    private String listGames() {
        try {
            ListGamesResult result = server.listGames(session.authToken);
            lastGameList = result.games(); // Save the list for later use

            if (lastGameList == null || lastGameList.isEmpty()) {
                return "No games found.";
            }

            StringBuilder output = new StringBuilder();
            int index = 1;

            for (GameData game : lastGameList) {
                output.append(String.format("%d. Game: \"%s\"\n", index++, game.gameName()));

                if (game.whiteUsername() != null) {
                    output.append("   White: ").append(game.whiteUsername()).append("\n");
                }
                if (game.blackUsername() != null) {
                    output.append("   Black: ").append(game.blackUsername()).append("\n");
                }
            }

            return output.toString();

        } catch (ResponseException e) {
            return "Could not list games: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }

    private String playGame(String... params) {
        if (params.length != 2) {
            return "You need to provide information like this: play <NUMBER> <WHITE|BLACK>";
        }

        try {
            int gameNumber = Integer.parseInt(params[0]);
            String colorInput = params[1].toUpperCase();

            if (gameNumber < 1 || gameNumber > lastGameList.size()) {
                return "Invalid game number. Try using 'list' to see available games.";
            }

            GameData selectedGame = null;
            int i = 0;
            for (GameData game : lastGameList) {
                if (i == gameNumber - 1) {
                    selectedGame = game;
                    break;
                }
                i++;
            }

            if (selectedGame == null) {
                return "Game not found at that number. Try using 'list' again.";
            }
            ChessGame.TeamColor color;

            if ("WHITE".equals(colorInput)) {
                color = ChessGame.TeamColor.WHITE;
            } else if ("BLACK".equals(colorInput)) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                return "Invalid color. Choose WHITE or BLACK.";
            }

            JoinGameRequest request = new JoinGameRequest(color, selectedGame.gameID());
            server.joinGame(request, session.authToken);

            session.state = State.LOGEDIN;
            session.currentGameId = selectedGame.gameID();
            session.playerColor = color;

            return String.format("Joined game '%s' as %s.", selectedGame.gameName(), color);

        } catch (ResponseException e) {
            return "Failed to join game: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }


    private String observeGame(String[] params) {
        if (params.length != 1) {
            return "You need to provide information like this: observe <NUMBER>";
        }

        try {
            int gameNumber = Integer.parseInt(params[0]);

            if (gameNumber < 1 || gameNumber > lastGameList.size()) {
                return "Invalid game number. Try using 'list' to see available games.";
            }

            GameData selectedGame = null;
            int i = 0;
            for (GameData game : lastGameList) {
                if (i == gameNumber - 1) {
                    selectedGame = game;
                    break;
                }
                i++;
            }

            if (selectedGame == null) {
                return "Game not found at that number.";
            }

            JoinGameRequest request = new JoinGameRequest(null, selectedGame.gameID());
            server.joinGame(request, session.authToken);

            session.state = State.LOGEDIN;
            session.currentGameId = selectedGame.gameID();
            session.playerColor = null;

            return String.format("Now observing game '%s'.", selectedGame.gameName());

        } catch (ResponseException e) {
            return "Failed to observe game: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }
    public String help () {
        return """
                    
                    - create <NAME> - a game
                    - list - games
                    - join <ID> [WHITE|BLACK] - a game
                    - observe ‹ID> - a game
                    - logout - when you are done
                    - help - with possible commands
                    
                    """;
    }
}
