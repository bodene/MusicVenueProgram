package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import model.UserRole;
import service.SceneManager;
import service.UserService;
import util.AlertUtils;


/**
 * Controller class for managing staff operations.
 * <p>
 * This class provides functionalities for displaying staff details in a table, searching, adding,
 * updating, deleting, and promoting staff members. It leverages the {@link UserService} for performing
 * database operations and {@link SceneManager} for scene transitions.
 * </p>
 *
 * @author  Bodene Downie
 * @version 1.0
 */
public class StaffManagementController {

    /* STAFF TABLE FIELDS */
    @FXML private TableView<User> staffTable;
    @FXML private TableColumn<User, Integer> staffIdColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TextField searchStaffField;

    /** Observable list holding staff data. */
    private ObservableList<User> staffList = FXCollections.observableArrayList();

    /**
     * Initialises the staff management view.
     * <p>
     * This method is automatically called after the FXML file is loaded. It sets up the table columns and loads
     * staff data from the database.
     * </p>
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        loadStaffData();
    }

    /**
     * Configures the table columns to match the properties of the {@code User} object.
     * <p>
     * This method maps the staff ID, first name, last name, username, and role properties to the corresponding table columns.
     * </p>
     */
    private void setupTableColumns() {

        staffIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUserId()));
        firstNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));
    }

    /**
     * Loads staff data from the database.
     * <p>
     * This method retrieves all users via the {@link UserService} and updates the staff table.
     * </p>
     */
    private void loadStaffData() {
        staffList.setAll(UserService.getAllUsers());
        staffTable.setItems(staffList);
    }

    /**
     * Searches staff by name or username.
     * <p>
     * This method is invoked when the user types in the search field. It updates the staff list based on the query.
     * If the query is empty, all users are displayed; otherwise, a filtered list is shown.
     * </p>
     */
    @FXML
    private void searchStaff() {
        String query = searchStaffField.getText().trim();
        staffList.setAll(query.isEmpty() ? UserService.getAllUsers() : UserService.searchUsers(query));
    }

    /**
     * Navigates to the add user view.
     * <p>
     * This method switches the scene to "add-user-view.fxml" using the {@link SceneManager} and refreshes the staff data afterward.
     * </p>
     */
    @FXML
    private void addStaff() {
        SceneManager.switchScene("add-user-view.fxml");
        loadStaffData(); // Refresh after adding a user.
    }

    /**
     * Opens the update user form with the selected user's data.
     * <p>
     * If no user is selected, a warning alert is displayed. Otherwise, the scene is switched to "user-profile-edit-view.fxml"
     * with the selected user passed as a parameter.
     * </p>
     */
    @FXML
    private void updateStaff() {
        User selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Update Error", "Please select a staff member to update.", Alert.AlertType.WARNING);
            return;
        }
        // Switch scene and pass the selected user to the update view.
        SceneManager.switchScene("user-profile-edit-view.fxml", selectedUser);
        loadStaffData(); // Refresh after updating.
    }

    /**
     * Deletes the selected staff member.
     * <p>
     * If no user is selected, a warning alert is displayed. After confirming deletion with the user, the user is deleted
     * via the {@link UserService}. Upon success or failure, an appropriate alert is shown.
     * </p>
     */
    @FXML
    private void deleteStaff() {
        User selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Delete Error", "Please select a staff member to delete.", Alert.AlertType.WARNING);
            return;
        }

        // Confirm deletion with the user.
        if (AlertUtils.showConfirmation("Delete User", "Are you sure you want to delete " + selectedUser.getUsername() + "?")) {
            if (UserService.deleteUser(selectedUser)) {
                staffList.remove(selectedUser);
                AlertUtils.showAlert("Success", "User deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                AlertUtils.showAlert("Delete Error", "Failed to delete user.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Promotes the selected staff member to a manager role.
     * <p>
     * This method first checks if a user is selected and then verifies if the user is already a manager.
     * If not, it prompts for confirmation before promoting the user via the {@link UserService}. The staff table
     * is refreshed upon completion.
     * </p>
     */
    @FXML
    private void promoteToManager() {
        User selectedUser = staffTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showAlert("Promotion Error", "Please select a staff member to promote.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedUser.getRole() == UserRole.MANAGER) {
            AlertUtils.showAlert("Already Manager", selectedUser.getUsername() + " is already a manager.", Alert.AlertType.INFORMATION);
            return;
        }

        // Confirm promotion with the user.
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

    /**
     * Navigates to the settings view.
     * <p>
     * This method switches the scene to "manager-view.fxml" using the {@link SceneManager}.
     * </p>
     */
    @FXML private void goToSettings() {
        SceneManager.switchScene("manager-view.fxml");
    }

    /**
     * Logs out the current user.
     * <p>
     * This method switches the scene to "login.fxml", effectively logging the user out.
     * </p>
     */
    @FXML private void logout() {
        SceneManager.switchScene("login.fxml");
    }
}