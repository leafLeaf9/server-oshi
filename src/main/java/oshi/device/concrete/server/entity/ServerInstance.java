package oshi.device.concrete.server.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import oshi.device.concrete.router.entity.NetworkInterface;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务器实例数据表
 * </p>
 *
 * @author Administrator
 * @since 2024-04-09
 */
@Getter
@Setter
//@TableName("server_instance")
@Schema(description = "ServerInstance对象 服务器实例数据表")
public class ServerInstance implements Serializable {

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "CPU使用率，单位%")
    private BigDecimal cpuUsageRate;

    @Schema(description = "CPU温度，单位°C")
    private BigDecimal cpuTemp;

    @Schema(description = "内存使用率，单位%")
    private BigDecimal memoryUsageRate;

    @Schema(description = "磁盘使用率，单位%，格式为磁盘A:占用率,磁盘B:占用率 例如C:20,D:40")
    private String diskUsageRate;

    @Schema(description = "接口列表")
//    @TableField(exist = false)
    private List<NetworkInterface> interfaces;


    @Schema(description = "硬件信息")
//    @TableField(exist = false)
    private Hardware hardware;
}
