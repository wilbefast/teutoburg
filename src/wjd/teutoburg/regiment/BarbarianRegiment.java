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
import wjd.math.Circle;
import wjd.math.V2;
import wjd.teutoburg.simulation.Tile;
import wjd.util.Timer;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class BarbarianRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int REGIMENT_SIZE = 63; // = 1 + 2 + 4 + ... + 16 + 32
  private static final float SPEED_FACTOR = 0.1f;
  
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors
  
  public BarbarianRegiment(V2 position, Faction faction)
  {
    super(position, REGIMENT_SIZE, faction);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected void ai(int t_delta)
  {
	  RomanRegiment nearestRoman = null;
	  float distanceFromRoman = Float.MAX_VALUE, tmp;
	  BarbarianRegiment nearestChargingBarbarian = null;
	  float distanceFromBarbarian = Float.MAX_VALUE;
	  
	  for(Tile t : percepts)
	  {
		  if(t.agent instanceof RomanRegiment)
		  {
			  tmp = t.agent.getCircle().centre.distance2(c.centre);
			  if(tmp < distanceFromRoman)
			  {
				  nearestRoman = (RomanRegiment)t.agent;
				  distanceFromRoman = tmp;
			  }
		  }
		  else if(t.agent instanceof BarbarianRegiment)
		  {
			  tmp = t.agent.getCircle().centre.distance2(c.centre);
			  if((t.agent.state == State.charging || t.agent.state == State.fighting) && tmp < distanceFromBarbarian)
			  {
				  nearestChargingBarbarian = (BarbarianRegiment)t.agent;
				  distanceFromBarbarian = tmp;
			  }
		  }
	  }
	  // stay hidden behind trees while romans are not near enough
	  // if no romans are visible, stay and wait
	  // else if not charging or fighting, charge !!!
	  // else if charging, charge
	  // else if fighting and has an ennemy, fight

	  if(nearestRoman != null && state != State.fighting && c.collides(nearestRoman.getCircle()))
	  {
		  state = State.fighting;
	  }
	  if(state == State.fighting)
	  {
		  if(nearestRoman != null)
			  fight(nearestRoman);
		  else
			  state = State.waiting;
	  }
	  else if(state == State.waiting)
	  {
		  if(nearestRoman != null && distanceFromRoman <= SPEED_FACTOR*t_delta*3)
		  {
			  state = State.charging;
		  }
		  else if(nearestChargingBarbarian != null)
		  {
			  state = State.charging;
		  }
	  }
	  else if(state == State.charging)
	  {
		  if(nearestRoman != null)
		  {
			  // charge : turn in front of roman, then advance
			  faceTowards(nearestRoman.getCircle().centre);
			  float min = Math.min(SPEED_FACTOR*t_delta, (float)Math.sqrt(distanceFromRoman));
			  advance(min);
			  if(min == distanceFromRoman)
				  state = State.fighting;
		  }
		  else if(nearestChargingBarbarian != null)
		  {
			  faceTowards(nearestChargingBarbarian.getCircle().centre);
			  float min = Math.min(SPEED_FACTOR*t_delta, ((float)Math.sqrt(distanceFromBarbarian)-2*c.radius));
			  advance(min);
			  state = State.waiting;
		  }
	  }
  }
  
  
}
