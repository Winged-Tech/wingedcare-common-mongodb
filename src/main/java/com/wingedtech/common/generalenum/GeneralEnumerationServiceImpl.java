package com.wingedtech.common.generalenum;

import com.google.common.collect.Lists;
import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.service.GenericServiceTemplateImpl;
import com.wingedtech.common.util.random.RandomCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */
@Service
@Slf4j
public class GeneralEnumerationServiceImpl extends GenericServiceTemplateImpl<GeneralEnumerationDTO, GeneralEnumeration> implements GeneralEnumerationService {

    private final GeneralEnumerationRepository repository;

    private final GeneralEnumerationMapper mapper;

    public GeneralEnumerationServiceImpl(GeneralEnumerationRepository repository, GeneralEnumerationMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<GeneralEnumerationDTO> findEnumerationItemsByType(String type) {
        GeneralEnumeration generalEnumeration = new GeneralEnumeration();
        generalEnumeration.setType(type);
        return findByExample(generalEnumeration);
    }

    private List<GeneralEnumerationDTO> findByExample(GeneralEnumeration generalEnumeration) {
        return mapper.toDto(repository.findAll(buildExample(generalEnumeration)));
    }

    @Override
    public GeneralEnumerationDTO save(final GeneralEnumerationDTO generalEnumerationDTO, String type) {
        generalEnumerationDTO.setType(type);
        final GeneralEnumeration entity = mapper.toEntity(generalEnumerationDTO);
        final String id = entity.getId();
        if (id != null) {
            repository.findById(id).ifPresent(existingEntity -> {
                entity.setCreatedBy(existingEntity.getCreatedBy());
                entity.setCreatedDate(existingEntity.getCreatedDate());
            });
        }

        // 若value为空则自动创建8位随机编码
        if (StringUtils.isBlank(entity.getValue())) {
            entity.setValue(
                RandomCodeUtils.generateShortUuidWithPredicate(value ->
                    // 不存在则代表此次生成的value唯一
                    !repository.existsTypeByCodeOrCode(entity.getId(), entity.getType(), null, value))
            );
        }

        this.validateAndCheckRepeat(entity);
        return mapper.toDto(repository.save(entity));
    }

    private void validateAndCheckRepeat(GeneralEnumeration entity) {

        String name = entity.getName();
        if (StringUtils.isBlank(name)) {
            throw new BusinessException("枚举名称不能为空");
        }

        String value = entity.getValue();
        if (StringUtils.isBlank(value)) {
            throw new BusinessException("枚举值不能为空");
        }

        if (repository.existsTypeByCodeOrCode(entity.getId(), entity.getType(), entity.getName(), null)) {
            throw new BusinessException("枚举名称已存在于该枚举类型");
        }

        if (repository.existsTypeByCodeOrCode(entity.getId(), entity.getType(), null, entity.getValue())) {
            throw new BusinessException("枚举值已存在于该枚举类型");
        }

    }

    @Override
    public boolean exist(GeneralEnumerationDTO generalEnumerationDTO, String type) {
        generalEnumerationDTO.setType(type);
        Example<GeneralEnumeration> generalEnumerationExample = buildExample(mapper.toEntity(generalEnumerationDTO));
        return repository.exists(generalEnumerationExample);
    }

    @Override
    public GeneralEnumerationDTO findOrInsert(GeneralEnumerationDTO generalEnumerationDTO, String type) {
        generalEnumerationDTO.setType(type);
        if (StringUtils.isEmpty(generalEnumerationDTO.getValue())) {
            generalEnumerationDTO.setValue(generalEnumerationDTO.getName());
        }
        if (StringUtils.isBlank(generalEnumerationDTO.getName())) {
            throw new BusinessException("枚举名称不能为空");
        }
        return mapper.toDto(repository.findOrInsert(mapper.toEntity(generalEnumerationDTO)));
    }

    @Override
    public Optional<GeneralEnumerationDTO> findOne(GeneralEnumerationDTO generalEnumerationDTO, String type) {
        generalEnumerationDTO.setType(type);
        return findOneByExample(mapper.toEntity(generalEnumerationDTO));
    }

    @Override
    public void delete(String id, String type) {
        log.debug("Request to delete GeneralEnumeration : {}", id);
        repository.deleteByIdAndType(id, type);
    }

    @Override
    public Optional<GeneralEnumerationDTO> findOneByExample(GeneralEnumerationDTO generalEnumerationDTO, String type) {
        generalEnumerationDTO.setType(type);
        return findOneByExample(mapper.toEntity(generalEnumerationDTO));
    }

    @Override
    public List<GeneralEnumerationDTO> findByExample(GeneralEnumerationDTO example, String type) {
        example.setType(type);
        return findByExample(mapper.toEntity(example));
    }

    @Override
    public List<GeneralEnumerationDTO> findByTypes(String... types) {
        List<String> typeList = Lists.newArrayList(types);
        return mapper.toDto(repository.findByTypes(typeList));
    }

    private Optional<GeneralEnumerationDTO> findOneByExample(GeneralEnumeration generalEnumeration) {
        return repository.findOne(buildExample(generalEnumeration)).map(mapper::toDto);
    }

}
