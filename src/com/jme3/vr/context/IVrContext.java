/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.vr.context;


/**
 *
 * @author reden (neph1@github)
 */
public interface IVrContext {
    
    void initialize();
    
    void update();
    
    Eye getEye(int eye);
}
