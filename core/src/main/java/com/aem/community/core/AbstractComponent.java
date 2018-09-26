package com.aem.community.core;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.log.LogService;

public abstract class AbstractComponent
{
	private AtomicReference<LogService> logServiceReference = new AtomicReference<LogService>();

	protected void bindLogService(LogService logService)
	{
		logServiceReference.set(logService);
	}

	protected void unbindLogService(LogService logService)
	{
		logServiceReference.compareAndSet(logService, null);
	}

	protected void handleIllegalConfiguration(String message)
	{
		if (message == null)
			return;

		log(LogService.LOG_ERROR, message);
		throw new IllegalStateException(message);
	}

	protected void handleConfigurationException(String message, Exception e)
	{
	  log(LogService.LOG_ERROR, message, e);
		throw new IllegalStateException(e);
	}
	
	protected void log(int level, String message)
	{
    LogService logService = logServiceReference.get();

    if (logService != null)
      logService.log(level, message);	  
	}
	
	protected void log(int level, String message, Exception e)
	{
    LogService logService = logServiceReference.get();

    if (logService != null)
      logService.log(level, message, e);
	  
	}
}