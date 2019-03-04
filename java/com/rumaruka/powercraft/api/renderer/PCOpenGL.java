package com.rumaruka.powercraft.api.renderer;

import com.rumaruka.powercraft.api.PCLogger;
import com.rumaruka.powercraft.api.PCUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PCOpenGL {


    private static Stack<DoubleBuffer> matrixStack = new Stack<DoubleBuffer>();

    private PCOpenGL(){
        PCUtils.staticClassConstructor();
    }

    public static void pushMatrix(){
        DoubleBuffer db = BufferUtils.createDoubleBuffer(16);
        int mode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        int mat;
        switch(mode){
            case GL11.GL_MODELVIEW:
                mat = GL11.GL_MODELVIEW_MATRIX;
                break;
            case GL11.GL_PROJECTION:
                mat = GL11.GL_PROJECTION_MATRIX;
                break;
            case GL11.GL_TEXTURE:
                mat = GL11.GL_TEXTURE_MATRIX;
                break;
            default:
                throw new RuntimeException("Unknown or not supported matrix mode");
        }
        GL11.glGetDouble(mat, db);
        matrixStack.push(db);
    }

    public static void popMatrix(){
        GL11.glLoadIdentity();
        GL11.glMultMatrix(matrixStack.pop());
    }

    private static List<String> allreadyThrown = new ArrayList<String>();

    public static void checkError(String where) {
        int error = GL11.glGetError();
        if(error!=GL11.GL_NO_ERROR){
            if(allreadyThrown.contains(where)){
                System.out.println("OpenGL Error "+ GLU.gluErrorString(error)+" again in "+ where);
            }else{
                allreadyThrown.add(where);
                PCLogger.severe("OpenGL Error %s in %s", GLU.gluErrorString(error), where);
            }
        }
    }
}
