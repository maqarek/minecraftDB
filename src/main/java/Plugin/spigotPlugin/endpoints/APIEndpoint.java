package Plugin.spigotPlugin.endpoints;

import Plugin.spigotPlugin.SpigotPlugin;
import Plugin.spigotPlugin.WorldEditor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class APIEndpoint implements HttpHandler {
    protected Logger logger;
    protected World world;
    protected Plugin plugin;
    protected WorldEditor editor;

    public boolean preflightCheck(HttpExchange exchange) {
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            try {
                exchange.sendResponseHeaders(200, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
    }

    public APIEndpoint(Plugin plugin, World world, WorldEditor editor, Logger logger) {
        this.world = world;
        this.logger = logger;
        this.plugin = plugin;
        this.editor = editor;
    }
}
