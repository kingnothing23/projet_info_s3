/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author fears
 */
public class TestCoucou extends BorderPane{
    public TestCoucou(){
        this.setPadding(new Insets(30,30,30,30));
        
        Label lNom = new Label ("Votre Nom :");
        TextField fNom = new TextField();
        fNom.setMaxWidth(50);
        
        Label lOutput = new Label();
        
        Button bCoucou = new Button("Coucou");
        bCoucou.setOnAction((t) -> {
            lOutput.setText(lOutput.getText()+"Coucou "+fNom.getText()+"\n");
        });
        
        Button bSalut = new Button("Salut");
        bSalut.setOnAction((t) -> {
            lOutput.setText(lOutput.getText()+"Salut "+fNom.getText()+"\n");
        });
        
        Button bReset = new Button("Reset");
        bReset.setOnAction((t) -> {
            lOutput.setText("");
        });
        
        HBox hboxBas = new HBox(bCoucou,bSalut,bReset);
        hboxBas.setSpacing(10);
        VBox vboxHaut = new VBox(lNom,fNom);
        this.setBottom(hboxBas);
        this.setTop(vboxHaut);
        this.setLeft(lOutput);
    }
}
