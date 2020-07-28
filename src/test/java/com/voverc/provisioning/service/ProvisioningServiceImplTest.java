package com.voverc.provisioning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.voverc.provisioning.AppConfig;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.repository.DeviceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProvisioningServiceImplTest {

	@Autowired
	private ProvisioningServiceImpl provisioningService;

	@MockBean
	private DeviceRepository deviceRepository;

	@MockBean
	private AppConfig appConfig;

	@Test
	public void testCheckOverrideFragmentSuccess() {
		//given
		String str = "domain=sip.anotherdomain.com" + "\n" + "port=5161" + "\n" + "timeout=10";

		//when
		Map<String, String> result = provisioningService.checkOverrideFragment(str);

		//then
		assertEquals("sip.anotherdomain.com", result.get("domain"));
		assertEquals("5161", result.get("port"));
		assertEquals("10", result.get("timeout"));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testCheckOverrideFragmentFailed() {
		//when
		provisioningService.checkOverrideFragment("");
	}

	@Test
	public void testConvertMapToJsonSuccess() throws JsonProcessingException {
		//given
		Map<String, String> testMap = new HashMap<>();
		testMap.put("password", "1");
		testMap.put("username", "name");
		testMap.put("timeout", "1");

		//when
		String result = provisioningService.convertMapToJson(testMap);

		//then
		assertEquals("{\"password\":\"1\",\"timeout\":\"1\",\"username\":\"name\"}", result);
	}

	@Test
	public void testParseConferenceDataSuccess() throws IOException {
		//given
		String str = "{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}";

		//when
		Map<String, String> result = provisioningService.parseConferenceData(str);

		//then
		assertEquals("sip.anotherdomain.com", result.get("domain"));
		assertEquals("5161", result.get("port"));
		assertEquals("10", result.get("timeout"));
	}

	@Test(expected = IOException.class)
	public void testParseConferenceDataFailed() throws IOException {
		//given
		String str = "{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",timeout\":10}";

		//when
		provisioningService.parseConferenceData(str);
	}

	@Test
	public void testGetProvisioningFileSuccess() throws IOException {
		//given
		Device device = new Device();
		device.setModel(Device.DeviceModel.CONFERENCE);
		device.setMacAddress("any");
		device.setPassword("pwd");
		device.setUsername("username");
		given(deviceRepository.findDeviceByMacAddress("any")).willReturn(device);
		given(appConfig.getCodecs()).willReturn(Arrays.asList("ASAS", "ASDASD"));
		given(appConfig.getDomain()).willReturn("domainName");
		given(appConfig.getPort()).willReturn("1111");

		//when
		String result = provisioningService.getProvisioningFile("any");

		//then
		assertEquals(
				"{\"password\":\"pwd\",\"port\":\"1111\",\"domain\":\"domainName\",\"codecs\":\"[ASAS, ASDASD]\",\"username\":\"username\"}",
				result);
	}

	@Test
	public void testGetProvisioningFileSuccessWithOverrideFragment() throws IOException {
		//given
		Device device = new Device();
		device.setModel(Device.DeviceModel.CONFERENCE);
		device.setMacAddress("any");
		device.setPassword("pwd");
		device.setUsername("username");
		device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10}");
		given(deviceRepository.findDeviceByMacAddress("any")).willReturn(device);
		given(appConfig.getCodecs()).willReturn(Arrays.asList("ASAS", "ASDASD"));
		given(appConfig.getDomain()).willReturn("domainName");
		given(appConfig.getPort()).willReturn("1111");

		//when
		String result = provisioningService.getProvisioningFile("any");

		//then
		assertEquals(
				"{\"password\":\"pwd\",\"port\":\"5161\",\"domain\":\"sip.anotherdomain.com\",\"codecs\":\"[ASAS, ASDASD]\",\"timeout\":\"10\",\"username\":\"username\"}",
				result);
	}

	@Test
	public void testGetProvisioningFileNull() throws IOException {
		//when
		String result = provisioningService.getProvisioningFile("any");

		//then
		assertNull(result);
	}
}