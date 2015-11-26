package KeyLibrary;


import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * KeyLibrary: A utility for assigning keys and keeping track of which keys are
 * being used.
 * 
 * @author David Zhou dz54
 *
 */
public class KeyLibrary {
	private Stage stage;
	private GridPane mainPane;
	private GridPane infoPane;
	private HashMap<KeyCode, ImageView> keys;
	private HashMap<KeyCode, ObservableList<String>> library;
	private ListView<String> list;
	private Text currentKey;
	private ResourceBundle myResources = ResourceBundle.getBundle("KeyLibrary/KeyLibrary");
	private boolean multi;
	private boolean selectMode;
	private Scene s;
	private KeyCode selectedKey;
	private Button goButton;
	private ImageView keyboard;

	/**
	 * Constructor
	 * 
	 * @param multi
	 *            determines whether the library will allow the user to have
	 *            multiple strings assigned to the same key
	 */
	public KeyLibrary(boolean multi) {
		this.multi = multi;
		load();
		makePane();
		init();
	}

	/**
	 * Check out a desired key to a string. Can be used when setting up default
	 * controls.
	 * 
	 * @param s
	 *            String to assign the key to.
	 * @param selection
	 *            KeyCode to check out.
	 * @return true if checkout was successful, false otherwise.
	 */
	public boolean checkoutKey(String s, KeyCode selection) {
		if (!library.keySet().contains(selection)) {
			return false;
		}
		if (!multi && !library.get(selection).isEmpty()) {
			return false;
		}
		if (!mainPane.getChildren().contains(keys.get(selection))) {
			mainPane.add(keys.get(selection), 0, 0);
		}
		library.get(selection).add(s);
		return true;
	}

	/**
	 * Allow the user to select a key and assign it to the given string. Useful
	 * for allowing the user to create custom controls.
	 * 
	 * @param s
	 *            String to assign the key to.
	 * @return the KeyCode chosen by the user.
	 */

	public KeyCode checkoutKey(String s) {
		showStage();
		selectMode = true;
		stage.setTitle(myResources.getString("selectionmode") + s);
		stage.showAndWait();
		if (selectedKey == null) {
			return null;
		}
		checkoutKey(s, selectedKey);
		return selectedKey;
	}

	/**
	 * Return a key assignment that is no longer being used.
	 * 
	 * @param s
	 *            String that is currently associated with the selected key.
	 * @param selection
	 *            KeyCode to return
	 * @return true if the return was successful, false otherwise.
	 */

	public boolean returnKey(String s, KeyCode selection) {
		if (library.get(selection).contains(s)) {
			library.get(selection).remove(s);
			if (library.get(selection).isEmpty()) {
				mainPane.getChildren().remove(keys.get(selection));
			}
			return true;
		}
		return false;
	}

	/**
	 * Return all key assignments associated with a String
	 * 
	 * @param s
	 *            Return all keys assigned to this string
	 * @return true if the return was successful, false otherwise.
	 */

	public boolean returnKey(String s) {
		boolean output = false;
		for (KeyCode selection : library.keySet()) {
			if (library.get(selection).contains(s)) {
				library.get(selection).remove(s);
				if (library.get(selection).isEmpty()) {
					mainPane.getChildren().remove(keys.get(selection));
				}
				output = true;
			}
		}
		return output;
	}

	/**
	 * Show the user the current key assignments.
	 */

	public void show() {
		showStage();
		stage.show();
	}

	private void showStage() {
		if (stage != null) {
			stage.close();
		}
		reset();
		stage = new Stage();
		stage.setScene(s);
		stage.setTitle(myResources.getString("title"));
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.setOnCloseRequest(e -> {
			stage = null;
		});
	}

	private void reset() {
		currentKey.setText(myResources.getString("default"));
		selectMode = false;
		selectedKey = null;
		infoPane.getChildren().remove(goButton);
		list.setMaxHeight(keyboard.getBoundsInLocal().getHeight() - currentKey.getBoundsInLocal().getHeight());
	}

	private void load() {
		keys = new HashMap<KeyCode, ImageView>();
		library = new HashMap<KeyCode, ObservableList<String>>();
		Scanner s;
		try {
			s = new Scanner(new File("src/KeyLibrary/keys.txt"));
			while (s.hasNext()) {
				String next = s.nextLine();
				try {
					keys.put(KeyCode.getKeyCode(next), makeImage("KeyLibrary/" + next + ".png"));
					library.put(KeyCode.getKeyCode(next), FXCollections
							.observableArrayList(FXCollections.observableArrayList(new ArrayList<String>())));
				} catch (java.lang.NullPointerException e) {
					e.printStackTrace();
					System.err.println(myResources.getString("imgerror"));
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println(myResources.getString("fileerror"));
		}
	}

	private void makePane() {
		mainPane = new GridPane();
		keyboard = makeImage("KeyLibrary/keyboard.png");
		mainPane.add(keyboard, 0, 0);
		infoPane = new GridPane();
		currentKey = new Text(myResources.getString("default"));
		currentKey
				.setFont(new Font(Font.getDefault().getName(), Double.parseDouble(myResources.getString("fontsize"))));
		list = new ListView<String>();
		list.setFocusTraversable(false);
		list.setMaxWidth(Double.parseDouble(myResources.getString("infowidth")));
		infoPane.add(currentKey, 0, 0);
		infoPane.add(list, 0, 1);
		mainPane.add(infoPane, 1, 0);
		mainPane.setHgap(Double.parseDouble(myResources.getString("hgap")));
	}

	private void init() {
		s = new Scene(mainPane);
		s.setOnKeyPressed(e -> select(e));
		goButton = new Button(myResources.getString("go"));
		goButton.setOnAction(e -> assign());
		goButton.setFocusTraversable(false);
		goButton.setMinWidth(Double.parseDouble(myResources.getString("infowidth")));
	}

	private void assign() {
		selectedKey = KeyCode.getKeyCode(currentKey.getText());
		stage.close();
		stage = null;
	}

	private void select(KeyEvent e) {
		if (library.keySet().contains(e.getCode())) {
			if (selectMode && !infoPane.getChildren().contains(goButton)) {
				infoPane.add(goButton, 0, 2);
				list.setMaxHeight(keyboard.getBoundsInLocal().getHeight() - currentKey.getBoundsInLocal().getHeight()
						- Double.parseDouble(myResources.getString("shrinkheight")));
			}
			KeyCode code = e.getCode();
			list.setItems(library.get(code));
			currentKey.setText(code.getName());
		}
	}

	private ImageView makeImage(String s) {
		ImageView image = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(s)));
		image.setFitWidth(Double.parseDouble(myResources.getString("width")));
		image.setPreserveRatio(true);
		image.setSmooth(true);
		image.setCache(true);
		return image;
	}
}
