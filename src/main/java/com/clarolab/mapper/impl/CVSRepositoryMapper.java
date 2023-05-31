package com.clarolab.mapper.impl;

import com.clarolab.dto.CVSRepositoryDTO;
import com.clarolab.mapper.Mapper;
import com.clarolab.model.CVSRepository;
import com.clarolab.service.CVSRepositoryService;
import com.clarolab.service.ProductService;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.clarolab.mapper.MapperHelper.getNullableByID;
import static com.clarolab.mapper.MapperHelper.setEntryFields;

@Component
public class CVSRepositoryMapper implements Mapper<CVSRepository, CVSRepositoryDTO> {

    @Autowired
    private CVSRepositoryService cvsRepositoryService;

    @Autowired
    private ProductService productService;

    @Override
    public CVSRepositoryDTO convertToDTO(CVSRepository cvsRepository) {
        CVSRepositoryDTO cvsRepositoryDTO = new CVSRepositoryDTO();

        setEntryFields(cvsRepository, cvsRepositoryDTO);

        cvsRepositoryDTO.setUrl(cvsRepository.getUrl());
        cvsRepositoryDTO.setLocalPath(cvsRepository.getLocalPath());
        cvsRepositoryDTO.setUser(cvsRepository.getUsername());
        cvsRepositoryDTO.setPassword(cvsRepository.getPassword());
        cvsRepositoryDTO.setBranch(cvsRepository.getBranch());
        cvsRepositoryDTO.setProduct(cvsRepository.getProduct() == null ? 0 : cvsRepository.getProduct().getId());
        cvsRepositoryDTO.setLastRead(cvsRepository.getLastRead());
        cvsRepositoryDTO.setPackageNames(cvsRepository.getPackageNames());

        return cvsRepositoryDTO;
    }

    @Override
    public CVSRepository convertToEntity(CVSRepositoryDTO dto) {
        CVSRepository cvsRepository;
        if (dto.getId() == null || dto.getId() < 1) {
            cvsRepository = CVSRepository.builder()
                    .id(null)
                    .enabled(dto.getEnabled())
                    .timestamp(dto.getTimestamp())
                    .updated(dto.getUpdated())
                    .url(dto.getUrl())
                    .localPath(dto.getLocalPath())
                    .username(dto.getUser())
                    .password(dto.getPassword())
                    .branch(dto.getBranch())
                    .lastRead(dto.getLastRead())
                    .packageNames(dto.getPackageNames())
                    .product(getNullableByID(dto.getProduct(), id -> productService.find(id)))
                    .build();
        } else {
            cvsRepository = cvsRepositoryService.find(dto.getId());
            cvsRepository.setUrl(dto.getUrl());
            cvsRepository.setLocalPath(dto.getLocalPath());
            cvsRepository.setUsername(dto.getUser());
            if (!StringUtils.isEmptyOrNull(dto.getPassword())){
                cvsRepository.setPassword(dto.getPassword());
            }
            cvsRepository.setBranch(dto.getBranch());
            cvsRepository.setLastRead(dto.getLastRead());
            cvsRepository.setPackageNames(dto.getPackageNames());
            cvsRepository.setProduct(getNullableByID(dto.getProduct(), id -> productService.find(id)));
        }
        return cvsRepository;
    }

}
