package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class BoardPrint extends EscapeSequences {
    private final Session session;

    public BoardPrint(Session session) {
        this.session = session;

    }

    public void printBoard(ChessGame game) {
        ChessBoard board = game.getBoard();
        System.out.println("Here is your board"+ RESET_TEXT_COLOR);

        int startRow;
        int endRow;
        int step;
        char[] columns;


        if (session.playerColor == null || session.playerColor == WHITE) {
            System.out.println("Here is your White board");
            startRow = 8;
            endRow = 0;
            columns = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
            step = -1;
            printLabels(columns, startRow, endRow, step, board);


        } else if (session.playerColor == BLACK) {
            System.out.println("Here is your Black board");
            startRow = 1;
            endRow = 9;
            step = 1;
            columns = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
            printLabels(columns, startRow, endRow, step, board);
        }

        System.out.println();
    }

    private void printLabels(char[] columns, int startRow, int endRow, int step, ChessBoard board) {

        System.out.print("    ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        for (int row = startRow; row != endRow; row += step) {
            System.out.printf(" %2d ", row);

            for (int colIndex = 0; colIndex < 8; colIndex++) {
                int col = columns[colIndex] - 'a' + 1;
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor = isLightSquare ?  SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;
                String pieceSymbol = getSymbol(piece);

                System.out.print(bgColor + pieceSymbol + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }

            System.out.printf(" %2d\n", row);
        }

        System.out.print("    ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();


    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) return EMPTY;

        return switch (piece.getPieceType()) {
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_KING : SET_TEXT_COLOR_RED + BLACK_KING;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_QUEEN : SET_TEXT_COLOR_RED + BLACK_QUEEN;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_ROOK : SET_TEXT_COLOR_RED + BLACK_ROOK;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_BISHOP : SET_TEXT_COLOR_RED + BLACK_BISHOP;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_KNIGHT : SET_TEXT_COLOR_RED + BLACK_KNIGHT;
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_GREEN + WHITE_PAWN : SET_TEXT_COLOR_RED + BLACK_PAWN;
        };
    }
}