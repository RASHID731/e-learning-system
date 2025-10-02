package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Represents the state of editing a component's contents on the canvas.
 * Subclass of CanvasState.
 */
public class EditComponentContentsState extends CanvasState {
    // The component to edit the contents of
    private DrawableComponent componentToEdit;
    // The dialog box to edit the contents of the component
    private Stage dialog;

    /**
     * Constructor for EditComponentContentsState.
     *
     * @param canvasContentManagementController The controller for the main window that uses this CanvasState object.
     * @param componentToEdit                    The component to edit the contents of.
     */
    public EditComponentContentsState(CanvasContentManagementController canvasContentManagementController, DrawableComponent componentToEdit) {
        super(canvasContentManagementController);
        this.componentToEdit = componentToEdit;
        dialog = new Stage();
        openDialogBox();
    }

    /**
     * Opens a dialog box to edit the contents of the selected component.
     */
    private void openDialogBox() {
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Get the dialog box contents
        VBox vBox = componentToEdit.fetchUpdateContentsDialog();
        Button doneButton = new Button("Done");
        doneButton.setId("donebutton");

        // Set activities to be performed on completion of the edit
        doneButton.setOnMouseClicked((e) -> exitState());
        vBox.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                exitState();
            }
        });

        // Create and display the dialog box
        vBox.getChildren().add(doneButton);
        Scene dialogScene = new Scene(vBox);
        dialogScene.getStylesheets().add(String.valueOf(getClass().getResource("/de/unirostock/usecaseeditor/drawablecomponent/drawablecomponent.css")));
        dialog.setScene(dialogScene);
        dialog.setOnCloseRequest((e) -> exitState());
        dialog.show();
    }

    /**
     * Performs necessary operations when exiting the EditComponentContentsState.
     * Updates the component's contents, closes the dialog box, and sets the current canvas state to SelectComponentState.
     * Executes the componentAddedListener and stopEditingListener if they are not null.
     * Resets clickComponent, highlightedComponent, and redraws the canvas.
     */
    @Override
    public void exitState() {
        componentToEdit.updateContents();
        dialog.close();
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));

        // Save the changes
        if (canvasContentManagementController.componentAddedListener != null) {
            canvasContentManagementController.componentAddedListener.run();
        }
        // Start polling again
        if (canvasContentManagementController.stopEditingListener != null) {
            canvasContentManagementController.stopEditingListener.run();
        }
        canvasContentManagementController.clickComponent = false;
        canvasContentManagementController.setHighlightedComponent(null);
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
    }
}
