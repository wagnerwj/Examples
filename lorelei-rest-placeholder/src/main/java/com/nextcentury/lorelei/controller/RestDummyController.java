package com.nextcentury.lorelei.controller;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.nextcentury.lorelei.utils.ActionNames;
import com.nextcentury.lorelei.utils.ActionNamesConverter;
import com.nextcentury.lorelei.utils.ServiceNames;
import com.nextcentury.lorelei.utils.ServiceNamesConverter;

@RestController
public class RestDummyController {
	
	
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {  
	    dataBinder.registerCustomEditor(ServiceNames.class, new ServiceNamesConverter());
	    dataBinder.registerCustomEditor(ActionNames.class, new ActionNamesConverter());
	}
	
	
	@RequestMapping(value= "/lorelei-rest/{service}/{action}", method = RequestMethod.GET)
	public String genericDummyEndpoint(@PathVariable ServiceNames service, @PathVariable ActionNames action){
		try {
		String response = "You ran service "+service.getShortName()+ " to do action "+action.getShortName();
		
			
			
					long delayTime = (long) (Math.ceil(Math.random()*20000)+2000);
PauseThread newThread = new PauseThread(delayTime);
newThread.run();

newThread.join();
response = response +" for "+delayTime;
				
		
		return response;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT);
		}finally{
			throw new HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT);
		}
	}
	
	
	private class PauseThread extends Thread{
		private long delay;
		
		public PauseThread(long delayTime){
			delay = delayTime;
		}
		
		public void run(){
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
