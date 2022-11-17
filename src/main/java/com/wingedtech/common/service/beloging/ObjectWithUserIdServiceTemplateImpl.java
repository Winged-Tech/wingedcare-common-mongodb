package com.wingedtech.common.service.beloging;

import com.wingedtech.common.security.SecurityUtils;
import com.wingedtech.common.service.GenericServiceTemplateImpl;
import com.wingedtech.common.service.mapper.EntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

/**
 * ObjectWithUserIdServiceTemplate接口的默认实现
 * @param <D>
 * @param <E>
 */
public abstract class ObjectWithUserIdServiceTemplateImpl<D extends ObjectWithUserId, E  extends ObjectWithUserId> extends GenericServiceTemplateImpl<D, E> implements ObjectWithUserIdServiceTemplate<D> {

    public ObjectWithUserIdServiceTemplateImpl(MongoRepository<E, String> repository, EntityMapper<D, E> mapper) {
        super(repository, mapper);
    }

    @Override
    public D save(D dto) {
        log.debug("Request to save entity : {}", dto);
        E entity = mapper.toEntity(dto);

        if (entity instanceof AbstractAuditingEntityWithUserId) {
            AbstractAuditingEntityWithUserId auditingEntity = (AbstractAuditingEntityWithUserId) entity;
            final String id = auditingEntity.getId();
            if (id != null) {
                repository.findById(id).ifPresent(e -> {
                    AbstractAuditingEntityWithUserId existingEntity = (AbstractAuditingEntityWithUserId) e;
                    auditingEntity.setCreatedBy(existingEntity.getCreatedBy());
                    auditingEntity.setCreatedDate(existingEntity.getCreatedDate());
                });
            }
        }

        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public D saveForCurrentUser(D dto) {
        final String currentUserId = getCurrentUserId();
        validateObjectUserId(dto, currentUserId);

        return save(dto);
    }

    /**
     * 验证当前用户是否可访问指定的对象
     * @param dto
     * @param currentUserId
     */
    private void validateObjectUserId(D dto, String currentUserId) {
        if (dto.getUserId() != null) {
            if (!ObjectWithUserIdUtils.isForUser(dto, currentUserId)) {
                throw new AccessDeniedException("只能存储属于当前用户的对象!");
            }
        }
        else {
            dto.setUserId(currentUserId);
        }
    }

    @Override
    public Page<D> findAllForCurrentUser(Pageable pageable) {
        return findAllForCurrentUserByExample(buildDefaultExample(), pageable);
    }

    @Override
    public Page<D> findAllForCurrentUserByExample(D example, Pageable pageable) {
        example.setUserId(getCurrentUserId());
        return findAllByExample(example, pageable);
    }

    @Override
    public Optional<D> findOneForCurrentUser(String id) {
        final Optional<D> one = findOne(id);
        if (one.isPresent() && belongsToCurrentUser(one.get())) {
            return one;
        }
        return Optional.empty();
    }

    @Override
    public boolean existsForCurrentUser(D example) {
        example.setUserId(getCurrentUserId());
        return super.existsByExample(example);
    }

    /**
     * 创建一个默认的Example对象，子类需实现此方法
     * @return
     */
    protected abstract D buildDefaultExample();

    /**
     * 获取当前用户的id标识，默认为当前用户的userLogin，子类可重载该方法
     * @return
     */
    @Override
    public String getCurrentUserId() {
        return SecurityUtils.getCurrentUserLoginWithException();
    }

    @Override
    public boolean belongsToCurrentUser(D example) {
        return ObjectWithUserIdUtils.isForUser(example, getCurrentUserId());
    }
}
