/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.*;


public class Graph implements Serializable {
    private String name;
    public List<Edge> Edges;
    public List<Node> Nodes;

    public Graph() {
        Edges = new ArrayList<>();
        Nodes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Node getNodeByName(Graph graph, String name) {
        for (Node node : graph.Nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

}
