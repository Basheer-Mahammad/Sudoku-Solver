import java.util.*;

/**
 * Flexible Sudoku Solver - Generic Java Version for 4x4, 9x9, 16x16
 * Uses generics and optimized algorithms for different grid sizes
 */
public class FlexibleSudoku {
    
    /**
     * Generic Grid class that can handle NxN Sudoku puzzles
     */
    public static class Grid {
        private final int N;
        private final int BOX_SIZE;
        private final int[][] grid;
        private final Set<String> preFilledCoords;
        
        // OPTIMIZATION: Cached constraint arrays for faster validation
        private final boolean[][] rowUsed;
        private final boolean[][] colUsed;
        private final boolean[][] boxUsed;
        
        // OPTIMIZATION: Reusable list to avoid garbage collection
        private final List<Integer> reusablePossibleValues;
        
        public Grid(int size) {
            this.N = size;
            this.BOX_SIZE = (int) Math.sqrt(size);
            
            if (BOX_SIZE * BOX_SIZE != N) {
                throw new IllegalArgumentException("Grid size must be a perfect square");
            }
            
            this.grid = new int[N][N];
            this.preFilledCoords = new HashSet<>();
            
            // Initialize constraint tracking arrays
            this.rowUsed = new boolean[N][N + 1];
            this.colUsed = new boolean[N][N + 1];
            this.boxUsed = new boolean[N][N + 1];
            
            // Initialize reusable list
            this.reusablePossibleValues = new ArrayList<>(N);
        }
        
        public Grid(int[][] inputGrid) {
            this(inputGrid.length);
            setInitialState(inputGrid);
        }
        
        /**
         * Set initial state and update constraint tracking
         */
        public void setInitialState(int[][] inputGrid) {
            if (inputGrid.length != N || inputGrid[0].length != N) {
                throw new IllegalArgumentException("Grid must be " + N + "x" + N);
            }
            
            preFilledCoords.clear();
            
            // Clear constraint arrays
            for (int i = 0; i < N; i++) {
                Arrays.fill(rowUsed[i], false);
                Arrays.fill(colUsed[i], false);
                Arrays.fill(boxUsed[i], false);
            }
            
            // Copy grid and track constraints
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    int value = inputGrid[row][col];
                    grid[row][col] = value;
                    
                    if (value != 0) {
                        preFilledCoords.add(row + "," + col);
                        updateConstraints(row, col, value, true);
                    }
                }
            }
        }
        
        /**
         * OPTIMIZATION: Update constraint tracking arrays
         */
        private void updateConstraints(int row, int col, int value, boolean used) {
            int box = getBoxIndex(row, col);
            rowUsed[row][value] = used;
            colUsed[col][value] = used;
            boxUsed[box][value] = used;
        }
        
        /**
         * Get box index for given coordinates
         */
        private int getBoxIndex(int row, int col) {
            return (row / BOX_SIZE) * BOX_SIZE + (col / BOX_SIZE);
        }
        
        /**
         * Check if a value can be placed at given coordinates
         */
        public boolean isValidPlacement(int row, int col, int value) {
            if (value < 1 || value > N) return false;
            
            int box = getBoxIndex(row, col);
            return !rowUsed[row][value] && !colUsed[col][value] && !boxUsed[box][value];
        }
        
        /**
         * Get all possible values for a cell - OPTIMIZED with reusable list
         */
        public List<Integer> getPossibleValues(int row, int col) {
            reusablePossibleValues.clear(); // Reuse list to avoid garbage collection
            
            for (int val = 1; val <= N; val++) {
                if (isValidPlacement(row, col, val)) {
                    reusablePossibleValues.add(val);
                }
            }
            
            return reusablePossibleValues;
        }
        
        /**
         * Update cell with new value
         */
        public void update(int row, int col, int value) {
            if (grid[row][col] != 0) {
                updateConstraints(row, col, grid[row][col], false);
            }
            
            grid[row][col] = value;
            
            if (value != 0) {
                updateConstraints(row, col, value, true);
            }
        }
        
        /**
         * Clear cell value
         */
        public void clear(int row, int col) {
            if (grid[row][col] != 0) {
                updateConstraints(row, col, grid[row][col], false);
                grid[row][col] = 0;
            }
        }
        
        /**
         * Check if coordinate was pre-filled
         */
        public boolean wasPreFilled(int row, int col) {
            return preFilledCoords.contains(row + "," + col);
        }
        
        /**
         * Get next coordinate in row-major order
         */
        public int[] getNextCoord(int row, int col) {
            col++;
            if (col >= N) {
                col = 0;
                row++;
            }
            return new int[]{row, col};
        }
        
        /**
         * Check if all cells are filled
         */
        public boolean isComplete() {
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    if (grid[row][col] == 0) return false;
                }
            }
            return true;
        }
        
        /**
         * Get grid size
         */
        public int getSize() {
            return N;
        }
        
        /**
         * Get box size
         */
        public int getBoxSize() {
            return BOX_SIZE;
        }
        
        /**
         * Get value at coordinates
         */
        public int getValue(int row, int col) {
            return grid[row][col];
        }
        
        /**
         * Get copy of the grid
         */
        public int[][] getGrid() {
            int[][] copy = new int[N][N];
            for (int i = 0; i < N; i++) {
                System.arraycopy(grid[i], 0, copy[i], 0, N);
            }
            return copy;
        }
        
        /**
         * Print grid in a formatted way
         */
        public void printGrid() {
            System.out.println("\n" + N + "x" + N + " Sudoku Grid:");
            System.out.println("=" .repeat(N * 3 + BOX_SIZE + 1));
            
            for (int row = 0; row < N; row++) {
                if (row > 0 && row % BOX_SIZE == 0) {
                    System.out.println("-".repeat(N * 3 + BOX_SIZE + 1));
                }
                
                for (int col = 0; col < N; col++) {
                    if (col > 0 && col % BOX_SIZE == 0) {
                        System.out.print("| ");
                    }
                    
                    if (grid[row][col] == 0) {
                        System.out.printf("%2s ", ".");
                    } else {
                        System.out.printf("%2d ", grid[row][col]);
                    }
                }
                System.out.println();
            }
            System.out.println("=" .repeat(N * 3 + BOX_SIZE + 1));
        }
    }
    
    /**
     * OPTIMIZATION: Most Constrained Variable heuristic - find best empty cell
     */
    public static int[] findBestEmptyCell(Grid grid) {
        int[] bestCell = {-1, -1};
        int minCandidates = grid.getSize() + 1;
        
        for (int row = 0; row < grid.getSize(); row++) {
            for (int col = 0; col < grid.getSize(); col++) {
                if (grid.getValue(row, col) == 0) {
                    List<Integer> candidates = grid.getPossibleValues(row, col);
                    
                    if (candidates.size() < minCandidates) {
                        minCandidates = candidates.size();
                        bestCell[0] = row;
                        bestCell[1] = col;
                        
                        if (candidates.size() == 0) return bestCell; // Dead end
                        if (candidates.size() == 1) return bestCell; // Forced move
                    }
                }
            }
        }
        
        return bestCell;
    }
    
    /**
     * Optimized recursive solver using MCV heuristic
     */
    public static boolean solveRecursive(Grid grid) {
        // Find the most constrained empty cell
        int[] bestCell = findBestEmptyCell(grid);
        
        if (bestCell[0] == -1) {
            return true; // No empty cells - solved!
        }
        
        int row = bestCell[0];
        int col = bestCell[1];
        
        List<Integer> possibleValues = grid.getPossibleValues(row, col);
        if (possibleValues.isEmpty()) {
            return false; // No valid values - backtrack
        }
        
        // Try each possible value
        for (int value : possibleValues) {
            grid.update(row, col, value);
            
            if (solveRecursive(grid)) {
                return true;
            }
            
            grid.clear(row, col);
        }
        
        return false;
    }
    
    /**
     * Main solve method with timing
     */
    public static SolverResult solve(int[][] inputGrid) {
        long startTime = System.nanoTime();
        
        try {
            Grid grid = new Grid(inputGrid);
            boolean solved = solveRecursive(grid);
            
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            
            if (solved) {
                return new SolverResult(true, grid.getGrid(), elapsedMs);
            } else {
                return new SolverResult(false, null, elapsedMs);
            }
        } catch (Exception e) {
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            return new SolverResult(false, null, elapsedMs);
        }
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
