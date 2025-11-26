package use_case.view_stats;

public class ViewStatsInputData {

    private final String username;

    public ViewStatsInputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
