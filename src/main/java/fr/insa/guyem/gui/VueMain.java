/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fears
 */


public class VueMain extends BorderPane{
    
    private ScrollPane scrollContent;
    private BorderPane welcomePage;
    
    public void setScrollCenter (Node n){
        this.scrollContent.setContent(n);
        this.setCenter(scrollContent);
    }
            
    public void setNormalCenter (Node n){
        this.setCenter(n);
    }
    
    public VueMain(){
        this.scrollContent = new ScrollPane();
        this.welcomePage = new PageAccueil(this);
        this.setCenter(scrollContent);
        setNormalCenter(welcomePage);
    }
}
