package singleton;

import task.Task;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TaskExecutor {

    // Singleton
    INSTANCE;

    public static final String TASKS_PACKAGE = "task.";

    public String run(String parameters) {
        String taskName, input = "";
        List<String> list = Arrays.stream(parameters.split(" ")).collect(Collectors.toList());

        long start = System.currentTimeMillis();
        if (list.size() != 2) {
            System.out.println("list.size() = " + list.size());
            taskName = "TaskIncorrectParams";
            System.out.println(list);
        } else {
            taskName = list.get(0);
            input = list.get(1);
            System.out.println("taskName = " + taskName);
        }
        Task task = createTask(taskName);
        task.setInput(input);
        task.execute();

        // TasksList.INSTANCE.add(task);
        long end = System.currentTimeMillis();
        String executedTime = new DecimalFormat("#0.00").format((end - start) / 1000d) + "s";
        task.setExecutedTime(executedTime);
        System.out.println("Executed time: " + executedTime);
        System.out.println("task.getResult(): " + task.getResult());
        return task.getResult();
    }

    private Task createTask(String className) {
        Task task = null;
        try {
            task = (Task) Class.forName(TASKS_PACKAGE + className).newInstance();
        } catch (Exception e) {
            try {
                task = (Task) Class.forName(TASKS_PACKAGE + "TaskNotFound").newInstance();
            } catch (Exception e1) {
                // e1.printStackTrace();
                System.out.println("ERROR");
                System.out.println(e1.getMessage());
            }
        }
        return task;
    }
}
