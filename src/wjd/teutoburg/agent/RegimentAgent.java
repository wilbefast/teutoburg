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

import wjd.amb.control.EUpdateResult;
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
  
  /* LOCAL VARIABLES */
  private V2 r_offset = new V2(), f_offset = new V2(), soldier_position = new V2();
  
  /* ATTRIBUTES */
  // model
  private int strength_current;
  private final int strength_max;
  // view
  private int n_ranks;
  private float ranks_middle;
  private int n_files;
  private float files_middle;
  private int incomplete_rank;
  private Soldier[] soldiers;
  private boolean visible_previous = false;

  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int strength)
  {
    super(start_position, (float) (Math.sqrt(strength) * SOLDIER_SPACING * 0.5f));

    strength_max = strength_current = strength;
    soldiers = new Soldier[strength];

    // calculate number of ranks and files, plus size of incomplete final rank
    n_files = (int) Math.sqrt(strength);
    files_middle = (n_files - 1) * SOLDIER_SPACING * 0.5f;
    n_ranks = strength / n_files;
    ranks_middle = (n_ranks - 1) * SOLDIER_SPACING * 0.5f;
    incomplete_rank = strength - (n_files * n_ranks);
    
    // generate and position the soldiers to begin with
    repositionSoldiers(true);
  }

  // accessors
  

  // mutators
  
  /* INTERFACE */
  public abstract Soldier createSoldier(V2 position, V2 offset);
  
  
  /* OVERRIDES -- AGENT */
  @Override
  public EUpdateResult update(int t_delta)
  {
    // default
    EUpdateResult result = super.update(t_delta);
    if (result != EUpdateResult.CONTINUE)
      return result;

    // extension
    if(visible)
      repositionSoldiers(!visible_previous); // regenerate if moved into view
    visible_previous = visible;

    // all clear!
    return EUpdateResult.CONTINUE;
  }

  @Override
  public void render(ICanvas canvas)
  {
    super.render(canvas);
    if(visible) for (int i = 0; i < /* FIXME */ n_ranks * n_files; i++)
      soldiers[i].render(canvas);
  }

  /* SUBROUTINES */
  private void repositionSoldiers(boolean regenerate)
  {
    for (int r = 0; r < (n_ranks + 1); r++)
    {
      // row offset
      r_offset.reset(direction).scale(r * SOLDIER_SPACING - ranks_middle);
      for (int f = 0; f < ((r < n_ranks) ? n_files : incomplete_rank); f++)
      {
        // file offset
        f_offset.reset(left).scale(f * SOLDIER_SPACING - files_middle);
        
        // calculate absolute position and move there
        soldier_position.reset(position).add(r_offset).add(f_offset);
        if(regenerate)
          soldiers[r * n_files + f] = createSoldier(soldier_position, direction);
        else
          soldiers[r * n_files + f].reposition(soldier_position, direction);
      }
    }
  }
}
