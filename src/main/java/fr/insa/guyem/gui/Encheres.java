/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint ;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author fears
 */
public class Encheres extends BorderPane{
    
    public Encheres (VueMain main) throws SQLException{
        Connection con =main.getInfoSession().getConBdd(); //Ref lien a la bdd
        
        gestionBddGUI.bandeauUtilisateur(con, main, this);
        gestionBddGUI.creationFiltre(main, this, con);
        
        //Affichage de tous les objets du site
        gestionBddGUI.tousLesObjets(con,this,main,Integer.toString(main.getInfoSession().getCurrentUserId()));
    }
    
}
