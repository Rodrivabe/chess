package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final PostLoginClient postLoginClient;
    private final PreLogInClient preLogInClient;
    private final GamePlayClient gamePlayClient;
    private final ClientSession session = new ClientSession();

    public Repl(String serverUrl){
        postLoginClient = new PostLoginClient(serverUrl, session);
        preLogInClient = new PreLogInClient(serverUrl, session);
        gamePlayClient = new GamePlayClient(serverUrl, session);


    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to the Ben&Ben Chess Game. This is what you can do:");
        System.out.print(preLogInClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                switch (session.state){
                    case LOGEDOUT:
                        result = preLogInClient.eval(line);
                        System.out.print(BLUE + result);
                        break;
                    case LOGEDIN:
                        result = postLoginClient.eval(line);
                        System.out.print(BLUE + result);
                        break;

                }


            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

}
