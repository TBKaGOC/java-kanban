package test.server_test;

import com.google.gson.Gson;
import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.model.EpicTask;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import main.server.HttpTaskServer;
import main.server.handlers.BaseHttpHandler;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedTest {
    private static HttpTaskServer server;
    private final Gson gson = BaseHttpHandler.gson;

    private static TaskManager manager;

    private final Task task1 = new Task("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final Task task2 = new Task("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(2000));
    private final Task task3 = new Task("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(3000));

    private final EpicTask eTask1 = new EpicTask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(4000));
    private final EpicTask eTask2 = new EpicTask("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(5000));
    private final EpicTask eTask3 = new EpicTask("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(6000));

    private final Subtask sTask1 = new Subtask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(7000));
    private final Subtask sTask2 = new Subtask("2task", "2thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(8000));
    private final Subtask sTask3 = new Subtask("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(9000));
    private final Subtask sTask4 = new Subtask("4task", "4thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(10000));
    private final Subtask sTask5 = new Subtask("5task", "5thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(11000));
    private final Subtask sTask6 = new Subtask("6task", "6thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(12000));

    @BeforeAll
    public static void startServer() throws IOException {
        server = new HttpTaskServer(Managers.getDefault());
        manager = Managers.getDefault();
        server.start();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void addAll() throws Exception {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        server.setTaskManager(manager);
        Managers.getDefaultHistory().remove();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.addEpicTask(eTask1);
        manager.addEpicTask(eTask2);
        manager.addEpicTask(eTask3);

        manager.addSubtask(sTask1, eTask1.getId());
        manager.addSubtask(sTask2, eTask1.getId());
        manager.addSubtask(sTask3, eTask1.getId());

        manager.addSubtask(sTask4, eTask2.getId());
        manager.addSubtask(sTask5, eTask2.getId());

        manager.addSubtask(sTask6, eTask3.getId());
    }

    @Test
    public void shouldWeGetCorrectSortedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getSortedTask());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }
}
