package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {
	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		try {
//			Path path = Paths.get("/www/wwwroot/sk_restaurant/logs/");
			Path path = Paths.get("/Users/qiangni/Documents/Tomcat/apache-tomcat-9.0.73/logs/");
			Path pathCreatePath = Files.createDirectories(path);

			FileHandler fileHandler = new FileHandler(
					pathCreatePath.toString() + File.separator + name + "%u.txt", 1024 * 1024, 3, true);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.ALL);
			logger.setUseParentHandlers(false);
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logger;
	}
}
