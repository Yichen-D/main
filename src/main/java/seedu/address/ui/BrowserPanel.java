package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.util.FxViewUtil;
import seedu.address.model.task.ReadOnlyTask;
import seedu.address.model.task.TaskComponent;
import seedu.address.model.task.TaskDate;
import seedu.address.commons.core.LogsCenter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

//@@author A0147967J-reused
/**
 * The Browser Panel of the App modified to display the agenda.
 */
public class BrowserPanel extends UiPart{

    private static Logger logger = LogsCenter.getLogger(BrowserPanel.class);
    private static final String FXML = "BrowserPanel.fxml";
    private VBox panel;
    private AnchorPane placeHolderPane;
    
    @FXML
    private MyAgenda agenda;

    /**
     * Constructor is kept private as {@link #load(AnchorPane)} is the only way to create a BrowserPanel.
     */
    @Override
    public void setNode(Node node) {
    	panel = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
		return FXML;
       //not applicable
    }
    
    @Override
    public void setPlaceholder(AnchorPane pane) {
    	this.placeHolderPane = pane;
    }

    /**
     * Factory method for creating a Browser Panel.
     * This method should be called after the FX runtime is initialized and in FX application thread.
     * @param placeholder The AnchorPane where the BrowserPanel must be inserted
     */
    public static BrowserPanel load(Stage primaryStage, AnchorPane browserPanelPlaceholder,
            ObservableList<TaskComponent> taskList){
        logger.info("Initializing Agenda");       
        BrowserPanel browserPanel =
                UiPartLoader.loadUiPart(primaryStage, browserPanelPlaceholder, new BrowserPanel());
        browserPanel.initialize(taskList);
        FxViewUtil.applyAnchorBoundaryParameters(browserPanel.agenda, 0.0, 0.0, 0.0, 0.0);
        browserPanel.placeHolderPane.getChildren().add(browserPanel.panel);
        return browserPanel;
    }
    
  //@@author A0147967J
    private void initialize(ObservableList<TaskComponent> taskList){
    	agenda.setDisplayedDateTime(new TaskDate(new Date(System.currentTimeMillis())));
    	loadTaskList(taskList);
    }

    public void loadTaskPage(ReadOnlyTask task) {
        //Deprecated method
    }
    
    public void updateAgenda(TaskDate inputDate, List<TaskComponent> taskList){
    	agenda.setDisplayedDateTime(inputDate);
    	loadTaskList(taskList);
    }
    
    public void reloadAgenda(List<TaskComponent> taskList){
    	loadTaskList(taskList);
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        agenda = null;
    }
    
    public void loadTaskList(List<TaskComponent> taskList){   	
    	agenda.addAllToAgenda(taskList);    		
    }
    
    public MyAgenda getAgenda(){
    	return agenda;
    }
    
}
