
package org.usfirst.frc.team2415.robot;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Robot extends SampleRobot {
	
	final NIVision.Range HUE_RANGE = new NIVision.Range(213,255);
	final NIVision.Range SAT_RANGE = new NIVision.Range(217,255);
	final NIVision.Range VIS_RANGE = new NIVision.Range(102,204);
	
	USBCamera cam;
	NIVision.Image img, bin;
	NIVision.RawData colorTable;
	NIVision.StructuringElement struct;
	
	WiredCatGamepad gamepad;
	
    public void robotInit() {
    	img = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 5);
    	bin = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 5);
    	colorTable = new NIVision.RawData();
    	struct = new NIVision.StructuringElement(3,3,0);
    	/*structuring element takes in parameters to the width and height in pixels,
    	 * and an integer value of 0 or 1; 0 means you use a square structuring element
    	 * and 1 means you use a hexagonal structuring element
    	 */
    	
    	cam = new USBCamera("cam1");
    	
    	cam.openCamera();
    	cam.startCapture();
    	cam.setSize(640, 480);
    	cam.setExposureManual(25);
    	cam.setExposureHoldCurrent();
    	cam.setWhiteBalanceManual(USBCamera.WhiteBalance.kFixedFlourescent2);
    	cam.setWhiteBalanceHoldCurrent();
    	
    	gamepad = new WiredCatGamepad(0);
    }
    
    public void disabled(){
    	while(isDisabled()){
    		cam.getImage(img);
    		CameraServer.getInstance().setImage(img);
    	}
    }
    
    public void autonomous(){
    	long start = System.currentTimeMillis();
    	int secs = 10;
    	int lastSec = -1;
    	boolean captured = false;
    	System.out.println("Taking picture in:");
    	while(isAutonomous() && isEnabled()){
    		cam.getImage(img);
    		if((System.currentTimeMillis() - start)/1000.0 >= secs && !captured){
    			NIVision.imaqWriteJPEGFile(img, "/home/lvuser/capture.jpg", 2000, colorTable);
    			captured = true;
    		}
    		
    		if((System.currentTimeMillis() - start)/1000 !=  lastSec && !captured){
    			System.out.println((secs - (System.currentTimeMillis() - start)/1000) + "!");
    			lastSec = (int)(System.currentTimeMillis() - start)/1000;
    		}
    		CameraServer.getInstance().setImage(img);
    	}
    }

    public void operatorControl() {
    	cam.setSize(320, 240);
    	
    	while(isOperatorControl() && isEnabled()){
    		cam.getImage(img);
    		NIVision.imaqColorThreshold(bin, img, 255, NIVision.ColorMode.HSV, HUE_RANGE, SAT_RANGE, VIS_RANGE);
    		NIVision.imaqMorphology(bin, bin, NIVision.MorphologyMethod.DILATE, struct);
    		CameraServer.getInstance().setImage(bin);
    	}
    	cam.setSize(640, 480);
    }

}
