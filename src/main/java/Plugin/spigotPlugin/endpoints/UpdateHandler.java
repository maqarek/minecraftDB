package Plugin.spigotPlugin.endpoints;

import Plugin.spigotPlugin.DataUtils;
import Plugin.spigotPlugin.SpigotPlugin;
import Plugin.spigotPlugin.WorldEditor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class UpdateHandler extends APIEndpoint{

    private HttpServer server;

    public UpdateHandler(HttpServer server, Plugin plugin, World world, WorldEditor editor, Logger logger) {
        super(plugin, world , editor , logger);
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("PUT") && exchange.getRequestURI().getPath().split("/").length > 2){

            int chunkId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String hex  = DataUtils.translateToHex(body);

            Bukkit.getScheduler().runTask(plugin, ()->{
                editor.buildChunk(hex, chunkId*16 , -60, 0);
            });
            exchange.sendResponseHeaders(200, hex.length());
            exchange.getResponseBody().write(hex.getBytes());
            exchange.close();
        }else{
            exchange.sendResponseHeaders(205, -1);
            exchange.close();
        }
    }

}
