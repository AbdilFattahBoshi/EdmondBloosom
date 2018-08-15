package model;

import java.util.ArrayList;

public class Factor {

    public ArrayList<Edge> edges;

    public Factor(ArrayList<Edge> edges) {
        this.edges = new ArrayList<>();
    }

    public Factor() {
        edges=new ArrayList<>();
    }
}
