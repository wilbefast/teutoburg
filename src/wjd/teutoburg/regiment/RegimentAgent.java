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
import wjd.math.V2;
import wjd.teutoburg.physics.Agent;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public class RegimentAgent extends Agent
{
  /* CONSTANTS */
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.25f;
  
  /* ATTRIBUTES */
  // model
  private int strength;
  private Faction faction;
  // organisation
  private Formation formation;
  // view
  private boolean visible_previous = true;
  private boolean nearby = true, nearby_previous = true;
  private final V2 arrow_left = new V2(), 
                    arrow_right = new V2(), 
                    arrow_top = new V2();

  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int start_strength, Faction faction)
  {
    // default
    super(start_position, 0);
    
    // save parameters
    this.strength = start_strength;
    this.faction = faction;
    
    // calculate unit positions based on the strength of the unit
    formation = faction.createFormation();
    setRadius(formation.rebuild(this));
    
    // calculate arrow graphic vertices for the first time, just in case
    recalculateArrow();
  }

  // accessors
  
  int getStrength()
  {
    return strength;
  }

  V2 getPosition()
  {
    return position;
  }

  V2 getDirection()
  {
    return direction;
  }

  V2 getLeft()
  {
    return left;
  }
  
  Faction getFaction()
  {
    return faction;
  }
  

  // mutators
  public EUpdateResult killSoldiers(int n_killed)
  {
    strength -= n_killed;
    if(strength <= 0)
      return EUpdateResult.DELETE_ME;
    
    setRadius(formation.rebuild(this));
    return EUpdateResult.CONTINUE;
  }  
  
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
      formation.refresh(this);
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
      formation.refresh(this);
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
      formation.deleteSoldiers();
    
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
          formation.refresh(this);
      
        // draw close-up regiments
        for(Soldier s : formation)
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
}
