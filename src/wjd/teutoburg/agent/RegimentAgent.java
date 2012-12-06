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
  private static final float SOLDIER_SPACING = 22.0f;
  // distance at which simplified "imposter" shapes replace regiments
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.25f;
  
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
  private boolean visible_previous = true;
  private boolean nearby = true, nearby_previous = true;
  private final Colour c_imposter;

  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int strength, Colour c_imposter)
  {
    super(start_position, 0);
    
    this.c_imposter = c_imposter;
    
    // calculate unit positions based on the strength of the unit
    strength_max = strength_current = strength;
    recalculateFormation();
  }

  // accessors
  

  // mutators
  public EUpdateResult killSoldiers(int n_killed)
  {
    strength_current -= n_killed;
    if(strength_current <= 0)
      return EUpdateResult.DELETE_ME;
    
    recalculateFormation();
    return EUpdateResult.CONTINUE;
  }
  
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

    // we need to recache the soldiers' positions if in view close to us
    if(visible && nearby)
      // regenerate if the regiment has just moved into view
      repositionSoldiers(!visible_previous || !nearby_previous);
    else
      soldiers = null;
    visible_previous = visible; // Agent doesn't have this attribute

    // all clear!
    return EUpdateResult.CONTINUE;
  }

  @Override
  public void render(ICanvas canvas)
  {
    super.render(canvas);
    if(visible && visible_previous) 
    {
      nearby_previous = nearby;
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
      
      // draw close-up regiments
      if(nearby && nearby_previous) 
        for(Soldier s : soldiers)
          s.render(canvas);
      
      // draw far away regiments
      else
      {
        canvas.setColour(c_imposter);
        canvas.angleBox(position, direction, radius, true);
      }
    }
  }

  /* SUBROUTINES */
  
  private void recalculateFormation()
  {
    double sqrt_strength = Math.sqrt(strength_current);
    
    // calculate number of ranks and files, plus size of incomplete final rank
    n_files = (int)Math.ceil(sqrt_strength);
    files_middle = (n_files - 1) * SOLDIER_SPACING * 0.5f;
    n_ranks = strength_current / n_files;
    ranks_middle = (n_ranks - 1) * SOLDIER_SPACING * 0.5f;
    incomplete_rank = strength_current - (n_files * n_ranks);
    
    // change the overall radius of the unit
    setRadius((float)sqrt_strength * SOLDIER_SPACING * 0.5f);
    
    // we must now reposition the soldiers in their new formation
    repositionSoldiers(true);
  }
  
  private void repositionSoldiers(boolean regenerate)
  {
    // reallocate vector if need be
    if(regenerate)
      soldiers = new Soldier[strength_current];
    
    // reset position by rank and file
    for (int r = 0; r < (n_ranks + 1); r++)
    {
      // row offset
      r_offset.reset(direction).scale(ranks_middle -(r * SOLDIER_SPACING));
      for (int f = 0; f < ((r < n_ranks) ? n_files : incomplete_rank); f++)
      {
        // file offset
        f_offset.reset(left).scale((f * SOLDIER_SPACING) - files_middle);
        
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
