package com.cichosz.anotherone.services;
import java.util.UUID;

public class UUIDGenerator {
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
