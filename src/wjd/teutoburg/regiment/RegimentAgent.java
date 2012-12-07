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
package wjd.teutoburg.regiment;

import wjd.amb.control.EUpdateResult;
import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.math.M;
import wjd.math.V2;
import wjd.teutoburg.physics.Agent;

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
  private final V2 arrow_left = new V2(), 
                    arrow_right = new V2(), 
                    arrow_top = new V2();
  private Faction faction;

  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int strength, Faction faction)
  {
    super(start_position, 0);
    
    this.faction = faction;
    
    // calculate unit positions based on the strength of the unit
    strength_max = strength_current = strength;
    
    // build structures for the first time
    recalculateFormation();
    recalculateArrow();
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
  protected void directionChange()
  {
    // default
    super.directionChange();
    
    // recalculate position of the arrow
    recalculateArrow();
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible && nearby)
      repositionSoldiers();
  }
  
  @Override
  protected void positionChange()
  {
    // default
    super.positionChange();
    
    // recalculate position of the arrow
    recalculateArrow();
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible && nearby)
      repositionSoldiers();
  }
  

  
  @Override
  public EUpdateResult update(int t_delta)
  {
    // default
    EUpdateResult result = super.update(t_delta);
    if (result != EUpdateResult.CONTINUE)
      return result;

    // NB - superclass Agent doesn't have attribute 'visible'
    if(!visible || !nearby)
      soldiers = null;
    
    // all clear!
    return EUpdateResult.CONTINUE;
  }

  @Override
  public void render(ICanvas canvas)
  {
     // skip if not in the camera's view
    visible_previous = visible; 
    super.render(canvas);
    if(visible) 
    {
      // skip if not nearby
      nearby_previous = nearby;
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
      if(nearby)
      {
        // regenerate if we've just come into previous or distance
        if(!visible_previous || !nearby_previous)
          repositionSoldiers();
      
        // draw close-up regiments
        for(Soldier s : soldiers)
          s.render(canvas);
      }
      // draw far away regiments
      else
      {
        canvas.setColour(faction.colour_shield);
        canvas.angleBox(position, direction, radius, true);
        canvas.setColour(Colour.WHITE);
        canvas.triangle(arrow_left, arrow_top, arrow_right, true);
      }
    }
  }

  /* SUBROUTINES */
  
  private void recalculateArrow()
  {
    // the arrow shows us which way the regiment is facing from afar
    arrow_top.reset(direction).scale(radius*0.5f).add(position);
    arrow_left.reset(left).scale(radius*0.5f).add(position)
      .add(-direction.x*radius*0.5f, -direction.y*radius*0.5f);
    arrow_right.reset(left).scale(radius*0.5f).opp().add(position)
      .add(-direction.x*radius*0.5f, -direction.y*0.5f*radius);
  }
  
  private void recalculateFormation()
  {
    double sqrt_strength = Math.sqrt(strength_current);
    
    // calculate number of ranks and files, plus size of incomplete final rank
    n_files = M.isqrt(strength_current);
    files_middle = (n_files - 1) * SOLDIER_SPACING * 0.5f;
    n_ranks = strength_current / n_files;
    ranks_middle = (n_ranks - 1) * SOLDIER_SPACING * 0.5f;
    incomplete_rank = strength_current - (n_files * n_ranks);
    
    // change the overall radius of the unit
    setRadius((float)sqrt_strength * SOLDIER_SPACING * 0.5f);
    
    // we must now reposition the soldiers in their new formation
    soldiers = null;
    repositionSoldiers();
  }
  
  private void repositionSoldiers()
  {
    // reallocate vector if need be
    boolean regenerate = (soldiers == null);
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
