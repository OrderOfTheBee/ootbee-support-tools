<#include "../admin-template.ftl" />

<@page title=msg("performance.title") readonly=true customJSFiles=["ootbee-support-tools/js/smoothie.js", "ootbee-support-tools/js/performance-monitor.js"]>

    <script type="text/javascript">//<![CDATA[
        AdminSP.setServiceUrl('${url.service}');
        
        AdminSP.setInitialMemoryMetrics({
            MaxMemory : ${memoryMetrics.heapMax?c},
            TotalMemory : ${memoryMetrics.heapCommitted?c},
            UsedMemory : ${memoryMetrics.heapUsed?c},
            FreeMemory : ${memoryMetrics.heapFree?c}
        });
        
        AdminSP.setInitialThreadMetrics({
            ThreadCount : ${threadMetrics.threadCount?c},
            PeakThreadCount: ${threadMetrics.peakThreadCount?c}
        });
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("performance.intro-text")?html}</p>
        <@section label=msg("performance.memory.memory-graph") />
      
        <canvas id="memory" width="720" height="200"></canvas>
    </div>

    <div class="column-left">
        <@options id="memTimescale" name="memTimescale" label=msg("performance.chart-timescale") value="11">
            <@option label=msg("performance.chart-timescale.1min") value="1" />
            <@option label=msg("performance.chart-timescale.10mins") value="11" />
            <@option label=msg("performance.chart-timescale.60mins") value="61" />
            <@option label=msg("performance.chart-timescale.12hrs") value="721" />
            <@option label=msg("performance.chart-timescale.24hrs") value="1441" />
            <@option label=msg("performance.chart-timescale.48hrs") value="2881" />
            <@option label=msg("performance.chart-timescale.7days") value="10081" />
        </@options>
    </div>
    <div class="column-right">
        <div class="control field">
            <span class="label">${msg("performance.memory.max")?html}:</span>
            <span class="value" id="MaxMemory">${memoryMetrics.heapMax?c}</span>
        </div>
        <div class="control field">
            <span class="label">${msg("performance.memory.free")?html}:</span>
            <span class="value" id="FreeMemory">${memoryMetrics.heapFree?c}</span>
        </div>
        <div class="control field">
            <div style="background: #7fff7f; width:0.6em; height:0.7em; border:1px solid #00ff00; display:inline-block;"></div>
            <span class="label">${msg("performance.memory.committed")?html}:</span>
            <span class="value" id="TotalMemory">${memoryMetrics.heapCommitted?c}</span>
        </div>
        <div class="control field">
            <div style="background: #7f7fff; width:0.6em; height:0.7em; border:1px solid #0000ff; display:inline-block;"></div>
            <span class="label">${msg("performance.memory.used")?html}:</span>
            <span class="value" id="UsedMemory">${memoryMetrics.heapUsed?c}</span>
        </div>
    </div>

    <div class="column-full">
        <@section label=msg("performance.cpu.cpu-graph") />

        <canvas id="CPU" width="720" height="200"></canvas>
    </div>

    <div class="column-left">
        <@options id="cpuTimescale" name="cpuTimescale" label=msg("performance.chart-timescale") value="11">
            <@option label=msg("performance.chart-timescale.1min") value="1" />
            <@option label=msg("performance.chart-timescale.10mins") value="11" />
            <@option label=msg("performance.chart-timescale.60mins") value="61" />
            <@option label=msg("performance.chart-timescale.12hrs") value="721" />
            <@option label=msg("performance.chart-timescale.24hrs") value="1441" />
            <@option label=msg("performance.chart-timescale.48hrs") value="2881" />
            <@option label=msg("performance.chart-timescale.7days") value="10081" />
        </@options>
    </div>
    <div class="column-right">
        <div class="control field">
            <div style="background: #ff6464; width:0.6em; height:0.7em; border:1px solid #ff3232; display:inline-block;"></div> <span class="label">${msg("performance.cpu.system.percent")?html}</span><span class="label">:</span>
            <span class="value" id="SystemLoad">${cpuMetrics.systemCPULoad?c}</span>
        </div>
        <div class="control field">
            <div style="background: #fde2c3; width:0.6em; height:0.7em; border:1px solid #f99f38; display:inline-block;"></div> <span class="label">${msg("performance.cpu.process.percent")?html}</span><span class="label">:</span>
            <span class="value" id="ProcessLoad">${cpuMetrics.processCPULoad?c}</span>
        </div>
    </div>
   

    <div class="column-full">
        <@section label=msg("performance.Threads") />

        <canvas id="Threads" width="720" height="200"></canvas>
    </div>

    <div class="column-left">
        <@options id="threadsTimescale" name="threadsTimescale" label=msg("performance.chart-timescale") value="11">
            <@option label=msg("performance.chart-timescale.1min") value="1" />
            <@option label=msg("performance.chart-timescale.10mins") value="11" />
            <@option label=msg("performance.chart-timescale.60mins") value="61" />
            <@option label=msg("performance.chart-timescale.12hrs") value="721" />
            <@option label=msg("performance.chart-timescale.24hrs") value="1441" />
            <@option label=msg("performance.chart-timescale.48hrs") value="2881" />
            <@option label=msg("performance.chart-timescale.7days") value="10081" />
        </@options>
    </div>
    <div class="column-right">
        <div class="control field">
            <span class="label">${msg("performance.Threads.PeakThreadCount")?html}</span><span class="label">:</span>  
            <span class="value" id="PeakThreadCount">${threadMetrics.peakThreadCount?c}</span>
        </div>
        <div class="control field">
            <div style="background: #c3fcc3; width:0.6em; height:0.7em; border:1px solid #39BB39; display:inline-block;"></div> <span class="label">${msg("performance.Threads")?html}</span><span class="label">:</span>
            <span class="value" id="ThreadCount">${threadMetrics.threadCount?c}</span>
        </div>
    </div>
</@page>