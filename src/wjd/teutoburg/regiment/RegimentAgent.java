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
  private boolean nearby = true;
  private V2 left = new V2();


  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int start_strength, Faction faction)
  {
    // default
    super(start_position, 0);
    left.reset(direction).left();
    
    // save parameters
    this.strength = start_strength;
    this.faction = faction;
    
    // calculate unit positions based on the strength of the unit
    formation = faction.createFormation(this);
    setRadius(formation.reform());
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
    
    setRadius(formation.reform());
    return EUpdateResult.CONTINUE;
  }  
  
  /* OVERRIDES -- AGENT */
  
  @Override
  protected void directionChange()
  {
    // default
    super.directionChange();
    left.reset(direction).left();
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible)
      formation.reposition();
  }
  
  @Override
  protected void positionChange()
  {
    // default
    super.positionChange();
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible)
      formation.reposition();
  }
  

  
  @Override
  public EUpdateResult update(int t_delta)
  {
    // default
    EUpdateResult result = super.update(t_delta);
    if (result != EUpdateResult.CONTINUE)
      return result;

    // set level of detail
    formation.setDetail(visible && nearby);
    
    // all clear!
    return EUpdateResult.CONTINUE;
  }

  @Override
  public void render(ICanvas canvas)
  {
     // skip if not in the camera's view
    if(visible = (canvas.getCamera().canSee(visibility_box)))
    {
      // we'll turn off the details if we're too far away
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
     
      // render the formation depending on the level of detail
      formation.render(canvas);
    }
    else
      nearby = false;
  }
}
