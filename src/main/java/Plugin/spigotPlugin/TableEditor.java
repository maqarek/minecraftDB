package Plugin.spigotPlugin;

import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TableEditor {
    public WorldEditor worldEditor;
    public List<Table> tables;

    public void increaseLength(int z){
        Table table = tables.get(z/16);
        table.increaseLength();
        worldEditor.overrideChunk(table.toString(), -16, -60, z);
    }
    public void decreaseLength(int z){
        Table table = tables.get(z/16);
        if(table != null){
            table.decreaseLength();
            worldEditor.overrideChunk(DataUtils.translateToHex(table.toString()), -16, -60, z);
        }
    }

    public boolean isBlockHex(int x, int y, int z){
        return DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType()) != null;
    }

    public TableEditor(WorldEditor worldEditor){
        this.worldEditor = worldEditor;
        tables = new ArrayList<>();
    }

    public String readChunk(int x, int y, int z){
        int startX = x;
        int startZ = z;

        String result = "";
        while(true){
            if(x - startX >= 16){
                if(z - startZ >=15){
                    z = 0;
                    y++;
                }else z++;
                x = startX;
            }
            Character c = DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType());
            if(c == null) break;
            result += c;
            x++;
        }
        return result;
    }

    public String getTableName(int id){
        int x = -16;
        int y = -60;
        int z = id*16;
        String tableName = DataUtils.translateToStringFromHex(readChunk(x,y,z)).split(";")[0]; //should be ok now
        return tableName;
    }

    public Table getTableById(int id){
        try{
            return tables.get(id);
        }catch(IndexOutOfBoundsException e){
            System.out.println("SOMETHING WENT WRONG HERE: getTableById");
            return null;
        }
    }

    public Integer findTableZByName(String tableName){
        int z = 0;
        int result = -1;
        while(isBlockHex(-16,-60,z)){
            String currentName = getTableName(z/16);
            if(currentName.equals(tableName)){
                result = z;
                break;
            }
            z+=16;
        }
        return result;
    }

    public void loadAllTables(){
        int  z = 0;
        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(-16,-60,z).getType()) != null){
            String chunkContent = this.readChunk(-16,-60,z);
            String chunkString = DataUtils.translateToStringFromHex(chunkContent);
            String[] parameters = chunkString.split(";");

            int i = 2;
            List<String> cols = new ArrayList<>();
            while(parameters.length > i){
                cols.add(parameters[i]);
                i++;
            }
            List<String> rows = readTableContent(z/16);

            tables.add(new Table(parameters[0], Integer.parseInt(parameters[1]), cols, rows));
            z += 16;
        }
    }

    public int findZForNewTable(){
        int z = 0;
        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(-16,-60,z).getType()) != null){
            z+=16;
        }
        return z;
    }

    public void addTable(String tableName, List<String> cols){
        Table tab = new Table(tableName, 1, cols, new ArrayList<>());
        tables.add(tab);
        worldEditor.overrideChunk(DataUtils.translateToHex(tab.toString()), -16, -60, findZForNewTable());
    }
    public List<String> readTableContent(int tableId){
        List<String> result = new ArrayList<>();
        int x = 0, y = -60, z = tableId*16;
        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType()) != null){
            result.add(DataUtils.translateToStringFromHex(worldEditor.readChunk(x/16,z)));
            x+=16;
        }
        return result;
    }

    public static String[] filter(String[] array, Predicate<JSONObject> condition){
        List<String>result = new ArrayList<>();
        for(String t : array){
            if(condition.test(new JSONObject(t))){
                result.add(t);
            }
        }
        return result.toArray(array);
    }


}
