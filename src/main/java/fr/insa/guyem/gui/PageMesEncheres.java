/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class PageMesEncheres extends BorderPane{
    public PageMesEncheres(Connection con,VueMain main) throws SQLException {
        Label lMsgEncheres = new Label("Vos enchères :");
        lMsgEncheres.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        lMsgEncheres.setPadding(new Insets(10));
        RadioButton rbEnCours = new RadioButton("En cours");
        RadioButton rbTerminees = new RadioButton("Terminées");
        //Bouton retour: 
        Button bHome = new Button("Retour au tableau des ventes");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        bHome.setGraphic(view);
        //Ajout à l'interface des boutons créés
        HBox hbRadioButtons = new HBox(rbEnCours,rbTerminees);
        VBox vbTop = new VBox(bHome,lMsgEncheres,hbRadioButtons);
        hbRadioButtons.setPadding(new Insets(10));
        hbRadioButtons.setSpacing(10);
        this.setTop(vbTop);
        gestionBddGUI.createHistoUtil(con,main, this, false);
        
        ToggleGroup tgButtons = new ToggleGroup ();
        rbEnCours.setToggleGroup(tgButtons);
        rbTerminees.setToggleGroup(tgButtons);
        rbEnCours.setSelected(true);
        
        rbEnCours.setOnAction((t) -> {
            if (rbEnCours.isSelected()){
                try {
                    gestionBddGUI.createHistoUtil(con,main, this, false);
                } catch (SQLException ex) {
                    Logger.getLogger(PageMesEncheres.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        rbTerminees.setOnAction((t) -> {
            if (rbTerminees.isSelected()){
                try {
                    gestionBddGUI.createHistoUtil(con,main, this, true);
                } catch (SQLException ex) {
                    Logger.getLogger(PageMesEncheres.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        bHome.setOnAction((t) -> {
            try {
                main.setCenter(new Encheres(main));
            } catch (SQLException ex) {
                Logger.getLogger(NouvelleVente.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
