/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;


/* A key step of the algorithm works by building up a collection of
     * forests of alternating trees.  We will need to be able to answer
     * the following queries about the tree structure:
     *
     * 1. For each node, what tree is it a part of?
     * 2. For each node, what is its parent node in the tree?
     * 3. For each node, is it an inner or outer node in its tree?
     *
     * We will represent all of this information by a utility struct that
     * stores this information.  For simplicity, each node will be
     * identified with its root.
 */
public class NodeInformation implements Serializable {

    public final Node parent;
    public final Node treeRoot;
    public final boolean isOuter;//even distance

    /**
     * Constructs a new NodeInformation wrapping the indicated data.
     *
     * @param parent The parent of the given node.
     * @param treeRoot The root of the given node.
     * @param isOuter Whether the given node is an outer node.
     */
    public NodeInformation(Node parent, Node treeRoot, boolean isOuter) {
        this.parent = parent;
        this.treeRoot = treeRoot;
        this.isOuter = isOuter;
    }
}
