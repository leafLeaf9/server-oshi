# server-oshi

oshi库监测指标服务

使用oshi库查询服务器相关指标，其中CPU温度在Windows某些系统中难以获取，根据 https://github.com/oshi/oshi/issues/119，
会再尝试从OpenHardwareMonitor获取。
