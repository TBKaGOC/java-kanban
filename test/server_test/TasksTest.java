package test.server_test;

import com.google.gson.Gson;
import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
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

public class TasksTest {
    private static HttpTaskServer server;
    private final Gson gson = BaseHttpHandler.gson;
    private static TaskManager manager;

    private final Task task1 = new Task("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final Task task2 = new Task("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(2000));
    private final Task task3 = new Task("3task", "3thForExamination", TaskStatus.DONE,
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

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
    }

    @Test
    public void shouldWeGetAllTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getTasks());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonSorted = gson.toJson(manager.getTask(task1.getId()).get());

        Assertions.assertEquals(response.body(), jsonSorted);
        client.close();
    }

    @Test
    public void shouldWeGet404WhenTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/" + 100000);
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
    public void shouldWeCreateTask() throws IOException, InterruptedException {
        manager.deleteTask(task1.getId());
        task1.setId(0);
        String jsonTask = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(InMemoryTaskManager.getNewId() - 1);
        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertTrue(manager.containsTask(task1));
        client.close();
    }

    @Test
    public void shouldWeGet406WhenCreateTask() throws IOException, InterruptedException {
        manager.deleteTask(task1.getId());
        task1.setId(0);
        task1.setStartTime(task2.getStartTime());
        String jsonTask = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(InMemoryTaskManager.getNewId() - 1);
        Assertions.assertEquals(response.statusCode(), 406);
        client.close();
    }

    @Test
    public void shouldWeUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("newTask", "newTask", TaskStatus.NEW,
                task1.getId(), task1.getDuration(), task1.getStartTime());
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.statusCode(), 201);
        Assertions.assertEquals(task.getTitle(), manager.getTask(task1.getId()).get().getTitle());

        client.close();
    }

    @Test
    public void shouldWeGet406WhenUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("newTask", "newTask", TaskStatus.NEW,
                task1.getId(), Duration.ofDays(384598340), LocalDateTime.now().minusDays(51));
        String jsonTask = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/");
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
    public void shouldWeDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/tasks/" + task1.getId());
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
