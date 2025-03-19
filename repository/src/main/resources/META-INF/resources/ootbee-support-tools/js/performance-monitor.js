/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */
/* global el: false, Admin: false, TimeSeries: false, SmoothieChart: false */
/**
 * System Performance Component
 */
var AdminSP = AdminSP || {};

(function()
{
    var serviceUrl, initialMemoryMetrics, initialThreadMetrics, memGraph, cpuGraph, threadGraph;
    
    // The AdminSP root object has been extracted from the Alfresco Support Tools
    // admin-performance.get.html.ftl trim down page HTML sizes and promote clean
    // separation of concerns
    /* Page load handler */
    Admin.addEventListener(window, 'load', function()
    {
        AdminSP.createCharts();

        Admin.addEventListener(el('memTimescale'), 'change', function()
        {
            AdminSP.changeChartTimescale(this, el('memory'), memGraph);
        });
        Admin.addEventListener(el('cpuTimescale'), 'change', function()
        {
            AdminSP.changeChartTimescale(this, el('CPU'), cpuGraph);
        });
        Admin.addEventListener(el('threadsTimescale'), 'change', function()
        {
            AdminSP.changeChartTimescale(this, el('Threads'), threadGraph);
        });
    });

    AdminSP.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminSP.setInitialMemoryMetrics = function setInitialMemoryMetrics(metrics)
    {
        initialMemoryMetrics = metrics;
    };

    AdminSP.setInitialThreadMetrics = function setInitialThreadMetrics(metrics)
    {
        initialThreadMetrics = metrics;
    };

    AdminSP.createCharts = function createCharts()
    {
        var memChartLineComtd, memChartLineUsed, processLoadChartLinePcent, systemLoadChartLinePcent, threadChartLine,
        chartResizer;
        
        memChartLineComtd = new TimeSeries();
        memChartLineUsed = new TimeSeries();
        processLoadChartLinePcent = new TimeSeries();
        systemLoadChartLinePcent = new TimeSeries();
        threadChartLine = new TimeSeries();

        chartResizer = function()
        {
            var memoryCanvas, cpuCanvas, threadCanvas;

            memoryCanvas = el('memory');
            cpuCanvas = el('CPU');
            threadCanvas = el('Threads');
            memoryCanvas.width = memoryCanvas.parentNode.clientWidth;
            cpuCanvas.width = cpuCanvas.parentNode.clientWidth;
            threadCanvas.width = threadCanvas.parentNode.clientWidth;
        };
        setInterval(chartResizer, 15000);
        setTimeout(chartResizer, 0);

        setInterval(function()
        {
            Admin.request({
                url : serviceUrl + '?format=json',
                fnSuccess : function(res)
                {
                    var json, now;
                    
                    if (res.responseJSON)
                    {
                        json = res.responseJSON;
                        now = new Date().getTime();

                        el('MaxMemory').innerHTML = json.MaxMemory;
                        el('TotalMemory').innerHTML = json.TotalMemory;
                        el('UsedMemory').innerHTML = json.UsedMemory;
                        el('FreeMemory').innerHTML = json.FreeMemory;

                        el('ProcessLoad').innerHTML = json.ProcessLoad;
                        el('SystemLoad').innerHTML = json.SystemLoad;
                        el('ThreadCount').innerHTML = json.ThreadCount;
                        el('PeakThreadCount').innerHTML = json.PeakThreadCount;

                        memChartLineComtd.append(now, json.TotalMemory);
                        memChartLineUsed.append(now, json.UsedMemory);
                        processLoadChartLinePcent.append(now, json.ProcessLoad);
                        systemLoadChartLinePcent.append(now, json.SystemLoad);
                        threadChartLine.append(now, json.ThreadCount);
                    }
                }
            });
        }, 2000);

        memGraph = new SmoothieChart({
            labels : {
                precision : 0,
                fillStyle : '#333333'
            },
            sieve : true,
            timestampFormatter : SmoothieChart.timeFormatter,
            millisPerPixel : 1000,
            maxValue : initialMemoryMetrics.MaxMemory,
            minValue : 0,
            grid : {
                strokeStyle : '#cccccc',
                fillStyle : '#ffffff',
                lineWidth : 1,
                millisPerLine : 60000,
                verticalSections : 10
            }
        });
        memGraph.addTimeSeries(memChartLineComtd, {
            strokeStyle : 'rgb(0, 255, 0)',
            fillStyle : 'rgba(0, 255, 0, 0.3)',
            lineWidth : 2
        });
        memGraph.addTimeSeries(memChartLineUsed, {
            strokeStyle : 'rgb(0 ,0 , 255)',
            fillStyle : 'rgba(0, 0, 255, 0.3)',
            lineWidth : 2
        });
        memGraph.streamTo(document.getElementById('memory'), 1000);

        cpuGraph = new SmoothieChart({
            labels : {
                precision : 0,
                fillStyle : '#333333'
            },
            sieve : true,
            timestampFormatter : SmoothieChart.timeFormatter,
            millisPerPixel : 1000,
            maxValue : 100,
            minValue : 0,
            grid : {
                strokeStyle : '#cccccc',
                fillStyle : '#ffffff',
                lineWidth : 1,
                millisPerLine : 60000,
                verticalSections : 10
            }
        });
        cpuGraph.addTimeSeries(processLoadChartLinePcent, {
            strokeStyle : 'rgb(249, 159, 56)',
            fillStyle : 'rgba(249, 159, 56, 0.3)',
            lineWidth : 2
        });
        cpuGraph.addTimeSeries(systemLoadChartLinePcent, {
            strokeStyle : 'rgb(255, 100, 100)',
            fillStyle : 'rgba(255, 100, 100, 0.3)',
            lineWidth : 2
        });
        cpuGraph.streamTo(document.getElementById('CPU'), 1000);

        threadGraph = new SmoothieChart({
            labels : {
                precision : 0,
                fillStyle : '#333333'
            },
            sieve : true,
            timestampFormatter : SmoothieChart.timeFormatter,
            millisPerPixel : 1000,
            maxValueScale : 1.25,
            minValue : 0,
            grid : {
                strokeStyle : '#cccccc',
                fillStyle : '#ffffff',
                lineWidth : 1,
                millisPerLine : 60000,
                verticalSections : 10
            }
        });
        threadGraph.addTimeSeries(threadChartLine, {
            strokeStyle : 'rgb(56, 187, 56)',
            fillStyle : 'rgba(56, 187, 56, 0.3)',
            lineWidth : 2
        });
        threadGraph.streamTo(document.getElementById('Threads'), 1000);
    };

    AdminSP.changeChartTimescale = function changeChartTimescale(element, canvas, chart)
    {
        var value, intVal, width, height, mspl, mspp, lpc, x;
        
        // get width of current canvas
        value = element.options[element.selectedIndex].value;
        intVal = parseInt(value);
        width = canvas.width;
        height = canvas.height;
        // get how many divisions there are
        mspl = chart.options.grid.millisPerLine;
        mspp = chart.options.millisPerPixel;
        lpc = (width * mspp) / mspl;

        // figure out time scale
        x = Math.ceil(intVal * 60);
        chart.options.millisPerPixel = (x / width) * 1000;
        chart.options.grid.millisPerLine = (width * chart.options.millisPerPixel) / lpc;
        if (value > 2900)
        {
            chart.options.timestampFormatter = SmoothieChart.dateFormatter;
        }
        else
        {
            chart.options.timestampFormatter = SmoothieChart.timeFormatter;
        }
        if (value < 3)
        {
            chart.options.timestampFormatter = SmoothieChart.secondsFormatter;
        }
    };

})();