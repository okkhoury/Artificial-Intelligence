
public class Value_Iteration {
	
	private static float grid[][] = new float[7][7];
	
	private static final float gamma = 1;
	private static final float epsilon = .001F;
	private  static final int MAX = 1000;
	
	
	public static void main (String[] args) {
		valueIteration(1);
	}
	
	
	public static void valueIteration( int caseNum) {
		initGridZero();
		
		float delta = 1;
		int iterations = 0;
		while (delta > epsilon && iterations < MAX) {
			iterations++;
			delta = 0;
			for (int i = 0; i < 7; i++) {
				for (int j=0; j < 7; j++) {
					float temp = grid[i][j];
					grid[i][j] = reward(i, j) + gamma*maxPrevious(caseNum, i, j);
					
					if (Math.abs(temp - grid[i][j]) > delta) {
						delta = Math.abs(temp - grid[i][j]);
					}
				}
			}
			
			printGrid();
			System.out.println();
			System.out.println();
		}
	}
	
	public static void case2() {
		// initialize every position to 0
		initGridZero();
		
		float delta = 1;
		int iterations = 0;
	}
	
	
	public static void initGridZero() {
		// initialize every position in the grid to 0
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				grid[i][j] = 0;
			}
		}
	}
	
	
	public static float reward(int i, int j) {
		if (i == 3 && j == 6) {
			return 0;
		} else {
			return -1;
		}
	}
	
	
	public static float maxPrevious(int caseNum, int i, int j) {
		
		if (caseNum == 1) {
			float maxUtil = grid[i][j];
			for (int x = i-1; x <= i+1; x++) {
				for (int y = j-1; y <= j+1; y++) {
					// If utility at neighbor is greater than current
					if (x >= 0 && x < 7 && y >= 0 && y < 7) {
						if(grid[x][y] > maxUtil) 
							maxUtil = grid[x][y];
					}
				}
			}
			return maxUtil;
		} else if (caseNum == 2) {
			float maxUtil = grid[i][j];
			
			// case where light wind applies
			if (j >= 3 && j <= 5) {
				for (int x = i-2; x <= i; x++) {
					for (int y = j-1; y <= j+1; y++) {
						// If utility at neighbor is greater than current
						if (x >= 0 && x < 7 && y >= 0 && y < 7) {
							if(grid[x][y] > maxUtil) 
								maxUtil = grid[x][y];
						}
					}
				}
			} else {		// Case where no wind applies
				for (int x = i-1; x <= i+1; x++) {
					for (int y = j-1; y <= j+1; y++) {
						// If utility at neighbor is greater than current
						if (x >= 0 && x < 7 && y >= 0 && y < 7) {
							if(grid[x][y] > maxUtil) 
								maxUtil = grid[x][y];
						}
					}
				}
			}
			
			
			return maxUtil;
		} else if (caseNum == 3) {
			float maxUtil = grid[i][j];
			
			// case where strong wind applies
			if (j >= 3 && j <= 5) {
				// If the boundary is one above current pos, then we can only check directly east, west, or current pos
				if (i == 0) {
					maxUtil = Math.max(Math.max(grid[i][j-1], grid[i][j+1]), grid[i][j]);
				} else {
					for (int x = i-3; x <= i-1; x++) {
						for (int y = j-1; y <= j+1; y++) {
							// If utility at neighbor is greater than current
							if (x >= 0 && x < 7 && y >= 0 && y < 7) {
								if(grid[x][y] > maxUtil) 
									maxUtil = grid[x][y];
							}
						}
					}
				}
				
			} else {		// Case where no wind applies
				for (int x = i-1; x <= i+1; x++) {
					for (int y = j-1; y <= j+1; y++) {
						// If utility at neighbor is greater than current
						if (x >= 0 && x < 7 && y >= 0 && y < 7) {
							if(grid[x][y] > maxUtil) 
								maxUtil = grid[x][y];
						}
					}
				}
			}
			
			
			return maxUtil;
		}
		
		return 0;
		
		
	}

	
	public static void printGrid() {
		for (int i = 0; i < 7; i++) {
			for (int j=0; j < 7; j++) {
				System.out.print(grid[i][j] + " ");
			}
			System.out.println();
		}
	}
	
}
