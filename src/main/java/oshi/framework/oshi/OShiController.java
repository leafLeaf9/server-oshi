package oshi.framework.oshi;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.device.concrete.router.entity.NetworkInterface;
import oshi.device.concrete.server.entity.Hardware;
import oshi.device.concrete.server.entity.ServerInstance;
import oshi.framework.constant.StringPool;
import oshi.framework.remote.RemoteObjectUtil;
import oshi.framework.util.BigDecimalUtils;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static oshi.framework.util.BigDecimalUtils.getRateDecimal;

@Slf4j
@RequestMapping("/monitor/server")
@RestController
public class OShiController {

    //    @ResponseResultBody
    @GetMapping("getServerInstance")
    public ServerInstance getServerInstance() {
        ServerInstance result = new ServerInstance();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();
        double systemCpuLoad = processor.getSystemCpuLoad(1000);
        Sensors sensors = hal.getSensors();
        result.setCpuUsageRate(BigDecimal.valueOf(systemCpuLoad).multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP));
        result.setCpuTemp(BigDecimal.valueOf(sensors.getCpuTemperature()).setScale(2, RoundingMode.HALF_UP));
        GlobalMemory memory = hal.getMemory();
        result.setMemoryUsageRate(getRateDecimal(memory.getTotal() - memory.getAvailable(), memory.getTotal()));
        result.setDiskUsageRate(buildDiskUsageRate(si));
        result.setInterfaces(buildNetworkInterfaces(hal.getNetworkIFs()));

        if (result.getCpuTemp().compareTo(BigDecimal.ZERO) == 0) {
            log.info("服务器信息获取-oshi获取CPU温度为0，尝试从OpenHardwareMonitor获取。");
            try {
                result.setCpuTemp(getCpuTempFromOpenHardwareMonitor());
                log.info("服务器信息获取-OpenHardwareMonitor获取CPU温度成功，值为{}。", result.getCpuTemp());
            } catch (RuntimeException e) {
                log.error("服务器信息获取-从OpenHardwareMonitor获取CPU温度异常，请检查该应用及web服务是否启动。", e);
            }
        }
        result.setHardware(buildHardware(si));
        return result;
    }

    private Hardware buildHardware(SystemInfo si) {
        Hardware result = new Hardware();
        HardwareAbstractionLayer hal = si.getHardware();
        result.setBaseboard(hal.getComputerSystem().getBaseboard().toString());
        OperatingSystem os = si.getOperatingSystem();
        result.setSystem(os.toString());
        result.setPhysicalProcessorCount(hal.getProcessor().getPhysicalProcessorCount());
        result.setCpu(hal.getProcessor().getProcessorIdentifier().getName());
        GlobalMemory globalMemory = hal.getMemory();
        result.setMemoryTotal(FormatUtil.formatBytes(globalMemory.getTotal()));
        result.setMemory(globalMemory.getPhysicalMemory().stream().map(PhysicalMemory::toString)
                .collect(Collectors.joining("\n")));
        result.setDisk(hal.getDiskStores().stream().map(e -> e.toString() + "\n"
                        + e.getPartitions().stream().map(part -> " |-- " + part.toString()).collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n")));
        result.setNetInterface(hal.getNetworkIFs().stream().map(this::buildFullName).collect(Collectors.joining("\n")));
        result.setGateway(os.getNetworkParams().getIpv4DefaultGateway());
        return result;
    }

    public String buildDiskUsageRate(SystemInfo si) {
        OperatingSystem os = si.getOperatingSystem();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        Stream<OSFileStore> stream = fileStores.stream().filter(e -> e.getTotalSpace() > 0);
        /*if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            stream = stream.filter(e -> Objects.equals(e.getName(), StringPool.SLASH));
        }*/
        return stream.map(e -> {
            long sum = e.getTotalSpace();
            long free = e.getUsableSpace();
            long used = Math.max(0, sum - free);
            BigDecimal rate = getRateDecimal(used, sum);
            return e.getName() + StringPool.COLON + rate;
        }).collect(Collectors.joining(StringPool.COMMA));
    }

    public List<NetworkInterface> buildNetworkInterfaces(List<NetworkIF> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 更新数据
        list.forEach(NetworkIF::updateAttributes);
        long[] initialBytesReceive = new long[list.size()];
        long[] initialBytesSent = new long[list.size()];

        for (int i = 0; i < list.size(); i++) {
            NetworkIF net = list.get(i);
            initialBytesReceive[i] = net.getBytesRecv();
            initialBytesSent[i] = net.getBytesSent();
        }
        // 等待1秒
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 更新所有网络接口的属性并获取最终字节数
        list.forEach(NetworkIF::updateAttributes);
        return list.stream().map(e -> {
            int index = list.indexOf(e);
            long bytesReceived = e.getBytesRecv() - initialBytesReceive[index];
            long bytesSent = e.getBytesSent() - initialBytesSent[index];
            long speed = e.getSpeed();
            return NetworkInterface.builder()
                    .interfaceId(e.getIndex())
                    .interfaceDesc(buildFullName(e))
                    .status(e.getIfOperStatus() == NetworkIF.IfOperStatus.UP)
                    .inboundTraffic(e.getBytesRecv())
                    .outboundTraffic(e.getBytesSent())
                    .bandwidth(BigDecimal.valueOf(speed).divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP))
                    .inboundBandwidthRate(BigDecimalUtils.getRateDecimal(bytesReceived * 8, speed))
                    .outboundBandwidthRate(BigDecimalUtils.getRateDecimal(bytesSent * 8, speed))
                    .inboundPacketsErrorRate(BigDecimalUtils.getRateDecimal(e.getInErrors(), e.getBytesRecv()))
                    .outboundPacketsErrorRate(BigDecimalUtils.getRateDecimal(e.getOutErrors(), e.getBytesSent()))
                    .build();
        }).collect(Collectors.toList());
    }

    public String buildFullName(NetworkIF net) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(net.getName());
        if (!net.getName().equals(net.getDisplayName())) {
            sb.append(" (").append(net.getDisplayName()).append(")");
        }
        if (!net.getIfAlias().isEmpty()) {
            sb.append(" [IfAlias=").append(net.getIfAlias()).append("]");
        }
        return sb.toString();
    }

    @GetMapping("printServerInstance")
    public void printServerInstance() {
        new SystemInfoTest().printServerInstance();
    }

    /**
     * 获取cpu温度 通过 OpenHardwareMonitor 提供的接口
     * <a href="https://github.com/oshi/oshi/issues/119">...</a>
     */
    @GetMapping("getCpuTempFromOpenHardwareMonitor")
    public BigDecimal getCpuTempFromOpenHardwareMonitor() {
        String url = "http://localhost:8085/data.json";
        JSONObject json = RemoteObjectUtil.getSimpleRestTemplate().getForObject(url, JSONObject.class);
        JSONArray children = json.getJSONArray("Children");

        // 递归遍历JSON数据结构
        return findCpuPackageTemperature(children);
    }

    private BigDecimal findCpuPackageTemperature(JSONArray children) {
        for (int i = 0; i < children.size(); i++) {
            JSONObject child = children.getJSONObject(i);
            if (child.containsKey("Text") && "CPU Package".equals(child.getString("Text"))) {
                return new BigDecimal(child.getString("Value").replace("°C", "").trim());
            }
            if (child.containsKey("Children")) {
                BigDecimal temp = findCpuPackageTemperature(child.getJSONArray("Children"));
                if (temp.compareTo(BigDecimal.ZERO) != 0) {
                    return temp;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
