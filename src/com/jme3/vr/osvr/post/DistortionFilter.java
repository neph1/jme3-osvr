/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.post;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author reden (neph1@github)
 */
public class DistortionFilter extends Filter{

    private int eyeIndex;
    
    public DistortionFilter(int eyeIndex){
        this.eyeIndex = eyeIndex;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "com/jme3/vr/shaders/Distortion.j3md");
        
        Vector2f center;
        if(eyeIndex == 0){
            center = new Vector2f(0.47099999999999997f, 0.5f);
        } else {
            center = new Vector2f(0.52900000000000003f, 0.5f);
        }
        material.setVector2("Center", center);
        material.setFloat("K1_Red", 1f);
        material.setFloat("K1_Green", 1f);
        material.setFloat("K1_Blue", 1f);
    }

    @Override
    protected Material getMaterial() {
        return material;
    }
    
}
