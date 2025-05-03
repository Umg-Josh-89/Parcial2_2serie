package TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class TreeVertex {
    int data;
    TreeVertex leftBranch;
    TreeVertex rightBranch;
    
    TreeVertex() {}
    TreeVertex(int data) { this.data = data; }
    TreeVertex(int data, TreeVertex leftBranch, TreeVertex rightBranch) {
        this.data = data;
        this.leftBranch = leftBranch;
        this.rightBranch = rightBranch;
    }
}

class RootToLeafPathAnalyzer {
    private TreeVertex graphOrigin;
    
    public RootToLeafPathAnalyzer(TreeVertex graphOrigin) {
        this.graphOrigin = graphOrigin;
    }
    
    /**
     * Discovers all origin-to-terminal paths equaling the required total.
     * @param desiredTotal The needed summation for each path
     * @return Collection of qualifying paths
     */
    public List<List<Integer>> discoverValidPaths(int desiredTotal) {
        List<List<Integer>> solutionSet = new ArrayList<>();
        if (graphOrigin == null) {
            return solutionSet;
        }
        
        traversePaths(graphOrigin, desiredTotal, new LinkedList<>(), solutionSet);
        return solutionSet;
    }
    
    /**
     * Depth-first traversal to identify qualifying paths.
     * @param currentVertex Node being examined
     * @param remainingTotal Outstanding amount needed
     * @param pathSequence Current traversal sequence
     * @param solutionSet Storage for valid paths
     */
    private void traversePaths(TreeVertex currentVertex, int remainingTotal, 
                    LinkedList<Integer> pathSequence, 
                    List<List<Integer>> solutionSet) {
        if (currentVertex == null) {
            return;
        }
        
        pathSequence.add(currentVertex.data);
        remainingTotal -= currentVertex.data;
        
        // Verify terminal vertex with exact total
        if (currentVertex.leftBranch == null && currentVertex.rightBranch == null && remainingTotal == 0) {
            solutionSet.add(new ArrayList<>(pathSequence));
        }
        
        // Investigate both branches
        traversePaths(currentVertex.leftBranch, remainingTotal, pathSequence, solutionSet);
        traversePaths(currentVertex.rightBranch, remainingTotal, pathSequence, solutionSet);
        
        // Remove last element for backtracking
        pathSequence.removeLast();
    }
}

class HierarchicalTreeAssembler {
    /**
     * Creates binary structure from tiered value sequence.
     * @param tieredValues Sequence of values (null indicates absence)
     * @return Foundation vertex of constructed structure
     */
    public static TreeVertex createFromTieredSequence(Integer[] tieredValues) {
        if (tieredValues == null || tieredValues.length == 0 || tieredValues[0] == null) {
            return null;
        }
        
        TreeVertex foundation = new TreeVertex(tieredValues[0]);
        Queue<TreeVertex> processingQueue = new LinkedList<>();
        processingQueue.offer(foundation);
        
        int position = 1;
        while (!processingQueue.isEmpty() && position < tieredValues.length) {
            TreeVertex parentNode = processingQueue.poll();
            
            if (position < tieredValues.length && tieredValues[position] != null) {
                parentNode.leftBranch = new TreeVertex(tieredValues[position]);
                processingQueue.offer(parentNode.leftBranch);
            }
            position++;
            
            if (position < tieredValues.length && tieredValues[position] != null) {
                parentNode.rightBranch = new TreeVertex(tieredValues[position]);
                processingQueue.offer(parentNode.rightBranch);
            }
            position++;
        }
        
        return foundation;
    }
}

public class PathSumDemonstration {
    public static void main(String[] args) {
        // Construct the hierarchical structure
        Integer[] vertexData = {5,4,8,11,null,13,4,7,2,null,null,5,1};
        int targetCalculation = 22;
        
        TreeVertex baseNode = HierarchicalTreeAssembler.createFromTieredSequence(vertexData);
        RootToLeafPathAnalyzer pathProcessor = new RootToLeafPathAnalyzer(baseNode);
        List<List<Integer>> validRoutes = pathProcessor.discoverValidPaths(targetCalculation);
        
        System.out.println("Valid path sequences summing to " + targetCalculation + ": " + validRoutes);
    }
}