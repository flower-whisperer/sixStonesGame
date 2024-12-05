package stud.randomA;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//全局乱下的AI就是g88，这里从第二个智障AI开始实现
public class AI extends core.player.AI {
//    从右开始顺时针八个方向的偏移
    private static int[] direction = {1, 20, 19, 18, -1, -20,-19,-18};
    private Random rand = new Random();
    @Override
    public Move findNextMove(Move opponentMove) {
        this.board.makeMove(opponentMove);
        while (true) {
            int index1 = rand.nextInt(361);
            if (this.board.get(index1) == PieceColor.EMPTY) {
                int index2 = checkSurround(index1);

                Move move = new Move(index1, index2);
                this.board.makeMove(move);
                return move;
            }
        }
    }
//    检查第一颗落子周围的八个位置，返回合法位置的list
    public int checkSurround(int index){
        List<Integer>list = new ArrayList<>();
        for (int dir : direction){
            int nextIndex = index + dir;
            if(nextIndex >=0 && nextIndex <= 360 && this.board.get(nextIndex) == PieceColor.EMPTY){
                //不超出棋盘范围且没有落子
                list.add(nextIndex);
            }
        }
        if(list.isEmpty()){
//            周围八个位置全部不能下
            while(true){
                int index2 = rand.nextInt(361);
                if(index2 != index && this.board.get(index2) == PieceColor.EMPTY)
                    return index2;
            }
            //从能下的位置里随机挑一个下
        }else return list.get(rand.nextInt(list.size()));
    }

    public String name() {
        return "randomA";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
    }
}
