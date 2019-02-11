package com.rumaruka.powercraft.api.multiblock;

import com.rumaruka.powercraft.api.PCDirection;

public enum PCMultiblockIndex {

    CENTER(PCMultiblockType.CENTER), FACENORTH(PCMultiblockType.FACE), FACEEAST(PCMultiblockType.FACE), FACESOUTH(PCMultiblockType.FACE), FACEWEST(
            PCMultiblockType.FACE), FACETOP(PCMultiblockType.FACE), FACEBOTTOM(PCMultiblockType.FACE), CORNERTOPNORTHEAST(PCMultiblockType.CORNER), CORNERTOPSOUTHEAST(
            PCMultiblockType.CORNER), CORNERTOPSOUTHWEST(PCMultiblockType.CORNER), CORNERTOPNORTHWEST(PCMultiblockType.CORNER), CORNERBOTTOMNORTHEAST(
            PCMultiblockType.CORNER), CORNERBOTTOMSOUTHEAST(PCMultiblockType.CORNER), CORNERBOTTOMSOUTHWEST(PCMultiblockType.CORNER), CORNERBOTTOMNORTHWEST(
            PCMultiblockType.CORNER), EDGETOPNORTH(PCMultiblockType.CORNER), EDGETOPEAST(PCMultiblockType.CORNER), EDGETOPSOUTH(
            PCMultiblockType.CORNER), EDGETOPWEST(PCMultiblockType.CORNER), EDGEBOTTOMNORTH(PCMultiblockType.CORNER), EDGEBOTTOMEAST(
            PCMultiblockType.CORNER), EDGEBOTTOMSOUTH(PCMultiblockType.CORNER), EDGEBOTTOMWEST(PCMultiblockType.CORNER), EDGENORTHEAST(
            PCMultiblockType.CORNER), EDGESOUTHEAST(PCMultiblockType.CORNER), EDGESOUTHWEST(PCMultiblockType.CORNER), EDGENORTHWEST(
            PCMultiblockType.CORNER);

    private static final PCDirection FACEDIRS[] = { PCDirection.NORTH, PCDirection.EAST, PCDirection.SOUTH, PCDirection.WEST, PCDirection.UP,
            PCDirection.DOWN };
    public static final PCMultiblockIndex FACEINDEXFORDIR[] = { PCMultiblockIndex.FACEBOTTOM, PCMultiblockIndex.FACETOP,
            PCMultiblockIndex.FACENORTH, PCMultiblockIndex.FACESOUTH, PCMultiblockIndex.FACEWEST, PCMultiblockIndex.FACEEAST };

    public final PCMultiblockType type;


    PCMultiblockIndex(PCMultiblockType type) {

        this.type = type;
    }


    public static PCDirection getFaceDir(PCMultiblockIndex index) {

        if (index.type == PCMultiblockType.FACE) {
            return FACEDIRS[index.ordinal() - 1];
        }
        return PCDirection.UNKNOWN;
    }

    public static PCMultiblockIndex getFromDir(PCDirection dir) {
        switch(dir){
            case DOWN:
                return FACEBOTTOM;
            case EAST:
                return FACEEAST;
            case NORTH:
                return FACENORTH;
            case SOUTH:
                return FACESOUTH;
            case UP:
                return FACETOP;
            case WEST:
                return FACEWEST;
            case UNKNOWN:
            default:
                return null;
        }
    }
}
