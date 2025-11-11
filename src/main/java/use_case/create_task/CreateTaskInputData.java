package use_case.create_task;
import java.time.LocalDateTime;
/**
 * The Input Data for the CreateTask Use Case.
 */
public class CreateTaskInputData {

    private final String taskName;
    private final LocalDateTime deadline;
    private final String taskGroup;
    private final boolean status;
    private final int priority;


    public CreateTaskInputData(String taskName, LocalDateTime deadline, String taskGroup, boolean status,int priority){
        this.taskName = taskName;
        this.deadline = deadline;
        this.taskGroup = taskGroup;
        this.status = status;
        this.priority = priority;
    }

    String getTaskName() {return taskName;}
    LocalDateTime getDeadline() {return deadline;}
    String getTaskGroup() {return taskGroup;}
    boolean getstatus() {return status;}
    int getPriority() {return priority;}


}
