/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 * 
 * This file is part of Community Support Tools
 * 
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco Copyright
 * (C) 2005-2016 Alfresco Software Limited.
 */

/* global Admin: false, el: false, TimeSeries: false, SmoothieChart: false */

// The AdminAS root object has been extracted from the Alfresco Support Tools
// admin-activesessions.get.html.ftl trim down page HTML sizes and promote clean
// separation of concerns

/**
 * Active Sessions Component
 */
var AdminAS = AdminAS || {};

(function()
{
    var serviceUrl, serviceContext, initialDBData, dbGraph, userGraph, _messages = {};

    Admin.addEventListener(window, 'load', function()
    {
        AdminAS.createCharts();
        setInterval(AdminAS.updateUsers, 60000);
        
        Admin.addEventListener(el('dbTimescale'), 'change', function()
        {
            AdminAS.changeChartTimescale(this, el('database'), dbGraph);
        });
        Admin.addEventListener(el('userTimescale'), 'change', function()
        {
            AdminAS.changeChartTimescale(this, el('users'), userGraph);
        });
    });

    AdminAS.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };
    
    AdminAS.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminAS.setInitialDBData = function setInitialDBData(data)
    {
        initialDBData = data;
    };

    AdminAS.addMessage = function addMessage(key, message)
    {
        _messages[key] = message;
    };

    AdminAS.createCharts = function createCharts()
    {
        var dbChartLine, dbChartLineIdle, userChartLine, chartResizer;
        
        dbChartLine = new TimeSeries();
        dbChartLineIdle = new TimeSeries();
        userChartLine = new TimeSeries();
        
        chartResizer = function()
        {
            var databaseCanvas, userCanvas;

            databaseCanvas = el('database');
            userCanvas = el('users');
            databaseCanvas.width = databaseCanvas.parentNode.clientWidth;
            userCanvas.width = userCanvas.parentNode.clientWidth;
        };
        setInterval(chartResizer, 15000);
        setTimeout(chartResizer, 0);

        setInterval(function()
        {
            Admin.request({
                url : serviceUrl + '?format=json',
                fnSuccess : function(res)
                {
                    if (res.responseJSON)
                    {
                        var json = res.responseJSON, now = new Date().getTime();

                        el('NumActive').innerHTML = json.NumActive;
                        el('NumIdle').innerHTML = json.NumIdle;
                        
                        el('UserCountNonExpired').innerHTML = json.UserCountNonExpired;
                        el('TicketCountNonExpired').innerHTML = json.TicketCountNonExpired;
                        
                        dbChartLine.append(now, json.NumActive);
                        dbChartLineIdle.append(now, json.NumIdle);
                        userChartLine.append(now, json.UserCountNonExpired);
                    }
                }
            });
        }, 2000);

        dbGraph = new SmoothieChart({
            labels : {
                precision : 0,
                fillStyle : '#333333'
            },
            timestampFormatter : SmoothieChart.timeFormatter,
            millisPerPixel : 1000,
            maxValue : initialDBData.MaxActive,
            minValue : 0,
            grid : {
                strokeStyle : '#cccccc',
                fillStyle : '#ffffff',
                lineWidth : 1,
                millisPerLine : 60000,
                verticalSections : 10
            }
        });
        dbGraph.addTimeSeries(dbChartLine, {
            strokeStyle : 'rgb(0, 255, 0)',
            fillStyle : 'rgba(0, 255, 0, 0.3)',
            lineWidth : 2
        });
        dbGraph.addTimeSeries(dbChartLineIdle, {
            strokeStyle : 'rgb(255, 153, 0)',
            fillStyle : 'rgba(255, 204, 0, 0.3)',
            lineWidth : 2
        });
        dbGraph.streamTo(document.getElementById('database'), 2000);

        userGraph = new SmoothieChart({
            labels : {
                precision : 0,
                fillStyle : '#333333'
            },
            timestampFormatter : SmoothieChart.timeFormatter,
            millisPerPixel : 1000,
            minValue : 0,
            maxValueScale : 2,
            grid : {
                strokeStyle : '#cccccc',
                fillStyle : '#ffffff',
                lineWidth : 1,
                millisPerLine : 60000,
                verticalSections : 10
            }
        });
        userGraph.addTimeSeries(userChartLine, {
            strokeStyle : 'rgb(0, 0, 255)',
            fillStyle : 'rgba(0, 0, 255, 0.3)',
            lineWidth : 2
        });
        userGraph.streamTo(document.getElementById('users'), 2000);
    };

    AdminAS.changeChartTimescale = function changeChartTimescale(element, canvas, chart)
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

    AdminAS.updateUsers = function updateUsers(userName)
    {
        var data = {};
        
        if (typeof userName === 'string')
        {
            data.userName = userName;
        }
        
        Admin.request({
            method : typeof userName === 'string' ? 'POST' : 'GET',
            url : serviceUrl + '-updateUsers',
            data : data,
            fnSuccess : function(res)
            {
                if (res.responseJSON)
                {
                    var json = res.responseJSON;

                    /* Clean and refresh the table */
                    var table = el('users-table');
                    while (table.rows.length > 1)
                    {
                        table.deleteRow(table.rows.length - 1);
                    }

                    var users = json.users;
                    if (users.length > 0)
                    {
                        for (var i = 0; i < users.length; i++)
                        {
                            var rows = [];
                            rows.push('<a href="' + serviceContext + '/api/people/' + encodeURIComponent(users[i].username) + '">' + Admin.html(users[i].username)
                                    + '</a>');
                            rows.push(Admin.html(users[i].firstName));
                            rows.push(Admin.html(users[i].lastName));
                            rows.push('<a href="mailto:' + encodeURIComponent(users[i].email) + '">' + Admin.html(users[i].email) + '</a>');
                            rows.push('<a href="#" onclick="AdminAS.updateUsers(\'' + users[i].username + '\');\'>'
                                    + Admin.html(_messages.logoff) + '</a>');
                            Admin.addTableRow(table, rows);
                        }
                    }
                }
            }
        });
    };

})();
