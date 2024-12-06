package stud.smart1;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static core.board.Board.FORWARD;

public class AI extends core.player.AI {
    private final Random rand = new Random();
    private int index1 = -1;//落子1位置
    private int index2 = -1;//落子2位置
    private boolean willwin = false;
    private boolean willloss = false;
    @Override
    public Move findNextMove(Move opponentMove) {
        //初始化！
        index1 = -1;
        index2 = -1;
        willwin = false;
        willloss =false;
        this.board.makeMove(opponentMove);
        //1.首先看看能不能直接胜利
        //从哪里拿到所有以下棋子的序列呢_this.board.getMoveList()
        //开局的第一颗棋子不会被纳入moveList中
        //开始就不要讲效率了，暴力遍历！！
//        System.out.println("movelist"+this.board.getMoveList());
//        System.out.println(this.board.getMoveList());
//        System.out.println(this._myColor);
        ArrayList<Move>movelist = this.board.getMoveList();
        //逆序遍历movelist，显然最后一个元素一定是对手的操作
        //对自己下过的所有历史棋子进行胜手判断
        for(int i = movelist.size() - 2; i >= 0 ; i = i - 2){
            Move mymove = movelist.get(i);
            //检查己方棋子
//            System.out.println("mymove:"+mymove+" col:"+mymove.col0()+"row:" + mymove.row0());
            checkWillWin(mymove.col0(),mymove.row0(),this._myColor);
            checkWillWin(mymove.col1(),mymove.row1(),this._myColor);
            //找到胜手
//            System.out.println("P1:"+Move.col(index1) + Move.row(index1) +Move.col(index2) + Move.row(index2));
            if(willwin) {
                Move move = new Move(index1, index2);
                this.board.makeMove(move);
                return move;
            }
        }
        //2.计算对面的威胁，防守<=2的威胁
        //对对方下的所有历史棋子遍历
        for(int i = movelist.size()-1; i >= 0; i = i-2){
            Move mymove = movelist.get(i);
            //这里居然合理复用了,检查对方棋子
//            System.out.println("opposite move:"+mymove+" col:"+mymove.col0()+"row:" + mymove.row0());
            checkWillWin(mymove.col0(),mymove.row0(),this._myColor.opposite());
            checkWillWin(mymove.col1(),mymove.row1(),this._myColor.opposite());
            //找到防手
//            System.out.println("P2:"+Move.col(index1) + Move.row(index1) +Move.col(index2) + Move.row(index2));
            if(willloss) {
                Move move = new Move(index1, index2);
                this.board.makeMove(move);
                return move;
            }
        }
        //3.选择合理的位置下棋
        //一个容易想到的暴力策略是：找最大扩展联通块（5/4/3）,最大连通块的计算在第一步已经完成了其实
        //这里存在一种特殊的情况，就是找不到对一个连通块进行连续两次扩展的情况，只能分开下
        //对于这种情况，直接随机算了，因为要产生这种情况棋盘上应该非常密集
        if(index1 == -1) {
            while(true){
                index1 = rand.nextInt(361);
                if(this.board.get(index1) == PieceColor.EMPTY)break;
            }
        }
        if(index2 == -1) {
            while(true){
                index2 = rand.nextInt(361);
                if(this.board.get(index2) == PieceColor.EMPTY)break;
            }
        }
//        System.out.println("P3:"+Move.col(index1) + Move.row(index1) +Move.col(index2) + Move.row(index2));
        Move move = new Move(index1, index2);
        this.board.makeMove(move);
        return move;
    }
//    当没有胜手和防手时的扩展策略
    private void expend(){

    }
    //检查一个点的位置，判断有没有胜手可以下，有则更新index
    private void checkWillWin(char col,char row,PieceColor color){
        int maxblocklen = 0;
        for(int dir = 0; dir < 4; ++dir) {
//            正向串长度
            int forwardstep = 0;
//            寻找正向最大连续串长度，一个方向最多有4个自己的棋子，否则已经赢了
            for(int j = 1; j <= 4; j ++){
                //在dir方向偏移j个单位
                char tempCol = (char)(col + FORWARD[dir][0] * j);
                char tempRow = (char)(row + FORWARD[dir][1] * j);
                if(!Move.validSquare(tempCol,tempRow) || this.board.get(tempCol,tempRow) != color){
                    // 找连续块
                    break;
                }
                //遇到空位置和白子就记录长度+1
                forwardstep ++;
            }
            //反向串长度
            int backwardstep = 0;
            for(int j = 1; j <= 4; j ++){
                //在dir方向偏移j个单位
                char tempCol = (char)(col - FORWARD[dir][0] * j);
                char tempRow = (char)(row - FORWARD[dir][1] * j);
                if(!Move.validSquare(tempCol,tempRow) || this.board.get(tempCol,tempRow) != color){
                    //遇到边界了（棋盘边界or对手的防守棋子）
                    break;
                }
                //遇到空位置和白子就记录长度+1
                backwardstep ++;
            }
            //可能的取胜机会 这里第一步和第三步可以合并到下面的情况，如果存在胜手，那maxblocklen一定的>=4的
            //有胜手选择胜手，反之找最大联通块进行扩展，落子选择规则居然都是一样的
            //如果找到落子位置才更新maxblocklen，否则不更新
//            System.out.println("bkstep:" + backwardstep + "fwdstep:" + forwardstep+"maxblocklen"+maxblocklen);
            if(backwardstep + forwardstep >= maxblocklen){
//                对于防手的预测特判一下，否则防的太早了

                //至少有四颗连成一块了
                //这时候有两种情况，第一种两边各下一颗胜利；第二种一边连下两颗胜利
                //情况1：两边都能落子
                boolean f1 = chekAPoint(col,row,dir,forwardstep+1);
                boolean f2 =  chekAPoint(col,row,dir,-backwardstep-1);
                if(f1 && f2){
//                    System.out.println("case1");
                    //找到了才能更新
                    maxblocklen = backwardstep + forwardstep;
                    //对于判断防手来说不要急着赋值
                    if(maxblocklen < 3 && color == this._myColor.opposite()) continue;
                    //因为这里的col和row都是char，所以有个1300的偏移
                    index1 = (char)(col + FORWARD[dir][0] * (forwardstep+1)) +
                             (char)(row + FORWARD[dir][1] * (forwardstep+1)) * 19 + -1300;
                    index2 = (char)(col - FORWARD[dir][0] * (backwardstep+1)) +
                            (char)(row - FORWARD[dir][1] * (backwardstep+1)) * 19 + -1300;

                }else if(!f1 && !f2){
                    //两边都不能落子
//                    System.out.println("case2");
                    return;
                }else if(f1){
//                    System.out.println("case3");
                    //情况2：右边可以落子，左边不行，需要再向右探索一次
                    if(chekAPoint(col,row,dir,forwardstep+2)){
                        maxblocklen = backwardstep + forwardstep;
                        if(maxblocklen < 3 && color == this._myColor.opposite()) continue;
                        index1 = (char)(col + FORWARD[dir][0] * (forwardstep+1)) +
                                (char)(row + FORWARD[dir][1] * (forwardstep+1)) * 19 + -1300;
                        index2 = (char)(col + FORWARD[dir][0] * (forwardstep+2)) +
                                (char)(row + FORWARD[dir][1] * (forwardstep+2)) * 19 + -1300;

                    }
                    //右边只能落一颗子，最长连成5颗，无法直接取胜

                }else{
//                    System.out.println("case4");
                    //情况2：左边可以落子，右边不行，需要向左探索一次
                    if(chekAPoint(col,row,dir,-backwardstep - 2)){
                        maxblocklen = backwardstep + forwardstep;
                        if(maxblocklen < 3 && color == this._myColor.opposite()) continue;
                        index1 = (char)(col - FORWARD[dir][0] * (backwardstep+1)) +
                                (char)(row - FORWARD[dir][1] * (backwardstep+1)) * 19 + -1300;
                        index2 = (char)(col - FORWARD[dir][0] * (backwardstep+2)) +
                                (char)(row - FORWARD[dir][1] * (backwardstep+2)) * 19 + -1300;

                    }
                }
//                如果一个方向已经延申3颗（自己不算），那必有胜手
                if(maxblocklen >= 3 ) willwin = true;
                if(maxblocklen >= 3 && color == this._myColor.opposite()) willloss = true;
            }

        }

    }
//    检查一个点的位置的某个偏移是否可以落子
    private boolean chekAPoint(char col,char row,int dir, int offset){
        char tempCol = (char)(col + FORWARD[dir][0] * offset);
        char tempRow = (char)(row + FORWARD[dir][1] * offset);
        if(!Move.validSquare(tempCol,tempRow) || this.board.get(tempCol,tempRow)!= PieceColor.EMPTY)
            return false;
        return true;
    }
    @Override
    public String name() {
        return "smart1";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();

    }
}
