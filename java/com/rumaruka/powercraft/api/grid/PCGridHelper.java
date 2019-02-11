package com.rumaruka.powercraft.api.grid;

import com.rumaruka.powercraft.api.PCDirection;
import com.rumaruka.powercraft.api.PCUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PCGridHelper {

    public static final List<Object> list = new ArrayList<Object>();

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void remove(T tile){
        tile.getGrid().removeTile(tile);
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void connect(T connection1, T connection2){
        connection1.getGrid().makeConnection(connection1, connection2);
    }

    public static <T extends IGridTile<?, T, ?, ?>> T getGridTile(World world, BlockPos pos, PCDirection side, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, pos);
        return getGridFrom(tileEntity, side, tileClass);
    }
    public static <T extends IGridTile<?, T, ?, ?>> T getGridTile(World world, int x, int y, int z, PCDirection side, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, new BlockPos( x, y, z));
        return getGridFrom(tileEntity, side, tileClass);
    }
    public static <T extends IGridTile<?, T, ?, ?>> T getGridTile(World world, int x, int y, int z, PCDirection side, int flags, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world,new BlockPos( x, y, z));
        return getGridFrom(tileEntity, side, flags, tileClass);
    }

    public static <T extends IGridTile<?, T, ?, ?>> T getGridFrom(Object obj, PCDirection side, int flags, Class<T> tileClass){
        if(obj instanceof IGridSided){
            return ((IGridSided)obj).getTile(side, flags, tileClass);
        }else if(obj!=null && tileClass.isAssignableFrom(obj.getClass())){
            return tileClass.cast(obj);
        }
        return null;
    }

    public static <T extends IGridTile<?, T, ?, ?>> T getGridFrom(Object obj, PCDirection side, Class<T> tileClass){
        if(obj instanceof IGridSided){
            return ((IGridSided)obj).getTile(side, -1, tileClass);
        }else if(obj!=null && tileClass.isAssignableFrom(obj.getClass())){
            return tileClass.cast(obj);
        }
        return null;
    }

    public static <T extends IGridTile<?, T, ?, ?>> T getGridTile(World world, int x, int y, int z, PCDirection dir, PCDirection dir2, int flags, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, new BlockPos(x, y, z));
        return getGridFrom(tileEntity, dir, dir2, flags, tileClass);
    }

    private static <T extends IGridTile<?, T, ?, ?>> T getGridFrom(Object obj, PCDirection dir, PCDirection dir2, int flags, Class<T> tileClass){
        if(obj instanceof IGridSidedSide){
            return ((IGridSidedSide)obj).getTile(dir, dir2, flags, tileClass);
        }else if(obj!=null && tileClass.isAssignableFrom(obj.getClass())){
            return tileClass.cast(obj);
        }
        return null;
    }

    public static <T extends IGridTile<?, T, ?, ?>> T getGridTile(World world, int x, int y, int z, PCDirection dir, PCDirection dir2, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, new BlockPos(x, y, z));
        return getGridFrom(tileEntity, dir, dir2, tileClass);
    }

    private static <T extends IGridTile<?, T, ?, ?>> T getGridFrom(Object obj, PCDirection dir, PCDirection dir2, Class<T> tileClass){
        if(obj instanceof IGridSidedSide){
            return ((IGridSidedSide)obj).getTile(dir, dir2, -1, tileClass);
        }else if(obj!=null && tileClass.isAssignableFrom(obj.getClass())){
            return tileClass.cast(obj);
        }
        return null;
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> boolean hasGrid(World world, int x, int y, int z, PCDirection side, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, new BlockPos(x, y, z));
        if(tileEntity instanceof IGridSided){
            return ((IGridSided)tileEntity).getTile(side, -1, tileClass)!=null;
        }else if(tileEntity!=null && tileClass.isAssignableFrom(tileEntity.getClass())){
            return true;
        }
        return false;
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> boolean hasGrid(World world, int x, int y, int z, PCDirection side, int flags, Class<T> tileClass){
        TileEntity tileEntity = PCUtils.getTileEntity(world, new BlockPos(x, y, z));
        if(tileEntity instanceof IGridSided){
            return ((IGridSided)tileEntity).getTile(side, flags, tileClass)!=null;
        }else if(tileEntity!=null && tileClass.isAssignableFrom(tileEntity.getClass())){
            return true;
        }
        return false;
    }

    private static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> G getGridIfNull(T tile){
        if(tile instanceof IGridHolder){
            ((IGridHolder)tile).getGridIfNull();
        }
        return tile.getGrid();
    }
    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void getGridIfNull(World world, int x, int y, int z, int sides, T thisTile, IGridFactory<G, T, N, E> factory, Class<T> tileClass){
        getGridIfNull(world, x, y, z, sides, -1, thisTile, factory, tileClass);
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void getGridIfNull(World world, int x, int y, int z, int sides, int flags, T thisTile, IGridFactory<G, T, N, E> factory, Class<T> tileClass){
        if(world!=null && !world.isRemote && thisTile.getGrid()==null && !list.contains(thisTile)){
            boolean b = list.isEmpty();
            list.add(thisTile);
            int s = sides;
            boolean hasGrid = false;
            for(PCDirection dir:PCDirection.VALID_DIRECTIONS){
                if((s&1)!=0){
                    T tile = getGridTile(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, dir.getOpposite(), flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                s>>>=1;
            }
            if(!hasGrid){
                factory.make(thisTile);
            }
            if(b){
                list.clear();
                thisTile.getGrid().update();
            }
        }
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void getGridIfNull(World world, int x, int y, int z, int sides, PCDirection dir, T thisTile, IGridFactory<G, T, N, E> factory, Class<T> tileClass){
        getGridIfNull(world, x, y, z, sides, -1, dir, thisTile, factory, tileClass);
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void getGridIfNull(World world, int x, int y, int z, int sides, int flags, PCDirection dir, T thisTile, IGridFactory<G, T, N, E> factory, Class<T> tileClass){
        if(world!=null && !world.isRemote && thisTile.getGrid()==null && !list.contains(thisTile)){
            boolean b = list.isEmpty();
            list.add(thisTile);
            int s = sides;
            boolean hasGrid = false;
            for(PCDirection dir2:PCDirection.VALID_DIRECTIONS){
                if(dir2==dir || dir2==dir.getOpposite()){
                    continue;
                }
                if((s&(1<<0))!=0){
                    T tile = getGridTile(world, x, y, z, dir2, dir, flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                if((s&(1<<1))!=0){
                    T tile = getGridTile(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir2, dir.getOpposite(), flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                if((s&(1<<2))!=0){
                    T tile = getGridTile(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir, dir2.getOpposite(), flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                if((s&(1<<3))!=0){
                    T tile = getGridTile(world, x + dir2.offsetX, y + dir2.offsetY, z + dir2.offsetZ, dir2.getOpposite(), dir, flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                if((s&(1<<4))!=0){
                    T tile = getGridTile(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir2.getOpposite(), dir.getOpposite(), flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                if((s&(1<<4))!=0){
                    T tile = getGridTile(world, x + dir2.offsetX + dir.offsetX, y + dir2.offsetY + dir.offsetY, z + dir2.offsetZ + dir.offsetZ, dir.getOpposite(), dir2.getOpposite(), flags, tileClass);
                    if(tile!=null && getGridIfNull(tile)!=null){
                        if(hasGrid){
                            connect(tile, thisTile);
                        }else{
                            tile.getGrid().addTile(thisTile, tile);
                            hasGrid = true;
                        }
                    }
                }
                s>>>=8;
            }
            if(!hasGrid){
                factory.make(thisTile);
            }
            if(b){
                list.clear();
                thisTile.getGrid().update();
            }
        }
    }

    public static <G extends PCGrid<G, T, N, E>, T extends IGridTile<G, T, N, E>, N extends PCNode<G, T, N, E>, E extends PCEdge<G, T, N, E>> void removeFromGrid(World world, T thisTile) {
        if(world!=null && !world.isRemote && thisTile.getGrid()!=null){
            remove(thisTile);
        }
    }

    private PCGridHelper(){
        PCUtils.staticClassConstructor();
    }
}
