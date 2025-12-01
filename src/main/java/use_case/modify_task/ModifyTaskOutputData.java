package use_case.modify_task;

import entities.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ModifyTaskOutputData{

    private ArrayList<Task> taskList;

    public ModifyTaskOutputData(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public ArrayList<ArrayList<String>> getTaskList() {
        ArrayList<ArrayList<String>> formattedTasks = new ArrayList<>();

        final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (Task task : taskList) {
            ArrayList<String> formattedTask = new ArrayList<>();

            String taskName = task.getName();
            LocalDateTime taskDeadline = task.getDueDate();
            LocalDateTime taskStartTime = task.getStartTime();
            String taskGroup = task.getTaskGroup();
            boolean status = task.getStatus();
            int priority = task.getPriority();
            String description = task.getDescription();

            formattedTask.add(taskName);
            String taskStartTimeToString = "";
            if (taskStartTime != null) {
                taskStartTimeToString = taskStartTime.format(ISO_FORMATTER);
                switch (taskStartTimeToString.substring(5, 7)) {
                    case "01":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " January, "
                                + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "02":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " February, "
                                + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "03":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " March, "
                                + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "04":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " April, "
                                + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "05":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " May, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "06":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " June, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "07":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " July, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "08":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " August, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "09":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " September, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "10":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " October, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "11":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " November, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                    case "12":
                        formattedTask.add(taskStartTimeToString.substring(8, 10) + " December, " + taskStartTimeToString.substring(0, 4) + " " + taskStartTimeToString.substring(11, 16));
                        break;
                }
            } else {
                formattedTask.add("");
            }

            String taskDeadlineToString = taskDeadline.toString();
            switch (taskDeadlineToString.substring(5, 7)) {
                case "01":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " January, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "02":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " February, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "03":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " March, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "04":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " April, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "05":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " May, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "06":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " June, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "07":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " July, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "08":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " August, " +
                            taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "09":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " September, "
                           + taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "10":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " October, "
                           + taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "11":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " November, "
                            +taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
                case "12":
                    formattedTask.add(taskDeadlineToString.substring(8, 10) + " December, "
                            +taskDeadlineToString.substring(0, 4) + " " + taskDeadlineToString.substring(11, 16));
                    break;
            }

            formattedTask.add(taskGroup);

            if (status) {
                formattedTask.add("Complete");
            }
            else {
                formattedTask.add("Incomplete");
            }

            formattedTask.add(Integer.toString(priority));

            formattedTask.add(description);

            formattedTasks.add(formattedTask);

        }
        return formattedTasks;
    }
}
