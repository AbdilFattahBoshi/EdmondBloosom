package sample;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    public Button browseButton;
    public Button executeButton;
    public TextField fileUrlTextField;//for the input graph
    public TextArea inputGraphTextArea;
    public TextArea outputGraphTextArea;
    public Pane mainView;
    public TextArea maxMatchGraphTextArea;
    public TextField maxMatchGraphUriTextField;
    public TextField outputMatchingGraphUriTextField;
    public Button editingButton;
    public TextField utilsTextField;

    private Graph readiedGraph;
    private Graph maxMatchGraph=null;
    private Graph outputGraph;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BackgroundImage myBI= new BackgroundImage(
                new Image("file:///C:/Users/informatic/IdeaProjects/EdmondsAlgorithm/AbstractWhiteBackground.jpg",
                        730,430,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        //then you set to your node
        mainView.setBackground(new Background(myBI));
    }

    public void createJson(ActionEvent event){
        Gson gson=new Gson();
        Graph g=createSampleGraph();
//        String gsonString=gson.toJson(g);
        try (Writer writer = new FileWriter("Output.json")) {
            gson.toJson(g, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            JsonReader reader = new JsonReader(new FileReader("Output.json"));
//            Graph read=gson.fromJson(reader, Graph.class);
//            System.out.println("some thing");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private Graph createSampleGraph(){
        Graph graph =new Graph();
        graph.Nodes.add(new Node("0"));
        graph.Nodes.add(new Node("1"));
        graph.Nodes.add(new Node("2"));
        graph.Nodes.add(new Node("3"));
        graph.Nodes.add(new Node("4"));
        graph.Nodes.add(new Node("5"));
        graph.Nodes.add(new Node("6"));
        graph.Nodes.add(new Node("7"));
        graph.Nodes.add(new Node("8"));
        graph.Nodes.add(new Node("9"));
        graph.Nodes.add(new Node("10"));
        graph.Nodes.add(new Node("11"));
        graph.Nodes.add(new Node("12"));
        graph.Nodes.add(new Node("13"));
        graph.Nodes.add(new Node("14"));
        graph.Nodes.add(new Node("15"));
        graph.Edges.add(new Edge("1",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "1")));
        graph.Edges.add(new Edge("1",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "13")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "4")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "15")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "4"),Graph.getNodeByName(graph, "3")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "4"),Graph.getNodeByName(graph, "5")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "5"),Graph.getNodeByName(graph, "3")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "3"),Graph.getNodeByName(graph, "2")));
        graph.Edges.add(new Edge("3",Graph.getNodeByName(graph, "2"),Graph.getNodeByName(graph, "13")));
        graph.Edges.add(new Edge("4",Graph.getNodeByName(graph, "2"),Graph.getNodeByName(graph, "6")));
        graph.Edges.add(new Edge("5",Graph.getNodeByName(graph, "6"),Graph.getNodeByName(graph, "7")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "7"),Graph.getNodeByName(graph, "8")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "8"),Graph.getNodeByName(graph, "11")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "8"),Graph.getNodeByName(graph, "12")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "11"),Graph.getNodeByName(graph, "12")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "12"),Graph.getNodeByName(graph, "10")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "10"),Graph.getNodeByName(graph, "9")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "9"),Graph.getNodeByName(graph, "14")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "7"),Graph.getNodeByName(graph, "14")));

        for(Edge e:graph.Edges){
            e.setWeight(5);
        }
        return graph;
    }

    private Graph createTemplateGraph(){
        Graph graph =new Graph();
        graph.Nodes.add(new Node("0"));
        graph.Nodes.add(new Node("1"));
        graph.Nodes.add(new Node("2"));
        graph.Edges.add(new Edge("1",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "1")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "2")));

        return graph;
    }

    private Graph createTemplateGraphWithWeight(){
        Graph graph =new Graph();
        graph.Nodes.add(new Node("0"));
        graph.Nodes.add(new Node("1"));
        graph.Nodes.add(new Node("2"));
        graph.Edges.add(new Edge("1",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "1")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "2")));

        for(Edge e:graph.Edges){
            e.setWeight(5);
        }

        return graph;
    }

    //for the input file
    @FXML
    public void browseButtonClick(ActionEvent event){
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(new Stage());
            fileUrlTextField.setText(file.getAbsolutePath());
            Gson gson=new Gson();
            JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
            readiedGraph=gson.fromJson(reader, Graph.class);
            StringBuilder inputGraph= new StringBuilder();
            inputGraph.append("Verities:").append("\n");
            for (Node xx : readiedGraph.Nodes) {
                inputGraph.append(xx.getName()).append("\t");
            }
            inputGraph.append("\n").append("Edges:").append("\n");
            for (Edge Edge1 : readiedGraph.Edges) {
                inputGraph.append("start : ").append(Edge1.getStartNode().getName()).append(" -> ")
                        .append("End : ").append(Edge1.getEndNode().getName()).append("\n");

            }
            inputGraphTextArea.setText(inputGraph.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void executeButtonClick(ActionEvent event){
        if(readiedGraph!=null){
            /////
            Graph helper = new Graph();
            helper.Nodes = readiedGraph.Nodes;
            for (Edge e:readiedGraph.Edges) {
                helper.Edges.add(new Edge(Graph.getNodeByName(helper,e.getStartNode().getName()),Graph.getNodeByName(helper,e.getEndNode().getName())));
            }
            //////
            outputGraph = Edmond.EdmondBloss(helper,maxMatchGraph);
            StringBuilder outputGraph= new StringBuilder();
            outputGraph.append("Verities:").append("\n");
            for (Node xx : this.outputGraph.Nodes) {
                outputGraph.append(xx.getName()).append("\t");
            }
            outputGraph.append("\n").append("Edges:").append("\n");
            for (Edge Edge1 : this.outputGraph.Edges) {
                outputGraph.append("start : ").append(Edge1.getStartNode().getName()).append(" -> ")
                        .append("End : ").append(Edge1.getEndNode().getName()).append("\n");
            }
            outputGraphTextArea.setText(outputGraph.toString());
        }
    }


    public void maxMatchBrowseButtonClick(ActionEvent event) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(new Stage());
            maxMatchGraphUriTextField.setText(file.getAbsolutePath());
            Gson gson=new Gson();
            JsonReader reader = new JsonReader(new FileReader(file.getAbsolutePath()));
            Graph helper=gson.fromJson(reader, Graph.class);
            maxMatchGraph=new Graph();
            maxMatchGraph.Nodes = readiedGraph.Nodes;
            for (Edge e:helper.Edges) {
                maxMatchGraph.Edges.add(new Edge(Graph.getNodeByName(maxMatchGraph,e.getStartNode().getName()),Graph.getNodeByName(maxMatchGraph,e.getEndNode().getName())));
            }
            StringBuilder inputGraph= new StringBuilder();
            inputGraph.append("Verities:").append("\n");
            for (Node xx : maxMatchGraph.Nodes) {
                inputGraph.append(xx.getName()).append("\t");
            }
            inputGraph.append("\n").append("Edges:").append("\n");
            for (Edge Edge1 : maxMatchGraph.Edges) {
                inputGraph.append("start : ").append(Edge1.getStartNode().getName()).append(" -> ")
                        .append("End : ").append(Edge1.getEndNode().getName()).append("\n");
            }
            maxMatchGraphTextArea.setText(inputGraph.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void outputMatchingGraphButtonClick(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        File dest = fileChooser.showSaveDialog(new Stage());
        outputMatchingGraphUriTextField.setText(dest.getAbsolutePath());
        Gson gson=new Gson();
        try (Writer writer = new FileWriter(dest.getAbsolutePath())) {
            gson.toJson(outputGraph, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void editingButtonClick(ActionEvent event) {
        Graph g=testGraph();
//        Graph g1=createTemplateGraph();
//        Graph g2=createTemplateGraphWithWeight();
        Gson gson=new Gson();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        File dest = fileChooser.showSaveDialog(new Stage());
        try (Writer writer = new FileWriter(dest.getAbsolutePath())) {
            gson.toJson(g, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Graph fGraph =new Graph();
//        fGraph.Nodes.add(new Node("0"));
//        fGraph.Nodes.add(new Node("1"));
//        fGraph.Nodes.add(new Node("2"));
//        fGraph.Nodes.add(new Node("3"));
//
//        fGraph.Edges.add(new Edge("1",Graph.getNodeByName(fGraph, "0"),Graph.getNodeByName(fGraph, "1")));
//        fGraph.Edges.add(new Edge("2",Graph.getNodeByName(fGraph, "1"),Graph.getNodeByName(fGraph, "2")));
//        fGraph.Edges.add(new Edge("3",Graph.getNodeByName(fGraph, "2"),Graph.getNodeByName(fGraph, "3")));
//        fGraph.Edges.add(new Edge("4",Graph.getNodeByName(fGraph, "3"),Graph.getNodeByName(fGraph, "0")));
//
//        ArrayList<Factor> factors=new ArrayList<>();
//        Factor f1=new Factor();
//        Factor f2=new Factor();
//        factors.add(f1);
//        factors.add(f2);
//
//        f1.edges.add(new Edge("1",Graph.getNodeByName(fGraph, "0"),Graph.getNodeByName(fGraph, "1")));
//        f1.edges.add(new Edge("3",Graph.getNodeByName(fGraph, "2"),Graph.getNodeByName(fGraph, "3")));
//
//        f2.edges.add(new Edge("2",Graph.getNodeByName(fGraph, "1"),Graph.getNodeByName(fGraph, "2")));
//        f2.edges.add(new Edge("4",Graph.getNodeByName(fGraph, "3"),Graph.getNodeByName(fGraph, "0")));
//        f2.edges.add(new Edge("5",Graph.getNodeByName(fGraph, "1"),Graph.getNodeByName(fGraph, "3")));
//
//
//        ArrayList<Edge> graphEdges=new ArrayList<>();//the factor lists
//        boolean answer=true;
//
//        for(int i=0;i<factors.size();i++){
//            for(int j=0;j<factors.get(i).edges.size();j++){
//                if(contains(factors.get(i).edges.get(j),fGraph.Edges) && !contains(factors.get(i).edges.get(j),graphEdges)){
//                    graphEdges.add(factors.get(i).edges.get(j));
//                } else {
//                    answer=false;
//                    break;
//                }
//            }
//        }
//
//        if(answer){
//            if(graphEdges.size()!=fGraph.Edges.size())
//                answer=false;
//        }

    }

    private boolean contains(Edge edge, List<Edge> edges) {
        for(Edge e:edges){
            if((e.getName().equals(edge.getName()))
                    &&( (e.getStartNode().getName().equals(edge.getStartNode().getName()) &&
                    e.getEndNode().getName().equals(edge.getEndNode().getName()))  ||
                    (e.getStartNode().getName().equals(edge.getEndNode().getName()) &&
                            e.getEndNode().getName().equals(edge.getStartNode().getName()))) ){
                return true;
            }
        }
        return false;
    }

    private Graph testGraph(){
        Graph graph =new Graph();
        graph.Nodes.add(new Node("0"));
        graph.Nodes.add(new Node("1"));
        graph.Nodes.add(new Node("2"));
        graph.Nodes.add(new Node("3"));
        graph.Nodes.add(new Node("4"));
        graph.Nodes.add(new Node("5"));
        graph.Nodes.add(new Node("6"));
        graph.Nodes.add(new Node("7"));
        graph.Nodes.add(new Node("8"));
        graph.Nodes.add(new Node("9"));
        graph.Nodes.add(new Node("10"));
        graph.Nodes.add(new Node("11"));
        graph.Nodes.add(new Node("12"));
        graph.Nodes.add(new Node("13"));
        graph.Nodes.add(new Node("14"));
        graph.Nodes.add(new Node("15"));
        graph.Edges.add(new Edge("1",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "1")));
        graph.Edges.add(new Edge("2",Graph.getNodeByName(graph, "0"),Graph.getNodeByName(graph, "13")));
        graph.Edges.add(new Edge("3",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "4")));
        graph.Edges.add(new Edge("4",Graph.getNodeByName(graph, "1"),Graph.getNodeByName(graph, "15")));
        graph.Edges.add(new Edge("5",Graph.getNodeByName(graph, "4"),Graph.getNodeByName(graph, "3")));
        graph.Edges.add(new Edge("6",Graph.getNodeByName(graph, "4"),Graph.getNodeByName(graph, "5")));
        graph.Edges.add(new Edge("7",Graph.getNodeByName(graph, "5"),Graph.getNodeByName(graph, "3")));
        graph.Edges.add(new Edge("8",Graph.getNodeByName(graph, "3"),Graph.getNodeByName(graph, "2")));
        graph.Edges.add(new Edge("9",Graph.getNodeByName(graph, "2"),Graph.getNodeByName(graph, "13")));
        graph.Edges.add(new Edge("10",Graph.getNodeByName(graph, "2"),Graph.getNodeByName(graph, "6")));
        graph.Edges.add(new Edge("11",Graph.getNodeByName(graph, "6"),Graph.getNodeByName(graph, "7")));
        graph.Edges.add(new Edge("12",Graph.getNodeByName(graph, "7"),Graph.getNodeByName(graph, "8")));
        graph.Edges.add(new Edge("13",Graph.getNodeByName(graph, "8"),Graph.getNodeByName(graph, "11")));
        graph.Edges.add(new Edge("14",Graph.getNodeByName(graph, "8"),Graph.getNodeByName(graph, "12")));
        graph.Edges.add(new Edge("15",Graph.getNodeByName(graph, "11"),Graph.getNodeByName(graph, "12")));
        graph.Edges.add(new Edge("16",Graph.getNodeByName(graph, "12"),Graph.getNodeByName(graph, "10")));
        graph.Edges.add(new Edge("17",Graph.getNodeByName(graph, "10"),Graph.getNodeByName(graph, "9")));
        graph.Edges.add(new Edge("18",Graph.getNodeByName(graph, "9"),Graph.getNodeByName(graph, "14")));
        graph.Edges.add(new Edge("19",Graph.getNodeByName(graph, "7"),Graph.getNodeByName(graph, "14")));

        return graph;
    }
}
