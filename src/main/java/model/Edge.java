/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Edge implements Serializable {

    private String name;
    private double weight;
    private Node startNode;
    private Node endNode;

    public Edge(String name) {
        this.name = name;
    }

    public Edge(double weight,String name, Node a, Node b) {
        this.weight = weight;
        this.name = name;
        startNode = a;
        endNode = b;
    }

    public Edge(String x, Node a, Node b) {
        name = x;
        startNode = a;
        endNode = b;

    }
      public Edge(Node a, Node b) {
        startNode = a;
        endNode = b;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

}
