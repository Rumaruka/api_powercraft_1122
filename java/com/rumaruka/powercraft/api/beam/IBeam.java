package com.rumaruka.powercraft.api.beam;

import com.rumaruka.powercraft.api.PCVec3;

public interface IBeam {

     PCVec3 getDirections();
     PCVec3 getPosition();
     void setPosition(PCVec3 pos);
     PCLightValue getLightValue();
     PCVec3 getColor();
     double getLength();
     double getRemainingLenght();
     IBeam getNewBeam(double maxLenght, PCVec3 startPos, PCVec3 newDir, PCLightFilter filter);
     void  noTrace();

}

