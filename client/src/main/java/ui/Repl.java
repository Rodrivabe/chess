package ui;

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
        System.out.println("\uD83D\uDC36 Welcome to the Ben&Ben Chess Game. Sign in to start.");
        System.out.print(client.help());
    }
}
