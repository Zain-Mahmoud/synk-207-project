package entities;

import java.time.LocalDateTime;

public interface Completable {

    void complete();

    boolean isCompleted();

    String getName();

    String getDescription();

    /**
     * Returns the due data
     * @return
     */
    LocalDateTime getDueDate();

    int getPriority();


    LocalDateTime getStartTime();

}