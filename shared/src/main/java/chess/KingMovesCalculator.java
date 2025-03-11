package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board,  ChessPosition position,  ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = new ArrayList<>();
        int [][] directions = {{1,-1}, {1,0}, {1,1}, {0,-1}, {0, 1}, {-1,-1}, {-1,0}, {-1,1}};

        ChessPosition targetPosition;
        int rowPosition = position.getRow();
        int colPosition = position.getColumn();

        ChessMove validMove;

        for(int[] direction : directions){

            int newRow = rowPosition + direction[0];
            int newColumn = colPosition + direction[1];

            if(inBounds(newRow, newColumn)){
                targetPosition = new ChessPosition(newRow, newColumn);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if(targetPiece == null){
                    validMove = new ChessMove(position, targetPosition, null);
                    moves.add(validMove);
                } else if (targetPiece.getTeamColor()!= teamColor) {
                    validMove = new ChessMove(position, targetPosition, null);
                    moves.add(validMove);
                }
            }
        }


        return moves;
    }

    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }



}
