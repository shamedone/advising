package edu.sfsu.edu.sfsu.http;

import edu.sfsu.HtmlFormatter;
import edu.sfsu.db.Course;
import edu.sfsu.db.Student;
import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import edu.sfsu.db.DB;

public class Server implements Container {

    private static final String BASE = "www";
    private static final int    PORT = 4242;

    private Connection          connection;

    private DB                  db;


    public Server(DB db) {
        this.db = db;
    }

    public void start() {
        try {
            ContainerSocketProcessor processor = new ContainerSocketProcessor(this, 1);
            connection = new SocketConnection(processor);
            SocketAddress address = new InetSocketAddress(PORT);
            connection.connect(address);

        } catch (IOException ex) {
            System.out.println("Could not start HTTP server: " + ex.getMessage());
        }

    }

    public void stop() {
        db.close();
        try {
            connection.close();
        } catch (IOException ex) {
            System.out.println("Could not stop HTTP server: " + ex.getMessage());
        }
    }

    @Override
    public void handle(Request request, Response response) {
        String path = request.getPath().toString();
        System.out.println("Incoming request for path: " + path);
        try {
            if (path.equals("/")) {
                response.setContentType("text/html");
                sendFile(response, "index.html");
            } else if (path.endsWith(".html")) {
                response.setContentType("text/html");
                sendFile(response, path);
            } else if (path.endsWith(".css")) {
                response.setContentType("text/css");
                sendFile(response, path);
            } else if (path.endsWith(".js")) {
                response.setContentType("application/javascript");
                sendFile(response, path);
            } else if (path.startsWith("/student-")) {
                String id = path.substring("/student-".length());
                processStudentRequest(response, id);
            } else {
                System.out.println("Not serving " + path);
                response.setCode(404);
            }

            response.close();
        } catch (IOException ex) {
            System.out.println("Error handling request");
        }
    }

    private void sendFile(Response response, String path) throws IOException {
        File file = new File(BASE + "/" + path);
        if (!file.exists()) {
            response.setCode(404);
            return;
        }
        response.setCode(200);
        InputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
    }

    private void processStudentRequest(Response response, String id) throws IOException {
        response.setContentType("text/html");
        response.setCode(200);
        Student student = db.getStudent(id);
        String html;
        if (student != null) {
            html = HtmlFormatter.generateHtml(student);
        } else {
            html = "<b>Student not found</b>";
        }
        OutputStream out = response.getOutputStream();
        out.write(html.getBytes());
        out.close();
    }
}
