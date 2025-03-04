import chess.*;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        UserDAO userDataAccess = new MemoryUserDAO();
        var service = new Server(userDataAccess);



        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}