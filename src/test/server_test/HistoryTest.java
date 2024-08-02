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

public class HistoryTest {
    private static HttpTaskServer server;
    private final Gson gson = BaseHttpHandler.gson;
    private static TaskManager manager;
    private final Task task1 = new Task("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final EpicTask eTask1 = new EpicTask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(4000));
    private final Subtask sTask1 = new Subtask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(7000));

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

        manager.addEpicTask(eTask1);

        manager.addSubtask(sTask1, eTask1.getId());
    }

    @Test
    public void shouldWeGetCorrectHistory() throws IOException, InterruptedException {
        manager.getTask(task1.getId());
        manager.getEpicTask(eTask1.getId());
        manager.getSubtask(sTask1.getId());

        String jsonHistory = gson.toJson(manager.getHistory().getHistory());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(response.body(), jsonHistory);
        client.close();
    }
}
