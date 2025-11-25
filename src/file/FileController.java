package file;

import model.SlideDocument;
import org.json.JSONObject;

import java.io.*;

public class FileController {

    public static boolean save(SlideDocument document, String filePath) {
        try {
            JSONObject json = SlideSerializer.toJSON(document);

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(json.toString(2)); // 美化格式
                writer.flush();
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SlideDocument load(String filePath) {
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
            }

            if (sb.length() == 0) {
                throw new RuntimeException("空文件或读取失败");
            }

            JSONObject json = new JSONObject(sb.toString());
            return SlideDeserializer.fromJSON(json);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
