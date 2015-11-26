package KeyLibrary;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Demo extends Application {
	private KeyLibrary kl;
	private TextField t1;
	private TextField t2;

	@Override
	public void start(Stage primaryStage) {
		kl = new KeyLibrary(false);
		GridPane pane = new GridPane();
		t1 = new TextField();
		t1.setPromptText("enter string to checkout");
		t2 = new TextField();
		t2.setPromptText("enter string to return");
		Button b1 = new Button("Checkout");
		b1.setOnAction(e -> checkoutKey());
		Button b2 = new Button("Return");
		b2.setOnAction(e -> returnKey());
		Button b3 = new Button("Show KeyLibrary");
		b3.setOnAction(e -> kl.show());
		Text text = new Text("KEYLIBRARY DEMO");
		text.setFont(new Font(Font.getDefault().getName(), 40));
		GridPane.setColumnSpan(text, 2);
		GridPane.setColumnSpan(b3, 2);
		pane.add(text, 0, 0);
		pane.add(t1, 0, 1);
		pane.add(t2, 0, 2);
		pane.add(b1, 1, 1);
		pane.add(b2, 1, 2);
		pane.add(b3, 0, 3);
		pane.setVgap(20);
		pane.setHgap(20);
		pane.setAlignment(Pos.CENTER);
		primaryStage.setScene(new Scene(pane, 400, 400));
		primaryStage.setTitle("KeyLibrary Demo");
		primaryStage.show();
	}

	private void checkoutKey() {
		if (t1.getText() != null && !t1.getText().equals("")) {
			kl.checkoutKey(t1.getText());
		}
	}

	private void returnKey() {
		if (t2.getText() != null && !t2.getText().equals("")) {
			kl.returnKey(t2.getText());
		}
	}



	public static void main(String[] args) {
		launch(args);
	}
}
