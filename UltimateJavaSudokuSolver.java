import java.util.*;
import java.io.*;

public class UltimateJavaSudokuSolver {
    private static Scanner scanner = new Scanner(System.in);
    
    // Safe input function to handle EOF and invalid input
    private static int safeInput() {
        while (true) {
            try {
                if (scanner.hasNextInt()) {
                    return scanner.nextInt();
                } else {
                    System.out.print("Invalid input! Please enter a valid number: ");
                    scanner.next(); // consume invalid input
                }
            } catch (Exception e) {
                System.out.println("\nEOF detected. Exiting program.");
                System.exit(0);
            }
        }
    }
    
    // Input grid from user
    private static int[][] inputGrid(int size) {
        int[][] grid = new int[size][size];
        
        System.out.println("Enter the " + size + "x" + size + " Sudoku puzzle:");
        System.out.println("Use 0 for empty cells, values 1-" + size + " for filled cells");
        System.out.println("Enter row by row (space-separated):\n");
        
        for (int i = 0; i < size; i++) {
            System.out.print("Row " + (i + 1) + ": ");
            for (int j = 0; j < size; j++) {
                int value;
                while (true) {
                    try {
                        value = scanner.nextInt();
                        if (value >= 0 && value <= size) {
                            grid[i][j] = value;
                            break;
                        } else {
                            System.out.print("Invalid value! Enter 0-" + size + ": ");
                        }
                    } catch (Exception e) {
                        System.out.println("\nEOF detected. Exiting program.");
                        System.exit(0);
                    }
                }
            }
        }
        
        return grid;
    }
    
    // Load sample puzzle based on size
    private static int[][] getSamplePuzzle(int size) {
        switch (size) {
            case 4:
                return new int[][] {
                    {1, 0, 0, 0},
                    {0, 0, 0, 3},
                    {0, 0, 0, 0},
                    {0, 4, 0, 0}
                };
            case 9:
                return new int[][] {
                    {5, 3, 0, 0, 7, 0, 0, 0, 0},
                    {6, 0, 0, 1, 9, 5, 0, 0, 0},
                    {0, 9, 8, 0, 0, 0, 0, 6, 0},
                    {8, 0, 0, 0, 6, 0, 0, 0, 3},
                    {4, 0, 0, 8, 0, 3, 0, 0, 1},
                    {7, 0, 0, 0, 2, 0, 0, 0, 6},
                    {0, 6, 0, 0, 0, 0, 2, 8, 0},
                    {0, 0, 0, 4, 1, 9, 0, 0, 5},
                    {0, 0, 0, 0, 8, 0, 0, 7, 9}
                };
            case 16: {
                int[][] puzzle = new int[16][16];
                // Create a basic 16x16 pattern
                puzzle[0][0] = 1; puzzle[0][4] = 2; puzzle[0][8] = 3; puzzle[0][12] = 4;
                puzzle[4][1] = 5; puzzle[4][5] = 6; puzzle[4][9] = 7; puzzle[4][13] = 8;
                puzzle[8][2] = 9; puzzle[8][6] = 10; puzzle[8][10] = 11; puzzle[8][14] = 12;
                puzzle[12][3] = 13; puzzle[12][7] = 14; puzzle[12][11] = 15; puzzle[12][15] = 16;
                return puzzle;
            }
            case 25: {
                int[][] puzzle = new int[25][25];
                // Create a minimal 25x25 puzzle
                puzzle[0][0] = 1; puzzle[0][5] = 6; puzzle[0][10] = 11; puzzle[0][15] = 16; puzzle[0][20] = 21;
                puzzle[5][1] = 7; puzzle[5][6] = 12; puzzle[5][11] = 17; puzzle[5][16] = 22; puzzle[5][21] = 2;
                puzzle[10][2] = 13; puzzle[10][7] = 18; puzzle[10][12] = 23; puzzle[10][17] = 3; puzzle[10][22] = 8;
                puzzle[15][3] = 19; puzzle[15][8] = 24; puzzle[15][13] = 4; puzzle[15][18] = 9; puzzle[15][23] = 14;
                puzzle[20][4] = 25; puzzle[20][9] = 5; puzzle[20][14] = 10; puzzle[20][19] = 15; puzzle[20][24] = 20;
                return puzzle;
            }
        }
        return null;
    }
    
    // Load from file
    private static int[][] loadFromFile(int size) {
        System.out.print("Enter filename: ");
        String filename = scanner.next();
        
        try {
            File file = new File(filename);
            Scanner fileScanner = new Scanner(file);
            int[][] grid = new int[size][size];
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (fileScanner.hasNextInt()) {
                        grid[i][j] = fileScanner.nextInt();
                    } else {
                        System.out.println("Error: File format incorrect or incomplete.");
                        fileScanner.close();
                        return null;
                    }
                }
            }
            
            fileScanner.close();
            System.out.println("Puzzle loaded successfully!");
            return grid;
        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + filename + "' not found.");
            return null;
        }
    }
    
    // Display menu for input method
    private static int getInputMethod() {
        System.out.println("\nChoose input method:");
        System.out.println("1. Manual input");
        System.out.println("2. Load sample puzzle");
        System.out.println("3. Load from file");
        System.out.print("Enter choice (1-3): ");
        return safeInput();
    }
    
    // Clean grid printing with proper formatting
    private static void printSolution(int[][] grid, int size) {
        if (size > 16) {
            // For large grids (25x25), use compact format
            printCompactGrid(grid, size);
        } else {
            // For smaller grids, use detailed format
            printDetailedGrid(grid, size);
        }
    }
    
    // Compact format for large grids
    private static void printCompactGrid(int[][] grid, int size) {
        System.out.println("Solution:");
        int subgridSize = 5; // 25x25 has 5x5 subgrids
        
        // Top border
        System.out.print("+");
        for (int col = 0; col < size; col++) {
            System.out.print("---");
            if ((col + 1) % subgridSize == 0) {
                System.out.print("+");
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
        
        // Grid content with borders
        for (int row = 0; row < size; row++) {
            System.out.print("|");
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 0) {
                    System.out.print(" . ");
                } else {
                    System.out.printf("%2d ", grid[row][col]);
                }
                if ((col + 1) % subgridSize == 0) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
            
            // Horizontal separator after every 5 rows
            if ((row + 1) % subgridSize == 0 && row + 1 < size) {
                System.out.print("+");
                for (int col = 0; col < size; col++) {
                    System.out.print("---");
                    if ((col + 1) % subgridSize == 0) {
                        System.out.print("+");
                    } else {
                        System.out.print("-");
                    }
                }
                System.out.println();
            }
        }
        
        // Bottom border
        System.out.print("+");
        for (int col = 0; col < size; col++) {
            System.out.print("---");
            if ((col + 1) % subgridSize == 0) {
                System.out.print("+");
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
    }
    
    // Detailed format for smaller grids
    private static void printDetailedGrid(int[][] grid, int size) {
        int subgridSize = (int) Math.sqrt(size);
        
        // Top border
        System.out.print("+");
        for (int col = 0; col < size; col++) {
            System.out.print("---");
            if ((col + 1) % subgridSize == 0) {
                System.out.print("+");
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
        
        // Grid content
        for (int row = 0; row < size; row++) {
            System.out.print("|");
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 0) {
                    System.out.print(" . ");
                } else {
                    System.out.printf("%2d ", grid[row][col]);
                }
                if ((col + 1) % subgridSize == 0) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
            
            // Horizontal separator
            if ((row + 1) % subgridSize == 0 && row + 1 < size) {
                System.out.print("+");
                for (int col = 0; col < size; col++) {
                    System.out.print("---");
                    if ((col + 1) % subgridSize == 0) {
                        System.out.print("+");
                    } else {
                        System.out.print("-");
                    }
                }
                System.out.println();
            }
        }
        
        // Bottom border
        System.out.print("+");
        for (int col = 0; col < size; col++) {
            System.out.print("---");
            if ((col + 1) % subgridSize == 0) {
                System.out.print("+");
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
    }
    
    // Solve with timing
    private static boolean solveWithTiming(int[][] puzzle, int size) {
        System.out.print("\n[>] Solving... ");
        
        long startTime = System.nanoTime();
        boolean solved = false;
        
        if (size == 25) {
            // Use CSP Solver for 25x25 - much faster and more reliable
            CSPSudokuSolver.SolverResult result = CSPSudokuSolver.solve(puzzle);
            solved = result.solved;
            if (solved) {
                // Copy solution back to puzzle
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        puzzle[i][j] = result.grid[i][j];
                    }
                }
            }
        } else {
            // Use CSP Solver for all sizes - it's fast and reliable
            CSPSudokuSolver.SolverResult result = CSPSudokuSolver.solve(puzzle);
            solved = result.solved;
            if (solved) {
                // Copy solution back to puzzle
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        puzzle[i][j] = result.grid[i][j];
                    }
                }
            }
        }
        
        double timeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        
        if (solved) {
            System.out.printf("[OK] SOLVED in %.2f ms!\n\n", timeMs);
            printSolution(puzzle, size);
            return true;
        } else {
            System.out.printf("[X] NO SOLUTION found (%.2f ms)\n", timeMs);
            return false;
        }
    }
    
    // Generic Sudoku solver for sizes other than 25x25
    private static boolean solveSudoku(int[][] grid, int size) {
        int subgridSize = (int) Math.sqrt(size);
        
        // Find empty cell
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 0) {
                    // Try numbers 1 to size
                    for (int num = 1; num <= size; num++) {
                        if (isValid(grid, row, col, num, size, subgridSize)) {
                            grid[row][col] = num;
                            
                            if (solveSudoku(grid, size)) {
                                return true;
                            }
                            
                            grid[row][col] = 0; // Backtrack
                        }
                    }
                    return false;
                }
            }
        }
        return true; // Grid is complete
    }
    
    // Validate placement
    private static boolean isValid(int[][] grid, int row, int col, int num, int size, int subgridSize) {
        // Check row
        for (int j = 0; j < size; j++) {
            if (grid[row][j] == num) return false;
        }
        
        // Check column
        for (int i = 0; i < size; i++) {
            if (grid[i][col] == num) return false;
        }
        
        // Check subgrid
        int startRow = (row / subgridSize) * subgridSize;
        int startCol = (col / subgridSize) * subgridSize;
        for (int i = startRow; i < startRow + subgridSize; i++) {
            for (int j = startCol; j < startCol + subgridSize; j++) {
                if (grid[i][j] == num) return false;
            }
        }
        
        return true;
    }
    
    // Main solver function for a specific size
    private static void solveSudokuSize(int size) {
        System.out.println("\n=== " + size + "x" + size + " SUDOKU SOLVER ===");
        
        int inputMethod = getInputMethod();
        int[][] puzzle = null;
        
        switch (inputMethod) {
            case 1:
                puzzle = inputGrid(size);
                break;
            case 2:
                puzzle = getSamplePuzzle(size);
                if (puzzle == null) {
                    System.out.println("No sample puzzle available for " + size + "x" + size);
                    return;
                }
                System.out.println("Sample puzzle loaded!");
                break;
            case 3:
                puzzle = loadFromFile(size);
                if (puzzle == null) {
                    return; // Error already reported
                }
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        
        System.out.println("\nInitial puzzle:");
        printSolution(puzzle, size);
        System.out.println();
        
        // Solve with timing
        if (solveWithTiming(puzzle, size)) {
            System.out.println("\n[SUCCESS] Puzzle solved successfully!");
            
            if (size == 25) {
                System.out.println("\n[ACHIEVEMENT] Congratulations! You've solved a 25x25 Sudoku!");
                System.out.println("This is one of the most challenging Sudoku sizes!");
            }
        } else {
            System.out.println("\n[X] No solution exists for this puzzle.");
            System.out.println("Please check your input for conflicts.");
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== ULTIMATE OPTIMIZED SUDOKU SOLVER ===");
        System.out.println("Supports: 4x4, 9x9, 16x16, and 25x25 Sudoku puzzles");
        System.out.println("Features: Ultra-fast algorithms, beautiful output, multiple input methods");
        System.out.println("============================================================");
        
        while (true) {
            System.out.println("\nChoose grid size:");
            System.out.println("4  - 4x4 Sudoku (Instant)");
            System.out.println("9  - 9x9 Sudoku (Classic)");
            System.out.println("16 - 16x16 Sudoku (Advanced)");
            System.out.println("25 - 25x25 Sudoku (Ultra Challenge)");
            System.out.println("0  - Exit");
            System.out.print("\nEnter grid size: ");
            
            int size = safeInput();
            
            switch (size) {
                case 0:
                    System.out.println("Thank you for using the Ultimate Sudoku Solver!");
                    return;
                case 4:
                case 9:
                case 16:
                case 25:
                    solveSudokuSize(size);
                    break;
                default:
                    System.out.println("Invalid size! Please choose 4, 9, 16, 25, or 0 to exit.");
            }
            
            System.out.println("\n------------------------------------------------------------");
        }
    }
}
