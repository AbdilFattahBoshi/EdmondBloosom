/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/* One final piece of information that's necessary to get the algorithm
     * working is the ability to locate and navigate odd alternating cycles in
     * the alternating forest.  Once we've found such a cycle, we need to
     * contract it down to a single node, find an alternating path in the
     * contracted graph, then expand the path back into an alternating path in
     * the original graph.  To do this, we need to be able to answer the
     * following questions efficiently:
     *
     * 1. What nodes are in the cycle (blossom)?
     * 2. Starting at the root of the blossom, what order do the edges go in?
     * 3. What node is used to represent the cycle in the contracted graph?
     * 
     * This information is encoded in this utility struct.
 */
public class Blossom implements Serializable {

    public final Node root;// The root of the blossom; also the representative
    public final List<Node> cycle;// The nodes, listed in order around the cycle
    public final Set<Node> nodes; // The nodes, stored in a set for efficient lookup

    /**
     * Given information about the blossom, constructs a new Blossom holding
     * this information.
     *
     * @param root The root node of the blossom.
     * @param cycle The nodes of the cycle listed in the order in which they
     * appear in the blossom.
     * @param nodes The nodes in the cycle, stored as a set.
     */
    public Blossom(Node root, List<Node> cycle, Set<Node> nodes) {
        this.root = root;
        this.cycle = cycle;
        this.nodes = nodes;
    }

}
