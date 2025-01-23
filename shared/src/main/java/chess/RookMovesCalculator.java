package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = new ArrayList<>();
        int [][] directions = {{0,-1}, {1,0}, {0,1}, {-1,0}};

        ChessPosition target_position;
        int row_position = position.getRow();
        int col_position = position.getColumn();

        ChessMove validMove;

        for(int[] direction : directions){
            int i;
            for(i=1; i<9; i++){
                int newRow = row_position + (direction[0]*i);
                int newColumn = col_position + (direction[1]*i);

                if(inBounds(newRow, newColumn)){
                    target_position = new ChessPosition(newRow, newColumn);
                    ChessPiece target_piece = board.getPiece(target_position);

                    if(target_piece == null){
                        validMove = new ChessMove(position, target_position, null);
                        moves.add(validMove);
                    } else if (target_piece.getTeamColor()!= teamColor) {
                        validMove = new ChessMove(position, target_position, null);
                        moves.add(validMove);
                        break;
                    } else if (target_piece.getTeamColor()== teamColor) {
                        break;
                    } else {
                        break;
                    }
                }

            }
        }


        return moves;
    }
    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }
}
