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
package wjd.teutoburg.agent;

import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class RegimentAgent extends Agent
{
  /* CONSTANTS */
  private static final float RADIUS_PER_SOLDER = 4.0f;
  private static final float SOLDIER_SPACING = 22.0f;
  private static final float SOLDIER_RADIUS = 3.0f;
  
  /* ATTRIBUTES */
  private int strength_current;
  private final int strength_max;
  private int n_ranks;
    private float ranks_middle;
  private int n_files;
    private float files_middle;
  private int incomplete_rank;
  
  /* METHODS */
  
  // constructors
  public RegimentAgent(V2 start_position, int strength)
  {
    super(start_position, (float)(Math.sqrt(strength) * SOLDIER_SPACING * 0.5f));
    
    strength_max = strength_current = strength;
    n_files = (int)Math.sqrt(strength);
      files_middle = (n_files - 1) * SOLDIER_SPACING * 0.5f;
    n_ranks = strength / n_files;
      ranks_middle = (n_ranks - 1) * SOLDIER_SPACING * 0.5f;
    incomplete_rank = strength - (n_files * n_ranks);
  }

  // accessors
  
  public abstract Soldier getSoldier();

  // mutators
  
  
  /* OVERRIDES -- AGENT */

  @Override
  public void render(ICanvas canvas)
  {
    super.render(canvas);
    
    canvas.setColour(Colour.RED);
    
    V2 position = new V2(), left = new V2(), direction = new V2();
    

    for(int r = 0; r < n_ranks; r++) for(int f = 0; f < n_files; f++)
    {
      writePositionTo(position); 
      writeDirection(direction); 
      writeLeftTo(left);
      position.add(left.scale(f*SOLDIER_SPACING - files_middle));
      position.add(direction.scale(r*SOLDIER_SPACING - ranks_middle));
      
      
      writeDirection(direction); 
      getSoldier().render(canvas, position, direction);
    }
  }
}
