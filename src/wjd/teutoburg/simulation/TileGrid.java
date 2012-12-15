/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.simulation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.forest.Copse;

/**
 *
 * @author wdyce
 * @since Nov 9, 2012
 */
public class TileGrid implements Iterable<Tile>
{
  /* ATTRIBUTES */

  public final Tile[][] tiles;
  private final Rect grid_area;
  private final Rect pixel_area;
  
  /* METHODS */
  
  // constructors
  private TileGrid(Tile[][] tiles, Rect grid_area)
  {
    this.tiles = tiles;
    this.grid_area = grid_area;
    this.pixel_area 
      = new Rect(grid_area.pos(), grid_area.size().add(1,1)).mult(Tile.SIZE);
  }
  
  public TileGrid(V2 size)
  {
    this(new Tile[(int)size.y][(int)size.x], 
         new Rect(V2.ORIGIN, size.clone().dinc()).floor());
  }

  // mutators
  /**
   * Set all the tiles in the grid to be free.
   */
  public TileGrid clear()
  {
    // set all tiles as free
    for (int row = (int) grid_area.y; row <= (int)(grid_area.endy()); row++)
      for (int col = (int) grid_area.x; col <= (int) (grid_area.endx()); col++)
        tiles[row][col] = new Tile(row, col, this);
    return this;
  }

  // accessors
  
  public Rect getPixelArea()
  {
    return pixel_area;
  }
  
  /**
   * Grab the Tile at the specified "pixel" position (x, y).
   *
   * @param pixel_pos the vector pixel-position (x, y) of the desired Tile,
   * not to the confused with the (col, row) grid-position.
   * @return the Tile at the specified position or null if there position is
   * invalid (outside of the grid).
   */
  public Tile pixelToTile(V2 pixel_pos)
  {
    
    
    
    V2 grid_pos = pixel_pos.clone().floor().scale(Tile.ISIZE);
    
    
    if(!validGridPos(grid_pos))
      System.out.println("bink");
    
    return (validGridPos(grid_pos) 
            ? tiles[(int)grid_pos.y][(int)grid_pos.x] 
            : null);
  }
  
  /**
   * Grab the Tile at the specified "grid" position (col, row).
   *
   * @param grid_pos the vector grid-position (col, row) of the desired Tile,
   * not to the confused with the (x, y) pixel-position.
   * @return the Tile at the specified position or null if there position is
   * invalid (outside of the grid).
   */
  public Tile gridToTile(V2 grid_pos)
  {
    return (validGridPos(grid_pos) 
            ? tiles[(int)grid_pos.y][(int)grid_pos.x] 
            : null);
  }

  /**
   * Which cells of the grid are inside the rectangle?
   *
   * @param sub_area the rectangle which we want to draw cells from.
   * @return a Tile.Field structure containing a pair of coordinates
   * corresponding to the top-left- and bottom-right-most cells in the grid.
   */
  public TileGrid createSubGrid(Rect sub_area)
  {
    // build the sub-field
    int min_col = (int)(sub_area.x * Tile.ISIZE.x),
        min_row = (int)(sub_area.y * Tile.ISIZE.y),
        max_col = (int)(sub_area.endx() * Tile.ISIZE.x),
        max_row = (int)(sub_area.endy() * Tile.ISIZE.y);
    Rect sub_grid_area 
      = new Rect(min_col, min_row, max_col-min_col, max_row-min_row);
    
    // constraints
    sub_grid_area = sub_grid_area.getIntersection(grid_area);
    return (sub_grid_area == null) ? null : new TileGrid(tiles, sub_grid_area);
  }

  public List<Tile> getNeighbours(Tile tile, boolean diagonals)
  {
    // local variables
    V2 pos = new V2();
    Tile neighbour;
    LinkedList<Tile> neighbour_list = new LinkedList<Tile>();
    
    // add applicable neighbours
    for(int row = -1; row < 2; row++)
    for(int col = -1; col < 2; col++)
    if(diagonals || Math.abs(row + col) == 1) // only the 4 direct neighbours
    {
      neighbour = gridToTile(pos.reset(tile.grid_position).add(col, row));
      if(neighbour != null)
        neighbour_list.add(neighbour);
    }
    
    // return the result
    return neighbour_list;
  }

  /**
   * Check if a position is on the grid.
   *
   * @param grid_pos the vector pair of coordinates (col, row) to check.
   * @return true is the given pair of coordinates is inside the grid, false if
   * not.
   */
  public boolean validGridPos(V2 grid_pos)
  {
    return (grid_pos.x >= 0 && grid_pos.y >= 0
            && grid_pos.y < tiles.length && grid_pos.x < tiles[0].length);
  }

  /* OVERRIDES -- OBJECT */
  @Override
  public String toString()
  {
    return "Tilegrid(" + grid_area + ')';
  }

  void registerCopse(Copse copse)
  {
    Rect copse_area = new Rect();
      copse.getCircle().getBoundingBox(copse_area);
    V2 copse_centre = copse.getCircle().centre;
    
    
    TileGrid tree_tiles = createSubGrid(copse_area);
    for(Tile t : tree_tiles)
    {
      V2 tile_centre = t.pixel_position.clone().add(Tile.HSIZE);
      t.forest_amount.tryDeposit(copse.getCircle().getCloseness(tile_centre));
    }
  }

  /* IMPLEMENTS -- ITERABLE */
  public static class RowByRow implements Iterator<Tile>
  {
    // attributes

    private final TileGrid tilegrid;
    private V2 current_pos;

    // methods
    public RowByRow(TileGrid tilegrid)
    {
      this.tilegrid = tilegrid;
      this.current_pos = tilegrid.grid_area.pos();
    }
    
    @Override
    public boolean hasNext()
    {
      return (current_pos != null);
    }

    @Override
    public Tile next()
    {
      Tile previous = tilegrid.gridToTile(current_pos);
      
      // overlap collumns
      current_pos.x++;
      if(current_pos.x > tilegrid.grid_area.endx())
      {
        current_pos.x = tilegrid.grid_area.x;
        current_pos.y++;
      }
      
      // overlap row
      if(current_pos.y > tilegrid.grid_area.endy())
        current_pos = null;

      return previous;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("remove not supported.");
    }
  }

  @Override
  public Iterator<Tile> iterator()
  {
    return new RowByRow(this);
  }
}
