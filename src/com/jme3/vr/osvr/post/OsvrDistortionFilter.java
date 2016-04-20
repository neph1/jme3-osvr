/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.post;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.vr.context.Eye;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrDistortionFilter extends Filter{

    private Eye eye;
    
    public OsvrDistortionFilter(Eye eye){
        this.eye = eye;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "com/jme3/vr/shaders/Distortion.j3md");
        
        if(eye.getDistortionCenter().length() > 0.1f){
            material.setVector2("Center", eye.getDistortionCenter());
            Vector3f k1 = eye.getDistortionK();
            material.setFloat("K1_Red", k1.x);
            material.setFloat("K1_Green", k1.y);
            material.setFloat("K1_Blue", k1.z);
        } else {
            Vector2f center;
            if(eye.getIndex() == 0){
                center = new Vector2f(0.47099999999999997f, 0.5f);
            } else {
                center = new Vector2f(0.52900000000000003f, 0.5f);
            }
            material.setVector2("Center", center);
            material.setFloat("K1_Red", 1);
            material.setFloat("K1_Green", 1);
            material.setFloat("K1_Blue", 1);
        }
    }

    @Override
    protected Material getMaterial() {
        return material;
    }
    
}
