package view;

import interface_adapter.ViewManagerModel;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * The View Manager for the program (JavaFX version).
 * It listens for property change events in the ViewManagerModel
 * and updates which View should be visible using a StackPane.
 */
public class ViewManager implements PropertyChangeListener {
    private final StackPane viewContainer;
    private final Map<String, Node> views = new HashMap<>();
    private final ViewManagerModel viewManagerModel;

    public ViewManager(StackPane viewContainer, ViewManagerModel viewManagerModel) {
        this.viewContainer = viewContainer;
        this.viewManagerModel = viewManagerModel;
        this.viewManagerModel.addPropertyChangeListener(this);
    }

    /**
     * Register a named view (JavaFX Node) with the manager.
     */
    public void addView(String viewName, Node view) {
        views.put(viewName, view);
        view.setVisible(false);
        view.setManaged(false);
        viewContainer.getChildren().add(view);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final String viewName = (String) evt.getNewValue();
            Platform.runLater(() -> showView(viewName));
        }
    }

    private void showView(String viewName) {
        for (Map.Entry<String, Node> entry : views.entrySet()) {
            boolean isTarget = entry.getKey().equals(viewName);
            entry.getValue().setVisible(isTarget);
            entry.getValue().setManaged(isTarget);
        }
    }
}
