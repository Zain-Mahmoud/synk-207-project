package use_case.create_task;
import java.time.LocalDateTime;
/**
 * The Input Data for the CreateTask Use Case.
 */
public class CreateTaskInputData {

    private String taskName;
    private LocalDateTime deadline;
    private String taskGroup;
    private boolean status;
    private int priority;


    public CreateTaskInputData(String taskName, LocalDateTime deadline, String taskGroup, boolean status,int priority){
        this.taskName = taskName;
        this.deadline = deadline;
        this.taskGroup = taskGroup;
        this.status = status;
        this.priority = priority;
    }

    String getTaskName() {return taskName; }

    boolean getstatus() {return status; }

}
