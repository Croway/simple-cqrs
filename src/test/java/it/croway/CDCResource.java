package it.croway;

import java.util.Map;
import java.util.UUID;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class CDCResource implements QuarkusTestResourceLifecycleManager {

	@Override
	public Map<String, String> start() {
		return Map.of("cdc.storage.filename", "target/" + UUID.randomUUID() + ".dat");
	}

	@Override
	public void stop() {

	}
}
