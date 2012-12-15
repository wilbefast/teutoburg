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

import wjd.math.V2;
import wjd.teutoburg.regiment.RegimentAgent.State;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class RomanRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int REGIMENT_SIZE = 6*6;
  private static final float SPEED_FACTOR = 0.5f;
  
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors
  
  public RomanRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected void ai(int t_delta, Iterable<Tile> percepts)
  {
	  V2 escape_point = getCircle().centre.clone();
	  escape_point.add(0, -10);
	  RomanRegiment nearestRoman = null;
	  float distanceFromRoman = Float.MAX_VALUE, tmp;
	  BarbarianRegiment nearestBarbarian = null;
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
			  if(tmp < distanceFromBarbarian)
			  {
				  nearestBarbarian = (BarbarianRegiment)t.agent;
				  distanceFromBarbarian = tmp;
			  }
		  }
	  }

	  if(nearestBarbarian != null && state != State.fighting && c.collides(nearestBarbarian.getCircle()))
	  {
		  state = State.fighting;
	  }
	  if(state == State.fighting)
	  {
		  if(nearestBarbarian != null)
			  fight(nearestBarbarian);
		  else
			  state = State.waiting;
	  }
	  else if(state == State.waiting)
	  {
		  if(nearestBarbarian != null)
		  {
			  state = State.charging;
		  }
		  else
		  {
			  faceTowards(escape_point);
			  advance(SPEED_FACTOR*t_delta);
		  }
	  }
	  else if(state == State.charging)
	  {
		  if(nearestBarbarian != null)
		  {
			  faceTowards(nearestBarbarian.getCircle().centre);
			  float min = Math.min(SPEED_FACTOR*t_delta, (float)Math.sqrt(distanceFromBarbarian));
			  advance(min);
			  if(min == distanceFromBarbarian)
				  state = State.fighting;
		  }
		  else
		  {
			  state = State.waiting;
		  }
	  }
  }
}
