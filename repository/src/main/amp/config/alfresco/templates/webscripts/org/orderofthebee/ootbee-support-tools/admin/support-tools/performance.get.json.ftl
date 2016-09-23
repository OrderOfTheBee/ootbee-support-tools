{
    "MaxMemory" : ${memoryMetrics.heapMax?c},
    "TotalMemory" : ${memoryMetrics.heapCommitted?c},
    "UsedMemory" : ${memoryMetrics.heapUsed?c},
    "FreeMemory" : ${memoryMetrics.heapFree?c},
    "ProcessLoad" : ${cpuMetrics.processCPULoad?c},
    "SystemLoad" : ${cpuMetrics.systemCPULoad?c},
    "ThreadCount" : ${threadMetrics.threadCount?c},
    "PeakThreadCount": ${threadMetrics.peakThreadCount?c}
}