package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> endPositions = new ArrayList<>();

        int direction;

        if (teamColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;}

        /** Move types
         * 1 = move forwards
         * 2 = first move
         * **/

        if (can_move_forward(board, position, direction)) {
             add_valid_positions(1, position, direction, endPositions);
            if (is_first_move(position, teamColor) && can_move_forward(board, position, direction*2)) {
                add_valid_positions(2, position, direction, endPositions);
            }
        }
        can_capture_enemy(board, position, direction, teamColor, endPositions);

        destination_is_promotion_and_add_moves(position, endPositions, teamColor, moves);

        return moves;
    }

    private void destination_is_promotion_and_add_moves(ChessPosition startPosition, Collection<ChessPosition> endPositions, ChessGame.TeamColor teamColor, Collection<ChessMove> moves) {
        for(ChessPosition endPosition: endPositions){
            int endPositionRow = endPosition.getRow();
            if (endPositionRow == 8 && teamColor == ChessGame.TeamColor.WHITE || endPositionRow == 1 && teamColor == ChessGame.TeamColor.BLACK) {
                ChessPiece.PieceType [] pieceTypes = {
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.ROOK};
                for(ChessPiece.PieceType pieceType: pieceTypes){
                    moves.add(new ChessMove(startPosition, endPosition, pieceType));
                }
            }
            else{
                moves.add(new ChessMove(startPosition, endPosition, null));
            }
        }


    }

    private void can_capture_enemy(ChessBoard board, ChessPosition position, int direction, ChessGame.TeamColor teamColor, Collection<ChessPosition> endPositions) {
        int [][] diagonal_directions = {{direction, 1}, {direction, -1}};
        int row_position = position.getRow();
        int col_position = position.getColumn();
        for(int[] diagonal_position : diagonal_directions){
            int diagonal_row = diagonal_position[0];
            int diagonal_col =diagonal_position[1];
            ChessPosition target_position = new ChessPosition(row_position+diagonal_row, col_position+diagonal_col);

            if (inBounds(target_position.getRow(), target_position.getColumn())){
                ChessPiece target_piece = board.getPiece(target_position);
                boolean is_enemy;
                is_enemy = target_piece != null && target_piece.getTeamColor() != teamColor;
                if(is_enemy){
                    endPositions.add(target_position);
                }
            }
        }
    }


    private boolean is_first_move( ChessPosition position, ChessGame.TeamColor teamColor) {
        int row_position = position.getRow();
        return teamColor == ChessGame.TeamColor.BLACK && row_position == 7 || teamColor == ChessGame.TeamColor.WHITE && row_position == 2;
    }


    private void add_valid_positions(int move_type, ChessPosition position, int direction,  Collection<ChessPosition> endPositions ) {
        //move forward
        int row_position = position.getRow();
        int col_position = position.getColumn();
        ChessPosition target_position = new ChessPosition(row_position+(direction*move_type), col_position);


        endPositions.add(target_position);
    }

    private boolean can_move_forward(ChessBoard board, ChessPosition position, int direction) {
        int row_position = position.getRow();
        int col_position = position.getColumn();
        ChessPosition target_position = new ChessPosition(row_position+direction, col_position);
        ChessPiece target_piece = board.getPiece(target_position);
        boolean is_empty;
        is_empty = target_piece == null;
        return inBounds(target_position.getRow(), target_position.getColumn()) && is_empty;
    }

    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }
}