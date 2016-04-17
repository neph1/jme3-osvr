/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.osvr.util;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author reden (neph1@github)
 */
public class OsvrUtil {
    
    
    public static final Quaternion INVERT_Y_QUAT = new Quaternion(0f, -1f, 0f, 0f);
    public static final Quaternion ROTATE_HALF_PI = new Quaternion().fromAngles(0, FastMath.PI, 0);
    
    public static Matrix4f toMatrix4f(Matrix4f store, float[] newMatrix){
        if(store == null){
            store = new Matrix4f();
        }
        store.set(newMatrix);
        return store;
    }
    
    public static Vector2f toVector2f(Vector2f store, double[] vec2){
        if(store == null){
            store = new Vector2f();
        }
        store.set((float) vec2[0],(float) vec2[1]);
        return store;
    }
    
    public static Vector3f toVector3f(Vector3f store, double[] vec3){
        if(store == null){
            store = new Vector3f();
        }
        store.set((float) vec3[0],(float)  vec3[1],(float)  vec3[2]);
        return store;
    }
    
    public static Quaternion invertY(Quaternion q){
        return q.set(q.getX(), q.getY(), q.getZ(), q.getW());
    }
}
