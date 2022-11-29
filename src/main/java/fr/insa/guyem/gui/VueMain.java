/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import fr.insa.guyem.gestionBddGUI;
import java.sql.SQLException;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fears
 */


public class VueMain extends BorderPane{
    
    private ScrollPane scrollContent;
    private BorderPane welcomePage;
    private InfoSession info;
    
    public InfoSession getInfoSession(){
        return info;
    }
    
    public VueMain() throws SQLException{
        this.scrollContent = new ScrollPane();

        this.info = new InfoSession();
        try {
            this.info.setConBdd(gestionBddGUI.defautConnect());
            System.out.println("Connexion reussie");
            this.welcomePage = new PageAccueil(this);
            this.setCenter(welcomePage);
        } catch (ClassNotFoundException ex) {
            System.out.println("Connexion echoue");
        } catch (SQLException ex) {
            System.out.println("Connexion echoue");
        }

    }
}
