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
import java.util.logging.Logger;

public class GetAllHandler extends APIEndpoint {

    private HttpServer server;

    public GetAllHandler(HttpServer server, Plugin plugin, World world, WorldEditor editor, Logger logger) {
        super(plugin, world, editor, logger);
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if(exchange.getRequestMethod().equals("GET")) {

            String response;

            if(exchange.getRequestURI().getPath().split("/").length > 2) {

                int dataId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                response = DataUtils.translateToStringFromHex(editor.readChunk(dataId));

                if(response == "" || response == null){
                    exchange.sendResponseHeaders(405,-1);
                    exchange.close();
                    return;
                }

            }else{
                response = DataUtils.translateToStringFromHex(editor.getAllData());
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();

        }else{
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
