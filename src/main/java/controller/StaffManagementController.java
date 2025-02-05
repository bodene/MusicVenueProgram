package controller;

import dao.UserDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Manager;
import model.Staff;
import model.UserRole;
import service.SceneManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StaffManagementController {

    @FXML
    private TableView<Staff> staffTable;
    @FXML
    private TableColumn<Staff, Integer> staffIdColumn;
    @FXML
    private TableColumn<Staff, String> firstNameColumn;
    @FXML
    private TableColumn<Staff, String> lastNameColumn;
    @FXML
    private TableColumn<Staff, String> usernameColumn;
    @FXML
    private TableColumn<Staff, String> roleColumn;
    @FXML
    private TextField searchStaffField;
    @FXML
    private Button searchStaffButton, addStaffButton, updateStaffButton, deleteStaffButton, promoteToManagerButton, backButton;

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStaffData();
    }

    /**
     * Set up columns to match Staff object properties.
     */
    private void setupTableColumns() {
        staffIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserRole().toString()));
    }

    /**
     * Load staff data from the database into the TableView.
     */
    private void loadStaffData() {
        staffList = UserDAO.getAllUsers();
        staffTable.setItems(staffList);
    }

    /**
     * Search staff by name or username.
     */
    @FXML
    private void searchStaff() {
        String query = searchStaffField.getText().trim();
        if (query.isEmpty()) {
            loadStaffData();
            return;
        }

        List<Staff> filteredStaff = UserDAO.searchUsers(query);
        staffList.setAll(filteredStaff);
    }

    /**
     * Open the Add User Form.
     */
    @FXML
    private void addStaff() {
        SceneManager.switchScene("add-user-view.fxml");
        loadStaffData(); // Refresh after adding
    }

    /**
     * Open the Update User Form with selected user data.
     */
    @FXML
    private void updateStaff() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Update Error", "Please select a staff member to update.", Alert.AlertType.WARNING);
            return;
        }

        SceneManager.switchScene("user-profile-edit-view.fxml", selectedUser);
        loadStaffData(); // Refresh after updating
    }

    /**
     * Delete selected staff.
     */
    @FXML
    private void deleteStaff() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Delete Error", "Please select a staff member to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Are you sure you want to delete " + selectedUser.getUsername() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                UserDAO.deleteUser(selectedUser.getUserId());
                staffList.remove(selectedUser);
            } catch (SQLException e) {
                showAlert("Delete Error", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Promote selected staff to manager.
     */
    @FXML
    private void promoteToManager() {
        Staff selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Promotion Error", "Please select a staff member to promote.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedUser.getUserRole() == UserRole.MANAGER) {
            showAlert("Already Manager", selectedUser.getUsername() + " is already a manager.", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Promote to Manager");
        confirm.setHeaderText("Are you sure you want to promote " + selectedUser.getUsername() + " to Manager?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                UserDAO.updateUserRole(selectedUser.getUserId(), UserRole.MANAGER);
                selectedUser.setUserRole(UserRole.MANAGER);
                staffTable.refresh();
            } catch (SQLException e) {
                showAlert("Promotion Error", "Failed to promote user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Show an alert box.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Return to the main dashboard.
     */
    @FXML
    private void goToSettings() {
        SceneManager.switchScene("manager-view.fxml");
    }

    /**
     * Logout and return to the login screen.
     */
    @FXML
    private void logout() {
        SceneManager.switchScene("login.fxml");
    }
}
