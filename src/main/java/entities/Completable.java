package entities;

import java.time.temporal.TemporalAccessor;

public interface Completable {
    void complete();

    boolean isCompleted();

    String getName();

    String getDescription();

    TemporalAccessor getStartTime();

    TemporalAccessor getDeadline();

    String getTaskGroup();

    boolean getStatus();

    int getPriority();
}