package interface_adapter.view_stats;

import interface_adapter.ViewModel;

public class ViewStatsViewModel extends ViewModel<ViewStatsState> {

    public ViewStatsViewModel() {
        super("view stats");
        setState(new ViewStatsState());
    }
}
