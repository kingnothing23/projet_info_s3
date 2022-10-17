/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.guyem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * coucousdfgh test merge
 * @author Utilisateur
 */
public class gestionbdd {

    public static Connection connectGeneralPostGres(String host, int port, String database,
            String user, String pass) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    public static Connection defautConnect()
            throws ClassNotFoundException, SQLException {
        return connectGeneralPostGres("localhost", 5439, "postgres", "postgres", "emgu7747");
    }

    public static void menu(Connection con) throws SQLException {
        int rep = -1;
        while (rep != 0) {
            System.out.println("Menu BdD Aime");
            System.out.println("=============");
            System.out.println("1) créer/recréer la BdD initiale");
            System.out.println("2) liste des utilisateurs");
            System.out.println("3) liste des objets");
            System.out.println("4) ajouter un utilisateur");
            System.out.println("5) ajouter un objet");
            System.out.println("6) ajouter n utilisateurs aléatoires");
            System.out.println("7) placer  une enchere sur  un objet");
            System.out.println("8) liste des encheres");
            System.out.println("9) bilan utilisateur donné");

            System.out.println("0) quitter");
            System.out.println("Votre choix ?");
            rep = Lire.i();
            try {
                if (rep == 1) {
                    recreeTout(con);
                } else if (rep == 2) {
                    afficheTousLesUtilisateur(con);
                } else if (rep == 3) {
                    afficheObjets(con);
                } else if (rep == 4) {
                    demandeNouvelUtilisateur(con);
                } else if (rep == 5) {
                    demandeNouvelObjet(con);
                } else if (rep == 6) {
                    System.out.println("création d'utilisateurs 'aléatoires'");
                    System.out.println("combien ?");
                    int combien = Lire.i();
                    for (int i = 0; i < combien; i++) {
                        boolean exist = true;
                        while (exist) {
                            String nom = "U" + ((int) (Math.random() * 10000));
                            String mail = "R" + ((int) (Math.random() * 10000));
                            if (!nomUtilisateurExiste(con, nom)) {
                                exist = false;
                                createUtilisateur(con, nom, "P" + ((int) (Math.random() * 10000)), mail);
                            }
                        }

                    }
                } else if (rep == 7) {
                    afficheObjets(con);
                    System.out.println("entrer l'id de  l'objet");
                    int obj = Lire.i();
                    System.out.println("entrer l'id de  l'utilisateur");
                    int utis = Lire.i();
                    System.out.println("le prix de  lobjet est :");
                     float pessi = PriceActual(con, obj);
                      System.out.println(pessi);
                    System.out.println("entrer le montant de  votre enchere");
                    float price = Lire.f();
                    while (price <= pessi){
                        System.out.println(" montant  inferieur ,entrer le montant de  votre enchere");
                        price = Lire.f();
                    }
                    CreateEnchere(con, obj, utis, price);
                } else if (rep == 8) {
                    afficheToutesLesEncheres(con);
                } else if (rep == 9) {
                    afficheTousLesUtilisateur(con);
                    System.out.println("entrer l'id de  l'utilisateur");
                    int uti = Lire.i();
                    Bilanutil(con, uti);
                }
            } catch (SQLException ex) {
                throw new Error(ex);
            }

        }
        int k = Lire.i();
        System.out.print(k);

    }

    public static void creeSchema(Connection con)
            throws SQLException {
        // je veux que le schema soit entierement créé ou pas du tout
        // je vais donc gérer explicitement une transaction
        con.setAutoCommit(false);
        try ( Statement st = con.createStatement()) {
            // creation des tables
            st.executeUpdate(
                    """
                    create table utilisateur (
                        id integer not null primary key
                        generated always as identity,
                        nom varchar(30) not null,
                        pass varchar(30) not null,
                        mail varchar(30) not null
                        
                    )
                    """);

            st.executeUpdate(
                    """
                    create table objets (
                     ido integer not null primary key 
                     generated always as identity,
                     nom varchar(30) not null,
                     descri varchar(100),
                     prixbase real not null ,
                     categorie integer not null,
                     vendeur integer not null
                    )
                    """);
            st.executeUpdate(
                    """
                    create table enchere (
                        ide integer not null primary key 
                        generated always as identity,
                        de integer not null,
                        sur integer not null,
                        montant real not null
                    
                    )
                    """);
            st.executeUpdate(
                    """
                    create table categorie (
                        idc integer not null primary key 
                        generated always as identity,
                        nom varchar(45) not null
                    
                    )
                    """);
            // je defini les liens entre les clés externes et les clés primaires
            // correspondantes
            st.executeUpdate("""
                   alter table objets
                        add constraint fk_objets_vendeur
                       foreign key (vendeur) references utilisateur(id),
                      add constraint fk_objets_categorie
                        foreign key (categorie) references categorie(idc)                        
                  """);
            st.executeUpdate("""
                   alter table enchere
                        add constraint fk_enchere_de
                       foreign key (de) references utilisateur(id),
                      add constraint fk_enchere_sur
                        foreign key (sur) references objets(ido)                        
                  """);
//           st.executeUpdate(
//                   """
//                   alter table aime
//                      add constraint fk_aime_u2
//                     foreign key (u2) references utilisateur(id)
//                    """);
            // si j'arrive jusqu'ici, c'est que tout s'est bien passé
            // je confirme (commit) la transaction
            con.commit();
            // je retourne dans le mode par défaut de gestion des transaction :
            // chaque ordre au SGBD sera considéré comme une transaction indépendante
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            // quelque chose s'est mal passé
            // j'annule la transaction
            con.rollback();
            // puis je renvoie l'exeption pour qu'elle puisse éventuellement
            // être gérée (message à l'utilisateur...)
            throw ex;
        }
    }

    public static void deleteSchema(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate(
                        """
                    alter table objets
                        drop constraint fk_objets_vendeur,
                        drop constraint fk_objets_categorie
                             """);
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate(
                        """
                    alter table enchere
                        drop constraint fk_enchere_de,
                        drop constraint fk_enchere_sur
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            // je peux maintenant supprimer les tables
            try {
                st.executeUpdate(
                        """
                    drop table objets
                    """);
                // je defini les liens entre les clés externes et les clés primaires
                // correspondantes
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table utilisateur
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table enchere
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
            try {
                st.executeUpdate(
                        """
                    drop table categorie
                    """);
            } catch (SQLException ex) {
                // nothing to do : maybe the table was not created
            }
        }
    }

    public static void main(String[] args) {

        try {
            Connection lol = defautConnect();
            System.out.println("lol");
            //creeSchema(lol);
            menu(lol);

            System.out.println("tables");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static boolean nomUtilisateurExiste(Connection con, String nom) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                "select id from utilisateur where nom = ?")) {
            pst.setString(1, nom);
            ResultSet res = pst.executeQuery();
            return res.next();

        }
    }

    private static int createUtilisateur(Connection con, String nom, String pass, String mail) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into utilisateur (nom,pass,mail) values (?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setString(2, pass);
            pst.setString(3, mail);
            pst.executeUpdate();

            // je peux alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
    }

    private static void afficheTousLesUtilisateur(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("select * from utilisateur")) {

                System.out.println("liste des utilisateurs :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("id");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String pass = tlu.getString(3);
                    String mail = tlu.getString(4);
                    System.out.println(id + " : " + nom + " (" + pass + ") mail :" + mail);
                }
            }
        }
    }

    private static void recreeTout(Connection con) throws SQLException {
        try {
            deleteSchema(con);
//            System.out.println("ancien schéma supprimé");
        } catch (SQLException ex) {
//            System.out.println("pas de suppression d'un ancien schéma");
        }
        creeSchema(con);
    }

    private static void afficheObjets(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("select * from objets")) {

                System.out.println("liste des objets :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("ido");
                    // ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(2);
                    String prixbase = tlu.getString(3);
                    String vendeur = tlu.getString(4);
                    System.out.println(id + " : " + nom + " prix(" + prixbase + ") vendeur :" + vendeur);
                }
            }
        }
    }

    private static void demandeNouvelObjet(Connection con) throws SQLException {
        boolean ok = true;
        while (ok) {
            System.out.println("--- creation nouvel objet");
            System.out.println("nom");
            String nom = Lire.S();
            System.out.println("prixbase");
            float prix = Lire.f();
            System.out.println("description");
            String desc;
            desc = Lire.S();
            System.out.println("id vendeur");
            int idv = Lire.i();

            createObjets(con, nom, prix, desc, idv);

        }
    }

    private static void demandeNouvelUtilisateur(Connection con) throws SQLException {
        boolean ok = true;
        while (ok) {
            System.out.println("--- creation nouvel utilisateur");
            System.out.println("nom");
            String nom = Lire.S();
            System.out.println("pass");
            String pass = Lire.S();
            System.out.println("mail");
            String mail = Lire.S();
            ok = nomUtilisateurExiste(con, nom);
            if (ok) {
                System.out.println("ce nom existe deja, choisissez en un autre");
            } else {
                createUtilisateur(con, nom, pass, mail);
            }
            ok = false;
        }

    }

    private static int createObjets(Connection con, String nom, float prixbase, String desc, int idv) throws SQLException {
        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into objets (nom,prixbase,vendeur) values (?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nom);
            pst.setFloat(2, prixbase);
            pst.setInt(3, idv);
            pst.executeUpdate();

            // je peux alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
    }

    private static float PriceActual(Connection con, int obj) throws SQLException {
        int yesin;
        float actual;
        try ( Statement st = con.createStatement()) {
            String query = "select count(sur) from enchere where sur=" + obj;
            try ( ResultSet tlu = st.executeQuery(query)) {
                tlu.next();
                yesin = tlu.getInt(1);
            }
        }

        if (yesin == 0) {
            try ( Statement st = con.createStatement()) {
                String query = "select prixbase from objets where ido=" + obj;
                try ( ResultSet tlu = st.executeQuery(query)) {
                    System.out.println("prix actuel :");
                    actual = tlu.getFloat(1);

                    System.out.println(actual);
                }

            }

        } else {

//            try ( Statement st = con.createStatement()) {
//                String query = "select max(montant) from enchere where enchere.sur=" + obj;
//                try ( ResultSet tlu = st.executeQuery(query)) {
//                    System.out.println("prix actuel :");
//                    actual = tlu.getFloat(1);
//
//                    System.out.println(actual);
//
//                }
//            }
           try ( PreparedStatement st = con.prepareStatement("select max(montant) from enchere where enchere.sur=?")) {
                st.setInt(1, obj);
                try ( ResultSet tlu = st.executeQuery()) {
                    System.out.println("prix actuel :");
                    tlu.next();
                    actual = tlu.getFloat(1);

                    System.out.println(actual);

                }
            }

        }
        return actual;
    }

    private static int CreateEnchere(Connection con, int obj, int utis, float price) throws SQLException {

        try ( PreparedStatement pst = con.prepareStatement(
                """
                insert into enchere (de,sur,montant) values (?,?,?)
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, utis);
            pst.setInt(2, obj);
            pst.setFloat(3, price);
            pst.executeUpdate();

            // je peux  alors récupérer les clés créées comme un result set :
            try ( ResultSet rid = pst.getGeneratedKeys()) {
                // et comme ici je suis sur qu'il y a une et une seule clé, je
                // fait un simple next 
                rid.next();
                // puis je récupère la valeur de la clé créé qui est dans la
                // première colonne du ResultSet
                int id = rid.getInt(1);
                return id;
            }
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }

    private static void afficheToutesLesEncheres(Connection con) throws SQLException {
        try ( Statement st = con.createStatement()) {

            try ( ResultSet tlu = st.executeQuery("""
                                                  select ide,montant,sur, (SELECT nom from objets where objets.ido=enchere.sur) as objet,
                                                  de, (SELECT nom from utilisateur where utilisateur.id=enchere.de)as acheteur
                                                  
                                                  
                                                  
                                                  
                                                       from enchere  """)) {

                System.out.println("liste des enchere :");
                System.out.println("------------------------");
                // ici, on veut lister toutes les lignes, d'où le while
                while (tlu.next()) {

                    int id = tlu.getInt("ide");
                    //ou par son numéro (la première colonne a le numéro 1)
                    String nom = tlu.getString(1);
                    String prixbase = tlu.getString(2);
                    String vendeurid = tlu.getString(5);
                    String vendeur = tlu.getString(6);
                    String objetid = tlu.getString(3);
                    String objet = tlu.getString(4);

                    System.out.println("id : " + id + "montant :"
                            + prixbase + "sur :" + objetid + " " + objet + " de " + vendeurid + " " + vendeur);
                }
            }
        }
    }

    private static void Bilanutil(Connection con, int uti) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
