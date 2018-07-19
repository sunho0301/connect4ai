package connect4ai;

public class BitBoard64 {
	
	private int width;
	private int height;
	private int newHeight;
	private long currentPosition;
	private long mask;
	private long boardMask;
	private long bottom;
	private int numMoves;
	
	public BitBoard64(int width, int height) {
		if (width < 0 || height < 0 || (height + 1) * width > 64) {
			throw new IllegalArgumentException();
		}
		this.width = width;
		this.height = height;
		this.newHeight = height + 1;
		this.currentPosition = 0l;
		this.mask = 0l;
		this.bottom = 0l;
		for (int i = 0; i < width; ++i) {
			this.bottom |= 1l << (i * this.newHeight);
		}
		this.boardMask = this.bottom * ((1l << this.height) - 1);
	}
	
	public BitBoard64(BitBoard64 other) {
		this.width = other.getWidth();
		this.height = other.getHeight();
		this.newHeight = other.getNewHeight();
		this.currentPosition = other.getCurrentPosition();
		this.mask = other.getMask();
		this.bottom = other.getBottom();
		this.boardMask = other.getBoardMask();
		this.numMoves = other.getNumMoves();
	}

	
	// width * (height+1) should not be greater than 64
	public void setWidth(int width) {
		if (width < 0) throw new IllegalArgumentException();
		this.width = width;
	}
	
	// width * (height+1) should not be greater than 64
	public void setHeight(int height) {
		if (height < 0) throw new IllegalArgumentException();
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getNewHeight() {
		return this.newHeight;
	}
	
	public long getCurrentPosition() {
		return this.currentPosition;
	}
	
	public long getMask() {
		return this.mask;
	}
	
	public long getBottom() {
		return this.bottom;
	}
	
	public long getBoardMask() {
		return this.boardMask;
	}
	
	public int getNumMoves() {
		return this.numMoves;
	}
	
	public void updateBoard(int row, int col) {
		this.currentPosition ^= this.mask;
		this.mask |= (1l << (this.newHeight * col + this.height - 1 - row));
		++this.numMoves;
	}
	
	public void updateBoard(long newBoard) {
		this.currentPosition ^= this.mask;
		this.mask |= newBoard;
		++this.numMoves;
	}
	
	public void removeDisc(long disc) {
		this.mask ^= disc;
		this.currentPosition ^= this.mask;
		--this.numMoves;
	}
	
	public long getNonLosingPositions() {
		long possible = (this.mask + this.bottom) & this.boardMask;
		long opponentWinning = getWinningPositions(this.currentPosition ^ this.mask);
		long needToBeCovered = possible & opponentWinning;
		if (needToBeCovered != 0) {
			// if there are more than one position to be covered
			if ((needToBeCovered & (needToBeCovered - 1)) != 0) {
				return 0l;
			}
			possible = needToBeCovered;
		}
		return possible & ~(opponentWinning >> 1);
	}
	
	public boolean canWin() {
		return (getWinningPositions(this.currentPosition) & (this.mask + this.bottom) & this.boardMask) != 0;
	}
	
	public long getWinningBoard() {
		return getWinningPositions(this.currentPosition) & (this.mask + this.bottom) & this.boardMask;
	}
	
	public int getBitScore(long move) {
		return Long.bitCount(getWinningPositions(this.currentPosition | move));
	}
	
	public long getKey() {
		return this.currentPosition + this.mask;
	}
	
	public long getFilledCol(int col) {
		return ((1l << this.height) - 1) << (col * this.newHeight);
	}
	
	public void clear() {
		this.currentPosition = 0l;
		this.mask = 0l;
		this.numMoves = 0;
		// no need to change other variables unless the board shape is changed
		// and the shape is unlikely to be changed. so handling omitted.
	}
	
	private long getWinningPositions(long pos) {
		long pos1 = (pos << 1) & (pos << 2) & (pos << 3);
		long pos2 = (pos << this.newHeight) & (pos << 2 * this.newHeight);
		//
		pos1 |= pos2 & (pos << 3 * this.newHeight);
		pos1 |= pos2 & (pos >> this.newHeight);
		pos2 = (pos >> this.newHeight) & (pos >> 2 * this.newHeight);
		pos1 |= pos2 & (pos << this.newHeight);
		pos1 |= pos2 & (pos >> 3 * this.newHeight);
		//
		pos2 = (pos << this.height) & (pos << 2 * this.height);
		pos1 |= pos2 & (pos << 3 * this.height);
		pos1 |= pos2 & (pos >> this.height);
		pos2 = (pos >> this.height) & (pos >> 2 * this.height);
		pos1 |= pos2 & (pos << this.height);
		pos1 |= pos2 & (pos >> 3 * this.height);
		//
		pos2 = (pos << (this.height + 2)) & (pos << 2 * (this.height + 2));
		pos1 |= pos2 & (pos << 3 * (this.height + 2));
		pos1 |= pos2 & (pos >> (this.height + 2));
		pos2 = (pos >> (this.height + 2)) & (pos >> 2 * (this.height + 2));
		pos1 |= pos2 & (pos << 3 * (this.height + 2));
		pos1 |= pos2 & (pos >> 3 * (this.height + 2));
		
		return pos1 & (this.boardMask ^ this.mask);
	}
	
}