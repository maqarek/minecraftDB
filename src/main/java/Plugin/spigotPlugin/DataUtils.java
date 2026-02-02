package Plugin.spigotPlugin;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class DataUtils {
    public static final Map<Material, Character> MATERIAL_TO_CHAR = new HashMap<>(Map.ofEntries(
                    Map.entry(Material.WHITE_WOOL, '0'),
                    Map.entry(Material.BLACK_WOOL, '1'),
                    Map.entry(Material.LIGHT_GRAY_WOOL, '2'),
                    Map.entry(Material.GRAY_WOOL, '3'),
                    Map.entry(Material.BROWN_WOOL, '4'),
                    Map.entry(Material.RED_WOOL, '5'),
                    Map.entry(Material.ORANGE_WOOL, '6'),
                    Map.entry(Material.YELLOW_WOOL, '7'),
                    Map.entry(Material.LIME_WOOL, '8'),
                    Map.entry(Material.GREEN_WOOL, '9'),
                    Map.entry(Material.CYAN_WOOL, 'A'),
                    Map.entry(Material.LIGHT_BLUE_WOOL, 'B'),
                    Map.entry(Material.BLUE_WOOL, 'C'),
                    Map.entry(Material.PURPLE_WOOL, 'D'),
                    Map.entry(Material.MAGENTA_WOOL, 'E'),
                    Map.entry(Material.PINK_WOOL, 'F')
            ));
    public static final Map<Character, Material> CHAR_TO_MATERIAL = new HashMap<>(Map.ofEntries(
                    Map.entry('0', Material.WHITE_WOOL),
                    Map.entry('1', Material.BLACK_WOOL),
                    Map.entry('2', Material.LIGHT_GRAY_WOOL),
                    Map.entry('3', Material.GRAY_WOOL),
                    Map.entry('4', Material.BROWN_WOOL),
                    Map.entry('5', Material.RED_WOOL),
                    Map.entry('6', Material.ORANGE_WOOL),
                    Map.entry('7', Material.YELLOW_WOOL),
                    Map.entry('8', Material.LIME_WOOL),
                    Map.entry('9', Material.GREEN_WOOL),
                    Map.entry('A', Material.CYAN_WOOL),
                    Map.entry('B', Material.LIGHT_BLUE_WOOL),
                    Map.entry('C', Material.BLUE_WOOL),
                    Map.entry('D', Material.PURPLE_WOOL),
                    Map.entry('E', Material.MAGENTA_WOOL),
                    Map.entry('F', Material.PINK_WOOL)
            ));





    public static String translateToBinary(String message){
        String messageInBinary = "";
        for(Character ch : message.toCharArray()){

            int ascii = (int) ch;
            StringBuilder bin = new StringBuilder();

            while(ascii != 0){
                if(ascii % 2 == 1){
                    bin.insert(0, "1");
                }else {
                    bin.insert(0, "0");
                }
                ascii /= 2;
            }

            while(bin.length()<8){
                bin.insert(0, "0");
            }

            String binary = bin.toString();
            messageInBinary += binary;

        }
        return messageInBinary;
    }
    public static String translateToHex(String message){
        String messageInHex = "";
        for(Character ch : message.toCharArray()){
            int ascii = (int) ch;
            StringBuilder hex = new StringBuilder();

            while (ascii != 0){
                char hexChar = ascii%16 > 9? (char) (ascii%16 + 55): (char) (ascii%16 + 48) ;
                hex.insert(0, hexChar);
                ascii /= 16;
            }
            if(hex.length()  < 2){
                hex.insert(0, "0");
            }
            messageInHex += hex.toString();
        }
        return messageInHex;
    }

    public static String translateToString(String messageInBinary){
        String message = "";

        while(!messageInBinary.isEmpty()){
            String StringByte = messageInBinary.substring(0,8);
            messageInBinary = messageInBinary.substring(8);
            int number = 0;
            int multiplier = 128;
            for(char c : StringByte.toCharArray()){
                if(c == '1') number+=multiplier;
                multiplier /= 2;
            }
            message = message + (char)number;
        }
        return message;
    }
    public static String translateToStringFromHex(String messageInHex) {
        StringBuilder message = new StringBuilder();

        while (messageInHex.length() >= 2) {
            String StringByte = messageInHex.substring(0, 2);
            messageInHex = messageInHex.substring(2);

            char high = StringByte.charAt(0);
            char low = StringByte.charAt(1);

            int number1 = (high >= 'A') ? (high - 'A' + 10) : (high - '0');
            int number2 = (low >= 'A') ? (low - 'A' + 10) : (low - '0');

            int number = number1 * 16 + number2;

            message.append((char) number);
        }

        return message.toString();
    }
}

