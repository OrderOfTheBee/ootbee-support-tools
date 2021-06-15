/**
 *
 */
package de.fme.jsconsole;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * class for performance monitoring. create your instance via constructor, start
 * the monitoring via {@link #start()} or {@link #start(String, Object...)} and
 * stop the monitoring with {@link #stop(String, Object...)} or
 * {@link #stop(int, String, Object...)}.
 *
 * @author jgoldhammer
 */
public class PerfLog {

	/** The Constant DEFAULT_ASSERT_PERFORMANCE. */
	private static final int DEFAULT_ASSERT_PERFORMANCE = 2000;
	
	/** The logger. */
	private Log logger = LogFactory.getLog(PerfLog.class);
	
	/** The start time. */
	private long startTime;

	/**
	 * Instantiates a new perf log.
	 *
	 * @param logger            the logger to log the performance mesaure
	 */
	public PerfLog(Log logger) {
		if (logger != null) {
			this.logger = logger;
		}
	}

	/**
	 * simple timelogger.
	 */
	public PerfLog() {
	}

	/**
	 * start the logging.
	 *
	 * @param message the message
	 * @param params the params
	 * @return the perf log
	 */
	public PerfLog start(String message, Object... params) {
		startTime = System.currentTimeMillis();
		if (logger.isInfoEnabled() && message != null && !message.trim().isEmpty()) {
			logger.info(MessageFormat.format(message, params));
		}
		return this;
	}

	/**
	 * start the logging.
	 *
	 * @return the perf log
	 */
	public PerfLog start() {
		return start(null, (Object) null);
	}

	/**
	 * Stop.
	 *
	 * @param assertPerformanceOf the assert performance of
	 * @param message the message
	 * @param params the params
	 * @return the measured time
	 */
	public long stop(int assertPerformanceOf, String message, Object... params) {
		long endTime = System.currentTimeMillis();
		long neededTime = endTime - startTime;
		boolean inTime = neededTime < assertPerformanceOf;
        if (logger.isInfoEnabled() && inTime) {
			logger.info("(OK) " + neededTime + " ms:" + MessageFormat.format(message, params));
		} else if (logger.isWarnEnabled() && !inTime){
			logger.warn("(WARNING) " + neededTime + " ms: " + MessageFormat.format(message, params));
		}
		return neededTime;

	}

	/**
	 * ends the performance monitoring. Logs a warning if the operation is
	 * finished after {@value #DEFAULT_ASSERT_PERFORMANCE} milliseconds. To
	 * specify your own time limit, use {@link #stop(int, String, Object...)}.
	 *
	 * @param message            the message to log in the log statement.
	 * @param params the params
	 * @return the long
	 */
	public long stop(String message, Object... params) {
		return stop(DEFAULT_ASSERT_PERFORMANCE, message, params);
	}
}
