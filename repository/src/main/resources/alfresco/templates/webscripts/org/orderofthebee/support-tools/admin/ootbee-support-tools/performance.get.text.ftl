<#compress>
<#--
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.

-->
# HELP alfresco_system_performance General system performance metrics
# TYPE alfresco_system_performance gauge
alfresco_system_performance{type="MaxMemory"} ${memoryMetrics.heapMax?c}
alfresco_system_performance{type="TotalMemory"} ${memoryMetrics.heapCommitted?c}
alfresco_system_performance{type="UsedMemory"} ${memoryMetrics.heapUsed?c}
alfresco_system_performance{type="FreeMemory"} ${memoryMetrics.heapFree?c}
alfresco_system_performance{type="ProcessLoad"} ${cpuMetrics.processCPULoad?c}
alfresco_system_performance{type="SystemLoad"} ${cpuMetrics.systemCPULoad?c}
alfresco_system_performance{type="ThreadCount"} ${threadMetrics.threadCount?c}
alfresco_system_performance{type="PeakThreadCount"} ${threadMetrics.peakThreadCount?c}
</#compress>