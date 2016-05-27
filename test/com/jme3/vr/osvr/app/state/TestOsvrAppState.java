/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.app.state;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author reden (neph1@github)
 */
public class TestOsvrAppState extends SimpleApplication {
    
    public static void main(String... args){
        TestOsvrAppState test = new TestOsvrAppState();
       test.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        OsvrAppState osvrAppState = new OsvrAppState(rootNode);
        stateManager.attach(osvrAppState);
        
        Geometry g = new Geometry("", new Box(1,1,1));
        g.move(0, 0, 5);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        g.setMaterial(m);
        rootNode.attachChild(g);
    }
}
