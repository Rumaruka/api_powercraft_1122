package com.rumaruka.powercraft.api.grid;

public interface IGridFactory<G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> {

    G make(T tile);
}
