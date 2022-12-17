/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fears
 */
public class Encheres extends BorderPane{
    
    public Encheres (VueMain main) throws SQLException{
        Connection con =main.getInfoSession().getConBdd(); //Ref lien a la bdd
        
        gestionBddGUI.bandeauUtilisateur(con, main, this);
        gestionBddGUI.creationFiltre(main, this, con,new Image(getClass().getResourceAsStream("ebuy.png")));
        
        //Affichage de tous les objets du site
        gestionBddGUI.tousLesObjets(con,this,main,Integer.toString(main.getInfoSession().getCurrentUserId()));
    }
    
}
