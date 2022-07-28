/*
 * PROJECT valkyrja2
 * util/LocalCommandExecutor.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * 当地命令执行
 *
 * @author Tequila
 * @create 2022/06/28 16:31
 **/
public class LocalCommandExecutor implements CommandExecutor {
	
	private static final Logger log = LoggerFactory.getLogger(LocalCommandExecutor.class);

	/** 默认超时 */
	private static final int DEFAULT_TIMEOUT = 10 * 1000;

	/** 线程池 */
	static ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 3L, TimeUnit.SECONDS, new SynchronousQueue<>());

	/** 异步模式，默认为true */
	private boolean asyncMode = true;

	/**
	 * 执行命令
	 *
	 * @param command 命令
	 * @param useShc linux环境下，使用sh -c执行命令
	 * @return {@link ExecuteResult }
	 * @author Tequila
	 * @date 2022/06/28 16:34
	 */
	public ExecuteResult executeCommand(String command, boolean useShc) {
		return executeCommand(command, useShc, DEFAULT_TIMEOUT);
	}


	/**
	 * 执行命令
	 *
	 * @param command 命令
	 * @param useShc  linux环境下，使用sh -c执行命令
	 * @param timeout 超时
	 * @return {@link ExecuteResult }
	 * @author Tequila
	 * @date 2022/06/28 16:34
	 */
	public ExecuteResult executeCommand(String command, boolean useShc, long timeout) {
		if (useShc) {
			return executeCommand(new String[] { "sh", "-c", command }, timeout);
		} else {
			return executeCommand(new String[] { command }, timeout);
		}
	}

	/**
	 * 执行命令
	 *
	 * @param commands 命令
	 * @return {@link ExecuteResult }
	 * @author Tequila
	 * @date 2022/06/28 16:36
	 */
	public ExecuteResult executeCommand(String...commands) {
		return executeCommand(commands, DEFAULT_TIMEOUT);
	}

	/**
	 * 执行命令
	 *
	 * @param commands 命令
	 * @param timeout  超时
	 * @return {@link ExecuteResult }
	 * @author Tequila
	 * @date 2022/06/28 16:36
	 */
	public ExecuteResult executeCommand(String[] commands, long timeout) {
		commands = arrangeCommands(commands);
		
		Process process = null;
		InputStream pIn = null;
		InputStream pErr = null;
		StreamGobbler outputGobbler = null;
		StreamGobbler errorGobbler = null;
		Future<Integer> executeFuture = null;
		try {
			log.debug("LocalCommandExecutor: " + commands2string(commands));
			process = Runtime.getRuntime().exec(commands);

			if (asyncMode == true) {
				final Process p = process;
				// close process's output stream.
				p.getOutputStream().close();
	
				pIn = process.getInputStream();
				outputGobbler = new StreamGobbler(pIn, "OUTPUT");
				outputGobbler.start();
	
				pErr = process.getErrorStream();
				errorGobbler = new StreamGobbler(pErr, "ERROR");
				errorGobbler.start();
				// create a Callable for the command's Process which can be called by an
				// Executor
				Callable<Integer> call = new Callable<Integer>() {
					public Integer call() throws Exception {
						p.waitFor();
						return p.exitValue();
					}
				};

				// submit the command's call and get the result from a
				executeFuture = pool.submit(call);
				int exitCode = executeFuture.get(timeout, TimeUnit.MILLISECONDS);
				return new ExecuteResult(exitCode, outputGobbler.getContent());
			} else {
				if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
					throw new TimeoutException();
				}
				int exitCode = process.exitValue();
				try (
						InputStream is = process.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
				) {
					StringBuilder sb = new StringBuilder(); 
					String line = null; 
					while ((line = reader.readLine()) != null) {      
						sb.append(line).append("\n");
					}
					return new ExecuteResult(exitCode, sb.toString());
				} catch (IOException e) {
					log.warn("getProcessResult raise error.", e);
					return new ExecuteResult(exitCode, null);
				}   
			}
		} catch (IOException ex) {
			log.error(errorMessage(commands, "execute failed"), ex);
			return new ExecuteResult(-1, null);
		} catch (TimeoutException ex) {
			log.error(errorMessage(commands, "time out"), ex);
			return new ExecuteResult(-1, null);
		} catch (ExecutionException ex) {
			log.error(errorMessage(commands, "did not complete due to an execution error"), ex);
			return new ExecuteResult(-1, null);
		} catch (InterruptedException ex) {
			log.error(errorMessage(commands, "did not complete due to an interrupted error"), ex);
			return new ExecuteResult(-1, null);
		} finally {
			if (executeFuture != null) {
				try {
					executeFuture.cancel(true);
				} catch (Exception ignore) {
					log.warn(ignore.getMessage(), ignore);
				}
			}
			if (pIn != null) {
				this.closeQuietly(pIn);
				if (!outputGobbler.isInterrupted()) {
					outputGobbler.interrupt();
				}
			}
			if (pErr != null) {
				this.closeQuietly(pErr);
				if (!errorGobbler.isInterrupted()) {
					errorGobbler.interrupt();
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
	}

	/**
	 * 安全关闭
	 *
	 * @param c 需要关闭的对象
	 * @author Tequila
	 * @date 2022/06/28 16:37
	 */
	private void closeQuietly(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (IOException e) {
			log.error("exception", e);
		}
	}

	/**
	 * 命令行数组转命令行
	 *
	 * @param commands 命令
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 16:37
	 */
	private String commands2string(String[] commands) {
		StringBuilder sb = new StringBuilder();
		for (String cmd: commands) {
			sb.append(cmd).append(" ");
		}
		
		return sb.toString();
	}

	/**
	 * 错误输出
	 *
	 * @param commands 命令行
	 * @param message  输出的信息
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 16:37
	 */
	private String errorMessage(String[] commands, String message) {
		return "LocalCommandExecutor [" + commands2string(commands) + "] " + message;
	}

	/**
	 * 重排命令，去除空命令
	 *
	 * @param commands 命令
	 * @return {@link String[] }
	 * @author Tequila
	 * @date 2022/06/28 16:38
	 */
	public String[] arrangeCommands(String[] commands) {
		List<String> outputCommand = new ArrayList<>();
		for (String cmd: commands) {
			if (StringUtils.isNotBlank(cmd)) outputCommand.add(cmd);
		}

		return outputCommand.toArray(new String[0]);
	}

	public boolean isAsyncMode() {
		return asyncMode;
	}

	public void setAsyncMode(boolean asyncMode) {
		this.asyncMode = asyncMode;
	}
}

	

