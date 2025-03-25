package ui;

import exception.ResponseException;
import model.UserData;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.GREEN;
import static ui.EscapeSequences.RESET;

public class PreLogInClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGEDOUT;

    public PreLogInClient(String serverUrl) {
            server = new ServerFacade(serverUrl);
            this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }


    }

    private String register(String... params) {
        if (params.length != 3) {
            return "You need to provide information like this: register <USERNAME> <PASSWORD> <EMAIL>";
        }

        try {
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            server.register(registerRequest);
            state = State.LOGEDOUT;
            visitorName = params[0];
            return String.format("You registered as %s.", visitorName);
        } catch (ResponseException e) {
            return "Server rejected registration: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }


    private String login(String... params) {
        if (params.length != 2) {
            return "You need to provide information like this: login <USERNAME> <PASSWORD>";
        }

        try {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(loginRequest);
            state = State.LOGEDIN;
            visitorName = params[0];
            return String.format("You signed in as %s.", visitorName);
        } catch (ResponseException e) {
            return "Login failed: " + e.getMessage();
        } catch (Exception e) {
            return "Could not connect to server: " + e.getMessage();
        }
    }


    public String help () {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
    }

}

