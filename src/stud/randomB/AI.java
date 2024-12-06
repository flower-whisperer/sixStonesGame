package stud.randomB;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI extends core.player.AI {
    private static List<Integer> index1Box = new ArrayList<>();
    static {
        //中心区域的169个位置
        for(int i = 3; i <= 15; i ++){
            for(int j = 3; j <= 15; j ++){
                index1Box.add(i*19+j);
            }
        }

    }
    private Random rand = new Random();

    @Override
    public Move findNextMove(Move opponentMove) {
        this.board.makeMove(opponentMove);
        //在中心区域找不到落子位置次数
//        System.out.println(this.board.getMoveList());
        int failTimes = 0;
        while (true) {
            //从中心13*13区域随机选
            int index1 = index1Box.get(rand.nextInt(169));
            int index2 = index1Box.get(rand.nextInt(169));
            if(index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY) {
                Move move = new Move(index1, index2);
                this.board.makeMove(move);
                return move;
            }else failTimes ++;
            if(failTimes == 10) break;
        }
        while(true){
            int index1 = rand.nextInt(361);
            int index2 = rand.nextInt(361);
            if(index1 != index2 && this.board.get(index1) == PieceColor.EMPTY && this.board.get(index2) == PieceColor.EMPTY) {
                Move move = new Move(index1, index2);
                this.board.makeMove(move);
                return move;
            }
        }
    }

    public String name() {
        return "randomB";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
    }
}