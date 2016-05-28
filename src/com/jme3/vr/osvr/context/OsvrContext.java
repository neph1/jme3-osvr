/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.context;

import com.jme3.math.Vector2f;
import com.jme3.util.TempVars;
import com.jme3.vr.context.Eye;
import com.jme3.vr.context.IVrContext;
import com.jme3.vr.osvr.app.state.OsvrAppState;
import com.jme3.vr.osvr.util.OsvrUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import osvr.clientkit.ContextWrapper;
import osvr.clientkit.DisplayC;
import osvr.clientkit.Interface;
import osvr.clientkit.InterfaceState;
import osvr.clientkit.OSVRConstants;
import osvr.util.OSVR_Pose3;
import osvr.util.OSVR_RadialDistortionParameters;
import osvr.util.OSVR_TimeValue;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrContext implements IVrContext{

    private  Eye leftEye;
    private  Eye rightEye;
    
//    private final Map<String, Interface> interfaces = new HashMap<String, Interface>();
//    private final Map<String, OSVR_Pose3> trackedPoses = new HashMap<String, OSVR_Pose3>();
    
    ContextWrapper context;
    private  DisplayC display;
    private OSVR_RadialDistortionParameters distortion;
//    InterfaceState interfaceState;
//    private OSVR_Pose3 tempPose = new OSVR_Pose3();
//    private OSVR_TimeValue timeValue = new OSVR_TimeValue();
//    
    public OsvrContext(ContextWrapper context, DisplayC display){
        this.context = context;
        this.display = display;
        leftEye = new Eye(0);
        rightEye = new Eye(1);
        
        initialize();
    }

    public OsvrContext() {
    }

    @Override
    public void initialize() {
        TempVars tempVars = TempVars.get();
        distortion = new OSVR_RadialDistortionParameters();
        display.osvrClientGetViewerEyeSurfaceRadialDistortion(0, 0, 0, distortion);
        OsvrUtil.toVector2f(tempVars.vect2d, distortion.getCenterOfProjection().getData());
        leftEye.setDistortionCenter(tempVars.vect2d);
        OsvrUtil.toVector3f(tempVars.vect1, distortion.getK1().getData());
        leftEye.setDistortionK(tempVars.vect1);
        display.osvrClientGetViewerEyeSurfaceRadialDistortion(0, 1, 0, distortion);
        OsvrUtil.toVector2f(tempVars.vect2d, distortion.getCenterOfProjection().getData());
        rightEye.setDistortionCenter(tempVars.vect2d);
        OsvrUtil.toVector3f(tempVars.vect1, distortion.getK1().getData());
        rightEye.setDistortionK(tempVars.vect1);
        display.releaseDoubleArray(distortion.getCenterOfProjection().getData());
        display.releaseDoubleArray(distortion.getK1().getData());
        tempVars.release();
//        interfaceState = new InterfaceState();
    }
    
    @Override
    public void update() {
        context.update();
        TempVars tempVars = TempVars.get();
        
        display.osvrClientGetViewerEyeViewMatrixf(0, 0, 0, tempVars.matrixWrite);
        OsvrUtil.toMatrix4f(tempVars.tempMat4, tempVars.matrixWrite);
        leftEye.setViewMatrix(tempVars.tempMat4);
        display.releaseFloatArray(tempVars.matrixWrite);
        
        display.osvrClientGetViewerEyeViewMatrixf(0, 1, 0, tempVars.matrixWrite);
        OsvrUtil.toMatrix4f(tempVars.tempMat4, tempVars.matrixWrite);
        rightEye.setViewMatrix(tempVars.tempMat4);
        display.releaseFloatArray(tempVars.matrixWrite);
        tempVars.release();
        
//        Iterator<String> it = interfaces.keySet().iterator();
            
//        String key;
//        while(it.hasNext()){
//            key = it.next();
//            int result = interfaceState.osvrGetPoseState(interfaces.get(key).getNativeHandle(), timeValue, tempPose);
//            if(result == OSVRConstants.OSVR_RETURN_SUCCESS){
//                trackedPoses.get(key).getRotation().set(tempPose.getRotation());
//                trackedPoses.get(key).getTranslation().set(tempPose.getTranslation());
//            } else {
//                Logger.getLogger(OsvrAppState.class.getSimpleName()).log(Level.FINE, "No pose data for " + key);
//            }
//            tempPose.dispose();
//            display.releaseDoubleArray(tempPose.getRotation().getData());
//            display.releaseDoubleArray(tempPose.getTranslation().getData());
//        }
    }

    @Override
    public Eye getEye(int eye) {
        if(eye == 0){
            return leftEye;
        } else if (eye == 1){
            return rightEye;
        }
        return null;
    }

//    public void addTrackingInterface(String name){
//        Interface iface = new Interface();
//        
//        context.getInterface(name, iface);
//        interfaces.put(name, iface);
//        trackedPoses.put(name, new OSVR_Pose3());
//        Logger.getLogger(OsvrAppState.class.getSimpleName()).log(Level.FINE, "Added interface for " + name);
//    }
//    
//    public OSVR_Pose3 getPose(String name){
//        return trackedPoses.get(name);
//    }

    
}
