/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

/* global Admin: false, el: false, TimeSeries: false, SmoothieChart: false, $: false */

// The AdminAS root object has been extracted from the Alfresco Support Tools
// admin-activesessions.get.html.ftl trim down page HTML sizes and promote clean
// separation of concerns
/**
 * Active Sessions Component
 */
var AdminAS = AdminAS || {};

(function()
{
    var serviceContext, initialDBData, dbGraph, userGraph, userTable, messages = {};

    Admin.addEventListener(window, 'load', function()
    {
        AdminAS.createCharts();
        AdminAS.createUserTable();

        Admin.addEventListener(el('dbTimescale'), 'change', function()
        {
            AdminAS.changeChartTimescale(this, el('database'), dbGraph);
        });
        Admin.addEventListener(el('userTimescale'), 'change', function()
        {
            AdminAS.changeChartTimescale(this, el('users'), userGraph);
        });
    });

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
        messages[key] = message;
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
                url : serviceContext + '/ootbee/admin/active-sessions',
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

    AdminAS.createUserTable = function createUserTable()
    {
        var dataTableConfig;

        dataTableConfig = {
            ajax : {
                url : serviceContext + '/ootbee/admin/active-sessions/users',
                dataSrc : 'users',
                dataType : 'json'
            },
            paging : true,
            pagingType : 'simple_numbers',
            pageLength: 10,
            lengthChange: false,
            autoWidth : false,
            columnDefs : [
                    {
                        render : function renderUserProfileLink(data)
                        {
                            return '<a href="' + serviceContext + '/api/people/' + encodeURIComponent(data) + '">' + data + '</a>';
                        },
                        targets : [ 0 ]
                    },
                    {
                        render : function renderUserEmailLink(data)
                        {
                            var rendered = '';
                            if (data !== '')
                            {
                                rendered = '<a href="mailto:' + Admin.html(data) + '">' + data + '</a>';
                            }
                            return rendered;
                        },
                        targets : [ 3 ]
                    },
                    {
                        orderable : false,
                        render : function renderUserInvalidateLink(data)
                        {
                            return '<a href="#" onclick="AdminAS.logOffUser(\'' + data + '\');">'
                                    + Admin.html(messages['activesessions.users.logoff']) + '</a>';
                        },
                        targets : [ 4 ]
                    } ],
            columns : [ {
                data : 'userName'
            }, {
                data : 'firstName',
                defaultContent : ''
            }, {
                data : 'lastName',
                defaultContent : ''
            }, {
                data : 'email',
                defaultContent : ''
            }, {
                data : 'userName'
            } ]
        };

        userTable = $('#users-table').DataTable(dataTableConfig);
    };

    AdminAS.refreshUserTable = function refereshUserTable()
    {
        userTable.ajax.reload();
    };

    AdminAS.logOffUser = function logOffUser(userName)
    {
        Admin.request({
            url : serviceContext + '/ootbee/admin/active-sessions/users/' + encodeURIComponent(userName),
            method : 'DELETE',
            fnSuccess : function logOffUser_success()
            {
                AdminAS.refreshUserTable();
            }
        });
    };
})();
