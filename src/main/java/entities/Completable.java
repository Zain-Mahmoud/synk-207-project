package entities;

public interface Completable {
    void complete();

    boolean isCompleted();

    String getName();

    String getDescription();

    String getTitle();

    Object getDueDate();
}