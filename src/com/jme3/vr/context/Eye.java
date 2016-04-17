/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.context;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author reden (neph1@github)
 */
public class Eye {
    
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Vector3f distortionK = new Vector3f();
    private final Vector2f distortionCenter = new Vector2f();
    
    public Matrix4f getViewMatrix(){
        return viewMatrix;
    }
    
    public void setViewMatrix(Matrix4f viewMatrix){
        this.viewMatrix.set(viewMatrix);
    }

    public Vector3f getDistortionK() {
        return distortionK;
    }

    public void setDistortionK(Vector3f distortionK) {
        this.distortionK.set(distortionK);
    }

    public Vector2f getDistortionCenter() {
        return distortionCenter;
    }

    public void setDistortionCenter(Vector2f distortionCenter) {
        this.distortionCenter.set(distortionCenter);
    }
    
    
}
