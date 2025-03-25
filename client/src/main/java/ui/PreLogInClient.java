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

    private String register(String... params) throws ResponseException{
        if (params.length >= 1) {
            try{
                RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
                server.register(registerRequest);
                state = State.LOGEDOUT;
                visitorName = String.join("-", params[0]);
                return String.format("You registered in as %s.", visitorName);
            }catch (Exception e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {

        if (params.length >= 1) {
            try{
                LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
                server.login(loginRequest);
                state = State.LOGEDIN;
                visitorName = String.join("-", params);
                return String.format("You signed in as %s.", visitorName);
            }catch (Exception e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }

        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
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

