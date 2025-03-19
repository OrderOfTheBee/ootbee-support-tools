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

function toHex(thisNumber, chars)
{
    var hexNum = "0x" + ("0000000000000000000" + thisNumber.toString(16)).substr(-1 * chars);
    return hexNum;
}

/* exported stackTrace */
function stackTrace(stacks, lockedMonitors, thisThread)
{
    var stackTrace, n, stack, lockInfo, j;

    stackTrace = "";
    for (n = 0; n < stacks.length; n++)
    {
        stack = stacks[n];

        if (stack.nativeMethod === true)
        {
            stackTrace += "\tat " + stack.className + "." + stack.methodName + "(Native Method)\n";

            if (thisThread.lockInfo)
            {
                lockInfo = thisThread.lockInfo;
                stackTrace += "\t- parking to wait for <" + toHex(lockInfo.identityHashCode, 16) + "> (a " + lockInfo.className + ")\n";
            }
        }
        else
        {
            stackTrace += "\tat " + stack.className + "." + stack.methodName + "(" + stack.fileName + ":" + stack.lineNumber + ")\n";
        }

        if (lockedMonitors)
        {
            for (j = 0; j < lockedMonitors.length; j++)
            {
                if (lockedMonitors[j].lockedStackDepth === n)
                {
                    stackTrace += "\t- locked <" + toHex(lockedMonitors[j].identityHashCode, 16) + "> (a " + lockedMonitors[j].className
                            + ")\n";
                }
            }
        }
    }

    return stackTrace;
}