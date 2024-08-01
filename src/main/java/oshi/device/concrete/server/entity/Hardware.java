package oshi.device.concrete.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "服务器硬件信息")
@Data
public class Hardware implements Serializable {
    @Schema(description = "主板信息")
    private String baseboard;
    @Schema(description = "核数")
    private int physicalProcessorCount;
    @Schema(description = "内存总量")
    private String memoryTotal;
    @Schema(description = "cpu信息")
    private String cpu;
    @Schema(description = "内存条信息")
    private String memory;
    @Schema(description = "操作系统信息")
    private String system;
    @Schema(description = "硬盘信息")
    private String disk;
    @Schema(description = "网卡信息")
    private String netInterface;
    @Schema(description = "ip")
    private String ip;
    @Schema(description = "网关")
    private String gateway;
}
