<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">

function stackTrace(stacks, lockedMonitors, thisThread) {
    var stackTrace = "";

    for (var n = 0; n < stacks.length; n++) {
        stack = stacks[n];

        if (stack.nativeMethod == true) {
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
                if (lockedMonitors[j].lockedStackDepth == n) {
                    stackTrace += "\t- locked <" + toHex(lockedMonitors[j].identityHashCode, 16) + "> (a " + lockedMonitors[j].className + ")\n";
                }
            }
        }
    }

    return stackTrace;
}

function toHex(thisNumber, chars) {
    var hexNum = "0x" + ("0000000000000000000" + thisNumber.toString(16)).substr(-1 * chars);
    return hexNum;
};