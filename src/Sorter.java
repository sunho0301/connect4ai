package connect4ai;

import java.util.Arrays;

/*
 * 
 * Sorter program sorts moves based on the score.
 * 
 * */
public class Sorter {
	
	private int[] scores;
	private long[] moves;
	private int index;
	
	public Sorter(int size) {
		this.scores = new int[size];
		this.moves = new long[size];
		this.index = -1;
	}
	
	// uses insertion sort since there aren't many columns
	public void add(int score, long move) {
		int i = ++index;
		while (i > 0 && scores[i - 1] > score) {
			scores[i] = scores[i - 1];
			moves[i] = moves[i-- - 1];
		}
		scores[i] = score;
		moves[i] = move;
	}
	
	public long getNext() {
		if (index < 0) return 0;
		return moves[index--];
	}
	
	public void clear() {
		Arrays.fill(scores, 0);
		Arrays.fill(moves, 0);
		index = -1;
	}
	
}

