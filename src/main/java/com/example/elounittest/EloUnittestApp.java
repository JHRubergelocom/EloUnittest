package com.example.elounittest;

import elounittest.*;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class EloUnittestApp extends Application {
    static final String LABEL_STYLE = "-fx-font-weight: bold; -fx-font-style: regular; -fx-font-size: 18px";
    static final String LISTVIEW_STYLE = "-fx-font-style: regular; -fx-font-size: 14px";
    static final String TEXTFIELD_STYLE = "-fx-font-style: regular; -fx-font-size: 12px";

    private final Stacks stacks;

    private final EloProperties eloProperties = new EloProperties();
    private final EloService eloService = new EloService();

    private final Label lblStack = new Label();
    private final ComboBox<String> cmbStack = new ComboBox<>();

    private final Label lblEloCli = new Label();
    private final ListView<String> listvEloCli = new ListView<>();

    private final Label lblUnittestTools = new Label();
    private final ListView<String> listvUnittestTools = new ListView<>();

    private final Label lblEloServices = new Label();
    private final ListView<String> listvEloServices = new ListView<>();

    private final TextField txtPattern = new TextField();
    private final CheckBox chkCaseSensitiv = new CheckBox("Case sensitiv");

    private final TabPane tabPane = new TabPane();

    private final ProgressBar pgBar = new ProgressBar(0);
    private final TextField txtProgress = new TextField();

    public EloUnittestApp()  throws JSONException {
        this.stacks = new Stacks("Stacks.json");
    }

    private void fillListView(Label label, String lblText, ListView<String> listview, List <String> entries) {
        label.setText(lblText);
        label.setStyle(LABEL_STYLE);

        listview.setItems(FXCollections.observableArrayList(entries));
        listview.setStyle(LISTVIEW_STYLE);
    }

    private void fillComboBox(Label label, String lblText, ComboBox<String> combobox, List<String> entries) {
        label.setText(lblText);
        label.setStyle(LABEL_STYLE);

        combobox.setItems(FXCollections.observableArrayList(entries));
        combobox.setStyle(LISTVIEW_STYLE);
    }

    private void fillComboBoxStack() {
        final ArrayList<String> entries = new ArrayList<>();

        stacks.getStacks().forEach((n, s) -> {
            entries.add(s.getStack());
        });

        fillComboBox(lblStack, "Stack", cmbStack, entries);
    }

    private void initComboBoxStack() {
        fillComboBoxStack();
        String selectedStackName = eloProperties.getSelectedStack();
        if (selectedStackName != null) {
            cmbStack.getSelectionModel().select(selectedStackName);
        } else {
            cmbStack.getSelectionModel().select(0);
        }

        cmbStack.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedStack(newValue);
            fillListViewEloCli();
        });
    }

    private void fillListViewEloCli() {
        String sname = cmbStack.getSelectionModel().getSelectedItem();

        final ArrayList<String> entries = new ArrayList<>();
        Stack p = stacks.getStacks().get(sname);

        p.getEloCommands().forEach((n, c) -> {
            entries.add(c.getName());
        });

        fillListView(lblEloCli, "ELO Cli", listvEloCli, entries);
    }

    private void initListViewEloCli() {
        fillListViewEloCli();
        String selectedEloCliName = eloProperties.getSelectedEloCli();
        if (selectedEloCliName != null) {
            listvEloCli.getSelectionModel().select(selectedEloCliName);
        } else {
            listvEloCli.getSelectionModel().select(0);
        }

        listvEloCli.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvEloCli.getSelectionModel().getSelectedItem();
                String sname = cmbStack.getSelectionModel().getSelectedItem();
                Stack stack = stacks.getStacks().get(sname);
                EloCommand eloCommand = stack.getEloCommands().get(currentItemSelected);
                eloService.runEloCommand(eloCommand, stack, stacks, this);
            }
        });

        listvEloCli.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedEloCli(newValue);
        });

    }

    private void fillListViewUnittestTools() {
        final ArrayList<String> entries = new ArrayList<>();
        entries.add("show");
        entries.add("matching");
        entries.add("create");
        entries.add("ranger");
        entries.add("gitpullall");
        entries.add("search");
        entries.add("export");
        fillListView(lblUnittestTools, "Unittest Tools", listvUnittestTools, entries);
    }

    private void initListViewUnittestTools() {
        fillListViewUnittestTools();
        String selectedUnittestToolsName = eloProperties.getSelectedUnittestTools();
        if (selectedUnittestToolsName != null) {
            listvUnittestTools.getSelectionModel().select(selectedUnittestToolsName);
        } else {
            listvUnittestTools.getSelectionModel().select(0);
        }

        listvUnittestTools.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvUnittestTools.getSelectionModel().getSelectedItem();
                String pname = cmbStack.getSelectionModel().getSelectedItem();
                Stack stack = stacks.getStacks().get(pname);
                eloService.runUnittestTools(currentItemSelected, stack, stacks, this);
            }
        });

        listvUnittestTools.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedUnittestTools(newValue);
        });

    }

    private void fillListViewEloServices() {
        final ArrayList<String> entries = new ArrayList<>();
        entries.add("Application Server");
        entries.add("Admin Console");
        entries.add("App Manager");
        entries.add("Webclient");
        entries.add("KnowledgeBoard");
        fillListView(lblEloServices, "Elo Services", listvEloServices, entries);
    }

    private void initListViewEloServices() {
        fillListViewEloServices();
        String selectedEloServicesName = eloProperties.getSelectedEloServices();
        if (selectedEloServicesName != null) {
            listvEloServices.getSelectionModel().select(selectedEloServicesName);
        } else {
            listvEloServices.getSelectionModel().select(0);
        }

        listvEloServices.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                String currentItemSelected = listvEloServices.getSelectionModel().getSelectedItem();
                String pname = cmbStack.getSelectionModel().getSelectedItem();
                Stack stack = stacks.getStacks().get(pname);
                eloService.runEloServices(currentItemSelected, stack, stacks, this);
            }
        });

        listvEloServices.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Selected item: " + newValue);
            eloProperties.setSelectedEloServices(newValue);
        });

    }

    private void initSearchPattern() {
        txtPattern.setText(eloProperties.getPattern());
        txtPattern.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println(" Text Changed to  " + newValue + "\n");
            eloProperties.setPattern(newValue);
        });

        chkCaseSensitiv.setSelected(eloProperties.getCaseSensitiv());
        chkCaseSensitiv.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            System.out.println(" CheckBox Changed to  " + newValue + "\n");
            eloProperties.setCaseSensitiv(newValue);
        });
    }

    public void setDisableControls(boolean value) {
        lblStack.setDisable(value);
        cmbStack.setDisable(value);
        lblEloCli.setDisable(value);
        listvEloCli.setDisable(value);
        lblUnittestTools.setDisable(value);
        listvUnittestTools.setDisable(value);
        lblEloServices.setDisable(value);
        listvEloServices.setDisable(value);
        txtPattern.setDisable(value);
        chkCaseSensitiv.setDisable(value);
        tabPane.setDisable(value);
    }

    public static void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void setTextFieldColor(TextField textField, String color) {

        if(!color.contentEquals("")) {
            textField.setStyle("-fx-text-fill: " + color);
        } else {
            textField.setStyle("-fx-text-fill: regular");
        }
    }

    public TextField getTxtPattern() {
        return txtPattern;
    }

    public CheckBox getChkCaseSensitiv() {
        return chkCaseSensitiv;
    }

    public ProgressBar getPgBar() {
        return pgBar;
    }

    public TextField getTxtProgress() {
        return txtProgress;
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane root = new GridPane();

        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints(200);
            root.getColumnConstraints().add(column);
        }

        root.getRowConstraints().add(new RowConstraints(30));
        root.getRowConstraints().add(new RowConstraints(320));

        root.setPadding(new Insets(10, 10, 10, 10));
        root.setHgap(7);
        root.setVgap(7);

        initComboBoxStack();

        initListViewEloCli();

        initListViewUnittestTools();

        initListViewEloServices();

        initSearchPattern();

        root.add(lblStack, 0, 0);
        root.add(cmbStack, 1, 0);
        GridPane.setHalignment(cmbStack, HPos.RIGHT);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox vbox = new VBox(lblEloCli, listvEloCli);
        Tab tab = new Tab("ELO Cli", vbox);
        tabPane.getTabs().add(tab);

        vbox = new VBox(lblUnittestTools, listvUnittestTools);
        tab = new Tab("Unittest Tools", vbox);
        tabPane.getTabs().add(tab);

        vbox = new VBox(lblEloServices, listvEloServices);
        tab = new Tab("ELO Services", vbox);
        tabPane.getTabs().add(tab);

        root.add(tabPane, 0, 1, 2, 1);

        root.add(txtProgress, 0, 2, 2, 1);
        txtProgress.setStyle(TEXTFIELD_STYLE);
        txtProgress.setEditable(false);

        pgBar.setMaxWidth(Double.MAX_VALUE);
        root.add(pgBar, 0, 3, 2, 1);

        root.add(txtPattern, 0, 4);
        txtPattern.setStyle(TEXTFIELD_STYLE);
        root.add(chkCaseSensitiv, 1, 4);
        GridPane.setHalignment(chkCaseSensitiv, HPos.RIGHT);

        Scene scene = new Scene(root, 430, 460);

        primaryStage.setTitle("ELO Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


}
