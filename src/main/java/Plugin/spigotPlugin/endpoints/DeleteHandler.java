package Plugin.spigotPlugin.endpoints;

import Plugin.spigotPlugin.DataUtils;
import Plugin.spigotPlugin.WorldEditor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Logger;

public class DeleteHandler extends APIEndpoint{

    private HttpServer server;

    public DeleteHandler(HttpServer server, Plugin plugin, World world, WorldEditor editor, Logger logger) {
        super(plugin,world,editor,logger);
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }
        if(exchange.getRequestURI().getPath().split("/").length > 2 && exchange.getRequestMethod().equals("DELETE")) {
            String response;

            int chunkId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            int tableId;

            if(exchange.getRequestURI().getPath().split("/").length > 3){
                tableId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[3]);
            }else tableId = 0;

            response = DataUtils.translateToStringFromHex(editor.readChunk(chunkId));
            Bukkit.getScheduler().runTask(plugin, () -> editor.clearChunk(chunkId, tableId));

            exchange.sendResponseHeaders(200,response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }else{
            exchange.sendResponseHeaders(405,-1);
            exchange.close();
        }
    }
}
