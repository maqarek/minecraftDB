package Plugin.spigotPlugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SpigotPlugin extends JavaPlugin {

    public static World world;
    public static WorldEditor editor;
    public Logger logger;
    public static SpigotPlugin instance;
    public static SQLParser sqlParser;
    Player player;

    public SpigotPlugin(){

        instance = this;
        editor = new WorldEditor();
        world = Bukkit.getWorld("world");
        logger = this.getLogger();
        sqlParser = new SQLParser(editor);
    };

    @Override
    public void onEnable() {
        instance = this;
        world = Bukkit.getWorld("world");
        logger = this.getLogger();
        try{
            ApiManager.startHttpServer(this, world, editor, logger);
        }catch(IOException e){
            e.printStackTrace();
        }
        getLogger().info("Plugin has been started!");

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been started!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

//        if (!(sender instanceof Player)) {
//            sender.sendMessage("thats all");
//            return true;
//        }

        player = (Player) sender;


        if(command.getName().equalsIgnoreCase("clearAll")) {
            editor.clearChunks(0);
        }else if(command.getName().equalsIgnoreCase("translatetobin")) {
            String message = String.join(" ", args);
            String bin = DataUtils.translateToBinary(message);
            editor.addData(bin);
        }else if(command.getName().equalsIgnoreCase("readChunkBin")) {
            String message = editor.readChunk(Integer.parseInt(args[0]));
            message = DataUtils.translateToString(message);
            player.sendMessage(message);
        }else if(command.getName().equalsIgnoreCase("readChunk")) {
            String message = editor.readChunk(Integer.parseInt(args[0]));
            message = DataUtils.translateToStringFromHex(message);
            player.sendMessage(message);
        }
        else if(command.getName().equalsIgnoreCase("translatetohex")) {
            String message = String.join(" ", args);
            String hex = DataUtils.translateToHex(message);
            editor.addData(hex);
            player.sendMessage(message + ':' + hex);
        }else if(command.getName().equalsIgnoreCase("testcommand")) {
            String message = editor.readChunk(Integer.parseInt(args[0]));
            message = DataUtils.translateToStringFromHex(message);
            JSONObject obj = new JSONObject(message);
            player.sendMessage(obj.getString("name"));
        }else if(command.getName().equalsIgnoreCase("addTable")) {
            List<String> cols = new ArrayList<>();
            cols.add("id");
            cols.add("name");
            cols.add("world");
            editor.addTable(args[0], cols); // Change #1
        }else if(command.getName().equalsIgnoreCase("readTable")) {
            editor.tableEditor.loadAllTables();
            if(args.length >= 1) {
                Table table = editor.tableEditor.getTableById(Integer.parseInt(args[0]));
                if(table.rows.isEmpty()) player.sendMessage("IS EMPTY");
                else player.sendMessage("IS NOT EMPTY");
                for(String row : table.rows){
                    player.sendMessage(row);
                }
            }
        }else if(command.getName().equalsIgnoreCase("query")) {
            editor.tableEditor.loadAllTables();
            //player.sendMessage(String.valueOf(editor.tableEditor.tables.size()));
            String sql = String.join(" ", args);
            sqlParser.parseSqlType(sql);
        }
        return true;
    }
}