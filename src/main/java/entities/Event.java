package entities;

public interface Event {
    void complete();

    boolean isCompleted();

    String getName();

    String getDescription();
}