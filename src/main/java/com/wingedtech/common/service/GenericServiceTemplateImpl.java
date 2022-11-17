package com.wingedtech.common.service;

import com.wingedtech.common.service.mapper.EntityMapper;
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
import java.util.stream.Collectors;

/**
 * GenericServiceTemplate的默认实现
 * @param <D> DTO类
 * @param <E> Entity类
 *
 * <pre class="code">
 * @Service
 * public class GoodsHistoryServiceImpl extends GenericServiceTemplateImpl<GoodsHistoryDTO, GoodsHistory> implements GoodsHistoryService {
 *
 *     private final GoodsHistoryRepository goodsHistoryRepository;
 *
 *     private final GoodsHistoryMapper goodsHistoryMapper;
 *
 *     public GoodsHistoryServiceImpl(GoodsHistoryRepository goodsHistoryRepository, GoodsHistoryMapper goodsHistoryMapper) {
 *         super(goodsHistoryRepository,goodsHistoryMapper);
 *         this.goodsHistoryRepository = goodsHistoryRepository;
 *         this.goodsHistoryMapper = goodsHistoryMapper;
 *     }
 * }
 * </pre>
 *
 * @author taozhou
 */
@SuppressWarnings("unused")
public class GenericServiceTemplateImpl<D, E> implements GenericServiceTemplate<D> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final MongoRepository<E, String> repository;

    protected final EntityMapper<D, E> mapper;

    public GenericServiceTemplateImpl(MongoRepository<E, String> repository, EntityMapper<D, E> mapper) {
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
    public D save(D dto) {
        if (log.isTraceEnabled()) {
            log.trace("Request to save entity : {}", dto);
        }
        E entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    /**
     * Save a list of entities
     * @param dtoList the list of entities to save
     * @return the persisted entities
     */
    @Override
    public List<D> saveAll(List<D> dtoList) {
        if (log.isTraceEnabled()) {
            log.trace("Request to save list of entity : {}", dtoList);
        }
        List<E> entities = mapper.toEntity(dtoList);
        entities = repository.saveAll(entities);
        return mapper.toDto(entities);
    }

    /**
     * Get all the entities
     * @return
     */
    @Override
    public List<D> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Get all the entities
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<D> findAll(Pageable pageable) {
        log.trace("Request to get all entities");
        return repository.findAll(pageable).map(mapper::toDto);
    }

    /**
     * Get all the entities by id
     * @param ids
     * @return
     */
    @Override
    public List<D> findAllById(Iterable<String> ids) {
        List<D> list = new ArrayList<>();
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
    public Page<D> findAllByExample(D example, Pageable pageable) {
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
    public Optional<D> findOne(String id) {
        log.trace("Request to get entity : {}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<D> findOne(D example) {
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
    public boolean existsByExample(D example) {
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
