package com.wingedtech.common.holiday;

import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.util.ExampleQueryVM;
import com.wingedtech.common.util.PageableParam;
import com.wingedtech.common.util.PaginationUtil;
import com.wingedtech.common.util.excel.ExcelImport;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

@RestController
@RequestMapping("/api")
@Slf4j
public class HolidayResource {

    private final HolidayService service;

    @Autowired
    public HolidayResource(HolidayService service) {
        this.service = service;
    }


    @GetMapping("/holiday/by-id/{id}")
    @ApiOperation("通过id获取一个详情")
    public ResponseEntity<HolidayDTO> getById(@NotNull @PathVariable("id") String id) {
        return ResponseUtil.wrapOrNotFound(service.findOne(id));
    }

    @GetMapping("/holiday/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") String id) {
        log.debug("REST request to delete Holiday : {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/holiday/delete-by-ids")
    public ResponseEntity<Void> delete(@RequestBody List<String> ids) {
        service.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/holiday/insert-one")
    public ResponseEntity<HolidayDTO> findOrInsert(@RequestBody HolidayDTO HolidayDTO) {
        return ResponseEntity.ok(service.findOrInsert(HolidayDTO));
    }

    @PostMapping("/holiday/create")
    public ResponseEntity<List<HolidayDTO>> findOneByExample(@RequestBody List<HolidayDTO> example) {
        return ResponseEntity.ok(service.insertHolidays(example));
    }

    @PostMapping("/holiday/edit")
    public ResponseEntity<HolidayDTO> edit(@RequestBody HolidayDTO holidayDTO) {
        return ResponseEntity.ok(service.edit(holidayDTO));
    }

    @PostMapping("/holiday/by-example")
    public ResponseEntity<List<HolidayDTO>> findByExample(@RequestBody ExampleQueryVM<HolidayDTO> exampleVM) {
        Page<HolidayDTO> page = service.findAllByExample(exampleVM.getExample(), exampleVM.getPageable().toPageable());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/holiday/by-example");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/holiday/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> importByExcel(@RequestPart(name = "excel") MultipartFile excel) {
        ExcelImport excelImport;
        try {
            excelImport = new ExcelImport(excel, 0, 0);
        } catch (Exception e) {
            throw new BusinessException("表格文件解析失败，请上传正确的Excel表格文件！");
        }
        service.importHolidays(excelImport);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/holiday/search")
    public ResponseEntity<List<HolidayDTO>> search(@RequestBody HolidaySearchDTO searchDTO) {
        Page<HolidayDTO> page = service.search(searchDTO);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/holiday/search");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/holiday/init-weekend/{year}")
    public ResponseEntity<Void> initWeekend(@PathVariable(value = "year") Integer year, @RequestBody(required = false) List<Integer> month) {
        service.initWeekend(year, month);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/holiday/is-holiday")
    public ResponseEntity<Boolean> isHoliday(@RequestBody HolidayDTO holidayDTO) {
        return ResponseEntity.ok(service.isHoliday(holidayDTO));
    }

    @GetMapping("/holiday/get-future-holidays")
    public ResponseEntity<List<HolidayDTO>> getFutureHolidays(@RequestParam Integer days, @RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer day) {
        return ResponseEntity.ok(service.getFutureHolidays(LocalDate.of(year, month, day), days));
    }

    /**
     * 获取当前日期到指定的截止日期的数据
     *
     * @param deadline 截止日期
     * @return the List<HolidayDTO>
     */
    @GetMapping("/holiday/before-deadline")
    public ResponseEntity<List<HolidayDTO>> getBeforeDeadline(@RequestParam long deadline) {
        Page<HolidayDTO> periodOfTime = service.findPeriodOfTime(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), Instant.ofEpochMilli(deadline), PageableParam.unpaged());
        return ResponseEntity.ok(periodOfTime.getContent());
    }
}
