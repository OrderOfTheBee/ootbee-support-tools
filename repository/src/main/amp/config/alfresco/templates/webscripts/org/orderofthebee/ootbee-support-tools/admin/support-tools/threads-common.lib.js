/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
 */

function toHex(thisNumber, chars) {
    var hexNum = "0x" + ("0000000000000000000" + thisNumber.toString(16)).substr(-1 * chars);
    return hexNum;
}

function stackTrace(stacks, lockedMonitors, thisThread) {
    var stackTrace = "";

    for (var n = 0; n < stacks.length; n++) {
        var stack = stacks[n];

        if (stack.nativeMethod === true) {
            stackTrace = "\tat " + stack.className + "." + stack.methodName + "(Native Method)\n";

            if (thisThread.lockInfo) {
                var lockInfo = thisThread.lockInfo;
                stackTrace += "\t- parking to wait for <" + toHex(lockInfo.identityHashCode, 16) + "> (a " + lockInfo.className + ")\n";
            }
        } else {
            stackTrace += "\tat " + stack.className + "." + stack.methodName + "(" + stack.fileName + ":" + stack.lineNumber + ")\n";
        }

        if (lockedMonitors) {
            for (var j = 0; j < lockedMonitors.length; j++) {
                if (lockedMonitors[j].lockedStackDepth === n) {
                    stackTrace += "\t- locked <" + toHex(lockedMonitors[j].identityHashCode, 16) + "> (a " + lockedMonitors[j].className + ")\n";
                }
            }
        }
    }

    return stackTrace;
}