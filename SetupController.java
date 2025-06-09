import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;


// TODO
public class SetupController {

    private Delegate currentlyEditing = null;
    private final ObservableList<Delegate> delegateObservableList = Committee.getDelegates();

    @Getter
    private VBox view;
    private TextField nameField;
    private CheckBox pnvCheckBox;
    private TableView<Delegate> tableView;
    private Button submitButton;
    private Button clearButton;
    private Button removeButton;
    private TextField topicField;
    private Button addTopicButton;
    private ListView<String> topicListView;
    private final ObservableList<String> topicObservableList = javafx.collections.FXCollections.observableArrayList();

    public SetupController() {
        // Initialize with Committee's delegate list
        createView();
        setupBindings();
    }

    private void createView() {
        // Form fields
        nameField = new TextField();
        pnvCheckBox = new CheckBox("Present and Voting");

        // Form labels
        Label nameLabel = new Label("Name:");

        // Form grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.addRow(0, nameLabel, nameField);
        formGrid.addRow(1, pnvCheckBox);

        // Buttons
        submitButton = new Button("Register");
        clearButton = new Button("Clear");
        removeButton = new Button("Remove");
        HBox buttonBox = new HBox(10, submitButton, clearButton, removeButton);

        // Table view to display registered Delegates
        tableView = new TableView<>();
        tableView.setItems(delegateObservableList);

        // Table columns
        TableColumn<Delegate, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Delegate, Boolean> pnvCol = new TableColumn<>("Present and Voting");
        pnvCol.setCellValueFactory(new PropertyValueFactory<>("presentAndVoting"));

        tableView.getColumns().addAll(nameCol, pnvCol);

        // Edit button column
        TableColumn<Delegate, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Main.L.info("Button Clicked");
                    Delegate delegate = getTableView().getItems().get(getIndex());
                    editDelegate(delegate);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        tableView.getColumns().add(actionsCol);

        topicField = new TextField();
        topicField.setPromptText("Enter topic");
        addTopicButton = new Button("Add Topic");
        topicListView = new ListView<>(topicObservableList);
        topicListView.setPrefHeight(100);

        HBox topicInputBox = new HBox(10, topicField, addTopicButton);
        VBox topicSection = new VBox(5, new Label("Topics"), topicInputBox, topicListView);



        // Main layout
        view = new VBox(20);
        view.getChildren().addAll(
                new Label("Registration Form"),
                formGrid,
                buttonBox,
                new Label("Registered Delegates"),
                tableView,
                topicSection
        );
        view.setPadding(new Insets(20));
    }

    private void setupBindings() {
        submitButton.setOnAction(e -> {
            Main.L.info("Button Clicked");
            if (validateFields()) {
                if (currentlyEditing != null) {
                    // Update existing delegate
                    updateDelegate(currentlyEditing);
                    currentlyEditing = null;
                    submitButton.setText("Register");
                } else {
                    // Add new delegate
                    addDelegate();
                }
                clearFields();
            }
        });

        clearButton.setOnAction(e -> {
            Main.L.info("Button Clicked");
            clearFields();
            currentlyEditing = null;
            submitButton.setText("Register");
        });

        removeButton.setOnAction(e -> {
            Main.L.info("Button Clicked");
            Delegate selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                removeDelegate(selected);
            }
        });

        // Allow row selection for potential editing
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadDelegateToForm(newSelection);
            }
        });

        addTopicButton.setOnAction(e -> {
            String topic = topicField.getText().trim();
            if (!topic.isEmpty() && !topicObservableList.contains(topic)) {
                topicObservableList.add(topic);
                topicField.clear();
                Main.L.info("Added topic: " + topic);
                Committee.addResolutionTopic(topic);
            } else {
                showAlert("Topic cannot be empty or already exists!");
            }
        });
    }

    private boolean validateFields() {
        if (nameField.getText().isEmpty()) {
            showAlert("Name is required!");
            return false;
        }

        // Check if delegate already exists (when adding new, not when editing)
        if (currentlyEditing == null && Committee.find(nameField.getText()) != null) {
            showAlert("Delegate with this name already exists!");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addDelegate() {
        Delegate delegate = new Delegate(
                nameField.getText(),
                pnvCheckBox.isSelected()
        );
        Main.L.info("Adding delegate " + delegate.getName() + ", total delegates: " + Committee.getDelegates().size() + 1);
        Committee.addDelegate(delegate);
        // delegateObservableList.setAll(Committee.getDelegates()); // Remove this line
    }

    private void editDelegate(Delegate delegate) {
        Main.L.info("Editing delegate " + delegate.getName());
        currentlyEditing = delegate;
        loadDelegateToForm(delegate);
        submitButton.setText("Update");
    }

    private void updateDelegate(Delegate delegate) {
        Main.L.info("Updating delegate " + delegate.getName());
        String newName = nameField.getText();
        // Prevent changing to a name that already exists (except for self)
        Delegate existing = Committee.find(newName);
        if (existing != null && existing != delegate) {
            showAlert("Delegate with this name already exists!");
            return;
        }
        delegate.setName(newName);
        delegate.setPresentAndVoting(pnvCheckBox.isSelected());
    }

    private void removeDelegate(Delegate delegate) {
        Main.L.info("Removing delegate " + delegate.getName() + ", total delegates: " + Committee.getDelegates().size());
        Committee.removeDelegate(delegate);
        clearFields();
    }

    private void loadDelegateToForm(Delegate delegate) {
        Main.L.info("Loading delegate" + delegate.getName() + " to form");
        nameField.setText(delegate.getName());
        pnvCheckBox.setSelected(delegate.isPresentAndVoting());
    }

    private void clearFields() {
        Main.L.info("Fields cleared");
        nameField.clear();
        pnvCheckBox.setSelected(false);
        tableView.getSelectionModel().clearSelection();
        currentlyEditing = null;
        submitButton.setText("Register");
    }
}