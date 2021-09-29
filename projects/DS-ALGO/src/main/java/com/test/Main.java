package com.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	static {
		System.setProperty("log4j.configurationFile", "./log4j2.xml");
	}

	public static List<String> getLinesFromFile(String configFileName) {

		try {
			List<String> allLines = Files.readAllLines(Paths.get(configFileName));
			return allLines;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
