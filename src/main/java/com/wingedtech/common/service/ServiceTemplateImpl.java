package com.wingedtech.common.service;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wingedtech.common.service.mapper.EntityMapperWrapper;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ServiceTemplate的默认实现
 *
 * @param <D> DTO类
 * @param <E> Entity类
 * @author Jason
 */
@SuppressWarnings("unused")
public class ServiceTemplateImpl<D, E> implements ServiceTemplate<D> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final MongoRepository<E, String> repository;

    protected final EntityMapperWrapper mapper;

    public ServiceTemplateImpl(MongoRepository<E, String> repository, EntityMapperWrapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Save an entity.
     *
     * @param dto the entity to save
     * @return the persisted entity
     */
    @Override
    public <S extends D> S save(S dto) {
        log.debug("Request to save entity : {}", dto);
        E entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    /**
     * Save a list of entities
     *
     * @param dtoList the list of entities to save
     * @return the persisted entities
     */
    @Override
    public <S extends D> List<S> saveAll(List<S> dtoList) {
        log.debug("Request to save list of entity : {}", dtoList);
        List<E> entities = mapper.toEntity(dtoList);
        entities = repository.saveAll(entities);
        return mapper.toDto(entities);
    }

    /**
     * Get all the entities
     *
     * @return
     */
    @Override
    public <S extends D> List<S> findAll() {
        List<E> list = MoreObjects.firstNonNull(repository.findAll(), ImmutableList.of());
        if (list.isEmpty()) {
            return Lists.newArrayList();
        }
        return mapper.toDto(list);
    }

    /**
     * Get all the entities
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public <S extends D> Page<S> findAll(Pageable pageable) {
        log.debug("Request to get all entities");
        return repository.findAll(pageable).map(mapper::toDto);
    }

    /**
     * Get all the entities by id
     *
     * @param ids
     * @return
     */
    @Override
    public <S extends D> List<S> findAllById(Iterable<String> ids) {
        List<S> list = new ArrayList<>();
        if (!IterableUtils.isEmpty(ids)) {
            final Iterable<E> allById = repository.findAllById(ids);
            for (E entity : allById) {
                list.add(mapper.toDto(entity));
            }
        }
        return list;
    }

    /**
     * Get all the dto using example object.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public <S extends D> Page<S> findAllByExample(S example, Pageable pageable) {
        log.trace("Request to get all entities by example {}", example);
        return repository.findAll(buildExampleFromDto(example), pageable).map(mapper::toDto);
    }

    /**
     * Get one entity by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public <S extends D> Optional<S> findOne(String id) {
        log.trace("Request to get entity : {}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public <S extends D> Optional<S> findOne(S example) {
        log.trace("Request to get entity : {}", example);
        return repository.findOne(buildExampleFromDto(example)).map(mapper::toDto);
    }

    /**
     * Delete the entity by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete entity : {}", id);
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public <S extends D> boolean existsByExample(S example) {
        return repository.exists(buildExampleFromDto(example));
    }

    protected Example<E> buildExampleFromDto(D example) {
        return buildExample(mapper.toEntity(example));
    }

    protected Example<E> buildExample(E entity) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnorePaths("createdDate", "lastModifiedDate");
        return Example.of(entity, matcher);
    }
}
