package com.wingedtech.common.generalenum;

import com.wingedtech.common.errors.BusinessException;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

@RestController
@RequestMapping("/api")
@Slf4j
public class GeneralEnumerationResource {

    private final GeneralEnumerationService service;

    @Autowired
    public GeneralEnumerationResource(GeneralEnumerationService service) {
        this.service = service;
    }

    @PostMapping("/enumeration/{type}/add")
    public ResponseEntity<GeneralEnumerationDTO> addEnumerationItem(@RequestBody GeneralEnumerationDTO generalEnumerationDTO, @PathVariable String type) {
        log.debug("REST request to save GeneralEnumeration : {}", generalEnumerationDTO);
        if (StringUtils.isNotEmpty(generalEnumerationDTO.getId())) {
            throw new BusinessException("A new generalEnumeration cannot already have an ID");
        }
        return ResponseEntity.ok(service.save(generalEnumerationDTO, type));
    }

    @PostMapping("/enumeration/{type}/")
    public ResponseEntity<GeneralEnumerationDTO> editEnumerationItem(@RequestBody GeneralEnumerationDTO generalEnumerationDTO, @PathVariable String type) {
        log.debug("REST request to edit GeneralEnumeration : {}", generalEnumerationDTO);
        if (StringUtils.isEmpty(generalEnumerationDTO.getId())) {
            throw new BusinessException("Edit generalEnumeration cannot have null ID");
        }
        return ResponseEntity.ok(service.save(generalEnumerationDTO, type));
    }

    @GetMapping("/enumeration/by-id/{id}")
    @ApiOperation("通过id获取一个枚举项的详情")
    public ResponseEntity<GeneralEnumerationDTO> getEnumerationById(@NotNull @PathVariable("id") String id) {
        return ResponseUtil.wrapOrNotFound(service.findOne(id));
    }

    @GetMapping("/enumeration/{type}/")
    public ResponseEntity<List<GeneralEnumerationDTO>> getListEnumerationItems(@PathVariable String type) {
        List<GeneralEnumerationDTO> enumerationItemsByType = service.findEnumerationItemsByType(type);
        // 根据sort字段进行排序
        enumerationItemsByType.sort(Comparator.comparing(GeneralEnumerationDTO::getSort, Comparator.nullsLast(Integer::compareTo)));
        return ResponseEntity.ok(enumerationItemsByType);
    }

    @DeleteMapping("/enumeration/{type}/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable String id, @PathVariable String type) {
        log.debug("REST request to delete GeneralEnumeration : {}", id);
        service.delete(id, type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enumeration/{type}/find-or-insert")
    public ResponseEntity<GeneralEnumerationDTO>
    findOrInsert(@RequestBody GeneralEnumerationDTO generalEnumerationDTO, @PathVariable String type) {
        return ResponseEntity.ok(service.findOrInsert(generalEnumerationDTO, type));
    }

    @PostMapping("/enumeration/{type}/one-by-example")
    public ResponseEntity<GeneralEnumerationDTO> findOneByExample(@RequestBody GeneralEnumerationDTO example, @PathVariable String type) {
        return ResponseUtil.wrapOrNotFound(service.findOneByExample(example, type));
    }

    @PostMapping("/enumeration/{type}/by-example")
    public ResponseEntity<List<GeneralEnumerationDTO>> findByExample(@RequestBody GeneralEnumerationDTO example, @PathVariable String type) {
        return ResponseEntity.ok(service.findByExample(example, type));
    }
}
