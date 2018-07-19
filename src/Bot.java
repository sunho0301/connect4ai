package connect4ai;

import java.util.Arrays;

import connect4ai.Sorter;
import connect4ai.TranspositionTable;

public class Bot {

    public static final String PLACE_DISC = "place_disc ";
    public static final int NUM_DP_MOVE = 9;

    private BitBoard64 board;
    private int boardWidth;
    private int boardHeight;
    private int[] levels;
    private TranspositionTable map;
    private int[] colOrder;
    private int minScore;
    private int maxScore;
    private DPMoves dp;
    private int dpPos;

    public Bot(BitBoard64 board) {
        this.board = board;
        this.boardWidth = board.getWidth();
        this.boardHeight = board.getHeight();
        this.levels = new int[this.boardWidth];
        Arrays.fill(this.levels, this.boardHeight - 1);
        this.map = new TranspositionTable();
        this.colOrder = new int[this.boardWidth];
        for (int i = 0; i < this.boardWidth; ++i) {
        	colOrder[i] = this.boardWidth / 2 + (1-2*(i%2))*(i+1)/2;
        }
        this.minScore = 3 - (this.boardWidth * this.boardHeight / 2);
        this.maxScore = (this.boardWidth * this.boardHeight + 1) / 2 - 3;
        this.dp = new DPMoves();
        this.dp.fillDPMoves();
        this.dpPos = -1;
    }

    public void setBoard(BitBoard64 board) {
        this.board = board;
    }

    public BitBoard64 getBoard() {
        return this.board;
    }

    public void makeMove() {
    	int column = 3; //default;
    	long nextMove = getNextMove(column); // column 3 
    	long canWin = this.board.getWinningBoard();
    	if (canWin != 0) {
    		column = Long.numberOfTrailingZeros(canWin) / (this.boardHeight + 1);
    		nextMove = getNextMove(column);
    	} else {
    		if (this.board.getNumMoves() < NUM_DP_MOVE) {
    			column = this.dp.getCol(dpPos);
    			nextMove = getNextMove(column);
    		} else {
	    		int score = this.maxScore;
		    	for (int i = 0; i < this.colOrder.length; ++i) {
		    		if (levels[this.colOrder[i]] >= 0) {
		    			long next = 1l << (this.boardHeight+1) * this.colOrder[i] + this.boardHeight - 1 - levels[this.colOrder[i]];
		    			this.board.updateBoard(next);
		    			levels[this.colOrder[i]]--;
		    			int currentScore = getScore(this.board);
		    			
		    			// if there is a winning move, just choose that move and don't search the rest.
		    			if (currentScore < 0) {
		    				System.out.println(PLACE_DISC + this.colOrder[i]);
		    				return;
		    			}
		    			
		    			// for non-winning move, choose the least bad.
		    			if (currentScore < score) {
		    				score = currentScore;
		    				nextMove = next;
		    				column = this.colOrder[i];
		    			}
		    			this.board.removeDisc(next);
		    			levels[this.colOrder[i]]++;
		    			
		    		}
		    	} 
    		}
    	}
        
        System.out.println(PLACE_DISC + column);
        --this.levels[column];
        this.board.updateBoard(nextMove);
        updateDpPos(column);
    }
    
    private long getNextMove(int column) {
    	return (1l << ((this.boardHeight + 1 ) * column + this.boardHeight - 1 - this.levels[column]));
    }
    
    private void updateDpPos(int col) {
    	if (this.dpPos == -1) {
    		if (col == 0) {
    			this.dpPos = 7;
    		} else {
    			this.dpPos = col;
    		}
    	} else {
    		this.dpPos = this.dpPos * 10 + col;
    	}
    }
    
    // updates the board when update has been made.
    public void placeDiscToBoard(String updatedBoard) {
    	for (int i = 0; i < this.levels.length; ++i) {
    		if (this.levels[i] >= 0) {
	    		char disc = updatedBoard.charAt(this.levels[i] * this.levels.length * 2 + i * 2);
	    		if (disc != '.') {
	    			this.board.updateBoard(getNextMove(i));
	    			--this.levels[i];
	    			updateDpPos(i);
	    			return;
	    		}
    		}
    	}
    }
    
    private int negamax(BitBoard64 b1, int alpha, int beta, int depth) {
    	if (alpha >= beta) {
    		throw new IllegalArgumentException();
    	}
    	long possible = b1.getNonLosingPositions();
    	if (possible == 0) {
    		return -(this.boardWidth * this.boardHeight - b1.getNumMoves()) / 2;
    	}
    	if (b1.getNumMoves() >= this.boardWidth * this.boardHeight - 2) {
    		return 0;
    	}
    	int min = -(this.boardWidth * this.boardHeight - 2 - b1.getNumMoves()) / 2;
    	if (alpha < min) {
    		alpha = min;
    		if (alpha >= beta) return alpha;
    	}
    	int max = (this.boardWidth * this.boardHeight - 1 - b1.getNumMoves()) / 2;
    	if (beta > max) {
    		beta = max;
    		if (alpha >= beta) return beta;
    	}
    	long key = b1.getKey();
    	int val = this.map.get(key);
    	if (val != 0) {
    		if (val > this.maxScore - this.minScore + 1) {
    			min = val + 2 * this.minScore - this.maxScore - 2;
    			if (alpha < min) {
    				alpha = min;
	    			if (alpha >= beta) {
	    				return alpha;
	    			}
    			}
    		} else {
    			max = val + this.minScore - 1;
    			if (beta > max) {
    				beta = max;
    				if (alpha >= beta) {
    					return beta;
    				}
    			}
    		}
    	}
    	Sorter sorter = new Sorter(this.boardWidth);
    	for (int i = 0; i < this.boardWidth; ++i) {
    		long nextMove = possible & b1.getFilledCol(this.colOrder[i]);
    		if (nextMove != 0) {
    			sorter.add(b1.getBitScore(nextMove), nextMove);
    		}
    	}
    	long next = sorter.getNext();
    	while (next != 0) {


			b1.updateBoard(next);
			
			// dfs
			int score = -negamax(b1, -beta, -alpha, depth + 1);
			b1.removeDisc(next);
			if (score >= beta) {
				map.put(key, score + this.maxScore - 2 * this.minScore + 2);
				return score;
			}
			if (score > alpha) {
				alpha = score;
			}
			next = sorter.getNext();
    	}

    	map.put(key, alpha - this.minScore + 1);
    	return alpha;
    }
    
    private int getScore(BitBoard64 b1) {
    	if (b1.canWin()) {
    		return (this.boardWidth * this.boardHeight + 1 - b1.getNumMoves()) / 2;
    	}
    	int min = -(this.boardWidth * this.boardHeight - b1.getNumMoves()) / 2;
    	int max = (this.boardWidth * this.boardHeight + 1 - b1.getNumMoves()) / 2;
    	while (min < max) {
    		int med = min + (max - min) / 2;
    		if (med <= 0 && min / 2 < med) {
    			med = min / 2;
    		} else if (med >= 0 && max / 2 > med) {
    			med = max / 2;
    		}
    		int r = negamax(b1, med, med + 1, 0);
    		if (r <= med) {
    			max = r;
    		} else {
    			min = r;
    		}
    	}
    	return min;
    }
}