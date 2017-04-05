package puzzle;

import java.util.*;

import static edu.princeton.cs.algs4.StdRandom.*;
import static edu.princeton.cs.algs4.StdRandom.uniform;

public class Board {

    private final int[][] blocks;

    private int hammingCount = -1;

    private int manhattanCount = -1;


    public Board(int[][] blocks) {
        this.blocks = getCopyBlocks(blocks);
        hamming();
        manhattan();
    }

    private int[][] getCopyBlocks(int[][] initBlocks) {
        int boardLength = initBlocks.length;
        int[][] copy = new int[boardLength][];
        for (int i = 0; i < boardLength; i++) {
            int[] col = new int[boardLength];
            System.arraycopy(initBlocks[i], 0, col, 0, boardLength);
            copy[i] = col;
        }
        return copy;
    }

    public int dimension() {
        return blocks.length;
    }

    public int hamming() {
        if (hammingCount != -1)
            return hammingCount;
        int wrong = 0;
        int size = dimension();
        int value;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                value = blocks[i][j];
                if (value == 0)
                    continue;
                if (value != getIndex(i, j))
                    wrong++;
            }
        }
        this.hammingCount = wrong;
        return hammingCount;
    }

    private int getIndex(int row, int col) {
        return dimension() * row + col + 1;
    }


    public int manhattan() {
        if (manhattanCount != -1) {
            return manhattanCount;
        }
        int distance = 0;
        int size = dimension();
        int value;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                value = blocks[i][j];
                if (value == 0)
                    continue;
                distance += getDistanceToCorrectPosition(value, i, j);
            }
        }
        this.manhattanCount = distance;
        return manhattanCount;
    }

    private int getDistanceToCorrectPosition(int value, int currentRow, int currentCol) {
        int correctRow = getCorrectIndexRow(value);
        int correctCol = getCorrectIndexCol(value);
        return Math.abs(currentRow - correctRow) + Math.abs(currentCol - correctCol);

    }

    private int getCorrectIndexRow(int value) {
        return ((int) Math.ceil((double) value / dimension()) - 1);

    }

    private int getCorrectIndexCol(int value) {
        int size = dimension();
        int mod = value % size;
        return mod == 0 ? size - 1 : mod - 1;
    }

    public boolean isGoal() {
        return manhattan() == 0;
    }

    public Board twin() {
        int[][] copy = getCopyBlocks(blocks);
        swapRandom(copy);
        return new Board(copy);
    }

    private void swap(int[][] deepArr, int[] first, int[] second) {
        int temp = deepArr[first[0]][first[1]];
        deepArr[first[0]][first[1]] = deepArr[second[0]][second[1]];
        deepArr[second[0]][second[1]] = temp;
    }

    private void swapRandom(int[][] deepArr) {
        int size = dimension();
        int firstValue;
        int secondValue;
        int[] firstCoordinates;
        int[] secondCoordinates;
        while (true) {
            int firstRandomRow = uniform(size);
            int firstRandomCol = uniform(size);
            firstValue = blocks[firstRandomRow][firstRandomCol];
            if (firstValue != 0) {
                firstCoordinates = new int[]{firstRandomRow, firstRandomCol};
                break;
            }
        }
        while (true) {
            int secondRandomRow = uniform(size);
            int secondRandomCol = uniform(size);
            secondValue = blocks[secondRandomRow][secondRandomCol];
            if (secondValue != 0 && secondValue != firstValue) {
                secondCoordinates = new int[]{secondRandomRow, secondRandomCol};
                break;
            }
        }
        swap(deepArr, firstCoordinates, secondCoordinates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return Arrays.deepEquals(blocks, board.blocks);

    }

    public Iterable<Board> neighbors() {
        return NeighborsIterator::new;
    }

    private class NeighborsIterator implements Iterator<Board> {
        private Board[] neighbors;

        private int current = 0;

        private int length;

        NeighborsIterator() {
            this.neighbors = getNeighbors(Board.this);
            this.length = neighbors.length;
        }

        @Override
        public boolean hasNext() {
            return current < length;
        }

        @Override
        public Board next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return neighbors[current++];
        }

        private Board[] getNeighbors(Board board) {
            List<Board> result = new ArrayList<>();
            int[][] mainBlocks = board.blocks;
            int[] blankCoordinates = getZeroCoordinates(mainBlocks);
            int[][] neighborsCoordinates = getNeighborCoordinates(blankCoordinates);
            for (int[] neighbor : neighborsCoordinates) {
                int[][] copy = getCopyBlocks(mainBlocks);
                swap(copy, blankCoordinates, neighbor);
                Board bro = new Board(copy);
                result.add(bro);
            }
            return result.toArray(new Board[result.size()]);
        }


        private int[][] getNeighborCoordinates(int[] blank) {
            List<int[]> result = new ArrayList<>();
            int[] top;
            if ((top = getTop(blank)).length != 0) result.add(top);
            int[] bottom;
            if ((bottom = getBottom(blank)).length != 0) result.add(bottom);
            int[] left;
            if ((left = getLeft(blank)).length != 0) result.add(left);
            int[] right;
            if ((right = getRight(blank)).length != 0) result.add(right);
            return result.toArray(new int[result.size()][]);
        }

        private int[] getTop(int[] blank) {
            int topRow = blank[0] - 1;
            int topCol = blank[1];
            if (checkRange(topRow, topCol)) return new int[]{topRow, topCol};
            return new int[0];
        }

        private int[] getBottom(int[] blank) {
            int bottomRow = blank[0] + 1;
            int bottomCol = blank[1];
            if (checkRange(bottomRow, bottomCol)) return new int[]{bottomRow, bottomCol};
            return new int[0];
        }

        private int[] getLeft(int[] blank) {
            int leftRow = blank[0];
            int leftCol = blank[1] - 1;
            if (checkRange(leftRow, leftCol)) return new int[]{leftRow, leftCol};
            return new int[0];
        }

        private int[] getRight(int[] blank) {
            int rightRow = blank[0];
            int rightCol = blank[1] + 1;
            if (checkRange(rightRow, rightCol)) return new int[]{rightRow, rightCol};
            return new int[0];
        }

        private boolean checkRange(int i, int j) {
            int size = dimension();
            return !(i < 0 || i > (size - 1)) && !(j < 0 || j > (size - 1));
        }

        private int[] getZeroCoordinates(int[][] quadraticArr) {
            int size = quadraticArr.length;
            int value;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    value = quadraticArr[i][j];
                    if (value == 0)
                        return new int[]{i, j};
                }
            }
            throw new IllegalArgumentException("blocks not have blank value");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int size = dimension();
        sb.append(size).append("\n");
        int value;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                value = blocks[i][j];
                sb.append(" ").append(value);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Board board = new Board(new int[][]{{8, 1, 3}, {4, 6, 2}, {7, 5, 0}});
        System.out.println(board);
        Board board1 = new Board(new int[][]{{8, 1, 3}, {4, 6, 2}, {7, 5, 0}});
//        puzzle.Board board1 = board.twin();
        System.out.println(board1);
        System.out.println(board.hashCode() + "===" + board1.hashCode());
    }
}
