package duke;

import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The Duke program can record down todos, deadlines and events and save it on your computer.
 *
 * @author  Hope Leong
 * @version 0.1
 * @since   27/8/2020
 */
public class Duke extends Application {
    private Storage storage;
    private TaskList taskList;
    private Ui ui;
    private JavafxUi javafxUi;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Duke constructor to initialize a Duke object, initializes a Ui, Storage and TaskList object.
     * @exception DukeException On input error and file path error.
     */
    public Duke() throws DukeException {
        String logo =
                " ____        _        \n"
                        + "|  _ \\ _   _| | _____ \n"
                        + "| | | | | | | |/ / _ \\\n"
                        + "| |_| | |_| |   <  __/\n"
                        + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
        ui = new Ui();
        javafxUi = new JavafxUi();
        ui.drawLine();
        storage = new Storage();
        taskList = new TaskList(storage.loadFile());

    }

    @Override
    public void start(Stage stage) throws DukeException {

        // The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        sendButton = new Button("Send");
        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);
        scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();

        // Formatting the window to look as expected
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);
        mainLayout.setPrefSize(400.0, 600.0);
        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);
        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));


        // adding functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });
        userInput.setOnAction((event) -> {
            handleUserInput();
        });
    }


    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getDukeDialog(dukeText, new ImageView(duke))
        );
        userInput.clear();
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    private String getResponse(String input) {
        try {
            return javafxBot(input);
        } catch (DukeException e) {
            return e.getMessage();
        }
    }

    /**
     * Main method which runs the bot
     * @param args user input
     * @throws DukeException if bot does not understand user input
     */
    public static void main(String[] args) {
        Application.launch(Duke.class, args);
    }


    /**
     * Bot method which handles the inputs and responds to the user while calling the appropriate classes
     * @exception DukeException On input error and file path error.
     */
    public void bot() throws DukeException {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String input = sc.nextLine();
            // splits the input into the different words in order to understand what the user wants
            String first = input.split(" ")[0];
            // user exits the program
            if (input.equals("bye")) {
                ui.bye();
                break;
            // user sees the list of tasks
            } else if (input.equals("list")) {
                ui.printList(taskList.getList());
            // user sets a specific task as completed
            } else if (input.split(" ")[0].equals("done")) {
                ui.doneTask(taskList.done(Integer.parseInt(input.split(" ")[1])));
                ui.listCount(taskList.countList());
                ui.drawLine();
                storage.saveFile(taskList.getList());
            // user creates a new task
            } else if (first.equals("todo") || first.equals("deadline") || first.equals("event")) {
                ui.addTask(taskList.add(input));
                ui.listCount(taskList.countList());
                ui.drawLine();
                storage.saveFile(taskList.getList());
            // user deletes a task
            } else if (first.equals("delete")) {
                ui.deleteTask(taskList.delete(input));
                ui.listCount(taskList.countList());
                ui.drawLine();
                storage.saveFile(taskList.getList());
            // user searches for a keyword
            } else if (first.equals("find")) {
                ui.foundWord(taskList.findWord(input));
            // user types something the bot does not understand
            } else {
                throw new DukeException("Sorry I don't know what you mean");
            }
        }
    }

    /**
     * javafxBot method which handles the inputs and responds to the user while calling the appropriate classes
     * in the javafx interface
     * @param input user input
     * @exception DukeException On input error and file path error.
     */
    public String javafxBot(String input) throws DukeException {
        // String to output
        String output = "";
        // splits the input into the different words in order to understand what the user wants
        String first = input.split(" ")[0];
        // user exits the program
        if (input.equals("bye")) {
            output += javafxUi.bye();
        // user sees the list of tasks
        } else if (input.equals("list")) {
            output += javafxUi.printList(taskList.getList());
            output += javafxUi.drawLine();
        // user sets a specific task as completed
        } else if (input.split(" ")[0].equals("done")) {
            output += javafxUi.doneTask(taskList.done(Integer.parseInt(input.split(" ")[1])));
            output += javafxUi.listCount(taskList.countList());
            output += javafxUi.drawLine();
            storage.saveFile(taskList.getList());
        // user creates a new task
        } else if (first.equals("todo") || first.equals("deadline") || first.equals("event")) {
            output += javafxUi.addTask(taskList.add(input));
            output += javafxUi.listCount(taskList.countList());
            output += javafxUi.drawLine();
            storage.saveFile(taskList.getList());
        // user deletes a task
        } else if (first.equals("delete")) {
            output += javafxUi.deleteTask(taskList.delete(input));
            output += javafxUi.listCount(taskList.countList());
            output += javafxUi.drawLine();
            storage.saveFile(taskList.getList());
        // user searches for a keyword
        } else if (first.equals("find")) {
            output += javafxUi.foundWord(taskList.findWord(input));
        // user types something the bot does not understand
        } else {
            throw new DukeException("Sorry I don't know what you mean");
        }
        return output;
    }
}
