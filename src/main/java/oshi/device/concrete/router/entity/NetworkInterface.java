package oshi.device.concrete.router.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 网络接口数据表
 * </p>
 *
 * @author Administrator
 * @since 2024-04-09
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@TableName("network_interface")
@Schema(description = "NetworkInterface对象 路由器交换机接口数据表")
public class NetworkInterface implements Serializable {

    //    @TableId(value = "id", type = IdType.ASSIGN_ID)
//    @TableId
    private Long id;

    @Schema(description = "关联主表编号")
    private Long recordId;

    @Schema(description = "接口序号")
    private Integer interfaceId;

    @Schema(description = "采样时间")
//    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "接口描述")
    private String interfaceDesc;

    @Schema(description = "更新时间")
//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "入口流量，单位字节byte")
    private Long inboundTraffic;

    @Schema(description = "出口流量，单位字节byte")
    private Long outboundTraffic;

    @Schema(description = "带宽上限，单位Mbps")
    private BigDecimal bandwidth;

    @Schema(description = "入口带宽占用率，单位%")
    private BigDecimal inboundBandwidthRate;

    @Schema(description = "出口带宽占用率，单位%")
    private BigDecimal outboundBandwidthRate;

    @Schema(description = "入口包转发错误率，单位%")
    private BigDecimal inboundPacketsErrorRate;

    @Schema(description = "出口包转发错误率，单位%")
    private BigDecimal outboundPacketsErrorRate;

    @Schema(description = "接口状态 0异常 1正常")
    private Boolean status;

    public NetworkInterface(Long recordId) {
        this.recordId = recordId;
    }
}
