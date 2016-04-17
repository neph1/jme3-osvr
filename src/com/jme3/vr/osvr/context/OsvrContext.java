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
import com.jme3.vr.osvr.util.OsvrUtil;
import osvr.clientkit.DisplayC;
import osvr.util.OSVR_RadialDistortionParameters;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrContext implements IVrContext{

    private Eye leftEye;
    private Eye rightEye;
    
    private final DisplayC display;
    
    public OsvrContext(DisplayC display){
        this.display = display;
        leftEye = new Eye();
        rightEye = new Eye();
        
        initialize();
    }
    
    @Override
    public void initialize() {
        TempVars tempVars = TempVars.get();
        OSVR_RadialDistortionParameters distortion = new OSVR_RadialDistortionParameters();
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
    }
    
    @Override
    public void update() {
        TempVars tempVars = TempVars.get();
        
        display.osvrClientGetViewerEyeViewMatrixf(0, 0, 0, tempVars.matrixWrite);
        OsvrUtil.toMatrix4f(tempVars.tempMat4, tempVars.matrixWrite);
        leftEye.setViewMatrix(tempVars.tempMat4);
        display.osvrClientGetViewerEyeViewMatrixf(0, 1, 0, tempVars.matrixWrite);
        OsvrUtil.toMatrix4f(tempVars.tempMat4, tempVars.matrixWrite);
        rightEye.setViewMatrix(tempVars.tempMat4);
        display.releaseFloatArray(tempVars.matrixWrite);
        tempVars.release();
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

    

    
}
