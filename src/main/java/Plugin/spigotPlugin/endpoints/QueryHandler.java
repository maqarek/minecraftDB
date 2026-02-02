package Plugin.spigotPlugin.endpoints;

import Plugin.spigotPlugin.DataUtils;
import Plugin.spigotPlugin.WorldEditor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import org.json.JSONObject;



public class QueryHandler extends APIEndpoint{

    HttpServer server;

    public QueryHandler(HttpServer server, Plugin plugin, World world, WorldEditor editor, Logger logger){
        super(plugin, world, editor, logger);
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            String response = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(response);
            String query = obj.getString("query");
            JSONObject queryBody = obj.getJSONObject("queryBody");
            String hexBody = DataUtils.translateToHex(queryBody.toString());
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                editor.addData(hexBody);
            });

            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }catch(Exception e){
            e.printStackTrace();
            exchange.sendResponseHeaders(400,0 );
            exchange.close();
        }
    }
}
