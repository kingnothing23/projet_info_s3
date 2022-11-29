/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.guyem.gui;

import java.sql.Connection;
import java.sql.SQLException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author francois
 */
public class Main extends Application {

    private Scene sc;
    @Override
    public void start(Stage stage) {
        sc = new Scene(new VueMain());
//        Scene sc = new Scene(new TestFx());
        stage.setWidth(700);
        stage.setHeight(500);
        stage.setScene(sc);
        stage.setTitle("Ebay.fr");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
