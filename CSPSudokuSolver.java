import java.util.*;

/**
 * CONSTRAINT SATISFACTION PROBLEM SOLVER
 * Advanced CSP techniques for Sudoku with maximum optimization
 */
public class CSPSudokuSolver {
    
    /**
     * OPTIMIZATION: Specialized solver for different sizes with adaptive algorithms
     */
    public static class AdaptiveSolver {
        
        /**
         * ADVANCED TECHNIQUE 1: Naked Singles + Hidden Singles
         */
        public static boolean applyNakedAndHiddenSingles(int[][] grid, BitSet[][] domains) {
            int N = grid.length;
            boolean progress = true;
            
            while (progress) {
                progress = false;
                
                // Naked Singles: cells with only one possible value
                for (int r = 0; r < N; r++) {
                    for (int c = 0; c < N; c++) {
                        if (grid[r][c] == 0 && domains[r][c].cardinality() == 1) {
                            int value = domains[r][c].nextSetBit(1);
                            grid[r][c] = value;
                            if (propagateValue(grid, domains, r, c, value)) {
                                progress = true;
                            } else {
                                return false; // Conflict
                            }
                        }
                    }
                }
                
                // Hidden Singles: values that can only go in one cell
                progress |= applyHiddenSingles(grid, domains);
            }
            
            return true;
        }
        
        private static boolean applyHiddenSingles(int[][] grid, BitSet[][] domains) {
            int N = grid.length;
            int BOX_SIZE = (int) Math.sqrt(N);
            boolean progress = false;
            
            // Check rows
            for (int r = 0; r < N; r++) {
                for (int val = 1; val <= N; val++) {
                    if (isValueUsedInRow(grid, r, val)) continue;
                    
                    int possibleCol = -1;
                    int count = 0;
                    
                    for (int c = 0; c < N; c++) {
                        if (grid[r][c] == 0 && domains[r][c].get(val)) {
                            possibleCol = c;
                            count++;
                        }
                    }
                    
                    if (count == 1) {
                        grid[r][possibleCol] = val;
                        propagateValue(grid, domains, r, possibleCol, val);
                        progress = true;
                    }
                }
            }
            
            // Check columns
            for (int c = 0; c < N; c++) {
                for (int val = 1; val <= N; val++) {
                    if (isValueUsedInColumn(grid, c, val)) continue;
                    
                    int possibleRow = -1;
                    int count = 0;
                    
                    for (int r = 0; r < N; r++) {
                        if (grid[r][c] == 0 && domains[r][c].get(val)) {
                            possibleRow = r;
                            count++;
                        }
                    }
                    
                    if (count == 1) {
                        grid[possibleRow][c] = val;
                        propagateValue(grid, domains, possibleRow, c, val);
                        progress = true;
                    }
                }
            }
            
            // Check boxes
            for (int box = 0; box < N; box++) {
                int startRow = (box / BOX_SIZE) * BOX_SIZE;
                int startCol = (box % BOX_SIZE) * BOX_SIZE;
                
                for (int val = 1; val <= N; val++) {
                    if (isValueUsedInBox(grid, startRow, startCol, BOX_SIZE, val)) continue;
                    
                    int possibleRow = -1, possibleCol = -1;
                    int count = 0;
                    
                    for (int r = startRow; r < startRow + BOX_SIZE; r++) {
                        for (int c = startCol; c < startCol + BOX_SIZE; c++) {
                            if (grid[r][c] == 0 && domains[r][c].get(val)) {
                                possibleRow = r;
                                possibleCol = c;
                                count++;
                            }
                        }
                    }
                    
                    if (count == 1) {
                        grid[possibleRow][possibleCol] = val;
                        propagateValue(grid, domains, possibleRow, possibleCol, val);
                        progress = true;
                    }
                }
            }
            
            return progress;
        }
        
        /**
         * OPTIMIZATION: Efficient constraint propagation
         */
        private static boolean propagateValue(int[][] grid, BitSet[][] domains, int row, int col, int value) {
            int N = grid.length;
            int BOX_SIZE = (int) Math.sqrt(N);
            
            // Clear domain of assigned cell
            domains[row][col].clear();
            domains[row][col].set(value);
            
            // Remove value from row
            for (int c = 0; c < N; c++) {
                if (c != col) {
                    domains[row][c].clear(value);
                    if (grid[row][c] == 0 && domains[row][c].isEmpty()) {
                        return false; // Conflict
                    }
                }
            }
            
            // Remove value from column
            for (int r = 0; r < N; r++) {
                if (r != row) {
                    domains[r][col].clear(value);
                    if (grid[r][col] == 0 && domains[r][col].isEmpty()) {
                        return false; // Conflict
                    }
                }
            }
            
            // Remove value from box
            int startRow = (row / BOX_SIZE) * BOX_SIZE;
            int startCol = (col / BOX_SIZE) * BOX_SIZE;
            
            for (int r = startRow; r < startRow + BOX_SIZE; r++) {
                for (int c = startCol; c < startCol + BOX_SIZE; c++) {
                    if (r != row || c != col) {
                        domains[r][c].clear(value);
                        if (grid[r][c] == 0 && domains[r][c].isEmpty()) {
                            return false; // Conflict
                        }
                    }
                }
            }
            
            return true;
        }
        
        /**
         * OPTIMIZATION: Pre-validation to catch unsolvable puzzles early
         */
        public static boolean isValidPuzzle(int[][] puzzle) {
            int N = puzzle.length;
            int BOX_SIZE = (int) Math.sqrt(N);
            
            // Check if BOX_SIZE is valid
            if (BOX_SIZE * BOX_SIZE != N) {
                return false;
            }
            
            // Check row constraints
            for (int r = 0; r < N; r++) {
                BitSet used = new BitSet(N + 1);
                for (int c = 0; c < N; c++) {
                    int val = puzzle[r][c];
                    if (val != 0) {
                        if (val < 1 || val > N || used.get(val)) {
                            return false;
                        }
                        used.set(val);
                    }
                }
            }
            
            // Check column constraints
            for (int c = 0; c < N; c++) {
                BitSet used = new BitSet(N + 1);
                for (int r = 0; r < N; r++) {
                    int val = puzzle[r][c];
                    if (val != 0) {
                        if (val < 1 || val > N || used.get(val)) {
                            return false;
                        }
                        used.set(val);
                    }
                }
            }
            
            // Check box constraints
            for (int box = 0; box < N; box++) {
                BitSet used = new BitSet(N + 1);
                int startRow = (box / BOX_SIZE) * BOX_SIZE;
                int startCol = (box % BOX_SIZE) * BOX_SIZE;
                
                for (int r = startRow; r < startRow + BOX_SIZE; r++) {
                    for (int c = startCol; c < startCol + BOX_SIZE; c++) {
                        int val = puzzle[r][c];
                        if (val != 0) {
                            if (val < 1 || val > N || used.get(val)) {
                                return false;
                            }
                            used.set(val);
                        }
                    }
                }
            }
            
            return true;
        }
        
        // Helper methods
        private static boolean isValueUsedInRow(int[][] grid, int row, int value) {
            for (int c = 0; c < grid.length; c++) {
                if (grid[row][c] == value) return true;
            }
            return false;
        }
        
        private static boolean isValueUsedInColumn(int[][] grid, int col, int value) {
            for (int r = 0; r < grid.length; r++) {
                if (grid[r][col] == value) return true;
            }
            return false;
        }
        
        private static boolean isValueUsedInBox(int[][] grid, int startRow, int startCol, int boxSize, int value) {
            for (int r = startRow; r < startRow + boxSize; r++) {
                for (int c = startCol; c < startCol + boxSize; c++) {
                    if (grid[r][c] == value) return true;
                }
            }
            return false;
        }
    }
    
    /**
     * SOLVER RESULT CLASS
     */
    public static class SolverResult {
        public final boolean solved;
        public final int[][] grid;
        public final double timeMs;
        public final int size;
        
        public SolverResult(boolean solved, int[][] grid, double timeMs, int size) {
            this.solved = solved;
            this.grid = grid;
            this.timeMs = timeMs;
            this.size = size;
        }
    }

    /**
     * MAIN SOLVING METHOD with all optimizations
     */
    public static SolverResult solve(int[][] puzzle) {
        long startTime = System.nanoTime();
        
        // STEP 1: Validate puzzle
        if (!AdaptiveSolver.isValidPuzzle(puzzle)) {
            double timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
            return new SolverResult(false, puzzle, timeMs, puzzle.length);
        }
        
        int N = puzzle.length;
        
        // STEP 2: Create working copy
        int[][] grid = new int[N][N];
        for (int r = 0; r < N; r++) {
            System.arraycopy(puzzle[r], 0, grid[r], 0, N);
        }
        
        // STEP 3: Initialize domains
        BitSet[][] domains = new BitSet[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                domains[r][c] = new BitSet(N + 1);
                if (grid[r][c] == 0) {
                    for (int val = 1; val <= N; val++) {
                        domains[r][c].set(val);
                    }
                } else {
                    domains[r][c].set(grid[r][c]);
                }
            }
        }
        
        // STEP 4: Initial constraint propagation
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (grid[r][c] != 0) {
                    if (!AdaptiveSolver.propagateValue(grid, domains, r, c, grid[r][c])) {
                        double timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
                        return new SolverResult(false, puzzle, timeMs, N);
                    }
                }
            }
        }
        
        // STEP 5: Apply constraint propagation techniques
        if (!AdaptiveSolver.applyNakedAndHiddenSingles(grid, domains)) {
            double timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
            return new SolverResult(false, puzzle, timeMs, N);
        }
        
        // STEP 6: Backtracking if needed
        boolean solved = backtrackSolve(grid, domains);
        
        double timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return new SolverResult(solved, solved ? grid : puzzle, timeMs, N);
    }
    
    /**
     * OPTIMIZED BACKTRACKING with MCV and constraint propagation
     */
    private static boolean backtrackSolve(int[][] grid, BitSet[][] domains) {
        // Find most constrained variable
        int bestRow = -1, bestCol = -1;
        int minDomain = Integer.MAX_VALUE;
        
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid.length; c++) {
                if (grid[r][c] == 0) {
                    int domainSize = domains[r][c].cardinality();
                    if (domainSize == 0) {
                        return false; // Conflict
                    }
                    if (domainSize < minDomain) {
                        minDomain = domainSize;
                        bestRow = r;
                        bestCol = c;
                    }
                }
            }
        }
        
        if (bestRow == -1) {
            return true; // Solved
        }
        
        // Try values in domain
        BitSet currentDomain = (BitSet) domains[bestRow][bestCol].clone();
        
        for (int val = currentDomain.nextSetBit(1); val >= 0; val = currentDomain.nextSetBit(val + 1)) {
            // Save state
            int[][] gridBackup = deepCopy(grid);
            BitSet[][] domainsBackup = deepCopyDomains(domains);
            
            // Make assignment
            grid[bestRow][bestCol] = val;
            
            // Propagate
            if (AdaptiveSolver.propagateValue(grid, domains, bestRow, bestCol, val) &&
                AdaptiveSolver.applyNakedAndHiddenSingles(grid, domains) &&
                backtrackSolve(grid, domains)) {
                return true;
            }
            
            // Restore state
            restoreState(grid, domains, gridBackup, domainsBackup);
        }
        
        return false;
    }
    
    // Utility methods
    private static int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
    
    private static BitSet[][] deepCopyDomains(BitSet[][] original) {
        BitSet[][] copy = new BitSet[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = (BitSet) original[i][j].clone();
            }
        }
        return copy;
    }
    
    private static void restoreState(int[][] grid, BitSet[][] domains, int[][] gridBackup, BitSet[][] domainsBackup) {
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(gridBackup[i], 0, grid[i], 0, grid[i].length);
            for (int j = 0; j < domains[i].length; j++) {
                domains[i][j] = (BitSet) domainsBackup[i][j].clone();
            }
        }
    }
}
