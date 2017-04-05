package puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.*;

public class Solver {

    private final SearchNode initialNode;

    private final SearchNode initialNodeTwin;

    private SearchNode endNode;

    private final Comparator<SearchNode> comp;

    private Iterable<Board> solution;

    private int moves;

    private boolean solvable;

    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException("passed argument equal to NULL");
        this.initialNode = new SearchNode(initial, null);
        this.initialNodeTwin = new SearchNode(initialNode.board.twin(), null);
        this.comp = new PuzzleComparator();
        solve();
    }

    private class SearchNode {
        private Board board;
        private SearchNode parent;
        private int manhattan;
        private int hamming;
        private int moves;

        private SearchNode(Board board, SearchNode parent) {
            this.board = board;
            this.parent = parent;
            this.moves = (parent == null) ? 0 : parent.moves + 1;
            this.manhattan = board.manhattan();
            this.hamming = board.hamming();
        }
    }

    private static class PuzzleComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode o1, SearchNode o2) {
            int thisPriority = o1.manhattan + o1.moves;
            int thatPriority = o2.manhattan + o2.moves;
            int result = thisPriority - thatPriority;
            if (result == 0)
                result = o1.manhattan - o2.manhattan;
            if (result == 0)
                result = o1.hamming - o2.hamming;
            return result;
        }
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return moves;
    }

    public Iterable<Board> solution() {
        if (this.solution == null && solvable)
            this.solution = ParentIterator::new;
        return solution;
    }

    private class ParentIterator implements Iterator<Board> {
        private List<SearchNode> steps;
        private int current;
        private int size;

        ParentIterator() {
            this.steps = getAllSteps();
            this.size = steps.size();
        }

        private List<SearchNode> getAllSteps() {
            List<SearchNode> result = new ArrayList<>();
            SearchNode previous = endNode;
            result.add(previous);
            while ((previous = previous.parent) != null)
                result.add(previous);
            return result;
        }

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public Board next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return steps.get(size - 1 - current++).board;
        }
    }

    private void solve() {
        SearchNode node = initialNode;
        SearchNode nodeTwin = initialNodeTwin;
        MinPQ<SearchNode> pq = new MinPQ<>(comp);
        MinPQ<SearchNode> pqTwin = new MinPQ<>(comp);
        while (!isEnd(node) && !isEnd(nodeTwin)) {
            addAllNeighbor(node, pq);
            node = retrieveMin(pq);
            addAllNeighbor(nodeTwin, pqTwin);
            nodeTwin = retrieveMin(pqTwin);
        }
        if (isEnd(nodeTwin)) {
            markAsUnsolvable();
            return;
        }
        markAsSolvable(node);
    }

    private void addAllNeighbor(SearchNode node, MinPQ<SearchNode> queue) {
        Iterable<Board> neighbors = node.board.neighbors();
        for (Board board : neighbors) {
            if (checkForEqual(board, node)) continue;
            add(new SearchNode(board, node), queue);
        }
    }

    private boolean checkForEqual(Board board, SearchNode node) {

        return node.parent != null && board.equals(node.parent.board);
    }

    private void markAsSolvable(SearchNode node) {
        this.solvable = true;
        this.endNode = node;
        this.moves = endNode.moves;
    }

    private void markAsUnsolvable() {
        this.solvable = false;
        this.endNode = null;
        this.moves = -1;
    }

    private boolean isEnd(SearchNode node) {
        return node.board.isGoal();
    }

    private void add(SearchNode node, MinPQ<SearchNode> queue) {
        queue.insert(node);
    }

    private SearchNode retrieveMin(MinPQ<SearchNode> queue) {
        return queue.delMin();
    }

}
