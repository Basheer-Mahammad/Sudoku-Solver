import java.util.*;

/**
 * Ultra-Fast 25x25 Sudoku Solver - Java Version
 * Optimized with BitSet constraints, MCV heuristic, and precomputed mappings
 * Performance target: ~2-3 seconds for complex 25x25 puzzles
 */
public class UltraFast25x25 {
    private static final int N = 25;
    private static final int BOX_SIZE = 5;
    
    // Core data structures - using int[][] for maximum performance
    public final int[][] grid;
    
    // OPTIMIZATION 1: BitSet for ultra-fast constraint checking (Java equivalent to C++ bitset)
    private final BitSet[] rowMask;     // rowMask[r].get(v) = true if value v is used in row r
    private final BitSet[] colMask;     // colMask[c].get(v) = true if value v is used in col c  
    private final BitSet[] boxMask;     // boxMask[b].get(v) = true if value v is used in box b
    
    // OPTIMIZATION 2: Precomputed box indices for O(1) lookup
    private final int[][] cellToBox;
    
    // OPTIMIZATION 3: Reusable objects to avoid garbage collection
    private final BitSet tempBitSet;
    
    public UltraFast25x25() {
        grid = new int[N][N];
        
        // Initialize BitSets for constraint tracking
        rowMask = new BitSet[N];
        colMask = new BitSet[N];
        boxMask = new BitSet[N];
        
        for (int i = 0; i < N; i++) {
            rowMask[i] = new BitSet(N + 1);
            colMask[i] = new BitSet(N + 1);
            boxMask[i] = new BitSet(N + 1);
        }
        
        // Precompute box mapping
        cellToBox = new int[N][N];
        precomputeBoxMapping();
        
        // Reusable objects
        tempBitSet = new BitSet(N + 1);
    }
    
    /**
     * OPTIMIZATION 2: Precompute box indices to avoid runtime calculations
     */
    private void precomputeBoxMapping() {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                cellToBox[row][col] = (row / BOX_SIZE) * BOX_SIZE + (col / BOX_SIZE);
            }
        }
    }
    
    /**
     * OPTIMIZATION 3: Ultra-fast constraint updates using BitSet operations
     */
    private void updateMasks(int row, int col, int value, boolean setMask) {
        int box = cellToBox[row][col];
        rowMask[row].set(value, setMask);
        colMask[col].set(value, setMask);
        boxMask[box].set(value, setMask);
    }
    
    /**
     * OPTIMIZATION 4: Most Constrained Variable (MCV) heuristic
     * Finds the empty cell with minimum possible values to minimize branching
     */
    private int[] findBestEmptyCell() {
        int[] bestCell = {-1, -1};
        int minCandidates = N + 1;
        
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (grid[row][col] == 0) {
                    int box = cellToBox[row][col];
                    
                    // OPTIMIZATION: Use BitSet OR operations for instant constraint checking
                    tempBitSet.clear();
                    tempBitSet.or(rowMask[row]);
                    tempBitSet.or(colMask[col]);
                    tempBitSet.or(boxMask[box]);
                    
                    // OPTIMIZATION: Use cardinality() correctly - count only valid numbers 1-25
                    int usedValues = tempBitSet.cardinality() - 1; // -1 to exclude bit 0
                    int candidates = N - usedValues;
                    
                    if (candidates < minCandidates) {
                        minCandidates = candidates;
                        bestCell[0] = row;
                        bestCell[1] = col;
                        
                        if (candidates == 0) return bestCell; // Dead end
                        if (candidates == 1) return bestCell; // Forced move
                    }
                }
            }
        }
        
        return bestCell;
    }
    
    /**
     * OPTIMIZATION 5: Get valid values using array to avoid any collection issues
     */
    private int[] getValidValuesArray(int row, int col) {
        int box = cellToBox[row][col];
        
        // OPTIMIZATION: Use BitSet operations for ultra-fast constraint checking
        tempBitSet.clear();
        tempBitSet.or(rowMask[row]);
        tempBitSet.or(colMask[col]);
        tempBitSet.or(boxMask[box]);
        
        // Count valid values first
        int count = 0;
        for (int val = 1; val <= N; val++) {
            if (!tempBitSet.get(val)) {
                count++;
            }
        }
        
        // Create array of exact size
        int[] validValues = new int[count];
        int index = 0;
        for (int val = 1; val <= N; val++) {
            if (!tempBitSet.get(val)) {
                validValues[index++] = val;
            }
        }
        
        return validValues;
    }
    
    /**
     * OPTIMIZATION 6: Ultra-optimized recursive solver with smart backtracking
     */
    private boolean solveRecursive() {
        // Find the most constrained empty cell
        int[] bestCell = findBestEmptyCell();
        if (bestCell[0] == -1) {
            return true; // No more empty cells - solved!
        }
        
        int row = bestCell[0];
        int col = bestCell[1];
        
        int[] validValues = getValidValuesArray(row, col);
        if (validValues.length == 0) {
            return false; // No valid values - backtrack
        }
        
        // Early termination: if only one valid value, use it immediately
        if (validValues.length == 1) {
            int value = validValues[0];
            grid[row][col] = value;
            updateMasks(row, col, value, true);
            
            boolean result = solveRecursive();
            
            if (!result) {
                grid[row][col] = 0;
                updateMasks(row, col, value, false);
            }
            
            return result;
        }
        
        // Try each valid value in order
        for (int value : validValues) {
            // Place value
            grid[row][col] = value;
            updateMasks(row, col, value, true);
            
            // Recursively solve
            if (solveRecursive()) {
                return true;
            }
            
            // Backtrack
            grid[row][col] = 0;
            updateMasks(row, col, value, false);
        }
        
        return false;
    }
    
    /**
     * Initialize grid from 2D array and set up constraint masks
     */
    public void initializeGrid(int[][] inputGrid) {
        if (inputGrid.length != N || inputGrid[0].length != N) {
            throw new IllegalArgumentException("Grid must be 25x25");
        }
        
        // Clear all masks
        for (int i = 0; i < N; i++) {
            rowMask[i].clear();
            colMask[i].clear();
            boxMask[i].clear();
        }
        
        // Copy grid and update masks for pre-filled cells
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                grid[row][col] = inputGrid[row][col];
                if (grid[row][col] != 0) {
                    updateMasks(row, col, grid[row][col], true);
                }
            }
        }
    }
    
    /**
     * Solve the 25x25 Sudoku puzzle with timing and debugging
     */
    public SolverResult solve25x25UltraFast(int[][] inputGrid) {
        long startTime = System.nanoTime();
        
        try {
            initializeGrid(inputGrid);
            
            // Quick validation: check for obvious conflicts
            if (!isValidInitialState()) {
                long endTime = System.nanoTime();
                double elapsedMs = (endTime - startTime) / 1_000_000.0;
                System.out.println("Initial state validation failed - puzzle has conflicts");
                return new SolverResult(false, null, elapsedMs);
            }
            
            // Count empty cells for debugging
            int emptyCells = 0;
            for (int[] row : grid) {
                for (int cell : row) {
                    if (cell == 0) emptyCells++;
                }
            }
            System.out.printf("Starting solve with %d empty cells...\n", emptyCells);
            
            boolean solved = solveRecursive();
            
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            
            if (solved) {
                System.out.printf("Solution found in %.3f ms\n", elapsedMs);
                return new SolverResult(true, copyGrid(), elapsedMs);
            } else {
                System.out.printf("No solution found after %.3f ms\n", elapsedMs);
                return new SolverResult(false, null, elapsedMs);
            }
        } catch (Exception e) {
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Exception during solve: %s\n", e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            return new SolverResult(false, null, elapsedMs);
        }
    }
    
    /**
     * Check if the initial puzzle state is valid (no immediate conflicts)
     */
    private boolean isValidInitialState() {
        // Check for conflicts without modifying the grid state
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (grid[row][col] != 0) {
                    int value = grid[row][col];
                    
                    // Check for duplicates in row (excluding current cell)
                    for (int c = 0; c < N; c++) {
                        if (c != col && grid[row][c] == value) {
                            return false;
                        }
                    }
                    
                    // Check for duplicates in column (excluding current cell)
                    for (int r = 0; r < N; r++) {
                        if (r != row && grid[r][col] == value) {
                            return false;
                        }
                    }
                    
                    // Check for duplicates in box (excluding current cell)
                    int boxRowStart = (row / BOX_SIZE) * BOX_SIZE;
                    int boxColStart = (col / BOX_SIZE) * BOX_SIZE;
                    for (int r = boxRowStart; r < boxRowStart + BOX_SIZE; r++) {
                        for (int c = boxColStart; c < boxColStart + BOX_SIZE; c++) {
                            if ((r != row || c != col) && grid[r][c] == value) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Get a copy of the current grid
     */
    private int[][] copyGrid() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, N);
        }
        return copy;
    }
    
    /**
     * Get sample 25x25 puzzle for testing - Simple valid puzzle
     */
    public static int[][] getSample25x25Puzzle() {
        int[][] puzzle = new int[25][25];
        
        // Create a simple but valid puzzle by placing numbers very carefully
        // Use only a subset of numbers to avoid conflicts while testing the algorithm
        
        // Place numbers in first row of each 5x5 box to give structure
        for (int box = 0; box < 5; box++) {
            int startCol = box * 5;
            for (int j = 0; j < 5; j++) {
                puzzle[0][startCol + j] = j + 1 + (box * 5);
            }
        }
        
        // Place numbers in first column of each 5x5 box (avoiding conflicts)
        for (int box = 0; box < 5; box++) {
            int startRow = box * 5;
            for (int i = 1; i < 5; i++) { // Skip first row, already filled
                if (startRow + i < 25) {
                    puzzle[startRow + i][0] = ((i * 6) % 25) + 1;
                }
            }
        }
        
        // Add some additional strategic numbers
        puzzle[1][6] = 1;
        puzzle[2][7] = 2;
        puzzle[3][8] = 3;
        puzzle[4][9] = 4;
        
        puzzle[6][11] = 7;
        puzzle[7][12] = 8;
        puzzle[8][13] = 9;
        puzzle[9][14] = 10;
        
        puzzle[11][16] = 12;
        puzzle[12][17] = 13;
        puzzle[13][18] = 14;
        puzzle[14][19] = 15;
        
        puzzle[16][21] = 17;
        puzzle[17][22] = 18;
        puzzle[18][23] = 19;
        puzzle[19][24] = 20;
        
        // Add some middle elements
        puzzle[10][10] = 11;
        puzzle[15][15] = 16;
        puzzle[20][20] = 21;
        
        return puzzle;
    }
    
    /**
     * Public solve method without parameters for convenience
     */
    public boolean solve() {
        return solveRecursive();
    }
    
    /**
     * Result class to hold solving results
     */
    public static class SolverResult {
        public final boolean solved;
        public final int[][] grid;
        public final double timeMs;
        
        public SolverResult(boolean solved, int[][] grid, double timeMs) {
            this.solved = solved;
            this.grid = grid;
            this.timeMs = timeMs;
        }
    }
}
