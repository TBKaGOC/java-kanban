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

public class EpicsTest {
    private static HttpTaskServer server;
    private final static Gson gson = BaseHttpHandler.gson;
    private static TaskManager manager;

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
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubtasks();
        Managers.getDefaultHistory().remove();

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
    public void shouldWeGetAllEpicTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getEpicTasks());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGetEpicTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/" + eTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getEpicTask(eTask1.getId()).get());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGet404WhenEpicTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/" + 100000);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 404);
        Assertions.assertEquals(response.body(), "The task not founded.");
        client.close();
    }

    @Test
    public void shouldWeGetSubtasksOfEpicTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/" + eTask1.getId() + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getEpicTask(eTask1.getId()).get().getSubtasks());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGet404WhenEpicTaskForSubtasksNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/" + 100000 + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 404);
        Assertions.assertEquals(response.body(), "The task not founded.");
        client.close();
    }

    @Test
    public void shouldWeCreateEpicTask() throws IOException, InterruptedException {
        manager.deleteEpicTask(eTask1.getId());
        eTask1.setId(0);
        String jsonTask = gson.toJson(eTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        eTask1.setId(InMemoryTaskManager.getNewId() - 1);
        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertTrue(manager.containsTask(eTask1));
        client.close();
    }

    @Test
    public void shouldWeDeleteEpicTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/epics/" + eTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertFalse(manager.containsTask(eTask1));
        client.close();
    }
}
