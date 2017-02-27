package ru.diowo.activewindowcheck;

import com.sun.jna.Native;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.util.Timer;
import java.util.TimerTask;

public class Main {

	private static String lastWindow = ""; 
	
	private static final int MAX_TITLE_LENGTH = 1024;

	public interface PsApi extends StdCallLibrary {

	    int GetModuleFileNameExA(HANDLE process, HANDLE module, byte[] name, int i);

	}
	
	public static void main(String[] args) throws Exception {
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				PsApi psapi = (PsApi) Native.loadLibrary("psapi", PsApi.class);
				
				char[] buffer = new char[MAX_TITLE_LENGTH * 2];
			    HWND hwnd = User32.INSTANCE.GetForegroundWindow();
			    User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
			    String currWindow = Native.toString(buffer);
			    if (! lastWindow.equals(currWindow)) {
			    	lastWindow = currWindow;
			    	
				    System.out.println("Active window title: " + Native.toString(buffer));
				    RECT rect = new RECT();
				    User32.INSTANCE.GetWindowRect(hwnd, rect);
				    System.out.println("rect = " + rect);
				    
				    IntByReference pid = new IntByReference();
			        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
			        
			        byte[] filename = new byte[1024];

			        HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, pid.getValue());
			        psapi.GetModuleFileNameExA(process, null, filename, 1024);
			        String filenameString= Native.toString(filename);
			        
			        System.out.println("PID "+pid.getValue()+", filename "+filenameString);
			    }
			}
		};
		
		timer.schedule(task, 1000, 500);
		
    }

}
