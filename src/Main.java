import Managers.*;
import Managers.Server.HttpTaskServer;
import Managers.Server.KVServer;
import Tasks.*;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        TaskManager manager = Managers.getHttpTaskManager("http://localhost:8078");
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

        httpTaskServer.startServer();

    }
}
