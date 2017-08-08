package com.esko.Other;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourcePath {
	public String getResourcePath() throws Exception {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URI resourcePathFile = classLoader.getResource("RESOURCE_PATH").toURI();
			String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
			URI rootURI = new File("").toURI();
			URI resourceURI = new File(resourcePath).toURI();
			URI relativeResourceURI = rootURI.relativize(resourceURI);
			return relativeResourceURI.getPath();
		} catch (Exception e) {
			throw e;
		}
	}
}
