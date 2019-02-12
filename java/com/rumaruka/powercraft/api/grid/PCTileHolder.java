package com.rumaruka.powercraft.api.grid;

import java.util.List;

public abstract class PCTileHolder <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> {


    protected G grid;

    protected PCTileHolder(G grid){
        this.grid=grid;
    }
    protected abstract boolean hasTile(T tile);

    protected abstract N getAsNode(T tile);

    protected abstract  List<T> getTile();

    protected abstract void markVisible(List<N> visibleNode, List<E> visibleEdges);

}
