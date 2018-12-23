import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LevelLoader {


    public static void load(String filename, ArrayList<Level> levels) throws Exception{

        InputStreamReader sReader = new InputStreamReader(LevelLoader.class.getResourceAsStream(filename));
        BufferedReader reader = new BufferedReader(sReader);

        ArrayList<String> lines = new ArrayList<>();
        String line, levelname = "";
        int cols = 0, skipCounter = 0;
        while((line = reader.readLine()) != null){
            if(line.startsWith("Level")){
                skipCounter = 0;
                cols = 0;
            }
            if(line.equals("")) {
                if(lines.size() > 0){           // x        y
                    Field[][] fields = new Field[cols][lines.size()];

                    for (int i = 0; i < lines.size(); i++) {
                        for (int j = 0; j < cols; j++) {
                            char val = lines.get(i).length() > j ? lines.get(i).charAt(j) : ' ';
                            fields[j][i] = new Field(val);
                        }
                    }
                    levels.add(new Level(fields, levelname, cols, lines.size()));
                }
                lines.clear();
            }else if(skipCounter >= 2){
                lines.add(line);
                cols = Math.max(cols, line.length());
            }
            if(skipCounter == 1){
                levelname = line.substring(1, line.length() - 1);
            }

            skipCounter++;
        }

        //return levels;
    }
}
