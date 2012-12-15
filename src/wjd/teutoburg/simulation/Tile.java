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

import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IDynamic;
import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.regiment.RegimentAgent;
import wjd.util.BoundedValue;

/**
 *
 * @author wdyce
 * @since Nov 1, 2012
 */
public class Tile implements IVisible, IDynamic
{
  /* CONSTANTS */
  public static final V2 SIZE = new V2(128, 128);
  public static final V2 HSIZE = SIZE.clone().scale(0.5f);
  public static final V2 ISIZE = SIZE.clone().inv();

  /* ATTRIBUTES */
  public final TileGrid grid;
  public final V2 grid_position, pixel_position;
  private final Rect pixel_area;
  
  public BoundedValue forest_amount = new BoundedValue(1.0f);
  public RegimentAgent agent = null;
  
  /* METHODS */
  
  // constructors
  public Tile(int row, int col, TileGrid grid)
  {
    grid_position = new V2(col, row);
    pixel_position = grid_position.clone().scale(SIZE);
    pixel_area = new Rect(pixel_position, SIZE);
    this.grid = grid;
  }
  
  // mutators
  public boolean setRegiment(RegimentAgent agent_)
  {
    if(agent == null || agent_ == null)
    {
      agent = agent_;
      return true;
    }
    else
      return false;
    
  }

  /* OVERRIDES -- IDYNAMIC */
  @Override
  public void render(ICanvas canvas)
  {
    canvas.setColour(Colour.BLACK);
    if(agent != null)
      canvas.line(pixel_position, agent.getCircle().centre);
  }
  
  /* OVERRIDES -- OBJECT */
  @Override
  public String toString()
  {
    return "Tile @"+grid_position;
  }
  
  /* IMPLEMENTS -- IDYNAMIC */
 
  @Override
  public EUpdateResult update(int t_delta)
  {
    // all clear
    return EUpdateResult.CONTINUE;
  }
  
  /* SUBROUTINES */
}
