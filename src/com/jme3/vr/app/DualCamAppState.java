/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.app;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 *
 * @author reden (neph1@github)
 */
public class DualCamAppState extends AbstractAppState{
    
    
    private Texture2D leftEyeTex, rightEyeTex, dualEyeTex;
    private Camera camLeft,camRight;
    private ViewPort viewPortLeft, viewPortRight;
    private final static String LEFT_VIEW_NAME = "Left View";
    private final static String RIGHT_VIEW_NAME = "Right View";
    private Node mainScene;
    private Node rootNode;
    
    public DualCamAppState(Node rootNode){
        this.rootNode = rootNode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        setupViews(app);
        
        setupMainCamera(app);
        
        setupMainScene(app);
    }
    
    private void setupMainCamera(Application app){
        float frustumSize = 0.5f;
        Camera cam = app.getCamera();
        cam.setFrustumNear(0.5f);
        cam.setLocation(Vector3f.ZERO);
        cam.setParallelProjection(true);
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustum(-15, 15, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
    }
    
    private void setupMainScene(Application app){
        app.getViewPort().detachScene(rootNode);
        mainScene = new Node("Eye Scene");
        
        Geometry leftQuad = new Geometry("", new Quad(1,1));
        leftQuad.center();
        leftQuad.move(-0.5f, 0f, -1.1f);
        
        Geometry rightQuad = new Geometry("", new Quad(1,1));
        rightQuad.center();
        rightQuad.move(0.5f, 0f, -1.1f);
        mainScene.attachChild(leftQuad);
        mainScene.attachChild(rightQuad);
        
        app.getViewPort().attachScene(mainScene);
        
        Material leftMat = new Material(app.getAssetManager(), "com/jme3/vr/shaders/Distortion.j3md");
        Vector2f center = new Vector2f(0.47099999999999997f, 0.5f);
        leftMat.setVector2("Center", center);
        leftMat.setFloat("K1_Red", 1f);
        leftMat.setFloat("K1_Green", 1);
        leftMat.setFloat("K1_Blue", 1);
        leftMat.setInt("Eye", 0);
        leftMat.setTexture("Texture", leftEyeTex);
//        leftMat.setColor("Color", ColorRGBA.Blue);
        leftQuad.setMaterial(leftMat);
        
        
        Material rightMat = new Material(app.getAssetManager(), "com/jme3/vr/shaders/Distortion.j3md");
        center = new Vector2f(0.52900000000000003f, 0.5f);
        rightMat.setVector2("Center", center);
        rightMat.setFloat("K1_Red", 1f);
        rightMat.setFloat("K1_Green", 1);
        rightMat.setFloat("K1_Blue", 1);
        rightMat.setTexture("Texture", rightEyeTex);
        rightMat.setInt("Eye", 1);
//        rightMat.setColor("Color", ColorRGBA.Cyan);
        rightQuad.setMaterial(rightMat);
        
        mainScene.updateGeometricState();
    }
    
    private void setupViews(Application app){
        camLeft = app.getCamera().clone();//new Camera(application.getCamera().getWidth(), application.getCamera().getHeight()); //application.getCamera();
        camLeft.setFrustumPerspective(45f, (float)camLeft.getWidth() / camLeft.getHeight(), 0.5f, 1000f);
//        camLeft.setViewPort(0, 0.5f, 0, 1f);
        camRight = camLeft.clone();
        
        int[] vpLeft = new int[4];
        int[] vpRight = new int[4];
        
        
        viewPortLeft = setupViewBuffers(app, rootNode, camLeft, LEFT_VIEW_NAME, 0);
        viewPortRight = setupViewBuffers(app, rootNode, camRight, RIGHT_VIEW_NAME, 1);
        leftEyeTex = (Texture2D)viewPortLeft.getOutputFrameBuffer().getColorBuffer().getTexture();
        rightEyeTex = (Texture2D)viewPortRight.getOutputFrameBuffer().getColorBuffer().getTexture(); 
    }
    
    private ViewPort setupViewBuffers(Application app, Node rootNode, Camera cam, String viewName, int eye){
        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(cam.getWidth(), cam.getHeight(), 1);
        //offBufferLeft.setSrgb(true);
        
        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(cam.getWidth(), cam.getHeight(), Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorTexture(offTex);        
        
        ViewPort viewPort = app.getRenderManager().createMainView(viewName, cam);
        viewPort.setClearFlags(true, true, true);
        viewPort.setBackgroundColor(ColorRGBA.Black);
        viewPort.attachScene(rootNode);
        //set viewport to render to offscreen framebuffer
        viewPort.setOutputFrameBuffer(offBuffer);
        
        return viewPort;
    }
    
    public Camera getCamera(int eye){
        if(eye == 0){
            return camLeft;
        } else {
            return camRight;
        }
    }
    
    public ViewPort getViewPort(int eye){
        if(eye == 0){
            return viewPortLeft;
        } else {
            return viewPortRight;
        }
    }
}
