package smit.com.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TouchScreen extends Object
{
	public TouchScreen()
	{
	}
	
	public String runCmd(String cmd, boolean respond)
	{
		StringBuffer result = new StringBuffer();

		try
		{
			Process process = Runtime.getRuntime().exec("/system/bin/sh");
			DataOutputStream stdIn = new DataOutputStream(process.getOutputStream());
			DataInputStream stdOut = new DataInputStream(process.getInputStream());
			DataInputStream stdErr = new DataInputStream(process.getErrorStream());
	        
			if(cmd.endsWith("\n"))
				stdIn.writeBytes(cmd);
			else
				stdIn.writeBytes(cmd + "\n");
			stdIn.flush();
			
	    	try
	    	{
	    		if(respond)
	    		{
	    			while((stdOut.available()==0) && (stdErr.available()==0) );
	    		
	    			if(stdOut.available()>0)
	    			{
	    				while(stdOut.available() > 0)
	    				{
	    					result.append("" + (char)stdOut.read());
	    				}						
	    			}
				
	    			if(stdErr.available() > 0)
	    			{
	    				while(stdErr.available() > 0)
	    				{
	    					stdErr.read();
	    				}						
	    			}
	    			
	    		}
	    		
	    		return result.toString();
	    	}
	    	catch(IOException e)
	    	{
				e.printStackTrace();
				return("ERROR:" + e.getLocalizedMessage());
	    	}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return("ERROR:" + e.getLocalizedMessage());
		}
	}
	
	public void writeCalToHardware(int[] cal) throws IOException
	{
		/*
		int fd0, fd1, fd2, fd3, fd4, fd5, fd6;

		fd0 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/0", posix.O_WRONLY, 0666);
		fd1 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/1", posix.O_WRONLY, 0666);
		fd2 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/2", posix.O_WRONLY, 0666);
		fd3 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/3", posix.O_WRONLY, 0666);
		fd4 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/4", posix.O_WRONLY, 0666);
		fd5 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/5", posix.O_WRONLY, 0666);
		fd6 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/6", posix.O_WRONLY, 0666);

		if( (fd0 < 0) || (fd1 < 0) || (fd2 < 0) || (fd3 < 0) || (fd4 < 0) || (fd5 < 0) || (fd6 < 0) )
		{
			posix.close(fd0);
			posix.close(fd1);
			posix.close(fd2);
			posix.close(fd3);
			posix.close(fd4);
			posix.close(fd5);
			posix.close(fd6);
			return;
		}
		
		
		posix.write(fd0, String.valueOf(cal[0]).getBytes());
		posix.write(fd1, String.valueOf(cal[1]).getBytes());
		posix.write(fd2, String.valueOf(cal[2]).getBytes());
		posix.write(fd3, String.valueOf(cal[3]).getBytes());
		posix.write(fd4, String.valueOf(cal[4]).getBytes());
		posix.write(fd5, String.valueOf(cal[5]).getBytes());
		posix.write(fd6, String.valueOf(cal[6]).getBytes());
		
		posix.close(fd0);
		posix.close(fd1);
		posix.close(fd2);
		posix.close(fd3);
		posix.close(fd4);
		posix.close(fd5);
		posix.close(fd6);
		*/
		RandomAccessFile  fd0=null, fd1=null, fd2=null, fd3=null, fd4=null, fd5=null, fd6=null;
		String tmp;

		
				
		try{
		fd0 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/0"), "w") ;
		fd1 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/1"), "w") ; 
		fd2 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/2"), "w") ;
		fd3 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/3"), "w") ; 
		fd4 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/4"), "w") ;
		fd5 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/5"), "w") ; 
		fd6 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/6"), "w") ;
		}
		catch (Exception e) {
			// TODO: handle exception
			if(fd0!=null)
			{
				fd0.close();
			}
			if(fd1!=null)
			{
				fd1.close();
			}
			if(fd1!=null)
			{
				fd1.close();
			}
			if(fd2!=null)
			{
				fd2.close();
			}
			
			if(fd3!=null)
			{
				fd3.close();
			}
			if(fd4!=null)
			{
				fd4.close();
			}
			if(fd5!=null)
			{
				fd5.close();
			}
			if(fd6!=null)
			{
				fd6.close();
			}
			
		
			return ;
		}
		fd0.write(String.valueOf(cal[0]).getBytes());
		fd1.write( String.valueOf(cal[1]).getBytes());
		fd2.write( String.valueOf(cal[2]).getBytes());
		fd3.write( String.valueOf(cal[3]).getBytes());
		fd4.write( String.valueOf(cal[4]).getBytes());
		fd5.write( String.valueOf(cal[5]).getBytes());
		fd6.write( String.valueOf(cal[6]).getBytes());
		
		fd0.close();
		fd0.close();
		fd0.close();
		fd0.close();
		fd0.close();
		fd0.close();
		fd0.close();
		
	}
	
	public int[] readCalFromHardware() throws IOException
	{
		int cal[] = new int[7];
		//int fd0, fd1, fd2, fd3, fd4, fd5, fd6;
		RandomAccessFile  fd0=null, fd1=null, fd2=null, fd3=null, fd4=null, fd5=null, fd6=null;
		String tmp;

		
				
		try{
		fd0 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/0"), "r") ;
		fd1 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/1"), "r") ; 
		fd2 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/2"), "r") ;
		fd3 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/3"), "r") ; 
		fd4 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/4"), "r") ;
		fd5 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/5"), "r") ; 
		fd6 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/calibration0/6"), "r") ;
				
		//fd0 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/0", posix.O_RDONLY, 0666);
		//fd1 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/1", posix.O_RDONLY, 0666);
		//fd2 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/2", posix.O_RDONLY, 0666);
		//fd3 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/3", posix.O_RDONLY, 0666);
		//fd4 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/4", posix.O_RDONLY, 0666);
		//fd5 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/5", posix.O_RDONLY, 0666);
		//fd6 = posix.open("/sys/devices/platform/tcc-ak4183-ts/calibration0/6", posix.O_RDONLY, 0666);
		}
		catch (Exception e) {
			// TODO: handle exception
			if(fd0!=null)
			{
				fd0.close();
			}
			if(fd1!=null)
			{
				fd1.close();
			}
			if(fd1!=null)
			{
				fd1.close();
			}
			if(fd2!=null)
			{
				fd2.close();
			}
			
			if(fd3!=null)
			{
				fd3.close();
			}
			if(fd4!=null)
			{
				fd4.close();
			}
			if(fd5!=null)
			{
				fd5.close();
			}
			if(fd6!=null)
			{
				fd6.close();
			}
			
			cal[0] = 26593;
			cal[1] = -54;
			cal[2] = -1315176;
			cal[3] = 81;
			cal[4] = 17532;
			cal[5] = -3077376;
			cal[6] = 65536;
			
			return cal;
		}
		/*
		if( (fd0 < 0) || (fd1 < 0) || (fd2 < 0) || (fd3 < 0) || (fd4 < 0) || (fd5 < 0) || (fd6 < 0) )
		{
			posix.close(fd0);
			posix.close(fd1);
			posix.close(fd2);
			posix.close(fd3);
			posix.close(fd4);
			posix.close(fd5);
			posix.close(fd6);

			cal[0] = 26593;
			cal[1] = -54;
			cal[2] = -1315176;
			cal[3] = 81;
			cal[4] = 17532;
			cal[5] = -3077376;
			cal[6] = 65536;
			
			return cal;
		}
		
		tmp = posix.read(fd0, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[0] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd1, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[1] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd2, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[2] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd3, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[3] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd4, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[4] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd5, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[5] = Integer.valueOf(tmp).intValue();
		
		tmp = posix.read(fd6, 16);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
			
		*/
		byte[] b = new byte[18] ;  
		  for (int i = 0; i < 16 ;i++) 
		  {  
		      
					b[i] = fd0.readByte();
			       
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[0] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16 ;i++) 
		  {  
		        b[i] = fd1.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[1] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16; i++) 
		  {  
		        b[i] = fd2.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[2] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16; i++) 
		  {  
		        b[i] = fd3.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[3] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16; i++) 
		  {  
		        b[i] = fd4.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[4] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16 ;i++) 
		  {  
		        b[i] = fd5.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[5] = Integer.valueOf(tmp).intValue();
		
		 for (int i = 0; i < 16 ;i++) 
		  {  
		        b[i] = fd6.readByte();        
	
		  }  
		tmp = new String(b);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[6] = Integer.valueOf(tmp).intValue();
		
		fd0.close();
		fd1.close();
		fd2.close();
		fd3.close();
		fd4.close();
		fd5.close();
		fd6.close();

		return cal;
		
		/*
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/0", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[0] = Integer.valueOf(tmp).intValue();
		
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/1", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[1] = Integer.valueOf(tmp).intValue();
	
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/2", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[2] = Integer.valueOf(tmp).intValue();
		
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/3", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[3] = Integer.valueOf(tmp).intValue();
		
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/4", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[4] = Integer.valueOf(tmp).intValue();
		
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/5", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[5] = Integer.valueOf(tmp).intValue();
		
		tmp = runCmd("cat /sys/devices/platform/tcc-ak4183-ts/calibration0/6", true);
		if(tmp.endsWith("\n"))
			tmp = tmp.substring(0, tmp.length()-1);
		cal[6] = Integer.valueOf(tmp).intValue();
		
		return cal;
		*/
	}
	
	public void setZoomToHardware(int zoom) throws IOException
	{
		//int fd = posix.open("/sys/devices/platform/tcc-ak4183-ts/zoom0/0", posix.O_WRONLY, 0666);
		RandomAccessFile fd0 =null;
		try {
			fd0 = new RandomAccessFile(new File("/sys/devices/platform/tcc-ak4183-ts/zoom0/0"), "w") ;
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		fd0.write(String.valueOf(zoom).getBytes());
		fd0.close();

		/*
		runCmd(String.format("echo %d > /sys/devices/platform/tcc-ak4183-ts/zoom0/0", zoom), false);
		*/		
	}
}
