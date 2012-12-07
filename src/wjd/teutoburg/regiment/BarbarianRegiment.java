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

import wjd.teutoburg.regiment.RegimentAgent;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 5, 2012
 */
public class BarbarianRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int STRENGTH_START = 7*7;
  
  /* NESTING */
  public static class Barbarian extends Soldier
  {
    public Barbarian(V2 _position, V2 _direction)
    {
      super(_position, _direction, Faction.BARBARIAN);
    }
  }
  
  /* METHODS */
  
  // constructors
  public BarbarianRegiment(V2 start_position)
  {
    super(start_position, STRENGTH_START, Faction.BARBARIAN);
  }
  
  @Override
  public Soldier createSoldier(V2 position, V2 direction)
  {
    return new Barbarian(position, direction);
  }
}
