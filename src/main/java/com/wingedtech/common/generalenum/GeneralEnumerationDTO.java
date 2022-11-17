package com.wingedtech.common.generalenum;

import com.google.common.collect.Maps;
import com.wingedtech.common.dto.AbstractAuditingEntityDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel("通用枚举信息")
@Data
public class GeneralEnumerationDTO extends AbstractAuditingEntityDTO {

    private String id;

    @ApiModelProperty("枚举类型, 不为空")
    private String type;

    @ApiModelProperty("名字, 和枚举类型对应, 与type值可以相同也可以不同")
    private String name;

    @ApiModelProperty("该枚举的值")
    private String value;

    @ApiModelProperty("展示顺序")
    private Integer sort;

    @ApiModelProperty("扩展信息")
    private Map<String, Object> extensionInfo;

    public GeneralEnumerationDTO setExtensionInfo(String key, Object value) {
        if (this.extensionInfo == null) {
            extensionInfo = Maps.newHashMap();
        }
        extensionInfo.put(key, value);
        return this;
    }

    @Override
    public void copy(AbstractAuditingEntityDTO dto) {
        super.copy(dto);
        GeneralEnumerationDTO enumerationDTO = dto instanceof GeneralEnumerationDTO ? ((GeneralEnumerationDTO) dto) : null;
        if (enumerationDTO != null) {
            setId(enumerationDTO.getId());
            setType(enumerationDTO.getType());
            setName(enumerationDTO.getName());
            setValue(enumerationDTO.getValue());
            setExtensionInfo(Maps.newHashMap(enumerationDTO.getExtensionInfo()));
        }
    }

    /**
     * 数据脱敏
     */
    @Override
    public void desensitize() {
        super.desensitize();
    }
}
