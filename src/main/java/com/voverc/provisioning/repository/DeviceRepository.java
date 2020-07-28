package com.voverc.provisioning.repository;

import com.voverc.provisioning.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

	Device findDeviceByMacAddress(String macAddress);
}
