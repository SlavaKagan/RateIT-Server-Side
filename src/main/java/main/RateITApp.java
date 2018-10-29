package main;

import java.util.Scanner;

//import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.logic.TrailerPane;

//@SpringBootApplication
public class RateITApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		System.out.println("Enter search terms: ");
		TrailerPane tpane = new TrailerPane(s.nextLine());
		stage.setScene(new Scene(tpane));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}