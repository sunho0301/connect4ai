package connect4ai;

import java.util.Arrays;

public class TranspositionTable {
	
	public static final int DEFAULT_SIZE = 8388617; // the smallest prime number that is greater than 2^23
	
	private long[] keys;
	private int[] vals;
	
	public TranspositionTable() {
		this.keys = new long[DEFAULT_SIZE];
		this.vals = new int[DEFAULT_SIZE];
	}
	
	public void put(long key, int val) {
		int index = (int) (key % DEFAULT_SIZE);
		keys[index] = key;
		vals[index] = val;
	}
	
	public int get(long key) {
		int index = (int) (key % DEFAULT_SIZE);
		if (keys[index] == key) {
			return vals[index];
		}
		return 0;
	}
	
	public void clear() {
		Arrays.fill(this.keys, 0l);
		Arrays.fill(this.vals, 0);
	}
}