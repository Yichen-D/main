package seedu.taskmaster.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.taskmaster.commons.core.LogsCenter;
import seedu.taskmaster.commons.events.ui.NavigationSelectionChangedEvent;
import seedu.taskmaster.logic.commands.FindCommand;
import seedu.taskmaster.logic.commands.ListCommand;

//@@author A0147967J
public class NavbarPanel extends UiPart {

    private final Logger logger = LogsCenter.getLogger(NavbarPanel.class);
    private static final String FXML = "NavbarPanel.fxml";
    private VBox panel;
    private AnchorPane placeHolderPane;

    private final String NAVBAR_TODAY= " Today";
    private final String NAVBAR_TASKS = " Tasks";
    private final String NAVBAR_DEADLINES = " Deadlines";
    private final String NAVBAR_INCOMING_DEADLINES = " Incoming Deadlines";
    private final String NAVBAR_FLOATING_TASKS = " Floating Tasks";
    private final String NAVBAR_COMPLETED = " Completed";
    
    private final String TODAY_PERIOD = " from today 12am to tomorrow 12am";

    private final ObservableList<String> navbarElement = FXCollections.observableArrayList(NAVBAR_TODAY, NAVBAR_TASKS,
            NAVBAR_DEADLINES, NAVBAR_FLOATING_TASKS, NAVBAR_INCOMING_DEADLINES, NAVBAR_COMPLETED);
    // private variables for navbar commands
    private String command = null;
    private Date day = null;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

    @FXML
    private ListView<String> navbarView;

    public NavbarPanel() {
        super();
    }

    @Override
    public void setNode(Node node) {
        panel = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    public static NavbarPanel load(Stage primaryStage, AnchorPane navbarPlaceholder) {
        NavbarPanel navbarPanel = UiPartLoader.loadUiPart(primaryStage, navbarPlaceholder, new NavbarPanel());
        navbarPanel.configure();
        return navbarPanel;
    }

    private void configure() {
        setConnection();
        addToPlaceHolder();
    }

    private void addToPlaceHolder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(panel);
    }

    private void setConnection() {
        navbarView.setItems(navbarElement);
        navbarView.setCellFactory(listView -> new NavbarViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        navbarView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.fine("Selection in navigation bar panel changed to : '" + newValue + "'");
                raise(new NavigationSelectionChangedEvent(newValue));
            }
        });
    }
    
    public String getNavigationCommand(String navigation) {
        switch (navigation) {

        case NAVBAR_TODAY:
            command = FindCommand.COMMAND_WORD + TODAY_PERIOD;
            return command;
        case NAVBAR_DEADLINES:
            day = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            command = FindCommand.COMMAND_WORD + " by " + formatter.format(day) + " 12am";
            return command;
        case NAVBAR_INCOMING_DEADLINES:
            day = new Date(System.currentTimeMillis() + 24 * 8 * 60 * 60 * 1000);
            command = FindCommand.COMMAND_WORD + " by " + formatter.format(day) + " 12am";
            return command;
        case NAVBAR_FLOATING_TASKS:
            command = FindCommand.COMMAND_WORD + " -F";
            return command;
        case NAVBAR_COMPLETED:
            command = FindCommand.COMMAND_WORD + " -C";
            return command;
        default:
            return ListCommand.COMMAND_WORD;
        }
    }
    // @@author

    class NavbarViewCell extends ListCell<String> {

        public NavbarViewCell() {
        }

        @Override
        protected void updateItem(String li, boolean empty) {
            super.updateItem(li, empty);

            if (empty || li == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(NavbarCard.load(li).getLayout());
            }
        }
    }
}
