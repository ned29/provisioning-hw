package com.voverc.provisioning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voverc.provisioning.AppConfig;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.voverc.provisioning.entity.Device.DeviceModel.CONFERENCE;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final DeviceRepository deviceRepository;

	private final AppConfig appConfig;

	private final ObjectMapper objectMapper;

	public ProvisioningServiceImpl(DeviceRepository deviceRepository, AppConfig appConfig, ObjectMapper objectMapper) {
		this.deviceRepository = deviceRepository;
		this.appConfig = appConfig;
		this.objectMapper = objectMapper;
	}

	public String getProvisioningFile(String macAddress) throws IOException {
		Map<String, String> dataBaseResult = new HashMap<>();
		Device device = deviceRepository.findDeviceByMacAddress(macAddress);
		if (device != null) {
			dataBaseResult.put("username", device.getUsername());
			dataBaseResult.put("password", device.getPassword());
			dataBaseResult.putAll(formatAppConfig());
			String overrideFragment = device.getOverrideFragment() != null ? device.getOverrideFragment() : "";
			if (device.getModel() == CONFERENCE) {
				if (!overrideFragment.isEmpty()) {
					Map<String, String> overrideFragmentMap = parseConferenceData(overrideFragment);
					dataBaseResult.putAll(overrideFragmentMap);
				}
				return convertMapToJson(dataBaseResult);
			} else {
				if (!overrideFragment.isEmpty()) {
					Map<String, String> overrideFragmentMap = checkOverrideFragment(overrideFragment.trim());
					dataBaseResult.putAll(overrideFragmentMap);
				}
				return formatDescResponse(dataBaseResult);
			}
		}
		return null;
	}

	protected Map<String, String> formatAppConfig() {
		Map<String, String> appConfigMap = new HashMap<>();
		appConfigMap.put("domain", appConfig.getDomain());
		appConfigMap.put("port", appConfig.getPort());
		appConfigMap.put("codecs", appConfig.getCodecs().toString());
		return appConfigMap;
	}

	protected String formatDescResponse(Map<String, String> result) {
		StringBuilder builder = new StringBuilder();
		result.keySet().forEach(key -> builder.append(key).append("=").append(result.get(key)).append(LINE_SEPARATOR));
		return builder.toString();
	}

	protected Map<String, String> parseConferenceData(String overrideFragment) throws IOException {
		return objectMapper.readValue(overrideFragment, new TypeReference<Map<String, String>>() {
		});
	}

	protected String convertMapToJson(Map<String, String> result) throws JsonProcessingException {
		return objectMapper.writeValueAsString(result);
	}

	protected Map<String, String> checkOverrideFragment(String overrideFragment) {
		return Arrays.stream(overrideFragment.split(LINE_SEPARATOR)).map(newLine -> newLine.split("="))
				.collect(Collectors.toMap(e -> e[0], e -> e[1]));
	}
}
