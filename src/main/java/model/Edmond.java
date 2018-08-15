/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class Edmond implements Serializable {

    /**
     * Given an graph, returns a graph containing the edges of a
     * maximum matching in that graph.
     *
     * @param graph The graph in which a maximum matching should be found.
     * @return A graph containing a maximum matching in that graph.
     */
    public static Graph EdmondBloss(Graph graph, Graph maxMatch) {

        /* Edge case - if the graph is empty, just hand back an empty graph as
         * the matching.
         */
        if (graph.Nodes.isEmpty()) {
            return new Graph();
        }

        /* Construct a new graph to hold the matching.  Fill it with the nodes
         * from the original graph, but not the edges.
         */
        if (maxMatch == null) {
            maxMatch = new Graph();
            for (Node node : graph.Nodes) {
                maxMatch.Nodes.add(node);
            }
        }
         /* Now, continuously iterate, looking for alternating paths between
         * exposed vertices until none can be found.
         */
        while (true) {
            List<Node> path = findAlternatingPath(graph, maxMatch);
            if (path == null) {
                return maxMatch;
            }
            updateMatching(path, maxMatch);
        }

    }

    /*
     *
     * Updates a matching by increasing the cardinality of the matching by
     * flipping the status of the edges in the specified alternating path. It is
     * assumed that the alternating path is between exposed vertices, which
     * means that the edges [0, 1], [2, 3], ..., [n-2, n-1] are assumed not to
     * be in the matching.
     *
     * @param path The alternating path linking the exposed endpoints.
     * @param maxMatch The matching to update, which is an in/out parameter.
     */
    private static void updateMatching(List<Node> path, Graph maxMatch) {
        /* Scan across the edges in the path, flipping whether or not they're
         * in the matching.  This iteration counts up to the size of the list
         * minus one because each iteration looks at the edge (i, i + 1).
         */
        for (int i = 0; i < path.size() - 1; ++i) {
            /* Check whether the edge exists and react appropriately. */
            if (isEdgeExists(maxMatch, new Edge(path.get(i), path.get(i + 1)))) {
                removeEdge(maxMatch, path.get(i), path.get(i + 1));
            } else {
                maxMatch.Edges.add(new Edge(path.get(i), path.get(i + 1)));
            }
        }
    }

    /**
     * Given a graph and a matching in that graph, returns an augmenting path
     * in the graph if one exists.
     *
     * @param graph  The graph in which to search for the path.
     * @param mGraph A matching in that graph.
     * @return An alternating path in g, or null if none exists.
     */
    private static List<Node> findAlternatingPath(Graph graph, Graph mGraph) {
        /* We need to maintain as state all of the forests that are currently
         * being considered.  To do this, we'll create a map associating each
         * node with its information.
         */
        Map<Node, NodeInformation> forest = new HashMap<Node, NodeInformation>();
        /* We also will maintain a worklist of edges that need to be
         * considered.  These will be explored in a breadth-first fashion.
         * Whenever we add a new node to the forest, we'll add all its
         * outgoing edges to this queue.
         */
        Queue<Edge> worklist = new LinkedList<Edge>();
        /* Begin by adding all of the exposed vertices to the forest as their
         * own singleton trees.
         */
        for (Node node : graph.Nodes) {
            /* Check if the node is a singleton by seeing if it has no edges
             * leaving it in the matching.
             */
            if (!isNodeFree(mGraph, node)) {
                continue;
            }
            /* This node is an outer node that has no parent and belongs in
             * its own tree.
             */
            forest.put(node, new NodeInformation(null, node, true));
             /* Add to the worklist all edges leaving this node. */
            for (Node n : getAdjacentNodes(graph, node)) {
                worklist.add(new Edge(node, n));
            }
        }
        /* Now, we start growing all the trees outward by considering edges
         * leaving each tree.
         */
        while (!worklist.isEmpty()) {
            /* Grab the next edge.  We're only growing the tree along edges
             * that are not part of the matching (since we're looking to grow
             * trees with pairs of edges, a non-matched edge from an outer
             * node to an inner node, followed by a matched edge to some next
             * node.
             */
            Edge curr = worklist.remove();
            if (isEdgeExists(mGraph, curr)) {
                continue;
            }

            /* Look up the information associated with the endpoints. */
            NodeInformation startInfo = forest.get(curr.getStartNode());
            NodeInformation endInfo = forest.get(curr.getEndNode());

            /* We have several cases to consider.  First, if the endpoint of
             * this edge is in some tree, there are two options:
             *
             * 1. If both endpoints are outer nodes in the same tree, we have
             *    found an odd-length cycle (blossom).  We then contract the
             *    edges in the cycle, repeat the search in the contracted
             *    graph, then expand the result.
             * 2. If both endpoints are outer nodes in different trees, then
             *    we've found an augmenting path from the root of one tree
             *    down through the other.
             * 3. If one endpoint is an outer node and one is an inner node,
             *    we don't need to do anything.  The path that we would end
             *    up taking from the root of the first tree through this edge
             *    would not end up at the root of the other tree, since the
             *    only way we could do this while alternating would direct us
             *    away from the root.  We can just skip this edge.
             */
            if (endInfo != null) {
                /* Case 1: Do the contraction. */
                if (endInfo.isOuter && startInfo.treeRoot == endInfo.treeRoot) {
                    /* Get information about the blossom necessary to reduce
                     * the graph.
                     */
                    Blossom blossom = findBlossom(forest, curr);

                    /* Next, rebuild the graph using the indicated pseudonode,
                     * and recursively search it for an augmenting path.
                     */
                    List<Node> path = findAlternatingPath(contractGraph(graph, blossom), contractGraph(mGraph, blossom));

                    /* If no augmenting path exists, then no augmenting path
                     * exists in this graph either.
                     */
                    if (path == null) {
                        return path;
                    }

                    /* Otherwise, expand the path out into a path in this
                     * graph, then return it.
                     */
                    return expandPath(path, graph, forest, blossom);
                }
                /* Case 2: Return the augmenting path from root to root. */
                else if (endInfo.isOuter && startInfo.treeRoot != endInfo.treeRoot) {
                    /* The alternating path that we'll be building consists of
                     * the path from the root of the first tree to the first
                     * outer node, followed by the edge, followed by the path
                     * from the second outer node to its root.  Our path info
                     * is stored in a fashion that makes it easy to walk up
                     * to the root, and so we'll build up the path (which
                     * we'll represent by a deque) by walking up to the tree
                     * roots and creating the path as appropriate.
                     */
                    List<Node> result = new ArrayList<Node>();

                    /* Get the path from the first node to the root.  Note
                     * that this path is backwards, so we'll need to reverse
                     * it afterwards.
                     */
                    for (Node node = curr.getStartNode(); node != null; node = forest.get(node).parent) {
                        result.add(node);
                    }

                    /* Turn the path around. */
                    result = reversePath(result);

                    /* Path from edge end to its root. */
                    for (Node node = curr.getEndNode(); node != null; node = forest.get(node).parent) {
                        result.add(node);
                    }

                    return result;
                }
                /* Case 3 requires no processing. */
            }
            /* Otherwise, we have no info on this edge, which means that it
             * must correspond to a matched node (all exposed nodes are in a
             * forest).  We'll thus add that node to the tree containing the
             * start of the endpoint as an inner node, then add the node for
             * its endpoint to the tree as an outer node.
             */
            else {
                /* The endpoint has the edge start as a parent and the same
                 * root as its parent.
                 */
                forest.put(curr.getEndNode(), new NodeInformation(curr.getStartNode(), startInfo.treeRoot, false));

                /* Look up the unique edge that is matched to this node.  Its
                 * endpoint is the node that will become an outer node of this
                 * tree.
                 */
                Node endpoint = getAdjacentNodes(mGraph, curr.getEndNode()).iterator().next();
                forest.put(endpoint, new NodeInformation(curr.getEndNode(), startInfo.treeRoot, true));

                /* Add all outgoing edges from this endpoint to the work
                 * list.
                 */
                for (Node fringeNode : getAdjacentNodes(graph, endpoint)) {
                    worklist.add(new Edge(endpoint, fringeNode));
                }
            }
        }

        /* If we reach here, it means that we've constructed a maximum forest
         * without finding any augmenting paths.
         */
        return null;
    }

    public static boolean isNodeFree(Graph graph, Node node) {
        for (Edge edge : graph.Edges) {
            if ((edge.getStartNode().getName().equals(node.getName())) || (edge.getEndNode().getName().equals(node.getName()))) {
                return false;
            }
        }
        return true;
    }



    public static boolean isEdgeExists(Graph graph, Edge edge) {
        for (Edge e : graph.Edges) {
            if ((e.getStartNode().getName().equals(edge.getStartNode().getName()) && e.getEndNode().getName().equals(edge.getEndNode().getName()))
                    || (e.getStartNode().getName().equals(edge.getEndNode().getName()) && e.getEndNode().getName().equals(edge.getStartNode().getName()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a forest of alternating trees and an edge forming a blossom in one
     * of those trees, returns information about the blossom.
     *
     * @param forest The alternating forest.
     * @param edge   The edge that created a cycle.
     * @return A Blossom struct holding information about then blossom.
     */
    private static Blossom findBlossom(Map<Node, NodeInformation> forest, Edge edge) {
        /* We need to locate the root of the blossom, which is the lowest
         * common ancestor of the two nodes at the endpoint of each edge.  To
         * do this, we'll walk up from each node to the root, storing the
         * paths we find.  We will then look for the last node that's common
         * to both paths.
         */
        LinkedList<Node> onePath = new LinkedList<Node>(), twoPath = new LinkedList<Node>();
        for (Node node = edge.getStartNode(); node != null; node = forest.get(node).parent) {
            onePath.addFirst(node);
        }
        for (Node node = edge.getEndNode(); node != null; node = forest.get(node).parent) {
            twoPath.addFirst(node);
        }

        /* Now that we have that paths, continue walking forward in them until
         * we find a mismatch.
         */
        int mismatch = 0;
        for (; mismatch < onePath.size() && mismatch < twoPath.size(); ++mismatch) {
            if (onePath.get(mismatch) != twoPath.get(mismatch)) {
                break;
            }
        }

        /* At this point, we know that the mismatch occurs at index right
         * before mismatch.  Because both nodes are part of the same tree, we
         * know that they have the same root, and so the mismatch index is
         * nonzero.
         *
         * From here, we can recover the cycles by walking down from the root
         * to the first node, across the indicated edge, and then back up the
         * path from the second node to the cycle.
         */
        List<Node> cycle = new ArrayList<Node>();
        for (int i = mismatch - 1; i < onePath.size(); ++i) {
            cycle.add(onePath.get(i));
        }
        for (int i = twoPath.size() - 1; i >= mismatch - 1; --i) {
            cycle.add(twoPath.get(i));
        }

        return new Blossom(onePath.get(mismatch - 1), cycle, new HashSet(cycle));
    }

    /**
     * Given a graph and a blossom, returns the contraction of the graph around
     * that blossom.
     *
     * @param g       The graph to contract.
     * @param blossom The set of nodes in the blossom.
     * @return The contraction g / blossom.
     */
    private static Graph contractGraph(Graph g, Blossom blossom) {
        /* The contraction of the graph is the modified graph where:
         * 1. All nodes in the blossom are removed.
         * 2. There is a new node, the pseudonode.
         * 3. All edges between nodes in the blossom are removed.
         * 4. All edges between nodes out of the blossom and nodes in the
         *    blossom are replaced by an edge to the pseudonode.
         */
        Graph result = new Graph();

        /* Begin by adding all nodes not in the blossom. */
        for (Node node : g.Nodes) {
            if (!blossom.nodes.contains(node)) {
                result.Nodes.add(node);
            }
        }

        /* Add the pseudonode in. */
        result.Nodes.add(blossom.root);

        /* Add each edge in, adjusting the endpoint as appropriate. */
        for (Node node : g.Nodes) {
            /* Skip nodes in the blossom; they're not included in the
             * contracted graph.
             */
            if (blossom.nodes.contains(node)) {
                continue;
            }

            /* Explore all nodes connected to this one. */
            for (Node endpoint : getAdjacentNodes(g, node)) {
                /* If this endpoint is in the blossom, pretend that it's now
                 * an edge to the pseudonode.
                 */
                if (blossom.nodes.contains(endpoint)) {
                    endpoint = blossom.root;
                }

                /* Add the edge to the graph, accounting for the fact that it
                 * might now be transformed.
                 */
                //هون لازم اتأكد مشان ما ينضاف الحافة و عكسها
                result.Edges.add(new Edge(node, endpoint));
            }
        }

        return result;
    }

    private static List<Node> getAdjacentNodes(Graph graph, Node node) {
        List<Node> x = new ArrayList<>();
        for (Edge Edge1 : graph.Edges) {
            if (Edge1.getStartNode().getName().equals(node.getName())) {
                x.add(Edge1.getEndNode());
            } else if (Edge1.getEndNode().getName().equals(node.getName())) {
                x.add(Edge1.getStartNode());
            }

        }
        return x;
    }

    /**
     * Given an alternating path in a graph formed by the contraction of a
     * blossom into a pseudonode, along with the alternating forest in the
     * original graph, returns a new alternating path in the original graph
     * formed by expanding the path if it goes through a pseudonode.
     *
     * @param path    The path in the contracted graph.
     * @param g       The uncontracted graph.
     * @param forest  The alternating forest of the original graph.
     * @param blossom The blossom that was contracted.
     *                contracted graph.
     * @return An alternating path in the original graph.
     */
    private static List<Node> expandPath(List<Node> path, Graph g, Map<Node, NodeInformation> forest, Blossom blossom) {
        /* Any path in the contracted graph will have at most one instance of
         * the blossom's pseudonode in it.  This node will have at most one
         * dotted (unmatched) edge incident to it and one solid (matched)
         * edge.  When expanding the node, the solid edge will end up incident
         * to the root of the blossom, and the dotted edge will leave the
         * blossom through some edge.  The challenge of this function is as
         * follows: given a path that may or may not pass through this
         * blossom, find some edge leaving the blossom to the next node in the
         * chain, then route the path through the blossom in such a way that
         * the path is alternating.
         *
         * To do this, we'll make a simplifying assumption that the path is
         * oriented such that as we start from the front and walk toward the
         * end, we enter the blossom's pseudonode through a solid edge and
         * leave through a dotted edge.  Dotted edges are at indices (0, 1),
         * (2, 3), ..., (2i, 2i + 1), and so we're going to want the
         * pseudonode to appear at an even index.  Because the path is
         * alternating, there's an odd number of edges in the path, and
         * therefore our path has an even number of nodes.  Consequently, if
         * the pseudonode exists in the path, then either it's at an even
         * index (in which case our assumption holds), or its at an odd index
         * (in which case we need to reverse our list, converting the odd
         * index into an even index).
         */
        int index = path.indexOf(blossom.root);

        /* If the node doesn't exist at all, our path is valid. */
        if (index == -1) {
            return path;
        }

        /* If the node is at an odd index, reverse the list and recompute the
         * index.
         */
        if (index % 2 == 1) {
            path = reversePath(path);
        }

        /* Now that we have the pseudonode at an even index (the start of a
         * dotted edge), we can start expanding out the path.
         */
        List<Node> result = new ArrayList<Node>();
        for (int i = 0; i < path.size(); ++i) {
            /* Look at the current node.  If it's not the pseudonode, then add
             * it into the resulting path with no modifications.
             */
            if (path.get(i) != blossom.root) {
                result.add(path.get(i));
            }
            /* Otherwise, we are looking at the pseudonode, which must be at
             * the start of a dotted edge.  We need to look at the next node
             * in the path to determine how to expand this pseudonode.  In
             * particular, we need to find some node in the cycle that the
             * next node in the path connects to, then need to route the path
             * around the cycle to that node.
             */
            else {
                /* Add the blossom's root to the path, since any path entering
                 * the blossom on a solid edge must come in here.
                 */
                result.add(blossom.root);

                /* Find some node in the cycle with an edge to the next node
                 * in the path.
                 */
                Node outNode = findNodeLeavingCycle(g, blossom, path.get(i + 1));

                /* Look up where this node is in the cycle. */
                int outIndex = blossom.cycle.indexOf(outNode);

                /* When expanding out this path around the cycle, we need to
                 * ensure that the path we take ends by following a solid
                 * edge, since the next edge that we'll be taking is dashed.
                 * There are two possible ways to navigate the cycle - one
                 * going clockwise and one going counterclockwise.  If the
                 * index of the outgoing node is even, then the path through
                 * the cycle in the forward direction will end by following a
                 * solid edge.  If it's odd, then the path through the cycle
                 * in the reverse direction ends with an outgoing edge.  We'll
                 * choose which way to go accordingly.
                 *
                 * The cycle we've stored has the root of the blossom at
                 * either endpoint, and so we'll skip over it in this
                 * iteration.
                 */
                int start = (outIndex % 2 == 0) ? 1 : blossom.cycle.size() - 2;
                int step = (outIndex % 2 == 0) ? +1 : -1;

                /* Walk along the cycle until we have processed the node that will
                 * take us out.  This means that we walk until we have seen the node
                 * that will leave the blossom, then taken another step.
                 */
                for (int k = start; k != outIndex + step; k += step) {
                    result.add(blossom.cycle.get(k));
                }
            }
        }
        return result;
    }

    /**
     * Given a path, returns the path formed by reversing the order of the nodes
     * in that path.
     *
     * @param path The path to reverse.
     * @return The reverse of that path.
     */
    private static List<Node> reversePath(List<Node> path) {
        List<Node> result = new ArrayList<Node>();

        /* Visit the path in the reverse order. */
        for (int i = path.size() - 1; i >= 0; --i) {
            result.add(path.get(i));
        }

        return result;
    }

    /**
     * Given a graph, a blossom in that graph, and a node in that graph, returns
     * a node in the blossom that has an outgoing edge to that node. If multiple
     * nodes in the blossom have such an edge, one of them is chosen
     * arbitrarily.
     *
     * @param g       The graph in which the cycle occurs.
     * @param blossom The blossom in that graph.
     * @param node    The node outside of the blossom.
     * @return Some node in the blossom with an edge in g to the indicated node.
     */
    private static Node findNodeLeavingCycle(Graph g, Blossom blossom, Node node) {
        /* Check each node in the blossom for a matching edge. */
        for (Node cycleNode : blossom.nodes) {
            if (isEdgeExists(g, new Edge(cycleNode, node))) {
                return cycleNode;
            }
        }

        /* If we got here, something is wrong because the node in question
         * should have some edge into it.
         */
        throw new AssertionError("Could not find an edge out of the blossom.");
    }

    public static void removeEdge(Graph g, Node s, Node e) {
        List<Edge> edges = g.Edges;
        for (int i = edges.size() - 1; i >= 0; i--) {
            if ((edges.get(i).getStartNode().getName().equals(s.getName()) && edges.get(i).getEndNode().getName().equals(e.getName()))
                    || (edges.get(i).getStartNode().getName().equals(e.getName()) && edges.get(i).getEndNode().getName().equals(s.getName())))
                edges.remove(i);
        }
    }
}

