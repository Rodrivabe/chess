package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final GamePlayClient gamePlayClient;
    private final PostLoginClient postLoginClient;
    private final PreLogInClient preLogInClient;

    public Repl(String serverUrl){
        gamePlayClient = new GamePlayClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        preLogInClient = new PreLogInClient(serverUrl);

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
                result = preLogInClient.eval(line);
                System.out.print(BLUE + result);
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
