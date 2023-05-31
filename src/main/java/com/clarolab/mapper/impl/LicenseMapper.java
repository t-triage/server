package com.clarolab.mapper.impl;

import com.clarolab.dto.LicenseDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.service.LicenseService;
import com.clarolab.startup.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class LicenseMapper implements Mapper<License, LicenseDTO> {

    @Autowired
    private LicenseService licenseService;

    @Override
    public LicenseDTO convertToDTO(License license) {

        LicenseDTO licenseDTO = new LicenseDTO();

        setEntryFields(license, licenseDTO);

        licenseDTO.setCreationTime(license.getCreationTime());
        licenseDTO.setFree(license.isFree());
        licenseDTO.setLicenseCode(license.getLicenseCode());
        licenseDTO.setExpirationTime(license.getExpirationTime());
        licenseDTO.setExpired(license.isExpired());

        return licenseDTO;
    }

    @Override
    public License convertToEntity(LicenseDTO dto) {
        License license;

        if (dto.getId() == null || dto.getId() < 1) {
            license = License.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .creationTime(dto.getCreationTime())
                    .free(dto.isFree())
                    .licenseCode(dto.getLicenseCode())
                    .expirationTime(dto.getExpirationTime())
                    .expired(dto.isExpired())
                    .build();
        } else {
            license = licenseService.find(dto.getId());
            license.setFree(dto.isFree());
            license.setLicenseCode(dto.getLicenseCode());

        }
        return license;

    }
}
