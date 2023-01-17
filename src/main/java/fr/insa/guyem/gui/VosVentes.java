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
public class VosVentes extends BorderPane{
    public VosVentes(VueMain main,Connection con) throws SQLException{
        Label lMsgVentes = new Label("Vos ventes :");
        lMsgVentes.setFont(Font.font("Montserra",FontWeight.BOLD,20));
        lMsgVentes.setPadding(new Insets(10));
        Button bHome = new Button("Retour au tableau des ventes");
        ImageView view = new ImageView (new Image(getClass().getResourceAsStream("home2.png")));
        view.setFitHeight(50);
        view.setFitWidth(50);
        bHome.setGraphic(view);
        this.setTop(lMsgVentes);
        
        //Radios boutons :
        RadioButton rbEnCours = new RadioButton("En cours");
        RadioButton rbTerminees = new RadioButton("Terminées");
        HBox hbRadioButtons = new HBox(rbEnCours,rbTerminees);
        VBox vbLeft = new VBox(bHome,hbRadioButtons);
        hbRadioButtons.setPadding(new Insets(10));
        hbRadioButtons.setSpacing(10);
        ToggleGroup tgButtons = new ToggleGroup ();
        rbEnCours.setToggleGroup(tgButtons);
        rbTerminees.setToggleGroup(tgButtons);
        rbEnCours.setSelected(true);
        
        this.setLeft(vbLeft);
        gestionBddGUI.bandeauUtilisateur(con, main, this);
        
        String query ="select * from objets where vendeur ="+main.getInfoSession().getCurrentUserId();
        gestionBddGUI.affichageQuery(con, this, main, query,0,1000000000,false);
       
        
        rbEnCours.setOnAction((t) -> {
            if (rbEnCours.isSelected()){
                try {
                    gestionBddGUI.affichageQuery(con, this, main, query,0,1000000000,false);
                } catch (SQLException ex) {
                    Logger.getLogger(PageMesEncheres.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        rbTerminees.setOnAction((t) -> {
            if (rbTerminees.isSelected()){
                try {
                    gestionBddGUI.affichageQuery(con, this, main, query,0,1000000000,true);
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
