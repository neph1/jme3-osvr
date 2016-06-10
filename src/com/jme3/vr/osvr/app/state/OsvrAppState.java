/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.app.state;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;
import com.jme3.vr.app.DualCamAppState;
import com.jme3.vr.osvr.context.OsvrContext;
import com.jme3.vr.osvr.util.OsvrUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import osvr.clientkit.ContextWrapper;
import osvr.clientkit.DisplayC;
import osvr.clientkit.OSVR_DisplayConfig;
import osvr.java.util.LibraryLoader;
import org.lwjgl.opengl.Display;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrAppState extends AbstractAppState{

    private ContextWrapper context;
    private DisplayC display;
    
    private Application application;
    private OsvrContext osvrContext;
    private Node rootNode;
    
    
    private Camera camLeft,camRight;
    private Node observer; // = new Node("Observer");
    private DualCamAppState camAppState;
    
    static {
        LibraryLoader.loadLibraries();
    }
    
    public OsvrAppState(Node rootNode){
        this.rootNode = rootNode;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.application = app;
        if(observer != null){
            rootNode.attachChild(observer);
        }
        
        setupAndWaitForContext();
        
        setupAndWaitForDisplay();
        
        osvrContext = new OsvrContext(context, display);

        setupViews();
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
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(context != null){
            
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
    
    private void setupViews(){
        if(osvrContext.getRenderManagerConfig().isDirectModeEnabled()){
            throw new UnsupportedOperationException("OSVR Direct Mode is not yet supported!");
        } else {
            Display.setLocation(osvrContext.getRenderManagerConfig().getXPosition(), osvrContext.getRenderManagerConfig().getYPosition());
            application.getRenderManager().notifyReshape(osvrContext.getDisplayParameters().getResolution(0).getWidth(), osvrContext.getDisplayParameters().getResolution(0).getHeight());
        }
        
        camAppState = application.getStateManager().getState(DualCamAppState.class);
        
        camLeft = camAppState.getCamera(0);
        camRight = camAppState.getCamera(1);
        
        int[] vpLeft = new int[4];
        int[] vpRight = new int[4];
        display.osvrClientGetRelativeViewportForViewerEyeSurface(0, 0, 0, vpLeft);
        display.osvrClientGetRelativeViewportForViewerEyeSurface(0, 1, 0, vpRight);
//        camLeft.resize(vpLeft[2], vpLeft[3], true);
//        camRight.resize(vpRight[2], vpRight[3], true);
        display.releaseIntArray(vpLeft);
        display.releaseIntArray(vpRight);
        
        float[] projectionMatrix = new float[16];
//        display.osvrClientGetViewerEyeSurfaceProjectionMatrixf(0, 0, 0, camLeft.getFrustumNear(), camLeft.getFrustumFar(), 0, projectionMatrix);
//        camLeft.setProjectionMatrix(new Matrix4f(projectionMatrix));
//        display.osvrClientGetViewerEyeSurfaceProjectionMatrixf(0, 1, 0, camRight.getFrustumNear(), camRight.getFrustumFar(), 0, projectionMatrix);
//        camRight.setProjectionMatrix(new Matrix4f(projectionMatrix));
//        display.releaseFloatArray(projectionMatrix);
        
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
