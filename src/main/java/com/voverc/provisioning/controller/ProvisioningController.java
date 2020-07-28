package com.voverc.provisioning.controller;

import com.voverc.provisioning.service.ProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ProvisioningController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProvisioningController.class);

	private final ProvisioningService provisioningService;

	public ProvisioningController(ProvisioningService provisioningService) {
		this.provisioningService = provisioningService;
	}

	@GetMapping("/provisioning/{macAddress}")
	public ResponseEntity<String> getProvisioningFile(@NotNull @PathVariable("macAddress") String macAddress)
			throws IOException {
		LOGGER.info("Start processing");
		String result = provisioningService.getProvisioningFile(macAddress);
		LOGGER.info("Finished");
		return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
	}
}