package Plugin.spigotPlugin;

import org.bukkit.World;
import org.json.JSONObject;

import java.util.function.Predicate;

public class SQLParser {

    private WorldEditor editor;
    private TableEditor tableEditor;

    public SQLParser(WorldEditor editor) {
        this.editor = editor;
        this.tableEditor = editor.tableEditor;
    }

    public void parseSqlType(String sql){
        String[] arr = sql.split(" ");
        if(arr.length > 3){
            switch(arr[0]){ //TODO
                case "INSERT":
                    break;
                case "UPDATE":
                    break;
                case "DELETE":
                    break;
                case "SELECT":
                    parseSELECT(sql);
                    break;
                default:
                    throw new NullPointerException(); // you may want to replace it with another exception in the future
            }
        }else{
            throw new NullPointerException(); // same here
        }
    }

    public void parseSELECT(String sql){
        String[] arr = sql.split(" ");
        String[] selFields = arr[1].split(",");
        String tableName = arr[3];
        Predicate<JSONObject> filterCond =null;
        if(arr.length > 4){
            String condition = arr[5];
            filterCond = parsePredicate(condition);
        }

        int tableZ = tableEditor.findTableZByName(tableName);
        if(tableZ == -1){
            throw new NullPointerException("[MyError] No such table: " + tableName);
        }

        Table table = tableEditor.getTableById(tableZ/16);
        if(table == null){
            throw new NullPointerException("[MyError] No such table: " + tableName + "[tableZ/16(id)]: " + String.valueOf(tableZ/16));
        }
        String[] rows = table.rows.toArray(new String[0]);

        if(filterCond != null){
            String[] results = TableEditor.filter(rows, filterCond);
            SpigotPlugin.instance.player.sendMessage("SOMETHING IS FILTERED HERE");
            for(String s : results){
                SpigotPlugin.instance.player.sendMessage(s);
            }
        }else{
            SpigotPlugin.instance.player.sendMessage("NOTHING IS FILTERED HERE");
            for(String s : rows){
                SpigotPlugin.instance.player.sendMessage(s);
            }
        }
    }


    public void parseINSERT(String sql){ //TODO
        String[] arr = sql.split(" ");

    }
    public void parseUPDATE(String sql){ //TODO
        String[] arr = sql.split(" ");

    }
    public void parseDELETE(String sql){
        String[] arr = sql.split(" ");

    }


    public Predicate<JSONObject> parsePredicate(String condition){
        if(condition.contains("=") && !condition.contains(">") &&  !condition.contains("<")){
            String[] arr = condition.split("=");
            return (JSONObject obj) -> obj.getString(arr[0]).equals(arr[1]);
        }
        else if(!condition.contains("=") && !condition.contains(">") &&  condition.contains("<")){ // TODO:handle possible exceptions
            String[] arr = condition.split("<");
            return (JSONObject obj) -> obj.getDouble(arr[0]) < Double.parseDouble(arr[1]);
        }
        else if(!condition.contains("=") && condition.contains(">") &&  !condition.contains("<")){ // TODO:handle possible exceptions
            String[] arr = condition.split(">");
            return (JSONObject obj) -> obj.getDouble(arr[0]) > Double.parseDouble(arr[1]);
        }
        return null;
    }

}
