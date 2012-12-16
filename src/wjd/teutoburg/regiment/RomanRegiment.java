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
  private static final float SPEED_FACTOR = 0.06f;
  private static final double BLOCK_CHANCE_TURTLE = 0.6;
  private static final double BLOCK_CHANCE_RABBLE = 0.2;
  private static final double ATTACK_CHANCE = 0.5;
  private static final int FLANK_MIN_ANGLE = 55;
  
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
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {
	  V2 escape_point = getCircle().centre.clone().add(0, -15);

	  if(nearestEnemy != null 
       && state != State.FIGHTING 
       && c.collides(nearestEnemy.getCircle()))
	  {
		  state = State.FIGHTING;
	  }
    
    
	  if(state == State.FIGHTING)
	  {
		  if(nearestEnemy != null)
			  if(melee(nearestEnemy) == EUpdateResult.DELETE_ME)
			  {
				  return EUpdateResult.DELETE_ME;
			  }
		  else
			  state = State.WAITING;
	  }
    
    
	  else if(state == State.WAITING)
	  {
		  if(nearestEnemy != null)
		  {
			  state = State.CHARGING;
		  }
		  else
		  {
			  V2 new_direction = escape_point.clone(), tmp;
			  int nbCleanNeig = 0;
			  for(Tile t : percepts)
			  {
				  nbCleanNeig++;
				  if(t == tile)
					  nbCleanNeig--;
				  else if(!(t.forest_amount.isEmpty()))
				  {
					  tmp = new V2(t.pixel_position, c.centre);
					  tmp.normalise();
					  tmp.scale(t.forest_amount.balance());
					  new_direction.add(tmp);
					  nbCleanNeig--;
				  }
			  }
			  faceTowards(new_direction);
			  if(advance(SPEED_FACTOR*t_delta) == EUpdateResult.DELETE_ME)
				  return EUpdateResult.DELETE_ME;
		  }
	  }
    
    
	  else if(state == State.CHARGING)
	  {
		  if(nearestEnemy != null)
		  {
			  faceTowards(nearestEnemy.getCircle().centre);
        
        float nearestEnemyDist = (float)Math.sqrt(nearestEnemyDist2);
          
			  float min = Math.min(SPEED_FACTOR * t_delta, nearestEnemyDist);
			  advance(min);
			  if(min == nearestEnemyDist)
				  state = State.FIGHTING;
		  }
		  else
		  {
			  state = State.WAITING;
		  }
	  }
	  return EUpdateResult.CONTINUE;
  }
  
  @Override
  protected double chanceToBlock(RegimentAgent attacker)
  {
    if(isFormedUp())
    {
      // deform if flank-attack
      if(V2.angleBetween(getDirection(), attacker.getDirection()) > FLANK_MIN_ANGLE)
        setFormedUp(false);
      else
        return BLOCK_CHANCE_TURTLE;
    }
    return BLOCK_CHANCE_RABBLE;
  }
  
  @Override
  protected double chanceToHit(RegimentAgent defender)
  {
    return ATTACK_CHANCE;
  }
  
  @Override
  protected boolean isEnemy(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof BarbarianRegiment);
  }
  
  @Override
  protected boolean isAlly(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof RomanRegiment);
  }
}
