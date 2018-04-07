package Server;

public class Utilities {
    public static String getStartText(String className){
        String result = className;
        String space = "";
        for (int i = 0; i<13 - className.length(); i++){
            space = space.concat(" ");
        }
        return "["+result+"]:"+space+" ";
    }
}
