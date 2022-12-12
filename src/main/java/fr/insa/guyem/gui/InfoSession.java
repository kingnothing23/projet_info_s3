/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem.gui;

import java.sql.Connection;

/**
 *
 * @author fears
 */
public class InfoSession {
    private int currentUserId;
    private Connection conBdd;
    private String currentUserName;
    
    public void setConBdd(Connection conBdd){
        this.conBdd = conBdd;
    }
    
    public Connection getConBdd(){
        return this.conBdd;
    }
    
    public void setCurrentUserId(int currentUserId){
        this.currentUserId = currentUserId;
    }
    
    public int getCurrentUserId(){
        return this.currentUserId;
    }
    
    public void setCurrentUserName(String currentUserName){
        this.currentUserName = currentUserName;
    }
    
    public String getCurrentUserName(){
        return this.currentUserName;
    }
}
