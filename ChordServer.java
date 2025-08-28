import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class ChordServer {
    static String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/detect", ChordServer::handleDetect);
        server.createContext("/", ChordServer::handleStatic);
        server.setExecutor(null);
        System.out.println("Server started at http://localhost:8080");
        server.start();
    }

    private static void handleDetect(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1); return;
        }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("/detect POST body: " + body);
        List<String> notes = parseNotesFromJson(body);
        System.out.println("Parsed notes: " + notes);
        List<Integer> noteValues = new ArrayList<>();
        for (String note : notes) {
            int idx = Arrays.asList(NOTES).indexOf(note);
            if (idx != -1) noteValues.add(idx);
        }
        System.out.println("Note values: " + noteValues);
        List<String> chords = ChordDetector.detectChord(noteValues);
        System.out.println("Detected chords: " + chords);
        String resp = "{\"chords\": " + toJsonArray(chords) + "}";
        Headers h = ex.getResponseHeaders();
        h.set("Content-Type", "application/json");
        ex.sendResponseHeaders(200, resp.getBytes(StandardCharsets.UTF_8).length);
        ex.getResponseBody().write(resp.getBytes(StandardCharsets.UTF_8));
        ex.close();
    }

    private static void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";
        File file = new File("." + path);
        if (!file.exists() || file.isDirectory()) {
            ex.sendResponseHeaders(404, -1); return;
        }
        String mime = path.endsWith(".html") ? "text/html" : "text/plain";
        ex.getResponseHeaders().set("Content-Type", mime);
        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
        ex.sendResponseHeaders(200, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }

    private static List<String> parseNotesFromJson(String json) {
        // Very simple JSON parser for { "notes": ["C", ...] }
        int i = json.indexOf("[");
        int j = json.indexOf("]");
        if (i == -1 || j == -1) return List.of();
        String arr = json.substring(i+1, j);
        String[] parts = arr.split(",");
        List<String> notes = new ArrayList<>();
        for (String p : parts) {
            String s = p.replaceAll("[\"'\s]", "");
            if (!s.isEmpty()) notes.add(s);
        }
        return notes;
    }

    private static String toJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append('"').append(list.get(i)).append('"');
        }
        sb.append("]");
        return sb.toString();
    }
}
