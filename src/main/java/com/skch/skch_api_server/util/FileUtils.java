package com.skch.skch_api_server.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
	
	public static void saveByteArrayToFile(String filePath, byte[] fileData){
        try {
			Path path = Paths.get(filePath);
			// Create parent directories if they don't exist
			Files.createDirectories(path.getParent());
			// Write bytes to file
			Files.write(path, fileData);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
