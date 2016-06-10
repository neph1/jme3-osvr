package com.jme3.vr.util;

import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author reden (neph1@github)
 */
public class DistortionMeshFactory {
    
    public enum DistortionType{
        NONE,
        OSVR;
    }
    private DistortionType type;
    
    private float k1;
    
    public DistortionMeshFactory(DistortionType type, float k1){
        this.type = type;
        this.k1 = k1;
    }
    
    public Geometry makeOsvrDistortionMesh(int resX, int resY, Vector2f centerLeft){
        Geometry distortion = new Geometry("", makeBaseMesh(resX, resY, centerLeft));
        
        return distortion;
    }
    
    private Mesh makeBaseMesh(int resX, int resY, Vector2f centerLeft){
        Mesh mesh = new Mesh();
        
        mesh.setBuffer(VertexBuffer.Type.Position, 3, makeVertexArray(resX, resY, centerLeft));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, makeIndexArray(resX, resY));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, makeTexCoordArray(resX, resY, centerLeft));
        mesh.updateBound();
        mesh.setStatic();
        return mesh;
    }
    
    private float[] makeVertexArray(int resX, int resY, Vector2f center){
        float[] vertices = new float[resX * resY * 3];
        int i = 0;
        float fractionX = 1f/((float)resX-1f);
        float fractionY= 1f/((float)resY-1f);
        float[] res = new float[2];
        for(int y = 0; y < resY; y++){
            for(int x = 0; x < resX; x++){
                
                switch(type){
                    case OSVR:{
                        res = distortOsvr(x * fractionX - center.x, y * fractionY - center.y, k1);
                        break;
                    }
                    default:{
                        res[0] = x * fractionX;
                        res[1] = y * fractionY;
                        break;
                    }
                }
                vertices[i++] = res[0];
                vertices[i++] = res[1];
                vertices[i++] = 0;
//                System.out.println(x + ", " + y + ": " + vertices[i-3] + ", " + vertices[i-2]);
            }
        }
        return vertices;
    }
    
    private int[] makeIndexArray(int resX, int resY){
        int[] indices = new int[resX * resY * 6];
        int i = 0;
        for(int y = 0; y < resY-1; y++){
            for(int x = 0; x < resX-1; x++){
                int x1 = (y) * resX + x;
                int y1 = (y+1) * resX + x;
                int y2 = (y+1) * resX + x + 1;
                indices[i++] = x1;
                indices[i++] = x1 + 1;
                indices[i++] = y1;
                
                indices[i++] = x1 + 1;
                indices[i++] = y2;
                indices[i++] = y1;
            }
        }
        return indices;
    }
    
    private float[] makeTexCoordArray(int resX, int resY, Vector2f center){
        float fractionX = 1f/((float)resX-1f);
        float fractionY= 1f/((float)resY-1f);
        float[] texCoords = new float[resX * resY * 2];
        int i = 0;
        for(int y = 0; y < resY; y++){
            for(int x = 0; x < resX; x++){
                texCoords[i++] = x * fractionX;
                texCoords[i++] = y * fractionY;
            }
        }
        return texCoords;
    }
    
    private float[] distortOsvr(float x, float y, float k1){
        float r2 = x * x + y * y;
        float newRadius = (1.0f + k1*r2);
        x *= newRadius;
        y *= newRadius;

        return new float[]{x,y};
    }
}
