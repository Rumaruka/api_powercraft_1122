package com.rumaruka.powercraft.api.grid;

public interface IGridTile <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> {

    void setGrid(G grid);

    G getGrid();
}
