// normally, logger should be considered immutable - this is one of the rare exceptions
/* global jsconsole: false, dumpService: false, logger: true */
logger = jsconsole.getLogger();

/* exported print */
function print(obj)
{
	jsconsole.print(obj);
}

/* exported dump */
function dump(obj)
{
    dumpService.addDump(obj);
}