package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Staff;
import model.UserRole;
import service.SceneManager;
import service.UserService;
import util.AlertUtils;

public class StaffManagementController {

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Integer> staffIdColumn;
    @FXML private TableColumn<Staff, String> firstNameColumn;
    @FXML private TableColumn<Staff, String> lastNameColumn;
    @FXML private TableColumn<Staff, String> usernameColumn;
    @FXML private TableColumn<Staff, String> roleColumn;
    @FXML private TextField searchStaffField;
    @FXML private Button searchStaffButton, addStaffButton, updateStaffButton, deleteStaffButton, promoteToManagerButton, backButton;

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStaffData();
    }

    // Set up columns to match Staff object properties
    private void setupTableColumns() {
        staffIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserRole().toString()));
    }

    // Load staff data from the database into the TableView.
    private void loadStaffData() {
        staffList.setAll(UserService.getAllUsers());
        staffTable.setItems(staffList);
    }

    // Search staff by name or username
    @FXML
    private void searchStaff() {
        String query = searchStaffField.getText().trim();
        if (query.isEmpty()) {
            loadStaffData();
            return;
        }
        staffList.setAll(UserService.searchUsers(query));
    }

    @FXML
    private void addStaff() {
        SceneManager.switchScene("add-user-view.fxml");
        loadStaffData(); // Refresh after adding
    }

    // Open the Update User Form with selected user data
    @FXML
    private void updateStaff() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Update Error", "Please select a staff member to update.", Alert.AlertType.WARNING);
            return;
        }
        SceneManager.switchScene("user-profile-edit-view.fxml", selectedUser);
        loadStaffData(); // Refresh after updating
    }

    // Delete selected staff
    @FXML
    private void deleteStaff() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Delete Error", "Please select a staff member to delete.", Alert.AlertType.WARNING);
            return;
        }

        if (AlertUtils.showConfirmation("Delete User", "Are you sure you want to delete " + selectedUser.getUsername() + "?")) {
            if (UserService.deleteUser(selectedUser)) {
                staffList.remove(selectedUser);
                AlertUtils.showAlert("Success", "User deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.showAlert("Delete Error", "Failed to delete user.", Alert.AlertType.ERROR);
            }
        }
    }

    // Promote selected staff to manager.
    @FXML
    private void promoteToManager() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Promotion Error", "Please select a staff member to promote.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedUser.getUserRole() == UserRole.MANAGER) {
            AlertUtils.showAlert("Already Manager", selectedUser.getUsername() + " is already a manager.", Alert.AlertType.INFORMATION);
            return;
        }

        if (AlertUtils.showConfirmation("Promote to Manager", "Are you sure you want to promote " + selectedUser.getUsername() + " to Manager?")) {
            if (UserService.promoteToManager(selectedUser)) {
                staffTable.refresh();
                AlertUtils.showAlert("Success", selectedUser.getUsername() + " has been promoted to Manager.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.showAlert("Promotion Error", "Failed to promote user.", Alert.AlertType.ERROR);
            }
        }
        loadStaffData();
    }

    @FXML private void goToSettings() {SceneManager.switchScene("manager-view.fxml");}
    @FXML private void logout() {SceneManager.switchScene("login.fxml");}
}