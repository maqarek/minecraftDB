package Plugin.spigotPlugin;

import Plugin.spigotPlugin.endpoints.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ApiManager {
    

    public static String readBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public static void startHttpServer(Plugin plugin, World world, WorldEditor editor, Logger logger) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/get", new GetAllHandler(server, plugin, world, editor, logger));
        server.createContext("/upload", new UploadHandler(server, plugin, world, editor, logger));
        server.createContext("/delete", new DeleteHandler(server, plugin, world, editor, logger));
        server.createContext("/update", new UpdateHandler(server, plugin, world, editor, logger));
        server.createContext("/query", new QueryHandler(server, plugin, world, editor,logger));



        server.start();
        System.out.println("Server started on port 8080");
    }
}