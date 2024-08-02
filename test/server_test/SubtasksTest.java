package test.server_test;

import com.google.gson.Gson;
import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.model.EpicTask;
import main.model.Subtask;
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

public class SubtasksTest {
    private static HttpTaskServer server;
    private final Gson gson = BaseHttpHandler.gson;
    private static TaskManager manager;

    private final EpicTask eTask1 = new EpicTask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(4000));
    private final Subtask task1 = new Subtask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final Subtask task2 = new Subtask("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(2000));
    private final Subtask task3 = new Subtask("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(3000));

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
        manager.addSubtask(task1, eTask1.getId());
        manager.addSubtask(task2, eTask1.getId());
        manager.addSubtask(task3, eTask1.getId());
    }

    @Test
    public void shouldWeGetAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getSubtasks());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGetSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getSubtask(task1.getId()).get());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGet404WhenSubtaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/" + 100000);
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
    public void shouldWeCreateSubtask() throws IOException, InterruptedException {
        manager.deleteSubtask(task1.getId());
        task1.setId(0);
        String jsonTask = gson.toJson(task1);
        String jsonResponse = "{\"epicTaskId\":" + eTask1.getId() + ",\"subtask\":" + jsonTask + "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonResponse))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(InMemoryTaskManager.getNewId() - 1);
        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertTrue(manager.containsTask(task1));
        client.close();
    }

    @Test
    public void shouldWeGet406WhenCreateSubtask() throws IOException, InterruptedException {
        manager.deleteSubtask(task1.getId());
        task1.setId(0);
        task1.setStartTime(LocalDateTime.now().minusSeconds(5000));
        task1.setDuration(Duration.ofSeconds(15000));
        String jsonTask = gson.toJson(task1);
        String jsonResponse = "{\"epicTaskId\":" + eTask1.getId() + ",\"subtask\":" + jsonTask + "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonResponse))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(InMemoryTaskManager.getNewId() - 1);
        Assertions.assertEquals(response.statusCode(), 406);
        client.close();
    }

    @Test
    public void shouldWeUpdateSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("newTask", "newTask", TaskStatus.NEW,
                task1.getId(), task1.getDuration(), task1.getStartTime());
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(task.getTitle(), manager.getSubtask(task1.getId()).get().getTitle());

        client.close();
    }

    @Test
    public void shouldWeGet406WhenUpdateSubtask() throws IOException, InterruptedException {
        Subtask task = new Subtask("newTask", "newTask", TaskStatus.NEW,
                task1.getId(), Duration.ofDays(384598340), LocalDateTime.now().minusDays(51));
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 406);
        Assertions.assertEquals(response.body(), "The tasks should not intersections.");

        client.close();
    }

    @Test
    public void shouldWeDeleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/subtasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertFalse(manager.containsTask(task1));
        client.close();
    }
}
