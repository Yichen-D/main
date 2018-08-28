package seedu.taskmaster.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.taskmaster.model.task.ReadOnlyTask;
import seedu.taskmaster.model.task.RecurringType;
import seedu.taskmaster.model.task.Task;
import seedu.taskmaster.model.task.TaskDate;
import seedu.taskmaster.model.task.TaskOccurrence;
import seedu.taskmaster.model.task.TaskType;

public class TaskCard extends UiPart {

    private static final String FXML = "TaskListCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label tags;
    @FXML
    private Label startDate;
    @FXML
    private Label endDate;
    @FXML
    private Label recurringType;
    @FXML
    private Label period;

    private ReadOnlyTask task;
    private int displayedIndex;
    private TaskOccurrence dateComponent;

    public TaskCard() {
    }

    public static TaskCard load(TaskOccurrence taskComponent, int displayedIndex) {
        TaskCard card = new TaskCard();
        card.task = taskComponent.getTaskReference();
        card.displayedIndex = displayedIndex;
        card.dateComponent = taskComponent;
        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        name.setText(task.getName().fullName);
        id.setText(displayedIndex + ". ");
        tags.setText(task.tagsString());
        initializeDate();
        initializeRecurringType();
        initializaRecurringPeriod();
        setCellColor();
    }

    private void initializeRecurringType() {
        String recurringTypeToShow = "";
        if (!task.getRecurringType().equals(RecurringType.NONE)) {
            recurringTypeToShow = task.getRecurringType().name();
        }
        recurringType.setText(recurringTypeToShow);
    }
    
    //@@author A0147967J
    private void initializaRecurringPeriod() {
        if(task.getRecurringPeriod() == Task.NO_RECURRING_PERIOD && !task.getRecurringType().equals(RecurringType.NONE)){
            period.setText("Always");
        } else {
            period.setText("");
        }
    }

    
    private void initializeDate() {
        if (!dateComponent.getStartDate().isPresent()) {
            startDate.setText("");
        } else {
            startDate.setText(dateComponent.getStartDate().getFormattedDate());
        }
        if (!dateComponent.getEndDate().isPresent()) {
            endDate.setText("");
        } else {
            endDate.setText(dateComponent.getEndDate().getFormattedDate());
        }
    }

    /**
     * Sets cell color for the task list. Style the css here to prevent
     * overriding.
     */
    private void setCellColor() {
        // normal non-floating task
        cardPane.setStyle("-fx-background-color : rgba(110, 196, 219, 0.3);");
        // Deadline
        if (dateComponent.isDeadline())
            cardPane.setStyle("-fx-background-color : rgba(250, 124, 146, 0.3);");
        // Floating task
        if (task.getTaskType() == TaskType.FLOATING)
            cardPane.setStyle("-fx-background-color : rgba(255, 247, 192, 0.3);");
        // Blocked Slot
        if (dateComponent.isBlockedSlot())
            cardPane.setStyle("-fx-background-color : rgba(148, 93, 96, 0.3);");
        // Completed
        if (dateComponent.isArchived()) {
            cardPane.setStyle("-fx-background-color : rgba(102,171,140,0.3);");
            name.setStyle("-fx-text-fill : derive(#373737, 20%);");
            id.setStyle("-fx-text-fill : derive(#373737, 20%);");
            startDate.setStyle("-fx-text-fill : derive(#373737, 20%);");
            endDate.setStyle("-fx-text-fill : derive(#373737, 20%);");
            recurringType.setStyle("-fx-text-fill : derive(#373737, 20%);");
        }
    }

    // @@author
    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        // cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
