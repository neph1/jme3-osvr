/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.app.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Matrix4f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;
import com.jme3.vr.osvr.context.OsvrContext;
import com.jme3.vr.osvr.post.DistortionFilter;
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
import osvr.clientkit.OSVR_DisplayConfig;
import osvr.java.util.LibraryLoader;
import osvr.util.OSVR_Pose3;
import osvr.util.OSVR_TimeValue;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrAppState extends AbstractAppState{

    ContextWrapper context;
    DisplayC display;
    
    private Application application;
    private OsvrContext osvrContext;
    
    
    
    private Camera camLeft,camRight;
    private ViewPort viewPortLeft, viewPortRight;
    private Node observer; // = new Node("Observer");
    
    static {
        LibraryLoader.loadLibraries();
    }
    
    public OsvrAppState(){
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.application = app;
        if(application instanceof SimpleApplication && observer != null){
            ((SimpleApplication)application).getRootNode().attachChild(observer);
        }
        camLeft = app.getCamera();
        viewPortLeft = app.getViewPort();

        setupAndWaitForContext();
        
        setupAndWaitForDisplay();
        osvrContext = new OsvrContext(context, display);
    }
    
    private void setupAndWaitForContext(){
        context = new ContextWrapper();
        context.initialize(application.getClass().getName());

        while (!context.checkStatus()) {
            Logger.getLogger(OsvrAppState.class.getName()).log(Level.SEVERE, "Context not started.. ");
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(OsvrAppState.class.getName()).log(Level.SEVERE, null, ex);
            }
            context.update();
        }
        
        
    }
    
    private void setupAndWaitForDisplay(){
        OSVR_DisplayConfig displayConfig = new OSVR_DisplayConfig(context);
        while (!displayConfig.valid()) {
            System.out.println("display not valid ");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(OsvrAppState.class.getName()).log(Level.SEVERE, null, ex);
            }
            displayConfig = new OSVR_DisplayConfig(context);

        }

        while (!displayConfig.checkStartup()) {
            System.out.println("display not started ");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(OsvrAppState.class.getName()).log(Level.SEVERE, null, ex);
            }
            context.update();
        }
        display = new DisplayC();
        display.setDisplayConfig(displayConfig);
        
        setupDisplay();
        
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(context != null){
            context.update();
            osvrContext.update();
            
            
            TempVars tempVars = TempVars.get();
            // left eye
            osvrContext.getEye(0).getViewMatrix().toTranslationVector(tempVars.vect1);
            osvrContext.getEye(0).getViewMatrix().toRotationQuat(tempVars.quat1);
            OsvrUtil.invertY(tempVars.quat1);
            tempVars.quat1.multLocal(OsvrUtil.ROTATE_HALF_PI);
            if(observer != null){
                tempVars.vect1.addLocal(observer.getLocalTranslation());
                tempVars.quat1.multLocal(observer.getLocalRotation());
            }
            camLeft.setFrame(tempVars.vect1, tempVars.quat1);

            // right eye
            osvrContext.getEye(1).getViewMatrix().toTranslationVector(tempVars.vect1);
            osvrContext.getEye(1).getViewMatrix().toRotationQuat(tempVars.quat1);
            OsvrUtil.invertY(tempVars.quat1);
            tempVars.quat1.multLocal(OsvrUtil.ROTATE_HALF_PI);
            if(observer != null){
                tempVars.vect1.addLocal(observer.getLocalTranslation());
                tempVars.quat1.multLocal(observer.getLocalRotation());
            }
            camRight.setFrame(tempVars.vect1, tempVars.quat1);
            tempVars.release();
        }
    }
    
    
    
    private void setupDisplay(){
        camRight = camLeft.clone();
        camLeft.setViewPort(0, 0.5f, 0, 1f);
        camRight.setViewPort(0.5f, 1f, 0, 1f);
        float[] projectionMatrix = new float[16];
        
        viewPortRight = application.getRenderManager().createMainView("Right viewport", camRight);
        viewPortRight.setClearFlags(true, true, true);
        viewPortRight.setBackgroundColor(viewPortLeft.getBackgroundColor());
        viewPortRight.attachScene(((SimpleApplication)this.application).getRootNode());
        
        display.osvrClientGetViewerEyeSurfaceProjectionMatrixf(0, 0, 0, application.getCamera().getFrustumNear(), application.getCamera().getFrustumFar(), 0, projectionMatrix);
        camLeft.setProjectionMatrix(new Matrix4f(projectionMatrix));
        
        display.osvrClientGetViewerEyeSurfaceProjectionMatrixf(0, 1, 0, application.getCamera().getFrustumNear(), application.getCamera().getFrustumFar(), 0, projectionMatrix);
        camRight.setProjectionMatrix(new Matrix4f(projectionMatrix));
        display.releaseFloatArray(projectionMatrix);
        FilterPostProcessor leftProcessor = new FilterPostProcessor(application.getAssetManager());
        leftProcessor.addFilter(new DistortionFilter(0));
        application.getRenderManager().getMainView("Default").addProcessor(leftProcessor);
        
        FilterPostProcessor rightProcessor = new FilterPostProcessor(application.getAssetManager());
        rightProcessor.addFilter(new DistortionFilter(1));
        viewPortRight.addProcessor(rightProcessor);
    }

    @Override
    public void cleanup() {
        super.cleanup(); //To change body of generated methods, choose Tools | Templates.
        context.dispose();
        display.dispose();
    }

    public Node getObserver() {
        return observer;
    }

    public void setObserver(Node observer) {
        this.observer = observer;
    }
    
    
    
}
