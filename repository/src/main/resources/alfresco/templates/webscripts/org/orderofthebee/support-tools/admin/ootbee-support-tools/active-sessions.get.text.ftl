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
# HELP alfresco_active_sessions Number of active sessions
# TYPE alfresco_active_sessions gauge
alfresco_active_sessions{type="NumActive"} ${connectionPoolData.numActive?c}
alfresco_active_sessions{type="MaxActive"} ${connectionPoolData.maxActive?c}
alfresco_active_sessions{type="NumIdle"} ${connectionPoolData.numIdle?c}
alfresco_active_sessions{type="UserCountNonExpired"} ${userSessionData.userCountNonExpired?c}
alfresco_active_sessions{type="TicketCountNonExpired"} ${userSessionData.ticketCountNonExpired?c}
</#compress>